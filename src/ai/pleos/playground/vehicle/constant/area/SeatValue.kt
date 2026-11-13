package ai.pleos.playground.vehicle.constant.area

import ai.pleos.playground.vehicle.constant.VehicleAreaValue.SeatConstant

/**
 * Represents available seat area values.
 */
enum class SeatValue(override val id: Int) : AreaValue {
    ROW_1_LEFT(SeatConstant.ROW_1_LEFT), ROW_1_CENTER(SeatConstant.ROW_1_CENTER), ROW_1_RIGHT(SeatConstant.ROW_1_RIGHT),
    ROW_2_LEFT(SeatConstant.ROW_2_LEFT), ROW_2_CENTER(SeatConstant.ROW_2_CENTER), ROW_2_RIGHT(SeatConstant.ROW_2_RIGHT),
    ROW_3_LEFT(SeatConstant.ROW_3_LEFT), ROW_3_CENTER(SeatConstant.ROW_3_CENTER), ROW_3_RIGHT(SeatConstant.ROW_3_RIGHT),
}
