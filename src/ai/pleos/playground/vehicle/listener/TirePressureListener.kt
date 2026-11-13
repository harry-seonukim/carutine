package ai.pleos.playground.vehicle.listener

/**
 * @suppress
 * @hide
 * Interface definition for the Tire Pressure updates
 */
interface TirePressureListener : VehicleListener {

    /**
     * @suppress
     * @hide
     * Callback invoked when Tire Pressure updates.
     *
     * @param pressure Current tire pressure in kilopascals (kPa),
     * or `null` if no value is available.
     */
    fun onTirePressureUpdated(pressure: Float?)
}
