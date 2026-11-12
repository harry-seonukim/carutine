import { readFileSync } from 'node:fs';
import request from 'supertest';
import YAML from 'yaml';
import Ajv2020 from 'ajv/dist/2020.js';
import addFormats from 'ajv-formats';
import { expect } from 'vitest';
import { loadConfig, type Config } from '../src/config.js';
import { createDb, type Db } from '../src/db/db.js';
import { migrate } from '../src/db/migrate.js';
import { seed } from '../src/db/seed.js';
import { BannedWords } from '../src/domain/textguard.js';
import { createApp } from '../src/http/app.js';

export const ALL_CAPS = 'location,time,ev.battery,fuel.level,wiper.state,hvac.cabinTemp,hvac.temp,hvac.fan,hvac.air,seat.heater,steeringwheel.heater,sidemirror.heater,light.ambient,tts,navi.waypoint';
/** 프론트엔드 문서 예시처럼 EV·연료 없음·와이퍼 없음·열선 일부만 있는 차 */
export const EV_CAPS = 'location,time,hvac.temp,hvac.fan,hvac.air,hvac.cabinTemp,tts,navi.waypoint,ev.battery,seat.heater';

export async function makeApp(over: Partial<Omit<Config, 'limits'>> & { limits?: Partial<Config['limits']> } = {}) {
  const base = loadConfig({ NODE_ENV: 'test', DATABASE_URL: await testDatabaseUrl() } as any);
  const cfg: Config = { ...base, ...over, limits: { ...base.limits, ...(over.limits ?? {}) } };
  const db: Db = await createDb(cfg.databaseUrl);
  await migrate(db);
  await seed(db);
  const logs: Record<string, unknown>[] = [];
  const app = createApp({ db, cfg, banned: BannedWords.fromFile(cfg.bannedWordsFile), log: (l) => logs.push(l) });
  return { app, db, cfg, logs };
}

/**
 * 기본은 PGlite 메모리 DB. TEST_DATABASE_URL(postgres://…)을 주면 실제 Postgres에 테스트마다 새 DB를 만든다.
 * 예) TEST_DATABASE_URL=postgres://carutine:carutine@localhost:5432/carutine npm test
 */
async function testDatabaseUrl(): Promise<string> {
  const admin = process.env.TEST_DATABASE_URL;
  if (!admin) return 'pglite://memory';
  const name = 'carutine_test_' + Math.random().toString(36).slice(2, 10);
  const db = await createDb(admin);
  await db.query(`CREATE DATABASE ${name}`);           // name은 위에서 만든 영숫자만
  await db.close();
  const u = new URL(admin);
  u.pathname = '/' + name;
  return u.toString();
}

export async function register(app: any): Promise<string> {
  const r = await request(app).post('/v1/devices').send({ appVersion: '0.1.0' });
  expect(r.status).toBe(201);
  return r.body.token;
}

/* ---------- openapi.yaml 대조 ---------- */
const doc = YAML.parse(readFileSync(new URL('../openapi.yaml', import.meta.url), 'utf8'));
const ajv = new (Ajv2020 as any)({ strict: false, allErrors: true });
(addFormats as any)(ajv);
ajv.addSchema(doc, 'openapi');

function deref(o: any): any {
  while (o && o.$ref) {
    const path = o.$ref.replace(/^#\//, '').split('/').map((p: string) => p.replace(/~1/g, '/').replace(/~0/g, '~'));
    o = path.reduce((a: any, k: string) => a[k], doc);
  }
  return o;
}

export const openapiDoc = doc;

/** 응답이 openapi.yaml의 해당 경로·메서드·상태 코드 정의와 맞는지 확인한다 */
export function expectSpec(res: request.Response, method: string, path: string) {
  const op = doc.paths[path]?.[method.toLowerCase()];
  expect(op, `openapi에 ${method} ${path} 없음`).toBeTruthy();
  const resp = deref(op.responses[String(res.status)]);
  expect(resp, `openapi에 ${method} ${path} → ${res.status} 응답 정의 없음`).toBeTruthy();
  const media = resp.content?.['application/json'];
  if (!media) { expect(res.text ?? '').toBe(''); return; }
  const schemaRef = rawSchemaRef(op.responses[String(res.status)], path, method, res.status);
  const validate = ajv.getSchema(schemaRef) ?? ajv.compile({ $ref: schemaRef });
  const ok = validate(res.body);
  expect(ok, `${method} ${path} ${res.status} 스키마 불일치: ${JSON.stringify(validate.errors?.slice(0, 3))}`).toBe(true);
}

function rawSchemaRef(r: any, path: string, method: string, status: number): string {
  const enc = (s: string) => s.replace(/~/g, '~0').replace(/\//g, '~1');
  if (r.$ref) return 'openapi' + r.$ref + '/content/application~1json/schema';
  return `openapi#/paths/${enc(path)}/${method.toLowerCase()}/responses/${status}/content/application~1json/schema`;
}

export function validSpec(over: Record<string, unknown> = {}) {
  return {
    schemaVersion: 1,
    name: '테스트 루틴',
    description: '시트를 데워요',
    tags: ['겨울'],
    trigger: { type: 'location_exit', place: 'work' },
    conditions: [{ type: 'cabin_temp', op: 'below', value: 10 }],
    actions: [{ type: 'seat_heater', seat: 'driver', level: 2 }, { type: 'speak', text: '데울게요' }],
    ...over,
  };
}
