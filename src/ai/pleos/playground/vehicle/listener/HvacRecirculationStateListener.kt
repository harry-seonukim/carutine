package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the HvacAutoRecirculationState updates
 */
interface HvacRecirculationStateListener : VehicleListener {

    /**
     * Callback invoked when HvacAutoRecirculationState updates.
     */
    fun onHvacAutoRecirculationStateUpdated(state: Boolean?)
}