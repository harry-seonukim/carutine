import { afterAll, beforeAll, describe, expect, it } from 'vitest';
import request from 'supertest';
import { ALL_CAPS, EV_CAPS, expectSpec, makeApp, openapiDoc, register, validSpec } from './helpers.js';
import { seedId } from '../seed/build-seed.js';

let ctx: Awaited<ReturnType<typeof makeApp>>;
let A: string; let B: string; let C: string; let D: string;   // 기기 토큰 4개
const auth = (t: string) => ({ Authorization: `Bearer ${t}` });

beforeAll(async () => {
  ctx = await makeApp();
  [A, B, C, D] = [await register(ctx.app), await register(ctx.app), await register(ctx.app), await register(ctx.app)];
});
afterAll(async () => { await ctx.db.close(); });

describe('openapi.yaml', () => {
  it('정의된 경로·메서드가 5장 API 11개 + /healthz와 같다', () => {
    const ops = Object.entries(openapiDoc.paths).flatMap(([p, v]: any) =>
      Object.keys(v).filter((m) => ['get', 'post', 'put', 'delete'].includes(m)).map((m) => `${m.toUpperCase()} ${p}`)).sort();
    expect(ops).toEqual([
      'DELETE /v1/routines/{id}', 'DELETE /v1/routines/{id}/like', 'GET /healthz', 'GET /v1/gallery/shelves',
      'GET /v1/routines', 'GET /v1/routines/{id}', 'POST /v1/devices', 'POST /v1/routines',
      'POST /v1/routines/{id}/import', 'POST /v1/routines/{id}/report', 'PUT /v1/routines/{id}/like',
    ].sort());
  });
});

describe('GET /healthz · POST /v1/devices · 인증', () => {
  it('healthz', async () => {
    const r = await request(ctx.app).get('/healthz');
    expect(r.status).toBe(200); expectSpec(r, 'GET', '/healthz');
    expect(r.body.seedVersion).toBe('2026-11-12.1');
  });
  it('기기 등록: 토큰은 DB에 해시로만 저장', async () => {
    const r = await request(ctx.app).post('/v1/devices').send({ appVersion: '0.1.0' });
    expect(r.status).toBe(201); expectSpec(r, 'POST', '/v1/devices');
    const row = (await ctx.db.query('SELECT token_hash FROM devices WHERE id = $1', [r.body.deviceId])).rows[0];
    expect(row.token_hash).toMatch(/^[0-9a-f]{64}$/);
    expect(row.token_hash).not.toContain(r.body.token);
  });
  it('기기 등록: 본문 없이도 가능, 잘못된 appVersion은 400', async () => {
    expect((await request(ctx.app).post('/v1/devices')).status).toBe(201);
    const r = await request(ctx.app).post('/v1/devices').send({ appVersion: '<script>' });
    expect(r.status).toBe(400); expectSpec(r, 'POST', '/v1/devices');
  });
  it.each([
    ['토큰 없음', {}],
    ['형식 틀림', { Authorization: 'Bearer abc' }],
    ['없는 토큰', { Authorization: 'Bearer ' + 'A'.repeat(43) }],
    ['Bearer 아님', { Authorization: 'Basic ' + 'A'.repeat(43) }],
  ])('401: %s', async (_n, h) => {
    const r = await request(ctx.app).get('/v1/routines').set(h as any);
    expect(r.status).toBe(401); expectSpec(r, 'GET', '/v1/routines');
    expect(r.body.error.code).toBe('UNAUTHORIZED');
  });
});

