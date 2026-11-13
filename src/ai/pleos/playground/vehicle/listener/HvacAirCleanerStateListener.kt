package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the HvacAirCleanerState updates
 */
interface HvacAirCleanerStateListener : VehicleListener {

    /**
     * Callback invoked when HvacAirCleanerState updates.
     */
    fun onHvacAirCleanerStateUpdated(state: Boolean?)
}