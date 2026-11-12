# Carutine 갤러리 API (backend)

Carutine(차량판 단축어)의 루틴 갤러리·공유 서버. 루틴 실행은 모두 차량 안에서 일어나고, 이 서버는 공유된 루틴을 보관하고
차량이 보낸 지원 기능(caps)으로 **내 차에 맞는 루틴만 골라**(Fit-Filtered Sharing) 내려준다. 서버가 죽어도 차량 앱의 내 루틴은 그대로 동작한다.

- 원본 문서: [백엔드] Carutine 갤러리 API 개발 문서 — 루틴 데이터 계약 v0.1(3장)이 원본이다. **계약은 혼자 바꾸지 않는다**(PM 승인).
- API 명세: [`openapi.yaml`](openapi.yaml) — 통합 테스트가 모든 응답을 이 파일로 검증한다.
- 보안 자체 점검: [`SECURITY_CHECK.md`](SECURITY_CHECK.md)

스택: Node.js 20+ · TypeScript · Express 5 · PostgreSQL 16(pg) · zod 4 · vitest + supertest. ORM 없이 SQL 마이그레이션 파일을 쓴다.

---

## 1. 로컬 실행

### A. Docker 없이 (가장 빠름)
Node 20 이상만 있으면 된다. DB는 PGlite(WASM으로 도는 실제 Postgres)를 `./.pgdata` 폴더에 만든다.
```bash
cd backend
npm ci
npm run migrate && npm run seed    # 테이블 + 시드 20개 (dev 서버를 끈 상태에서)
npm run dev                        # http://localhost:3000
curl localhost:3000/healthz
```
> PGlite 폴더는 한 프로세스만 열 수 있다. `npm run seed`는 dev 서버를 끄고 실행하거나, `.env`에 `AUTO_SEED=true`를 넣고 서버를 띄운다.

### B. docker compose (문서 9장 방식: Postgres 16 + API)
```bash
cd backend
docker compose up --build          # 마이그레이션·시드 자동, http://localhost:3000
```
DB만 띄우고 API는 로컬에서 돌리려면: `docker compose up db` 후 `.env`의 `DATABASE_URL`을 `postgres://carutine:carutine_local_only@localhost:5432/carutine`로.

### 빠른 확인
```bash
TOKEN=$(curl -s -X POST localhost:3000/v1/devices -H 'content-type: application/json' -d '{"appVersion":"0.1.0"}' | node -pe 'JSON.parse(require("fs").readFileSync(0)).token')
curl -s "localhost:3000/v1/gallery/shelves?caps=location,time,hvac.temp,hvac.fan,hvac.air,hvac.cabinTemp,tts,navi.waypoint,ev.battery&season=winter&hour=18" \
  -H "Authorization: Bearer $TOKEN"
```

## 2. 명령

| 명령 | 하는 일 |
|---|---|
| `npm run dev` | 개발 서버(파일 바뀌면 재시작). 시작할 때 마이그레이션 자동 적용 |
| `npm test` | 단위·통합 테스트(PGlite). `TEST_DATABASE_URL=postgres://…`를 주면 실제 Postgres에서 테스트마다 새 DB를 만들어 돈다 |
| `npm run typecheck` | 타입 검사 |
| `npm run migrate` / `npm run seed` | 마이그레이션 / 시드 적재(여러 번 실행해도 안전) |
| `npm run build` → `npm start` | 운영 빌드(`dist/`) 후 실행. 운영에서 마이그레이션·시드는 `npm run migrate:prod` / `npm run seed:prod` |
| `npm run seed:build` | `seed/seed-source.ts` → `seed/seed_routines.json` 재생성 + 계약 검증 |

## 3. 환경 변수
전체 목록과 기본값은 [`.env.example`](.env.example). **비밀값(DB 접속 정보)은 저장소에 올리지 않는다.** `.env`는 `.gitignore`에 있다.

