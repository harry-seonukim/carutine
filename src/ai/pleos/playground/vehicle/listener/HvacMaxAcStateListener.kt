package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the HvacMaxAcState updates
 */
interface HvacMaxAcStateListener : VehicleListener {

    /**
     * Callback invoked when HvacMaxAcState updates.
     */
    fun onHvacMaxAcStateUpdated(state: Boolean?)
}