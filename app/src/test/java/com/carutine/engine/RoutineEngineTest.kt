package com.carutine.engine

import com.carutine.model.Action
import com.carutine.model.AirMode
import com.carutine.model.AmbientBrightness
import com.carutine.model.AmbientColor
import com.carutine.model.Condition
import com.carutine.model.PlaceKey
import com.carutine.model.RoutineJson
import com.carutine.model.RoutineSpec
import com.carutine.model.Seat
import com.carutine.model.Trigger
import com.carutine.vehicle.fake.FakeVehicleGateway
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.Duration
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

class RoutineEngineTest {

    // ---------------------------------------------------------------- 픽스처

    private val zone = ZoneId.of("Asia/Seoul")
    /** 2026-11-18(수) 18:30 KST */
    private var now: Instant = ZonedDateTime.of(2026, 11, 18, 18, 30, 0, 0, zone).toInstant()

    private val WORK = Place(PlaceKey.WORK, "회사", 37.5665, 126.9780)
    private val HOME = Place(PlaceKey.HOME, "집", 37.5000, 127.0300)
    private val atWork = VehicleEvent.Location(WORK.lat, WORK.lng)
    private val awayFromWork = VehicleEvent.Location(WORK.lat + 0.003, WORK.lng)       // 약 333m
    private val inHysteresisBand = VehicleEvent.Location(WORK.lat + 0.0021, WORK.lng)  // 약 233m (200~250m)

    private fun spec(json: String) = (RoutineJson.parse(json) as RoutineJson.Result.Ok).spec

    private val winterCommute = spec(
        """{"schemaVersion":1,"name":"퇴근길 겨울","category":"commute",
        "trigger":{"type":"location_exit","place":"work"},
        "conditions":[{"type":"cabin_temp","op":"below","value":15}],
        "actions":[
          {"type":"steering_heater","on":true,"fallback":{"type":"hvac_temp","mode":"delta","value":2}},
          {"type":"seat_heater","seat":"driver","level":2},
          {"type":"ambient_light","on":true,"color":"warm"},
          {"type":"speak","text":"퇴근 루틴을 실행했어요"}]}""",
    )
    private val batterySaver = spec(
        """{"schemaVersion":1,"name":"배터리 절약","category":"ev",
        "trigger":{"type":"energy_below","source":"ev_battery","percent":20},
        "actions":[{"type":"hvac_fan","level":1},{"type":"hvac_air","mode":"recirc"},{"type":"suggest_charger"}]}""",
    )
    private val rainyNight = spec(
        """{"schemaVersion":1,"name":"비 오는 밤","category":"weather",
        "trigger":{"type":"wiper_on"},
        "conditions":[{"type":"time_window","days":["mon","tue","wed","thu","fri","sat","sun"],"start":"19:00","end":"06:00"}],
        "actions":[{"type":"ambient_light","on":true,"brightness":"low"},{"type":"mirror_heater","on":true},{"type":"hvac_air","mode":"fresh"}]}""",
    )

    private lateinit var car: FakeVehicleGateway
    private lateinit var store: InMemoryRoutineStore
    private lateinit var undo: InMemoryUndoStore
    private lateinit var log: InMemoryRunLog
    private lateinit var engine: RoutineEngine

    private fun setup(vararg specs: RoutineSpec, caps: Set<com.carutine.model.Cap> = FakeVehicleGateway.ALL_CAPS) {
        car = FakeVehicleGateway(caps)
        store = InMemoryRoutineStore(specs.mapIndexed { i, s -> StoredRoutine("r$i", s, true, i, RoutineSource.TEMPLATE) })
        undo = InMemoryUndoStore()
        log = InMemoryRunLog()
        engine = RoutineEngine(
            gateway = car, routines = store, places = InMemoryPlaceStore(mapOf(PlaceKey.WORK to WORK, PlaceKey.HOME to HOME)),
            undoStore = undo, cooldowns = InMemoryCooldownStore(), runLog = log,
            clock = { now }, zone = zone,
        )
    }

    private fun run(block: suspend () -> Unit) = runBlocking { block() }
    private fun advance(d: Duration) { now = now.plus(d) }

    /** 회사 안에서 시작 → 밖으로 나감. 첫 값은 기준만 잡는다. */
    private suspend fun leaveWork(): List<ExecutionReport> {
        engine.onVehicleEvent(atWork)
        return engine.onVehicleEvent(awayFromWork)
    }