| 변수 | 기본값 | 설명 |
|---|---|---|
| `DATABASE_URL` | 개발 `pglite://./.pgdata`, 운영은 **필수** | `postgres://…` 또는 `pglite://memory` · `pglite://<폴더>` |
| `NODE_ENV` | `development` | `production`이면 HTTPS 강제·프록시 신뢰가 기본 켜짐 |
| `PORT` | `3000` | |
| `REQUIRE_HTTPS` / `TRUST_PROXY` | 운영 `true` | 프록시가 보낸 `X-Forwarded-Proto`로 HTTPS를 판단, HTTP는 403 |
| `AUTO_MIGRATE` / `AUTO_SEED` | `true` / `false` | 서버 시작 때 실행 여부 |
| `BANNED_WORDS_FILE` | `config/banned-words.json` | 금지어 목록 |
| `RATE_*`, `DUPLICATE_WINDOW_SEC`, `HIDE_AFTER_REPORTS` | 120/분, 10/일, 20/일, 10/시간, 60초, 3 | 6장 제한값 |

## 4. 구조
```
backend/
├─ openapi.yaml               API 명세 (실제 동작과 같게 테스트로 고정)
├─ migrations/001_init.sql    스키마 (4장)
├─ seed/
│  ├─ seed_routines.json      시드 20개 원본 — 서버·차량 앱·HTML 데모가 같이 씀. 손으로 고치지 않는다
│  ├─ seed-source.ts          시드 입력값 (여기를 고치고 npm run seed:build)
│  └─ build-seed.ts           id·category 계산 + 계약 검증 → JSON 생성
├─ config/banned-words.json   금지어 목록(초안)
├─ src/
│  ├─ domain/                 계약 스키마(contract), category 분류, Cap·호환 판정(caps), 텍스트 검사(textguard)
│  ├─ db/                     DB 연결(pg / PGlite), 마이그레이션, 시드 적재
│  ├─ services/               카드 변환, 선반·목록, 올리기·가져오기·좋아요·신고·내리기
│  └─ http/                   Express 앱, 라우트, 인증, 요청 제한, 오류 형식
└─ test/                      domain.test.ts(계약·분류·호환 판정), api.test.ts(API 11개, openapi 대조)
```

## 5. 동작 메모 (문서에 없던 세부를 정한 것)
- **category '밤에 걸침'**: time_window의 **시작 시각**이 19:00 이상 또는 05:00 미만이면 night. 단순 겹침으로 보면 '주말 드라이브'(08~20시)가 night가 되어 문서 의도(commute)와 달라서다. UX 분류표(11/20)가 나오면 `src/domain/category.ts`만 고친다.
- **'now' 선반**: 맞는 규칙 수가 많은 순 → featured_rank. 하나도 맞지 않으면 공식 추천 순서로 채운다(빈 선반 방지). 시간 구간은 [시작, 끝)으로 본다.
- **목록 페이지**: `compat=hide_none`이면 trigger 미지원은 SQL에서, '실행 가능한 동작 0개'는 앱에서 거른다. 그래서 한 페이지가 limit보다 적을 수 있고, 끝은 `nextCursor: null`로 판단한다.
- **올리기 하루 10회**는 성공한 올리기만 센다. 검증에 걸린 요청은 분당 120회 제한으로만 막는다.
- **시드 id**는 고정값(`rt_01M75HYYC00000000000SEED01`~`20`). 서버·앱·데모가 같은 루틴을 같은 id로 가리킨다.
- 응답에 작성자 정보는 없다. `isMine`은 요청한 기기가 올린 루틴에서만 true.

## 6. 배포 (11/19 예정)
무료 플랜 후보: API는 Render 무료 웹 서비스, DB는 Neon 또는 Supabase 무료 Postgres. **가입 전에 현재 무료 조건을 다시 확인하고, 유료 전환이 필요하면 PM에게 먼저 묻는다.**
배포 서비스에 넣을 환경 변수: `NODE_ENV=production`, `DATABASE_URL`(DB 서비스가 준 값, `sslmode=require`), 필요하면 `AUTO_SEED=true`(첫 배포 1회).
세부 절차는 배포하면서 이 절에 채운다.

## 7. 시연 전 점검
1. 시연 **10분 전** `GET /healthz` 호출 → `{"status":"ok","db":"ok"}` 확인 (무료 서버는 잠들어 있다가 첫 요청에 늦게 깨어난다)
2. `seedVersion`이 차량 앱에 넣은 `seed_routines.json`의 `seedVersion`과 같은지 확인
3. 갤러리 선반 호출이 200인지 확인(위 '빠른 확인')
4. 서버가 응답하지 않으면 차량 앱은 앱 안의 `seed_routines.json`으로 갤러리를 보여준다(프론트엔드 문서 9장)
