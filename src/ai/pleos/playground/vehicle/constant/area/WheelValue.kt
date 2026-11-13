package ai.pleos.playground.vehicle.constant.area

import ai.pleos.playground.vehicle.constant.VehicleAreaValue.WheelConstant


/**
 * Represents available wheel area values.
 */

enum class WheelValue(override val id: Int) : AreaValue {
    UNKNOWN(WheelConstant.UNKNOWN),
    LEFT_FRONT(WheelConstant.LEFT_FRONT), RIGHT_FRONT(WheelConstant.RIGHT_FRONT),
    LEFT_REAR(WheelConstant.LEFT_REAR), RIGHT_REAR(WheelConstant.RIGHT_REAR)
}