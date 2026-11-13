package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the SmartGlass Level updates
 */
interface SmartGlassLevelListener : VehicleListener {

    /**
     * Callback invoked when SmartGlass Level updates.
     */
    fun onSmartGlassLevelUpdated(level: Int?)
}