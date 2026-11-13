package ai.pleos.playground.vehicle.listener

/**
 * Listener for cruise control target speed updates.
 */
interface CruiseControlTargetSpeedListener : VehicleListener {

    /**
     * Called with the updated cruise control target speed in meters per second (m/s).
     *
     * @param speed Cruise control target speed in meters per second (m/s),
     * or `null` if no value is available.
     */
    fun onCruiseControlTargetSpeedUpdated(speed: Float?)
}
