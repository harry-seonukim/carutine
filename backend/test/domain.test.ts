import { describe, expect, it } from 'vitest';
import { readFileSync } from 'node:fs';
import { routineSpecSchema, uploadSpecSchema, CAP_KEYS } from '../src/domain/contract.js';
import { computeCategory } from '../src/domain/category.js';
import { deriveCaps, judgeCompat, parseCaps } from '../src/domain/caps.js';
import { buildSeed, seedId } from '../seed/build-seed.js';

const seedFile = JSON.parse(readFileSync(new URL('../seed/seed_routines.json', import.meta.url), 'utf8'));
const byNo = (n: number) => seedFile.routines.find((r: any) => r.no === n).spec;

describe('seed_routines.json', () => {
  it('생성기 결과와 커밋된 파일이 같다 (손으로 고치지 않기)', () => {
    const { errors, file } = buildSeed();
    expect(errors).toEqual([]);
    expect(seedFile).toEqual(JSON.parse(JSON.stringify(file)));
  });
  it('20개 모두 계약 v0.1 검증을 통과한다', () => {
    expect(seedFile.routines).toHaveLength(20);
    for (const r of seedFile.routines) expect(routineSpecSchema.safeParse(r.spec).success).toBe(true);
  });
  it('인기 수치가 없고, 모두 공식이며, featuredRank는 1~11 연속', () => {
    const txt = JSON.stringify(seedFile);
    expect(txt).not.toMatch(/import|like/i);
    expect(seedFile.routines.every((r: any) => r.isOfficial)).toBe(true);
    const ranks = seedFile.routines.map((r: any) => r.featuredRank).filter((x: any) => x !== null).sort((a: number, b: number) => a - b);
    expect(ranks).toEqual([1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]);
  });
  it('id는 고정값이고 rt_+ULID 형식', () => {
    expect(byNo(1).id).toBe(seedId(1));
    expect(byNo(20).id).toMatch(/^rt_[0-9A-HJKMNP-TV-Z]{26}$/);
  });
  it('#15 아이 하원길은 수동 루틴 (11/11 PM 결정)', () => {
    expect(byNo(15).trigger).toBeNull();
    expect(byNo(15).conditions).toEqual([]);
  });
  it('좌표처럼 보이는 숫자(소수점)·URL·전화번호가 없다', () => {
    const texts = seedFile.routines.flatMap((r: any) => [r.spec.name, r.spec.description, ...r.spec.tags,
      ...r.spec.actions.filter((a: any) => a.type === 'speak').map((a: any) => a.text)]);
    for (const t of texts) expect(t).not.toMatch(/\d+\.\d{3,}|https?:|www\.|\d{2,3}-\d{3,4}-\d{4}|@/);
  });
});

describe('category 자동 분류 (D7)', () => {
  const loc = { type: 'location_exit', place: 'work' } as const;
  const tw = (start: string, end: string) => ({ type: 'time_window', days: ['mon'], start, end }) as const;
  it.each([
    [{ trigger: { type: 'energy_below', source: 'fuel', percent: 10 }, conditions: [] }, 'ev'],
    [{ trigger: { type: 'wiper_on' }, conditions: [tw('19:00', '06:00')] }, 'weather'],
    [{ trigger: loc, conditions: [tw('21:00', '05:00')] }, 'night'],
    [{ trigger: loc, conditions: [tw('00:30', '04:00')] }, 'night'],
    [{ trigger: loc, conditions: [tw('06:00', '10:00')] }, 'commute'],
    [{ trigger: loc, conditions: [tw('08:00', '20:00')] }, 'commute'],
    [{ trigger: loc, conditions: [] }, 'commute'],
    [{ trigger: null, conditions: [] }, 'etc'],
  ])('%j → %s', (spec, cat) => expect(computeCategory(spec as any)).toBe(cat));
});

