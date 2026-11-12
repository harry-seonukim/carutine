/**
 * 시드 루틴 20개 입력값 (백엔드 문서 7장 표 + 11/11 PM 결정).
 * 이 파일을 직접 배포하지 않는다. `npm run seed:build`가 id·category를 붙이고 계약 검증을 통과시킨 뒤
 * seed/seed_routines.json을 만든다. 그 JSON이 서버·차량 앱·HTML 데모가 함께 쓰는 원본이다.
 *
 * - id: 서버 발급 형식(rt_ + ULID)을 따르되 시드는 고정값이어야 하므로 결정적으로 만든다(build 스크립트).
 * - category: 여기에 적지 않는다. 6장 규칙(D7)으로 서버가 계산한다.
 * - 인기 수치(가져오기·좋아요 수)는 넣지 않는다. 초기 노출 순서는 featuredRank로 정한다.
 */
const EVERY = ['mon', 'tue', 'wed', 'thu', 'fri', 'sat', 'sun'];
const WEEKDAY = ['mon', 'tue', 'wed', 'thu', 'fri'];
const WEEKEND = ['sat', 'sun'];

export interface SeedInput {
  no: number;                 // 7장 표의 번호 (id 생성에 사용, 바꾸지 않는다)
  featuredRank: number | null;
  name: string;
  description: string;
  tags: string[];
  trigger: unknown;
  conditions: unknown[];
  actions: unknown[];
}

