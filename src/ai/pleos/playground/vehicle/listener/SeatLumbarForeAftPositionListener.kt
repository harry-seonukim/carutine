package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the SeatLumbarForeAftPosition updates
 */
interface SeatLumbarForeAftPositionListener : VehicleListener {

    /**
     * Callback invoked when SeatLumbarForeAftPosition updates.
     */
    fun onSeatLumbarForeAftPositionUpdated(position: Int?)
}