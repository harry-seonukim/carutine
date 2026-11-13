package ai.pleos.playground.vehicle.api.model.area

import ai.pleos.playground.vehicle.constant.area.WindowValue

/**
 * Represents one or more window areas.
 *
 * @property values The selected window area values.
 */
class WindowArea(vararg val values: WindowValue) : Area() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WindowArea) return false
        return values.contentEquals(other.values)
    }

    override fun hashCode(): Int {
        return values.contentHashCode()
    }
}