export const SEED_INPUT: SeedInput[] = [
  {
    no: 1, featuredRank: 1, name: '퇴근길 겨울',
    description: '회사를 나설 때 추우면 핸들과 시트를 데워요. 앰비언트 라이트를 따뜻한 주황으로 바꾸고 한마디로 알려 줘요.',
    tags: ['겨울', '퇴근', '열선'],
    trigger: { type: 'location_exit', place: 'work' },
    conditions: [{ type: 'cabin_temp', op: 'below', value: 15 }],
    actions: [
      { type: 'steering_heater', on: true, fallback: { type: 'hvac_temp', mode: 'delta', value: 2 } },
      { type: 'seat_heater', seat: 'driver', level: 2 },
      { type: 'ambient_light', on: true, color: 'warm' },
      { type: 'speak', text: '퇴근 루틴을 실행했어요' },
    ],
  },
  {
    no: 2, featuredRank: 2, name: '배터리 절약',
    description: '배터리가 20% 아래로 내려가면 공조를 줄이고 근처 충전소를 경유지로 제안해요.',
    tags: ['배터리', '충전', '절약'],
    trigger: { type: 'energy_below', source: 'ev_battery', percent: 20 },
    conditions: [],
    actions: [
      { type: 'hvac_fan', level: 1 },
      { type: 'hvac_air', mode: 'recirc' },
      { type: 'suggest_charger' },
    ],
  },
  {
    no: 3, featuredRank: 3, name: '비 오는 밤',
    description: '밤에 비가 오기 시작하면 실내 조명을 낮추고 김서림을 막아요.',
    tags: ['비', '밤', '김서림'],
    trigger: { type: 'wiper_on' },
    conditions: [{ type: 'time_window', days: EVERY, start: '19:00', end: '06:00' }],
    actions: [
      { type: 'ambient_light', on: true, brightness: 'low' },
      { type: 'mirror_heater', on: true },
      { type: 'hvac_air', mode: 'fresh' },
    ],
  },
  {
    no: 4, featuredRank: 4, name: '출근길 예열',
    description: '추운 평일 아침, 집을 나서면 실내와 시트를 데워요.',
    tags: ['겨울', '출근', '아침'],
    trigger: { type: 'location_exit', place: 'home' },
    conditions: [
      { type: 'time_window', days: WEEKDAY, start: '06:00', end: '10:00' },
      { type: 'cabin_temp', op: 'below', value: 10 },
    ],
    actions: [
      { type: 'hvac_temp', mode: 'set', value: 23 },
      { type: 'hvac_fan', level: 3 },
      { type: 'seat_heater', seat: 'driver', level: 2 },
    ],
  },
  {
    no: 5, featuredRank: null, name: '출근길 더위 탈출',
    description: '더운 평일 아침, 집을 나서면 실내를 시원하게 식혀요.',
    tags: ['여름', '출근', '아침'],
    trigger: { type: 'location_exit', place: 'home' },
    conditions: [
      { type: 'time_window', days: WEEKDAY, start: '06:00', end: '10:00' },
      { type: 'cabin_temp', op: 'above', value: 27 },
    ],
    actions: [
      { type: 'hvac_temp', mode: 'set', value: 21 },
      { type: 'hvac_fan', level: 4 },
      { type: 'hvac_air', mode: 'recirc' },
    ],
  },
  {
    no: 6, featuredRank: 5, name: '퇴근길 여름',
    description: '더운 날 회사를 나서면 실내를 빠르게 식혀요.',
    tags: ['여름', '퇴근'],
    trigger: { type: 'location_exit', place: 'work' },
    conditions: [{ type: 'cabin_temp', op: 'above', value: 28 }],
    actions: [
      { type: 'hvac_temp', mode: 'set', value: 21 },
      { type: 'hvac_fan', level: 4 },
      { type: 'hvac_air', mode: 'recirc' },
      { type: 'speak', text: '시원하게 해 드릴게요' },
    ],
  },
  {
    no: 7, featuredRank: 6, name: '집 도착',
    description: '집에 도착하면 열선과 조명을 끄고 수고했다고 말해 줘요.',
    tags: ['귀가', '퇴근'],
    trigger: { type: 'location_enter', place: 'home' },
    conditions: [],
    actions: [
      { type: 'seat_heater', seat: 'driver', level: 0 },
      { type: 'steering_heater', on: false },
      { type: 'ambient_light', on: false },
      { type: 'speak', text: '오늘도 수고했어요' },
    ],
  },
  {
    no: 8, featuredRank: null, name: '회사 도착',
    description: '회사에 도착하면 열선을 끄고 바람을 약하게 줄여요.',
    tags: ['출근'],
    trigger: { type: 'location_enter', place: 'work' },
    conditions: [],
    actions: [
      { type: 'seat_heater', seat: 'driver', level: 0 },
      { type: 'steering_heater', on: false },
      { type: 'hvac_fan', level: 1 },
    ],
  },
  {
    no: 9, featuredRank: 7, name: '비 오는 날 김서림 방지',
    description: '와이퍼를 켜면 바깥 공기로 바꾸고 바람을 올려 김서림을 막아요.',
    tags: ['비', '김서림'],
    trigger: { type: 'wiper_on' },
    conditions: [],
    actions: [
      { type: 'hvac_air', mode: 'fresh' },
      { type: 'hvac_fan', level: 3 },
    ],
  },
  {
    no: 10, featuredRank: null, name: '추운 아침 한 번에',
    description: '버튼 하나로 온도, 바람, 시트·핸들·미러 열선을 한 번에 켜요.',
    tags: ['겨울', '아침', '난방'],
    trigger: null,
    conditions: [],
    actions: [
      { type: 'hvac_temp', mode: 'set', value: 24 },
      { type: 'hvac_fan', level: 4 },
      { type: 'seat_heater', seat: 'driver', level: 3 },
      { type: 'steering_heater', on: true },
      { type: 'mirror_heater', on: true },
    ],
  },
  {
    no: 11, featuredRank: null, name: '폭염 탈출',
    description: '버튼 하나로 온도를 낮추고 바람을 세게, 내기 순환으로 바꿔요.',
    tags: ['여름', '냉방'],
    trigger: null,
    conditions: [],
    actions: [
      { type: 'hvac_temp', mode: 'set', value: 18 },
      { type: 'hvac_fan', level: 5 },
      { type: 'hvac_air', mode: 'recirc' },
    ],
  },
  {
    no: 12, featuredRank: 8, name: '배터리 15% 알림',
    description: '배터리가 15% 아래로 내려가면 말로 알려 주고 충전소를 제안해요.',
    tags: ['배터리', '충전'],
    trigger: { type: 'energy_below', source: 'ev_battery', percent: 15 },
    conditions: [],
    actions: [
      { type: 'speak', text: '배터리가 15% 남았어요' },
      { type: 'suggest_charger' },
    ],
  },
  {
    no: 13, featuredRank: null, name: '연료 부족 알림',
    description: '연료가 15% 아래로 내려가면 말로 알려 줘요.',
    tags: ['연료', '주유'],
    trigger: { type: 'energy_below', source: 'fuel', percent: 15 },
    conditions: [],
    actions: [{ type: 'speak', text: '연료가 15% 남았어요' }],
  },
  {
    no: 14, featuredRank: null, name: '고속도로 절전',
    description: '버튼 하나로 바람을 줄이고 내기 순환으로 바꾸고 조명을 꺼요.',
    tags: ['고속도로', '절약', '배터리'],
    trigger: null,
    conditions: [],
    actions: [
      { type: 'hvac_fan', level: 1 },
      { type: 'hvac_air', mode: 'recirc' },
      { type: 'ambient_light', on: false },
    ],
  },
  {
    // 11/11 PM 결정(Q3): MVP 장소는 home·work만 노출 → custom1 대신 수동 루틴(trigger null)
    no: 15, featuredRank: 9, name: '아이 하원길',
    description: '아이를 태우기 전에 버튼을 누르면 실내를 알맞게 맞추고 조명을 낮춰요.',
    tags: ['아이', '가족'],
    trigger: null,
    conditions: [],
    actions: [
      { type: 'hvac_temp', mode: 'set', value: 23 },
      { type: 'hvac_fan', level: 2 },
      { type: 'ambient_light', on: true, brightness: 'low' },
    ],
  },
  {
    no: 16, featuredRank: null, name: '가족 나들이',
    description: '출발할 때 버튼을 누르면 실내를 맞추고 따뜻한 조명으로 인사해요.',
    tags: ['가족', '나들이'],
    trigger: null,
    conditions: [],
    actions: [
      { type: 'hvac_temp', mode: 'set', value: 23 },
      { type: 'ambient_light', on: true, color: 'warm' },
      { type: 'speak', text: '즐거운 나들이 되세요' },
    ],
  },
  {
    no: 17, featuredRank: null, name: '동승자 탑승',
    description: '동승자가 타면 조수석 시트를 데우고 온도를 1도 올려요.',
    tags: ['동승자', '가족'],
    trigger: null,
    conditions: [],
    actions: [
      { type: 'seat_heater', seat: 'passenger', level: 2 },
      { type: 'hvac_temp', mode: 'delta', value: 1 },
    ],
  },
  {
    no: 18, featuredRank: 10, name: '야근 퇴근',
    description: '밤늦게 회사를 나서면 조명을 낮추고 바깥 공기로 바꿔 졸음을 막아요.',
    tags: ['야근', '밤', '퇴근'],
    trigger: { type: 'location_exit', place: 'work' },
    conditions: [{ type: 'time_window', days: EVERY, start: '21:00', end: '05:00' }],
    actions: [
      { type: 'ambient_light', on: true, brightness: 'low' },
      { type: 'hvac_air', mode: 'fresh' },
      { type: 'speak', text: '조심히 들어가세요' },
    ],
  },
  {
    no: 19, featuredRank: null, name: '심야 귀가',
    description: '늦은 밤 집에 도착하면 조명을 끄고 바람을 약하게 줄여요.',
    tags: ['밤', '귀가'],
    trigger: { type: 'location_enter', place: 'home' },
    conditions: [{ type: 'time_window', days: EVERY, start: '22:00', end: '06:00' }],
    actions: [
      { type: 'ambient_light', on: false },
      { type: 'hvac_fan', level: 1 },
    ],
  },
  {
    no: 20, featuredRank: 11, name: '주말 드라이브',
    description: '주말 낮에 집을 나서면 파란 조명과 바깥 공기로 기분을 바꿔요.',
    tags: ['주말', '드라이브'],
    trigger: { type: 'location_exit', place: 'home' },
    conditions: [{ type: 'time_window', days: WEEKEND, start: '08:00', end: '20:00' }],
    actions: [
      { type: 'ambient_light', on: true, color: 'blue' },
      { type: 'hvac_air', mode: 'fresh' },
      { type: 'speak', text: '즐거운 주말 드라이브 되세요' },
    ],
  },
];