    // ---------------------------------------------------------------- 시나리오 B (4개 장면)

    @Test fun `B1 회사를 떠나고 춥다면 퇴근길 겨울이 실행되고 A8 문장으로 한 번만 알린다`() = run {
        setup(winterCommute)
        car.cabinTemp = 10.0
        val r = leaveWork().single()
        assertTrue(car.steering)
        assertEquals(2, car.seat[Seat.DRIVER])
        assertEquals(DeviceValue.Ambient(true, AmbientColor.WARM, AmbientBrightness.DEFAULT), car.ambient)
        assertEquals(NoticeKind.R1, r.notice.kind)
        assertEquals("퇴근 루틴을 실행했어요", r.notice.text)    // D10: 기본 문장 대신 A8
        assertTrue(r.shouldSpeak)
        assertTrue(r.undoable)
        assertEquals(1, log.logs.size)
    }

    @Test fun `B2 원래대로 하면 실행 직전 값으로 돌아간다`() = run {
        setup(winterCommute)
        car.cabinTemp = 10.0
        leaveWork()
        val u = engine.undo()
        assertEquals("원래대로 돌렸어요", u.notice.text)
        assertFalse(car.steering)
        assertEquals(0, car.seat[Seat.DRIVER])
        assertEquals(DeviceValue.Ambient(false, AmbientColor.COOL, AmbientBrightness.DEFAULT), car.ambient)
        // 한 단계만: 두 번째 되돌리기는 없음
        assertEquals(Announcer.UNDO_NOTHING, engine.undo().notice.text)
    }

    @Test fun `B3 밤에 와이퍼를 켜면 비 오는 밤이 실행된다`() = run {
        setup(rainyNight)
        now = ZonedDateTime.of(2026, 11, 18, 21, 0, 0, 0, zone).toInstant()
        engine.onVehicleEvent(VehicleEvent.Wiper(false))           // 기준
        val r = engine.onVehicleEvent(VehicleEvent.Wiper(true)).single()
        assertEquals("비 오는 밤을 실행했어요", r.notice.text)
        assertTrue(car.mirror)
        assertEquals(AirMode.FRESH, car.air)
        assertEquals(AmbientBrightness.LOW, car.ambient.brightness)
    }

    @Test fun `B4 배터리가 20퍼센트 아래로 떨어지면 공조를 줄이고 충전소 제안 후 응으로 경유지를 추가한다`() = run {
        setup(batterySaver)
        engine.onVehicleEvent(FakeVehicleGateway.battery(21.0))   // 기준
        val r = engine.onVehicleEvent(FakeVehicleGateway.battery(19.0)).single()
        assertEquals(1, car.fan)
        assertEquals(AirMode.RECIRC, car.air)
        assertEquals(now.plusSeconds(10), r.proposal!!.expiresAt)
        val accepted = engine.acceptProposal()
        assertEquals(ProposalOutcome.Accepted(Notice(NoticeKind.R6, "경유지로 추가했어요")), accepted)
        assertEquals(1, car.waypoints)
        // 경유지는 되돌리기 대상이 아니다
        engine.undo()
        assertEquals(1, car.waypoints)
    }

    // ---------------------------------------------------------------- 사건 감지

    @Test fun `앱 시작 후 첫 값은 기준만 잡고 사건으로 보지 않는다`() = run {
        setup(winterCommute, batterySaver)
        car.cabinTemp = 10.0
        assertTrue(engine.onVehicleEvent(awayFromWork).isEmpty())          // 처음부터 회사 밖
        assertTrue(engine.onVehicleEvent(FakeVehicleGateway.battery(15.0)).isEmpty())  // 처음부터 20% 미만
        assertTrue(engine.onVehicleEvent(VehicleEvent.Wiper(true)).isEmpty())
        assertTrue(car.applied.isEmpty())
    }

    @Test fun `반경 200m와 250m 사이 흔들림은 밖으로 보지 않는다`() = run {
        setup(winterCommute)
        car.cabinTemp = 10.0
        engine.onVehicleEvent(atWork)
        assertTrue(engine.onVehicleEvent(inHysteresisBand).isEmpty())
        assertEquals(1, engine.onVehicleEvent(awayFromWork).size)
    }

