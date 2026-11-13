package ai.pleos.playground.vehicle.constant.unit

import ai.pleos.playground.vehicle.constant.VehicleUnitValue

/**
 * Represents the distance display unit.
 */
enum class DistanceDisplayUnit(internal val id: Int) {
    /** Kilometers (km). */
    KILOMETER(VehicleUnitValue.KILOMETER),

    /** Miles (mi). */
    MILE(VehicleUnitValue.MILE)
    ;

    internal companion object {
        fun findById(id: Int?): DistanceDisplayUnit? {
            return values().find { it.id == id }
        }
    }
}
