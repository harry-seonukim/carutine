package com.carutine.vehicle.pleos

import ai.pleos.playground.vehicle.Vehicle
import ai.pleos.playground.vehicle.api.model.area.MirrorArea
import ai.pleos.playground.vehicle.api.model.area.SeatArea
import ai.pleos.playground.vehicle.api.model.area.WindowArea
import ai.pleos.playground.vehicle.api.model.capability.AccessType
import ai.pleos.playground.vehicle.api.model.capability.AreaBounds
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.api.model.capability.HvacTemperatureConfigArray
import ai.pleos.playground.vehicle.constant.area.SeatValue
import ai.pleos.playground.vehicle.constant.area.WindowValue
import ai.pleos.playground.vehicle.constant.control.LightSwitch
import ai.pleos.playground.vehicle.constant.control.VehicleGear
import ai.pleos.playground.vehicle.constant.control.WiperState
import ai.pleos.playground.vehicle.constant.control.WiperSwitch
import ai.pleos.playground.vehicle.constant.unit.TemperatureUnit
import ai.pleos.playground.vehicle.listener.EvBatteryStateOfChargeListener
import ai.pleos.playground.vehicle.listener.FuelPercentListener
import ai.pleos.playground.vehicle.listener.GearSelectionListener
import ai.pleos.playground.vehicle.listener.WiperStateListener
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import android.os.SystemClock
import android.util.Log
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
import com.carutine.vehicle.pleos.PleosMapping.TempRange
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Pleos Vehicle SDK 2.3.2 기반 [VehicleGateway]. API·권한 이름은 SDK 소스(Vehicle-2.3.2-sources)에서 확인한 것만 쓴다.
 * Cap ↔ SDK API ↔ 권한 표는 `docs/pleos-sdk-mapping.md`.
 *
 * 사용법(앱):
 * ```
 * val gw = PleosVehicleGateway(context)
 * if (gw.connect()) use(gw) else { gw.close(); use(FakeVehicleGateway(...)) }   // 시뮬레이터 전환
 * ```
 * - [connect] 성공 시 capability가 캐시되고, [capabilities]는 캐시를 돌려준다.
 * - [observe]는 구독할 때마다 SDK 리스너를 등록하고, 수집이 끝나면(awaitClose) 해제한다.
 * - 모든 SDK 호출은 try/catch + 타임아웃으로 감싸고 [PleosDiagnostics]에 기록한다.
 * - SDK 2.3.2 AAR에서 VehicleException은 난독화돼 있으므로 `Exception`으로만 받는다.
 *
 * [apply]의 성공 기준: SDK set의 onComplete는 서비스가 요청을 받았다는 뜻일 뿐(setPropertyAsync 완료) 값 반영을 보장하지 않는다.
 * 그래서 set 뒤 [VERIFY_INTERVAL_MS] 간격으로 최대 [VERIFY_TRIES]번 다시 읽어 목표값이 확인될 때만 Success,
 * 끝내 다르면 `Failed("차량에 반영되지 않음(현재값 X)")`. 읽기 권한이 없는(WRITE 전용) 항목만 확인을 생략한다.
 * 공조(온도·팬·내외기)는 실행 전에 HVAC 전원(HVAC_POWER_ON)을 확인하고 꺼져 있으면 켠다.
 *
 * 차량 값 매핑 요약:
 * - 영역: 항상 check가 돌려준 area 객체를 그대로 쓴다(area id = 좌석 비트 OR이라 임의로 만든 area는 서비스가 다르게 본다).
 *   hvac.temp/fan/air·미러: 돌려준 모든 영역에 같은 값. 읽기는 운전석(ROW_1_LEFT 포함) 영역.
 * - 온도: ℃, HvacTemperatureConfigArray(min/max/incrementCelsius)가 있으면 그 범위·증분, 없으면 AreaBounds로 clamp.
 * - 팬: 루틴 0..10을 차량 AreaBounds로 clamp(0이 범위 밖이면 최저 단계, 팬을 끄지는 않는다).
 * - 시트 열선: driver=ROW_1_LEFT, passenger=ROW_1_RIGHT(좌핸들 가정). 단계는 [max(0,min), max]로 clamp.
 * - 스티어링·미러 열선: on=최대 단계, off=0.
 * - 앰비언트: on/off=setAmbientLightSwitch(ON/OFF). 색·밝기 매핑은 [PleosMapping.ambientArgb], [PleosMapping.brightnessValue] KDoc 표.
 * - 와이퍼: WiperState ON=on, OFF·SERVICE=off, OTHER면 WiperSwitch로 보조 판단([PleosMapping.wiperSwitchOn]).
 */
@SuppressLint("MissingPermission")
class PleosVehicleGateway(context: Context) : VehicleGateway, AutoCloseable {

    private val appContext: Context = context.applicationContext ?: context

    @Volatile private var vehicle: Vehicle? = null
    @Volatile private var cachedCaps: Set<Cap>? = null
    private val probeLock = Mutex()

    // ---- capability 조회 결과(영역·범위) ----
    @Volatile private var tempAreas: Map<SeatArea, AreaBounds<Float>> = emptyMap()
    @Volatile private var tempRange: TempRange? = null
    @Volatile private var tempReadable = false
    @Volatile private var fanAreas: Map<SeatArea, AreaBounds<Int>> = emptyMap()
    @Volatile private var fanReadable = false
    @Volatile private var recircAreas: List<SeatArea> = emptyList()
    @Volatile private var recircReadable = false
    @Volatile private var powerAreas: List<SeatArea> = emptyList()
    @Volatile private var powerReadable = false
    @Volatile private var powerWritable = false
    @Volatile private var cabinArea: SeatArea? = null
    @Volatile private var seatAreas: Map<SeatArea, AreaBounds<Int>> = emptyMap()
    @Volatile private var seatReadable = false
    @Volatile private var steeringBounds: AreaBounds<Int>? = null
    @Volatile private var steeringReadable = false
    @Volatile private var mirrorAreas: Map<MirrorArea, AreaBounds<Int>> = emptyMap()
    @Volatile private var mirrorReadable = false
    @Volatile private var ambientArea: SeatArea? = null
    @Volatile private var ambientReadable = false
    @Volatile private var ambientBrightness: Map<SeatArea, AreaBounds<Int>> = emptyMap()
    @Volatile private var ambientBrightnessWritable = false
    @Volatile private var ambientBrightnessReadable = false
    @Volatile private var ambientColorAreas: List<SeatArea> = emptyList()
    @Volatile private var ambientColorWritable = false
    @Volatile private var ambientColorReadable = false
    @Volatile private var wiperArea: WindowArea? = null

    // ================================================================ 연결

    /** Pleos 차량 서비스 연결 시도. timeout 안에 capability 조회가 한 번이라도 성공하면 true. 실패·예외면 false(앱은 시뮬레이터로 전환). */
    suspend fun connect(timeoutMs: Long = 4000): Boolean {
        return try {
            releaseVehicle()
            diag("connect 시작(timeout ${timeoutMs}ms)")
            val v = Vehicle(appContext)
            v.initialize() // bindService는 SDK 내부 코루틴에서 비동기로 진행된다. 연결 전 호출은 SDK가 큐에 쌓았다가 연결 후 보낸다.
            vehicle = v
            val (caps, anyOk) = probeLock.withLock { probe(v, timeoutMs) }
            if (anyOk) cachedCaps = caps
            diag("connect 결과: ok=$anyOk caps=${Cap.toQuery(caps)}")
            diag("시동 상태: SDK 2.3.2에 공개 조회 API 없음(IgnitionState는 @hide 열거형뿐) → 기록 생략")
            if (anyOk) logHvacPowerState(v)
            anyOk
        } catch (e: Exception) {
            diag("connect 예외: ${describe(e)}")
            false
        }
    }

