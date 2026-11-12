/**
 * seed/seed_routines.json 생성기.  실행: npm run seed:build
 *  1) id를 결정적으로 만든다: rt_ + ULID(시각 2026-11-12T00:00:00+09:00, 랜덤부 '0000000000SEED' + 번호 2자리)
 *     → 다시 만들어도 같은 id. 서버·앱·데모가 같은 루틴을 같은 id로 가리킨다.
 *  2) category를 6장 규칙(D7)으로 계산한다.
 *  3) 계약 v0.1(zod)과 업로드 텍스트 규칙으로 검증한다. 하나라도 실패하면 파일을 쓰지 않는다.
 */
import { writeFileSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import { dirname, join } from 'node:path';
import { encodeTime } from 'ulid';
import { routineSpecSchema, SCHEMA_VERSION } from '../src/domain/contract.js';
import { computeCategory } from '../src/domain/category.js';
import { SEED_INPUT } from './seed-source.js';

export const SEED_VERSION = '2026-11-12.1';
const SEED_TIME = Date.parse('2026-11-12T00:00:00+09:00');

export function seedId(no: number): string {
  return 'rt_' + encodeTime(SEED_TIME, 10) + '0000000000SEED' + String(no).padStart(2, '0');
}

export function buildSeed() {
  const errors: string[] = [];
  const routines = SEED_INPUT.map((s) => {
    const draft: any = {
      schemaVersion: SCHEMA_VERSION,
      id: seedId(s.no),
      name: s.name,
      description: s.description,
      category: 'etc',
      tags: s.tags,
      trigger: s.trigger,
      conditions: s.conditions,
      actions: s.actions,
    };
    draft.category = computeCategory(draft);
    const r = routineSpecSchema.safeParse(draft);
    if (!r.success) {
      errors.push(`#${s.no} ${s.name}: ` + r.error.issues.map((i) => `${i.path.join('.')} ${i.message}`).join('; '));
    }
    return { no: s.no, featuredRank: s.featuredRank, isOfficial: true, spec: draft };
  });
  const ranks = routines.map((r) => r.featuredRank).filter((x): x is number => x !== null);
  if (new Set(ranks).size !== ranks.length) errors.push('featuredRank 중복');
  const names = routines.map((r) => r.spec.name);
  if (new Set(names).size !== names.length) errors.push('이름 중복');
  return {
    errors,
    file: {
      seedVersion: SEED_VERSION,
      schemaVersion: SCHEMA_VERSION,
      note: '백엔드 문서 7장 시드 20개. 서버 시드·차량 앱 assets·HTML 데모가 같은 파일을 쓴다. 인기 수치는 넣지 않으며 초기 노출 순서는 featuredRank(작을수록 앞, null은 순서 없음)로 정한다.',
      routines,
    },
  };
}

if (process.argv[1] === fileURLToPath(import.meta.url)) {
  const { errors, file } = buildSeed();
  if (errors.length) {
    console.error('시드 검증 실패:\n' + errors.join('\n'));
    process.exit(1);
  }
  const out = join(dirname(fileURLToPath(import.meta.url)), 'seed_routines.json');
  writeFileSync(out, JSON.stringify(file, null, 2) + '\n');
  console.log(`OK ${file.routines.length}개 → ${out}`);
}
