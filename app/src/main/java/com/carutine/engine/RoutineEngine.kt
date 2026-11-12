package com.carutine.engine

import com.carutine.model.Action
import com.carutine.model.ActionSpec
import com.carutine.model.Cap
import com.carutine.model.CompatChecker
import com.carutine.model.Condition
import com.carutine.model.ActionSupport
import com.carutine.model.HvacTempMode
import com.carutine.model.TempOp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Duration
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.abs

enum class RunSource { AUTO, BUTTON, VOICE }

enum class OutcomeStatus {
    /** 원래 동작으로 실행됨 */
    APPLIED,
    /** fallback으로 대체 실행됨 */
    APPLIED_VIA_FALLBACK,
    /** 차량이 지원하지 않고 fallback도 없음 → 건너뜀(실패 아님, '제외'로 표시) */
    SKIPPED_UNSUPPORTED,
    /** 같은 장치를 우선순위가 높은 루틴이 이미 바꿈 */
    SKIPPED_CONFLICT,
    /** 실행했지만 차량이 거부·오류 */
    FAILED,
    /** speak: 알림 문장에 합쳐 읽음 */
    SPOKEN,
    /** suggest_charger: 제안 대기로 넘김 */
    PROPOSED,
}

data class ActionOutcome(
    val routineId: String,
    val spec: ActionSpec,
    /** 실제로 보낸 동작(fallback·delta→set 변환 반영). 건너뛰면 null. */
    val executed: Action?,
    val status: OutcomeStatus,
    val reason: String? = null,
)

data class Proposal(val routineId: String, val expiresAt: Instant)

data class ExecutionReport(
    val at: Instant,
    val source: RunSource,
    val trigger: TriggerEvent?,
    val routineIds: List<String>,
    val routineNames: List<String>,
    val outcomes: List<ActionOutcome>,
    val notice: Notice,
    /** UX 6장: 음성은 자동·음성 실행만, 버튼 실행은 화면만. */
    val shouldSpeak: Boolean,
    val proposal: Proposal?,
    /** 되돌릴 스냅샷이 저장됐는지(R3면 false). */
    val undoable: Boolean,
) {
    /** S02 '제외' 표시용 */
    val excludedActionTypes: List<String>
        get() = outcomes.filter { it.status == OutcomeStatus.SKIPPED_UNSUPPORTED }.map { it.spec.action.type }
}

data class UndoReport(
    val notice: Notice,
    val restored: List<DeviceKey>,
    /** 실행 뒤 사용자가 직접 바꾼 장치(복원하지 않음) */
    val skippedUserChanged: List<DeviceKey>,
    val failed: List<DeviceKey>,
)

sealed interface ProposalOutcome {
    data class Accepted(val notice: Notice) : ProposalOutcome
    data class Failed(val notice: Notice) : ProposalOutcome
    data class Rejected(val notice: Notice) : ProposalOutcome
    /** 10초 무응답: 화면·음성 모두 없음(UX EV-S12-04). */
    data object Expired : ProposalOutcome
    data object NoPending : ProposalOutcome
}

/**
 * 루틴 엔진(작업 문서 5장). 사건이 일어났을 때만 평가하고, 상태 조건을 계속 감시하지 않는다.
 * 모든 공개 함수는 Mutex로 직렬화한다(사건 처리 중 수동 실행·되돌리기가 끼어들지 않게).
 */
