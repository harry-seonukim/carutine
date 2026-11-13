package ai.pleos.playground.vehicle.listener

/**
 * Interface definition for the HvacSeatVentilation updates
 */
interface HvacSeatVentilationListener : VehicleListener {

    /**
     * Callback invoked when HvacSeatVentilation updates.
     */
    fun onHvacSeatVentilationUpdated(ventilation: Int?)
}