    override fun close() {
        releaseVehicle()
        cachedCaps = null
    }

    private fun releaseVehicle() {
        val v = vehicle ?: return
        vehicle = null
        try { v.release(); diag("Vehicle.release()") } catch (e: Exception) { diag("release 실패: ${describe(e)}") }
    }

    // ================================================================ capabilities

    override suspend fun capabilities(): Set<Cap> {
        cachedCaps?.let { return it }
        val v = vehicle ?: return baseCaps()
        return probeLock.withLock {
            cachedCaps ?: probe(v, CALL_TIMEOUT_MS).first.also { cachedCaps = it }
        }
    }

    /** SDK 없이도 되는 Cap: time, tts(Android TextToSpeech), location(권한 있을 때). */
    private fun baseCaps(): Set<Cap> = buildSet {
        add(Cap.TIME)
        add(Cap.TTS)
        if (hasLocationPermission() && locationManager() != null) add(Cap.LOCATION)
    }

    /** 도메인별 checkXxxCapabilities를 병렬로 부른다. 반환: (Cap 집합, 하나라도 성공했는지). */
    private suspend fun probe(v: Vehicle, timeoutMs: Long): Pair<Set<Cap>, Boolean> = coroutineScope {
        val probes = listOf(
            async { probeHvacTemp(v, timeoutMs) },
            async { probeHvacFan(v, timeoutMs) },
            async { probeHvacAir(v, timeoutMs) },
            async { probeHvacPower(v, timeoutMs) },
            async { probeCabinTemp(v, timeoutMs) },
            async { probeSeatHeater(v, timeoutMs) },
            async { probeSteering(v, timeoutMs) },
            async { probeMirror(v, timeoutMs) },
            async { probeAmbient(v, timeoutMs) },
            async { probeEv(v, timeoutMs) },
            async { probeFuel(v, timeoutMs) },
            async { probeWiper(v, timeoutMs) },
        ).awaitAll()
        val caps = baseCaps().toMutableSet()
        probes.forEach { p -> p.cap?.let { caps += it } }
        caps to probes.any { it.sdkOk }
    }

    /** 한 도메인 조회 결과. sdkOk = SDK 콜백이 성공으로 돌아왔는지(지원 여부와 무관). */
    private data class Probe(val sdkOk: Boolean, val cap: Cap?)

    private suspend fun probeHvacTemp(v: Vehicle, t: Long): Probe {
        val r = sdkCall<Triple<CapabilityConfig, Map<SeatArea, AreaBounds<Float>>, HvacTemperatureConfigArray?>>("checkHvacTemperatureCapabilities", "", t) { ok, fail ->
            v.getHVAC().checkHvacTemperatureCapabilities(
                onComplete = { c: CapabilityConfig, m: Map<SeatArea, AreaBounds<Float>>, a: HvacTemperatureConfigArray? -> ok(Triple(c, m, a)) },
                onFailed = { e: Exception -> fail(e) },
            )
        }
        val (cfg, map, arr) = r.valueOrNull() ?: return Probe(false, null)
        tempAreas = map
        tempReadable = cfg.readable()
        tempRange = when {
            arr != null && arr.maxCelsius > arr.minCelsius ->
                TempRange(arr.minCelsius.toDouble(), arr.maxCelsius.toDouble(), arr.incrementCelsius.toDouble())
            else -> driverOf(map.keys)?.let { map[it] }?.let { TempRange(it.minValue.toDouble(), it.maxValue.toDouble(), 0.0) }
        }
        diag("  hvac.temp access=${cfg.accessType} areas=${boundsMap(map)} configArray=${arr?.let { "${it.minCelsius}..${it.maxCelsius} step ${it.incrementCelsius}" }}")
        return Probe(true, Cap.HVAC_TEMP.takeIf { cfg.writable() && map.isNotEmpty() })
    }

    private suspend fun probeHvacFan(v: Vehicle, t: Long): Probe {
        val r = sdkCall<Pair<CapabilityConfig, Map<SeatArea, AreaBounds<Int>>>>("checkHvacFanSpeedCapabilities", "", t) { ok, fail ->
            v.getHVAC().checkHvacFanSpeedCapabilities(
                onComplete = { c: CapabilityConfig, m: Map<SeatArea, AreaBounds<Int>> -> ok(c to m) },
                onFailed = { e: Exception -> fail(e) },
            )
        }
        val (cfg, map) = r.valueOrNull() ?: return Probe(false, null)
        fanAreas = map
        fanReadable = cfg.readable()
        diag("  hvac.fan access=${cfg.accessType} areas=${boundsMap(map)}")
        return Probe(true, Cap.HVAC_FAN.takeIf { cfg.writable() && map.isNotEmpty() })
    }

    private suspend fun probeHvacAir(v: Vehicle, t: Long): Probe {
        val r = sdkCall<Pair<CapabilityConfig, List<SeatArea>>>("checkHvacRecirculationCapabilities", "", t) { ok, fail ->
            v.getHVAC().checkHvacRecirculationCapabilities(
                onComplete = { c: CapabilityConfig, l: List<SeatArea> -> ok(c to l) },
                onFailed = { e: Exception -> fail(e) },
            )
        }
        val (cfg, list) = r.valueOrNull() ?: return Probe(false, null)
        recircAreas = list
        recircReadable = cfg.readable()
        diag("  hvac.air access=${cfg.accessType} areas=${list.map { seatName(it) }}")
        return Probe(true, Cap.HVAC_AIR.takeIf { cfg.writable() && list.isNotEmpty() })
    }

    /** HVAC 전원(HVAC_POWER_ON). Cap은 아니지만 공조 동작 전에 켜기 위해 영역·접근을 기억한다. */
    private suspend fun probeHvacPower(v: Vehicle, t: Long): Probe {
        val r = sdkCall<Pair<CapabilityConfig, List<SeatArea>>>("checkHvacPowerCapabilities", "", t) { ok, fail ->
            v.getHVAC().checkHvacPowerCapabilities(
                onComplete = { c: CapabilityConfig, l: List<SeatArea> -> ok(c to l) },
                onFailed = { e: Exception -> fail(e) },
            )
        }
        val (cfg, list) = r.valueOrNull() ?: return Probe(false, null)
        powerAreas = list
        powerReadable = cfg.readable()
        powerWritable = cfg.writable()
        diag("  hvac.power access=${cfg.accessType} areas=${list.map { seatName(it) }}")
        return Probe(true, null)
    }

