package com.carutine.engine

import com.carutine.model.EnergySource
import com.carutine.model.PlaceKey
import com.carutine.model.Trigger
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/** 엔진이 반응하는 '사건'. 상태 값의 변화(경계 넘음)만 사건이다(작업 문서 5장 1. 사건 감지). */
sealed interface TriggerEvent {
    data class LocationEntered(val place: PlaceKey) : TriggerEvent
    data class LocationExited(val place: PlaceKey) : TriggerEvent
    data class EnergyDropped(val source: EnergySource, val from: Double, val to: Double) : TriggerEvent
    data object WiperTurnedOn : TriggerEvent

    fun matches(t: Trigger): Boolean = when (t) {
        is Trigger.LocationEnter -> this is LocationEntered && place == t.place
        is Trigger.LocationExit -> this is LocationExited && place == t.place
        // 작업 문서 5장: 기준 '이상' → '미만'으로 바뀐 순간. 기준 아래에서 오르내리면 사건 아님.
        is Trigger.EnergyBelow -> this is EnergyDropped && source == t.source && from >= t.percent && to < t.percent
        Trigger.WiperOn -> this is WiperTurnedOn
    }
}

/**
 * 원시 상태 값 → 사건. 앱이 시작된 뒤 첫 값은 기준 상태로만 저장하고 사건으로 보지 않는다.
 * 상태를 가진 객체이므로 엔진 안에서 Mutex로 한 번에 하나씩만 호출한다.
 */
class TriggerDetector(private val config: EngineConfig) {

    private enum class Zone { INSIDE, OUTSIDE }

    private val placeZone = mutableMapOf<PlaceKey, Zone>()
    private val lastEnergy = mutableMapOf<EnergySource, Double>()
    private var lastWiper: Boolean? = null

    fun reset() {
        placeZone.clear(); lastEnergy.clear(); lastWiper = null
    }

    fun onEvent(e: VehicleEvent, places: Map<PlaceKey, Place>): List<TriggerEvent> = when (e) {
        is VehicleEvent.Location -> onLocation(e, places)
        is VehicleEvent.Energy -> onEnergy(e)
        is VehicleEvent.Wiper -> onWiper(e)
        is VehicleEvent.Drive -> emptyList()   // 향후 '주행 시작' 조건 확장용. 지금은 사건 아님.
    }

    private fun onLocation(e: VehicleEvent.Location, places: Map<PlaceKey, Place>): List<TriggerEvent> {
        // 삭제된 장소의 상태는 버린다(다시 등록되면 첫 값부터 기준으로 잡는다).
        placeZone.keys.retainAll(places.keys)
        val out = mutableListOf<TriggerEvent>()
        for ((key, place) in places) {
            val d = distanceM(e.lat, e.lng, place.lat, place.lng)
            val radius = place.radiusM.takeIf { it > 0 } ?: config.radiusM
            val observed: Zone? = when {
                d <= radius -> Zone.INSIDE
                d > radius + config.hysteresisM -> Zone.OUTSIDE
                else -> null   // 흔들림 구간: 이전 상태 유지
            }
            if (observed == null) continue
            val prev = placeZone.put(key, observed)
            if (prev == null || prev == observed) continue   // 첫 값은 기준만
            out += if (observed == Zone.INSIDE) TriggerEvent.LocationEntered(key) else TriggerEvent.LocationExited(key)
        }
        return out
    }

    private fun onEnergy(e: VehicleEvent.Energy): List<TriggerEvent> {
        val prev = lastEnergy.put(e.source, e.percent) ?: return emptyList()
        return if (e.percent < prev) listOf(TriggerEvent.EnergyDropped(e.source, prev, e.percent)) else emptyList()
    }

    private fun onWiper(e: VehicleEvent.Wiper): List<TriggerEvent> {
        val prev = lastWiper
        lastWiper = e.on
        return if (prev == false && e.on) listOf(TriggerEvent.WiperTurnedOn) else emptyList()
    }

    companion object {
        /** 하버사인 거리(m). 반경 200m 수준에서는 충분히 정확하다. */
        fun distanceM(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
            val r = 6_371_000.0
            val dLat = Math.toRadians(lat2 - lat1)
            val dLng = Math.toRadians(lng2 - lng1)
            val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
            return 2 * r * asin(sqrt(a))
        }
    }
}
