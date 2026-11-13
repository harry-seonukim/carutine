package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for ETCS voice guidance volume updates.
 */
interface EtcsVoiceGuidanceVolumeListener : VehicleListener {

    /**
     * Callback invoked when ETCS voice guidance volume updates.
     */
    fun onEtcsVoiceGuidanceVolumeUpdated(volume: Int?)
}
