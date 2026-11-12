import { fileURLToPath } from 'node:url';
import { dirname, join, basename } from 'node:path';

/** 프로젝트 루트(backend/). tsx로 src/에서 실행하든, 빌드된 dist/src/에서 실행하든 같은 곳을 가리킨다. */
const here = dirname(fileURLToPath(import.meta.url));
export const ROOT = basename(dirname(here)) === 'dist' ? join(here, '..', '..') : join(here, '..');
