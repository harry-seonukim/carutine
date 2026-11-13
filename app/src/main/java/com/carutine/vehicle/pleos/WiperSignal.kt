package com.carutine.vehicle.pleos

import com.carutine.engine.VehicleEvent

/**
 * 와이퍼 원시값 → [VehicleEvent.Wiper]. Android·Pleos 클래스를 쓰지 않아 JVM 단위 테스트로 흐름을 고정한다.
 *
 * 값은 세 경로로 들어온다: 등록 직후 현재값 읽기(기준선), SDK 리스너 콜백, 주기 읽기(리스너가 조용히 안 오는 경우 대비).
 * 어느 경로든 매핑 결과가 **바뀔 때만** 방출한다. 첫 방출이 기준선이 되므로(엔진 TriggerDetector는 첫 값을 사건으로 보지 않는다)
 * 등록 직후 OFF를 먼저 흘려 두면 이후 첫 ON이 바로 '와이퍼 켜짐' 사건이 된다.
 *
 * 매핑: WiperState ON=켜짐, OFF·SERVICE=꺼짐, OTHER·null이면 WiperSwitch로 보조 판단(OFF=꺼짐, MIST·INTERMITTENT·CONTINUOUS·AUTO 등=켜짐).
 */
class WiperSignal(
    private val log: (String) -> Unit,
    private val send: (VehicleEvent) -> Boolean,
) {
    @Volatile var last: Boolean? = null
        private set

    /**
     * 원시값 하나를 처리한다. [source]는 진단용(기준선/콜백/주기 읽기). [quiet]면 값이 바뀔 때만 로그를 남긴다.
     * 반환: 매핑 결과(판단 불가면 null).
     */
    @Synchronized
    fun onRaw(source: String, stateName: String?, switchName: String? = null, quiet: Boolean = false): Boolean? {
        val mapped = PleosMapping.wiperOn(stateName) ?: PleosMapping.wiperSwitchOn(switchName)
        val changed = mapped != null && mapped != last
        if (!quiet || changed) log("와이퍼 $source raw state=$stateName switch=$switchName → on=$mapped")
        if (!changed) return mapped
        val first = last == null
        last = mapped
        val ok = send(VehicleEvent.Wiper(mapped!!))
        log("  → VehicleEvent.Wiper($mapped) ${if (ok) "방출" else "방출 실패"}${if (first) " (기준선)" else ""}")
        return mapped
    }

    /** WiperState만으로 판단이 안 될 때(OTHER·null) 스위치 값을 더 읽어야 하는지. */
    fun needsSwitch(stateName: String?): Boolean = PleosMapping.wiperOn(stateName) == null
}
