package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the SeatHeightPosition updates
 */
interface SeatHeightPositionListener : VehicleListener {

    /**
     * Callback invoked when SeatHeightPosition updates.
     */
    fun onSeatHeightPositionUpdated(position: Int?)
}