package com.carutine.app

import android.content.Context
import com.carutine.engine.VehicleGateway
import com.carutine.vehicle.fake.FakeVehicleGateway

enum class VehicleMode { CONNECTING, REAL, SIMULATOR }

/**
 * 차량 연결 결정: 실제 차량(Pleos) 연결에 성공하면 '실제 차량 모드', 아니면 [FakeVehicleGateway]로 '시뮬레이터 모드'.
 * 현재 모드는 화면 상단 배지로 항상 보여 준다(심사 때 실제/시뮬레이터를 숨기지 않는다).
 */
object GatewayFactory {

    class Connection(val gateway: VehicleGateway, val mode: VehicleMode, val fake: FakeVehicleGateway?)

    suspend fun connect(context: Context): Connection {
        val real = try {
            createPleosGateway(context.applicationContext)
        } catch (t: Throwable) {
            null   // SDK 서비스가 없는 기기(일반 에뮬레이터·폰)에서도 앱은 시뮬레이터로 뜬다
        }
        if (real != null) return Connection(real, VehicleMode.REAL, null)
        val fake = FakeVehicleGateway()
        return Connection(fake, VehicleMode.SIMULATOR, fake)
    }
}

/**
 * TODO(PM merge): fe/pleos-gateway 브랜치를 합칠 때 이 함수만 채운다. 예:
 *   val g = com.carutine.vehicle.pleos.PleosVehicleGateway(context)
 *   return if (g.connect()) g else { g.close(); null }
 * (앱 종료 시 close는 AppController.close()에서 AutoCloseable이면 부른다.)
 */
@Suppress("UNUSED_PARAMETER", "RedundantSuspendModifier")
internal suspend fun createPleosGateway(context: Context): VehicleGateway? = null
