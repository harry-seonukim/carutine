package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the LowWasherFluidWaring updates.
 */
interface LowWasherFluidWaringListener : VehicleListener {

    /**
     * Callback invoked when the LowWasherFluidWaring updates.
     */
    fun onLowWasherFluidWaringUpdated(state: Boolean?)
}