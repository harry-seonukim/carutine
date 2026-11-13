package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the SeatAirbagDeployed updates
 */
interface SeatAirbagDeployedListener : VehicleListener {

    /**
     * Callback invoked when SeatAirbagDeployed updates.
     */
    fun onSeatAirbagDeployUpdated(deployed: Boolean?)
}