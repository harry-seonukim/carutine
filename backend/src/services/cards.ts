/** DB 행 → 루틴 카드(5장). 작성자 정보는 내리지 않는다. isMine은 요청 기기 본인 루틴에서만 true(11/11 PM 결정 Q2). */
import { judgeCompat } from '../domain/caps.js';
import type { CapKey } from '../domain/contract.js';

/** 카드에 필요한 컬럼. $1 = 요청 기기 ID */
export const CARD_COLUMNS = `
  r.id, r.name, r.description, r.category, r.tags, r.trigger_type, r.action_types,
  r.trigger_caps, r.action_caps, r.action_fallback_caps, r.import_count, r.like_count,
  r.is_official, r.featured_rank, r.created_at,
  (r.author_device_id IS NOT NULL AND r.author_device_id = $1) AS is_mine,
  EXISTS (SELECT 1 FROM routine_likes l WHERE l.routine_id = r.id AND l.device_id = $1) AS liked_by_me`;

export interface CardRow {
  id: string; name: string; description: string; category: string; tags: string[];
  trigger_type: string | null; action_types: string[]; trigger_caps: string[]; action_caps: string[];
  action_fallback_caps: string[]; import_count: number; like_count: number; is_official: boolean;
  featured_rank: number | null; created_at: Date | string; is_mine: boolean; liked_by_me: boolean;
}

export interface Card {
  id: string; name: string; description: string; category: string; tags: string[];
  triggerType: string | null; actionTypes: string[];
  compat: 'full' | 'partial' | 'none' | null; unsupportedActions: string[]; fallbackActions: string[];
  importCount: number; likeCount: number; likedByMe: boolean; isOfficial: boolean; isMine: boolean;
  createdAt: string;
}

export function toCard(r: CardRow, caps: Set<CapKey> | null): Card {
  const j = judgeCompat(
    { triggerCaps: r.trigger_caps as CapKey[], actionCaps: r.action_caps as CapKey[], actionFallbackCaps: r.action_fallback_caps },
    r.action_types, caps);
  return {
    id: r.id,
    name: r.name,
    description: r.description,
    category: r.category,
    tags: r.tags,
    triggerType: r.trigger_type,
    actionTypes: r.action_types,
    compat: j.compat,
    unsupportedActions: j.unsupportedActions,
    fallbackActions: j.fallbackActions,
    importCount: Number(r.import_count),
    likeCount: Number(r.like_count),
    likedByMe: !!r.liked_by_me,
    isOfficial: !!r.is_official,
    isMine: !!r.is_mine,
    createdAt: new Date(r.created_at).toISOString(),
  };
}
