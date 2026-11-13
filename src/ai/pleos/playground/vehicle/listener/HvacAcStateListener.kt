package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the HvacAcState updates
 */
interface HvacAcStateListener : VehicleListener {

    /**
     * Callback invoked when HvacAcState updates.
     */
    fun onHvacAcStateUpdated(state: Boolean?)
}