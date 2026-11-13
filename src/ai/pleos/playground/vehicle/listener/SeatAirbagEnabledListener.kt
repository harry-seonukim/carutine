package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the SeatAirbagEnabled updates
 */
interface SeatAirbagEnabledListener : VehicleListener {

    /**
     * Callback invoked when SeatAirbagEnabled updates.
     */
    fun onSeatAirbagEnabledUpdated(enabled: Boolean?)
}