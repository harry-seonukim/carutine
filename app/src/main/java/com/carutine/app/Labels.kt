package com.carutine.app

import com.carutine.engine.Announcer
import com.carutine.engine.DriveState
import com.carutine.engine.Josa
import com.carutine.model.Action
import com.carutine.model.AirMode
import com.carutine.model.AmbientBrightness
import com.carutine.model.AmbientColor
import com.carutine.model.Cap
import com.carutine.model.CompatLevel
import com.carutine.model.Condition
import com.carutine.model.EnergySource
import com.carutine.model.HvacTempMode
import com.carutine.model.PlaceKey
import com.carutine.model.RoutineSpec
import com.carutine.model.Seat
import com.carutine.model.TempOp
import com.carutine.model.Trigger
import java.time.DayOfWeek

/** 화면 문구. HTML 데모(UX 1차 화면 정의서 기준)의 요약 문구와 같게 맞춘다. */
object Labels {

    fun place(p: PlaceKey): String = when (p) {
        PlaceKey.HOME -> "집"
        PlaceKey.WORK -> "회사"
        else -> p.wire
    }

    private fun placeQ(p: PlaceKey) = "'${place(p)}'"

    /** 작은따옴표로 감싼 이름 + 조사. 받침은 따옴표 안 글자로 판단한다. */
    fun quoted(name: String, pair: Josa.Pair): String =
        "'$name'" + if (Josa.hasFinal(name)) pair.withFinal else pair.withoutFinal

    fun trigger(t: Trigger): String = when (t) {
        is Trigger.LocationEnter -> "${placeQ(t.place)}에 도착했을 때"
        is Trigger.LocationExit -> "${quoted(place(t.place), Josa.Pair.EUL_REUL)} 떠났을 때"
        is Trigger.EnergyBelow -> "${energy(t.source)} ${t.percent}% 아래로"
        Trigger.WiperOn -> "와이퍼가 켜졌을 때"
    }

    fun energy(s: EnergySource) = if (s == EnergySource.FUEL) "연료" else "배터리"

    fun condition(c: Condition): String = when (c) {
        is Condition.TimeWindow -> "${days(c.days)} %02d:%02d~%02d:%02d".format(c.start.hour, c.start.minute, c.end.hour, c.end.minute)
        is Condition.CabinTemp -> "실내 온도 ${c.value}℃ ${if (c.op == TempOp.BELOW) "이하" else "이상"}"
    }

    val WEEKDAYS: Set<DayOfWeek> = setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)
    val WEEKEND: Set<DayOfWeek> = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

    fun days(d: Set<DayOfWeek>): String = when (d) {
        DayOfWeek.entries.toSet() -> "매일"
        WEEKDAYS -> "평일"
        WEEKEND -> "주말"
        else -> DayOfWeek.entries.filter { it in d }.joinToString("") { dayShort(it) }
    }

    fun dayShort(d: DayOfWeek) = when (d) {
        DayOfWeek.MONDAY -> "월"; DayOfWeek.TUESDAY -> "화"; DayOfWeek.WEDNESDAY -> "수"
        DayOfWeek.THURSDAY -> "목"; DayOfWeek.FRIDAY -> "금"; DayOfWeek.SATURDAY -> "토"; DayOfWeek.SUNDAY -> "일"
    }

    /** 카드·목록 한 줄 요약: 수동이면 '버튼·말로 실행'. */
    fun specSummary(spec: RoutineSpec): String {
        val t = spec.trigger ?: return "버튼·말로 실행"
        return (listOf(trigger(t)) + spec.conditions.map { condition(it) }).joinToString(" · ")
    }

    fun triggerTypeSummary(type: String?): String = when (type) {
        null -> "버튼·말로 실행"
        "location_enter" -> "장소에 도착했을 때"
        "location_exit" -> "장소를 떠났을 때"
        "energy_below" -> "배터리·연료가 기준 아래로"
        "wiper_on" -> "와이퍼가 켜졌을 때"
        else -> type
    }

    fun actionName(type: String) = Announcer.actionName(type)

    fun color(c: AmbientColor) = when (c) {
        AmbientColor.WARM -> "주황"; AmbientColor.COOL -> "흰색"; AmbientColor.BLUE -> "파랑"; AmbientColor.RED -> "빨강"
    }

    fun seat(s: Seat) = if (s == Seat.DRIVER) "운전석" else "동승석"

    fun actionValue(a: Action): String = when (a) {
        is Action.HvacTemp -> if (a.mode == HvacTempMode.SET) "${fmt(a.value)}℃" else "지금보다 ${if (a.value > 0) "+" else ""}${fmt(a.value)}℃"
        is Action.HvacFan -> "${a.level}단"
        is Action.HvacAir -> if (a.mode == AirMode.RECIRC) "내기" else "외기"
        is Action.SeatHeater -> "${seat(a.seat)} ${if (a.level > 0) "${a.level}단" else "끄기"}"
        is Action.SteeringHeater -> if (a.on) "켜기" else "끄기"
        is Action.MirrorHeater -> if (a.on) "켜기" else "끄기"
        is Action.AmbientLight -> if (!a.on) "끄기" else listOfNotNull(
            a.color?.let { color(it) } ?: "현재 색",
            if (a.brightness == AmbientBrightness.LOW) "밝기 낮게" else null,
        ).joinToString(" · ")
        is Action.Speak -> "“${a.text}”"
        Action.SuggestCharger -> ""
    }

    fun action(a: Action): String = if (a is Action.SuggestCharger) actionName(a.type) else "${actionName(a.type)} · ${actionValue(a)}"

    fun fmt(v: Double): String = if (v % 1.0 == 0.0) v.toInt().toString() else v.toString()

    fun compat(level: CompatLevel): String = when (level) {
        CompatLevel.FULL -> "내 차 완전 호환"
        CompatLevel.PARTIAL -> "일부 동작 제외"
        CompatLevel.NONE -> "이 차에서는 쓸 수 없어요"
    }

    fun cap(c: Cap): String = when (c) {
        Cap.LOCATION -> "위치"
        Cap.TIME -> "시간"
        Cap.EV_BATTERY -> "배터리 잔량"
        Cap.FUEL_LEVEL -> "연료 잔량"
        Cap.WIPER_STATE -> "와이퍼 상태"
        Cap.HVAC_CABIN_TEMP -> "실내 온도"
        Cap.HVAC_TEMP -> "설정 온도"
        Cap.HVAC_FAN -> "풍량"
        Cap.HVAC_AIR -> "내기/외기"
        Cap.SEAT_HEATER -> "시트 열선"
        Cap.STEERING_HEATER -> "핸들 열선"
        Cap.MIRROR_HEATER -> "사이드미러 열선"
        Cap.LIGHT_AMBIENT -> "앰비언트 라이트"
        Cap.TTS -> "음성 안내"
        Cap.NAVI_WAYPOINT -> "경유지 추가"
    }

    fun drive(d: DriveState): String = when (d) {
        DriveState.PARKED -> "정차 중"
        DriveState.CHARGING -> "충전 중"
        DriveState.DRIVING -> "주행 중"
        DriveState.UNKNOWN -> "주행 상태 확인 중"
    }
}
