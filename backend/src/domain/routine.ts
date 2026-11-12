/** spec → DB 행 값. 올리기와 시드 적재가 같은 함수를 쓴다(Cap·category를 서버가 계산). */
import { createHash } from 'node:crypto';
import { SCHEMA_VERSION, type Action, type RoutineSpec } from './contract.js';
import { computeCategory } from './category.js';
import { deriveCaps } from './caps.js';

/** 키 순서와 무관한 JSON 문자열 */
export function canonicalJson(v: unknown): string {
  if (Array.isArray(v)) return '[' + v.map(canonicalJson).join(',') + ']';
  if (v && typeof v === 'object') {
    return '{' + Object.keys(v).sort().filter((k) => (v as any)[k] !== undefined)
      .map((k) => JSON.stringify(k) + ':' + canonicalJson((v as any)[k])).join(',') + '}';
  }
  return JSON.stringify(v);
}

/** 중복 판정용 해시: 서버가 덮어쓰는 id·category는 제외 */
export function specHash(spec: object): string {
  const { id: _i, category: _c, ...rest } = spec as Record<string, unknown>;
  return createHash('sha256').update(canonicalJson(rest)).digest('hex');
}

export interface RoutineRow {
  id: string;
  name: string;
  description: string;
  category: string;
  tags: string[];
  spec: RoutineSpec;
  spec_hash: string;
  schema_version: number;
  trigger_type: string | null;
  action_types: string[];
  trigger_caps: string[];
  action_caps: string[];
  action_fallback_caps: string[];
}

/** 검증을 통과한 spec(id 제외 가능)을 받아 id·category를 확정한 완성 spec과 행 값을 만든다 */
export function prepareRoutine(input: Omit<RoutineSpec, 'id' | 'category'> & { id?: unknown; category?: unknown }, id: string): RoutineRow {
  const category = computeCategory(input as RoutineSpec);
  const spec: RoutineSpec = {
    schemaVersion: SCHEMA_VERSION,
    id,
    name: input.name,
    description: input.description,
    category,
    tags: input.tags,
    trigger: input.trigger,
    conditions: input.conditions,
    actions: input.actions,
  };
  const caps = deriveCaps(spec);
  return {
    id,
    name: spec.name,
    description: spec.description,
    category,
    tags: spec.tags,
    spec,
    spec_hash: specHash(spec),
    schema_version: SCHEMA_VERSION,
    trigger_type: spec.trigger ? spec.trigger.type : null,
    action_types: (spec.actions as Action[]).map((a) => a.type),
    trigger_caps: caps.triggerCaps,
    action_caps: caps.actionCaps,
    action_fallback_caps: caps.actionFallbackCaps,
  };
}
