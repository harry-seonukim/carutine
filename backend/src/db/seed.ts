/**
 * seed/seed_routines.json을 DB에 넣는다. 실행: npm run seed
 * 여러 번 실행해도 안전하다(같은 id는 내용만 갱신, 가져오기·좋아요·신고 수와 생성 시각은 유지).
 */
import { readFileSync } from 'node:fs';
import { join } from 'node:path';
import type { Db } from './db.js';
import { ROOT } from '../paths.js';
import { routineSpecSchema } from '../domain/contract.js';
import { prepareRoutine } from '../domain/routine.js';

export interface SeedFile {
  seedVersion: string;
  routines: { no: number; featuredRank: number | null; isOfficial: boolean; spec: unknown }[];
}

export function readSeedFile(): SeedFile {
  return JSON.parse(readFileSync(join(ROOT, 'seed', 'seed_routines.json'), 'utf8'));
}

export async function seed(db: Db, log: (m: string) => void = () => {}): Promise<number> {
  const file = readSeedFile();
  const rows = file.routines.map((r) => {
    const parsed = routineSpecSchema.safeParse(r.spec);
    if (!parsed.success) throw new Error(`시드 #${r.no} 계약 검증 실패: ${parsed.error.issues[0]?.message}`);
    const row = prepareRoutine(parsed.data, parsed.data.id);
    if (row.category !== parsed.data.category) {
      throw new Error(`시드 #${r.no} category가 규칙과 다릅니다 (${parsed.data.category} → ${row.category}). npm run seed:build 를 다시 실행하세요`);
    }
    return { ...row, is_official: r.isOfficial, featured_rank: r.featuredRank };
  });
  await db.tx(async (q) => {
    for (const r of rows) {
      await q.query(
        `INSERT INTO routines (id, author_device_id, name, description, category, tags, spec, spec_hash, schema_version,
            trigger_type, action_types, trigger_caps, action_caps, action_fallback_caps, is_official, featured_rank, status)
         VALUES ($1, NULL, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, $15, 'active')
         ON CONFLICT (id) DO UPDATE SET
           name = EXCLUDED.name, description = EXCLUDED.description, category = EXCLUDED.category,
           tags = EXCLUDED.tags, spec = EXCLUDED.spec, spec_hash = EXCLUDED.spec_hash,
           schema_version = EXCLUDED.schema_version, trigger_type = EXCLUDED.trigger_type,
           action_types = EXCLUDED.action_types, trigger_caps = EXCLUDED.trigger_caps,
           action_caps = EXCLUDED.action_caps, action_fallback_caps = EXCLUDED.action_fallback_caps,
           is_official = EXCLUDED.is_official, featured_rank = EXCLUDED.featured_rank, updated_at = now()`,
        [r.id, r.name, r.description, r.category, r.tags, JSON.stringify(r.spec), r.spec_hash, r.schema_version,
          r.trigger_type, r.action_types, r.trigger_caps, r.action_caps, r.action_fallback_caps, r.is_official, r.featured_rank],
      );
    }
  });
  log(`seeded ${rows.length} routines (seedVersion ${file.seedVersion})`);
  return rows.length;
}