describe('GET /v1/gallery/shelves', () => {
  it('caps 없음 → compat null, 선반 3개 순서 고정, 선반당 최대 10개', async () => {
    const r = await request(ctx.app).get('/v1/gallery/shelves').set(auth(A));
    expect(r.status).toBe(200); expectSpec(r, 'GET', '/v1/gallery/shelves');
    expect(r.body.shelves.map((s: any) => s.id)).toEqual(['now', 'popular', 'official']);
    for (const s of r.body.shelves) {
      expect(s.items.length).toBeLessThanOrEqual(10);
      for (const c of s.items) expect(c.compat).toBeNull();
    }
    // official은 featured_rank 순서: ★1~3이 맨 앞
    expect(r.body.shelves[2].items.slice(0, 3).map((c: any) => c.name)).toEqual(['퇴근길 겨울', '배터리 절약', '비 오는 밤']);
  });
  it('caps가 있으면 none은 빠진다 (와이퍼 없는 차 → 비 오는 밤 제외)', async () => {
    const r = await request(ctx.app).get('/v1/gallery/shelves').query({ caps: EV_CAPS }).set(auth(A));
    const names = r.body.shelves.flatMap((s: any) => s.items.map((c: any) => c.name));
    expect(names).not.toContain('비 오는 밤');
    expect(r.body.shelves.flatMap((s: any) => s.items).every((c: any) => c.compat !== 'none')).toBe(true);
  });
  it('now: 겨울 18시면 겨울 태그 + commute가 앞', async () => {
    const r = await request(ctx.app).get('/v1/gallery/shelves').query({ caps: ALL_CAPS, season: 'winter', hour: 18 }).set(auth(A));
    const now = r.body.shelves[0].items;
    expect(now[0].name).toBe('퇴근길 겨울');                 // 겨울 + commute (2점) 중 featured_rank 1
    expect(now.every((c: any) => c.tags.includes('겨울') || ['commute', 'night'].includes(c.category))).toBe(true);
  });
  it('now: 23시면 night가 앞', async () => {
    const r = await request(ctx.app).get('/v1/gallery/shelves').query({ caps: ALL_CAPS, season: 'autumn', hour: 23 }).set(auth(A));
    expect(r.body.shelves[0].items.map((c: any) => c.category)).toEqual(['night', 'night']);
  });
  it('now: 맞는 규칙이 없으면 공식 추천 순서로 채운다', async () => {
    const r = await request(ctx.app).get('/v1/gallery/shelves').query({ season: 'spring', hour: 13 }).set(auth(A));
    expect(r.body.shelves[0].items.map((c: any) => c.id)).toEqual(r.body.shelves[2].items.map((c: any) => c.id));
  });
  it('잘못된 hour·season은 400', async () => {
    const r = await request(ctx.app).get('/v1/gallery/shelves').query({ hour: 24 }).set(auth(A));
    expect(r.status).toBe(400); expectSpec(r, 'GET', '/v1/gallery/shelves');
    expect(r.body.error.details[0]).toEqual({ field: 'query.hour', reason: 'OUT_OF_RANGE' });
    expect((await request(ctx.app).get('/v1/gallery/shelves').query({ season: 'rainy' }).set(auth(A))).status).toBe(400);
  });
});

