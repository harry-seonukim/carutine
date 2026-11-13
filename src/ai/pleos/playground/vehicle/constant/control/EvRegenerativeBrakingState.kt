package ai.pleos.playground.vehicle.constant.control

import ai.pleos.playground.vehicle.constant.VehicleEVRegenerativeBrakingStateValue

/**
 * Represents the EV regenerative braking state.
 */
enum class EvRegenerativeBrakingState(internal val id: Int) {
    UNKNOWN(VehicleEVRegenerativeBrakingStateValue.UNKNOWN),
    DISABLED(VehicleEVRegenerativeBrakingStateValue.DISABLED),
    PARTIALLY_ENABLED(VehicleEVRegenerativeBrakingStateValue.PARTIALLY_ENABLED),
    FULLY_ENABLED(VehicleEVRegenerativeBrakingStateValue.FULLY_ENABLED)
    ;

    internal companion object {
        fun findById(id: Int?): EvRegenerativeBrakingState? {
            return entries.find { it.id == id }
        }
    }
}
