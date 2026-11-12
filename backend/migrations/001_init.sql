-- 001: 초기 스키마 (백엔드 문서 4장)
-- 루틴 원본은 spec(JSONB)에 그대로 두고, 검색·필터용 값은 컬럼으로 뽑는다.
-- Cap 배열과 category는 올릴 때·시드를 넣을 때 서버가 spec에서 다시 계산한다(클라이언트 값을 믿지 않음).

CREATE TABLE devices (
  id            TEXT PRIMARY KEY,                    -- 'dv_' + ULID
  token_hash    TEXT NOT NULL UNIQUE,                -- SHA-256(token) hex, 원문 미저장
  app_version   TEXT,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  last_seen_at  TIMESTAMPTZ
);

CREATE TABLE routines (
  id                   TEXT PRIMARY KEY,             -- 'rt_' + ULID
  author_device_id     TEXT REFERENCES devices(id),  -- 시드는 NULL. 응답에는 내리지 않는다
  name                 TEXT NOT NULL,
  description          TEXT NOT NULL DEFAULT '',
  category             TEXT NOT NULL CHECK (category IN ('commute','weather','ev','family','night','etc')),
  tags                 TEXT[] NOT NULL DEFAULT '{}',
  spec                 JSONB NOT NULL,               -- 3장 계약 원본
  spec_hash            TEXT NOT NULL,                -- 중복 올리기 판정용 (id·category 제외한 spec의 SHA-256)
  schema_version       INT NOT NULL DEFAULT 1,
  trigger_type         TEXT,                         -- spec.trigger.type, 수동 루틴은 NULL
  action_types         TEXT[] NOT NULL,              -- 동작 순서 유지
  trigger_caps         TEXT[] NOT NULL,              -- trigger + conditions의 Cap
  action_caps          TEXT[] NOT NULL,              -- 동작별 Cap (순서 유지)
  action_fallback_caps TEXT[] NOT NULL,              -- 동작별 fallback Cap, 없으면 ''
  is_official          BOOLEAN NOT NULL DEFAULT false,
  featured_rank        INT,                          -- 초기 노출 순서 (작을수록 앞)
  status               TEXT NOT NULL DEFAULT 'active' CHECK (status IN ('active','hidden','deleted')),
  import_count         INT NOT NULL DEFAULT 0,
  like_count           INT NOT NULL DEFAULT 0,
  report_count         INT NOT NULL DEFAULT 0,
  created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at           TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_routines_category ON routines (category) WHERE status = 'active';
CREATE INDEX idx_routines_tags ON routines USING GIN (tags);
CREATE INDEX idx_routines_trigger_caps ON routines USING GIN (trigger_caps);
CREATE INDEX idx_routines_new ON routines (created_at DESC, id DESC) WHERE status = 'active';
CREATE INDEX idx_routines_author_dup ON routines (author_device_id, spec_hash, created_at);

CREATE TABLE routine_likes (
  device_id   TEXT NOT NULL REFERENCES devices(id),
  routine_id  TEXT NOT NULL REFERENCES routines(id),
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY (device_id, routine_id)
);

CREATE TABLE routine_imports (
  device_id   TEXT NOT NULL REFERENCES devices(id),
  routine_id  TEXT NOT NULL REFERENCES routines(id),
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY (device_id, routine_id)                -- 기기당 1회만 집계
);
CREATE INDEX idx_imports_recent ON routine_imports (created_at, routine_id);   -- '이번 주 인기'

CREATE TABLE routine_reports (
  id          BIGSERIAL PRIMARY KEY,
  routine_id  TEXT NOT NULL REFERENCES routines(id),
  device_id   TEXT NOT NULL REFERENCES devices(id),
  reason      TEXT NOT NULL CHECK (reason IN ('inappropriate','spam','unsafe','other')),
  detail      TEXT CHECK (detail IS NULL OR char_length(detail) <= 200),
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (routine_id, device_id)
);
