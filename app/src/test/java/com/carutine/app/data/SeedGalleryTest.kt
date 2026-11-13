package com.carutine.app.data

import com.carutine.model.Cap
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.time.ZoneId
import java.time.ZonedDateTime

class SeedGalleryTest {
    private val items = SeedGallery.parse(
        listOf(File("src/main/assets/seed_routines.json"), File("app/src/main/assets/seed_routines.json")).first { it.isFile }.readText()
    )
    private val caps = Cap.entries.toSet()
    private fun at(month: Int, day: Int, h: Int, m: Int) = ZonedDateTime.of(2026, month, day, h, m, 0, 0, ZoneId.of("Asia/Seoul"))
    private fun names(shelf: Shelf?) = shelf?.items?.map { it.name } ?: emptyList()

    @Test fun `오전 6시 36분에는 19시~6시 시간대 루틴이 지금 이 시간에 나오지 않는다`() {
        val shelves = SeedGallery.shelves(items, caps, at(11, 13, 6, 36), hideNone = false)
        val now = shelves.firstOrNull { it.id == "now" }
        assertFalse("비 오는 밤" in names(now))
        // 지금 이 시간에 나온 루틴은 모두 점수가 있어야 한다
        now?.items?.forEach { assertTrue(it.name, SeedGallery.nowScore(it, at(11, 13, 6, 36)) > 0) }
    }

    @Test fun `밤 9시에는 비 오는 밤이 지금 이 시간에 나온다`() {
        val now = SeedGallery.shelves(items, caps, at(11, 13, 21, 0), hideNone = false).firstOrNull { it.id == "now" }
        assertTrue("비 오는 밤" in names(now))
    }

    @Test fun `공식 추천과 더 많은 공식 루틴은 겹치지 않고 합치면 공식 시드 전체다`() {
        val shelves = SeedGallery.shelves(items, caps, at(11, 13, 12, 0), hideNone = false)
        val featured = names(shelves.first { it.id == "official" })
        val more = names(shelves.firstOrNull { it.id == "more" })
        assertTrue(featured.intersect(more.toSet()).isEmpty())
        assertEquals(items.filter { it.isOfficial }.size, featured.size + more.size)
    }

    @Test fun `맞는 루틴이 없으면 지금 이 시간 선반을 숨긴다`() {
        val noTime = items.filter { it.spec?.conditions?.none { c -> c.type == "time_window" } == true && it.tags.none { t -> t == "겨울" || t == "여름" } }
            .map { it.copy(category = com.carutine.model.Category.ETC) }
        val shelves = SeedGallery.shelves(noTime, caps, at(4, 1, 12, 0), hideNone = false)
        assertFalse(shelves.any { it.id == "now" })
    }
}
