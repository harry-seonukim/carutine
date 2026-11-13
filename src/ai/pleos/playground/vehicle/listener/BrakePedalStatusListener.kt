package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the BrakePedalStatus updates
 */
interface BrakePedalStatusListener : VehicleListener {

    /**
     * Callback invoked when BrakePedalStatus updates.
     */
    fun onBrakePedalStatusUpdated(status: Boolean?)
}