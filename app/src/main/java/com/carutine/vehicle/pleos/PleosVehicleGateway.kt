package com.carutine.vehicle.pleos

import ai.pleos.playground.vehicle.Vehicle
import ai.pleos.playground.vehicle.api.model.area.SeatArea
import ai.pleos.playground.vehicle.api.model.area.MirrorArea
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
 * - 모든 SDK 호출은 try/catch + 타임아웃([CALL_TIMEOUT_MS])으로 감싼다. 실패는 앱을 죽이지 않고
 *   null / [ActionResult.Failed] / 이벤트 누락으로 끝난다.
 * - SDK 2.3.2 AAR에서 VehicleException은 난독화돼 있으므로 `Exception`으로만 받는다.
 *
 * 차량 값 매핑 요약:
 * - hvac.temp/fan/air: 차량이 알려준 모든 HVAC 영역(SeatArea)에 같은 값을 쓴다. 읽기는 운전석(ROW_1_LEFT 포함) 영역.
 * - 온도: ℃, HvacTemperatureConfigArray(min/max/incrementCelsius)가 있으면 그 범위·증분, 없으면 AreaBounds로 clamp.
 * - 팬: 루틴 0..10을 차량 AreaBounds로 clamp(0이 범위 밖이면 최저 단계, 팬을 끄지는 않는다).
 * - 시트 열선: driver=ROW_1_LEFT, passenger=ROW_1_RIGHT(좌핸들 가정). 단계는 [max(0,min), max]로 clamp.
 * - 스티어링·미러 열선: on=최대 단계, off=0.
 * - 앰비언트: on/off=setAmbientLightSwitch(ON/OFF). 색·밝기 매핑은 [PleosMapping.ambientArgb], [PleosMapping.brightnessValue] KDoc 표.
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
    @Volatile private var ambientColorAreas: List<SeatArea> = emptyList()
    @Volatile private var ambientColorWritable = false
    @Volatile private var wiperArea: WindowArea? = null

    // ================================================================ 연결

    /** Pleos 차량 서비스 연결 시도. timeout 안에 capability 조회가 한 번이라도 성공하면 true. 실패·예외면 false(앱은 시뮬레이터로 전환). */
    suspend fun connect(timeoutMs: Long = 4000): Boolean {
        return try {
            releaseVehicle()
            val v = Vehicle(appContext)
            v.initialize() // bindService는 SDK 내부 코루틴에서 비동기로 진행된다. 연결 전 호출은 SDK가 큐에 쌓았다가 연결 후 보낸다.
            vehicle = v
            val (caps, anyOk) = probeLock.withLock { probe(v, timeoutMs) }
            if (anyOk) cachedCaps = caps
            Log.i(TAG, "connect: ok=$anyOk caps=${Cap.toQuery(caps)}")
            anyOk
        } catch (e: Exception) {
            Log.w(TAG, "connect 실패: ${describe(e)}")
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
        try { v.release() } catch (e: Exception) { Log.w(TAG, "release 실패: ${describe(e)}") }
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
        val r = sdkCall<Triple<CapabilityConfig, Map<SeatArea, AreaBounds<Float>>, HvacTemperatureConfigArray?>>("checkHvacTemperatureCapabilities", t) { ok, fail ->
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
        return Probe(true, Cap.HVAC_TEMP.takeIf { cfg.writable() && map.isNotEmpty() })
    }

    private suspend fun probeHvacFan(v: Vehicle, t: Long): Probe {
        val r = sdkCall<Pair<CapabilityConfig, Map<SeatArea, AreaBounds<Int>>>>("checkHvacFanSpeedCapabilities", t) { ok, fail ->
            v.getHVAC().checkHvacFanSpeedCapabilities(
                onComplete = { c: CapabilityConfig, m: Map<SeatArea, AreaBounds<Int>> -> ok(c to m) },
                onFailed = { e: Exception -> fail(e) },
            )
        }
        val (cfg, map) = r.valueOrNull() ?: return Probe(false, null)
        fanAreas = map
        fanReadable = cfg.readable()
        return Probe(true, Cap.HVAC_FAN.takeIf { cfg.writable() && map.isNotEmpty() })
    }

    private suspend fun probeHvacAir(v: Vehicle, t: Long): Probe {
        val r = sdkCall<Pair<CapabilityConfig, List<SeatArea>>>("checkHvacRecirculationCapabilities", t) { ok, fail ->
            v.getHVAC().checkHvacRecirculationCapabilities(
                onComplete = { c: CapabilityConfig, l: List<SeatArea> -> ok(c to l) },
                onFailed = { e: Exception -> fail(e) },
            )
        }
        val (cfg, list) = r.valueOrNull() ?: return Probe(false, null)
        recircAreas = list
        recircReadable = cfg.readable()
        return Probe(true, Cap.HVAC_AIR.takeIf { cfg.writable() && list.isNotEmpty() })
    }

    private suspend fun probeCabinTemp(v: Vehicle, t: Long): Probe {
        val r = sdkCall<Pair<CapabilityConfig, List<SeatArea>>>("checkHvacTemperatureCurrentCapabilities", t) { ok, fail ->
            v.getHVAC().checkHvacTemperatureCurrentCapabilities(
                onComplete = { c: CapabilityConfig, l: List<SeatArea> -> ok(c to l) },
                onFailed = { e: Exception -> fail(e) },
            )
        }
        val (cfg, list) = r.valueOrNull() ?: return Probe(false, null)
        cabinArea = driverOf(list)
        return Probe(true, Cap.HVAC_CABIN_TEMP.takeIf { cfg.readable() && cabinArea != null })
    }

    private suspend fun probeSeatHeater(v: Vehicle, t: Long): Probe {
        val r = sdkCall<Pair<CapabilityConfig, Map<SeatArea, AreaBounds<Int>>>>("checkSeatTemperatureCapabilities", t) { ok, fail ->
            v.getSeat().checkSeatTemperatureCapabilities(
                onComplete = { c: CapabilityConfig, m: Map<SeatArea, AreaBounds<Int>> -> ok(c to m) },
                onFailed = { e: Exception -> fail(e) },
            )
        }
        val (cfg, map) = r.valueOrNull() ?: return Probe(false, null)
        seatAreas = map
        seatReadable = cfg.readable()
        return Probe(true, Cap.SEAT_HEATER.takeIf { cfg.writable() && map.isNotEmpty() })
    }

    private suspend fun probeSteering(v: Vehicle, t: Long): Probe {
        val r = sdkCall<Pair<CapabilityConfig, AreaBounds<Int>>>("checkHvacSteeringWheelHeatCapabilities", t) { ok, fail ->
            v.getSteeringWheel().checkHvacSteeringWheelHeatCapabilities(
                onComplete = { c: CapabilityConfig, b: AreaBounds<Int> -> ok(c to b) },
                onFailed = { e: Exception -> fail(e) },
            )
        }
        val (cfg, bounds) = r.valueOrNull() ?: return Probe(false, null)
        steeringBounds = bounds
        steeringReadable = cfg.readable()
        return Probe(true, Cap.STEERING_HEATER.takeIf { cfg.writable() })
    }

    private suspend fun probeMirror(v: Vehicle, t: Long): Probe {
        val r = sdkCall<Pair<CapabilityConfig, Map<MirrorArea, AreaBounds<Int>>>>("checkHvacSideMirrorHeatCapabilities", t) { ok, fail ->
            v.getSideMirror().checkHvacSideMirrorHeatCapabilities(
                onComplete = { c: CapabilityConfig, m: Map<MirrorArea, AreaBounds<Int>> -> ok(c to m) },
                onFailed = { e: Exception -> fail(e) },
            )
        }
        val (cfg, map) = r.valueOrNull() ?: return Probe(false, null)
        mirrorAreas = map
        mirrorReadable = cfg.readable()
        return Probe(true, Cap.MIRROR_HEATER.takeIf { cfg.writable() && map.isNotEmpty() })
    }

    /** light.ambient = 스위치(ON/OFF) 제어 가능. 색·밝기는 각각 지원될 때만 추가로 쓴다. */
    private suspend fun probeAmbient(v: Vehicle, t: Long): Probe = coroutineScope {
        val light = v.getLight()
        val sw = async {
            sdkCall<Pair<CapabilityConfig, Map<SeatArea, List<LightSwitch>>>>("checkAmbientLightSwitchCapabilities", t) { ok, fail ->
                light.checkAmbientLightSwitchCapabilities(
                    onComplete = { c: CapabilityConfig, m: Map<SeatArea, List<LightSwitch>> -> ok(c to m) },
                    onFailed = { e: Exception -> fail(e) },
                )
            }
        }
        val br = async {
            sdkCall<Pair<CapabilityConfig, Map<SeatArea, AreaBounds<Int>>>>("checkAmbientLightBrightnessCapabilities", t) { ok, fail ->
                light.checkAmbientLightBrightnessCapabilities(
                    onComplete = { c: CapabilityConfig, m: Map<SeatArea, AreaBounds<Int>> -> ok(c to m) },
                    onFailed = { e: Exception -> fail(e) },
                )
            }
        }
        val col = async {
            sdkCall<Pair<CapabilityConfig, List<SeatArea>>>("checkAmbientLightBrightnessColorCapabilities", t) { ok, fail ->
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
        }
        if (colR != null) {
            ambientColorAreas = colR.second
            ambientColorWritable = colR.first.writable() && colR.second.isNotEmpty()
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
            if (cfg.writable() && area != null && onOff) cap = Cap.LIGHT_AMBIENT
        }
        Probe(swR != null || brR != null || colR != null, cap)
    }

    private suspend fun probeEv(v: Vehicle, t: Long): Probe {
        val r = sdkCall<CapabilityConfig>("checkEvBatteryStateOfChargeCapabilities", t) { ok, fail ->
            v.getEvBattery().checkEvBatteryStateOfChargeCapabilities(
                onComplete = { c: CapabilityConfig -> ok(c) },
                onFailed = { e: Exception -> fail(e) },
            )
        }
        val cfg = r.valueOrNull() ?: return Probe(false, null)
        return Probe(true, Cap.EV_BATTERY.takeIf { cfg.readable() })
    }

    private suspend fun probeFuel(v: Vehicle, t: Long): Probe {
        val r = sdkCall<CapabilityConfig>("checkFuelPercentCapabilities", t) { ok, fail ->
            v.getFuel().checkFuelPercentCapabilities(
                onComplete = { c: CapabilityConfig -> ok(c) },
                onFailed = { e: Exception -> fail(e) },
            )
        }
        val cfg = r.valueOrNull() ?: return Probe(false, null)
        return Probe(true, Cap.FUEL_LEVEL.takeIf { cfg.readable() })
    }

    private suspend fun probeWiper(v: Vehicle, t: Long): Probe {
        val r = sdkCall<Pair<CapabilityConfig, Map<WindowArea, List<WiperState>>>>("checkWiperStateCapabilities", t) { ok, fail ->
            v.getWiper().checkWiperStateCapabilities(
                onComplete = { c: CapabilityConfig, m: Map<WindowArea, List<WiperState>> -> ok(c to m) },
                onFailed = { e: Exception -> fail(e) },
            )
        }
        val (cfg, map) = r.valueOrNull() ?: return Probe(false, null)
        wiperArea = PleosMapping.pickArea(map.keys) { WindowValue.FRONT_WINDSHIELD in it.values }
        return Probe(true, Cap.WIPER_STATE.takeIf { cfg.readable() && wiperArea != null })
    }

    // ================================================================ observe

    @Suppress("DEPRECATION") // Gear API는 'Google GEAR_SELECTION 사용' 권고로 deprecated(WARNING)지만 SDK에 대체 API가 없다.
    override fun observe(): Flow<VehicleEvent> = callbackFlow {
        val caps = capabilities()
        val v = vehicle
        val cleanups = mutableListOf<() -> Unit>()

        fun guard(name: String, block: () -> Unit) {
            try { block() } catch (e: Exception) { Log.w(TAG, "$name 실패: ${describe(e)}") }
        }

        if (v != null) {
            // EV 배터리 %
            if (Cap.EV_BATTERY in caps) {
                val ev = v.getEvBattery()
                val l = object : EvBatteryStateOfChargeListener {
                    override fun onEvBatteryStateOfChargeUpdated(value: Int?) {
                        value?.let { trySend(VehicleEvent.Energy(EnergySource.EV_BATTERY, it.toDouble())) }
                    }
                    override fun onFailed(e: Exception) { Log.w(TAG, "EV SOC 리스너 오류: ${describe(e)}") }
                }
                guard("registerEvBatteryStateOfCharge") {
                    ev.registerEvBatteryStateOfCharge(l)
                    cleanups += { ev.unregisterEvBatteryStateOfCharge(l) }
                }
                guard("getEvBatteryStateOfCharge") {
                    ev.getEvBatteryStateOfCharge(
                        onComplete = { value: Int? -> value?.let { trySend(VehicleEvent.Energy(EnergySource.EV_BATTERY, it.toDouble())) } },
                        onFailed = { e: Exception -> Log.w(TAG, "getEvBatteryStateOfCharge 실패: ${describe(e)}") },
                    )
                }
            }
            // 연료 %
            if (Cap.FUEL_LEVEL in caps) {
                val fuel = v.getFuel()
                val l = object : FuelPercentListener {
                    override fun onFuelPercentUpdated(value: Int?) {
                        value?.let { trySend(VehicleEvent.Energy(EnergySource.FUEL, it.toDouble())) }
                    }
                    override fun onFailed(e: Exception) { Log.w(TAG, "연료 리스너 오류: ${describe(e)}") }
                }
                guard("registerFuelPercent") {
                    fuel.registerFuelPercent(l)
                    cleanups += { fuel.unregisterFuelPercent(l) }
                }
                guard("getFuelPercent") {
                    fuel.getFuelPercent(
                        onComplete = { value: Int? -> value?.let { trySend(VehicleEvent.Energy(EnergySource.FUEL, it.toDouble())) } },
                        onFailed = { e: Exception -> Log.w(TAG, "getFuelPercent 실패: ${describe(e)}") },
                    )
                }
            }
            // 와이퍼
            val wArea = wiperArea
            if (Cap.WIPER_STATE in caps && wArea != null) {
                val wiper = v.getWiper()
                val l = object : WiperStateListener {
                    override fun onWiperStateUpdated(state: WiperState?) {
                        PleosMapping.wiperOn(state?.name)?.let { trySend(VehicleEvent.Wiper(it)) }
                    }
                    override fun onFailed(e: Exception) { Log.w(TAG, "와이퍼 리스너 오류: ${describe(e)}") }
                }
                guard("registerWiperState") {
                    wiper.registerWiperState(wArea, l)
                    cleanups += { wiper.unregisterWiperState(wArea, l) }
                }
                guard("getCurrentWiperState") {
                    wiper.getCurrentWiperState(
                        area = wArea,
                        onComplete = { s: WiperState? -> PleosMapping.wiperOn(s?.name)?.let { trySend(VehicleEvent.Wiper(it)) } },
                        onFailed = { e: Exception -> Log.w(TAG, "getCurrentWiperState 실패: ${describe(e)}") },
                    )
                }
            }
            // 기어 → 주행 상태(Cap과 무관하게 항상 시도. 실패하면 UNKNOWN = 주행 취급)
            val gearApi = v.getGear()
            val gl = object : GearSelectionListener {
                override fun onSelected(gear: VehicleGear?) {
                    trySend(VehicleEvent.Drive(PleosMapping.driveStateForGear(gear?.name)))
                }
                override fun onFailed(e: Exception) { Log.w(TAG, "기어 리스너 오류: ${describe(e)}") }
            }
            try {
                gearApi.registerGearSelection(gl)
                cleanups += { gearApi.unregisterGearSelection(gl) }
            } catch (e: Exception) {
                Log.w(TAG, "registerGearSelection 실패: ${describe(e)}")
                trySend(VehicleEvent.Drive(DriveState.UNKNOWN))
            }
            guard("getCurrentGearSelectionState") {
                gearApi.getCurrentGearSelectionState(
                    onComplete = { g: VehicleGear? -> trySend(VehicleEvent.Drive(PleosMapping.driveStateForGear(g?.name))) },
                    onFailed = { e: Exception -> Log.w(TAG, "getCurrentGearSelectionState 실패: ${describe(e)}") },
                )
            }
        }

        // 위치(Android LocationManager, 권한 있을 때만)
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
            cleanups.forEach { c -> try { c() } catch (e: Exception) { Log.w(TAG, "unregister 실패: ${describe(e)}") } }
        }
    }

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
        last?.let { listener.onLocationChanged(it) }
    }

    // ================================================================ 읽기

    override suspend fun readCabinTemp(): Double? {
        val v = vehicle ?: return null
        if (Cap.HVAC_CABIN_TEMP !in capabilities()) return null
        val area = cabinArea ?: return null
        return sdkCall<Float?>("getHvacTemperatureCurrent") { ok, fail ->
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
                readHvacTemp(v, area)?.let { DeviceValue.Temperature(it) }
            }
            DeviceKey.HvacFan -> {
                val area = driverOf(fanAreas.keys)
                if (Cap.HVAC_FAN !in caps || !fanReadable || area == null) return null
                sdkCall<Int?>("getHvacFanSpeed") { ok, fail ->
                    v.getHVAC().getHvacFanSpeed(area = area, onComplete = { x: Int? -> ok(x) }, onFailed = { e: Exception -> fail(e) })
                }.valueOrNull()?.let { DeviceValue.Level(it) }
            }
            DeviceKey.HvacAir -> {
                val area = driverOf(recircAreas)
                if (Cap.HVAC_AIR !in caps || !recircReadable || area == null) return null
                sdkCall<Boolean?>("getHvacRecirculationState") { ok, fail ->
                    v.getHVAC().getHvacRecirculationState(area = area, onComplete = { x: Boolean? -> ok(x) }, onFailed = { e: Exception -> fail(e) })
                }.valueOrNull()?.let { DeviceValue.Air(if (it) AirMode.RECIRC else AirMode.FRESH) }
            }
            is DeviceKey.SeatHeater -> {
                val area = seatAreaFor(key.seat)
                if (Cap.SEAT_HEATER !in caps || !seatReadable || area == null) return null
                PleosMapping.seatLevelFromValue(
                    sdkCall<Int?>("getHvacSeatTemperature") { ok, fail ->
                        v.getSeat().getHvacSeatTemperature(area = area, onComplete = { x: Int? -> ok(x) }, onFailed = { e: Exception -> fail(e) })
                    }.valueOrNull()
                )?.let { DeviceValue.Level(it) }
            }
            DeviceKey.SteeringHeater -> {
                if (Cap.STEERING_HEATER !in caps || !steeringReadable) return null
                PleosMapping.heaterIsOn(
                    sdkCall<Int?>("getHvacSteeringWheelHeatState") { ok, fail ->
                        v.getSteeringWheel().getHvacSteeringWheelHeatState(onComplete = { x: Int? -> ok(x) }, onFailed = { e: Exception -> fail(e) })
                    }.valueOrNull()
                )?.let { DeviceValue.OnOff(it) }
            }
            DeviceKey.MirrorHeater -> {
                val area = mirrorAreas.keys.firstOrNull()
                if (Cap.MIRROR_HEATER !in caps || !mirrorReadable || area == null) return null
                PleosMapping.heaterIsOn(
                    sdkCall<Int?>("getHvacSideMirrorHeatState") { ok, fail ->
                        v.getSideMirror().getHvacSideMirrorHeatState(area = area, onComplete = { x: Int? -> ok(x) }, onFailed = { e: Exception -> fail(e) })
                    }.valueOrNull()
                )?.let { DeviceValue.OnOff(it) }
            }
            DeviceKey.AmbientLight -> readAmbient(v, caps)
        }
    }

    private suspend fun readHvacTemp(v: Vehicle, area: SeatArea): Double? =
        sdkCall<Float?>("getHvacTemperature") { ok, fail ->
            v.getHVAC().getHvacTemperature(
                area = area,
                unit = TemperatureUnit.CELSIUS,
                onComplete = { x: Float? -> ok(x) },
                onFailed = { e: Exception -> fail(e) },
            )
        }.valueOrNull()?.toDouble()

    private suspend fun readAmbient(v: Vehicle, caps: Set<Cap>): DeviceValue? {
        val area = ambientArea
        if (Cap.LIGHT_AMBIENT !in caps || !ambientReadable || area == null) return null
        val light = v.getLight()
        val sw = sdkCall<LightSwitch?>("getAmbientLightSwitch") { ok, fail ->
            light.getAmbientLightSwitch(area = area, onComplete = { x: LightSwitch? -> ok(x) }, onFailed = { e: Exception -> fail(e) })
        }.valueOrNull() ?: return null
        val on = sw == LightSwitch.ON || sw == LightSwitch.AUTOMATIC
        val colorArea = driverOf(ambientColorAreas)
        val color: AmbientColor? = if (colorArea != null) {
            PleosMapping.nearestAmbientColor(
                sdkCall<Int?>("getAmbientLightBrightnessColor") { ok, fail ->
                    light.getAmbientLightBrightnessColor(area = colorArea, onComplete = { x: Int? -> ok(x) }, onFailed = { e: Exception -> fail(e) })
                }.valueOrNull()
            )
        } else null
        val brArea = driverOf(ambientBrightness.keys)
        val brightness: AmbientBrightness? = if (brArea != null) {
            val b = ambientBrightness.getValue(brArea)
            PleosMapping.brightnessFromValue(
                sdkCall<Int?>("getAmbientLightBrightness") { ok, fail ->
                    light.getAmbientLightBrightness(area = brArea, onComplete = { x: Int? -> ok(x) }, onFailed = { e: Exception -> fail(e) })
                }.valueOrNull(),
                b.minValue, b.maxValue,
            )
        } else null
        return DeviceValue.Ambient(on = on, color = color, brightness = brightness)
    }

    // ================================================================ 실행

    override suspend fun apply(action: Action): ActionResult {
        val v = vehicle ?: return ActionResult.Unsupported
        if (action.requiredCap !in capabilities()) return ActionResult.Unsupported
        return try {
            when (action) {
                is Action.HvacTemp -> applyHvacTemp(v, action)
                is Action.HvacFan -> applyAll(fanAreas.entries, "setHvacFanSpeed") { (area, b), ok, fail ->
                    v.getHVAC().setHvacFanSpeed(
                        area = area,
                        value = PleosMapping.clampInt(action.level, b.minValue, b.maxValue),
                        onComplete = { _: SeatArea, _: Int? -> ok(Unit) },
                        onFailed = { e: Exception -> fail(e) },
                    )
                }
                is Action.HvacAir -> applyAll(recircAreas, "setHvacRecirculation") { area, ok, fail ->
                    v.getHVAC().setHvacRecirculation(
                        area = area,
                        value = action.mode == AirMode.RECIRC,
                        onComplete = { _: SeatArea, _: Boolean? -> ok(Unit) },
                        onFailed = { e: Exception -> fail(e) },
                    )
                }
                is Action.SeatHeater -> {
                    val area = seatAreaFor(action.seat) ?: return ActionResult.Unsupported
                    val b = seatAreas.getValue(area)
                    applyAll(listOf(area), "setHvacSeatTemperature") { a, ok, fail ->
                        v.getSeat().setHvacSeatTemperature(
                            area = a,
                            value = PleosMapping.heaterLevel(action.level, b.minValue, b.maxValue),
                            onComplete = { _: SeatArea, _: Int? -> ok(Unit) },
                            onFailed = { e: Exception -> fail(e) },
                        )
                    }
                }
                is Action.SteeringHeater -> {
                    val b = steeringBounds ?: return ActionResult.Unsupported
                    applyAll(listOf(b), "setHvacSteeringWheelHeat") { bounds, ok, fail ->
                        v.getSteeringWheel().setHvacSteeringWheelHeat(
                            value = PleosMapping.heaterOnOffValue(action.on, bounds.minValue, bounds.maxValue),
                            onComplete = { _: Int? -> ok(Unit) },
                            onFailed = { e: Exception -> fail(e) },
                        )
                    }
                }
                is Action.MirrorHeater -> applyAll(mirrorAreas.entries, "setHvacSideMirrorHeat") { (area, b), ok, fail ->
                    v.getSideMirror().setHvacSideMirrorHeat(
                        area = area,
                        value = PleosMapping.heaterOnOffValue(action.on, b.minValue, b.maxValue),
                        onComplete = { _: MirrorArea, _: Int? -> ok(Unit) },
                        onFailed = { e: Exception -> fail(e) },
                    )
                }
                is Action.AmbientLight -> applyAmbient(v, action)
                // 엔진은 Speak를 넘기지 않는다(알림 문장에 합쳐 Android TTS로 읽는다). navi.waypoint는 이번 범위 밖.
                is Action.Speak, Action.SuggestCharger -> ActionResult.Unsupported
            }
        } catch (e: Exception) {
            Log.w(TAG, "apply(${action.type}) 예외: ${describe(e)}")
            ActionResult.Failed(describe(e))
        }
    }

    private suspend fun applyHvacTemp(v: Vehicle, action: Action.HvacTemp): ActionResult {
        val range = tempRange
        val target = when (action.mode) {
            HvacTempMode.SET -> action.value
            HvacTempMode.DELTA -> { // 엔진이 SET으로 바꿔 넘기지만, 혹시 DELTA가 오면 현재값 기준으로 계산
                val area = driverOf(tempAreas.keys) ?: return ActionResult.Unsupported
                val cur = readHvacTemp(v, area) ?: return ActionResult.Failed("현재 설정 온도를 읽을 수 없음")
                cur + action.value
            }
        }
        return applyAll(tempAreas.entries, "setHvacTemperature") { (area, b), ok, fail ->
            val r = range ?: TempRange(b.minValue.toDouble(), b.maxValue.toDouble(), 0.0)
            v.getHVAC().setHvacTemperature(
                area = area,
                value = PleosMapping.clampTemp(target, r).toFloat(),
                unit = TemperatureUnit.CELSIUS,
                onComplete = { _: SeatArea, _: Float? -> ok(Unit) },
                onFailed = { e: Exception -> fail(e) },
            )
        }
    }

    private suspend fun applyAmbient(v: Vehicle, action: Action.AmbientLight): ActionResult {
        val area = ambientArea ?: return ActionResult.Unsupported
        val light = v.getLight()
        val sw = applyAll(listOf(area), "setAmbientLightSwitch") { a, ok, fail ->
            light.setAmbientLightSwitch(
                area = a,
                value = if (action.on) LightSwitch.ON else LightSwitch.OFF,
                onComplete = { _: SeatArea, _: LightSwitch? -> ok(Unit) },
                onFailed = { e: Exception -> fail(e) },
            )
        }
        if (sw != ActionResult.Success || !action.on) return sw
        val color = action.color
        if (color != null && ambientColorWritable) {
            val r = applyAll(ambientColorAreas, "setAmbientLightBrightnessColor") { a, ok, fail ->
                light.setAmbientLightBrightnessColor(
                    area = a,
                    value = PleosMapping.ambientArgb(color),
                    onComplete = { _: SeatArea, _: Int -> ok(Unit) },
                    onFailed = { e: Exception -> fail(e) },
                )
            }
            if (r != ActionResult.Success) return r
        }
        val brightness = action.brightness
        if (brightness != null && ambientBrightnessWritable) {
            val r = applyAll(ambientBrightness.entries, "setAmbientLightBrightness") { (a, b), ok, fail ->
                light.setAmbientLightBrightness(
                    area = a,
                    value = PleosMapping.brightnessValue(brightness, b.minValue, b.maxValue),
                    onComplete = { _: SeatArea, _: Int -> ok(Unit) },
                    onFailed = { e: Exception -> fail(e) },
                )
            }
            if (r != ActionResult.Success) return r
        }
        return ActionResult.Success
    }

    /** 여러 영역에 같은 set을 순서대로 보낸다. 하나라도 실패하면 Failed(첫 실패 이유). 영역이 없으면 Unsupported. */
    private suspend fun <T> applyAll(
        targets: Collection<T>,
        name: String,
        call: (T, (Unit) -> Unit, (Exception) -> Unit) -> Unit,
    ): ActionResult {
        if (targets.isEmpty()) return ActionResult.Unsupported
        for (t in targets) {
            val r = sdkCall<Unit>(name) { ok, fail -> call(t, ok, fail) }
            if (r is SdkResult.Err) return ActionResult.Failed(r.reason)
        }
        return ActionResult.Success
    }

    // ================================================================ 공통

    private sealed interface SdkResult<out T> {
        data class Ok<T>(val value: T) : SdkResult<T>
        data class Err(val reason: String) : SdkResult<Nothing>
    }

    private fun <T> SdkResult<T>.valueOrNull(): T? = (this as? SdkResult.Ok<T>)?.value

    /**
     * 콜백 SDK 호출 하나를 suspend로 감싼다. 타임아웃·예외·중복 콜백을 모두 흡수한다.
     * SDK는 서비스 연결 전 호출을 큐에 쌓아 두므로, 연결이 안 되면 콜백이 오지 않는다 → 타임아웃으로 끝난다.
     */
    private suspend fun <T> sdkCall(
        name: String,
        timeoutMs: Long = CALL_TIMEOUT_MS,
        block: (ok: (T) -> Unit, fail: (Exception) -> Unit) -> Unit,
    ): SdkResult<T> {
        val r = withTimeoutOrNull(timeoutMs) {
            suspendCancellableCoroutine<SdkResult<T>> { cont ->
                val done = AtomicBoolean(false)
                fun finish(res: SdkResult<T>) {
                    if (done.compareAndSet(false, true) && cont.isActive) {
                        try { cont.resume(res) } catch (e: IllegalStateException) { Log.d(TAG, "$name 중복 resume 무시") }
                    }
                }
                try {
                    block({ value -> finish(SdkResult.Ok(value)) }, { e -> finish(SdkResult.Err(describe(e))) })
                } catch (e: Exception) {
                    finish(SdkResult.Err(describe(e)))
                }
            }
        } ?: SdkResult.Err("timeout ${timeoutMs}ms")
        if (r is SdkResult.Err) Log.w(TAG, "$name 실패: ${r.reason}")
        return r
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
        private const val LOCATION_MIN_TIME_MS = 5_000L
        private const val LOCATION_MIN_DISTANCE_M = 20f
    }
}
