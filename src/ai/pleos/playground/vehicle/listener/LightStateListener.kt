package ai.pleos.playground.vehicle.listener

import ai.pleos.playground.vehicle.constant.control.LightState

/**
 * Interface definition for the HighBeamLightState updates
 */
interface LightStateListener : VehicleListener {

    /**
     * Callback invoked when HighBeamLightState updates.
     */
    fun onLightStateUpdated(state: LightState?)
}