class RoutineEngine(
    private val gateway: VehicleGateway,
    private val routines: RoutineStore,
    private val places: PlaceStore,
    private val undoStore: UndoSnapshotStore,
    private val cooldowns: CooldownStore,
    private val runLog: RunLogSink,
    private val config: EngineConfig = EngineConfig(),
    private val clock: () -> Instant = Instant::now,
    private val zone: ZoneId = ZoneId.systemDefault(),
) {
    private val mutex = Mutex()
    private val detector = TriggerDetector(config)
    private var caps: Set<Cap>? = null
    private var pending: Proposal? = null

    var driveState: DriveState = DriveState.UNKNOWN
        private set

    /** 게이트웨이 스트림을 구독한다. 결과는 [onReport]로 UI(S12)·TTS에 넘긴다. */
    fun start(scope: CoroutineScope, onReport: suspend (ExecutionReport) -> Unit): Job = scope.launch {
        gateway.observe().collect { e -> onVehicleEvent(e).forEach { onReport(it) } }
    }

    suspend fun capabilities(): Set<Cap> = caps ?: gateway.capabilities().also { caps = it }

    /** 차량 상태 하나를 처리한다. 사건이 여러 개면 사건마다 별개 실행이다(UX F3 '동시 실행 판정'). */
    suspend fun onVehicleEvent(e: VehicleEvent): List<ExecutionReport> = mutex.withLock {
        if (e is VehicleEvent.Drive) driveState = e.state
        val events = detector.onEvent(e, places.all())
        events.mapNotNull { evaluateLocked(it) }
    }

    /** 사건 하나 평가(테스트·시나리오 재생에서 직접 부를 수 있게 공개). */
    suspend fun onTrigger(event: TriggerEvent): ExecutionReport? = mutex.withLock { evaluateLocked(event) }

    /** 버튼·음성 실행: trigger·conditions·cooldown을 보지 않고 실행부터(작업 문서 5장 6). */
    suspend fun runManually(routineId: String, source: RunSource): ExecutionReport? = mutex.withLock {
        require(source != RunSource.AUTO)
        val r = routines.all().firstOrNull { it.localId == routineId } ?: return@withLock null
        executeLocked(listOf(r), source, null)
    }

    suspend fun undo(): UndoReport = mutex.withLock {
        val snap = undoStore.load()
        val now = clock()
        if (snap == null || Duration.between(snap.at, now) > config.undoWindow) {
            undoStore.clear()
            return@withLock UndoReport(Notice(NoticeKind.R7, Announcer.UNDO_NOTHING), emptyList(), emptyList(), emptyList())
        }
        val restored = mutableListOf<DeviceKey>()
        val userChanged = mutableListOf<DeviceKey>()
        val failed = mutableListOf<DeviceKey>()
        for (entry in snap.entries) {
            val current = gateway.readDevice(entry.key)
            if (current == null) { failed += entry.key; continue }
            if (!matchesApplied(current, entry.applied)) { userChanged += entry.key; continue }
            when (gateway.apply(restoreAction(entry.key, entry.before))) {
                ActionResult.Success -> restored += entry.key
                else -> failed += entry.key
            }
        }
        undoStore.clear()   // 한 단계만 되돌린다. 재실행 방지(cooldown)는 그대로 둔다(UX F3).
        val text = if (restored.isEmpty() && failed.isNotEmpty()) Announcer.UNDO_NOTHING else Announcer.UNDO_DONE
        UndoReport(Notice(NoticeKind.R7, text), restored, userChanged, failed)
    }

    suspend fun canUndo(): Boolean = mutex.withLock {
        val s = undoStore.load() ?: return@withLock false
        Duration.between(s.at, clock()) <= config.undoWindow
    }

    suspend fun acceptProposal(): ProposalOutcome = mutex.withLock {
        val p = pending ?: return@withLock ProposalOutcome.NoPending
        pending = null
        if (clock() > p.expiresAt) return@withLock ProposalOutcome.Expired
        when (gateway.apply(Action.SuggestCharger)) {
            ActionResult.Success -> ProposalOutcome.Accepted(Notice(NoticeKind.R6, Announcer.PROPOSAL_ACCEPTED))
            else -> ProposalOutcome.Failed(Notice(NoticeKind.R6, Announcer.PROPOSAL_FAILED))
        }
    }

    suspend fun rejectProposal(): ProposalOutcome = mutex.withLock {
        val p = pending ?: return@withLock ProposalOutcome.NoPending
        pending = null
        if (clock() > p.expiresAt) ProposalOutcome.Expired
        else ProposalOutcome.Rejected(Notice(NoticeKind.R6, Announcer.PROPOSAL_REJECTED))
    }

    /** 앱 계층의 10초 타이머가 부른다. 만료됐으면 조용히 지운다. */
    suspend fun expireProposalIfDue(): Boolean = mutex.withLock {
        val p = pending ?: return@withLock false
        if (clock() > p.expiresAt) { pending = null; true } else false
    }

    val pendingProposal: Proposal? get() = pending

    // ------------------------------------------------------------------ 평가

    private suspend fun evaluateLocked(event: TriggerEvent): ExecutionReport? {
        val now = clock()
        val caps = capabilities()
        var cabinTemp: Double? = null
        var cabinRead = false
        val hits = mutableListOf<StoredRoutine>()
        for (r in routines.all()) {                         // priority 순
            val t = r.spec.trigger ?: continue
            if (!r.enabled || !event.matches(t)) continue
            val condOk = r.spec.conditions.all { c ->
                when (c) {
                    is Condition.TimeWindow -> ConditionEvaluator.inWindow(c, ZonedDateTime.ofInstant(now, zone))
                    is Condition.CabinTemp -> {
                        if (!cabinRead) { cabinTemp = gateway.readCabinTemp(); cabinRead = true }
                        cabinTemp?.let { ConditionEvaluator.cabinOk(c, it) } ?: false   // 값을 읽지 못하면 불만족
                    }
                }
            }
            if (!condOk) continue
            val last = cooldowns.lastAutoRun(r.localId)
            if (last != null && Duration.between(last, now) < config.cooldown) continue
            if (CompatChecker.check(r.spec, caps).runnableCount == 0) continue
            hits += r
        }
        if (hits.isEmpty()) return null
        return executeLocked(hits, RunSource.AUTO, event)
    }

    // ------------------------------------------------------------------ 실행

    private suspend fun executeLocked(list: List<StoredRoutine>, source: RunSource, trigger: TriggerEvent?): ExecutionReport {
        val now = clock()
        val caps = capabilities()
        val outcomes = mutableListOf<ActionOutcome>()
        val usedDevices = mutableSetOf<DeviceKey>()
        val entries = mutableListOf<UndoSnapshot.Entry>()
        val failedActions = mutableListOf<Action>()
        val speakTexts = mutableListOf<String>()
        var proposeFor: String? = null

        for (r in list) {
            // A8 음성 안내는 항상 마지막(UX 3장, D10). 나머지는 적힌 순서대로.
            val ordered = r.spec.actions.filter { it.action !is Action.Speak } + r.spec.actions.filter { it.action is Action.Speak }
            for (spec in ordered) {
                val (effective, viaFallback) = when (CompatChecker.supportOf(spec, caps)) {
                    ActionSupport.SUPPORTED -> spec.action to false
                    ActionSupport.VIA_FALLBACK -> spec.fallback!! to true
                    ActionSupport.UNSUPPORTED -> {
                        outcomes += ActionOutcome(r.localId, spec, null, OutcomeStatus.SKIPPED_UNSUPPORTED); continue
                    }
                }
                when (effective) {
                    is Action.Speak -> {
                        speakTexts += effective.text
                        outcomes += ActionOutcome(r.localId, spec, effective, OutcomeStatus.SPOKEN); continue
                    }
                    Action.SuggestCharger -> {
                        if (proposeFor == null) proposeFor = r.localId
                        outcomes += ActionOutcome(r.localId, spec, effective, OutcomeStatus.PROPOSED); continue
                    }
                    else -> Unit
                }
                val key = DeviceKey.of(effective)!!
                if (key in usedDevices) {   // 같은 장치는 목록 위 루틴의 값만 남긴다(D3)
                    outcomes += ActionOutcome(r.localId, spec, null, OutcomeStatus.SKIPPED_CONFLICT); continue
                }
                usedDevices += key

                // 1) 바꿀 장치의 현재값을 읽어 둔다(스냅샷)
                val before = gateway.readDevice(key)
                // 2) delta는 현재값 기준 절대값으로 바꾼다. 현재값을 모르면 계산할 수 없어 실패 처리.
                val toSend = if (effective is Action.HvacTemp && effective.mode == HvacTempMode.DELTA) {
                    val cur = (before as? DeviceValue.Temperature)?.celsius
                    if (cur == null) {
                        failedActions += effective
                        outcomes += ActionOutcome(r.localId, spec, effective, OutcomeStatus.FAILED, "현재 설정 온도를 읽지 못함"); continue
                    }
                    Action.HvacTemp(HvacTempMode.SET, (cur + effective.value).coerceIn(config.hvacTempMin, config.hvacTempMax))
                } else effective

                // 3) 실행. 하나가 실패해도 나머지는 계속한다.
                when (val res = gateway.apply(toSend)) {
                    ActionResult.Success -> {
                        outcomes += ActionOutcome(r.localId, spec, toSend,
                            if (viaFallback) OutcomeStatus.APPLIED_VIA_FALLBACK else OutcomeStatus.APPLIED)
                        if (before != null) entries += UndoSnapshot.Entry(key, before, toSend)
                    }
                    ActionResult.Unsupported -> {
                        // capability와 달리 차량이 미지원 응답 → 실패로 알린다(사용자 입장에선 켜지지 않음)
                        failedActions += effective
                        outcomes += ActionOutcome(r.localId, spec, toSend, OutcomeStatus.FAILED, "unsupported")
                    }
                    is ActionResult.Failed -> {
                        failedActions += effective
                        outcomes += ActionOutcome(r.localId, spec, toSend, OutcomeStatus.FAILED, res.reason)
                    }
                }
            }
            if (source == RunSource.AUTO) cooldowns.markAutoRun(r.localId, now)
        }

        val appliedCount = outcomes.count { it.status == OutcomeStatus.APPLIED || it.status == OutcomeStatus.APPLIED_VIA_FALLBACK }
        val notice = Announcer.executionNotice(
            names = list.map { it.spec.name },
            speakTexts = if (list.size == 1) speakTexts else emptyList(),
            appliedCount = appliedCount,
            failed = failedActions,
        )

        // 스냅샷: 실제로 바뀐 장치만. '다음 실행'이 오면 이전 스냅샷은 버리고(가장 최근 1건만),
        // 모두 실패(R3)면 저장할 값도 없다(UX F3).
        val undoable = notice.kind != NoticeKind.R3 && entries.isNotEmpty()
        if (undoable) undoStore.save(UndoSnapshot(now, list.map { it.localId }, entries)) else undoStore.clear()

        val proposal = if (proposeFor != null && notice.kind != NoticeKind.R3)
            Proposal(proposeFor, now.plus(config.proposalTimeout)) else null
        pending = proposal ?: pending

        runLog.record(RunLog(now, list.map { it.localId }, source, trigger, outcomes))

        return ExecutionReport(
            at = now, source = source, trigger = trigger,
            routineIds = list.map { it.localId }, routineNames = list.map { it.spec.name },
            outcomes = outcomes, notice = notice,
            shouldSpeak = source != RunSource.BUTTON,
            proposal = proposal, undoable = undoable,
        )
    }

    // ------------------------------------------------------------------ 조건·비교 helper

    private fun matchesApplied(current: DeviceValue, applied: Action): Boolean = when (applied) {
        is Action.HvacTemp -> current is DeviceValue.Temperature && abs(current.celsius - applied.value) <= config.tempTolerance
        is Action.HvacFan -> current is DeviceValue.Level && current.level == applied.level
        is Action.HvacAir -> current is DeviceValue.Air && current.mode == applied.mode
        is Action.SeatHeater -> current is DeviceValue.Level && current.level == applied.level
        is Action.SteeringHeater -> current is DeviceValue.OnOff && current.on == applied.on
        is Action.MirrorHeater -> current is DeviceValue.OnOff && current.on == applied.on
        is Action.AmbientLight -> current is DeviceValue.Ambient && current.on == applied.on &&
            (applied.color == null || current.color == applied.color) &&
            (applied.brightness == null || current.brightness == applied.brightness)
        is Action.Speak, Action.SuggestCharger -> false
    }

    private fun restoreAction(key: DeviceKey, before: DeviceValue): Action = when (key) {
        DeviceKey.HvacTemp -> Action.HvacTemp(HvacTempMode.SET, (before as DeviceValue.Temperature).celsius)
        DeviceKey.HvacFan -> Action.HvacFan((before as DeviceValue.Level).level)
        DeviceKey.HvacAir -> Action.HvacAir((before as DeviceValue.Air).mode)
        is DeviceKey.SeatHeater -> Action.SeatHeater(key.seat, (before as DeviceValue.Level).level)
        DeviceKey.SteeringHeater -> Action.SteeringHeater((before as DeviceValue.OnOff).on)
        DeviceKey.MirrorHeater -> Action.MirrorHeater((before as DeviceValue.OnOff).on)
        DeviceKey.AmbientLight -> (before as DeviceValue.Ambient).let { Action.AmbientLight(it.on, it.color, it.brightness) }
    }
}

/** 상태 조건 평가(T3, T6). 순수 함수라 따로 단위 테스트한다. */
object ConditionEvaluator {
    /**
     * T3 시간대. [start, end) 반열린 구간. end < start면 자정을 넘기고, 넘어간 새벽은 '시작한 날' 요일로 판정한다
     * (예: 평일 19:00~06:00이면 토요일 02:00은 금요일 밤에 포함). start == end면 하루 종일.
     */
    fun inWindow(c: Condition.TimeWindow, now: ZonedDateTime): Boolean {
        val t: LocalTime = now.toLocalTime()
        val today = now.dayOfWeek
        return when {
            c.start == c.end -> today in c.days
            !c.crossesMidnight -> today in c.days && t >= c.start && t < c.end
            t >= c.start -> today in c.days
            t < c.end -> today.minus(1) in c.days
            else -> false
        }
    }

    /** T6 실내 온도. UX 문구('15℃ 이하', '28℃ 이상')에 맞춰 경계를 포함한다. → 확인 요청 Q-04 */
    fun cabinOk(c: Condition.CabinTemp, temp: Double): Boolean = when (c.op) {
        TempOp.BELOW -> temp <= c.value
        TempOp.ABOVE -> temp >= c.value
    }
}
