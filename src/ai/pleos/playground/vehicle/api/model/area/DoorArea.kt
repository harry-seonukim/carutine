package ai.pleos.playground.vehicle.api.model.area

import ai.pleos.playground.vehicle.constant.area.DoorValue

/**
 * Represents one or more door areas.
 *
 * @property values The selected door area values.
 */
class DoorArea(vararg val values: DoorValue) : Area() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DoorArea) return false
        return values.contentEquals(other.values)
    }

    override fun hashCode(): Int {
        return values.contentHashCode()
    }
}
