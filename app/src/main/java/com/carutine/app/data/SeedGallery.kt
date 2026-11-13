package com.carutine.app.data

import com.carutine.model.Cap
import com.carutine.model.Category
import com.carutine.model.CompatChecker
import com.carutine.model.CompatLevel
import com.carutine.model.RoutineJson
import com.carutine.model.Condition
import com.carutine.engine.ConditionEvaluator
import java.time.ZonedDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.intOrNull

/**
 * 오프라인 갤러리: assets/seed_routines.json(공식 시드 20개, 수정 금지)을 읽어 선반을 만든다.
 * 서버의 선반 규칙(openapi.yaml /v1/gallery/shelves)을 따르되, 인기 선반은 수치가 없으므로 만들지 않는다.
 */
object SeedGallery {

    fun parse(text: String): List<GalleryItem> {
        val root = Json.parseToJsonElement(text) as? JsonObject ?: return emptyList()
        val arr = root["routines"] as? JsonArray ?: return emptyList()
        return arr.mapNotNull { el ->
            val o = el as? JsonObject ?: return@mapNotNull null
            val spec = when (val r = RoutineJson.parse(o["spec"] ?: return@mapNotNull null)) {
                is RoutineJson.Result.Ok -> r.spec
                is RoutineJson.Result.Error -> return@mapNotNull null
            }
            GalleryItem(
                id = spec.id,
                name = spec.name,
                description = spec.description,
                category = spec.category,
                tags = spec.tags,
                triggerType = spec.trigger?.type,
                actionTypes = spec.actions.map { it.action.type },
                isOfficial = (o["isOfficial"] as? JsonPrimitive)?.booleanOrNull ?: true,
                isMine = false,
                spec = spec,
                serverCompat = null,
                featuredRank = (o["featuredRank"] as? JsonPrimitive)?.intOrNull,
            )
        }
    }

    fun season(month: Int): String = when (month) {
        12, 1, 2 -> "winter"
        3, 4, 5 -> "spring"
        6, 7, 8 -> "summer"
        else -> "autumn"
    }

    /** 서버와 같은 규칙: featuredRank 오름차순(null은 뒤), 그다음 시드 순서. */
    private fun officialOrder(items: List<GalleryItem>) =
        items.withIndex().sortedWith(compareBy({ it.value.featuredRank ?: Int.MAX_VALUE }, { it.index })).map { it.value }

    /**
     * '지금 이 시간'에 맞는지. 점수 0이면 맞지 않음(선반에서 뺀다).
     * - 반대 계절 태그면 0점
     * - time_window 조건이 있으면: 지금 시각이 그 안이면 2점, 아니면 0점(다른 규칙과 상관없이 제외)
     * - 시간 조건이 없으면: 카테고리가 시간대에 맞으면(출퇴근 6~10시·17~21시, 야간 19~5시) 1점,
     *   계절 태그('겨울'/'여름')가 지금 계절과 맞으면 1점
     */
    fun nowScore(i: GalleryItem, now: ZonedDateTime): Int {
        val spec = i.spec ?: return 0
        val season = season(now.monthValue)
        // 반대 계절 루틴은 시간대가 맞아도 뺀다(가을·겨울엔 '여름', 봄·여름엔 '겨울' 태그)
        if ((season == "winter" || season == "autumn") && "여름" in i.tags) return 0
        if ((season == "summer" || season == "spring") && "겨울" in i.tags) return 0
        val window = spec.conditions.filterIsInstance<Condition.TimeWindow>().firstOrNull()
        if (window != null) return if (ConditionEvaluator.inWindow(window, now)) 2 else 0
        val hour = now.hour
        var s = 0
        if ((hour in 6..9 || hour in 17..20) && i.category == Category.COMMUTE) s++
        if ((hour >= 19 || hour < 5) && i.category == Category.NIGHT) s++
        if (season == "winter" && "겨울" in i.tags) s++
        if (season == "summer" && "여름" in i.tags) s++
        return s
    }

    /**
     * 선반: '지금 이 시간'(맞는 것만, 없으면 선반 숨김) / '공식 추천'(featuredRank 순) / '더 많은 공식 루틴'(추천에 없는 나머지).
     * 같은 루틴이 '공식 추천'과 '더 많은' 양쪽에 중복되지 않게 한다.
     */
    fun shelves(items: List<GalleryItem>, caps: Set<Cap>, now: ZonedDateTime, hideNone: Boolean): List<Shelf> {
        val visible = if (!hideNone) items else items.filter { it.spec == null || CompatChecker.check(it.spec, caps).level != CompatLevel.NONE }
        val official = officialOrder(visible.filter { it.isOfficial })
        val nowItems = official.map { it to nowScore(it, now) }.filter { it.second > 0 }
            .sortedByDescending { it.second }.map { it.first }.take(10)
        val featured = official.filter { it.featuredRank != null }
        val rest = official.filter { it.featuredRank == null }
        return buildList {
            if (nowItems.isNotEmpty()) add(Shelf("now", "지금 이 시간", nowItems))
            add(Shelf("official", "공식 추천", featured))
            if (rest.isNotEmpty()) add(Shelf("more", "더 많은 공식 루틴", rest))
        }
    }
}
