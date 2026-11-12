/**
 * 모든 설정은 환경 변수로 받는다. 비밀값(DATABASE_URL 등)은 코드·저장소에 두지 않는다.
 * 목록과 기본값은 .env.example에 있다.
 */
import 'dotenv/config';

const int = (v: string | undefined, d: number) => (v === undefined || v === '' ? d : Number.parseInt(v, 10));
const bool = (v: string | undefined, d: boolean) => (v === undefined || v === '' ? d : ['1', 'true', 'yes'].includes(v.toLowerCase()));

export interface Config {
  env: 'development' | 'test' | 'production';
  port: number;
  /** postgres://... (운영·docker) 또는 pglite://memory, pglite://./.pgdata (로컬·테스트) */
  databaseUrl: string;
  /** 프록시(Render 등) 뒤에서 X-Forwarded-Proto로 HTTPS 여부를 판단한다 */
  trustProxy: boolean;
  requireHttps: boolean;
  bannedWordsFile: string;
  limits: {
    readPerMin: number;
    uploadPerDay: number;
    reportPerDay: number;
    registerPerHourPerIp: number;
    duplicateWindowSec: number;
    hideAfterReports: number;
  };
}

export function loadConfig(env: NodeJS.ProcessEnv = process.env): Config {
  const nodeEnv = (env.NODE_ENV as Config['env']) ?? 'development';
  const production = nodeEnv === 'production';
  const databaseUrl = env.DATABASE_URL ?? (production ? '' : 'pglite://./.pgdata');
  if (!databaseUrl) throw new Error('DATABASE_URL 환경 변수가 필요합니다');
  return {
    env: nodeEnv,
    port: int(env.PORT, 3000),
    databaseUrl,
    trustProxy: bool(env.TRUST_PROXY, production),
    requireHttps: bool(env.REQUIRE_HTTPS, production),
    bannedWordsFile: env.BANNED_WORDS_FILE ?? 'config/banned-words.json',
    limits: {
      readPerMin: int(env.RATE_READ_PER_MIN, 120),
      uploadPerDay: int(env.RATE_UPLOAD_PER_DAY, 10),
      reportPerDay: int(env.RATE_REPORT_PER_DAY, 20),
      registerPerHourPerIp: int(env.RATE_REGISTER_PER_HOUR, 10),
      duplicateWindowSec: int(env.DUPLICATE_WINDOW_SEC, 60),
      hideAfterReports: int(env.HIDE_AFTER_REPORTS, 3),
    },
  };
}
