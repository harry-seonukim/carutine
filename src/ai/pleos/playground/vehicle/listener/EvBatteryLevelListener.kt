package ai.pleos.playground.vehicle.listener

/**
 * @suppress
 * @hide
 * Interface definition for the battery level updates.
 */
interface EvBatteryLevelListener : VehicleListener {

    /**
     * Callback invoked when battery level updates.
     *
     * @param level Current battery energy in watt-hours (Wh), or `null` if no value is available.
     */
    fun onEvBatteryLevelUpdated(level: Float?)
}
