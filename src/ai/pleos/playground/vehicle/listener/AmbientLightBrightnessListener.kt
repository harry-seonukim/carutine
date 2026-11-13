package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the AmbientLightBrightness updates
 */
interface AmbientLightBrightnessListener : VehicleListener {

    /**
     * Callback invoked when AmbientLightBrightness updates.
     */
    fun onBrightnessUpdated(brightness: Int?)
}