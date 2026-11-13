package ai.pleos.playground.vehicle.constant.control

/**
 * Represents the type of trip statistic.
 */
enum class TripStatisticType(val id: Int) {
    CURRENT(0),
    INSTANTANEOUS(1),
    CUMULATIVE(2),
    SINCE_REFILL(3),
    ;

    companion object {
        fun findById(id: Int?): TripStatisticType? {
            return entries.find { it.id == id }
        }
    }
}
