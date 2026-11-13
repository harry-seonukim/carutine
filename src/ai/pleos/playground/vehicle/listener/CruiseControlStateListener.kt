package ai.pleos.playground.vehicle.listener

import ai.pleos.playground.vehicle.constant.control.CruiseControl

/**
 * Interface definition for the CruiseControlState updates.
 */
interface CruiseControlStateListener : VehicleListener {

    /**
     * Callback invoked when the CruiseControlState updates.
     */
    fun onCruiseControlStateUpdated(state: CruiseControl?)
}