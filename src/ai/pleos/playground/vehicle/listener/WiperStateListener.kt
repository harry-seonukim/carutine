package ai.pleos.playground.vehicle.listener

import ai.pleos.playground.vehicle.constant.control.WiperState

/**
 * Interface definition for the WiperState updates
 */
interface WiperStateListener : VehicleListener {

    /**
     * Callback invoked when WiperState updates.
     */
    fun onWiperStateUpdated(state: WiperState?)
}