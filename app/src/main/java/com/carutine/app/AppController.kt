package com.carutine.app

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.carutine.app.data.ApiException
import com.carutine.app.data.FileCooldownStore
import com.carutine.app.data.FilePlaceStore
import com.carutine.app.data.FileRoutineStore
import com.carutine.app.data.FileRunLog
import com.carutine.app.data.FileUndoStore
import com.carutine.app.data.GalleryApi
import com.carutine.app.data.GalleryItem
import com.carutine.app.data.SeedGallery
import com.carutine.app.data.Shelf
import com.carutine.engine.Announcer
import com.carutine.engine.DriveState
import com.carutine.engine.ExecutionReport
import com.carutine.engine.ImportPlanner
import com.carutine.engine.NoticeKind
import com.carutine.engine.OutcomeStatus
import com.carutine.engine.Place
import com.carutine.engine.ProposalOutcome
import com.carutine.engine.RoutineEngine
import com.carutine.engine.RoutineSource
import com.carutine.engine.RunSource
import com.carutine.engine.StoredRoutine
import com.carutine.engine.VehicleEvent
import com.carutine.engine.VehicleGateway
import com.carutine.model.AirMode
import com.carutine.model.AmbientBrightness
import com.carutine.model.AmbientColor
import com.carutine.model.Cap
import com.carutine.model.EnergySource
import com.carutine.model.PlaceKey
import com.carutine.model.RoutineJson
import com.carutine.model.RoutineSpec
import com.carutine.model.Seat
import com.carutine.vehicle.fake.FakeVehicleGateway
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import java.io.File
import java.time.LocalDateTime
import java.util.UUID

/** 화면 이동. 상태는 컨트롤러가 들고 있어 회전·창 크기 변경에도 유지된다. */
sealed interface Screen {
    data object Home : Screen
    data object Gallery : Screen
    data class Detail(val item: GalleryItem) : Screen
    data class Editor(val localId: String?) : Screen
    data object Share : Screen
    data object Settings : Screen
}

enum class BannerTone { INFO, SUCCESS, WARN, FAIL }

/** S12 실행 알림 배너. */
data class Banner(
    val id: Long,
    val text: String,
    val sub: String? = null,
    val tone: BannerTone = BannerTone.INFO,
    val showUndo: Boolean = false,
    val durationMs: Long = 4000,
)

data class ProposalUi(val routineName: String, val durationMs: Long, val startedAt: Long)

/** S10 가져오기 시트 대상. */
data class ImportTarget(val spec: RoutineSpec, val remoteId: String?)

enum class SimPosition(val label: String) { HOME("집"), WORK("회사"), AWAY("이동 중") }

/** 시뮬레이터 차량 사양(호환 배지 시연용). 바꾸면 엔진을 새로 만든다(엔진이 capability를 캐시하므로). */
enum class SimProfile(val label: String, val caps: Set<Cap>) {
    FULL_EV("풀옵션 EV", Cap.entries.toSet() - Cap.FUEL_LEVEL),
    BASIC_EV("기본 사양 EV (열선·앰비언트 없음)", FakeVehicleGateway.BASIC_EV),
    ICE("내연기관 (옵션 포함)", Cap.entries.toSet() - Cap.EV_BATTERY),
}

data class SimSnapshot(
    val hvacTemp: Double = 22.0,
    val fan: Int = 2,
    val air: AirMode = AirMode.FRESH,
    val seatDriver: Int = 0,
    val seatPassenger: Int = 0,
    val steering: Boolean = false,
    val mirror: Boolean = false,
    val ambientOn: Boolean = false,
    val ambientColor: AmbientColor? = null,
    val ambientBrightness: AmbientBrightness? = null,
    val cabinTemp: Double = 20.0,
    val waypoints: Int = 0,
)

class AppController(context: Context) {
    private val app = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val dir = File(app.filesDir, "carutine")

    val routineStore = FileRoutineStore(dir)
    val placeStore = FilePlaceStore(dir)
    private val undoStore = FileUndoStore(dir)
    private val cooldownStore = FileCooldownStore(dir)
    private val runLog = FileRunLog(dir)
    val api = GalleryApi(BuildConfig.API_BASE_URL, dir, BuildConfig.VERSION_NAME)
    private val speaker = Speaker(app)
    val listener = Listener(app)

    // ---------------- 화면 상태 ----------------
    val backStack = mutableStateListOf<Screen>(Screen.Home)
    val screen: Screen get() = backStack.last()

