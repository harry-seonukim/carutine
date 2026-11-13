package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the fuel percentage updates.
 */
interface FuelPercentListener : VehicleListener {

    /**
     * Callback invoked with the updated fuel level as a percentage (%).
     *
     * @param value Updated fuel level as a percentage (%), or `null` if no value is available.
     */
    fun onFuelPercentUpdated(value: Int?)
}
