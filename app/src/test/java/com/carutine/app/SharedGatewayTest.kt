package com.carutine.app

import com.carutine.engine.ActionResult
import com.carutine.engine.DeviceKey
import com.carutine.engine.DeviceValue
import com.carutine.engine.VehicleEvent
import com.carutine.engine.VehicleGateway
import com.carutine.model.Action
import com.carutine.model.Cap
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Test

class SharedGatewayTest {

    /** 구독되자마자 기준선(와이퍼 OFF)과 기어를 내보내는 실제 차량 게이트웨이 흉내. */
    private class EagerInner : VehicleGateway {
        override suspend fun capabilities(): Set<Cap> = setOf(Cap.WIPER_STATE)
        override fun observe(): Flow<VehicleEvent> = flow {
            emit(VehicleEvent.Wiper(false))
            emit(VehicleEvent.Drive(com.carutine.engine.DriveState.PARKED))
            awaitCancellation()
        }
        override suspend fun readDevice(key: DeviceKey): DeviceValue? = null
        override suspend fun readCabinTemp(): Double? = null
        override suspend fun apply(action: Action): ActionResult = ActionResult.Unsupported
    }

    @Test
    fun 엔진이_늦게_구독해도_첫_값_기준선을_받는다() = runBlocking {
        val g = SharedGateway(EagerInner(), this)
        delay(100)   // 공유 수집이 먼저 돌아 첫 값이 이미 나간 뒤
        val got = withTimeout(2000) { g.observe().take(2).toList() }
        assertEquals(listOf(VehicleEvent.Wiper(false), VehicleEvent.Drive(com.carutine.engine.DriveState.PARKED)), got)
        g.stop()
    }
}
