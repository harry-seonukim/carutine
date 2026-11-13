package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the HvacSteeringWheelHeatState updates.
 */
interface HvacSteeringWheelHeatStateListener : VehicleListener {

    /**
     * Callback invoked when the HvacSteeringWheelHeatState updates.
     */
    fun onHvacSteeringWheelHeatStateUpdated(state: Int?)
}