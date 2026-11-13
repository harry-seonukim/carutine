package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the SeatHeadrestHeightAftPosition updates
 */
interface SeatHeadrestHeightAftPositionListener : VehicleListener {

    /**
     * Callback invoked when SeatHeadrestHeightAftPosition updates.
     */
    fun onSeatHeadrestHeightAftPositionUpdated(position: Int?)
}