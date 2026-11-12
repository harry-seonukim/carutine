package com.carutine.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * 앱에 복사한 시드(app/src/main/assets/seed_routines.json)가 계약 v0.1대로 읽히는지 확인한다.
 * 시드 원본은 backend/seed/seed_routines.json이며, 앱 쪽 파일은 복사본이라 고치지 않는다.
 */
class SeedRoutinesTest {

    private fun seedFile(): File {
        // Gradle 단위 테스트는 app/ 모듈 폴더에서, 로컬 스크립트는 저장소 루트에서 실행된다.
        val candidates = listOf(
            System.getProperty("carutine.seed")?.let(::File),
            File("src/main/assets/seed_routines.json"),
            File("app/src/main/assets/seed_routines.json"),
        )
        return candidates.firstOrNull { it?.isFile == true }
            ?: error("seed_routines.json을 찾지 못함: ${candidates.map { it?.absolutePath }}")
    }

    @Test fun `시드 20개가 모두 계약대로 읽히고 검증을 통과한다`() {
        val (routines, failed) = RoutineJson.parseList(seedFile().readText())
        assertEquals("읽기 실패: $failed", emptyList<Any>(), failed)
        assertEquals(20, routines.size)
        routines.forEach { spec ->
            assertEquals("${spec.name}: ${RoutineValidator.validate(spec)}", emptyList<ValidationError>(), RoutineValidator.validate(spec))
            // 다시 써도 같은 값(공유 업로드 때 계약 모양이 깨지지 않음)
            assertEquals(spec, (RoutineJson.parse(RoutineJson.encode(spec)) as RoutineJson.Result.Ok).spec)
        }
    }

    @Test fun `MVP 화면에 없는 장소 키를 쓰는 시드가 없다`() {
        val (routines, _) = RoutineJson.parseList(seedFile().readText())
        val custom = routines.filter { s -> s.places().any { !it.exposedInMvp } }.map { it.name }
        assertTrue("custom 장소를 쓰는 시드: $custom", custom.isEmpty())
    }

    @Test fun `모든 기능이 있는 차에서는 시드가 모두 완전 호환이다`() {
        val (routines, _) = RoutineJson.parseList(seedFile().readText())
        val notFull = routines.filter { CompatChecker.check(it, Cap.entries.toSet()).level != CompatLevel.FULL }.map { it.name }
        assertTrue("완전 호환이 아닌 시드: $notFull", notFull.isEmpty())
    }
}
