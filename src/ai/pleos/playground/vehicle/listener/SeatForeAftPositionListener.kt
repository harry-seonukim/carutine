package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the SeatForeAftPosition updates
 */
interface SeatForeAftPositionListener : VehicleListener {

    /**
     * Callback invoked when SeatForeAftPosition updates.
     */
    fun onSeatForeAftPositionUpdated(position: Int?)
}