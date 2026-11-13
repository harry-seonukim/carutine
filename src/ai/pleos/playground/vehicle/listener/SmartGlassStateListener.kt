package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the SmartGlassState updates
 */
interface SmartGlassStateListener : VehicleListener {

    /**
     * Callback invoked when SmartGlassState updates.
     */
    fun onSmartGlassStateUpdated(state: Boolean?)
}