describe('GET /v1/routines (목록·검색)', () => {
  it('기본: popular 정렬 = featured_rank 순, 20개', async () => {
    const r = await request(ctx.app).get('/v1/routines').set(auth(A));
    expect(r.status).toBe(200); expectSpec(r, 'GET', '/v1/routines');
    expect(r.body.items).toHaveLength(20);
    expect(r.body.items[0].name).toBe('퇴근길 겨울');
    expect(r.body.nextCursor).toBeNull();
  });
  it('category 필터', async () => {
    const r = await request(ctx.app).get('/v1/routines').query({ category: 'night' }).set(auth(A));
    expect(r.body.items.map((c: any) => c.name).sort()).toEqual(['심야 귀가', '야근 퇴근']);
  });
  it('q: 이름·설명·태그 부분 일치, LIKE 특수문자는 글자로 취급', async () => {
    const tag = await request(ctx.app).get('/v1/routines').query({ q: '김서림' }).set(auth(A));
    expect(tag.body.items.map((c: any) => c.name).sort()).toEqual(['비 오는 날 김서림 방지', '비 오는 밤']);
    const pct = await request(ctx.app).get('/v1/routines').query({ q: '%' }).set(auth(A));
    expect(pct.body.items.map((c: any) => c.name).sort()).toEqual(['배터리 15% 알림', '배터리 절약', '연료 부족 알림']);
  });
  it('compat=hide_none: 연료 센서 없는 차에는 연료 부족 알림이 안 보이고, compat=all이면 none으로 보인다', async () => {
    const hide = await request(ctx.app).get('/v1/routines').query({ caps: EV_CAPS }).set(auth(A));
    expect(hide.body.items.map((c: any) => c.name)).not.toContain('연료 부족 알림');
    const all = await request(ctx.app).get('/v1/routines').query({ caps: EV_CAPS, compat: 'all' }).set(auth(A));
    expect(all.body.items.find((c: any) => c.name === '연료 부족 알림').compat).toBe('none');
  });
  it('fallback: 핸들 열선 없는 차 → 퇴근길 겨울은 partial + fallbackActions', async () => {
    const r = await request(ctx.app).get('/v1/routines').query({ caps: EV_CAPS, q: '퇴근길 겨울' }).set(auth(A));
    expect(r.body.items[0]).toMatchObject({
      compat: 'partial', fallbackActions: ['steering_heater'], unsupportedActions: ['ambient_light'],
    });
  });
  it('cursor로 끝까지 넘기면 중복·누락 없이 20개', async () => {
    const seen: string[] = [];
    let cursor: string | undefined;
    for (let i = 0; i < 10; i++) {
      const r = await request(ctx.app).get('/v1/routines').query({ limit: 6, sort: 'new', ...(cursor ? { cursor } : {}) }).set(auth(A));
      expectSpec(r, 'GET', '/v1/routines');
      seen.push(...r.body.items.map((c: any) => c.id));
      if (!r.body.nextCursor) break;
      cursor = r.body.nextCursor;
    }
    expect(new Set(seen).size).toBe(20);
  });
  it.each([
    [{ cursor: 'garbage' }, 'query.cursor'],
    [{ limit: 51 }, 'query.limit'],
    [{ sort: 'likes' }, 'query.sort'],
    [{ category: 'family2' }, 'query.category'],
  ])('잘못된 쿼리 %j → 400', async (q, field) => {
    const r = await request(ctx.app).get('/v1/routines').query(q).set(auth(A));
    expect(r.status).toBe(400); expectSpec(r, 'GET', '/v1/routines');
    expect(r.body.error.details[0].field).toBe(field);
  });
});

describe('GET /v1/routines/{id}', () => {
  it('상세: 카드 + spec(계약 원본), 작성자 정보 없음', async () => {
    const r = await request(ctx.app).get(`/v1/routines/${seedId(1)}`).query({ caps: ALL_CAPS }).set(auth(A));
    expect(r.status).toBe(200); expectSpec(r, 'GET', '/v1/routines/{id}');
    expect(r.body.compat).toBe('full');
    expect(r.body.spec.id).toBe(seedId(1));
    expect(JSON.stringify(r.body)).not.toMatch(/author|deviceId|dv_/);
  });
  it.each(['rt_NOTVALID', 'rt_01M75HYYC00000000000SEED99', '../../etc'])('없는 id %s → 404', async (id) => {
    const r = await request(ctx.app).get(`/v1/routines/${encodeURIComponent(id)}`).set(auth(A));
    expect(r.status).toBe(404);
    expect(r.body.error.code).toBe('NOT_FOUND');
  });
});

