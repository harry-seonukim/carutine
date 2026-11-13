package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the SeatBeltHeightPosition updates
 */
interface SeatBeltHeightPositionListener : VehicleListener {

    /**
     * Callback invoked when SeatBeltHeightPosition updates.
     */
    fun onSeatBeltHeightPositionUpdated(position: Int?)
}