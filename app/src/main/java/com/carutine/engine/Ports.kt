package com.carutine.engine

import com.carutine.model.PlaceKey
import com.carutine.model.RoutineSpec
import java.time.Instant

/** routines 테이블 한 행(작업 문서 9장). priority가 작을수록 내 루틴 목록 위 = 충돌 시 우선. */
data class StoredRoutine(
    val localId: String,
    val spec: RoutineSpec,
    val enabled: Boolean,
    val priority: Int,
    val source: RoutineSource,
    val remoteId: String? = null,
)

enum class RoutineSource(val wire: String) { TEMPLATE("template"), LOCAL("local"), IMPORTED("imported") }

/** places 테이블 한 행. 좌표는 기기에만 저장하고 서버로 보내지 않는다. */
data class Place(val key: PlaceKey, val label: String, val lat: Double, val lng: Double, val radiusM: Double = 200.0)

interface RoutineStore {
    /** priority 오름차순. */
    suspend fun all(): List<StoredRoutine>
}

interface PlaceStore {
    suspend fun all(): Map<PlaceKey, Place>
}

/** undo_snapshot 테이블(1건). 앱이 재시작돼도 10분 안이면 되돌릴 수 있게 저장소로 뺀다. */
interface UndoSnapshotStore {
    suspend fun load(): UndoSnapshot?
    suspend fun save(snapshot: UndoSnapshot)
    suspend fun clear()
}

interface RunLogSink {
    suspend fun record(log: RunLog)
}

/** 마지막 자동 실행 시각(재실행 방지). 프로세스가 다시 떠도 유지돼야 하므로 저장소로 뺀다. */
interface CooldownStore {
    suspend fun lastAutoRun(routineId: String): Instant?
    suspend fun markAutoRun(routineId: String, at: Instant)
}

data class UndoSnapshot(
    val at: Instant,
    val routineIds: List<String>,
    val entries: List<Entry>,
) {
    /** before: 실행 직전 값, applied: 루틴이 설정한 동작(되돌리기 때 '사용자가 바꿨는지' 비교 기준). */
    data class Entry(val key: DeviceKey, val before: DeviceValue, val applied: com.carutine.model.Action)
}

data class RunLog(
    val at: Instant,
    val routineIds: List<String>,
    val source: RunSource,
    val trigger: TriggerEvent?,
    val outcomes: List<ActionOutcome>,
)

// ---- 메모리 구현(단위 테스트·Mock 데모 모드용). 앱에서는 Room 구현으로 바꾼다. ----

class InMemoryRoutineStore(initial: List<StoredRoutine> = emptyList()) : RoutineStore {
    var routines: List<StoredRoutine> = initial
    override suspend fun all() = routines.sortedBy { it.priority }
}

class InMemoryPlaceStore(initial: Map<PlaceKey, Place> = emptyMap()) : PlaceStore {
    var places: Map<PlaceKey, Place> = initial
    override suspend fun all() = places
}

class InMemoryUndoStore : UndoSnapshotStore {
    var value: UndoSnapshot? = null
    override suspend fun load() = value
    override suspend fun save(snapshot: UndoSnapshot) { value = snapshot }
    override suspend fun clear() { value = null }
}

class InMemoryRunLog : RunLogSink {
    val logs = mutableListOf<RunLog>()
    override suspend fun record(log: RunLog) { logs += log }
}

class InMemoryCooldownStore : CooldownStore {
    private val map = mutableMapOf<String, Instant>()
    override suspend fun lastAutoRun(routineId: String) = map[routineId]
    override suspend fun markAutoRun(routineId: String, at: Instant) { map[routineId] = at }
}
