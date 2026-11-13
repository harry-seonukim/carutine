package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the SeatBackrestAnglePosition updates
 */
interface SeatBackrestAnglePositionListener : VehicleListener {

    /**
     * Callback invoked when SeatBackrestAnglePosition updates.
     */
    fun onSeatBackrestAnglePositionUpdated(position: Int?)
}