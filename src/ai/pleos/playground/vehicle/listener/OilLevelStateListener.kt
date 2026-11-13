package ai.pleos.playground.vehicle.listener

import ai.pleos.playground.vehicle.constant.control.OilLevelState

/**
 * Interface definition for the OilLevelState updates.
 */
interface OilLevelStateListener : VehicleListener {

    /**
     * Callback invoked when the OilLevelState updates.
     */
    fun onOilLevelUpdated(level: OilLevelState?)
}