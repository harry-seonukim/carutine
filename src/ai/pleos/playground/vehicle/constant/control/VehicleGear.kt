package ai.pleos.playground.vehicle.constant.control

import ai.pleos.playground.vehicle.constant.VehicleGearValue


/**
 * @suppress
 * @hide
 * Vehicle gear
 *
 * @property id
 */
enum class VehicleGear(internal val id: Int) {
    GEAR_UNKNOWN(VehicleGearValue.GEAR_UNKNOWN),
    GEAR_NEUTRAL(VehicleGearValue.GEAR_NEUTRAL),
    GEAR_REVERSE(VehicleGearValue.GEAR_REVERSE),
    GEAR_PARK(VehicleGearValue.GEAR_PARK),
    GEAR_DRIVE(VehicleGearValue.GEAR_DRIVE),
    GEAR_1(VehicleGearValue.GEAR_1),
    GEAR_2(VehicleGearValue.GEAR_2),
    GEAR_3(VehicleGearValue.GEAR_3),
    GEAR_4(VehicleGearValue.GEAR_4),
    GEAR_5(VehicleGearValue.GEAR_5),
    GEAR_6(VehicleGearValue.GEAR_6),
    GEAR_7(VehicleGearValue.GEAR_7),
    GEAR_8(VehicleGearValue.GEAR_8),
    GEAR_9(VehicleGearValue.GEAR_9)
    ;

    internal companion object {
        fun findById(id: Int?): VehicleGear? {
            return entries.find { it.id == id }
        }
    }
}