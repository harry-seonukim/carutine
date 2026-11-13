package ai.pleos.playground.vehicle.constant.control

import ai.pleos.playground.vehicle.constant.VehicleLightStateValue

/**
 * Represents the light state.
 */
enum class LightState(internal val id: Int) {
    OFF(VehicleLightStateValue.OFF),
    ON(VehicleLightStateValue.ON),
    DAYTIME_RUNNING(VehicleLightStateValue.DAYTIME_RUNNING)
    ;

    internal companion object {
        fun findById(id: Int?): LightState? {
            return entries.find { it.id == id }
        }
    }
}
