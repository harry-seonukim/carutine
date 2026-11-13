package com.carutine.vehicle.pleos

import com.carutine.engine.DriveState
import com.carutine.model.AmbientBrightness
import com.carutine.model.AmbientColor
import com.carutine.vehicle.pleos.PleosMapping.TempRange
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PleosMappingTest {

    @Test
    fun clampInt_범위_안팎과_뒤집힌_범위() {
        assertEquals(1, PleosMapping.clampInt(0, 1, 8))   // 팬 0 → 최저 단계
        assertEquals(8, PleosMapping.clampInt(10, 1, 8))
        assertEquals(5, PleosMapping.clampInt(5, 1, 8))
        assertEquals(8, PleosMapping.clampInt(10, 8, 1))
    }

    @Test
    fun heaterLevel_음수_냉각_범위는_쓰지_않는다() {
        assertEquals(0, PleosMapping.heaterLevel(0, -3, 3))
        assertEquals(2, PleosMapping.heaterLevel(2, -3, 3))
        assertEquals(3, PleosMapping.heaterLevel(5, -3, 3))
        assertEquals(1, PleosMapping.heaterLevel(0, 1, 3))  // 0이 범위 밖이면 최저 단계
        assertEquals(2, PleosMapping.heaterLevel(3, 0, 2))
    }

    @Test
    fun heaterOnOff_켜기는_최대_끄기는_0() {
        assertEquals(3, PleosMapping.heaterOnOffValue(true, 0, 3))
        assertEquals(0, PleosMapping.heaterOnOffValue(false, 0, 3))
        assertEquals(1, PleosMapping.heaterOnOffValue(true, 0, 1))
        assertEquals(0, PleosMapping.heaterOnOffValue(false, -2, 2))
        assertEquals(true, PleosMapping.heaterIsOn(2))
        assertEquals(false, PleosMapping.heaterIsOn(0))
        assertEquals(false, PleosMapping.heaterIsOn(-1))
        assertNull(PleosMapping.heaterIsOn(null))
        assertEquals(0, PleosMapping.seatLevelFromValue(-2))
    }

    @Test
    fun clampTemp_범위와_증분() {
        val r = TempRange(17.0, 27.0, 0.5)
        assertEquals(17.0, PleosMapping.clampTemp(16.0, r), 1e-9)
        assertEquals(27.0, PleosMapping.clampTemp(30.0, r), 1e-9)
        assertEquals(22.5, PleosMapping.clampTemp(22.4, r), 1e-9)
        assertEquals(22.0, PleosMapping.clampTemp(22.2, r), 1e-9)
        // 증분 없음 → 반올림 없이 clamp만
        assertEquals(22.3, PleosMapping.clampTemp(22.3, TempRange(16.0, 30.0, 0.0)), 1e-9)
    }

    @Test
    fun 앰비언트_색_왕복_매핑() {
        AmbientColor.entries.forEach { c ->
            val argb = PleosMapping.ambientArgb(c)
            assertEquals(0xFF, (argb ushr 24) and 0xFF)  // 알파 고정
            assertEquals(c, PleosMapping.nearestAmbientColor(argb))
        }
        assertEquals(AmbientColor.RED, PleosMapping.nearestAmbientColor(0xFFDD0000.toInt()))
        assertEquals(AmbientColor.WARM, PleosMapping.nearestAmbientColor(0xFFFFAA00.toInt()))
        assertNull(PleosMapping.nearestAmbientColor(null))
    }

    @Test
    fun 앰비언트_밝기_매핑() {
        assertEquals(3, PleosMapping.brightnessValue(AmbientBrightness.LOW, 0, 10))
        assertEquals(7, PleosMapping.brightnessValue(AmbientBrightness.DEFAULT, 0, 10))
        assertEquals(1, PleosMapping.brightnessValue(AmbientBrightness.LOW, 0, 1))   // 0(꺼짐)으로 내려가지 않음
        assertEquals(5, PleosMapping.brightnessValue(AmbientBrightness.LOW, 5, 5))
        assertEquals(AmbientBrightness.LOW, PleosMapping.brightnessFromValue(3, 0, 10))
        assertEquals(AmbientBrightness.DEFAULT, PleosMapping.brightnessFromValue(7, 0, 10))
        AmbientBrightness.entries.forEach { b ->
            assertEquals(b, PleosMapping.brightnessFromValue(PleosMapping.brightnessValue(b, 1, 20), 1, 20))
        }
        assertNull(PleosMapping.brightnessFromValue(null, 0, 10))
    }

    @Test
    fun 기어_주행상태_매핑() {
        assertEquals(DriveState.PARKED, PleosMapping.driveStateForGear("GEAR_PARK"))
        assertEquals(DriveState.DRIVING, PleosMapping.driveStateForGear("GEAR_DRIVE"))
        assertEquals(DriveState.DRIVING, PleosMapping.driveStateForGear("GEAR_REVERSE"))
        assertEquals(DriveState.DRIVING, PleosMapping.driveStateForGear("GEAR_NEUTRAL"))
        assertEquals(DriveState.DRIVING, PleosMapping.driveStateForGear("GEAR_3"))
        assertEquals(DriveState.UNKNOWN, PleosMapping.driveStateForGear("GEAR_UNKNOWN"))
        assertEquals(DriveState.UNKNOWN, PleosMapping.driveStateForGear(null))
        assertTrue(PleosMapping.driveStateForGear(null).restrictsUi)
    }

    @Test
    fun 와이퍼_매핑() {
        assertEquals(true, PleosMapping.wiperOn("ON"))
        assertEquals(false, PleosMapping.wiperOn("OFF"))
        assertEquals(false, PleosMapping.wiperOn("SERVICE"))
        assertNull(PleosMapping.wiperOn("OTHER"))
        assertNull(PleosMapping.wiperOn(null))
    }

    @Test
    fun pickArea_선호_영역_우선() {
        assertEquals("b", PleosMapping.pickArea(listOf("a", "b")) { it == "b" })
        assertEquals("a", PleosMapping.pickArea(listOf("a", "c")) { it == "b" })
        assertNull(PleosMapping.pickArea(emptyList<String>()) { true })
    }
}
