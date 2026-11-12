/**
 * 익명 기기 인증. 토큰은 32바이트 암호학적 난수(base64url), DB에는 SHA-256 해시만 둔다.
 * 해시로 행을 찾은 뒤 상수 시간 비교로 한 번 더 확인한다.
 */
import { createHash, randomBytes, timingSafeEqual } from 'node:crypto';
import type { NextFunction, Request, Response } from 'express';
import type { Db } from '../db/db.js';
import { ApiError } from './errors.js';

export const hashToken = (token: string) => createHash('sha256').update(token, 'utf8').digest('hex');
export const newToken = () => randomBytes(32).toString('base64url');

const TOKEN_RE = /^[A-Za-z0-9_-]{43}$/;          // 32바이트 base64url

declare module 'express-serve-static-core' {
  interface Request { deviceId?: string }
}

export function requireDevice(db: Db) {
  return async (req: Request, _res: Response, next: NextFunction) => {
    const m = /^Bearer (\S+)$/.exec(req.get('authorization') ?? '');
    if (!m || !TOKEN_RE.test(m[1])) throw new ApiError('UNAUTHORIZED');
    const h = hashToken(m[1]);
    const r = await db.query<{ id: string; token_hash: string; stale: boolean }>(
      `SELECT id, token_hash, (last_seen_at IS NULL OR last_seen_at < now() - interval '1 hour') AS stale
         FROM devices WHERE token_hash = $1`, [h]);
    const row = r.rows[0];
    if (!row || !timingSafeEqual(Buffer.from(row.token_hash, 'hex'), Buffer.from(h, 'hex'))) {
      throw new ApiError('UNAUTHORIZED');
    }
    req.deviceId = row.id;
    if (row.stale) await db.query('UPDATE devices SET last_seen_at = now() WHERE id = $1', [row.id]);
    next();
  };
}
