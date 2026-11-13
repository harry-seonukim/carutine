package ai.pleos.playground.vehicle.constant.control

import ai.pleos.playground.vehicle.constant.VehicleOilLevelValue

/**
 * Represents the oil level state.
 */
enum class OilLevelState(internal val id: Int) {
    LOW(VehicleOilLevelValue.LOW),
    HIGH(VehicleOilLevelValue.HIGH),
    NORMAL(VehicleOilLevelValue.NORMAL),
    ERROR(VehicleOilLevelValue.ERROR),
    CRITICALLY_LOW(VehicleOilLevelValue.CRITICALLY_LOW),
    ;

    internal companion object {
        fun findById(id: Int?): OilLevelState? {
            return entries.find { it.id == id }
        }
    }
}
