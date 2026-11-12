/** 갤러리 선반 3개와 목록·검색 (백엔드 문서 5장) */
import type { Queryable } from '../db/db.js';
import type { CapKey } from '../domain/contract.js';
import { CARD_COLUMNS, toCard, type Card, type CardRow } from './cards.js';

export const SHELF_SIZE = 10;
const MAX_CANDIDATES = 500;

type Season = 'spring' | 'summer' | 'autumn' | 'winter';
interface Ranked { card: Card; featuredRank: number | null; recentImports: number; createdAt: number }

const byFeatured = (a: Ranked, b: Ranked) =>
  (a.featuredRank ?? Infinity) - (b.featuredRank ?? Infinity) || b.createdAt - a.createdAt || a.card.id.localeCompare(b.card.id);

/**
 * 'now' 선반 규칙(5장): 겨울이면 '겨울' 태그, 여름이면 '여름' 태그, 6~10시·17~21시면 commute, 19시~5시면 night를 우선한다.
 * 맞는 규칙 수가 많은 순 → featured_rank 순. 하나도 맞지 않으면 공식 추천 순서로 채운다(빈 선반 방지).
 * 구간은 [시작, 끝) 으로 본다: 6~10시 = 6:00~9:59, 19시~5시 = 19:00~4:59.
 */
export function nowScore(card: Card, season?: Season, hour?: number): number {
  let s = 0;
  if (season === 'winter' && card.tags.includes('겨울')) s++;
  if (season === 'summer' && card.tags.includes('여름')) s++;
  if (hour !== undefined) {
    const commuteHour = (hour >= 6 && hour < 10) || (hour >= 17 && hour < 21);
    const nightHour = hour >= 19 || hour < 5;
    if (commuteHour && card.category === 'commute') s++;
    if (nightHour && card.category === 'night') s++;
  }
  return s;
}

export async function getShelves(q: Queryable, deviceId: string, caps: Set<CapKey> | null, season?: Season, hour?: number) {
  const params: unknown[] = [deviceId];
  let capFilter = '';
  if (caps) { params.push([...caps]); capFilter = 'AND r.trigger_caps <@ $2::text[]'; }
  const rows = (await q.query<CardRow & { recent_imports: number }>(
    `SELECT ${CARD_COLUMNS},
            (SELECT count(*) FROM routine_imports i WHERE i.routine_id = r.id AND i.created_at > now() - interval '7 days')::int AS recent_imports
       FROM routines r
      WHERE r.status = 'active' ${capFilter}
      ORDER BY r.featured_rank ASC NULLS LAST, r.created_at DESC
      LIMIT ${MAX_CANDIDATES}`, params)).rows;

  const all: Ranked[] = rows
    .map((r) => ({ card: toCard(r, caps), featuredRank: r.featured_rank, recentImports: Number(r.recent_imports), createdAt: new Date(r.created_at).getTime() }))
    .filter((x) => x.card.compat !== 'none');

  const official = all.filter((x) => x.card.isOfficial).sort(byFeatured);
  const popular = [...all].sort((a, b) => b.recentImports - a.recentImports || byFeatured(a, b));
  const scored = all.map((x) => ({ x, s: nowScore(x.card, season, hour) })).filter((v) => v.s > 0)
    .sort((a, b) => b.s - a.s || byFeatured(a.x, b.x)).map((v) => v.x);
  const now = scored.length > 0 ? scored : official;

  const take = (l: Ranked[]) => l.slice(0, SHELF_SIZE).map((x) => x.card);
  return {
    shelves: [
      { id: 'now', title: '지금 상황에 맞는 루틴', items: take(now) },
      { id: 'popular', title: '이번 주 인기', items: take(popular) },
      { id: 'official', title: '공식 루틴', items: take(official) },
    ],
  };
}

export interface ListQuery {
  category?: string; q?: string; sort: 'popular' | 'new'; compat: 'hide_none' | 'all';
  caps: Set<CapKey> | null; offset: number; limit: number;
}

const escapeLike = (s: string) => s.replace(/[\\%_]/g, (c) => '\\' + c);

export async function listRoutines(q: Queryable, deviceId: string, lq: ListQuery) {
  const params: unknown[] = [deviceId];
  const where = [`r.status = 'active'`];
  if (lq.category) { params.push(lq.category); where.push(`r.category = $${params.length}`); }
  if (lq.q) {
    params.push(`%${escapeLike(lq.q)}%`);
    const p = `$${params.length}`;
    where.push(`(r.name ILIKE ${p} OR r.description ILIKE ${p} OR array_to_string(r.tags, ' ') ILIKE ${p})`);
  }
  const hide = lq.compat === 'hide_none' && lq.caps !== null;
  if (hide) { params.push([...lq.caps!]); where.push(`r.trigger_caps <@ $${params.length}::text[]`); }
  const order = lq.sort === 'new'
    ? 'r.created_at DESC, r.id DESC'
    : 'r.import_count DESC, r.featured_rank ASC NULLS LAST, r.created_at DESC, r.id DESC';
  params.push(lq.limit + 1, lq.offset);
  const rows = (await q.query<CardRow>(
    `SELECT ${CARD_COLUMNS} FROM routines r WHERE ${where.join(' AND ')}
      ORDER BY ${order} LIMIT $${params.length - 1} OFFSET $${params.length}`, params)).rows;
  const hasMore = rows.length > lq.limit;
  // trigger는 SQL에서 걸렀고, '실행 가능한 동작 0개'는 여기서 거른다(동작은 최대 5개)
  const items = rows.slice(0, lq.limit).map((r) => toCard(r, lq.caps)).filter((c) => !hide || c.compat !== 'none');
  return { items, nextOffset: hasMore ? lq.offset + lq.limit : null };
}
