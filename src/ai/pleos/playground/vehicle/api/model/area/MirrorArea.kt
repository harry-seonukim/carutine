package ai.pleos.playground.vehicle.api.model.area

import ai.pleos.playground.vehicle.constant.area.MirrorValue

/**
 * Represents one or more mirror areas.
 *
 * @property values The selected mirror area values.
 */
class MirrorArea(vararg val values: MirrorValue) : Area() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MirrorArea) return false
        return values.contentEquals(other.values)
    }

    override fun hashCode(): Int {
        return values.contentHashCode()
    }
}
