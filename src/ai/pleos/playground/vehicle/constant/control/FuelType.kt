package ai.pleos.playground.vehicle.constant.control

import ai.pleos.playground.vehicle.constant.VehicleFuelTypeValue

/**
 * Represents the fuel type.
 */
enum class FuelType(internal val id: Int) {
    BIODIESEL(VehicleFuelTypeValue.BIODIESEL),
    CNG(VehicleFuelTypeValue.CNG),
    DIESEL_1(VehicleFuelTypeValue.DIESEL_1),
    DIESEL_2(VehicleFuelTypeValue.DIESEL_2),
    E85(VehicleFuelTypeValue.E85),
    ELECTRIC(VehicleFuelTypeValue.ELECTRIC),
    HYDROGEN(VehicleFuelTypeValue.HYDROGEN),
    LEADED(VehicleFuelTypeValue.LEADED),
    LNG(VehicleFuelTypeValue.LNG),
    LPG(VehicleFuelTypeValue.LPG),
    OTHER(VehicleFuelTypeValue.OTHER),
    UNKNOWN(VehicleFuelTypeValue.UNKNOWN),
    UNLEADED(VehicleFuelTypeValue.UNLEADED)
    ;

    internal companion object {
        fun findById(id: Int?): FuelType? {
            return entries.find { it.id == id }
        }
    }
}
