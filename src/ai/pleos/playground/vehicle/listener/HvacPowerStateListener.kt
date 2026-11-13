package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the HvacPowerState updates
 */
interface HvacPowerStateListener : VehicleListener {

    /**
     * Callback invoked when HvacPowerState updates.
     */
    fun onHvacPowerStateUpdated(state: Boolean?)
}