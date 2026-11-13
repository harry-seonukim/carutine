package com.carutine.app.data

import com.carutine.model.Cap
import com.carutine.model.Category
import com.carutine.model.CompatChecker
import com.carutine.model.CompatLevel
import com.carutine.model.RoutineJson
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

    fun shelves(items: List<GalleryItem>, caps: Set<Cap>, month: Int, hour: Int, hideNone: Boolean): List<Shelf> {
        val visible = if (!hideNone) items else items.filter { it.spec == null || CompatChecker.check(it.spec, caps).level != CompatLevel.NONE }
        val official = officialOrder(visible.filter { it.isOfficial })
        val season = season(month)
        fun score(i: GalleryItem): Int {
            var s = 0
            if (season == "winter" && "겨울" in i.tags) s++
            if (season == "summer" && "여름" in i.tags) s++
            if ((hour in 6..9 || hour in 17..20) && i.category == Category.COMMUTE) s++
            if ((hour >= 19 || hour < 5) && i.category == Category.NIGHT) s++
            return s
        }
        val scored = official.map { it to score(it) }
        val now = if (scored.any { it.second > 0 }) scored.filter { it.second > 0 }.sortedByDescending { it.second }.map { it.first }
        else official
        return listOf(
            Shelf("now", "지금 이 시간", now.take(10)),
            Shelf("official", "공식 추천", official.filter { it.featuredRank != null }.take(10)),
            Shelf("all", "공식 루틴 전체", official),
        )
    }
}
