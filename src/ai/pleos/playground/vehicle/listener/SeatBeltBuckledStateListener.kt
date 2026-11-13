package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the SeatBeltBuckledState updates
 */
interface SeatBeltBuckledStateListener : VehicleListener {

    /**
     * Callback invoked when SeatBeltBuckledState updates.
     */
    fun onSeatBeltBuckledStateUpdated(state: Boolean?)
}