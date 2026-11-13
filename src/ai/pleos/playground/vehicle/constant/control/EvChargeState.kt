package ai.pleos.playground.vehicle.constant.control

import ai.pleos.playground.vehicle.constant.VehicleEVChargeStateValue

/**
 * Represents the EV charge state.
 */
enum class EvChargeState(internal val id: Int) {
    UNKNOWN(VehicleEVChargeStateValue.UNKNOWN),
    CHARGING(VehicleEVChargeStateValue.CHARGING),
    FULLY_CHARGED(VehicleEVChargeStateValue.FULLY_CHARGED),
    NOT_CHARGING(VehicleEVChargeStateValue.NOT_CHARGING),
    ERROR(VehicleEVChargeStateValue.ERROR)
    ;

    internal companion object {
        fun findById(id: Int?): EvChargeState? {
            return entries.find { it.id == id }
        }
    }
}
