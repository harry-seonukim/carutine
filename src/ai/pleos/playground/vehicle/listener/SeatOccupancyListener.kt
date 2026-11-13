package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the SeatOccupancy updates
 */
interface SeatOccupancyListener : VehicleListener {

    /**
     * Callback invoked when SeatOccupancy updates.
     */
    fun onSeatOccupancyUpdated(occupancy: Boolean?)
}