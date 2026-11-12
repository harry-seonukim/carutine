/**
 * 요청 횟수 제한 (백엔드 문서 6장, 값은 환경 변수). 인증된 요청은 기기 ID, 기기 등록은 IP 기준.
 * 메모리 저장소라 서버 1대 기준이다(무료 배포 1인스턴스 전제). 재시작하면 초기화된다.
 */
import rateLimit, { ipKeyGenerator, type Options } from 'express-rate-limit';
import type { Request } from 'express';
import { ApiError } from './errors.js';
import type { Config } from '../config.js';

function limiter(windowMs: number, limit: number, key: (req: Request) => string, skipFailedRequests = false) {
  const opts: Partial<Options> = {
    windowMs,
    limit,
    standardHeaders: 'draft-7',
    legacyHeaders: false,
    keyGenerator: key,
    skipFailedRequests,                       // true면 4xx·5xx 응답은 횟수에 넣지 않는다
    handler: (_req, res, _next, o) => {
      const retry = Math.ceil(o.windowMs / 1000);
      if (!res.getHeader('Retry-After')) res.setHeader('Retry-After', String(retry));
      res.status(429).json(new ApiError('RATE_LIMITED').toJSON());
    },
  };
  return rateLimit(opts);
}

const byDevice = (req: Request) => `d:${req.deviceId}`;
// IPv6는 /56 단위로 묶어 주소를 바꿔 가며 제한을 피하지 못하게 한다
const byIp = (req: Request) => `ip:${ipKeyGenerator(req.ip ?? '')}`;
const MIN = 60_000;
const DAY = 24 * 60 * MIN;

export function makeLimiters(c: Config['limits']) {
  return {
    read: limiter(MIN, c.readPerMin, byDevice),
    // 올리기 하루 제한은 성공한 것만 센다(검증에 걸려 고쳐 다시 올리는 사용자를 막지 않음). 실패 요청은 read 제한이 막는다.
    upload: limiter(DAY, c.uploadPerDay, byDevice, true),
    report: limiter(DAY, c.reportPerDay, byDevice),
    register: limiter(60 * MIN, c.registerPerHourPerIp, byIp),
  };
}
