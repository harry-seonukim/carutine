package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the HvacDualState updates
 */
interface HvacDualStateListener : VehicleListener {

    /**
     * Callback invoked when HvacDualState updates.
     */
    fun onHvacDualStateUpdated(state: Boolean?)
}