/** 상세·올리기·가져오기·좋아요·신고·내리기. 집계는 행 삽입과 카운트 갱신을 한 트랜잭션에서 한다(4장). */
import { ulid } from 'ulid';
import type { Db, Queryable } from '../db/db.js';
import type { CapKey, RoutineSpec } from '../domain/contract.js';
import { prepareRoutine } from '../domain/routine.js';
import { ApiError } from '../http/errors.js';
import { CARD_COLUMNS, toCard, type CardRow } from './cards.js';

const notFound = () => new ApiError('NOT_FOUND');

async function activeSpec(q: Queryable, id: string): Promise<RoutineSpec> {
  const r = await q.query<{ spec: RoutineSpec }>(`SELECT spec FROM routines WHERE id = $1 AND status = 'active'`, [id]);
  if (!r.rows[0]) throw notFound();
  return r.rows[0].spec;
}

export async function getDetail(q: Queryable, deviceId: string, id: string, caps: Set<CapKey> | null) {
  const r = await q.query<CardRow & { spec: RoutineSpec }>(
    `SELECT ${CARD_COLUMNS}, r.spec FROM routines r WHERE r.id = $2 AND r.status = 'active'`, [deviceId, id]);
  const row = r.rows[0];
  if (!row) throw notFound();
  return { ...toCard(row, caps), spec: row.spec };
}

export async function createRoutine(
  db: Db, deviceId: string, input: Parameters<typeof prepareRoutine>[0], duplicateWindowSec: number,
) {
  const row = prepareRoutine(input, 'rt_' + ulid());
  return db.tx(async (q) => {
    // 같은 기기가 짧은 시간 안에 같은 spec을 다시 올리면 거부(6장). 같은 기기 요청을 잠가 동시 요청도 막는다.
    await q.query('SELECT id FROM devices WHERE id = $1 FOR UPDATE', [deviceId]);
    const dup = await q.query(
      `SELECT 1 FROM routines WHERE author_device_id = $1 AND spec_hash = $2
          AND created_at > now() - make_interval(secs => $3) LIMIT 1`, [deviceId, row.spec_hash, duplicateWindowSec]);
    if (dup.rows.length) {
      throw new ApiError('VALIDATION_FAILED', '방금 올린 루틴과 같아요. 잠시 후 다시 시도해 주세요.', [{ field: 'spec', reason: 'DUPLICATE' }]);
    }
    await q.query(
      `INSERT INTO routines (id, author_device_id, name, description, category, tags, spec, spec_hash, schema_version,
          trigger_type, action_types, trigger_caps, action_caps, action_fallback_caps, is_official, featured_rank)
       VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, false, NULL)`,
      [row.id, deviceId, row.name, row.description, row.category, row.tags, JSON.stringify(row.spec), row.spec_hash,
        row.schema_version, row.trigger_type, row.action_types, row.trigger_caps, row.action_caps, row.action_fallback_caps]);
    return row.id;
  });
}

export async function importRoutine(db: Db, deviceId: string, id: string) {
  return db.tx(async (q) => {
    const spec = await activeSpec(q, id);
    const ins = await q.query(
      `INSERT INTO routine_imports (device_id, routine_id) VALUES ($1, $2) ON CONFLICT DO NOTHING`, [deviceId, id]);
    const r = ins.rowCount > 0
      ? await q.query<{ import_count: number }>(
        `UPDATE routines SET import_count = import_count + 1 WHERE id = $1 RETURNING import_count`, [id])
      : await q.query<{ import_count: number }>(`SELECT import_count FROM routines WHERE id = $1`, [id]);
    return { spec, importCount: Number(r.rows[0].import_count) };
  });
}

export async function setLike(db: Db, deviceId: string, id: string, liked: boolean) {
  return db.tx(async (q) => {
    await activeSpec(q, id);
    const ch = liked
      ? await q.query(`INSERT INTO routine_likes (device_id, routine_id) VALUES ($1, $2) ON CONFLICT DO NOTHING`, [deviceId, id])
      : await q.query(`DELETE FROM routine_likes WHERE device_id = $1 AND routine_id = $2`, [deviceId, id]);
    const r = ch.rowCount > 0
      ? await q.query<{ like_count: number }>(
        `UPDATE routines SET like_count = GREATEST(like_count + $2, 0) WHERE id = $1 RETURNING like_count`, [id, liked ? 1 : -1])
      : await q.query<{ like_count: number }>(`SELECT like_count FROM routines WHERE id = $1`, [id]);
    return { likeCount: Number(r.rows[0].like_count), likedByMe: liked };
  });
}

/** 서로 다른 기기 hideAfter곳 이상이 신고하면 숨긴다. 공식 루틴은 자동 숨김에서 제외. 같은 기기 중복 신고는 무시. */
export async function reportRoutine(
  db: Db, deviceId: string, id: string, reason: string, detail: string | null, hideAfter: number,
) {
  await db.tx(async (q) => {
    await activeSpec(q, id);
    const ins = await q.query(
      `INSERT INTO routine_reports (routine_id, device_id, reason, detail) VALUES ($1, $2, $3, $4)
       ON CONFLICT (routine_id, device_id) DO NOTHING`, [id, deviceId, reason, detail]);
    if (ins.rowCount === 0) return;
    await q.query(
      `UPDATE routines SET report_count = report_count + 1,
              status = CASE WHEN NOT is_official AND report_count + 1 >= $2 THEN 'hidden' ELSE status END,
              updated_at = now()
        WHERE id = $1`, [id, hideAfter]);
  });
}

export async function deleteRoutine(db: Db, deviceId: string, id: string) {
  await db.tx(async (q) => {
    const r = await q.query<{ author_device_id: string | null }>(
      `SELECT author_device_id FROM routines WHERE id = $1 AND status <> 'deleted' FOR UPDATE`, [id]);
    const row = r.rows[0];
    if (!row) throw notFound();
    if (row.author_device_id !== deviceId) throw new ApiError('FORBIDDEN', '내가 올린 루틴만 내릴 수 있어요.');
    await q.query(`UPDATE routines SET status = 'deleted', updated_at = now() WHERE id = $1`, [id]);
  });
}
