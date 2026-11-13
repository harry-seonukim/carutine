package com.carutine.vehicle.pleos

import com.carutine.engine.DriveState
import com.carutine.model.AmbientBrightness
import com.carutine.model.AmbientColor
import kotlin.math.roundToInt

/**
 * 루틴 값 ↔ Pleos Vehicle SDK 값 변환. Android·Pleos 클래스를 import하지 않는 순수 함수만 둔다(JVM 단위 테스트 대상).
 * SDK 열거형은 이름 문자열(`VehicleGear.name` 등)로 넘겨받는다.
 */
object PleosMapping {

    /** 온도 설정 범위(℃). step은 차량이 받는 증분(HvacTemperatureConfigArray.incrementCelsius). 0 이하면 반올림하지 않는다. */
    data class TempRange(val min: Double, val max: Double, val step: Double)

    /** 정수 범위 clamp. min > max로 들어와도(설정값 이상) 뒤집어서 처리한다. */
    fun clampInt(value: Int, min: Int, max: Int): Int {
        val lo = minOf(min, max)
        val hi = maxOf(min, max)
        return value.coerceIn(lo, hi)
    }

    /**
     * 열선 단계 clamp. AAOS 규약상 HVAC_SEAT_TEMPERATURE·HVAC_STEERING_WHEEL_HEAT 음수는 '통풍/냉각'이라
     * 루틴의 열선 단계(0 이상)는 [max(0, min), max] 안으로만 보낸다.
     */
    fun heaterLevel(level: Int, min: Int, max: Int): Int {
        val hi = maxOf(min, max)
        val lo = maxOf(minOf(min, max), 0).coerceAtMost(hi)
        return level.coerceIn(lo, hi)
    }

    /** 켜기/끄기만 있는 열선(스티어링·미러): 켜기 = 최대 단계, 끄기 = 0(범위 안으로 clamp). */
    fun heaterOnOffValue(on: Boolean, min: Int, max: Int): Int =
        if (on) maxOf(min, max) else clampInt(0, min, max)

    /** 읽은 열선 값 → 켜짐 여부. 0 이하(꺼짐·냉각)는 꺼짐으로 본다. */
    fun heaterIsOn(value: Int?): Boolean? = value?.let { it > 0 }

    /** 읽은 시트 값 → 루틴 단계(0 이상). 냉각(음수)은 0으로 본다. */
    fun seatLevelFromValue(value: Int?): Int? = value?.let { maxOf(it, 0) }

    /** 설정 온도를 범위 안으로 넣고 차량 증분에 맞춘다(min 기준 반올림). */
    fun clampTemp(value: Double, range: TempRange): Double {
        val lo = minOf(range.min, range.max)
        val hi = maxOf(range.min, range.max)
        var v = value.coerceIn(lo, hi)
        if (range.step > 0.0) {
            v = lo + ((v - lo) / range.step).roundToInt() * range.step
            v = v.coerceIn(lo, hi)
        }
        return (v * 100).roundToInt() / 100.0
    }

    // ---------------------------------------------------------------- 앰비언트

    /**
     * 앰비언트 색 → SDK `setAmbientLightBrightnessColor` 값(32-bit ARGB, 알파 0xFF 고정).
     *
     * | 루틴 color | ARGB        | 의도                 |
     * |-----------|-------------|----------------------|
     * | warm      | 0xFFFF9933  | 주황빛 따뜻한 색      |
     * | cool      | 0xFFCCE6FF  | 푸른빛 도는 흰색      |
     * | blue      | 0xFF1E5BFF  | 진한 파랑            |
     * | red       | 0xFFFF2A2A  | 빨강                 |
     */
    fun ambientArgb(color: AmbientColor): Int = when (color) {
        AmbientColor.WARM -> 0xFFFF9933.toInt()
        AmbientColor.COOL -> 0xFFCCE6FF.toInt()
        AmbientColor.BLUE -> 0xFF1E5BFF.toInt()
        AmbientColor.RED -> 0xFFFF2A2A.toInt()
    }

    /** 읽은 ARGB → 가장 가까운 루틴 색(RGB 거리). 되돌리기 스냅샷용이라 정확한 색이 아니라 근사다. */
    fun nearestAmbientColor(argb: Int?): AmbientColor? {
        if (argb == null) return null
        val r = (argb shr 16) and 0xFF
        val g = (argb shr 8) and 0xFF
        val b = argb and 0xFF
        return AmbientColor.entries.minByOrNull { c ->
            val t = ambientArgb(c)
            val dr = r - ((t shr 16) and 0xFF)
            val dg = g - ((t shr 8) and 0xFF)
            val db = b - (t and 0xFF)
            dr * dr + dg * dg + db * db
        }
    }

    /**
     * 앰비언트 밝기 → SDK 값. 범위 [min, max] 기준
     * low = min + 30%, default = min + 70% (반올림). 범위 폭이 1 이상이면 low는 최소 min+1(0=꺼짐 방지).
     */
    fun brightnessValue(b: AmbientBrightness, min: Int, max: Int): Int {
        val lo = minOf(min, max)
        val hi = maxOf(min, max)
        val span = hi - lo
        val ratio = if (b == AmbientBrightness.LOW) 0.3 else 0.7
        var v = lo + (span * ratio).roundToInt()
        if (span >= 1) v = v.coerceAtLeast(lo + 1)
        return v.coerceIn(lo, hi)
    }

    /** 읽은 밝기 → 루틴 밝기. 범위 중간 이하면 low, 초과면 default. */
    fun brightnessFromValue(value: Int?, min: Int, max: Int): AmbientBrightness? {
        if (value == null) return null
        val lo = minOf(min, max)
        val hi = maxOf(min, max)
        return if (value <= lo + (hi - lo) / 2.0) AmbientBrightness.LOW else AmbientBrightness.DEFAULT
    }

    // ---------------------------------------------------------------- 주행 상태·와이퍼

    /**
     * 기어(`VehicleGear.name`) → 주행 상태. 작업 문서 7장 원칙(모르면 주행 취급)에 따라
     * P만 PARKED, D·R·N·1~9단은 DRIVING, GEAR_UNKNOWN·null·처음 보는 값은 UNKNOWN.
     * (충전 상태 CHARGING은 이번 범위에서 판정하지 않는다.)
     */
    fun driveStateForGear(gearName: String?): DriveState = when {
        gearName == null -> DriveState.UNKNOWN
        gearName == "GEAR_PARK" -> DriveState.PARKED
        gearName == "GEAR_DRIVE" || gearName == "GEAR_REVERSE" || gearName == "GEAR_NEUTRAL" -> DriveState.DRIVING
        gearName.matches(Regex("GEAR_[1-9]")) -> DriveState.DRIVING
        else -> DriveState.UNKNOWN
    }

    /** 와이퍼 상태(`WiperState.name`) → 작동 여부. ON=true, OFF·SERVICE=false, OTHER·null=판단 안 함(null). */
    fun wiperOn(stateName: String?): Boolean? = when (stateName) {
        "ON" -> true
        "OFF", "SERVICE" -> false
        else -> null
    }

    /** 영역 목록에서 조건에 맞는 첫 영역, 없으면 첫 영역(운전석·앞유리 우선 선택용). */
    fun <A> pickArea(areas: Collection<A>, preferred: (A) -> Boolean): A? =
        areas.firstOrNull(preferred) ?: areas.firstOrNull()
}
