package ai.pleos.playground.vehicle.listener

import ai.pleos.playground.vehicle.api.model.telltale.Telltales

/**
 * Interface definition for the TelltaleInfo updates
 */
interface TelltaleInfoListener : VehicleListener {

    /**
     * Callback invoked when TelltaleInfo updates.
     */
    fun onTelltaleInfoUpdated(telltales: Telltales?)
}