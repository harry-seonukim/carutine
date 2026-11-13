package ai.pleos.playground.vehicle.utils

import ai.pleos.playground.vehicle.api.model.common.TripStatistics
import ai.pleos.playground.vehicle.common.fromJsonOrNull
import ai.pleos.playground.vehicle.constant.control.TripStatisticType
import java.util.concurrent.ConcurrentHashMap

internal object TripStatisticsUtil {
    private val configArrayMap = ConcurrentHashMap<String, List<Int>>()

    fun updateConfigArray(propertyKey: String, configArray: List<Int>?) {
        if (configArray.isNullOrEmpty()) {
            configArrayMap.remove(propertyKey)
            return
        }
        configArrayMap[propertyKey] = configArray
    }

    fun supportsType(propertyKey: String, tripStatisticType: TripStatisticType): Boolean {
        val configArray = configArrayMap[propertyKey] ?: return false
        return configArray.any { TripStatisticType.findById(it) == tripStatisticType }
    }

    fun parseFloatStatistics(propertyKey: String, value: String?): TripStatistics<Float>? {
        val raw = runCatching { value.fromJsonOrNull<FloatArray>()?.toList() }.getOrNull() ?: return null
        return buildStatistics(propertyKey, raw)
    }

    fun parseIntStatistics(propertyKey: String, value: String?): TripStatistics<Int>? {
        val raw = runCatching { value.fromJsonOrNull<IntArray>()?.toList() }.getOrNull() ?: return null
        return buildStatistics(propertyKey, raw)
    }

    private fun <T> buildStatistics(propertyKey: String, raw: List<T>): TripStatistics<T> {
        val typeIndexes = resolveTypeIndexes(propertyKey)
        return TripStatistics(
            current = raw.getOrNull(typeIndexes[TripStatisticType.CURRENT] ?: -1),
            instantaneous = raw.getOrNull(typeIndexes[TripStatisticType.INSTANTANEOUS] ?: -1),
            cumulative = raw.getOrNull(typeIndexes[TripStatisticType.CUMULATIVE] ?: -1),
            sinceRefill = raw.getOrNull(typeIndexes[TripStatisticType.SINCE_REFILL] ?: -1),
            raw = raw
        )
    }

    private fun resolveTypeIndexes(propertyKey: String): Map<TripStatisticType, Int> {
        val configured = configArrayMap[propertyKey]
            ?.mapIndexedNotNull { index, rawTypeId ->
                TripStatisticType.findById(rawTypeId)?.let { type -> type to index }
            }
            ?.toMap()
            .orEmpty()

        if (configured.isNotEmpty()) return configured

        return TripStatisticType.entries.associateWith { it.id }
    }
}
