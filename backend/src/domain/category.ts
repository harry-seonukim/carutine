/**
 * category 자동 분류 (11/11 PM 결정 D7, 백엔드 문서 6장).
 * 올릴 때와 시드를 넣을 때 서버가 spec에서 계산해 클라이언트 값을 덮어쓴다. 위에서부터 첫 번째로 맞는 규칙.
 *   1. trigger energy_below → ev
 *   2. trigger wiper_on     → weather
 *   3. trigger location_* 이고 time_window가 밤 구간(19:00~05:00)에 걸치면 → night
 *   4. trigger location_*   → commute
 *   5. trigger null         → etc
 * family는 계약에 남지만 이 규칙으로는 나오지 않는다.
 *
 * '걸친다'의 해석(확인 필요): time_window의 시작 시각이 19:00 이상이거나 05:00 미만이면 밤으로 본다.
 * 단순 겹침으로 보면 '주말 드라이브'(08:00~20:00)가 1시간 겹쳐 night가 되는데, 문서는 이 루틴이
 * commute로 분류된다고 적고 있어 그 의도에 맞췄다. UX 분류표(11/20)가 나오면 이 함수만 고친다.
 */
import type { Category } from './types.js';
import type { RoutineSpec } from './contract.js';

export const NIGHT_START = 19 * 60;
export const NIGHT_END = 5 * 60;

const toMin = (hhmm: string) => Number(hhmm.slice(0, 2)) * 60 + Number(hhmm.slice(3, 5));

export function isNightWindow(start: string): boolean {
  const s = toMin(start);
  return s >= NIGHT_START || s < NIGHT_END;
}

export function computeCategory(spec: Pick<RoutineSpec, 'trigger' | 'conditions'>): Category {
  const t = spec.trigger;
  if (t === null) return 'etc';
  if (t.type === 'energy_below') return 'ev';
  if (t.type === 'wiper_on') return 'weather';
  // location_enter | location_exit
  const tw = spec.conditions.find((c) => c.type === 'time_window');
  if (tw && tw.type === 'time_window' && isNightWindow(tw.start)) return 'night';
  return 'commute';
}
