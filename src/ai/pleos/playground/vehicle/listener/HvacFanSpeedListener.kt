package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the HvacFanSpeed updates
 */
interface HvacFanSpeedListener : VehicleListener {

    /**
     * Callback invoked when HvacFanSpeed updates.
     */
    fun onHvacFanSpeedUpdated(speed: Int?)
}