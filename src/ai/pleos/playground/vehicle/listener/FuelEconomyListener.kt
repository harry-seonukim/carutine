package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the fuel economy updates.
 */
interface FuelEconomyListener : VehicleListener {

    /**
     * Callback invoked when fuel economy updates.
     *
     * @param efficiency [Float]: Updated fuel economy value in L/100 km.
     */
    fun onFuelEconomyUpdated(efficiency: Float?)
}
