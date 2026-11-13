package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the EV battery efficiency updates.
 */
interface EvBatteryEfficiencyListener : VehicleListener {

    /**
     * Callback invoked when EV battery efficiency updates.
     *
     * @param efficiency [Float]: Updated EV battery efficiency value in km/kWh.
     */
    fun onEvBatteryEfficiencyUpdated(efficiency: Float?)
}