describe('POST /v1/routines (올리기)', () => {
  let myId: string;
  it('올리면 201, id는 서버 발급, category는 서버가 다시 계산, 내 기기에서만 isMine', async () => {
    const r = await request(ctx.app).post('/v1/routines').set(auth(B))
      .send({ spec: validSpec({ id: 'rt_CLIENTSENTTHISIDXXXXXXXXXX', category: 'family' }) });
    expect(r.status).toBe(201); expectSpec(r, 'POST', '/v1/routines');
    myId = r.body.id;
    expect(myId).not.toBe('rt_CLIENTSENTTHISIDXXXXXXXXXX');
    expect(r.body.category).toBe('commute');
    expect(r.body.spec.category).toBe('commute');
    expect(r.body.spec.id).toBe(myId);
    expect(r.body).toMatchObject({ isMine: true, isOfficial: false, importCount: 0, likeCount: 0 });
    const other = await request(ctx.app).get(`/v1/routines/${myId}`).set(auth(A));
    expect(other.body.isMine).toBe(false);
  });
  it('Cap은 서버가 spec에서 계산해 저장한다', async () => {
    const row = (await ctx.db.query('SELECT trigger_caps, action_caps, action_fallback_caps FROM routines WHERE id = $1', [myId])).rows[0];
    expect(row).toEqual({ trigger_caps: ['hvac.cabinTemp', 'location'], action_caps: ['seat.heater', 'tts'], action_fallback_caps: ['', ''] });
  });
  it('최신순 목록 맨 앞에 보인다', async () => {
    const r = await request(ctx.app).get('/v1/routines').query({ sort: 'new', limit: 1 }).set(auth(C));
    expect(r.body.items[0].id).toBe(myId);
  });
  it('같은 기기가 1분 안에 같은 spec을 다시 올리면 DUPLICATE (키 순서·category가 달라도)', async () => {
    const s = validSpec();
    const reordered = Object.fromEntries(Object.entries({ ...s, category: 'ev' }).reverse());
    const r = await request(ctx.app).post('/v1/routines').set(auth(B)).send({ spec: reordered });
    expect(r.status).toBe(400); expectSpec(r, 'POST', '/v1/routines');
    expect(r.body.error.details).toEqual([{ field: 'spec', reason: 'DUPLICATE' }]);
    // 다른 기기는 같은 내용을 올릴 수 있다
    expect((await request(ctx.app).post('/v1/routines').set(auth(C)).send({ spec: s })).status).toBe(201);
  });
  it.each([
    ['좌표', { description: '37.5665, 126.9780 근처' }, 'spec.description', 'COORDINATE'],
    ['도분초', { description: '37°33\'59" 지점' }, 'spec.description', 'COORDINATE'],
    ['URL', { name: 'bit.ly/abc' }, 'spec.name', 'URL'],
    ['https', { description: 'https://example.com 참고' }, 'spec.description', 'URL'],
    ['전화번호', { description: '문의 010-1234-5678' }, 'spec.description', 'PHONE'],
    ['대표번호', { tags: ['1588 1234'] }, 'spec.tags.0', 'PHONE'],
    ['이메일', { description: 'me@example.com' }, 'spec.description', 'EMAIL'],
    ['speak 좌표', { actions: [{ type: 'speak', text: '126.97801 도착' }] }, 'spec.actions.0.text', 'COORDINATE'],
    ['금지어', { name: '씨 발 루틴' }, 'spec.name', 'BANNED_WORD'],
    ['금지어(대소문자)', { tags: ['FUCK'] }, 'spec.tags.0', 'BANNED_WORD'],
  ])('텍스트 차단: %s', async (_n, over, field, reason) => {
    const r = await request(ctx.app).post('/v1/routines').set(auth(D)).send({ spec: validSpec(over) });
    expect(r.status).toBe(400); expectSpec(r, 'POST', '/v1/routines');
    expect(r.body.error.details).toContainEqual({ field, reason });
  });
  it('설명의 일반 숫자(10℃, 20%, 23.5도)는 통과', async () => {
    const r = await request(ctx.app).post('/v1/routines').set(auth(D))
      .send({ spec: validSpec({ name: '숫자 확인', description: '10℃ 아래, 배터리 20%, 23.5도로' }) });
    expect(r.status).toBe(201);
  });
  it.each([
    ['정의되지 않은 필드', { author: 'kim' }, 'spec.author', 'UNKNOWN_FIELD'],
    ['좌표 필드', { trigger: { type: 'location_exit', place: 'work', lat: 37.5 } }, 'spec.trigger.lat', 'UNKNOWN_FIELD'],
    ['허용 안 된 장소', { trigger: { type: 'location_exit', place: 'school' } }, 'spec.trigger.place', 'INVALID_VALUE'],
    ['이름 21자', { name: '가'.repeat(21) }, 'spec.name', 'TOO_LONG'],
    ['동작 0개', { actions: [] }, 'spec.actions', 'TOO_FEW'],
    ['온도 범위 밖', { actions: [{ type: 'hvac_temp', mode: 'set', value: 35 }] }, 'spec.actions.0.value', 'OUT_OF_RANGE'],
    ['풍량 범위 밖', { actions: [{ type: 'hvac_fan', level: 11 }] }, 'spec.actions.0.level', 'OUT_OF_RANGE'],
    ['trigger null + conditions', { trigger: null }, 'spec.conditions', 'CONDITIONS_WITHOUT_TRIGGER'],
    ['없는 동작 타입', { actions: [{ type: 'open_trunk' }] }, 'spec.actions.0.type', 'UNKNOWN_TYPE'],
    ['schemaVersion 2', { schemaVersion: 2 }, 'spec.schemaVersion', 'INVALID_VALUE'],
  ])('스키마 위반: %s', async (_n, over, field, reason) => {
    const r = await request(ctx.app).post('/v1/routines').set(auth(D)).send({ spec: validSpec(over) });
    expect(r.status).toBe(400); expectSpec(r, 'POST', '/v1/routines');
    expect(r.body.error.code).toBe('VALIDATION_FAILED');
    expect(r.body.error.details).toContainEqual({ field, reason });
  });
  it('spec 없이·여분 필드·JSON 아님 → 400', async () => {
    expect((await request(ctx.app).post('/v1/routines').set(auth(D)).send({})).status).toBe(400);
    expect((await request(ctx.app).post('/v1/routines').set(auth(D)).send({ spec: validSpec(), x: 1 })).body.error.details)
      .toContainEqual({ field: 'x', reason: 'UNKNOWN_FIELD' });
    const bad = await request(ctx.app).post('/v1/routines').set(auth(D)).set('content-type', 'application/json').send('{"spec":');
    expect(bad.status).toBe(400);
    expect(bad.body.error.details).toEqual([{ field: 'body', reason: 'INVALID_JSON' }]);
  });
  it('본문 16KB 초과 → 400 TOO_LARGE', async () => {
    const r = await request(ctx.app).post('/v1/routines').set(auth(D)).send({ spec: validSpec({ description: 'x'.repeat(17_000) }) });
    expect(r.status).toBe(400);
    expect(r.body.error.details).toEqual([{ field: 'body', reason: 'TOO_LARGE' }]);
  });
});

