/**
 * 루틴 데이터 계약 v0.1 (백엔드 문서 3장이 원본).
 * 이 파일은 계약을 코드로 옮긴 것이다. 계약을 바꾸지 않는다. 바꿔야 하면 PM에게 요청하고
 * 백엔드 문서 3장 · 프론트엔드 문서 4장 · 이 파일을 함께 고친다.
 */
import { z } from 'zod';

/** 한글 1글자 = 1자. 이모지 등 서로게이트 쌍도 1자로 센다. */
export const charLen = (s: string) => [...s].length;
const text = (min: number, max: number) =>
  z.string().refine((s) => charLen(s) >= min, { message: 'TOO_SHORT' })
    .refine((s) => charLen(s) <= max, { message: 'TOO_LONG' });

export const SCHEMA_VERSION = 1 as const;
export const ROUTINE_ID_RE = /^rt_[0-9A-HJKMNP-TV-Z]{26}$/;

export const CATEGORIES = ['commute', 'weather', 'ev', 'family', 'night', 'etc'] as const;
export const PLACES = ['home', 'work', 'custom1', 'custom2', 'custom3'] as const;
export const DAYS = ['mon', 'tue', 'wed', 'thu', 'fri', 'sat', 'sun'] as const;

export const CAP_KEYS = [
  'location', 'time', 'ev.battery', 'fuel.level', 'wiper.state', 'hvac.cabinTemp',
  'hvac.temp', 'hvac.fan', 'hvac.air', 'seat.heater', 'steeringwheel.heater',
  'sidemirror.heater', 'light.ambient', 'tts', 'navi.waypoint',
] as const;
export type CapKey = (typeof CAP_KEYS)[number];

const place = z.enum(PLACES);
const hhmm = z.string().regex(/^([01]\d|2[0-3]):[0-5]\d$/, 'INVALID_TIME');

/* ---------- trigger (사건 조건) ---------- */
export const triggerSchema = z.discriminatedUnion('type', [
  z.object({ type: z.literal('location_enter'), place }).strict(),            // T1
  z.object({ type: z.literal('location_exit'), place }).strict(),             // T2
  z.object({                                                                  // T4
    type: z.literal('energy_below'),
    source: z.enum(['ev_battery', 'fuel']),
    percent: z.number().int().min(1).max(99),
  }).strict(),
  z.object({ type: z.literal('wiper_on') }).strict(),                         // T5
]);

/* ---------- condition (상태 조건) ---------- */
export const conditionSchema = z.discriminatedUnion('type', [
  z.object({                                                                  // T3
    type: z.literal('time_window'),
    days: z.array(z.enum(DAYS)).min(1).max(7)
      .refine((d) => new Set(d).size === d.length, { message: 'DUPLICATE' }),
    start: hhmm,
    end: hhmm,
  }).strict(),
  z.object({                                                                  // T6
    type: z.literal('cabin_temp'),
    op: z.enum(['below', 'above']),
    value: z.number().int().min(-20).max(50),
  }).strict(),
]);

/* ---------- action (동작) ---------- */
const actionVariants = [
  z.object({                                                                  // A1
    type: z.literal('hvac_temp'),
    mode: z.enum(['set', 'delta']),
    value: z.number(),
  }).strict().superRefine((a, ctx) => {
    const ok = a.mode === 'set' ? a.value >= 16 && a.value <= 30
      : a.value >= -3 && a.value <= 3 && a.value !== 0;
    if (!ok) ctx.addIssue({ code: 'custom', path: ['value'], message: 'OUT_OF_RANGE' });
  }),
  z.object({ type: z.literal('hvac_fan'), level: z.number().int().min(0).max(10) }).strict(),      // A2
  z.object({ type: z.literal('hvac_air'), mode: z.enum(['recirc', 'fresh']) }).strict(),           // A3
  z.object({                                                                                        // A4
    type: z.literal('seat_heater'),
    seat: z.enum(['driver', 'passenger']),
    level: z.number().int().min(0).max(3),
  }).strict(),
  z.object({ type: z.literal('steering_heater'), on: z.boolean() }).strict(),                      // A5
  z.object({ type: z.literal('mirror_heater'), on: z.boolean() }).strict(),                        // A6
  z.object({                                                                                        // A7
    type: z.literal('ambient_light'),
    on: z.boolean(),
    color: z.enum(['warm', 'cool', 'blue', 'red']).optional(),
    brightness: z.enum(['low', 'default']).optional(),
  }).strict(),
  z.object({ type: z.literal('speak'), text: text(1, 40) }).strict(),                              // A8
  z.object({ type: z.literal('suggest_charger') }).strict(),                                        // A9
] as const;

