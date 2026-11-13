package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the HvacDefrosterState updates
 */
interface HvacDefrosterStateListener : VehicleListener {

    /**
     * Callback invoked when HvacDefrosterState updates.
     */
    fun onHvacDefrosterStateUpdated(state: Boolean?)
}