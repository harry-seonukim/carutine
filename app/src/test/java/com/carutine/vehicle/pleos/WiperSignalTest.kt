package com.carutine.vehicle.pleos

import com.carutine.engine.EngineConfig
import com.carutine.engine.TriggerDetector
import com.carutine.engine.TriggerEvent
import com.carutine.engine.VehicleEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** 와이퍼: 기준선(getCurrent) → 리스너 콜백 → 주기 읽기 흐름을 가짜 콜백으로 고정한다. */
class WiperSignalTest {

    private val sent = mutableListOf<VehicleEvent>()
    private val logs = mutableListOf<String>()
    private val signal = WiperSignal(log = { logs += it }, send = { sent += it; true })
    private val detector = TriggerDetector(EngineConfig())

    private fun triggers(): List<TriggerEvent> = sent.flatMap { detector.onEvent(it, emptyMap()) }

    @Test
    fun 기준선_OFF_뒤_첫_ON이_와이퍼_켜짐_사건이_된다() {
        signal.onRaw("기준선", "OFF")          // 등록 직후 getCurrentWiperState
        signal.onRaw("콜백", "ON")             // Virtual Console에서 켬
        assertEquals(listOf(VehicleEvent.Wiper(false), VehicleEvent.Wiper(true)), sent)
        assertEquals(listOf(TriggerEvent.WiperTurnedOn), triggers())
    }

    @Test
    fun 같은_값은_다시_방출하지_않는다_콜백과_주기읽기_중복() {
        signal.onRaw("기준선", "OFF")
        signal.onRaw("콜백", "ON")
        signal.onRaw("주기 읽기", "ON", quiet = true)
        signal.onRaw("주기 읽기", "ON", quiet = true)
        assertEquals(2, sent.size)
        assertEquals(1, triggers().size)
    }

    @Test
    fun 리스너가_안_와도_주기_읽기로_켜짐을_잡는다() {
        signal.onRaw("기준선", "OFF")
        signal.onRaw("주기 읽기", "OFF", quiet = true)
        signal.onRaw("주기 읽기", "ON", quiet = true)
        assertEquals(listOf(TriggerEvent.WiperTurnedOn), triggers())
    }

    @Test
    fun 기준선이_없으면_첫_ON은_기준선이_된다_엔진_규칙() {
        signal.onRaw("기준선", null)           // 읽기 실패
        assertTrue(sent.isEmpty())
        signal.onRaw("콜백", "ON")             // 기준선이 됨(사건 아님)
        signal.onRaw("콜백", "OFF")
        signal.onRaw("콜백", "ON")             // 이제 사건
        assertEquals(listOf(TriggerEvent.WiperTurnedOn), triggers())
    }

    @Test
    fun OTHER면_스위치로_보조_판단() {
        assertTrue(signal.needsSwitch("OTHER"))
        assertTrue(signal.needsSwitch(null))
        assertEquals(false, signal.onRaw("기준선", "OTHER", "OFF"))
        assertEquals(true, signal.onRaw("콜백", "OTHER", "INTERMITTENT_LEVEL_2"))
        assertEquals(listOf(TriggerEvent.WiperTurnedOn), triggers())
        assertNull(signal.onRaw("콜백", "OTHER", "OTHER"))
    }

    @Test
    fun SERVICE는_꺼짐() {
        assertEquals(false, signal.onRaw("기준선", "SERVICE"))
        assertEquals(true, signal.onRaw("콜백", "ON"))
        assertEquals(listOf(TriggerEvent.WiperTurnedOn), triggers())
    }
}