describe('가져오기·좋아요 (기기당 1회 집계)', () => {
  const id = seedId(2);
  it('같은 기기가 여러 번 가져와도 1, 다른 기기가 가져오면 2', async () => {
    const r1 = await request(ctx.app).post(`/v1/routines/${id}/import`).set(auth(A));
    expect(r1.status).toBe(200); expectSpec(r1, 'POST', '/v1/routines/{id}/import');
    expect(r1.body.importCount).toBe(1);
    expect(r1.body.spec.id).toBe(id);
    expect((await request(ctx.app).post(`/v1/routines/${id}/import`).set(auth(A))).body.importCount).toBe(1);
    expect((await request(ctx.app).post(`/v1/routines/${id}/import`).set(auth(B))).body.importCount).toBe(2);
  });
  it('가져오기 수는 이번 주 인기 선반 순서에 반영된다', async () => {
    const r = await request(ctx.app).get('/v1/gallery/shelves').set(auth(C));
    expect(r.body.shelves[1].items[0].id).toBe(id);
  });
  it('좋아요 PUT/DELETE는 멱등, likedByMe는 기기별', async () => {
    const p1 = await request(ctx.app).put(`/v1/routines/${id}/like`).set(auth(A));
    expect(p1.status).toBe(200); expectSpec(p1, 'PUT', '/v1/routines/{id}/like');
    expect(p1.body).toEqual({ likeCount: 1, likedByMe: true });
    expect((await request(ctx.app).put(`/v1/routines/${id}/like`).set(auth(A))).body).toEqual({ likeCount: 1, likedByMe: true });
    expect((await request(ctx.app).put(`/v1/routines/${id}/like`).set(auth(B))).body).toEqual({ likeCount: 2, likedByMe: true });
    expect((await request(ctx.app).get(`/v1/routines/${id}`).set(auth(A))).body.likedByMe).toBe(true);
    expect((await request(ctx.app).get(`/v1/routines/${id}`).set(auth(C))).body.likedByMe).toBe(false);
    const d1 = await request(ctx.app).delete(`/v1/routines/${id}/like`).set(auth(A));
    expectSpec(d1, 'DELETE', '/v1/routines/{id}/like');
    expect(d1.body).toEqual({ likeCount: 1, likedByMe: false });
    expect((await request(ctx.app).delete(`/v1/routines/${id}/like`).set(auth(A))).body).toEqual({ likeCount: 1, likedByMe: false });
    expect((await request(ctx.app).delete(`/v1/routines/${id}/like`).set(auth(C))).body).toEqual({ likeCount: 1, likedByMe: false });
  });
  it('동시에 여러 번 눌러도 1회만 집계', async () => {
    const id5 = seedId(5);
    await Promise.all(Array.from({ length: 8 }, () => request(ctx.app).put(`/v1/routines/${id5}/like`).set(auth(D))));
    await Promise.all(Array.from({ length: 8 }, () => request(ctx.app).post(`/v1/routines/${id5}/import`).set(auth(D))));
    const r = await request(ctx.app).get(`/v1/routines/${id5}`).set(auth(D));
    expect(r.body).toMatchObject({ likeCount: 1, importCount: 1 });
  });
  it('없는 루틴은 404', async () => {
    const r = await request(ctx.app).post(`/v1/routines/rt_01M75HYYC00000000000SEED99/import`).set(auth(A));
    expect(r.status).toBe(404); expectSpec(r, 'POST', '/v1/routines/{id}/import');
  });
});

