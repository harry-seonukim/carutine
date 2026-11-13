package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the door position updates.
 */
interface DoorPositionListener : VehicleListener {

    /**
     * Callback invoked when the door position updates.
     */
    fun onPositionUpdated(pos: Int?)
}