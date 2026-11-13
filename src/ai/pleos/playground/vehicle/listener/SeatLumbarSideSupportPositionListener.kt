package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the SeatLumbarSideSupportPosition updates
 */
interface SeatLumbarSideSupportPositionListener : VehicleListener {

    /**
     * Callback invoked when SeatLumbarSideSupportPosition updates.
     */
    fun onSeatLumbarSideSupportPositionUpdated(position: Int?)
}