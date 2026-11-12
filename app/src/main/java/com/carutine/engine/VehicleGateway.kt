package com.carutine.engine

import com.carutine.model.Action
import com.carutine.model.AirMode
import com.carutine.model.AmbientBrightness
import com.carutine.model.AmbientColor
import com.carutine.model.Cap
import com.carutine.model.EnergySource
import com.carutine.model.Seat
import kotlinx.coroutines.flow.Flow

/**
 * 엔진이 차량을 보는 유일한 창구. Pleos SDK 의존 코드는 `com.carutine.vehicle` 패키지의 구현체에만 두고,
 * `com.carutine.model`·`com.carutine.engine`은 Android·Pleos 클래스를 import하지 않는다(JVM 단위 테스트 가능).
 * 작업 문서 3장 스케치에서 read(cap)를 장치 단위(readDevice)와 센서 단위(readCabinTemp)로 나눴다
 * (시트 열선처럼 좌석별 값이 필요해서). 루틴 데이터 계약과는 무관한 내부 인터페이스다.
 */
interface VehicleGateway {
    /** 차종별 지원 기능. 앱 시작 시 한 번 읽어 캐시한다(각 도메인 checkXxxCapabilities 결과). */
    suspend fun capabilities(): Set<Cap>

    /** 상태 변경 스트림(위치, 배터리·연료, 와이퍼, 주행 상태). 폴링이 아니라 SDK 콜백을 흘려보낸다. */
    fun observe(): Flow<VehicleEvent>

    /** 스냅샷·되돌리기용 현재값. 읽을 수 없으면 null. */
    suspend fun readDevice(key: DeviceKey): DeviceValue?

    /** T6 조건 평가용 실내 온도(℃). 읽을 수 없으면 null → 조건 불만족. */
    suspend fun readCabinTemp(): Double?

    /**
     * 동작 하나를 실행한다. 엔진은 hvac_temp delta를 절대값(SET)으로 바꿔서 넘긴다.
     * Speak는 엔진이 넘기지 않는다(알림 문장에 합친다). SuggestCharger는 '경유지 추가'로 처리한다.
     */
    suspend fun apply(action: Action): ActionResult
}

sealed interface ActionResult {
    data object Success : ActionResult
    data object Unsupported : ActionResult
    data class Failed(val reason: String) : ActionResult
}

/** 루틴이 바꾸는 장치. 충돌 해소와 스냅샷의 키다(동작 type + 좌석 같은 대상 파라미터). */
sealed interface DeviceKey {
    data object HvacTemp : DeviceKey
    data object HvacFan : DeviceKey
    data object HvacAir : DeviceKey
    data class SeatHeater(val seat: Seat) : DeviceKey
    data object SteeringHeater : DeviceKey
    data object MirrorHeater : DeviceKey
    data object AmbientLight : DeviceKey

    companion object {
        /** 장치를 바꾸지 않는 동작(speak, suggest_charger)은 null. */
        fun of(action: Action): DeviceKey? = when (action) {
            is Action.HvacTemp -> HvacTemp
            is Action.HvacFan -> HvacFan
            is Action.HvacAir -> HvacAir
            is Action.SeatHeater -> SeatHeater(action.seat)
            is Action.SteeringHeater -> SteeringHeater
            is Action.MirrorHeater -> MirrorHeater
            is Action.AmbientLight -> AmbientLight
            is Action.Speak, Action.SuggestCharger -> null
        }
    }
}

sealed interface DeviceValue {
    data class Temperature(val celsius: Double) : DeviceValue
    data class Level(val level: Int) : DeviceValue
    data class Air(val mode: AirMode) : DeviceValue
    data class OnOff(val on: Boolean) : DeviceValue
    data class Ambient(val on: Boolean, val color: AmbientColor?, val brightness: AmbientBrightness?) : DeviceValue
}

enum class DriveState { PARKED, CHARGING, DRIVING, UNKNOWN;
    /** 작업 문서 7장: UNKNOWN은 DRIVING으로 취급한다. */
    val restrictsUi: Boolean get() = this == DRIVING || this == UNKNOWN
}

/** 게이트웨이가 흘려보내는 원시 상태 값. 사건 판정은 [TriggerDetector]가 한다. */
sealed interface VehicleEvent {
    data class Location(val lat: Double, val lng: Double) : VehicleEvent
    data class Energy(val source: EnergySource, val percent: Double) : VehicleEvent
    data class Wiper(val on: Boolean) : VehicleEvent
    data class Drive(val state: DriveState) : VehicleEvent
}
