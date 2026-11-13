package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for HVAC temperature updates in degrees Celsius (°C) and degrees Fahrenheit (°F).
 */
interface HvacTemperatureValueListener : VehicleListener {

    /**
     * Callback invoked when HVAC temperature updates.
     *
     * @param celsius currently set temperature value in degrees Celsius (°C).
     * @param fahrenheit converted temperature value in degrees Fahrenheit (°F) using vehicle rule when possible,
     * otherwise formula fallback.
     */
    fun onHvacTemperatureUpdated(celsius: Float?, fahrenheit: Float?)
}
