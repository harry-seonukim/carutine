package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the EV battery state of charge updates.
 */
interface EvBatteryStateOfChargeListener : VehicleListener {

    /**
     * Callback invoked with the updated EV battery state of charge as a percentage (%).
     *
     * @param value [Int]: Updated EV battery state of charge value as a percentage (%).
     */
    fun onEvBatteryStateOfChargeUpdated(value: Int?)
}
