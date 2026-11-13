package ai.pleos.playground.vehicle.api.model.capability

/**
 * HVAC temperature conversion table derived from the vehicle temperature setting config array.
 *
 * The VHAL configuration array stores temperatures and increments as integers multiplied by 10.
 * The temperature and increment properties below are `Float` values obtained by dividing
 * those integers by 10.
 *
 * @property minCelsius Minimum supported temperature in degrees Celsius (°C).
 * @property maxCelsius Maximum supported temperature in degrees Celsius (°C).
 * @property incrementCelsius Supported temperature increment in degrees Celsius (°C).
 * @property minFahrenheit Minimum supported temperature in degrees Fahrenheit (°F).
 * @property maxFahrenheit Maximum supported temperature in degrees Fahrenheit (°F).
 * @property incrementFahrenheit Supported temperature increment in degrees Fahrenheit (°F).
 * @property raw Original six integer values from the VHAL configuration array, without conversion.
 * Each value represents a temperature or increment multiplied by 10.
 */
data class HvacTemperatureConfigArray(
    val minCelsius: Float,
    val maxCelsius: Float,
    val incrementCelsius: Float,
    val minFahrenheit: Float,
    val maxFahrenheit: Float,
    val incrementFahrenheit: Float,
    val raw: List<Int>
)