    private suspend fun probeCabinTemp(v: Vehicle, t: Long): Probe {
        val r = sdkCall<Pair<CapabilityConfig, List<SeatArea>>>("checkHvacTemperatureCurrentCapabilities", "", t) { ok, fail ->
            v.getHVAC().checkHvacTemperatureCurrentCapabilities(
                onComplete = { c: CapabilityConfig, l: List<SeatArea> -> ok(c to l) },
                onFailed = { e: Exception -> fail(e) },
            )
        }
        val (cfg, list) = r.valueOrNull() ?: return Probe(false, null)
        cabinArea = driverOf(list)
        diag("  hvac.cabinTemp access=${cfg.accessType} areas=${list.map { seatName(it) }}")
        return Probe(true, Cap.HVAC_CABIN_TEMP.takeIf { cfg.readable() && cabinArea != null })
    }

    private suspend fun probeSeatHeater(v: Vehicle, t: Long): Probe {
        val r = sdkCall<Pair<CapabilityConfig, Map<SeatArea, AreaBounds<Int>>>>("checkSeatTemperatureCapabilities", "", t) { ok, fail ->
            v.getSeat().checkSeatTemperatureCapabilities(
                onComplete = { c: CapabilityConfig, m: Map<SeatArea, AreaBounds<Int>> -> ok(c to m) },
                onFailed = { e: Exception -> fail(e) },
            )
        }
        val (cfg, map) = r.valueOrNull() ?: return Probe(false, null)
        seatAreas = map
        seatReadable = cfg.readable()
        diag("  seat.heater access=${cfg.accessType} areas=${boundsMap(map)}")
        return Probe(true, Cap.SEAT_HEATER.takeIf { cfg.writable() && map.isNotEmpty() })
    }

    private suspend fun probeSteering(v: Vehicle, t: Long): Probe {
        val r = sdkCall<Pair<CapabilityConfig, AreaBounds<Int>>>("checkHvacSteeringWheelHeatCapabilities", "", t) { ok, fail ->
            v.getSteeringWheel().checkHvacSteeringWheelHeatCapabilities(
                onComplete = { c: CapabilityConfig, b: AreaBounds<Int> -> ok(c to b) },
                onFailed = { e: Exception -> fail(e) },
            )
        }
        val (cfg, bounds) = r.valueOrNull() ?: return Probe(false, null)
        steeringBounds = bounds
        steeringReadable = cfg.readable()
        diag("  steeringwheel.heater access=${cfg.accessType} bounds=${bounds.minValue}..${bounds.maxValue} (GLOBAL)")
        return Probe(true, Cap.STEERING_HEATER.takeIf { cfg.writable() })
    }

    private suspend fun probeMirror(v: Vehicle, t: Long): Probe {
        val r = sdkCall<Pair<CapabilityConfig, Map<MirrorArea, AreaBounds<Int>>>>("checkHvacSideMirrorHeatCapabilities", "", t) { ok, fail ->
            v.getSideMirror().checkHvacSideMirrorHeatCapabilities(
                onComplete = { c: CapabilityConfig, m: Map<MirrorArea, AreaBounds<Int>> -> ok(c to m) },
                onFailed = { e: Exception -> fail(e) },
            )
        }
        val (cfg, map) = r.valueOrNull() ?: return Probe(false, null)
        mirrorAreas = map
        mirrorReadable = cfg.readable()
        diag("  sidemirror.heater access=${cfg.accessType} areas=${map.entries.map { "${mirrorName(it.key)}:${it.value.minValue}..${it.value.maxValue}" }}")
        return Probe(true, Cap.MIRROR_HEATER.takeIf { cfg.writable() && map.isNotEmpty() })
    }

    /** light.ambient = 스위치(ON/OFF) 제어 가능. 색·밝기는 각각 지원될 때만 추가로 쓴다. */
    private suspend fun probeAmbient(v: Vehicle, t: Long): Probe = coroutineScope {
        val light = v.getLight()
        val sw = async {
            sdkCall<Pair<CapabilityConfig, Map<SeatArea, List<LightSwitch>>>>("checkAmbientLightSwitchCapabilities", "", t) { ok, fail ->
                light.checkAmbientLightSwitchCapabilities(
                    onComplete = { c: CapabilityConfig, m: Map<SeatArea, List<LightSwitch>> -> ok(c to m) },
                    onFailed = { e: Exception -> fail(e) },
                )
            }
        }
        val br = async {
            sdkCall<Pair<CapabilityConfig, Map<SeatArea, AreaBounds<Int>>>>("checkAmbientLightBrightnessCapabilities", "", t) { ok, fail ->
                light.checkAmbientLightBrightnessCapabilities(
                    onComplete = { c: CapabilityConfig, m: Map<SeatArea, AreaBounds<Int>> -> ok(c to m) },
                    onFailed = { e: Exception -> fail(e) },
                )
            }
        }
        val col = async {
            sdkCall<Pair<CapabilityConfig, List<SeatArea>>>("checkAmbientLightBrightnessColorCapabilities", "", t) { ok, fail ->
                light.checkAmbientLightBrightnessColorCapabilities(
                    onComplete = { c: CapabilityConfig, l: List<SeatArea> -> ok(c to l) },
                    onFailed = { e: Exception -> fail(e) },
                )
            }
        }
        val swR = sw.await().valueOrNull()
        val brR = br.await().valueOrNull()
        val colR = col.await().valueOrNull()
        if (brR != null) {
            ambientBrightness = brR.second
            ambientBrightnessWritable = brR.first.writable() && brR.second.isNotEmpty()
            ambientBrightnessReadable = brR.first.readable()
            diag("  ambient.brightness access=${brR.first.accessType} areas=${boundsMap(brR.second)}")
        }
        if (colR != null) {
            ambientColorAreas = colR.second
            ambientColorWritable = colR.first.writable() && colR.second.isNotEmpty()
            ambientColorReadable = colR.first.readable()
            diag("  ambient.color access=${colR.first.accessType} areas=${colR.second.map { seatName(it) }}")
        }
        var cap: Cap? = null
        if (swR != null) {
            val (cfg, map) = swR
            val area = driverOf(map.keys)
            val supported = area?.let { map[it] }.orEmpty()
            // 지원 값 목록이 비어 있으면 제한 없음으로 본다.
            val onOff = supported.isEmpty() || (LightSwitch.ON in supported && LightSwitch.OFF in supported)
            ambientArea = area
            ambientReadable = cfg.readable()
            diag("  ambient.switch access=${cfg.accessType} areas=${map.entries.map { "${seatName(it.key)}:${it.value}" }}")
            if (cfg.writable() && area != null && onOff) cap = Cap.LIGHT_AMBIENT
        }
        Probe(swR != null || brR != null || colR != null, cap)
    }

    private suspend fun probeEv(v: Vehicle, t: Long): Probe {
        val r = sdkCall<CapabilityConfig>("checkEvBatteryStateOfChargeCapabilities", "", t) { ok, fail ->
            v.getEvBattery().checkEvBatteryStateOfChargeCapabilities(
                onComplete = { c: CapabilityConfig -> ok(c) },
                onFailed = { e: Exception -> fail(e) },
            )
        }
        val cfg = r.valueOrNull() ?: return Probe(false, null)
        diag("  ev.battery access=${cfg.accessType}")
        return Probe(true, Cap.EV_BATTERY.takeIf { cfg.readable() })
    }

