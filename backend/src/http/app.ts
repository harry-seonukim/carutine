/** Express 앱 조립. 보안 기본값: helmet, HTTPS 강제(운영), 본문 16KB 제한, 최소 로그, 오류 상세 숨김. */
import express, { type NextFunction, type Request, type Response } from 'express';
import helmet from 'helmet';
import type { Db } from '../db/db.js';
import type { Config } from '../config.js';
import type { BannedWords } from '../domain/textguard.js';
import { readSeedFile } from '../db/seed.js';
import { ApiError } from './errors.js';
import { apiRouter } from './routes.js';

export interface AppDeps { db: Db; cfg: Config; banned: BannedWords; log?: (line: Record<string, unknown>) => void }

export function createApp({ db, cfg, banned, log = (l) => console.log(JSON.stringify(l)) }: AppDeps) {
  const app = express();
  app.disable('x-powered-by');
  app.set('trust proxy', cfg.trustProxy ? 1 : false);
  app.set('json spaces', 0);

  app.use(helmet());

  // HTTPS만 허용(8장). 프록시 뒤에서는 X-Forwarded-Proto를 믿는다(trust proxy). /healthz는 플랫폼 헬스체크용으로 예외.
  if (cfg.requireHttps) {
    app.use((req, res, next) => {
      if (req.secure || req.path === '/healthz') return next();
      res.status(403).json(new ApiError('FORBIDDEN', 'HTTPS로만 접속할 수 있어요.').toJSON());
    });
  }

  // 로그: 토큰·Authorization·요청 본문은 남기지 않는다. 경로 패턴, 루틴 ID, 기기 ID, 상태 코드, 소요 시간만.
  app.use((req, res, next) => {
    const t0 = process.hrtime.bigint();
    res.on('finish', () => {
      const route = req.route?.path ? (req.baseUrl ?? '') + req.route.path : req.path.replace(/rt_[0-9A-Z]{26}/g, ':id');
      log({
        t: new Date().toISOString(), method: req.method, route, status: res.statusCode,
        ms: Number((process.hrtime.bigint() - t0) / 1_000_000n),
        deviceId: req.deviceId ?? res.locals.deviceId, routineId: req.params?.id ?? res.locals.routineId,
      });
    });
    next();
  });

  app.use(express.json({ limit: '16kb', strict: true, type: ['application/json'] }));

  const seedVersion = readSeedFile().seedVersion;
  app.get('/healthz', async (_req, res) => {
    try {
      await db.query('SELECT 1');
      res.json({ status: 'ok', db: 'ok', seedVersion, time: new Date().toISOString() });
    } catch {
      res.status(503).json({ status: 'degraded', db: 'down', seedVersion, time: new Date().toISOString() });
    }
  });

  app.use('/v1', apiRouter(db, cfg, banned));

  app.use((_req, _res, next) => next(new ApiError('NOT_FOUND', '요청한 주소가 없어요.')));

  // 오류 처리: 스택 트레이스·SQL 오류를 응답에 넣지 않는다.
  app.use((err: unknown, req: Request, res: Response, _next: NextFunction) => {
    let e: ApiError;
    if (err instanceof ApiError) e = err;
    else if (isBodyError(err)) {
      e = err.type === 'entity.too.large'
        ? new ApiError('VALIDATION_FAILED', '요청이 너무 커요.', [{ field: 'body', reason: 'TOO_LARGE' }])
        : new ApiError('VALIDATION_FAILED', '요청 형식이 올바르지 않아요.', [{ field: 'body', reason: 'INVALID_JSON' }]);
    } else {
      e = new ApiError('INTERNAL');
      log({ t: new Date().toISOString(), level: 'error', route: req.path, error: err instanceof Error ? err.name + ': ' + err.message : 'unknown' });
    }
    res.status(e.status).json(e.toJSON());
  });

  return app;
}

function isBodyError(err: unknown): err is { type: string } {
  return !!err && typeof err === 'object' && typeof (err as any).type === 'string' && String((err as any).type).startsWith('entity.');
}
