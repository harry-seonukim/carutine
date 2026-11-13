package ai.pleos.playground.vehicle.api.model.common

import ai.pleos.playground.vehicle.constant.control.TripStatisticType

/**
 * Represents trip statistics grouped by type.
 *
 * @property current The current value.
 * @property instantaneous The instantaneous value.
 * @property cumulative The cumulative value.
 * @property sinceRefill The value since refill.
 * @property raw The full list of available values.
 */
data class TripStatistics<T>(
    val current: T? = null,
    val instantaneous: T? = null,
    val cumulative: T? = null,
    val sinceRefill: T? = null,
    val raw: List<T?> = emptyList()
) {
    fun get(type: TripStatisticType): T? {
        return when (type) {
            TripStatisticType.CURRENT -> current
            TripStatisticType.INSTANTANEOUS -> instantaneous
            TripStatisticType.CUMULATIVE -> cumulative
            TripStatisticType.SINCE_REFILL -> sinceRefill
        }
    }
}
