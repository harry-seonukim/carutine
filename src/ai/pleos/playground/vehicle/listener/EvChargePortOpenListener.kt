package ai.pleos.playground.vehicle.listener

/**
 * @suppress
 * @hide
 * Interface definition for the EvChargePortOpen updates.
 */
interface EvChargePortOpenListener : VehicleListener {

    /**
     * Callback invoked when EvChargePortOpen updates.
     */
    fun onEvChargePortOpenUpdated(isOpened: Boolean?)
}