const VARIANT_BY_TYPE = new Map<string, z.ZodTypeAny>(
  actionVariants.map((v) => {
    const inner: any = (v as any)._def?.schema ?? v;   // superRefine로 감싼 경우 안쪽 객체
    return [inner.shape.type.value as string, v];
  }),
);

/** fallback 없는 동작 1개. strict라 fallback 키가 있으면 거부된다(= fallback 안의 fallback 금지). */
export const baseActionSchema: z.ZodType<Action> = z.unknown().superRefine((a, ctx) => {
  const t = (a as any)?.type;
  const v = typeof t === 'string' ? VARIANT_BY_TYPE.get(t) : undefined;
  if (!v) { ctx.addIssue({ code: 'custom', path: ['type'], message: 'UNKNOWN_TYPE' }); return; }
  const r = v.safeParse(a);
  if (!r.success) r.error.issues.forEach((i) => ctx.addIssue({ ...(i as any), path: i.path }));
}) as any;

/** 동작 1개 + 선택적 fallback 1개(1단계만). */
export const actionSchema: z.ZodType<Action> = z.unknown().superRefine((a, ctx) => {
  if (a === null || typeof a !== 'object' || Array.isArray(a)) {
    ctx.addIssue({ code: 'custom', message: 'INVALID_ACTION' }); return;
  }
  const { fallback, ...own } = a as Record<string, unknown>;
  const r1 = baseActionSchema.safeParse(own);
  if (!r1.success) r1.error.issues.forEach((i) => ctx.addIssue({ ...(i as any), path: i.path }));
  if (fallback !== undefined) {
    const r2 = baseActionSchema.safeParse(fallback);
    if (!r2.success) r2.error.issues.forEach((i) => ctx.addIssue({ ...(i as any), path: ['fallback', ...i.path] }));
  }
}) as any;

export const ACTION_TYPES = ['hvac_temp', 'hvac_fan', 'hvac_air', 'seat_heater', 'steering_heater',
  'mirror_heater', 'ambient_light', 'speak', 'suggest_charger'] as const;
export type ActionType = (typeof ACTION_TYPES)[number];

/* ---------- 루틴 본문 ---------- */
const specShape = {
  schemaVersion: z.literal(SCHEMA_VERSION),
  name: text(1, 20),
  description: text(0, 80),
  category: z.enum(CATEGORIES),
  tags: z.array(text(1, 10)).max(5),
  trigger: triggerSchema.nullable(),
  conditions: z.array(conditionSchema).max(2),
  actions: z.array(actionSchema).min(1).max(5),
};

function structureRules(s: { trigger: unknown; conditions: { type: string }[] }, ctx: z.RefinementCtx) {
  if (s.trigger === null && s.conditions.length > 0) {
    ctx.addIssue({ code: 'custom', path: ['conditions'], message: 'CONDITIONS_WITHOUT_TRIGGER' });
  }
  const types = s.conditions.map((c) => c.type);
  if (new Set(types).size !== types.length) {
    ctx.addIssue({ code: 'custom', path: ['conditions'], message: 'DUPLICATE_CONDITION' });
  }
}

/** 저장·배포되는 완성 루틴 (id 포함). seed_routines.json의 spec이 이 모양이다. */
export const routineSpecSchema = z.object({ id: z.string().regex(ROUTINE_ID_RE), ...specShape })
  .strict().superRefine(structureRules);

/** 올리기 요청의 spec: id는 서버가 발급하므로 받지 않고(보내도 무시), category는 서버가 다시 계산한다. */
export const uploadSpecSchema = z.object({
  ...specShape,
  id: z.unknown().optional(),
  category: z.enum(CATEGORIES).optional(),
}).strict().superRefine(structureRules);

export type Trigger = z.infer<typeof triggerSchema>;
export type Condition = z.infer<typeof conditionSchema>;
export type RoutineSpec = z.infer<typeof routineSpecSchema>;
export interface Action { type: ActionType; fallback?: Action; [k: string]: unknown }
