package ai.pleos.playground.vehicle.constant.control

import ai.pleos.playground.vehicle.constant.VehicleCruiseControlValue

/**
 * Represents the cruise control state.
 */
enum class CruiseControl(internal val id: Int) {
    OTHER(VehicleCruiseControlValue.OTHER_ID),
    ENABLED(VehicleCruiseControlValue.ENABLED_ID),
    ACTIVATED(VehicleCruiseControlValue.ACTIVATED_ID),
    USER_OVERRIDE(VehicleCruiseControlValue.USER_OVERRIDE_ID),
    SUSPENDED(VehicleCruiseControlValue.SUSPENDED_ID),
    FORCED_DEACTIVATION_WARNING(VehicleCruiseControlValue.FORCED_DEACTIVATION_WARNING_ID)
    ;

    internal companion object {
        fun findById(id: Int?): CruiseControl? {
            return entries.find { it.id == id }
        }
    }
}
