package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the WindowLockState updates
 */
interface WindowLockStateListener : VehicleListener {

    /**
     * Callback invoked when WindowLockState updates.
     */
    fun onWindowLockStateUpdated(state: Boolean?)
}