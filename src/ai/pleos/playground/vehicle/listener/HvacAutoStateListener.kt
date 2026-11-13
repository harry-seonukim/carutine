package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the HvacAutoState updates
 */
interface HvacAutoStateListener : VehicleListener {

    /**
     * Callback invoked when HvacAutoState updates.
     */
    fun onHvacAutoStateUpdated(state: Boolean?)
}