describe('신고·숨김·내리기', () => {
  async function upload(token: string, name: string) {
    const r = await request(ctx.app).post('/v1/routines').set(auth(token)).send({ spec: validSpec({ name }) });
    expect(r.status).toBe(201);
    return r.body.id as string;
  }
  it('서로 다른 기기 3곳이 신고하면 숨겨지고(404), 같은 기기 중복 신고는 세지 않는다', async () => {
    const id = await upload(D, '신고 대상');
    const rep = (t: string) => request(ctx.app).post(`/v1/routines/${id}/report`).set(auth(t)).send({ reason: 'spam', detail: '광고' });
    const r1 = await rep(A);
    expect(r1.status).toBe(202); expectSpec(r1, 'POST', '/v1/routines/{id}/report');
    expect((await rep(A)).status).toBe(202);
    expect((await rep(A)).status).toBe(202);
    expect((await request(ctx.app).get(`/v1/routines/${id}`).set(auth(C))).status).toBe(200);   // 아직 1곳
    await rep(B);
    expect((await request(ctx.app).get(`/v1/routines/${id}`).set(auth(C))).status).toBe(200);   // 2곳
    await rep(C);
    expect((await request(ctx.app).get(`/v1/routines/${id}`).set(auth(C))).status).toBe(404);   // 3곳 → 숨김
    const list = await request(ctx.app).get('/v1/routines').query({ q: '신고 대상' }).set(auth(A));
    expect(list.body.items).toHaveLength(0);
    const row = (await ctx.db.query('SELECT report_count, status FROM routines WHERE id = $1', [id])).rows[0];
    expect(row).toEqual({ report_count: 3, status: 'hidden' });
  });
  it('공식 루틴은 3곳이 신고해도 숨기지 않는다', async () => {
    const id = seedId(3);
    for (const t of [A, B, C]) await request(ctx.app).post(`/v1/routines/${id}/report`).set(auth(t)).send({ reason: 'other' });
    expect((await request(ctx.app).get(`/v1/routines/${id}`).set(auth(D))).status).toBe(200);
  });
  it('신고 사유·상세 검증', async () => {
    const id = seedId(4);
    const bad = await request(ctx.app).post(`/v1/routines/${id}/report`).set(auth(A)).send({ reason: 'hate' });
    expect(bad.status).toBe(400); expectSpec(bad, 'POST', '/v1/routines/{id}/report');
    expect((await request(ctx.app).post(`/v1/routines/${id}/report`).set(auth(A)).send({ reason: 'spam', detail: 'x'.repeat(201) })).status).toBe(400);
  });
  it('내리기: 남의 루틴·시드는 403, 작성 기기는 204, 이후 404', async () => {
    const id = await upload(B, '내릴 루틴');
    const f = await request(ctx.app).delete(`/v1/routines/${id}`).set(auth(A));
    expect(f.status).toBe(403); expectSpec(f, 'DELETE', '/v1/routines/{id}');
    expect((await request(ctx.app).delete(`/v1/routines/${seedId(1)}`).set(auth(A))).status).toBe(403);
    const ok = await request(ctx.app).delete(`/v1/routines/${id}`).set(auth(B));
    expect(ok.status).toBe(204); expectSpec(ok, 'DELETE', '/v1/routines/{id}');
    expect((await request(ctx.app).get(`/v1/routines/${id}`).set(auth(B))).status).toBe(404);
    expect((await request(ctx.app).delete(`/v1/routines/${id}`).set(auth(B))).status).toBe(404);
    expect((await request(ctx.app).post(`/v1/routines/${id}/import`).set(auth(A))).status).toBe(404);
  });
});

