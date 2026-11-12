package com.carutine.model

import java.time.DayOfWeek
import java.time.LocalTime

/**
 * 루틴 데이터 계약 v0.1 (원본: 백엔드 문서 3장, 사본: 프론트엔드 문서 4장).
 * 이 파일의 타입과 [RoutineJson]의 JSON 키는 계약과 1:1이다. 계약을 바꾸려면 PM에게 요청한다.
 */
data class RoutineSpec(
    val schemaVersion: Int = 1,
    /** 서버 발급 id(`rt_` + ULID). 로컬에서 새로 만든 루틴은 null. */
    val id: String? = null,
    val name: String,
    val description: String = "",
    val category: Category = Category.ETC,
    val tags: List<String> = emptyList(),
    /** null이면 버튼·음성 전용(수동) 루틴. */
    val trigger: Trigger?,
    val conditions: List<Condition> = emptyList(),
    val actions: List<ActionSpec>,
) {
    val isManualOnly: Boolean get() = trigger == null

    /** trigger와 conditions가 요구하는 Cap 집합(호환 판정의 triggerOk에 쓴다). */
    fun triggerCaps(): Set<Cap> = buildSet {
        trigger?.let { add(it.requiredCap) }
        conditions.forEach { add(it.requiredCap) }
    }

    /** 이 루틴이 참조하는 장소 키. */
    fun places(): Set<PlaceKey> = buildSet {
        when (val t = trigger) {
            is Trigger.LocationEnter -> add(t.place)
            is Trigger.LocationExit -> add(t.place)
            else -> Unit
        }
    }
}

enum class Category(val wire: String) {
    COMMUTE("commute"), WEATHER("weather"), EV("ev"), FAMILY("family"), NIGHT("night"), ETC("etc");

    companion object {
        fun fromWire(v: String): Category? = entries.firstOrNull { it.wire == v }
    }
}

/** 장소 자리표시자. 좌표는 계약에 없고 기기의 places 표에만 있다. MVP 화면은 HOME·WORK만 노출(PM 결정 Q3). */
enum class PlaceKey(val wire: String) {
    HOME("home"), WORK("work"), CUSTOM1("custom1"), CUSTOM2("custom2"), CUSTOM3("custom3");

    val exposedInMvp: Boolean get() = this == HOME || this == WORK

    companion object {
        fun fromWire(v: String): PlaceKey? = entries.firstOrNull { it.wire == v }
    }
}

enum class EnergySource(val wire: String, val cap: Cap) {
    EV_BATTERY("ev_battery", Cap.EV_BATTERY), FUEL("fuel", Cap.FUEL_LEVEL);

    companion object {
        fun fromWire(v: String): EnergySource? = entries.firstOrNull { it.wire == v }
    }
}

/** 사건(trigger) 타입. UX ID: T1, T2, T4, T5. */
sealed interface Trigger {
    val type: String
    val requiredCap: Cap

    data class LocationEnter(val place: PlaceKey) : Trigger {
        override val type get() = "location_enter"
        override val requiredCap get() = Cap.LOCATION
    }

    data class LocationExit(val place: PlaceKey) : Trigger {
        override val type get() = "location_exit"
        override val requiredCap get() = Cap.LOCATION
    }

    data class EnergyBelow(val source: EnergySource, val percent: Int) : Trigger {
        override val type get() = "energy_below"
        override val requiredCap get() = source.cap
    }

    data object WiperOn : Trigger {
        override val type get() = "wiper_on"
        override val requiredCap get() = Cap.WIPER_STATE
    }
}

enum class TempOp(val wire: String) {
    BELOW("below"), ABOVE("above");

    companion object {
        fun fromWire(v: String): TempOp? = entries.firstOrNull { it.wire == v }
    }
}

/** 상태 조건(condition) 타입. UX ID: T3, T6. */
sealed interface Condition {
    val type: String
    val requiredCap: Cap

    /** end < start 이면 자정을 넘긴다. */
    data class TimeWindow(val days: Set<DayOfWeek>, val start: LocalTime, val end: LocalTime) : Condition {
        override val type get() = "time_window"
        override val requiredCap get() = Cap.TIME
        val crossesMidnight: Boolean get() = end < start
    }

    data class CabinTemp(val op: TempOp, val value: Int) : Condition {
        override val type get() = "cabin_temp"
        override val requiredCap get() = Cap.HVAC_CABIN_TEMP
    }
}

/** 동작 하나와 그 대체 동작(1단계만). fallback 안에 fallback을 둘 수 없다는 규칙을 타입으로 막는다. */
data class ActionSpec(val action: Action, val fallback: Action? = null)

enum class HvacTempMode(val wire: String) {
    SET("set"), DELTA("delta");

    companion object {
        fun fromWire(v: String): HvacTempMode? = entries.firstOrNull { it.wire == v }
    }
}

enum class AirMode(val wire: String) {
    RECIRC("recirc"), FRESH("fresh");

    companion object {
        fun fromWire(v: String): AirMode? = entries.firstOrNull { it.wire == v }
    }
}

enum class Seat(val wire: String) {
    DRIVER("driver"), PASSENGER("passenger");

    companion object {
        fun fromWire(v: String): Seat? = entries.firstOrNull { it.wire == v }
    }
}

enum class AmbientColor(val wire: String) {
    WARM("warm"), COOL("cool"), BLUE("blue"), RED("red");

    companion object {
        fun fromWire(v: String): AmbientColor? = entries.firstOrNull { it.wire == v }
    }
}

enum class AmbientBrightness(val wire: String) {
    LOW("low"), DEFAULT("default");

    companion object {
        fun fromWire(v: String): AmbientBrightness? = entries.firstOrNull { it.wire == v }
    }
}

/** 동작 타입. UX ID: A1~A9. */
sealed interface Action {
    val type: String
    val requiredCap: Cap

    data class HvacTemp(val mode: HvacTempMode, val value: Double) : Action {
        override val type get() = "hvac_temp"
        override val requiredCap get() = Cap.HVAC_TEMP
    }

    data class HvacFan(val level: Int) : Action {
        override val type get() = "hvac_fan"
        override val requiredCap get() = Cap.HVAC_FAN
    }

    data class HvacAir(val mode: AirMode) : Action {
        override val type get() = "hvac_air"
        override val requiredCap get() = Cap.HVAC_AIR
    }

    data class SeatHeater(val seat: Seat, val level: Int) : Action {
        override val type get() = "seat_heater"
        override val requiredCap get() = Cap.SEAT_HEATER
    }

    data class SteeringHeater(val on: Boolean) : Action {
        override val type get() = "steering_heater"
        override val requiredCap get() = Cap.STEERING_HEATER
    }

    data class MirrorHeater(val on: Boolean) : Action {
        override val type get() = "mirror_heater"
        override val requiredCap get() = Cap.MIRROR_HEATER
    }

    data class AmbientLight(
        val on: Boolean,
        val color: AmbientColor? = null,
        val brightness: AmbientBrightness? = null,
    ) : Action {
        override val type get() = "ambient_light"
        override val requiredCap get() = Cap.LIGHT_AMBIENT
    }

    data class Speak(val text: String) : Action {
        override val type get() = "speak"
        override val requiredCap get() = Cap.TTS
    }

    data object SuggestCharger : Action {
        override val type get() = "suggest_charger"
        override val requiredCap get() = Cap.NAVI_WAYPOINT
    }
}
