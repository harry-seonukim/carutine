package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the PowerDownWarningIndicator updates.
 */
interface PowerDownWarningIndicatorListener : VehicleListener {

    /**
     * Callback invoked when the PowerDownWarningIndicator updates.
     */
    fun onPowerDownWarningIndicatorUpdated(state: Boolean?)
}