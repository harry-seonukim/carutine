package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the AmbientLightBrightnessColor updates
 */
interface AmbientLightBrightnessColorListener : VehicleListener {

    /**
     * Callback invoked when AmbientLightBrightnessColor updates.
     */
    fun onBrightnessColorUpdated(argbColor: Int?)
}