    @Test fun `배터리 기준은 이상에서 미만으로 바뀐 순간만이고 아래에서 오르내리면 사건이 아니다`() = run {
        setup(batterySaver)
        engine.onVehicleEvent(FakeVehicleGateway.battery(21.0))
        assertTrue(engine.onVehicleEvent(FakeVehicleGateway.battery(20.0)).isEmpty())   // 20은 미만 아님
        assertEquals(1, engine.onVehicleEvent(FakeVehicleGateway.battery(19.0)).size)
        assertTrue(engine.onVehicleEvent(FakeVehicleGateway.battery(19.5)).isEmpty())
        advance(Duration.ofHours(1))
        assertTrue(engine.onVehicleEvent(FakeVehicleGateway.battery(18.0)).isEmpty())   // 기준 아래에서 오르내림
    }

    // ---------------------------------------------------------------- 평가 규칙

    @Test fun `같은 루틴은 자동 실행 후 30분 안에 다시 실행하지 않고 버튼 실행은 막지 않는다`() = run {
        setup(winterCommute)
        car.cabinTemp = 10.0
        assertEquals(1, leaveWork().size)
        advance(Duration.ofMinutes(20))
        assertTrue(leaveWork().isEmpty())
        assertTrue(engine.runManually("r0", RunSource.BUTTON) != null)
        advance(Duration.ofMinutes(11))
        assertEquals(1, leaveWork().size)
    }

    @Test fun `되돌려도 재실행 방지 30분은 유지한다`() = run {
        setup(winterCommute)
        car.cabinTemp = 10.0
        leaveWork(); engine.undo()
        advance(Duration.ofMinutes(5))
        assertTrue(leaveWork().isEmpty())
    }

    @Test fun `상태 조건이 맞지 않거나 값을 읽지 못하면 실행하지 않는다`() = run {
        setup(winterCommute)
        car.cabinTemp = 18.0
        assertTrue(leaveWork().isEmpty())
        car.cabinTemp = null
        assertTrue(leaveWork().isEmpty())
        car.cabinTemp = 15.0                                    // '15℃ 이하' 경계 포함
        assertEquals(1, leaveWork().size)
    }

    @Test fun `자동 실행이 꺼진 루틴은 사건에 반응하지 않지만 버튼으로는 실행된다`() = run {
        setup(winterCommute)
        store.routines = store.routines.map { it.copy(enabled = false) }
        car.cabinTemp = 10.0
        assertTrue(leaveWork().isEmpty())
        val r = engine.runManually("r0", RunSource.BUTTON)!!
        assertFalse(r.shouldSpeak)                              // 버튼 실행은 화면만
        assertTrue(car.steering)
    }

