/** 서버 시작점. 시작할 때 마이그레이션을 적용하고(AUTO_MIGRATE=false로 끌 수 있음) 포트를 연다. */
import { loadConfig } from './config.js';
import { createDb } from './db/db.js';
import { migrate } from './db/migrate.js';
import { seed } from './db/seed.js';
import { BannedWords } from './domain/textguard.js';
import { createApp } from './http/app.js';

const cfg = loadConfig();
const db = await createDb(cfg.databaseUrl);
const log = (m: string) => console.log(JSON.stringify({ t: new Date().toISOString(), msg: m }));

if (process.env.AUTO_MIGRATE !== 'false') await migrate(db, log);
if (process.env.AUTO_SEED === 'true') await seed(db, log);

const banned = BannedWords.fromFile(cfg.bannedWordsFile);
const app = createApp({ db, cfg, banned });
const server = app.listen(cfg.port, () => log(`listening on :${cfg.port} (${cfg.env}, db=${db.kind}, bannedWords=${banned.size})`));

const shutdown = async () => {
  server.close();
  await db.close().catch(() => {});
  process.exit(0);
};
process.on('SIGTERM', shutdown);
process.on('SIGINT', shutdown);
