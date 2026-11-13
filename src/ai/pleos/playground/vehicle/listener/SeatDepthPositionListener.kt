package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the SeatDepthPosition updates
 */
interface SeatDepthPositionListener : VehicleListener {

    /**
     * Callback invoked when SeatDepthPosition updates.
     */
    fun onSeatDepthPositionUpdated(position: Int?)
}