    var mode by mutableStateOf(VehicleMode.CONNECTING); private set
    var caps by mutableStateOf<Set<Cap>>(emptySet()); private set
    var driveState by mutableStateOf(DriveState.UNKNOWN); private set
    var routines by mutableStateOf<List<StoredRoutine>>(emptyList()); private set
    var places by mutableStateOf<Map<PlaceKey, Place>>(emptyMap()); private set
    var canUndo by mutableStateOf(false); private set
    var banner by mutableStateOf<Banner?>(null); private set
    var proposal by mutableStateOf<ProposalUi?>(null); private set
    var importTarget by mutableStateOf<ImportTarget?>(null)
    var voiceStatus by mutableStateOf<String?>(null)

    var shelves by mutableStateOf<List<Shelf>>(emptyList()); private set
    var galleryOnline by mutableStateOf(false); private set
    var galleryNote by mutableStateOf<String?>(null); private set
    var galleryLoading by mutableStateOf(false); private set
    var hideIncompatible by mutableStateOf(true); private set

    // 시뮬레이터
    var sim by mutableStateOf(SimSnapshot()); private set
    var simPosition by mutableStateOf(SimPosition.WORK); private set
    var simBattery by mutableStateOf(64); private set
    var simFuel by mutableStateOf(64); private set
    var simWiper by mutableStateOf(false); private set
    var simProfile by mutableStateOf(SimProfile.FULL_EV); private set
    val simEvents = mutableStateListOf<String>()

    /** 실제 차량 모드에서 장소 지정에 쓰는 마지막 위치(기기 메모리에만). */
    private var lastLocation: VehicleEvent.Location? = null

    private var gateway: VehicleGateway? = null
    private var shared: SharedGateway? = null
    private var fake: FakeVehicleGateway? = null
    private lateinit var engine: RoutineEngine
    private var engineJob: Job? = null
    private var observeJob: Job? = null
    private var proposalJob: Job? = null
    private var bannerSeq = 0L
    private var seeds: List<GalleryItem> = emptyList()

    val engineReady: Boolean get() = ::engine.isInitialized

    init {
        scope.launch { routineStore.flow.collect { routines = it } }
        scope.launch { placeStore.flow.collect { places = it } }
        scope.launch {
            routineStore.load()
            placeStore.all()
            seeds = runCatching {
                app.assets.open("seed_routines.json").bufferedReader(Charsets.UTF_8).use { SeedGallery.parse(it.readText()) }
            }.getOrDefault(emptyList())
            val conn = GatewayFactory.connect(app)
            gateway = conn.gateway
            fake = conn.fake
            fake?.caps = simProfile.caps
            startEngine()
            mode = conn.mode
            if (conn.mode == VehicleMode.SIMULATOR) simBaseline(initial = true)
            refreshUndo()
            loadGallery()
        }
        // 되돌리기 10분 창, 시뮬레이터 상태 표시를 주기적으로 갱신
        scope.launch {
            while (isActive) {
                delay(1000)
                refreshSim()
                if (engineReady) canUndo = engine.canUndo()
            }
        }
    }

    private suspend fun startEngine() {
        val raw = gateway ?: return
        engineJob?.cancel()
        observeJob?.cancel()
        shared?.stop()
        // observe()는 한 번만 수집해 엔진과 화면(주행 상태·위치)이 나눠 쓴다
        val gw = SharedGateway(raw, scope).also { shared = it }
        engine = RoutineEngine(gw, routineStore, placeStore, undoStore, cooldownStore, runLog)
        observeJob = scope.launch {
            gw.observe().collect { e ->
                when (e) {
                    is VehicleEvent.Drive -> onDrive(e.state)
                    is VehicleEvent.Location -> lastLocation = e
                    else -> Unit
                }
            }
        }
        engineJob = engine.start(scope) { onReport(it) }
        caps = engine.capabilities()
        delay(50)   // 구독이 붙은 뒤에 기준값을 보낸다
    }

    /**
     * 실제 차량 모드에서 위치 권한(dangerous)을 허용받은 뒤 부른다. SDK는 연결 시점의 권한으로 capability를 정하므로
     * close() → connect()로 다시 연결하고 엔진을 새로 만든다(location cap 반영).
     */
    fun reconnectAfterPermission() {
        val gw = gateway as? com.carutine.vehicle.pleos.PleosVehicleGateway ?: return
        scope.launch {
            val ok = try { gw.close(); gw.connect() } catch (t: Throwable) { false }
            startEngine()
            if (!ok) showBanner("차량에 다시 연결하지 못했어요", tone = BannerTone.WARN)
            else if (Cap.LOCATION in caps) showBanner("위치 권한이 반영됐어요", tone = BannerTone.SUCCESS, durationMs = 2500)
        }
    }