describe('보안·로그', () => {
  it('없는 경로는 404, 오류 응답에 스택·SQL 없음', async () => {
    const r = await request(ctx.app).get('/nope');
    expect(r.status).toBe(404);
    expect(r.text).not.toMatch(/at |stack|SELECT|node_modules/);
  });
  it('보안 헤더(helmet), x-powered-by 없음', async () => {
    const r = await request(ctx.app).get('/healthz');
    expect(r.headers['x-content-type-options']).toBe('nosniff');
    expect(r.headers['strict-transport-security']).toBeTruthy();
    expect(r.headers['x-powered-by']).toBeUndefined();
  });
  it('로그에 토큰·Authorization·요청 본문이 남지 않는다', async () => {
    const before = ctx.logs.length;
    const token = await register(ctx.app);
    await request(ctx.app).post('/v1/routines').set(auth(token)).send({ spec: validSpec({ name: '로그확인비밀문구' }) });
    const lines = JSON.stringify(ctx.logs.slice(before));
    expect(lines).not.toContain(token);
    expect(lines).not.toContain('로그확인비밀문구');
    expect(lines).not.toMatch(/authorization|bearer/i);
    expect(ctx.logs.at(-1)).toMatchObject({ method: 'POST', route: '/v1/routines', status: 201 });
  });
});

describe('요청 횟수 제한 · HTTPS', () => {
  it('올리기 하루 제한을 넘으면 429 + Retry-After', async () => {
    const c2 = await makeApp({ limits: { uploadPerDay: 2 } });
    const t = await register(c2.app);
    for (const n of ['하나', '둘']) {
      expect((await request(c2.app).post('/v1/routines').set(auth(t)).send({ spec: validSpec({ name: n }) })).status).toBe(201);
    }
    const r = await request(c2.app).post('/v1/routines').set(auth(t)).send({ spec: validSpec({ name: '셋' }) });
    expect(r.status).toBe(429); expectSpec(r, 'POST', '/v1/routines');
    expect(r.body.error.code).toBe('RATE_LIMITED');
    expect(Number(r.headers['retry-after'])).toBeGreaterThan(0);
    await c2.db.close();
  });
  it('기기 등록은 IP 기준 제한', async () => {
    const c3 = await makeApp({ limits: { registerPerHourPerIp: 1 } });
    expect((await request(c3.app).post('/v1/devices')).status).toBe(201);
    const r = await request(c3.app).post('/v1/devices');
    expect(r.status).toBe(429); expectSpec(r, 'POST', '/v1/devices');
    await c3.db.close();
  });
  it('requireHttps면 HTTP 요청은 거부, 프록시가 https라고 알려 주면 통과, healthz는 예외', async () => {
    const c4 = await makeApp({ requireHttps: true, trustProxy: true });
    const plain = await request(c4.app).post('/v1/devices');
    expect(plain.status).toBe(403);
    expect(plain.body.error.code).toBe('FORBIDDEN');
    expect((await request(c4.app).post('/v1/devices').set('X-Forwarded-Proto', 'https')).status).toBe(201);
    expect((await request(c4.app).get('/healthz')).status).toBe(200);
    await c4.db.close();
  });
});
