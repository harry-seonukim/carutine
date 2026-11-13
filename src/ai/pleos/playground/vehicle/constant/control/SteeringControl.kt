package ai.pleos.playground.vehicle.constant.control

import ai.pleos.playground.vehicle.constant.VehicleSteeringControlValue

/**
 * Represents the steering control state.
 */
enum class SteeringControl(internal val id: Int) {
    OFF(VehicleSteeringControlValue.OFF_ID),
    READY(VehicleSteeringControlValue.READY_ID),
    ACTIVE(VehicleSteeringControlValue.ACTIVE_ID)
    ;

    internal companion object {
        fun findById(id: Int?): SteeringControl? {
            return entries.find { it.id == id }
        }
    }
}
