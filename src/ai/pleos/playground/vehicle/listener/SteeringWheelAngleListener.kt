package ai.pleos.playground.vehicle.listener

/**
 * Listener for front-wheel steering angle updates.
 */
interface SteeringWheelAngleListener : VehicleListener {

    /**
     * Called when a front-wheel steering angle update is available.
     *
     * Angles are reported in degrees, and leftward steering angles are negative.
     * The `angle` parameter is `null` if no angle is available. Otherwise, it represents
     * the front-wheel steering angle, not the rotation angle of the steering wheel.
     *
     * @param angle Current front-wheel steering angle in degrees,
     * or `null` if no value is available.
     */
    fun onSteeringWheelAngleUpdated(angle: Float?)
}
