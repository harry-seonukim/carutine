package ai.pleos.playground.vehicle.listener

/**
 * Listener for distance updates.
 *
 * The unit depends on the method used to register this listener:
 * - [ai.pleos.playground.vehicle.api.Odometer.registerDistanceDriven]: kilometers (km).
 * - [ai.pleos.playground.vehicle.api.Odometer.registerTripCurrentDrivingDistance],
 *   [ai.pleos.playground.vehicle.api.Odometer.registerTripInstantaneousDrivingDistance],
 *   [ai.pleos.playground.vehicle.api.Odometer.registerTripCumulativeDrivingDistance], and
 *   [ai.pleos.playground.vehicle.api.Odometer.registerTripSinceRefillDrivingDistance]: meters (m).
 */
interface DistanceDrivenListener : VehicleListener {

    /**
     * Called with the updated distance in kilometers (km) or meters (m), depending on the registration API.
     *
     * @param value Updated distance in the unit documented by the registration API,
     * or `null` if no value is available.
     */
    fun onDistanceDrivenUpdated(value: Float?)
}
