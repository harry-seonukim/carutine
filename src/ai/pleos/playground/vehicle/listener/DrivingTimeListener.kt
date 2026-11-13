package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the driving time updates.
 */
interface DrivingTimeListener : VehicleListener {

    /**
     * Callback invoked when driving time updates.
     *
     * @param value [Int]: Updated driving time value in minutes.
     */
    fun onDrivingTimeUpdated(value: Int?)
}
