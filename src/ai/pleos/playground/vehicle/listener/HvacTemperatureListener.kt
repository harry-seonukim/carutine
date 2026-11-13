package ai.pleos.playground.vehicle.listener

/**
 * Listener for HVAC temperature updates.
 *
 * Reports the target temperature when registered with
 * [ai.pleos.playground.vehicle.api.HVAC.registerHvacTemperature], or the current interior
 * temperature when registered with [ai.pleos.playground.vehicle.api.HVAC.registerHvacTemperatureCurrent].
 * Both registration methods report values in degrees Celsius (°C).
 */
interface HvacTemperatureListener : VehicleListener {

    /**
     * Called with the updated HVAC temperature in degrees Celsius (°C).
     *
     * @param temperature Updated target or current interior temperature, depending on the
     * registration method, in degrees Celsius (°C); `null` if no value is available.
     */
    fun onHvacTemperatureUpdated(temperature: Float?)
}
