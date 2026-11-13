package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the GloveBox lock state updates.
 */
interface GloveBoxLockStateListener : VehicleListener {

    /**
     * Callback invoked when the GloveBox lock state updates.
     */
    fun onGloveBoxLockStateUpdated(state: Boolean?)
}