/**
 * 사람이 쓰는 텍스트(이름·설명·태그·speak 문구) 검사 (백엔드 문서 6장).
 * 위치 정보·연락처가 루틴에 섞여 공유되는 것을 막는다. 금지어 목록은 설정 파일로 분리한다.
 */
import { readFileSync, existsSync } from 'node:fs';
import { isAbsolute, join } from 'node:path';
import { ROOT } from '../paths.js';
import type { Action } from './contract.js';

export type TextIssueReason = 'COORDINATE' | 'URL' | 'PHONE' | 'EMAIL' | 'BANNED_WORD';
export interface TextIssue { field: string; reason: TextIssueReason }

const PATTERNS: [TextIssueReason, RegExp][] = [
  // 위·경도처럼 보이는 소수: 정수부 1~3자리 + 소수 3자리 이상 (예: 37.5665, 126.9780). '23.5도' 같은 값은 허용
  ['COORDINATE', /(?<![\d.])-?\d{1,3}\.\d{3,}(?![\d.])/],
  // 도분초 표기 (예: 37°33'59")
  ['COORDINATE', /\d{1,3}\s*°\s*\d{1,2}\s*['′]/],
  // 이메일을 URL보다 먼저 본다(me@example.com이 URL로 분류되지 않게)
  ['EMAIL', /[^\s@]+@[^\s@]+\.[^\s@]+/],
  ['URL', /\bhttps?:\/\/|\bwww\.|\b[a-z0-9-]+\.(com|net|org|kr|io|co|me|app|ly|gl)\b/i],
  // 국내 휴대폰·지역번호·대표번호 (하이픈·공백·점 허용), 그리고 9자리 이상 연속 숫자
  ['PHONE', /(?<!\d)(0\d{1,2}[\s.-]?\d{3,4}[\s.-]?\d{4}|1[5-9]\d{2}[\s.-]?\d{4})(?!\d)|\d{9,}/],
];

export class BannedWords {
  private words: string[];
  constructor(words: string[]) {
    this.words = [...new Set(words.map(normalize).filter((w) => w.length > 0))];
  }
  static fromFile(path: string): BannedWords {
    const p = isAbsolute(path) ? path : join(ROOT, path);
    if (!existsSync(p)) return new BannedWords([]);
    const raw = JSON.parse(readFileSync(p, 'utf8'));
    return new BannedWords(Array.isArray(raw) ? raw : raw.words ?? []);
  }
  hit(text: string): boolean {
    const t = normalize(text);
    return this.words.some((w) => t.includes(w));
  }
  get size() { return this.words.length; }
}

/** 공백·기호를 빼고 소문자로: '바 보', '바.보'도 걸리게 */
function normalize(s: string) {
  return s.normalize('NFKC').toLowerCase().replace(/[\s\p{P}\p{S}]/gu, '');
}

export function humanTexts(spec: { name: string; description: string; tags: string[]; actions: Action[] }) {
  const out: { field: string; text: string }[] = [
    { field: 'spec.name', text: spec.name },
    { field: 'spec.description', text: spec.description },
    ...spec.tags.map((t, i) => ({ field: `spec.tags.${i}`, text: t })),
  ];
  spec.actions.forEach((a, i) => {
    if (a.type === 'speak') out.push({ field: `spec.actions.${i}.text`, text: String(a.text) });
    if (a.fallback?.type === 'speak') out.push({ field: `spec.actions.${i}.fallback.text`, text: String(a.fallback.text) });
  });
  return out;
}

export function checkTexts(
  spec: { name: string; description: string; tags: string[]; actions: Action[] },
  banned: BannedWords,
): TextIssue[] {
  const issues: TextIssue[] = [];
  for (const { field, text } of humanTexts(spec)) {
    for (const [reason, re] of PATTERNS) {
      if (re.test(text)) { issues.push({ field, reason }); break; }
    }
    if (banned.hit(text)) issues.push({ field, reason: 'BANNED_WORD' });
  }
  return issues;
}
