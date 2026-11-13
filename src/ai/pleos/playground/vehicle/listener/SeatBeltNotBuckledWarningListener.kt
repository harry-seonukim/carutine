package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the SeatBeltNotBuckledWarning updates
 */
interface SeatBeltNotBuckledWarningListener : VehicleListener {

    /**
     * Callback invoked when SeatBeltNotBuckledWarning updates.
     */
    fun onSeatBeltNotBuckledWarningUpdated(state: Boolean?)
}