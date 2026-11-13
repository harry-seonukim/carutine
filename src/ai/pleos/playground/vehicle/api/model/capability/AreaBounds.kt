package ai.pleos.playground.vehicle.api.model.capability

/**
 * Represents the available value range for an area.
 *
 * @property minValue The minimum value.
 * @property maxValue The maximum value.
 */
data class AreaBounds<T>(
    val minValue: T,
    val maxValue: T,
)
