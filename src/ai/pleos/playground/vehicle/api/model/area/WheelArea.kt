package ai.pleos.playground.vehicle.api.model.area

import ai.pleos.playground.vehicle.constant.area.WheelValue

/**
 * Represents one or more wheel areas.
 *
 * @property values The selected wheel area values.
 */
class WheelArea(vararg val values: WheelValue) : Area(){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WheelArea) return false
        return values.contentEquals(other.values)
    }

    override fun hashCode(): Int {
        return values.contentHashCode()
    }
}
