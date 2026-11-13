package ai.pleos.playground.vehicle.constant.control

import ai.pleos.playground.vehicle.constant.VehicleLightSwitchValue

/**
 * Represents the light switch state.
 */
enum class LightSwitch(internal val id: Int) {
    OFF(VehicleLightSwitchValue.OFF),
    ON(VehicleLightSwitchValue.ON),
    DAYTIME_RUNNING(VehicleLightSwitchValue.DAYTIME_RUNNING),
    AUTOMATIC(VehicleLightSwitchValue.AUTOMATIC)
    ;

    internal companion object {
        fun findById(id: Int?): LightSwitch? {
            return entries.find { it.id == id }
        }
    }
}
