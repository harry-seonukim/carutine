package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the door lock state updates.
 */
interface DoorLockStateListener : VehicleListener {

    /**
     * Callback invoked when the door lock state updates.
     */
    fun onDoorLockStateUpdated(state: Boolean?)
}