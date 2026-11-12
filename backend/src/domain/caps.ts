/**
 * Cap 재계산과 호환 판정 (백엔드 문서 6장, 프론트엔드 문서 4장과 같은 규칙).
 * 서버는 클라이언트가 보낸 Cap 값을 믿지 않고 spec에서 다시 만든다.
 */
import { CAP_KEYS, type Action, type CapKey, type Condition, type RoutineSpec, type Trigger } from './contract.js';
import type { Compat } from './types.js';

export const ACTION_CAP: Record<string, CapKey> = {
  hvac_temp: 'hvac.temp',
  hvac_fan: 'hvac.fan',
  hvac_air: 'hvac.air',
  seat_heater: 'seat.heater',
  steering_heater: 'steeringwheel.heater',
  mirror_heater: 'sidemirror.heater',
  ambient_light: 'light.ambient',
  speak: 'tts',
  suggest_charger: 'navi.waypoint',
};

export function triggerCap(t: Trigger): CapKey {
  switch (t.type) {
    case 'location_enter':
    case 'location_exit': return 'location';
    case 'energy_below': return t.source === 'fuel' ? 'fuel.level' : 'ev.battery';
    case 'wiper_on': return 'wiper.state';
  }
}

export function conditionCap(c: Condition): CapKey {
  return c.type === 'time_window' ? 'time' : 'hvac.cabinTemp';
}

export interface DerivedCaps {
  triggerCaps: CapKey[];          // trigger + conditions (중복 제거, 정렬)
  actionCaps: CapKey[];           // 동작 순서 유지
  actionFallbackCaps: string[];   // 동작별 fallback Cap, 없으면 ''
}

export function deriveCaps(spec: Pick<RoutineSpec, 'trigger' | 'conditions' | 'actions'>): DerivedCaps {
  const tc = new Set<CapKey>();
  if (spec.trigger) tc.add(triggerCap(spec.trigger));
  for (const c of spec.conditions) tc.add(conditionCap(c));
  const actions = spec.actions as Action[];
  return {
    triggerCaps: [...tc].sort(),
    actionCaps: actions.map((a) => ACTION_CAP[a.type]),
    actionFallbackCaps: actions.map((a) => (a.fallback ? ACTION_CAP[a.fallback.type] : '')),
  };
}

/** 쿼리 caps=a,b,c 파싱. 모르는 키는 무시. 파라미터가 없으면 null(판정하지 않음). */
export function parseCaps(raw: unknown): Set<CapKey> | null {
  if (raw === undefined || raw === null) return null;
  const s = Array.isArray(raw) ? raw.join(',') : String(raw);
  const known = new Set<string>(CAP_KEYS);
  return new Set(s.split(',').map((x) => x.trim()).filter((x) => known.has(x)) as CapKey[]);
}

export interface CompatResult {
  compat: Compat | null;
  unsupportedActions: string[];
  fallbackActions: string[];
}

export function judgeCompat(
  derived: DerivedCaps,
  actionTypes: string[],
  caps: Set<CapKey> | null,
): CompatResult {
  if (caps === null) return { compat: null, unsupportedActions: [], fallbackActions: [] };
  const triggerOk = derived.triggerCaps.every((c) => caps.has(c));
  const unsupported: string[] = [];
  const viaFallback: string[] = [];
  let allSupported = true;
  let runnable = 0;
  derived.actionCaps.forEach((cap, i) => {
    const supported = caps.has(cap);
    const fb = derived.actionFallbackCaps[i];
    const fallbackOk = !supported && fb !== '' && caps.has(fb as CapKey);
    if (!supported) allSupported = false;
    if (supported || fallbackOk) runnable++;
    if (fallbackOk) viaFallback.push(actionTypes[i]);
    if (!supported && !fallbackOk) unsupported.push(actionTypes[i]);
  });
  let compat: Compat;
  if (!triggerOk || runnable === 0) compat = 'none';
  else if (allSupported) compat = 'full';
  else compat = 'partial';
  return { compat, unsupportedActions: unsupported, fallbackActions: viaFallback };
}
