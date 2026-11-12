/** API 11개 (백엔드 문서 5장). 입력은 모두 zod로 검증한다. */
import { Router, type Request } from 'express';
import { ulid } from 'ulid';
import { z } from 'zod';
import type { Db } from '../db/db.js';
import type { Config } from '../config.js';
import { CATEGORIES, uploadSpecSchema, type Action } from '../domain/contract.js';
import { parseCaps } from '../domain/caps.js';
import { checkTexts, type BannedWords } from '../domain/textguard.js';
import { ApiError, zodDetails } from './errors.js';
import { hashToken, newToken, requireDevice } from './auth.js';
import { makeLimiters } from './limits.js';
import { getShelves, listRoutines } from '../services/gallery.js';
import * as svc from '../services/routines.js';

const ID_RE = /^rt_[0-9A-HJKMNP-TV-Z]{26}$/;

function parse<T extends z.ZodTypeAny>(schema: T, value: unknown, prefix: string): z.infer<T> {
  const r = schema.safeParse(value);
  if (!r.success) throw new ApiError('VALIDATION_FAILED', undefined, zodDetails(r.error.issues, prefix));
  return r.data;
}

/** 경로의 루틴 id 형식이 틀리면 DB를 보지 않고 404 */
function routineId(req: Request): string {
  const id = String(req.params.id);
  if (!ID_RE.test(id)) throw new ApiError('NOT_FOUND');
  return id;
}

const one = (v: unknown) => (Array.isArray(v) ? v[0] : v);
const capsOf = (req: Request) => parseCaps(one(req.query.caps));

const registerBody = z.object({
  appVersion: z.string().max(20).regex(/^[0-9A-Za-z.+-]+$/, 'INVALID_FORMAT').optional(),
}).strip();

const shelvesQuery = z.object({
  season: z.enum(['spring', 'summer', 'autumn', 'winter']).optional(),
  hour: z.coerce.number().int().min(0).max(23).optional(),
});

const cursorSchema = z.string().max(200).transform((s, ctx) => {
  try {
    const v = JSON.parse(Buffer.from(s, 'base64url').toString('utf8'));
    if (Number.isInteger(v?.o) && v.o >= 0 && v.o <= 100_000) return v.o as number;
  } catch { /* fallthrough */ }
  ctx.addIssue({ code: 'custom', message: 'INVALID_CURSOR' });
  return z.NEVER;
});
const encodeCursor = (offset: number) => Buffer.from(JSON.stringify({ o: offset })).toString('base64url');

const listQuery = z.object({
  category: z.enum(CATEGORIES).optional(),
  q: z.string().trim().max(50).optional().transform((s) => (s ? s : undefined)),
  sort: z.enum(['popular', 'new']).default('popular'),
  compat: z.enum(['hide_none', 'all']).default('hide_none'),
  cursor: cursorSchema.optional(),
  limit: z.coerce.number().int().min(1).max(50).default(20),
});

const uploadBody = z.object({ spec: uploadSpecSchema }).strict();
const reportBody = z.object({
  reason: z.enum(['inappropriate', 'spam', 'unsafe', 'other']),
  detail: z.string().max(200).optional(),
}).strict();

/** 쿼리스트링에서 이 API가 쓰는 키만 1개 값으로 꺼낸다(배열 파라미터 공격 방지) */
const pick = (req: Request, keys: string[]) =>
  Object.fromEntries(keys.filter((k) => req.query[k] !== undefined).map((k) => [k, one(req.query[k])]));

export function apiRouter(db: Db, cfg: Config, banned: BannedWords): Router {
  const r = Router();
  const auth = requireDevice(db);
  const lim = makeLimiters(cfg.limits);

  r.post('/devices', lim.register, async (req, res) => {
    const body = parse(registerBody, req.body ?? {}, '');
    const token = newToken();
    const id = 'dv_' + ulid();
    await db.query('INSERT INTO devices (id, token_hash, app_version, last_seen_at) VALUES ($1, $2, $3, now())',
      [id, hashToken(token), body.appVersion ?? null]);
    res.locals.deviceId = id;
    res.status(201).json({ deviceId: id, token });
  });

  // 이하 모든 경로는 기기 토큰이 필요하다
  r.use(auth);

  r.get('/gallery/shelves', lim.read, async (req, res) => {
    const qy = parse(shelvesQuery, pick(req, ['season', 'hour']), 'query');
    res.json(await getShelves(db, req.deviceId!, capsOf(req), qy.season, qy.hour));
  });

  r.get('/routines', lim.read, async (req, res) => {
    const qy = parse(listQuery, pick(req, ['category', 'q', 'sort', 'compat', 'cursor', 'limit']), 'query');
    const out = await listRoutines(db, req.deviceId!, {
      category: qy.category, q: qy.q, sort: qy.sort, compat: qy.compat, caps: capsOf(req),
      offset: qy.cursor ?? 0, limit: qy.limit,
    });
    res.json({ items: out.items, nextCursor: out.nextOffset === null ? null : encodeCursor(out.nextOffset) });
  });

  r.get('/routines/:id', lim.read, async (req, res) => {
    res.json(await svc.getDetail(db, req.deviceId!, routineId(req), capsOf(req)));
  });

  r.post('/routines', lim.read, lim.upload, async (req, res) => {
    const { spec } = parse(uploadBody, req.body, '');
    const issues = checkTexts({ ...spec, actions: spec.actions as Action[] }, banned);
    if (issues.length) {
      const banned = issues.some((i) => i.reason === 'BANNED_WORD');
      throw new ApiError('VALIDATION_FAILED',
        banned ? '사용할 수 없는 표현이 들어 있어요.' : '위치·연락처·링크로 보이는 내용은 올릴 수 없어요.', issues);
    }
    const id = await svc.createRoutine(db, req.deviceId!, spec as any, cfg.limits.duplicateWindowSec);
    res.locals.routineId = id;
    res.status(201).json(await svc.getDetail(db, req.deviceId!, id, capsOf(req)));
  });

  r.post('/routines/:id/import', lim.read, async (req, res) => {
    res.json(await svc.importRoutine(db, req.deviceId!, routineId(req)));
  });

  r.put('/routines/:id/like', lim.read, async (req, res) => {
    res.json(await svc.setLike(db, req.deviceId!, routineId(req), true));
  });

  r.delete('/routines/:id/like', lim.read, async (req, res) => {
    res.json(await svc.setLike(db, req.deviceId!, routineId(req), false));
  });

  r.post('/routines/:id/report', lim.report, async (req, res) => {
    const id = routineId(req);
    const body = parse(reportBody, req.body ?? {}, '');
    await svc.reportRoutine(db, req.deviceId!, id, body.reason, body.detail ?? null, cfg.limits.hideAfterReports);
    res.status(202).json({ accepted: true });
  });

  r.delete('/routines/:id', lim.read, async (req, res) => {
    await svc.deleteRoutine(db, req.deviceId!, routineId(req));
    res.status(204).end();
  });

  return r;
}
