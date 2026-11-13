package ai.pleos.playground.vehicle.constant.area

import ai.pleos.playground.vehicle.constant.VehicleAreaValue.DoorConstant

/**
 * Represents available door area values.
 */
enum class DoorValue(override val id: Int) : AreaValue {
    ROW_1_LEFT(DoorConstant.ROW_1_LEFT), ROW_1_RIGHT(DoorConstant.ROW_1_RIGHT),
    ROW_2_LEFT(DoorConstant.ROW_2_LEFT), ROW_2_RIGHT(DoorConstant.ROW_2_RIGHT),
    ROW_3_LEFT(DoorConstant.ROW_3_LEFT), ROW_3_RIGHT(DoorConstant.ROW_3_RIGHT),
    HOOD(DoorConstant.HOOD), REAR(DoorConstant.REAR),
}
