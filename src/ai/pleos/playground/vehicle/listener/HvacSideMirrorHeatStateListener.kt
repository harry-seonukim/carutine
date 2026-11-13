package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the HvacSideMirrorHeatState updates.
 */
interface HvacSideMirrorHeatStateListener : VehicleListener {

    /**
     * Callback invoked when HvacSideMirrorHeatState updates.
     */
    fun onHvacSideMirrorHeatStateUpdated(state: Int?)
}