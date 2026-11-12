package com.carutine.spike

import ai.pleos.playground.vehicle.Vehicle
import ai.pleos.playground.vehicle.api.model.area.DoorArea
import ai.pleos.playground.vehicle.api.model.capability.AccessType
import ai.pleos.playground.vehicle.api.model.capability.AreaBounds
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.area.DoorValue
import ai.pleos.playground.vehicle.constant.unit.TemperatureUnit
import ai.pleos.playground.vehicle.listener.DoorPositionListener
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.carutine.spike.ui.theme.CarutineSpikeTheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * V1 검증 화면: Pleos 에뮬레이터에서 Vehicle SDK로 차량 값을 읽는다.
 * SDK 2.3.2 소스(Vehicle-2.3.2-sources.jar)의 시그니처로 확인한 API만 쓴다.
 * (공식 Callback 가이드 예시는 area 인자가 빠져 있어 실제 시그니처와 다르다.)
 *  - Door Position: Virtual Console > Convenience > Door Position 카드로 값을 바꿔 확인
 *  - 온도 표시 단위: Display 도메인 조회
 * V12용으로 현재 창 크기(dp)도 표시한다.
 */
class MainActivity : ComponentActivity() {

    private var vehicle: Vehicle? = null
    private val area = DoorArea(DoorValue.ROW_1_LEFT)

    private val status = mutableStateOf("초기화 전")
    private val support = mutableStateOf("-")
    private val doorValue = mutableStateOf("-")
    private val tempUnit = mutableStateOf("-")
    private val logs = mutableStateListOf<String>()

    private val doorListener = object : DoorPositionListener {
        override fun onPositionUpdated(pos: Int?) {
            ui { doorValue.value = "$pos (실시간)"; log("registerDoorPosition 업데이트: $pos") }
        }

        override fun onFailed(e: Exception) {
            ui { log("registerDoorPosition 실패: ${describe(e)}") }
        }
    }
    private var subscribed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarutineSpikeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { inner ->
                    ProbeScreen(Modifier.padding(inner))
                }
            }
        }
        initVehicle()
    }

    override fun onDestroy() {
        unsubscribe()
        vehicle?.release()
        vehicle = null
        super.onDestroy()
    }

    // ---------------------------------------------------------------- SDK 호출

    private fun initVehicle() {
        try {
            val v = Vehicle(this)
            v.initialize()
            vehicle = v
            status.value = "initialize() 반환됨 (서비스 연결 완료를 뜻하지는 않음)"
            log("Vehicle.initialize() OK")
        } catch (e: Exception) {
            status.value = "initialize() 예외: ${e.message}"
            log("Vehicle.initialize() 예외: ${describe(e)}")
        }
    }

    private fun checkSupport() {
        val door = vehicle?.getDoor() ?: return log("Vehicle 없음")
        door.checkDoorPositionCapabilities(
            onComplete = { config: CapabilityConfig, areaMap: Map<DoorArea, AreaBounds<Int>> ->
                val bounds = areaMap[area]
                ui {
                    support.value = "accessType=${config.accessType}, ROW_1_LEFT 범위=${bounds?.minValue}..${bounds?.maxValue}"
                    log("checkDoorPositionCapabilities OK: ${support.value}")
                    if (config.accessType == AccessType.READ || config.accessType == AccessType.READ_WRITE) readDoor()
                }
            },
            onFailed = { e: Exception -> ui { support.value = "실패"; log("checkDoorPositionCapabilities 실패: ${describe(e)}") } },
        )
    }

    private fun readDoor() {
        val door = vehicle?.getDoor() ?: return log("Vehicle 없음")
        door.getCurrentDoorPosition(
            area = area,
            onComplete = { pos: Int? -> ui { doorValue.value = "$pos"; log("getCurrentDoorPosition: $pos") } },
            onFailed = { e: Exception -> ui { log("getCurrentDoorPosition 실패: ${describe(e)}") } },
        )
    }

    private fun readTempUnit() {
        val display = vehicle?.getDisplay() ?: return log("Vehicle 없음")
        display.getCurrentTemperatureDisplayUnit(
            onComplete = { unit: TemperatureUnit? -> ui { tempUnit.value = "$unit"; log("getCurrentTemperatureDisplayUnit: $unit") } },
            onFailed = { e: Exception -> ui { log("getCurrentTemperatureDisplayUnit 실패: ${describe(e)}") } },
        )
    }

    private fun subscribe() {
        val door = vehicle?.getDoor() ?: return log("Vehicle 없음")
        if (subscribed) return
        door.registerDoorPosition(area, doorListener)
        subscribed = true
        log("registerDoorPosition 호출 반환 (서비스 쪽 완료 확인은 아님)")
    }

    private fun unsubscribe() {
        if (!subscribed) return
        vehicle?.getDoor()?.unregisterDoorPosition(area, doorListener)
        subscribed = false
        log("unregisterDoorPosition 호출")
    }

    // ---------------------------------------------------------------- helpers

    /**
     * SDK 2.3.2 배포본(AAR)에서는 VehicleException이 난독화돼(`callback.exception.a`) 앱에서 참조할 수 없다.
     * 메시지에 ErrorCode 이름(예: ERROR_SECURITY_PERMISSION)이 들어 있으므로 클래스 이름과 메시지만 남긴다.
     */
    private fun describe(e: Exception): String = "${e::class.java.name}: ${e.message}"

    private fun ui(block: () -> Unit) = runOnUiThread(block)

    private fun log(msg: String) {
        val line = "${LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))}  $msg"
        Log.i(TAG, msg)
        runOnUiThread { logs.add(0, line) }
    }

    // ---------------------------------------------------------------- UI

    @Composable
    private fun ProbeScreen(modifier: Modifier) {
        val cfg = LocalConfiguration.current
        Column(
            modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Carutine V1 검증", style = MaterialTheme.typography.headlineMedium)
            Row2("SDK", status.value)
            Row2("창 크기(V12)", "${cfg.screenWidthDp} x ${cfg.screenHeightDp} dp, orientation=${cfg.orientation}")
            Row2("Door Position 지원", support.value)
            Row2("Door Position 값", doorValue.value)
            Row2("온도 표시 단위", tempUnit.value)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { checkSupport() }) { Text("지원 확인 + 읽기") }
                Button(onClick = { readTempUnit() }) { Text("온도 단위 읽기") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { subscribe() }) { Text("실시간 구독") }
                Button(onClick = { unsubscribe() }) { Text("구독 해제") }
                Button(onClick = { vehicle?.release(); vehicle = null; initVehicle() }) { Text("재초기화") }
            }
            Text("API 기록 (최신이 위)", style = MaterialTheme.typography.titleMedium)
            logs.forEach { Text(it, style = MaterialTheme.typography.bodySmall) }
        }
    }

    @Composable
    private fun Row2(label: String, value: String) {
        Column {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }

    companion object { private const val TAG = "CarutineV1" }
}
