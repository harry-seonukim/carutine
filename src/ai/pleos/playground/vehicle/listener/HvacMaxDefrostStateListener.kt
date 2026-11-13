package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the HvacMaxDefrostState updates
 */
interface HvacMaxDefrostStateListener : VehicleListener {

    /**
     * Callback invoked when HvacMaxDefrostState updates.
     */
    fun onHvacMaxDefrostStateUpdated(state: Boolean?)
}