package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the SeatHeadrestAnglePosition updates
 */
interface SeatHeadrestAnglePositionListener : VehicleListener {

    /**
     * Callback invoked when SeatHeadrestAnglePosition updates.
     */
    fun onSeatHeadrestAnglePositionUpdated(position: Int?)
}