    fun close() {
        listener.cancel()
        speaker.shutdown()
        shared?.stop()
        (gateway as? AutoCloseable)?.let { runCatching { it.close() } }
        scope.cancel()
    }

    // ------------------------------------------------------------------ 내비게이션

    val restrictsUi: Boolean get() = driveState.restrictsUi

    fun navigate(s: Screen) {
        if (restrictsUi && s != Screen.Home) {
            showBanner("주행 중에는 실행만 할 수 있어요", tone = BannerTone.WARN, durationMs = 3000)
            return
        }
        backStack.add(s)
    }

    fun back(): Boolean {
        if (importTarget != null) { importTarget = null; return true }
        if (backStack.size <= 1) return false
        backStack.removeAt(backStack.lastIndex)
        return true
    }

    private fun onDrive(state: DriveState) {
        val was = driveState.restrictsUi
        driveState = state
        if (state.restrictsUi && !was) {
            // 주행 매트릭스: 편집·갤러리 탐색 대신 실행 화면만
            importTarget = null
            if (backStack.size > 1) {
                while (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
            }
        }
    }

    // ------------------------------------------------------------------ 실행·알림 (S12)

    fun showBanner(text: String, sub: String? = null, tone: BannerTone = BannerTone.INFO, showUndo: Boolean = false, durationMs: Long = 4000) {
        banner = Banner(++bannerSeq, text, sub, tone, showUndo, durationMs)
    }

    fun dismissBanner(id: Long) { if (banner?.id == id) banner = null }

    private fun onReport(r: ExecutionReport) {
        val applied = r.outcomes.count { it.status == OutcomeStatus.APPLIED }
        val viaFallback = r.outcomes.count { it.status == OutcomeStatus.APPLIED_VIA_FALLBACK }
        val excluded = r.outcomes.count { it.status == OutcomeStatus.SKIPPED_UNSUPPORTED || it.status == OutcomeStatus.SKIPPED_CONFLICT }
        val failed = r.outcomes.count { it.status == OutcomeStatus.FAILED }
        val sub = buildList {
            add(when (r.source) { RunSource.AUTO -> "자동 실행"; RunSource.BUTTON -> "버튼 실행"; RunSource.VOICE -> "음성 실행" })
            if (applied > 0) add("실행 $applied")
            if (viaFallback > 0) add("대체 $viaFallback")
            if (excluded > 0) add("제외 $excluded")
            if (failed > 0) add("실패 $failed")
        }.joinToString(" · ")
        val tone = when (r.notice.kind) {
            NoticeKind.R3 -> BannerTone.FAIL
            NoticeKind.R2 -> BannerTone.WARN
            else -> BannerTone.SUCCESS
        }
        val duration = when (r.notice.kind) { NoticeKind.R2 -> 6000L; else -> 4000L }
        showBanner(r.notice.text, sub, tone, showUndo = r.undoable, durationMs = duration)
        if (r.shouldSpeak) speaker.speak(r.notice.text)
        simLog(r.notice.text)

        r.proposal?.let {
            proposal = ProposalUi(r.routineNames.firstOrNull() ?: "", 10_000L, System.currentTimeMillis())
            if (r.shouldSpeak) speaker.speak(Announcer.PROPOSAL, add = true)
            proposalJob?.cancel()
            proposalJob = scope.launch {
                delay(10_200)
                engine.expireProposalIfDue()   // 10초 무응답: 화면·음성 모두 없음(UX EV-S12-04)
                proposal = null
            }
        }
        scope.launch { refreshUndo(); refreshSim() }
    }

    fun runRoutine(localId: String, source: RunSource = RunSource.BUTTON) {
        if (!engineReady) return
        scope.launch {
            val r = engine.runManually(localId, source) ?: return@launch
            onReport(r)
        }
    }

    fun undo(spoken: Boolean = false) {
        if (!engineReady) return
        scope.launch {
            val r = engine.undo()
            val sub = buildList {
                if (r.skippedUserChanged.isNotEmpty()) add("직접 바꾼 ${r.skippedUserChanged.size}개는 그대로 뒀어요")
                if (r.failed.isNotEmpty()) add("${r.failed.size}개는 되돌리지 못했어요")
            }.joinToString(" · ").ifEmpty { null }
            showBanner(r.notice.text, sub, if (r.restored.isEmpty()) BannerTone.INFO else BannerTone.SUCCESS, durationMs = 3000)
            if (spoken) speaker.speak(r.notice.text)
            refreshUndo(); refreshSim()
        }
    }

    fun answerProposal(accept: Boolean) {
        proposalJob?.cancel()
        proposal = null
        scope.launch {
            when (val o = if (accept) engine.acceptProposal() else engine.rejectProposal()) {
                is ProposalOutcome.Accepted -> showBanner(o.notice.text, tone = BannerTone.SUCCESS, durationMs = 3000)
                is ProposalOutcome.Failed -> showBanner(o.notice.text, tone = BannerTone.FAIL, durationMs = 3000)
                is ProposalOutcome.Rejected -> showBanner(o.notice.text, durationMs = 3000)
                ProposalOutcome.Expired, ProposalOutcome.NoPending -> Unit
            }
            refreshSim()
        }
    }

    private suspend fun refreshUndo() { if (engineReady) canUndo = engine.canUndo() }

    /** 음성 인식 결과 처리: '원래대로' → 되돌리기, 그 외 루틴 이름 매칭 → 음성 실행. */
    fun onVoiceResult(candidates: List<String>) {
        voiceStatus = null
        if (Listener.isUndo(candidates)) { undo(spoken = true); return }
        val hit = Listener.match(candidates, routines) { it.spec.name }
        if (hit == null) {
            val heard = candidates.firstOrNull()?.let { "“$it”" } ?: ""
            showBanner("그런 이름의 루틴이 없어요", heard.ifEmpty { null }, BannerTone.WARN, durationMs = 3000)
            speaker.speak("그런 이름의 루틴이 없어요")
            return
        }
        runRoutine(hit.localId, RunSource.VOICE)
    }

    // ------------------------------------------------------------------ 내 루틴

    fun setEnabled(localId: String, enabled: Boolean) { scope.launch { routineStore.setEnabled(localId, enabled) } }
    fun move(localId: String, delta: Int) { scope.launch { routineStore.move(localId, delta) } }
    fun delete(localId: String) { scope.launch { routineStore.delete(localId) } }

    fun saveRoutine(existing: StoredRoutine?, spec: RoutineSpec) {
        scope.launch {
            val r = existing?.copy(spec = spec)
                ?: StoredRoutine(
                    localId = newId(),
                    spec = spec,
                    enabled = true,
                    priority = routineStore.nextPriority(),
                    source = RoutineSource.LOCAL,
                )
            routineStore.upsert(r)
            showBanner("${Labels.quoted(spec.name, com.carutine.engine.Josa.Pair.EUL_REUL)} 저장했어요", tone = BannerTone.SUCCESS, durationMs = 3000)
        }
    }

    private fun newId() = "loc_" + UUID.randomUUID().toString().replace("-", "").take(20)

    // ------------------------------------------------------------------ 장소 (좌표는 기기에만)

    /** S10 '위치로 지정' 후보. 시뮬레이터는 가상 좌표, 실제 차량은 마지막으로 받은 차량 위치. */
    fun placeCandidate(key: PlaceKey): Place? = when (mode) {
        VehicleMode.SIMULATOR -> virtualPoint(key)?.let { Place(key, Labels.place(key), it.first, it.second) }
        VehicleMode.REAL -> lastLocation?.let { Place(key, Labels.place(key), it.lat, it.lng) }
        VehicleMode.CONNECTING -> null
    }

    fun registerSimPlaces() {
        scope.launch {
            placeStore.put(listOfNotNull(placeCandidate(PlaceKey.HOME), placeCandidate(PlaceKey.WORK)))
            reemitLocation()
            simLog("집·회사 가상 위치를 기기에 저장")
        }
    }

    // ------------------------------------------------------------------ 갤러리·가져오기 (S03~S06, S10)

    fun updateHideIncompatible(hide: Boolean) { hideIncompatible = hide; loadGallery() }

    fun loadGallery() {
        scope.launch {
            galleryLoading = true
            val now = LocalDateTime.now()
            val c = caps
            if (api.enabled) {
                try {
                    val list = api.shelves(c, SeedGallery.season(now.monthValue), now.hour)
                    shelves = list
                    galleryOnline = true
                    galleryNote = null
                    galleryLoading = false
                    return@launch
                } catch (e: ApiException) {
                    galleryNote = "서버에 연결하지 못해 공식 루틴만 보여요 (${e.message})"
                } catch (e: Exception) {
                    galleryNote = "서버에 연결하지 못해 공식 루틴만 보여요"
                }
            } else {
                galleryNote = "서버 미연결: 오프라인 모드에서는 공식 루틴만 보여요"
            }
            galleryOnline = false
            shelves = SeedGallery.shelves(seeds, c, now.monthValue, now.hour, hideIncompatible)
            galleryLoading = false
        }
    }

    /** 상세용 spec. 서버 카드는 GET /v1/routines/{id}로 받는다. */
    suspend fun specFor(item: GalleryItem): Result<RoutineSpec> {
        item.spec?.let { return Result.success(it) }
        val id = item.id ?: return Result.failure(ApiException("루틴 id가 없어요"))
        return try {
            Result.success(api.detail(id, caps))
        } catch (e: ApiException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(ApiException("루틴을 불러오지 못했어요"))
        }
    }

    fun startImport(spec: RoutineSpec, remoteId: String?) {
        val sections = ImportPlanner.sections(spec)
        if (sections.needsSheet) importTarget = ImportTarget(spec, remoteId)
        else confirmImport(ImportTarget(spec, remoteId), emptyMap(), autoRun = true)
    }

    /** S10 '가져오기'. 결과 문구를 배너로 알리고 성공하면 홈으로 돌아간다. */
    fun confirmImport(target: ImportTarget, assigned: Map<PlaceKey, Place>, autoRun: Boolean) {
        scope.launch {
            val res = ImportPlanner.plan(
                spec = target.spec,
                remoteId = target.remoteId,
                caps = caps,
                existing = routineStore.all(),
                registered = placeStore.all(),
                assigned = assigned,
                autoRun = autoRun,
                newLocalId = { newId() },
            )
            when (res) {
                is ImportPlanner.Result.Ok -> {
                    if (res.newPlaces.isNotEmpty()) { placeStore.put(res.newPlaces); reemitLocation() }
                    routineStore.upsert(res.routine)
                    importTarget = null
                    while (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
                    showBanner(
                        "${Labels.quoted(res.routine.spec.name, com.carutine.engine.Josa.Pair.EUL_REUL)} 내 루틴에 추가했어요",
                        if (res.routine.spec.trigger != null && !res.routine.enabled) "자동 실행은 꺼 두었어요" else null,
                        BannerTone.SUCCESS, durationMs = 3000,
                    )
                    val rid = res.routine.remoteId
                    if (api.enabled && rid != null) scope.launch { runCatching { api.markImported(rid) } }
                }
                is ImportPlanner.Result.Incompatible -> showBanner("이 차에서는 쓸 수 없어요", tone = BannerTone.FAIL)
                is ImportPlanner.Result.PlaceMissing ->
                    showBanner("장소를 먼저 지정해 주세요", res.keys.joinToString(", ") { Labels.place(it) }, BannerTone.WARN)
                is ImportPlanner.Result.PlaceNotSupportedInMvp ->
                    showBanner("이 버전에서는 집·회사만 지정할 수 있어요", tone = BannerTone.WARN)
            }
        }
    }

    // ------------------------------------------------------------------ 공유 (S11)

    /** 업로드될 spec: id 없음(서버 발급), 좌표·작성자 정보는 계약에 아예 없다. */
    fun uploadJson(spec: RoutineSpec): JsonObject = JsonObject(RoutineJson.toJson(spec.copy(id = null)) - "id")

    suspend fun share(spec: RoutineSpec): Result<String> = try {
        Result.success(api.share(uploadJson(spec), caps))
    } catch (e: ApiException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(ApiException("공유하지 못했어요"))
    }

    // ------------------------------------------------------------------ 시뮬레이터

    private fun virtualPoint(key: PlaceKey): Pair<Double, Double>? = when (key) {
        PlaceKey.HOME -> VIRTUAL_HOME
        PlaceKey.WORK -> VIRTUAL_WORK
        else -> null
    }

    private fun simLog(text: String) {
        simEvents.add(0, text)
        while (simEvents.size > 12) simEvents.removeAt(simEvents.lastIndex)
    }

    private fun pointOf(p: SimPosition) = when (p) {
        SimPosition.HOME -> VIRTUAL_HOME
        SimPosition.WORK -> VIRTUAL_WORK
        SimPosition.AWAY -> VIRTUAL_AWAY
    }

    /** 엔진은 앱 시작 뒤 첫 값을 '기준'으로만 쓴다. 시작·사양 변경 때 현재 상태를 한 번 흘려보낸다. */
    private suspend fun simBaseline(initial: Boolean) {
        val f = fake ?: return
        f.emit(VehicleEvent.Drive(if (initial) DriveState.PARKED else driveState))
        f.emit(VehicleEvent.Location(pointOf(simPosition).first, pointOf(simPosition).second))
        f.emit(VehicleEvent.Energy(EnergySource.EV_BATTERY, simBattery.toDouble()))
        f.emit(VehicleEvent.Energy(EnergySource.FUEL, simFuel.toDouble()))
        f.emit(VehicleEvent.Wiper(simWiper))
    }

    private suspend fun reemitLocation() {
        val f = fake ?: return
        f.emit(VehicleEvent.Location(pointOf(simPosition).first, pointOf(simPosition).second))
    }

    private fun simGo(p: SimPosition) {
        val f = fake ?: return
        simPosition = p
        scope.launch { f.emit(VehicleEvent.Location(pointOf(p).first, pointOf(p).second)) }
    }

    fun simDepart(place: SimPosition) {
        if (fake == null) return
        if (simPosition != place) simGo(place)
        simGo(SimPosition.AWAY)
        simLog("${place.label} 출발")
        missingPlaceHint(place)
    }

    fun simArrive(place: SimPosition) {
        if (fake == null) return
        if (simPosition == place) {
            showBanner("이미 '${place.label}'에 있어요. 먼저 출발해 보세요", tone = BannerTone.INFO, durationMs = 2500)
            return
        }
        simGo(place)
        simLog("${place.label} 도착")
        missingPlaceHint(place)
    }

    private fun missingPlaceHint(place: SimPosition) {
        val key = if (place == SimPosition.HOME) PlaceKey.HOME else PlaceKey.WORK
        if (key !in places) showBanner("'${place.label}' 위치가 아직 없어요", "시뮬레이터에서 [집·회사 가상 위치 저장]을 눌러 주세요", BannerTone.WARN)
    }

    fun simSetBattery(p: Int) {
        val f = fake ?: return
        simBattery = p
        scope.launch { f.emit(VehicleEvent.Energy(EnergySource.EV_BATTERY, p.toDouble())) }
        simLog("배터리 $p%")
    }

    fun simSetFuel(p: Int) {
        val f = fake ?: return
        simFuel = p
        scope.launch { f.emit(VehicleEvent.Energy(EnergySource.FUEL, p.toDouble())) }
        simLog("연료 $p%")
    }

    fun simSetWiper(on: Boolean) {
        val f = fake ?: return
        simWiper = on
        scope.launch { f.emit(VehicleEvent.Wiper(on)) }
        simLog(if (on) "와이퍼 켜짐" else "와이퍼 꺼짐")
    }

    fun simSetCabin(t: Double) {
        val f = fake ?: return
        f.cabinTemp = t
        refreshSim()
    }

    fun simSetDriving(driving: Boolean) {
        val f = fake ?: return
        scope.launch { f.emit(VehicleEvent.Drive(if (driving) DriveState.DRIVING else DriveState.PARKED)) }
        simLog(if (driving) "주행 시작" else "정차")
    }

    fun simSetProfile(p: SimProfile) {
        val f = fake ?: return
        if (p == simProfile) return
        simProfile = p
        f.caps = p.caps
        scope.launch {
            startEngine()
            simBaseline(initial = false)
            loadGallery()
            simLog("차량 사양: ${p.label}")
        }
    }

    fun simResetCooldowns() {
        scope.launch { cooldownStore.clearAll(); simLog("재실행 방지 기록 초기화") }
    }

    fun refreshSim() {
        val f = fake ?: return
        sim = SimSnapshot(
            hvacTemp = f.hvacTemp,
            fan = f.fan,
            air = f.air,
            seatDriver = f.seat[Seat.DRIVER] ?: 0,
            seatPassenger = f.seat[Seat.PASSENGER] ?: 0,
            steering = f.steering,
            mirror = f.mirror,
            ambientOn = f.ambient.on,
            ambientColor = f.ambient.color,
            ambientBrightness = f.ambient.brightness,
            cabinTemp = f.cabinTemp ?: 0.0,
            waypoints = f.waypoints,
        )
    }

    companion object {
        // 가상 좌표(실제 장소 아님). 위도 0 적도 위의 임의 점이며 기기 안에서만 쓴다. 집↔회사 약 11km.
        val VIRTUAL_HOME = 0.0 to 0.0
        val VIRTUAL_WORK = 0.0 to 0.1
        val VIRTUAL_AWAY = 0.0 to 0.05
    }
}
