package ai.pleos.playground.vehicle.constant.unit

import ai.pleos.playground.vehicle.constant.VehicleUnitValue


/**
 * Represents the temperature unit.
 */
enum class TemperatureUnit(internal val id: Int) {
    /** Degrees Celsius (°C). */
    CELSIUS(VehicleUnitValue.CELSIUS),

    /** Degrees Fahrenheit (°F). */
    FAHRENHEIT(VehicleUnitValue.FAHRENHEIT)
    ;

    internal companion object {
        fun findById(id: Int?): TemperatureUnit? {
            return values().find { it.id == id }
        }
    }
}