describe('호환 판정 (10장 완료 조건 6가지)', () => {
  const ALL = new Set(CAP_KEYS);
  const without = (...k: string[]) => new Set([...ALL].filter((x) => !k.includes(x)));
  const judge = (spec: any, caps: Set<any> | null) =>
    judgeCompat(deriveCaps(spec), spec.actions.map((a: any) => a.type), caps);

  it('full: 모든 Cap 지원', () => {
    expect(judge(byNo(1), ALL)).toEqual({ compat: 'full', unsupportedActions: [], fallbackActions: [] });
  });
  it('partial(미지원): 앰비언트 라이트 없음', () => {
    expect(judge(byNo(1), without('light.ambient'))).toEqual(
      { compat: 'partial', unsupportedActions: ['ambient_light'], fallbackActions: [] });
  });
  it('partial(fallback): 핸들 열선 없음 → 설정 온도로 대체', () => {
    expect(judge(byNo(1), without('steeringwheel.heater'))).toEqual(
      { compat: 'partial', unsupportedActions: [], fallbackActions: ['steering_heater'] });
  });
  it('fallback Cap도 없으면 미지원으로 센다', () => {
    expect(judge(byNo(1), without('steeringwheel.heater', 'hvac.temp')).unsupportedActions).toEqual(['steering_heater']);
  });
  it('none(trigger 미지원): 연료 차가 아닌데 연료 트리거', () => {
    expect(judge(byNo(13), without('fuel.level')).compat).toBe('none');
  });
  it('none(조건 미지원): 실내 온도 센서 없음', () => {
    expect(judge(byNo(4), without('hvac.cabinTemp')).compat).toBe('none');
  });
  it('none(실행 가능 동작 0개)', () => {
    expect(judge(byNo(13), without('tts')).compat).toBe('none');
  });
  it('caps 없음 → compat null, 걸러내지 않음', () => {
    expect(judge(byNo(1), null)).toEqual({ compat: null, unsupportedActions: [], fallbackActions: [] });
  });
  it('수동 루틴은 trigger Cap이 없어 동작만으로 판정', () => {
    expect(judge(byNo(10), new Set(['hvac.temp'])).compat).toBe('partial');
  });
  it('parseCaps: 모르는 키 무시, 공백 허용, 미지정은 null', () => {
    expect([...parseCaps('tts, foo,hvac.temp')!].sort()).toEqual(['hvac.temp', 'tts']);
    expect(parseCaps(undefined)).toBeNull();
    expect(parseCaps('')!.size).toBe(0);
  });
});

describe('계약 v0.1 거부 규칙', () => {
  const base = () => JSON.parse(JSON.stringify(byNo(1)));
  const bad = (mut: (s: any) => void, schema = routineSpecSchema) => {
    const s = base(); mut(s); return schema.safeParse(s).success;
  };
  it.each([
    ['정의되지 않은 필드', (s: any) => { s.author = 'x'; }],
    ['이름 21자', (s: any) => { s.name = '가'.repeat(21); }],
    ['설명 81자', (s: any) => { s.description = '가'.repeat(81); }],
    ['태그 6개', (s: any) => { s.tags = ['a', 'b', 'c', 'd', 'e', 'f']; }],
    ['태그 11자', (s: any) => { s.tags = ['가'.repeat(11)]; }],
    ['허용되지 않은 장소', (s: any) => { s.trigger.place = 'school'; }],
    ['trigger 자리에 condition 타입', (s: any) => { s.trigger = { type: 'cabin_temp', op: 'below', value: 1 }; }],
    ['trigger null인데 conditions 있음', (s: any) => { s.trigger = null; }],
    ['같은 condition 중복', (s: any) => { s.conditions = [s.conditions[0], s.conditions[0]]; }],
    ['condition 3개', (s: any) => { s.conditions = [1, 2, 3].map(() => s.conditions[0]); }],
    ['동작 0개', (s: any) => { s.actions = []; }],
    ['동작 6개', (s: any) => { s.actions = [...s.actions, ...s.actions.slice(0, 2)]; }],
    ['fallback 안의 fallback', (s: any) => { s.actions[0].fallback.fallback = { type: 'hvac_fan', level: 1 }; }],
    ['hvac_temp set 31', (s: any) => { s.actions[0] = { type: 'hvac_temp', mode: 'set', value: 31 }; }],
    ['hvac_temp delta 0', (s: any) => { s.actions[0] = { type: 'hvac_temp', mode: 'delta', value: 0 }; }],
    ['seat_heater level 4', (s: any) => { s.actions[1].level = 4; }],
    ['speak 41자', (s: any) => { s.actions[3].text = '가'.repeat(41); }],
    ['cabin_temp 51', (s: any) => { s.conditions[0].value = 51; }],
    ['percent 소수', (s: any) => { s.trigger = { type: 'energy_below', source: 'fuel', percent: 10.5 }; s.conditions = []; }],
    ['time_window 요일 중복', (s: any) => { s.conditions = [{ type: 'time_window', days: ['mon', 'mon'], start: '07:00', end: '08:00' }]; }],
    ['time_window 24:00', (s: any) => { s.conditions = [{ type: 'time_window', days: ['mon'], start: '24:00', end: '08:00' }]; }],
    ['좌표 필드 추가', (s: any) => { s.trigger.lat = 37.5665; }],
    ['schemaVersion 2', (s: any) => { s.schemaVersion = 2; }],
  ])('%s', (_name, mut) => expect(bad(mut)).toBe(false));

  it('업로드 스키마는 id·category를 받아도 통과(서버가 덮어씀), 없어도 통과', () => {
    const s = base();
    expect(uploadSpecSchema.safeParse(s).success).toBe(true);
    delete s.id; delete s.category;
    expect(uploadSpecSchema.safeParse(s).success).toBe(true);
  });
});