    @Test fun `시간대는 자정을 넘기고 넘어간 새벽은 시작한 날 요일로 본다`() {
        setup(rainyNight)
        val weekdayNight = Condition.TimeWindow(
            setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY),
            LocalTime.of(19, 0), LocalTime.of(6, 0),
        )
        fun at(d: Int, h: Int) = ZonedDateTime.of(2026, 11, d, h, 0, 0, 0, zone)   // 11/20 금, 11/21 토
        assertTrue(ConditionEvaluator.inWindow(weekdayNight, at(20, 23)))
        assertTrue(ConditionEvaluator.inWindow(weekdayNight, at(21, 2)))    // 토 02시 = 금요일 밤
        assertFalse(ConditionEvaluator.inWindow(weekdayNight, at(21, 23)))  // 토요일 밤
        assertFalse(ConditionEvaluator.inWindow(weekdayNight, at(20, 6)))   // 끝 시각은 포함하지 않음
    }

    // ---------------------------------------------------------------- 충돌 · fallback · 실패

    @Test fun `같은 장치를 여러 루틴이 바꾸면 목록 위 루틴의 값만 남기고 알림은 하나로 합친다`() = run {
        val rainFresh = rainyNight
        val rainRecirc = spec(
            """{"schemaVersion":1,"name":"비 오는 날 내기","trigger":{"type":"wiper_on"},
            "actions":[{"type":"hvac_air","mode":"recirc"},{"type":"hvac_fan","level":3}]}""",
        )
        setup(rainFresh, rainRecirc)                            // rainFresh가 우선
        now = ZonedDateTime.of(2026, 11, 18, 21, 0, 0, 0, zone).toInstant()
        engine.onVehicleEvent(VehicleEvent.Wiper(false))
        val r = engine.onVehicleEvent(VehicleEvent.Wiper(true)).single()
        assertEquals(AirMode.FRESH, car.air)                   // 우선 루틴 값
        assertEquals(3, car.fan)                                // 겹치지 않는 동작은 실행
        assertEquals(NoticeKind.R4, r.notice.kind)
        assertEquals("비 오는 밤과 비 오는 날 내기를 실행했어요", r.notice.text)
        assertEquals(OutcomeStatus.SKIPPED_CONFLICT, r.outcomes.first { it.routineId == "r1" && it.spec.action.type == "hvac_air" }.status)
    }

    @Test fun `옵션이 없는 차는 fallback으로 대신 실행하고 대체 동작도 되돌린다`() = run {
        setup(winterCommute, caps = FakeVehicleGateway.BASIC_EV)
        car.cabinTemp = 10.0
        car.hvacTemp = 22.0
        val r = leaveWork().single()
        assertEquals(24.0, car.hvacTemp, 0.0)                   // 핸들 열선 대신 설정 온도 +2
        assertEquals(Action.HvacTemp(com.carutine.model.HvacTempMode.SET, 24.0), r.outcomes[0].executed)
        assertEquals(OutcomeStatus.APPLIED_VIA_FALLBACK, r.outcomes[0].status)
        assertEquals(listOf("seat_heater", "ambient_light"), r.excludedActionTypes)
        assertEquals("퇴근 루틴을 실행했어요", r.notice.text)
        engine.undo()
        assertEquals(22.0, car.hvacTemp, 0.0)
    }

    @Test fun `하나가 실패해도 나머지는 실행하고 실패한 동작만 한 문장 더한다`() = run {
        setup(winterCommute)
        car.cabinTemp = 10.0
        car.failTypes += "seat_heater"
        val r = leaveWork().single()
        assertTrue(car.steering)
        assertTrue(car.ambient.on)
        assertEquals(NoticeKind.R2, r.notice.kind)
        assertEquals("퇴근 루틴을 실행했어요. 시트 열선은 켜지 않았어요", r.notice.text)
    }

    @Test fun `모든 동작이 실패하면 R3이고 되돌릴 스냅샷을 남기지 않는다`() = run {
        setup(batterySaver)
        car.failTypes += setOf("hvac_fan", "hvac_air")
        val r = engine.runManually("r0", RunSource.VOICE)!!
        assertEquals(NoticeKind.R3, r.notice.kind)
        assertEquals("배터리 절약을 실행하지 못했어요", r.notice.text)
        assertFalse(r.undoable)
        assertNull(r.proposal)                                  // 실행 못 했으면 제안도 하지 않음
        assertNull(undo.value)
    }

    // ---------------------------------------------------------------- 되돌리기

    @Test fun `되돌리기는 그사이 사용자가 직접 바꾼 장치는 건너뛴다`() = run {
        setup(winterCommute)
        car.cabinTemp = 10.0
        leaveWork()
        car.seat[Seat.DRIVER] = 1                               // 사용자가 직접 1단으로 낮춤
        val u = engine.undo()
        assertEquals(listOf<DeviceKey>(DeviceKey.SeatHeater(Seat.DRIVER)), u.skippedUserChanged)
        assertEquals(1, car.seat[Seat.DRIVER])                 // 덮어쓰지 않음
        assertFalse(car.steering)                               // 나머지는 복원
    }

    @Test fun `10분이 지나면 되돌릴 수 없다`() = run {
        setup(winterCommute)
        car.cabinTemp = 10.0
        leaveWork()
        advance(Duration.ofMinutes(10))
        assertTrue(engine.canUndo())
        advance(Duration.ofSeconds(1))
        assertFalse(engine.canUndo())
        assertEquals(Announcer.UNDO_NOTHING, engine.undo().notice.text)
        assertTrue(car.steering)
    }

    @Test fun `다음 실행이 오면 이전 스냅샷은 버리고 가장 최근 실행만 되돌린다`() = run {
        setup(winterCommute, batterySaver)
        car.cabinTemp = 10.0
        leaveWork()
        engine.runManually("r1", RunSource.BUTTON)
        engine.undo()
        assertTrue(car.steering)                                // 퇴근길 겨울은 그대로
        assertEquals(2, car.fan)                                // 배터리 절약만 복원
    }

    // ---------------------------------------------------------------- 제안

    @Test fun `제안은 10초가 지나면 조용히 취소된다`() = run {
        setup(batterySaver)
        engine.runManually("r0", RunSource.VOICE)
        advance(Duration.ofSeconds(11))
        assertEquals(ProposalOutcome.Expired, engine.acceptProposal())
        assertEquals(0, car.waypoints)
    }

    @Test fun `아니라고 답하면 알겠어요로 끝난다`() = run {
        setup(batterySaver)
        engine.runManually("r0", RunSource.VOICE)
        assertEquals(ProposalOutcome.Rejected(Notice(NoticeKind.R6, "알겠어요")), engine.rejectProposal())
        assertEquals(ProposalOutcome.NoPending, engine.acceptProposal())
    }

    // ---------------------------------------------------------------- 가져오기(D2)

    @Test fun `가져올 때 자동 실행 답을 enabled에 저장하고 목록 맨 아래에 넣는다`() {
        val existing = listOf(StoredRoutine("a", winterCommute, true, 0, RoutineSource.TEMPLATE))
        val gallery = winterCommute.copy(id = "rt_X")
        val r = ImportPlanner.plan(
            gallery, "rt_X", FakeVehicleGateway.ALL_CAPS, existing,
            registered = emptyMap(), assigned = mapOf(PlaceKey.WORK to WORK), autoRun = false, newLocalId = { "b" },
        ) as ImportPlanner.Result.Ok
        assertFalse(r.routine.enabled)
        assertEquals(1, r.routine.priority)
        assertEquals("퇴근길 겨울 2", r.routine.spec.name)       // 임시 규칙
        assertEquals(listOf(WORK), r.newPlaces)

        val manual = batterySaver.copy(trigger = null)
        assertEquals(ImportPlanner.SheetSections(emptySet(), askAutoRun = false), ImportPlanner.sections(manual))
        val m = ImportPlanner.plan(manual, null, FakeVehicleGateway.ALL_CAPS, existing, emptyMap(), emptyMap(), autoRun = false) { "c" }
        assertTrue((m as ImportPlanner.Result.Ok).routine.enabled)   // 수동 루틴은 묻지 않음
    }

    @Test fun `MVP에서는 집·회사 외 장소를 쓰는 루틴과 장소 미지정은 가져오지 않는다`() {
        val custom = winterCommute.copy(trigger = Trigger.LocationExit(PlaceKey.CUSTOM1))
        assertTrue(ImportPlanner.plan(custom, null, FakeVehicleGateway.ALL_CAPS, emptyList(), emptyMap(), emptyMap(), true) { "x" }
            is ImportPlanner.Result.PlaceNotSupportedInMvp)
        assertEquals(ImportPlanner.Result.PlaceMissing(setOf(PlaceKey.WORK)),
            ImportPlanner.plan(winterCommute, null, FakeVehicleGateway.ALL_CAPS, emptyList(), emptyMap(), emptyMap(), true) { "x" })
        assertTrue(ImportPlanner.plan(winterCommute, null, FakeVehicleGateway.ALL_CAPS - com.carutine.model.Cap.HVAC_CABIN_TEMP,
            emptyList(), mapOf(PlaceKey.WORK to WORK), emptyMap(), true) { "x" } is ImportPlanner.Result.Incompatible)
    }

    // ---------------------------------------------------------------- 문구

    @Test fun `조사는 받침에 따라 바뀐다`() {
        assertEquals("퇴근길 겨울을", Josa.attach("퇴근길 겨울", Josa.Pair.EUL_REUL))
        assertEquals("비 오는 밤과", Josa.attach("비 오는 밤", Josa.Pair.GWA_WA))
        assertEquals("배터리 절약을", Josa.attach("배터리 절약", Josa.Pair.EUL_REUL))
        assertEquals("출근 루틴 2를", Josa.attach("출근 루틴 2", Josa.Pair.EUL_REUL))
        assertEquals("핸들 열선은", Josa.attach("핸들 열선", Josa.Pair.EUN_NEUN))
        assertEquals("음성 안내는", Josa.attach("음성 안내", Josa.Pair.EUN_NEUN))
    }

    @Test fun `speak만 있는 수동 루틴은 실패가 아니라 A8 문장을 읽는다`() = run {
        val fuel = spec("""{"schemaVersion":1,"name":"연료 알림","trigger":null,
            "actions":[{"type":"speak","text":"연료가 15% 남았어요"}]}""")
        setup(fuel)
        val r = engine.runManually("r0", RunSource.VOICE)!!
        assertEquals(NoticeKind.R1, r.notice.kind)
        assertEquals("연료가 15% 남았어요", r.notice.text)
    }

}
