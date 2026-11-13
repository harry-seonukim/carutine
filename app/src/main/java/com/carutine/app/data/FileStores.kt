package com.carutine.app.data

import com.carutine.engine.CooldownStore
import com.carutine.engine.Place
import com.carutine.engine.PlaceStore
import com.carutine.engine.RoutineStore
import com.carutine.engine.RunLog
import com.carutine.engine.RunLogSink
import com.carutine.engine.StoredRoutine
import com.carutine.engine.UndoSnapshot
import com.carutine.engine.UndoSnapshotStore
import com.carutine.model.PlaceKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put
import java.io.File
import java.time.Instant

/**
 * 앱 내부 파일(context.filesDir 아래)에 JSON으로 저장하는 엔진 저장소들(Ports.kt 구현).
 * Room/KSP 없이 빌드 위험을 줄이려고 파일 하나 = 표 하나로 둔다. 데이터가 수십 건이라 통째로 읽고 쓴다.
 * 쓰기는 임시 파일에 쓴 뒤 rename해서 중간에 앱이 죽어도 이전 파일이 남게 한다.
 * Android 클래스를 쓰지 않아 JVM 단위 테스트에서 임시 폴더로 검증할 수 있다.
 */
internal class JsonFile(private val file: File) {
    private val json = Json { prettyPrint = false }

    suspend fun read(): JsonElement? = withContext(Dispatchers.IO) {
        try {
            if (!file.isFile) null else json.parseToJsonElement(file.readText(Charsets.UTF_8))
        } catch (e: Exception) {
            null   // 깨진 파일은 빈 값으로 취급(다음 저장 때 덮어씀)
        }
    }

    suspend fun write(e: JsonElement?): Unit = withContext(Dispatchers.IO) {
        file.parentFile?.mkdirs()
        if (e == null) { file.delete(); return@withContext }
        val tmp = File(file.parentFile, file.name + ".tmp")
        tmp.writeText(e.toString(), Charsets.UTF_8)
        if (!tmp.renameTo(file)) {
            file.delete()
            tmp.renameTo(file)
        }
        Unit
    }
}

class FileRoutineStore(dir: File) : RoutineStore {
    private val file = JsonFile(File(dir, "routines.json"))
    private val lock = Mutex()
    private var cache: List<StoredRoutine>? = null
    private val _flow = MutableStateFlow<List<StoredRoutine>>(emptyList())

    /** 화면용(priority 오름차순). [load]나 변경 후 갱신된다. */
    val flow: StateFlow<List<StoredRoutine>> = _flow.asStateFlow()

    private suspend fun loadLocked(): List<StoredRoutine> = cache ?: run {
        val arr = file.read() as? JsonArray
        val list = arr?.mapNotNull { StoreCodec.routineFromJson(it) } ?: emptyList()
        list.sortedBy { it.priority }.also { cache = it; _flow.value = it }
    }

    private suspend fun saveLocked(list: List<StoredRoutine>) {
        // priority를 0..n-1로 다시 매겨 목록 순서와 항상 같게 둔다.
        val normalized = list.sortedBy { it.priority }.mapIndexed { i, r -> r.copy(priority = i) }
        file.write(JsonArray(normalized.map { StoreCodec.routineToJson(it) }))
        cache = normalized
        _flow.value = normalized
    }

    suspend fun load(): List<StoredRoutine> = lock.withLock { loadLocked() }

    override suspend fun all(): List<StoredRoutine> = lock.withLock { loadLocked() }

    /** 같은 localId가 있으면 바꾸고, 없으면 그대로 추가한다(priority는 호출자가 정함). */
    suspend fun upsert(r: StoredRoutine) = lock.withLock {
        val cur = loadLocked()
        val next = if (cur.any { it.localId == r.localId }) cur.map { if (it.localId == r.localId) r else it } else cur + r
        saveLocked(next)
    }

    suspend fun delete(localId: String) = lock.withLock {
        saveLocked(loadLocked().filterNot { it.localId == localId })
    }

    suspend fun setEnabled(localId: String, enabled: Boolean) = lock.withLock {
        saveLocked(loadLocked().map { if (it.localId == localId) it.copy(enabled = enabled) else it })
    }

