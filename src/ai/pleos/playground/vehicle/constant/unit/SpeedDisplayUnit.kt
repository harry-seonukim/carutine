package ai.pleos.playground.vehicle.constant.unit

import ai.pleos.playground.vehicle.constant.VehicleUnitValue


/**
 * Represents the speed display unit.
 */
enum class SpeedDisplayUnit(internal val id: Int) {
    /** Meters per second (m/s). */
    METER_PER_SEC(VehicleUnitValue.METER_PER_SEC),

    /** Miles per hour (mph). */
    MILES_PER_HOUR(VehicleUnitValue.MILES_PER_HOUR),

    /** Kilometers per hour (km/h). */
    KILOMETERS_PER_HOUR(VehicleUnitValue.KILOMETERS_PER_HOUR),
    ;


    internal companion object {
        fun findById(id: Int?): SpeedDisplayUnit? {
            return values().find { it.id == id }
        }
    }
}
