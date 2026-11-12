package com.carutine.engine

import com.carutine.model.Cap
import com.carutine.model.CompatChecker
import com.carutine.model.CompatLevel
import com.carutine.model.PlaceKey
import com.carutine.model.RoutineSpec

/**
 * 갤러리 → 내 루틴 가져오기(S10, F2). 화면과 무관한 규칙만 담는다.
 *
 * - 자동 실행 답(스위치)을 routines.enabled에 저장한다(PM 결정 D2). 수동 루틴은 묻지 않고 enabled=true.
 * - 내 루틴 목록 맨 아래(priority = 최대+1) → 충돌 시 우선순위가 가장 낮다(UX 7장).
 * - MVP 화면은 '집'·'회사'만 노출(Q3). custom 키를 쓰는 루틴은 가져오지 않는다.
 * - 같은 이름이 있으면 ' 2'를 붙이는 임시 규칙(UX 9장, 11/20 2차에서 교체).
 */
object ImportPlanner {

    sealed interface Result {
        data class Ok(val routine: StoredRoutine, val newPlaces: List<Place>) : Result
        data class Incompatible(val level: CompatLevel) : Result
        data class PlaceNotSupportedInMvp(val keys: Set<PlaceKey>) : Result
        data class PlaceMissing(val keys: Set<PlaceKey>) : Result
    }

    /** S10 시트 표시 조건: 장소 조건이 있으면 장소 섹션, 사건 조건이 있으면 자동 실행 섹션, 수동 루틴은 시트 없이 저장. */
    data class SheetSections(val placeKeys: Set<PlaceKey>, val askAutoRun: Boolean) {
        val needsSheet: Boolean get() = placeKeys.isNotEmpty() || askAutoRun
    }

    fun sections(spec: RoutineSpec): SheetSections = SheetSections(spec.places(), askAutoRun = spec.trigger != null)

    /**
     * @param registered 이미 등록된 내 장소
     * @param assigned S10에서 '현재 위치로 지정'한 장소(가져오기 버튼을 누를 때 함께 저장)
     * @param autoRun S10 스위치 값. 수동 루틴이면 무시한다.
     */
    fun plan(
        spec: RoutineSpec,
        remoteId: String?,
        caps: Set<Cap>,
        existing: List<StoredRoutine>,
        registered: Map<PlaceKey, Place>,
        assigned: Map<PlaceKey, Place>,
        autoRun: Boolean,
        newLocalId: () -> String,
    ): Result {
        val compat = CompatChecker.check(spec, caps)
        if (compat.level == CompatLevel.NONE) return Result.Incompatible(compat.level)

        val keys = spec.places()
        val custom = keys.filterNot { it.exposedInMvp }.toSet()
        if (custom.isNotEmpty()) return Result.PlaceNotSupportedInMvp(custom)
        val missing = keys.filter { it !in registered && it !in assigned }.toSet()
        if (missing.isNotEmpty()) return Result.PlaceMissing(missing)

        val names = existing.map { it.spec.name.trim() }.toSet()
        var name = spec.name.trim()
        if (name in names) {
            var n = 2
            while ("$name $n" in names) n++
            name = "$name $n"
        }

        val routine = StoredRoutine(
            localId = newLocalId(),
            spec = spec.copy(name = name),
            enabled = if (spec.trigger == null) true else autoRun,
            priority = (existing.maxOfOrNull { it.priority } ?: -1) + 1,
            source = RoutineSource.IMPORTED,
            remoteId = remoteId ?: spec.id,
        )
        return Result.Ok(routine, assigned.filterKeys { it in keys }.values.toList())
    }
}
