package ai.pleos.playground.vehicle.constant.control

import ai.pleos.playground.vehicle.constant.VehicleDrivingModeValue

/**
 * Represents the driving mode.
 */
enum class DrivingModeType(internal val id: Int) {
    NORMAL(VehicleDrivingModeValue.NORMAL),
    ECO(VehicleDrivingModeValue.ECO),
    SPORT(VehicleDrivingModeValue.SPORT),
    ;

    internal companion object {
        fun findById(id: Int?): DrivingModeType? {
            return entries.find { it.id == id }
        }
    }
}
