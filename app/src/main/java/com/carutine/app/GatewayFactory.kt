package com.carutine.app

import android.content.Context
import android.content.Intent
import com.carutine.engine.ActionResult
import com.carutine.engine.DeviceKey
import com.carutine.engine.DeviceValue
import com.carutine.engine.VehicleEvent
import com.carutine.engine.VehicleGateway
import com.carutine.model.Action
import com.carutine.model.Cap
import com.carutine.vehicle.fake.FakeVehicleGateway
import com.carutine.vehicle.pleos.PleosVehicleGateway
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

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

    /** 기기에 Pleos 차량 서비스가 있는지. 없으면 SDK 객체를 아예 만들지 않는다(SDK 내부 비동기 오류로 죽는 위험 차단). */
    fun hasVehicleService(context: Context): Boolean = try {
        context.packageManager.queryIntentServices(Intent(VEHICLE_SERVICE_ACTION), 0).isNotEmpty()
    } catch (t: Throwable) {
        false
    }

    private const val VEHICLE_SERVICE_ACTION = "ai.pleos.playground.vehicle.VEHICLE_START"
}

/** Pleos 연결. 서비스가 없거나 connect() 실패·예외면 null → 시뮬레이터. */
internal suspend fun createPleosGateway(context: Context): VehicleGateway? {
    if (!GatewayFactory.hasVehicleService(context)) return null
    var gw: PleosVehicleGateway? = null
    return try {
        val g = PleosVehicleGateway(context)
        gw = g
        if (g.connect()) g else { g.close(); null }
    } catch (t: Throwable) {
        runCatching { gw?.close() }
        null
    }
}

/**
 * observe()를 한 번만 수집해 여러 구독자(엔진, 화면의 주행 상태·위치)에 나눠 준다.
 * PleosVehicleGateway.observe()는 수집할 때마다 SDK 리스너를 등록하므로 직접 여러 번 수집하지 않는다.
 * [close]는 공유만 멈추고 내부 게이트웨이는 닫지 않는다(재연결 때 새 래퍼를 만든다).
 */
class SharedGateway(val inner: VehicleGateway, parent: CoroutineScope) : VehicleGateway {
    private val scope = CoroutineScope(parent.coroutineContext + Job(parent.coroutineContext[Job]))
    // Eagerly라 구독자(엔진·화면)가 붙기 전에 첫 값(와이퍼 OFF 기준선, 기어, 배터리)이 나갈 수 있다.
    // replay=0이면 그 기준선이 사라져 엔진이 첫 'ON'을 기준선으로 삼고 무시한다 → 최근 값을 순서대로 다시 준다.
    private val shared: SharedFlow<VehicleEvent> = inner.observe().shareIn(scope, SharingStarted.Eagerly, replay = REPLAY)

    override suspend fun capabilities(): Set<Cap> = inner.capabilities()
    override fun observe(): Flow<VehicleEvent> = shared
    override suspend fun readDevice(key: DeviceKey): DeviceValue? = inner.readDevice(key)
    override suspend fun readCabinTemp(): Double? = inner.readCabinTemp()
    override suspend fun apply(action: Action): ActionResult = inner.apply(action)

    fun stop() = scope.cancel()

    companion object {
        /** 구독 전 이벤트를 다시 줄 개수. 연결 직후 나오는 초기값(와이퍼·기어·배터리·연료·위치)보다 넉넉하게. */
        const val REPLAY = 16
    }
}
