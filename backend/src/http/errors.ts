/** 오류 형식 (백엔드 문서 5장): { error: { code, message, details? } } */
import type { z } from 'zod';
type ZodIssue = z.core.$ZodIssue;

export type ErrorCode = 'UNAUTHORIZED' | 'FORBIDDEN' | 'NOT_FOUND' | 'VALIDATION_FAILED' | 'RATE_LIMITED' | 'INTERNAL';
const STATUS: Record<ErrorCode, number> = {
  UNAUTHORIZED: 401, FORBIDDEN: 403, NOT_FOUND: 404, VALIDATION_FAILED: 400, RATE_LIMITED: 429, INTERNAL: 500,
};
const DEFAULT_MESSAGE: Record<ErrorCode, string> = {
  UNAUTHORIZED: '기기 인증이 필요해요. 앱을 다시 실행해 주세요.',
  FORBIDDEN: '이 작업을 할 수 있는 권한이 없어요.',
  NOT_FOUND: '루틴을 찾을 수 없어요. 내려갔거나 숨겨졌을 수 있어요.',
  VALIDATION_FAILED: '입력한 내용을 확인해 주세요.',
  RATE_LIMITED: '요청이 너무 많아요. 잠시 후 다시 시도해 주세요.',
  INTERNAL: '일시적인 문제가 생겼어요. 잠시 후 다시 시도해 주세요.',
};

export interface ErrorDetail { field: string; reason: string }

export class ApiError extends Error {
  readonly status: number;
  constructor(readonly code: ErrorCode, message?: string, readonly details?: ErrorDetail[]) {
    super(message ?? DEFAULT_MESSAGE[code]);
    this.status = STATUS[code];
  }
  toJSON() {
    return { error: { code: this.code, message: this.message, ...(this.details?.length ? { details: this.details } : {}) } };
  }
}

/** zod 이슈 → { field, reason }. reason은 앱이 분기할 수 있는 고정 문자열 */
export function zodDetails(issues: ZodIssue[], prefix: string): ErrorDetail[] {
  const out: ErrorDetail[] = [];
  for (const i of issues) {
    const path = [prefix, ...i.path].filter((p) => p !== '').join('.');
    if (i.code === 'unrecognized_keys') {
      for (const k of i.keys) out.push({ field: path ? `${path}.${k}` : k, reason: 'UNKNOWN_FIELD' });
      continue;
    }
    out.push({ field: path, reason: reasonOf(i) });
  }
  // 같은 필드·사유 중복 제거
  return out.filter((d, idx) => out.findIndex((x) => x.field === d.field && x.reason === d.reason) === idx);
}

function reasonOf(i: ZodIssue): string {
  if (i.code === 'custom') return /^[A-Z_]+$/.test(i.message) ? i.message : 'INVALID';
  if (/^[A-Z_]+$/.test(i.message)) return i.message;            // regex 등에 지정한 사유
  switch (i.code) {
    case 'invalid_type': return /received undefined/.test(i.message) ? 'REQUIRED' : 'INVALID_TYPE';
    case 'invalid_value':
    case 'invalid_union': return 'INVALID_VALUE';
    case 'invalid_format': return 'INVALID_FORMAT';
    case 'too_big': return i.origin === 'array' || i.origin === 'set' ? 'TOO_MANY' : i.origin === 'string' ? 'TOO_LONG' : 'OUT_OF_RANGE';
    case 'too_small': return i.origin === 'array' || i.origin === 'set' ? 'TOO_FEW' : i.origin === 'string' ? 'TOO_SHORT' : 'OUT_OF_RANGE';
    case 'not_multiple_of': return 'OUT_OF_RANGE';
    default: return 'INVALID';
  }
}