    private suspend fun probeFuel(v: Vehicle, t: Long): Probe {
        val r = sdkCall<CapabilityConfig>("checkFuelPercentCapabilities", "", t) { ok, fail ->
            v.getFuel().checkFuelPercentCapabilities(
                onComplete = { c: CapabilityConfig -> ok(c) },
                onFailed = { e: Exception -> fail(e) },
            )
        }
        val cfg = r.valueOrNull() ?: return Probe(false, null)
        diag("  fuel.level access=${cfg.accessType}")
        return Probe(true, Cap.FUEL_LEVEL.takeIf { cfg.readable() })
    }

    private suspend fun probeWiper(v: Vehicle, t: Long): Probe {
        val r = sdkCall<Pair<CapabilityConfig, Map<WindowArea, List<WiperState>>>>("checkWiperStateCapabilities", "", t) { ok, fail ->
            v.getWiper().checkWiperStateCapabilities(
                onComplete = { c: CapabilityConfig, m: Map<WindowArea, List<WiperState>> -> ok(c to m) },
                onFailed = { e: Exception -> fail(e) },
            )
        }
        val (cfg, map) = r.valueOrNull() ?: return Probe(false, null)
        wiperArea = PleosMapping.pickArea(map.keys) { WindowValue.FRONT_WINDSHIELD in it.values }
        diag("  wiper.state access=${cfg.accessType} areas=${map.entries.map { "${windowName(it.key)}:${it.value}" }} 선택=${wiperArea?.let { windowName(it) }}")
        return Probe(true, Cap.WIPER_STATE.takeIf { cfg.readable() && wiperArea != null })
    }

    // ================================================================ observe

    @Suppress("DEPRECATION") // Gear API는 'Google GEAR_SELECTION 사용' 권고로 deprecated(WARNING)지만 SDK에 대체 API가 없다.
    override fun observe(): Flow<VehicleEvent> = callbackFlow {
        val caps = capabilities()
        val v = vehicle
        val cleanups = mutableListOf<() -> Unit>()
        diag("observe 시작 caps=${Cap.toQuery(caps)}")

        fun guard(name: String, block: () -> Unit) {
            try { block(); diag("$name 등록 OK") } catch (e: Exception) { diag("$name 등록 실패: ${describe(e)}") }
        }

        fun emit(what: String, e: VehicleEvent) {
            val ok = trySend(e).isSuccess
            if (e is VehicleEvent.Wiper || e is VehicleEvent.Drive) diag("  → $what 이벤트 ${if (ok) "방출" else "방출 실패"}: $e")
        }

        if (v != null) {
            // EV 배터리 %
            if (Cap.EV_BATTERY in caps) {
                val ev = v.getEvBattery()
                val l = object : EvBatteryStateOfChargeListener {
                    override fun onEvBatteryStateOfChargeUpdated(value: Int?) {
                        value?.let { emit("EV", VehicleEvent.Energy(EnergySource.EV_BATTERY, it.toDouble())) }
                    }
                    override fun onFailed(e: Exception) { diag("EV SOC 리스너 오류: ${describe(e)}") }
                }
                guard("registerEvBatteryStateOfCharge") {
                    ev.registerEvBatteryStateOfCharge(l)
                    cleanups += { ev.unregisterEvBatteryStateOfCharge(l) }
                }
                launch {
                    sdkCall<Int?>("getEvBatteryStateOfCharge") { ok, fail ->
                        ev.getEvBatteryStateOfCharge(onComplete = { x: Int? -> ok(x) }, onFailed = { e: Exception -> fail(e) })
                    }.valueOrNull()?.let { emit("EV", VehicleEvent.Energy(EnergySource.EV_BATTERY, it.toDouble())) }
                }
            }
            // 연료 %
            if (Cap.FUEL_LEVEL in caps) {
                val fuel = v.getFuel()
                val l = object : FuelPercentListener {
                    override fun onFuelPercentUpdated(value: Int?) {
                        value?.let { emit("연료", VehicleEvent.Energy(EnergySource.FUEL, it.toDouble())) }
                    }
                    override fun onFailed(e: Exception) { diag("연료 리스너 오류: ${describe(e)}") }
                }
                guard("registerFuelPercent") {
                    fuel.registerFuelPercent(l)
                    cleanups += { fuel.unregisterFuelPercent(l) }
                }
                launch {
                    sdkCall<Int?>("getFuelPercent") { ok, fail ->
                        fuel.getFuelPercent(onComplete = { x: Int? -> ok(x) }, onFailed = { e: Exception -> fail(e) })
                    }.valueOrNull()?.let { emit("연료", VehicleEvent.Energy(EnergySource.FUEL, it.toDouble())) }
                }
            }
            // 와이퍼: 기준선(등록 직후 현재값) → 리스너 콜백 → 주기 읽기(리스너가 안 올 때 대비). 바뀔 때만 방출([WiperSignal]).
            val wArea = wiperArea
            if (Cap.WIPER_STATE in caps && wArea != null) {
                val wiper = v.getWiper()
                val signal = WiperSignal(log = { diag(it) }, send = { e -> trySend(e).isSuccess })
                val l = object : WiperStateListener {
                    override fun onWiperStateUpdated(state: WiperState?) {
                        val name = state?.name
                        if (signal.needsSwitch(name)) launch { signal.onRaw("콜백", name, readWiperSwitch(v, wArea)) }
                        else signal.onRaw("콜백", name)
                    }
                    override fun onFailed(e: Exception) { diag("와이퍼 리스너 오류: ${describe(e)}") }
                }
                guard("registerWiperState(${windowName(wArea)})") {
                    wiper.registerWiperState(wArea, l)
                    cleanups += { wiper.unregisterWiperState(wArea, l) }
                }
                launch {
                    // 기준선: 엔진 TriggerDetector는 첫 값을 기준으로만 쓰므로 현재값(보통 OFF)을 먼저 흘린다.
                    val first = readWiperState(v, wArea, quiet = false)
                    val firstMapped = signal.onRaw("기준선", first, if (signal.needsSwitch(first)) readWiperSwitch(v, wArea) else null)
                    if (firstMapped == null) diag("와이퍼 기준선 없음: 다음 값이 기준선이 된다")
                    // 주기 읽기
                    while (true) {
                        delay(WIPER_POLL_MS)
                        val s = readWiperState(v, wArea, quiet = true)
                        signal.onRaw("주기 읽기", s, if (signal.needsSwitch(s)) readWiperSwitch(v, wArea, quiet = true) else null, quiet = true)
                    }
                }
            } else {
                diag("와이퍼 구독 안 함(cap=${Cap.WIPER_STATE in caps}, area=${wArea?.let { windowName(it) }})")
            }
            // 기어 → 주행 상태(Cap과 무관하게 항상 시도. 실패하면 UNKNOWN = 주행 취급)
            val gearApi = v.getGear()
            val gl = object : GearSelectionListener {
                override fun onSelected(gear: VehicleGear?) {
                    diag("기어 콜백 raw=${gear?.name}")
                    emit("기어", VehicleEvent.Drive(PleosMapping.driveStateForGear(gear?.name)))
                }
                override fun onFailed(e: Exception) { diag("기어 리스너 오류: ${describe(e)}") }
            }
            try {
                gearApi.registerGearSelection(gl)
                cleanups += { gearApi.unregisterGearSelection(gl) }
                diag("registerGearSelection 등록 OK")
            } catch (e: Exception) {
                diag("registerGearSelection 등록 실패: ${describe(e)}")
                emit("기어", VehicleEvent.Drive(DriveState.UNKNOWN))
            }
            launch {
                val g = sdkCall<VehicleGear?>("getCurrentGearSelectionState") { ok, fail ->
                    gearApi.getCurrentGearSelectionState(onComplete = { x: VehicleGear? -> ok(x) }, onFailed = { e: Exception -> fail(e) })
                }
                if (g is SdkResult.Ok) emit("기어", VehicleEvent.Drive(PleosMapping.driveStateForGear(g.value?.name)))
            }
        }

        // 위치(Android LocationManager, 권한 있을 때만). 좌표는 진단 로그에 남기지 않는다.
        if (Cap.LOCATION in caps) {
            val lm = locationManager()
            if (lm != null && hasLocationPermission()) {
                val ll = LocationListener { loc: Location -> trySend(VehicleEvent.Location(loc.latitude, loc.longitude)) }
                guard("requestLocationUpdates") {
                    startLocationUpdates(lm, ll)
                    cleanups += { lm.removeUpdates(ll) }
                }
            }
        }

        awaitClose {
            diag("observe 종료: 리스너 ${cleanups.size}개 해제")
            cleanups.forEach { c -> try { c() } catch (e: Exception) { diag("unregister 실패: ${describe(e)}") } }
        }
    }

