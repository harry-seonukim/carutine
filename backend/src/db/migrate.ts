/** migrations/*.sql을 이름순으로 한 번씩 실행한다. 실행: npm run migrate */
import { readdirSync, readFileSync } from 'node:fs';
import { join } from 'node:path';
import { createHash } from 'node:crypto';
import type { Db } from './db.js';
import { ROOT } from '../paths.js';

export async function migrate(db: Db, log: (m: string) => void = () => {}): Promise<string[]> {
  await db.query(`CREATE TABLE IF NOT EXISTS schema_migrations (
    name TEXT PRIMARY KEY, checksum TEXT NOT NULL, applied_at TIMESTAMPTZ NOT NULL DEFAULT now())`);
  const dir = join(ROOT, 'migrations');
  const files = readdirSync(dir).filter((f) => /^\d+_.+\.sql$/.test(f)).sort();
  const done = new Map((await db.query<{ name: string; checksum: string }>('SELECT name, checksum FROM schema_migrations')).rows
    .map((r) => [r.name, r.checksum]));
  const applied: string[] = [];
  for (const f of files) {
    const sql = readFileSync(join(dir, f), 'utf8');
    const sum = createHash('sha256').update(sql).digest('hex');
    if (done.has(f)) {
      if (done.get(f) !== sum) throw new Error(`이미 적용된 마이그레이션이 바뀌었습니다: ${f} (새 파일로 추가하세요)`);
      continue;
    }
    await db.tx(async (q) => {
      for (const stmt of splitSql(sql)) await q.query(stmt);
      await q.query('INSERT INTO schema_migrations (name, checksum) VALUES ($1, $2)', [f, sum]);
    });
    log(`migrated ${f}`);
    applied.push(f);
  }
  return applied;
}

/** 주석 제거 후 세미콜론 단위로 나눈다(마이그레이션에 함수·문자열 속 세미콜론은 쓰지 않는다). */
function splitSql(sql: string): string[] {
  return sql.replace(/--.*$/gm, '').split(';').map((s) => s.trim()).filter(Boolean);
}
