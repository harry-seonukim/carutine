package ai.pleos.playground.vehicle.constant.unit

import ai.pleos.playground.vehicle.constant.VehicleUnitValue

/**
 * Represents the EV battery display unit.
 */
enum class EvBatteryDisplayUnit(internal val id: Int) {
    /** Watt-hours (Wh). */
    WATT_HOUR(VehicleUnitValue.WATT_HOUR),

    /** Ampere-hours (Ah). */
    AMPERE_HOURS(VehicleUnitValue.AMPERE_HOURS),

    /** Kilowatt-hours (kWh). */
    KILOWATT_HOUR(VehicleUnitValue.KILOWATT_HOUR)
    ;

    internal companion object {
        fun findById(id: Int?): EvBatteryDisplayUnit? {
            return values().find { it.id == id }
        }
    }
}