    /** 와이퍼 상태 원시값 이름(읽기 실패면 null). [quiet]면 진단 로그를 남기지 않는다(주기 읽기). */
    private suspend fun readWiperState(v: Vehicle, area: WindowArea, quiet: Boolean = false): String? =
        sdkCall<WiperState?>("getCurrentWiperState", windowName(area), VERIFY_READ_TIMEOUT_MS, quiet) { ok, fail ->
            v.getWiper().getCurrentWiperState(area = area, onComplete = { s: WiperState? -> ok(s) }, onFailed = { e: Exception -> fail(e) })
        }.valueOrNull()?.name

    /** 와이퍼 스위치 원시값 이름(WiperState가 OTHER일 때 보조 판단용). */
    private suspend fun readWiperSwitch(v: Vehicle, area: WindowArea, quiet: Boolean = false): String? =
        sdkCall<WiperSwitch?>("getCurrentWiperSwitch", windowName(area), VERIFY_READ_TIMEOUT_MS, quiet) { ok, fail ->
            v.getWiper().getCurrentWiperSwitch(area = area, onComplete = { s: WiperSwitch? -> ok(s) }, onFailed = { e: Exception -> fail(e) })
        }.valueOrNull()?.name

    private fun startLocationUpdates(lm: LocationManager, listener: LocationListener) {
        if (!hasLocationPermission()) return
        val providers = lm.getProviders(true)
        var last: Location? = null
        for (p in listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER)) {
            if (p !in providers) continue
            lm.requestLocationUpdates(p, LOCATION_MIN_TIME_MS, LOCATION_MIN_DISTANCE_M, listener, Looper.getMainLooper())
            val l = lm.getLastKnownLocation(p)
            if (l != null && (last == null || l.time > last.time)) last = l
        }
        diag("위치 provider=${providers}")
        last?.let { listener.onLocationChanged(it) }
    }

    // ================================================================ 읽기(원시값)

    private suspend fun readPower(v: Vehicle, area: SeatArea, t: Long = CALL_TIMEOUT_MS) =
        sdkCall<Boolean?>("getHvacPowerState", seatName(area), t) { ok, fail ->
            v.getHVAC().getHvacPowerState(area = area, onComplete = { x: Boolean? -> ok(x) }, onFailed = { e: Exception -> fail(e) })
        }

    private suspend fun readTemp(v: Vehicle, area: SeatArea, t: Long = CALL_TIMEOUT_MS) =
        sdkCall<Float?>("getHvacTemperature", seatName(area), t) { ok, fail ->
            v.getHVAC().getHvacTemperature(area = area, unit = TemperatureUnit.CELSIUS, onComplete = { x: Float? -> ok(x) }, onFailed = { e: Exception -> fail(e) })
        }

    private suspend fun readFan(v: Vehicle, area: SeatArea, t: Long = CALL_TIMEOUT_MS) =
        sdkCall<Int?>("getHvacFanSpeed", seatName(area), t) { ok, fail ->
            v.getHVAC().getHvacFanSpeed(area = area, onComplete = { x: Int? -> ok(x) }, onFailed = { e: Exception -> fail(e) })
        }

    private suspend fun readRecirc(v: Vehicle, area: SeatArea, t: Long = CALL_TIMEOUT_MS) =
        sdkCall<Boolean?>("getHvacRecirculationState", seatName(area), t) { ok, fail ->
            v.getHVAC().getHvacRecirculationState(area = area, onComplete = { x: Boolean? -> ok(x) }, onFailed = { e: Exception -> fail(e) })
        }

    private suspend fun readSeat(v: Vehicle, area: SeatArea, t: Long = CALL_TIMEOUT_MS) =
        sdkCall<Int?>("getHvacSeatTemperature", seatName(area), t) { ok, fail ->
            v.getSeat().getHvacSeatTemperature(area = area, onComplete = { x: Int? -> ok(x) }, onFailed = { e: Exception -> fail(e) })
        }

    private suspend fun readSteering(v: Vehicle, t: Long = CALL_TIMEOUT_MS) =
        sdkCall<Int?>("getHvacSteeringWheelHeatState", "GLOBAL", t) { ok, fail ->
            v.getSteeringWheel().getHvacSteeringWheelHeatState(onComplete = { x: Int? -> ok(x) }, onFailed = { e: Exception -> fail(e) })
        }

    private suspend fun readMirror(v: Vehicle, area: MirrorArea, t: Long = CALL_TIMEOUT_MS) =
        sdkCall<Int?>("getHvacSideMirrorHeatState", mirrorName(area), t) { ok, fail ->
            v.getSideMirror().getHvacSideMirrorHeatState(area = area, onComplete = { x: Int? -> ok(x) }, onFailed = { e: Exception -> fail(e) })
        }

    private suspend fun readAmbientSwitch(v: Vehicle, area: SeatArea, t: Long = CALL_TIMEOUT_MS) =
        sdkCall<LightSwitch?>("getAmbientLightSwitch", seatName(area), t) { ok, fail ->
            v.getLight().getAmbientLightSwitch(area = area, onComplete = { x: LightSwitch? -> ok(x) }, onFailed = { e: Exception -> fail(e) })
        }

    private suspend fun readAmbientColor(v: Vehicle, area: SeatArea, t: Long = CALL_TIMEOUT_MS) =
        sdkCall<Int?>("getAmbientLightBrightnessColor", seatName(area), t) { ok, fail ->
            v.getLight().getAmbientLightBrightnessColor(area = area, onComplete = { x: Int? -> ok(x) }, onFailed = { e: Exception -> fail(e) })
        }

    private suspend fun readAmbientBrightness(v: Vehicle, area: SeatArea, t: Long = CALL_TIMEOUT_MS) =
        sdkCall<Int?>("getAmbientLightBrightness", seatName(area), t) { ok, fail ->
            v.getLight().getAmbientLightBrightness(area = area, onComplete = { x: Int? -> ok(x) }, onFailed = { e: Exception -> fail(e) })
        }

    private suspend fun logHvacPowerState(v: Vehicle) {
        if (powerAreas.isEmpty()) { diag("HVAC 전원: capability 없음"); return }
        if (!powerReadable) { diag("HVAC 전원: 읽기 불가(access WRITE 전용)"); return }
        powerAreas.forEach { a -> diag("HVAC 전원 ${seatName(a)} = ${readPower(v, a).valueOrNull()}") }
    }

    // ================================================================ 읽기(VehicleGateway)

    override suspend fun readCabinTemp(): Double? {
        val v = vehicle ?: return null
        if (Cap.HVAC_CABIN_TEMP !in capabilities()) return null
        val area = cabinArea ?: return null
        return sdkCall<Float?>("getHvacTemperatureCurrent", seatName(area)) { ok, fail ->
            v.getHVAC().getHvacTemperatureCurrent(
                area = area,
                unit = TemperatureUnit.CELSIUS,
                onComplete = { value: Float? -> ok(value) },
                onFailed = { e: Exception -> fail(e) },
            )
        }.valueOrNull()?.toDouble()
    }

    override suspend fun readDevice(key: DeviceKey): DeviceValue? {
        val v = vehicle ?: return null
        val caps = capabilities()
        return when (key) {
            DeviceKey.HvacTemp -> {
                val area = driverOf(tempAreas.keys)
                if (Cap.HVAC_TEMP !in caps || !tempReadable || area == null) return null
                readTemp(v, area).valueOrNull()?.let { DeviceValue.Temperature(it.toDouble()) }
            }
            DeviceKey.HvacFan -> {
                val area = driverOf(fanAreas.keys)
                if (Cap.HVAC_FAN !in caps || !fanReadable || area == null) return null
                readFan(v, area).valueOrNull()?.let { DeviceValue.Level(it) }
            }
            DeviceKey.HvacAir -> {
                val area = driverOf(recircAreas)
                if (Cap.HVAC_AIR !in caps || !recircReadable || area == null) return null
                readRecirc(v, area).valueOrNull()?.let { DeviceValue.Air(if (it) AirMode.RECIRC else AirMode.FRESH) }
            }
            is DeviceKey.SeatHeater -> {
                val area = seatAreaFor(key.seat)
                if (Cap.SEAT_HEATER !in caps || !seatReadable || area == null) return null
                PleosMapping.seatLevelFromValue(readSeat(v, area).valueOrNull())?.let { DeviceValue.Level(it) }
            }
            DeviceKey.SteeringHeater -> {
                if (Cap.STEERING_HEATER !in caps || !steeringReadable) return null
                PleosMapping.heaterIsOn(readSteering(v).valueOrNull())?.let { DeviceValue.OnOff(it) }
            }
            DeviceKey.MirrorHeater -> {
                val area = mirrorAreas.keys.firstOrNull()
                if (Cap.MIRROR_HEATER !in caps || !mirrorReadable || area == null) return null
                PleosMapping.heaterIsOn(readMirror(v, area).valueOrNull())?.let { DeviceValue.OnOff(it) }
            }
            DeviceKey.AmbientLight -> readAmbient(v, caps)
        }
    }

    private suspend fun readAmbient(v: Vehicle, caps: Set<Cap>): DeviceValue? {
        val area = ambientArea
        if (Cap.LIGHT_AMBIENT !in caps || !ambientReadable || area == null) return null
        val sw = readAmbientSwitch(v, area).valueOrNull() ?: return null
        val on = sw == LightSwitch.ON || sw == LightSwitch.AUTOMATIC
        val colorArea = driverOf(ambientColorAreas)
        val color: AmbientColor? = if (colorArea != null && ambientColorReadable) {
            PleosMapping.nearestAmbientColor(readAmbientColor(v, colorArea).valueOrNull())
        } else null
        val brArea = driverOf(ambientBrightness.keys)
        val brightness: AmbientBrightness? = if (brArea != null && ambientBrightnessReadable) {
            val b = ambientBrightness.getValue(brArea)
            PleosMapping.brightnessFromValue(readAmbientBrightness(v, brArea).valueOrNull(), b.minValue, b.maxValue)
        } else null
        return DeviceValue.Ambient(on = on, color = color, brightness = brightness)
    }

    // ================================================================ 실행

    override suspend fun apply(action: Action): ActionResult {
        val v = vehicle ?: return ActionResult.Unsupported
        if (action.requiredCap !in capabilities()) return ActionResult.Unsupported
        diag("apply ${action.type}: $action")
        val result = try {
            when (action) {
                is Action.HvacTemp -> ensureHvacPower(v) ?: applyHvacTemp(v, action)
                is Action.HvacFan -> ensureHvacPower(v) ?: applyEach(fanAreas.entries) { (area, b) ->
                    val target = PleosMapping.clampInt(action.level, b.minValue, b.maxValue)
                    setAndVerify<Int?>(
                        "setHvacFanSpeed", "${seatName(area)} value=$target",
                        set = { ok, fail ->
                            v.getHVAC().setHvacFanSpeed(area = area, value = target, onComplete = { _: SeatArea, _: Int? -> ok(Unit) }, onFailed = { e: Exception -> fail(e) })
                        },
                        readable = fanReadable, read = { readFan(v, area, VERIFY_READ_TIMEOUT_MS) },
                        matches = { it == target },
                    )
                }
                is Action.HvacAir -> ensureHvacPower(v) ?: applyEach(recircAreas) { area ->
                    val target = action.mode == AirMode.RECIRC
                    setAndVerify<Boolean?>(
                        "setHvacRecirculation", "${seatName(area)} value=$target",
                        set = { ok, fail ->
                            v.getHVAC().setHvacRecirculation(area = area, value = target, onComplete = { _: SeatArea, _: Boolean? -> ok(Unit) }, onFailed = { e: Exception -> fail(e) })
                        },
                        readable = recircReadable, read = { readRecirc(v, area, VERIFY_READ_TIMEOUT_MS) },
                        matches = { it == target },
                    )
                }
                is Action.SeatHeater -> {
                    val area = seatAreaFor(action.seat)
                    if (area == null) ActionResult.Unsupported else {
                        val b = seatAreas.getValue(area)
                        val target = PleosMapping.heaterLevel(action.level, b.minValue, b.maxValue)
                        setAndVerify<Int?>(
                            "setHvacSeatTemperature", "${seatName(area)} value=$target",
                            set = { ok, fail ->
                                v.getSeat().setHvacSeatTemperature(area = area, value = target, onComplete = { _: SeatArea, _: Int? -> ok(Unit) }, onFailed = { e: Exception -> fail(e) })
                            },
                            readable = seatReadable, read = { readSeat(v, area, VERIFY_READ_TIMEOUT_MS) },
                            matches = { it == target },
                        )
                    }
                }
                is Action.SteeringHeater -> {
                    val b = steeringBounds
                    if (b == null) ActionResult.Unsupported else {
                        val target = PleosMapping.heaterOnOffValue(action.on, b.minValue, b.maxValue)
                        setAndVerify<Int?>(
                            "setHvacSteeringWheelHeat", "GLOBAL value=$target",
                            set = { ok, fail ->
                                v.getSteeringWheel().setHvacSteeringWheelHeat(value = target, onComplete = { _: Int? -> ok(Unit) }, onFailed = { e: Exception -> fail(e) })
                            },
                            readable = steeringReadable, read = { readSteering(v, VERIFY_READ_TIMEOUT_MS) },
                            matches = { it == target },
                        )
                    }
                }
                is Action.MirrorHeater -> applyEach(mirrorAreas.entries) { (area, b) ->
                    val target = PleosMapping.heaterOnOffValue(action.on, b.minValue, b.maxValue)
                    setAndVerify<Int?>(
                        "setHvacSideMirrorHeat", "${mirrorName(area)} value=$target",
                        set = { ok, fail ->
                            v.getSideMirror().setHvacSideMirrorHeat(area = area, value = target, onComplete = { _: MirrorArea, _: Int? -> ok(Unit) }, onFailed = { e: Exception -> fail(e) })
                        },
                        readable = mirrorReadable, read = { readMirror(v, area, VERIFY_READ_TIMEOUT_MS) },
                        matches = { it == target },
                    )
                }
                is Action.AmbientLight -> applyAmbient(v, action)
                // 엔진은 Speak를 넘기지 않는다(알림 문장에 합쳐 Android TTS로 읽는다). navi.waypoint는 이번 범위 밖.
                is Action.Speak, Action.SuggestCharger -> ActionResult.Unsupported
            }
        } catch (e: Exception) {
            ActionResult.Failed(describe(e))
        }
        diag("apply ${action.type} 결과: $result")
        return result
    }

    /**
     * 공조 동작 전 HVAC 전원 확인. 꺼져 있으면 setHvacPower(true) 후 켜졌는지 확인한다.
     * 반환: null = 진행 가능, 아니면 그 결과(Failed)로 동작을 끝낸다.
     */
    private suspend fun ensureHvacPower(v: Vehicle): ActionResult? {
        if (powerAreas.isEmpty()) { diag("HVAC 전원: capability 없음 → 확인 생략"); return null }
        for (area in powerAreas) {
            val state: Boolean? = if (powerReadable) readPower(v, area).valueOrNull() else null
            diag("HVAC 전원 ${seatName(area)} = $state")
            if (state == true) continue
            if (!powerWritable) {
                return if (state == false) ActionResult.Failed("공조 전원이 꺼져 있고 앱에서 켤 수 없음") else null
            }
            val r = setAndVerify<Boolean?>(
                "setHvacPower", "${seatName(area)} value=true",
                set = { ok, fail ->
                    v.getHVAC().setHvacPower(area = area, value = true, onComplete = { _: SeatArea, _: Boolean? -> ok(Unit) }, onFailed = { e: Exception -> fail(e) })
                },
                readable = powerReadable, read = { readPower(v, area, VERIFY_READ_TIMEOUT_MS) },
                matches = { it == true },
            )
            if (r is ActionResult.Failed) return ActionResult.Failed("공조 전원을 켜지 못함: ${r.reason}")
        }
        return null
    }

    private suspend fun applyHvacTemp(v: Vehicle, action: Action.HvacTemp): ActionResult {
        val range = tempRange
        val target = when (action.mode) {
            HvacTempMode.SET -> action.value
            HvacTempMode.DELTA -> { // 엔진이 SET으로 바꿔 넘기지만, 혹시 DELTA가 오면 현재값 기준으로 계산
                val area = driverOf(tempAreas.keys) ?: return ActionResult.Unsupported
                val cur = readTemp(v, area).valueOrNull() ?: return ActionResult.Failed("현재 설정 온도를 읽을 수 없음")
                cur + action.value
            }
        }
        return applyEach(tempAreas.entries) { (area, b) ->
            val r = range ?: TempRange(b.minValue.toDouble(), b.maxValue.toDouble(), 0.0)
            val value = PleosMapping.clampTemp(target, r)
            setAndVerify<Float?>(
                "setHvacTemperature", "${seatName(area)} value=$value℃",
                set = { ok, fail ->
                    v.getHVAC().setHvacTemperature(
                        area = area, value = value.toFloat(), unit = TemperatureUnit.CELSIUS,
                        onComplete = { _: SeatArea, _: Float? -> ok(Unit) }, onFailed = { e: Exception -> fail(e) },
                    )
                },
                readable = tempReadable, read = { readTemp(v, area, VERIFY_READ_TIMEOUT_MS) },
                matches = { PleosMapping.tempMatches(it?.toDouble(), value, r.step) },
            )
        }
    }

    private suspend fun applyAmbient(v: Vehicle, action: Action.AmbientLight): ActionResult {
        val area = ambientArea ?: return ActionResult.Unsupported
        val light = v.getLight()
        val targetSwitch = if (action.on) LightSwitch.ON else LightSwitch.OFF
        val sw = setAndVerify<LightSwitch?>(
            "setAmbientLightSwitch", "${seatName(area)} value=$targetSwitch",
            set = { ok, fail ->
                light.setAmbientLightSwitch(area = area, value = targetSwitch, onComplete = { _: SeatArea, _: LightSwitch? -> ok(Unit) }, onFailed = { e: Exception -> fail(e) })
            },
            readable = ambientReadable, read = { readAmbientSwitch(v, area, VERIFY_READ_TIMEOUT_MS) },
            matches = { if (action.on) it == LightSwitch.ON || it == LightSwitch.AUTOMATIC else it == LightSwitch.OFF },
        )
        if (sw != ActionResult.Success || !action.on) return sw
        val color = action.color
        if (color != null && ambientColorWritable) {
            val argb = PleosMapping.ambientArgb(color)
            val r = applyEach(ambientColorAreas) { a ->
                setAndVerify<Int?>(
                    "setAmbientLightBrightnessColor", "${seatName(a)} value=0x${Integer.toHexString(argb)}",
                    set = { ok, fail ->
                        light.setAmbientLightBrightnessColor(area = a, value = argb, onComplete = { _: SeatArea, _: Int -> ok(Unit) }, onFailed = { e: Exception -> fail(e) })
                    },
                    readable = ambientColorReadable, read = { readAmbientColor(v, a, VERIFY_READ_TIMEOUT_MS) },
                    matches = { PleosMapping.ambientColorMatches(it, color) },
                )
            }
            if (r != ActionResult.Success) return r
        }
        val brightness = action.brightness
        if (brightness != null && ambientBrightnessWritable) {
            val r = applyEach(ambientBrightness.entries) { (a, b) ->
                val target = PleosMapping.brightnessValue(brightness, b.minValue, b.maxValue)
                setAndVerify<Int?>(
                    "setAmbientLightBrightness", "${seatName(a)} value=$target",
                    set = { ok, fail ->
                        light.setAmbientLightBrightness(area = a, value = target, onComplete = { _: SeatArea, _: Int -> ok(Unit) }, onFailed = { e: Exception -> fail(e) })
                    },
                    readable = ambientBrightnessReadable, read = { readAmbientBrightness(v, a, VERIFY_READ_TIMEOUT_MS) },
                    matches = { it == target },
                )
            }
            if (r != ActionResult.Success) return r
        }
        return ActionResult.Success
    }

    /** 여러 대상에 차례로 적용. 첫 실패에서 멈춘다. 대상이 없으면 Unsupported. */
    private suspend fun <T> applyEach(targets: Collection<T>, one: suspend (T) -> ActionResult): ActionResult {
        if (targets.isEmpty()) return ActionResult.Unsupported
        for (t in targets) {
            val r = one(t)
            if (r != ActionResult.Success) return r
        }
        return ActionResult.Success
    }

    /**
     * set 한 번 + 반영 확인. set의 onComplete는 '요청 접수'일 뿐이라 [VERIFY_INTERVAL_MS] 간격으로 최대 [VERIFY_TRIES]번 다시 읽는다.
     * [readable]이 false면(WRITE 전용) 확인을 생략하고 Success.
     */
    private suspend fun <V> setAndVerify(
        name: String,
        detail: String,
        set: ((Unit) -> Unit, (Exception) -> Unit) -> Unit,
        readable: Boolean,
        read: suspend () -> SdkResult<V>,
        matches: (V) -> Boolean,
    ): ActionResult {
        val r = sdkCall<Unit>(name, detail) { ok, fail -> set(ok, fail) }
        if (r is SdkResult.Err) return ActionResult.Failed(r.reason)
        if (!readable) {
            diag("  $name 반영 확인 생략(읽기 접근 없음)")
            return ActionResult.Success
        }
        var last = "읽기 실패"
        repeat(VERIFY_TRIES) { i ->
            delay(VERIFY_INTERVAL_MS)
            when (val x = read()) {
                is SdkResult.Ok -> {
                    if (matches(x.value)) {
                        diag("  $name 반영 확인 OK(${(i + 1) * VERIFY_INTERVAL_MS}ms, 현재값 ${x.value})")
                        return ActionResult.Success
                    }
                    last = x.value.toString()
                }
                is SdkResult.Err -> last = "읽기 실패: ${x.reason}"
            }
        }
        diag("  $name 반영 안 됨: 목표 [$detail], 현재값 $last")
        return ActionResult.Failed("차량에 반영되지 않음(현재값 $last)")
    }

    // ================================================================ 공통

    private sealed interface SdkResult<out T> {
        data class Ok<T>(val value: T) : SdkResult<T>
        data class Err(val reason: String) : SdkResult<Nothing>
    }

    private fun <T> SdkResult<T>.valueOrNull(): T? = (this as? SdkResult.Ok<T>)?.value

    /**
     * 콜백 SDK 호출 하나를 suspend로 감싼다. 타임아웃·예외·중복 콜백을 모두 흡수하고, 결과·소요 시간을 진단 로그에 남긴다.
     * SDK는 서비스 연결 전 호출을 큐에 쌓아 두므로, 연결이 안 되면 콜백이 오지 않는다 → 타임아웃으로 끝난다.
     */
    private suspend fun <T> sdkCall(
        name: String,
        detail: String = "",
        timeoutMs: Long = CALL_TIMEOUT_MS,
        quiet: Boolean = false,
        block: (ok: (T) -> Unit, fail: (Exception) -> Unit) -> Unit,
    ): SdkResult<T> {
        val start = SystemClock.elapsedRealtime()
        val r = withTimeoutOrNull(timeoutMs) {
            suspendCancellableCoroutine<SdkResult<T>> { cont ->
                val done = AtomicBoolean(false)
                fun finish(res: SdkResult<T>) {
                    if (done.compareAndSet(false, true) && cont.isActive) {
                        try { cont.resume(res) } catch (e: IllegalStateException) { Log.d(TAG, "$name 중복 resume 무시") }
                    }
                }
                try {
                    block({ value -> finish(SdkResult.Ok(value)) }, { e -> finish(SdkResult.Err("onFailed ${describe(e)}")) })
                } catch (e: Exception) {
                    finish(SdkResult.Err("예외 ${describe(e)}"))
                }
            }
        } ?: SdkResult.Err("timeout ${timeoutMs}ms")
        val ms = SystemClock.elapsedRealtime() - start
        if (quiet) return r
        val head = if (detail.isEmpty()) name else "$name($detail)"
        when (r) {
            is SdkResult.Ok -> diag("$head onComplete ${summarize(r.value)} ${ms}ms")
            is SdkResult.Err -> diag("$head ${r.reason} ${ms}ms")
        }
        return r
    }

    /** capability 결과처럼 긴 값은 줄여서 남긴다(세부는 probe 로그가 따로 남긴다). */
    private fun summarize(value: Any?): String = when (value) {
        is Unit -> ""
        is Pair<*, *>, is Triple<*, *, *> -> ""
        is CapabilityConfig -> "access=${value.accessType}"
        else -> "= $value"
    }

    private fun diag(message: String) {
        PleosDiagnostics.log(message)
        Log.i(TAG, message)
    }

    private fun CapabilityConfig.writable() = accessType == AccessType.WRITE || accessType == AccessType.READ_WRITE
    private fun CapabilityConfig.readable() = accessType == AccessType.READ || accessType == AccessType.READ_WRITE

    /** 운전석(ROW_1_LEFT)을 포함하는 HVAC/조명 영역, 없으면 첫 영역. */
    private fun driverOf(areas: Collection<SeatArea>): SeatArea? =
        PleosMapping.pickArea(areas) { SeatValue.ROW_1_LEFT in it.values }

    /** 시트 열선 영역. 좌핸들 가정: driver=ROW_1_LEFT, passenger=ROW_1_RIGHT. 없으면 null(Unsupported). */
    private fun seatAreaFor(seat: Seat): SeatArea? {
        val want = if (seat == Seat.DRIVER) SeatValue.ROW_1_LEFT else SeatValue.ROW_1_RIGHT
        return seatAreas.keys.firstOrNull { want in it.values }
    }

    private fun seatName(a: SeatArea): String = a.values.joinToString("|") { it.name }.ifEmpty { "(빈 영역)" }
    private fun mirrorName(a: MirrorArea): String = a.values.joinToString("|") { it.name }.ifEmpty { "(빈 영역)" }
    private fun windowName(a: WindowArea): String = a.values.joinToString("|") { it.name }.ifEmpty { "(빈 영역)" }
    private fun <N : Number> boundsMap(m: Map<SeatArea, AreaBounds<N>>): String =
        m.entries.joinToString(", ", "[", "]") { "${seatName(it.key)}:${it.value.minValue}..${it.value.maxValue}" }

    private fun hasLocationPermission(): Boolean =
        appContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            appContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun locationManager(): LocationManager? =
        try { appContext.getSystemService(Context.LOCATION_SERVICE) as? LocationManager } catch (e: Exception) { null }

    /** VehicleException은 난독화돼 참조할 수 없어 클래스 이름과 메시지(ErrorCode 포함)만 남긴다. */
    private fun describe(e: Exception): String = "${e::class.java.simpleName}: ${e.message}"

    companion object {
        private const val TAG = "PleosGateway"
        /** get/set 한 번의 대기 한도. */
        const val CALL_TIMEOUT_MS = 3000L
        /** 반영 확인: 간격 × 횟수 = 최대 1.5초. */
        const val VERIFY_INTERVAL_MS = 300L
        const val VERIFY_TRIES = 5
        private const val VERIFY_READ_TIMEOUT_MS = 1000L
        /** 와이퍼 주기 읽기 간격(리스너 콜백이 오지 않는 환경 대비). */
        private const val WIPER_POLL_MS = 1000L
        private const val LOCATION_MIN_TIME_MS = 5_000L
        private const val LOCATION_MIN_DISTANCE_M = 20f
    }
}
