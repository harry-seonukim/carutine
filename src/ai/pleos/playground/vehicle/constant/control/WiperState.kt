package ai.pleos.playground.vehicle.constant.control

import ai.pleos.playground.vehicle.constant.VehicleWiperStateValue

/**
 * Represents the wiper state.
 */
enum class WiperState(internal val id: Int) {
    OTHER(VehicleWiperStateValue.OTHER),
    OFF(VehicleWiperStateValue.OFF),
    ON(VehicleWiperStateValue.ON),
    SERVICE(VehicleWiperStateValue.SERVICE),
    ;

    internal companion object {
        fun findById(id: Int?): WiperState? {
            return entries.find { it.id == id }
        }
    }
}
