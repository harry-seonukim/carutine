package ai.pleos.playground.vehicle.api.model.telltale

import ai.pleos.playground.vehicle.constant.telltale.TelltaleState
import ai.pleos.playground.vehicle.constant.telltale.TelltaleType

/**
 * Represents a single telltale item.
 *
 * @property type The telltale type.
 * @property state The telltale state.
 * @property color The telltale color.
 * @property priority The telltale priority.
 * @property warningCount The warning count.
 */
data class TelltaleData(
    val type: TelltaleType,
    val state: TelltaleState,
    val color: Int,
    val priority: Int,
    val warningCount: Int
)
