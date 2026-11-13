package ai.pleos.playground.vehicle.constant.area

import ai.pleos.playground.vehicle.constant.VehicleAreaValue.WindowConstant

/**
 * Represents available window area values.
 */
enum class WindowValue(override val id: Int) : AreaValue {
    FRONT_WINDSHIELD(WindowConstant.FRONT_WINDSHIELD), REAR_WINDSHIELD(WindowConstant.REAR_WINDSHIELD),
    ROW_1_LEFT(WindowConstant.ROW_1_LEFT), ROW_1_RIGHT(WindowConstant.ROW_1_RIGHT),
    ROW_2_LEFT(WindowConstant.ROW_2_LEFT), ROW_2_RIGHT(WindowConstant.ROW_2_RIGHT),
    // TODO: Not supported for XA1
//    ROW_3_LEFT(WindowConstant.ROW_3_LEFT), ROW_3_RIGHT(WindowConstant.ROW_3_RIGHT),
    ROOF_TOP_1(WindowConstant.ROOF_TOP_1), ROOF_TOP_2(WindowConstant.ROOF_TOP_2)
}
