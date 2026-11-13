package ai.pleos.playground.vehicle.listener

import ai.pleos.playground.vehicle.constant.control.DrivingModeType

/**
 * Interface definition for the DrivingModeState updates.
 */
interface DrivingModeStateListener : VehicleListener {

    /**
     * Callback invoked when the DrivingModeState updates.
     */
    fun onDrivingModeStateUpdated(state: DrivingModeType?)
}