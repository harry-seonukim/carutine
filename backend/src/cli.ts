/** npm run migrate | npm run seed */
import { loadConfig } from './config.js';
import { createDb } from './db/db.js';
import { migrate } from './db/migrate.js';
import { seed } from './db/seed.js';

const cmd = process.argv[2];
const db = await createDb(loadConfig().databaseUrl);
try {
  if (cmd === 'migrate') {
    const done = await migrate(db, console.log);
    if (!done.length) console.log('적용할 마이그레이션이 없습니다');
  } else if (cmd === 'seed') {
    await seed(db, console.log);
  } else {
    console.error('사용법: tsx src/cli.ts migrate|seed');
    process.exitCode = 1;
  }
} finally {
  await db.close();
}
