/**
 * DB 접근 계층. SQL은 파라미터 바인딩($1, $2 …)만 쓴다. 문자열로 SQL을 이어 붙이지 않는다.
 * - postgres://…  → node-postgres Pool (운영, docker compose)
 * - pglite://memory, pglite://<dir> → PGlite(WASM Postgres). Docker 없이 로컬 실행·테스트용.
 */
import pg from 'pg';

export interface Queryable {
  query<T = any>(sql: string, params?: unknown[]): Promise<{ rows: T[]; rowCount: number }>;
}
export interface Db extends Queryable {
  tx<T>(fn: (q: Queryable) => Promise<T>): Promise<T>;
  close(): Promise<void>;
  kind: 'pg' | 'pglite';
}

export async function createDb(url: string): Promise<Db> {
  if (url.startsWith('pglite://')) return createPglite(url.slice('pglite://'.length));
  return createPg(url);
}

function createPg(url: string): Db {
  const pool = new pg.Pool({
    connectionString: url,
    max: 5,                                   // 무료 DB 연결 수 제한을 고려해 작게
    idleTimeoutMillis: 30_000,
    ssl: /sslmode=require|neon\.tech|supabase\.co/.test(url) ? { rejectUnauthorized: true } : undefined,
  });
  const q: Queryable['query'] = async (sql, params) => {
    const r = await pool.query(sql, params as any[]);
    return { rows: r.rows, rowCount: r.rowCount ?? 0 };
  };
  return {
    kind: 'pg',
    query: q,
    async tx(fn) {
      const client = await pool.connect();
      try {
        await client.query('BEGIN');
        const out = await fn({
          query: async (sql, params) => {
            const r = await client.query(sql, params as any[]);
            return { rows: r.rows, rowCount: r.rowCount ?? 0 };
          },
        });
        await client.query('COMMIT');
        return out;
      } catch (e) {
        await client.query('ROLLBACK').catch(() => {});
        throw e;
      } finally {
        client.release();
      }
    },
    close: () => pool.end(),
  };
}

async function createPglite(dir: string): Promise<Db> {
  const { PGlite } = await import('@electric-sql/pglite');
  const db = dir === 'memory' || dir === '' ? new PGlite() : new PGlite(dir);
  await db.waitReady;
  // PGlite는 연결이 하나라 트랜잭션을 직렬화한다
  let chain: Promise<unknown> = Promise.resolve();
  const q: Queryable['query'] = async (sql, params) => {
    const r = await db.query(sql, params as any[]);
    return { rows: r.rows as any[], rowCount: r.affectedRows ?? r.rows.length };
  };
  return {
    kind: 'pglite',
    query: q,
    tx(fn) {
      const run = chain.then(() => db.transaction(async (t) => fn({
        query: async (sql, params) => {
          const r = await t.query(sql, params as any[]);
          return { rows: r.rows as any[], rowCount: r.affectedRows ?? r.rows.length };
        },
      })));
      chain = run.catch(() => {});
      return run as any;
    },
    close: () => db.close(),
  };
}
