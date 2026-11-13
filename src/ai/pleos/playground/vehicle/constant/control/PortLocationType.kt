package ai.pleos.playground.vehicle.constant.control

import ai.pleos.playground.vehicle.constant.PortLocationTypeValue

/**
 * Represents the charge port location.
 */
enum class PortLocationType(internal val id: Int) {
    UNKNOWN(PortLocationTypeValue.UNKNOWN),
    FRONT_LEFT(PortLocationTypeValue.FRONT_LEFT),
    FRONT_RIGHT(PortLocationTypeValue.FRONT_RIGHT),
    REAR_RIGHT(PortLocationTypeValue.REAR_RIGHT),
    REAR_LEFT(PortLocationTypeValue.REAR_LEFT),
    FRONT(PortLocationTypeValue.FRONT),
    REAR(PortLocationTypeValue.REAR)
    ;

    internal companion object {
        fun findById(id: Int?): PortLocationType? {
            return entries.find { it.id == id }
        }

        fun Collection<Int>?.toPortLocationArrayOrNull(): List<PortLocationType>? {
            return this?.let {
                val mapping = entries.associateBy { it.id }
                this.filter { mapping.containsKey(it) }
                    .map { mapping[it] ?: UNKNOWN }
            }
        }
    }

}
