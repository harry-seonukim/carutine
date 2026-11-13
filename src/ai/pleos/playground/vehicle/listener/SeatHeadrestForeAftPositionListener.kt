package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the SeatHeadrestForeAftPosition updates
 */
interface SeatHeadrestForeAftPositionListener : VehicleListener {

    /**
     * Callback invoked when SeatHeadrestForeAftPosition updates.
     */
    fun onSeatHeadrestForeAftPositionUpdated(position: Int?)
}