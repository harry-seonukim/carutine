package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the AmbientColorAutoByDriveModeEnabled updates.
 */
interface AmbientColorAutoByDriveModeEnabledListener : VehicleListener {

    /**
     * Callback invoked when AmbientColorAutoByDriveModeEnabled updates.
     */
    fun onAmbientColorAutoByDriveModeEnabledUpdated(enabled: Boolean?)
}
