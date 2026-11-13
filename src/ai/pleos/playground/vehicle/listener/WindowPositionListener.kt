package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the WindowPosition updates
 */
interface WindowPositionListener : VehicleListener {

    /**
     * Callback invoked when WindowPosition updates.
     */
    fun onWindowPositionUpdated(position: Int?)
}