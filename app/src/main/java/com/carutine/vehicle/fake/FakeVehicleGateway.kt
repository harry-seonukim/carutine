package com.carutine.vehicle.fake

import com.carutine.engine.ActionResult
import com.carutine.engine.DeviceKey
import com.carutine.engine.DeviceValue
import com.carutine.engine.DriveState
import com.carutine.engine.VehicleEvent
import com.carutine.engine.VehicleGateway
import com.carutine.model.Action
import com.carutine.model.AirMode
import com.carutine.model.AmbientBrightness
import com.carutine.model.AmbientColor
import com.carutine.model.Cap
import com.carutine.model.EnergySource
import com.carutine.model.HvacTempMode
import com.carutine.model.Seat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * 가짜 차량. 단위 테스트, 에뮬레이터 없는 개발, 앱의 Mock 데모 모드(시나리오 재생)에 쓴다.
 * Pleos SDK를 전혀 참조하지 않는다.
 */
class FakeVehicleGateway(
    var caps: Set<Cap> = ALL_CAPS,
) : VehicleGateway {

    // ---- 차량 상태(테스트에서 직접 읽고 바꾼다) ----
    var hvacTemp = 22.0
    var fan = 2
    var air = AirMode.FRESH
    val seat = mutableMapOf(Seat.DRIVER to 0, Seat.PASSENGER to 0)
    var steering = false
    var mirror = false
    var ambient = DeviceValue.Ambient(on = false, color = AmbientColor.COOL, brightness = AmbientBrightness.DEFAULT)
    var cabinTemp: Double? = 20.0
    var waypoints = 0

    /** 이 동작 type은 실행 요청 시 실패한다(부분 실패 테스트). */
    val failTypes = mutableSetOf<String>()
    /** 이 장치는 현재값을 읽을 수 없다. */
    val unreadable = mutableSetOf<DeviceKey>()
    val applied = mutableListOf<Action>()

    private val events = MutableSharedFlow<VehicleEvent>(extraBufferCapacity = 64)

    override suspend fun capabilities(): Set<Cap> = caps
    override fun observe(): Flow<VehicleEvent> = events

    suspend fun emit(e: VehicleEvent) = events.emit(e)

    override suspend fun readCabinTemp(): Double? = if (Cap.HVAC_CABIN_TEMP in caps) cabinTemp else null

    override suspend fun readDevice(key: DeviceKey): DeviceValue? {
        if (key in unreadable) return null
        return when (key) {
            DeviceKey.HvacTemp -> DeviceValue.Temperature(hvacTemp)
            DeviceKey.HvacFan -> DeviceValue.Level(fan)
            DeviceKey.HvacAir -> DeviceValue.Air(air)
            is DeviceKey.SeatHeater -> DeviceValue.Level(seat.getValue(key.seat))
            DeviceKey.SteeringHeater -> DeviceValue.OnOff(steering)
            DeviceKey.MirrorHeater -> DeviceValue.OnOff(mirror)
            DeviceKey.AmbientLight -> ambient
        }
    }

    override suspend fun apply(action: Action): ActionResult {
        if (action.requiredCap !in caps) return ActionResult.Unsupported
        if (action.type in failTypes) return ActionResult.Failed("fake failure")
        applied += action
        when (action) {
            is Action.HvacTemp -> hvacTemp = if (action.mode == HvacTempMode.SET) action.value else hvacTemp + action.value
            is Action.HvacFan -> fan = action.level
            is Action.HvacAir -> air = action.mode
            is Action.SeatHeater -> seat[action.seat] = action.level
            is Action.SteeringHeater -> steering = action.on
            is Action.MirrorHeater -> mirror = action.on
            is Action.AmbientLight -> ambient = DeviceValue.Ambient(
                on = action.on,
                color = action.color ?: ambient.color,
                brightness = action.brightness ?: ambient.brightness,
            )
            is Action.Speak -> Unit
            Action.SuggestCharger -> waypoints++
        }
        return ActionResult.Success
    }

    companion object {
        val ALL_CAPS: Set<Cap> = Cap.entries.toSet()

        /** 옵션(열선·앰비언트)이 없는 기본 사양 EV. 템플릿의 fallback 검증용. */
        val BASIC_EV: Set<Cap> = setOf(
            Cap.LOCATION, Cap.TIME, Cap.EV_BATTERY, Cap.WIPER_STATE, Cap.HVAC_CABIN_TEMP,
            Cap.HVAC_TEMP, Cap.HVAC_FAN, Cap.HVAC_AIR, Cap.TTS, Cap.NAVI_WAYPOINT,
        )

        fun drive(state: DriveState) = VehicleEvent.Drive(state)
        fun battery(p: Double) = VehicleEvent.Energy(EnergySource.EV_BATTERY, p)
    }
}
