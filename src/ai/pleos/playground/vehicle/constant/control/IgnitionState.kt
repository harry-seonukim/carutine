package ai.pleos.playground.vehicle.constant.control

import ai.pleos.playground.vehicle.constant.VehicleIgnitionStateValue


/**
 * @suppress
 * @hide
 * Ignition state
 *
 * @property id
 */
enum class IgnitionState(internal val id: Int) {
    UNDEFINED(VehicleIgnitionStateValue.UNDEFINED),
    LOCK(VehicleIgnitionStateValue.LOCK),
    OFF(VehicleIgnitionStateValue.OFF),
    ACC(VehicleIgnitionStateValue.ACC),
    ON(VehicleIgnitionStateValue.ON),
    START(VehicleIgnitionStateValue.START)
    ;

    internal companion object {
        fun findById(id: Int?): IgnitionState? {
            return entries.find { it.id == id }
        }
    }
}