package ai.pleos.playground.vehicle.api.model.area

import ai.pleos.playground.vehicle.constant.area.SeatValue

/**
 * Represents one or more seat areas.
 *
 * @property values The selected seat area values.
 */
class SeatArea(vararg val values: SeatValue) : Area() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SeatArea) return false
        return values.contentEquals(other.values)
    }

    override fun hashCode(): Int {
        return values.contentHashCode()
    }
}
