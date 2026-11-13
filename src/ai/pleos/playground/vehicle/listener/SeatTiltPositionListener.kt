package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the SeatTiltPosition updates
 */
interface SeatTiltPositionListener : VehicleListener {

    /**
     * Callback invoked when SeatTiltPosition updates.
     */
    fun onSeatTiltPositionUpdated(position: Int?)
}