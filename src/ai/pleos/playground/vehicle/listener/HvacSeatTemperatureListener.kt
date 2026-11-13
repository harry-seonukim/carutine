package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the HvacSeatTemperature updates
 */
interface HvacSeatTemperatureListener : VehicleListener {

    /**
     * Callback invoked when HvacSeatTemperature updates.
     */
    fun onHvacSeatTemperatureUpdated(temperature: Int?)
}