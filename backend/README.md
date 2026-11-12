# Carutine 갤러리 API (backend)

백엔드 문서: [백엔드] Carutine 갤러리 API 개발 문서 · 루틴 데이터 계약 v0.1(3장)이 원본이다.

## 지금 들어 있는 것 (11/11)
- `seed/seed_routines.json` — 시드 루틴 20개. **서버·차량 앱·HTML 데모가 함께 쓰는 원본.** 손으로 고치지 말고 `seed/seed-source.ts`를 고친 뒤 `npm run seed:build`.
- `src/domain/contract.ts` — 계약 v0.1 zod 스키마
- `src/domain/category.ts` — category 자동 분류(6장 규칙)
- `src/domain/caps.ts` — Cap 재계산·호환 판정(full/partial/none)
- `test/domain.test.ts` — 단위 테스트

## 실행 (Mac, Node 20 이상)
```bash
cd backend
npm ci
npm test            # 단위 테스트
npm run seed:build  # 시드 재생성 + 계약 검증
```

## 다음 (11/18까지)
DB 스키마·마이그레이션, API 11개, 업로드 검증, openapi.yaml, docker compose 로컬 실행.