    /** 목록에서 한 칸 위(-1)/아래(+1)로. 위일수록 충돌 시 우선(UX 7장). */
    suspend fun move(localId: String, delta: Int) = lock.withLock {
        val list = loadLocked().toMutableList()
        val i = list.indexOfFirst { it.localId == localId }
        val j = i + delta
        if (i < 0 || j !in list.indices) return@withLock
        val tmp = list[i]; list[i] = list[j]; list[j] = tmp
        saveLocked(list.mapIndexed { idx, r -> r.copy(priority = idx) })
    }

    suspend fun nextPriority(): Int = lock.withLock { (loadLocked().maxOfOrNull { it.priority } ?: -1) + 1 }
}

/** 장소 좌표는 이 파일에만 있고 서버로 보내지 않는다(계약에는 장소 키만). */
class FilePlaceStore(dir: File) : PlaceStore {
    private val file = JsonFile(File(dir, "places.json"))
    private val lock = Mutex()
    private var cache: Map<PlaceKey, Place>? = null
    private val _flow = MutableStateFlow<Map<PlaceKey, Place>>(emptyMap())
    val flow: StateFlow<Map<PlaceKey, Place>> = _flow.asStateFlow()

    private suspend fun loadLocked(): Map<PlaceKey, Place> = cache ?: run {
        val arr = file.read() as? JsonArray
        val map = arr?.mapNotNull { StoreCodec.placeFromJson(it) }?.associateBy { it.key } ?: emptyMap()
        map.also { cache = it; _flow.value = it }
    }

    private suspend fun saveLocked(map: Map<PlaceKey, Place>) {
        file.write(JsonArray(map.values.map { StoreCodec.placeToJson(it) }))
        cache = map
        _flow.value = map
    }

    override suspend fun all(): Map<PlaceKey, Place> = lock.withLock { loadLocked() }

    suspend fun put(places: Collection<Place>) = lock.withLock {
        saveLocked(loadLocked() + places.associateBy { it.key })
    }

    suspend fun remove(key: PlaceKey) = lock.withLock { saveLocked(loadLocked() - key) }
}

class FileUndoStore(dir: File) : UndoSnapshotStore {
    private val file = JsonFile(File(dir, "undo.json"))
    private val lock = Mutex()

    override suspend fun load(): UndoSnapshot? = lock.withLock { file.read()?.let { StoreCodec.undoFromJson(it) } }
    override suspend fun save(snapshot: UndoSnapshot) = lock.withLock { file.write(StoreCodec.undoToJson(snapshot)) }
    override suspend fun clear() = lock.withLock { file.write(null) }
}

class FileCooldownStore(dir: File) : CooldownStore {
    private val file = JsonFile(File(dir, "cooldowns.json"))
    private val lock = Mutex()
    private var cache: MutableMap<String, Instant>? = null

    private suspend fun loadLocked(): MutableMap<String, Instant> = cache ?: run {
        val o = file.read() as? JsonObject
        val m = mutableMapOf<String, Instant>()
        o?.forEach { (k, v) -> (v as? JsonPrimitive)?.longOrNull?.let { m[k] = Instant.ofEpochMilli(it) } }
        m.also { cache = it }
    }

    override suspend fun lastAutoRun(routineId: String): Instant? = lock.withLock { loadLocked()[routineId] }

    override suspend fun markAutoRun(routineId: String, at: Instant) = lock.withLock {
        val m = loadLocked()
        m[routineId] = at
        file.write(buildJsonObject { m.forEach { (k, v) -> put(k, v.toEpochMilli()) } })
    }

    /** 시뮬레이터 '재실행 방지 초기화'(시연 반복용). */
    suspend fun clearAll() = lock.withLock {
        cache = mutableMapOf()
        file.write(null)
    }
}

/** 실행 기록. 최근 [max]건만 남긴다(요약 형태, 좌표 없음). */
class FileRunLog(dir: File, private val max: Int = 200) : RunLogSink {
    private val file = JsonFile(File(dir, "runlog.json"))
    private val lock = Mutex()

    override suspend fun record(log: RunLog) = lock.withLock {
        val cur = (file.read() as? JsonArray)?.toList() ?: emptyList()
        val next = (cur + StoreCodec.runLogToJson(log)).takeLast(max)
        file.write(buildJsonArray { next.forEach { add(it) } })
    }

    suspend fun count(): Int = lock.withLock { (file.read() as? JsonArray)?.size ?: 0 }
}
