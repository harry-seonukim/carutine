package ai.pleos.playground.vehicle.listener

/**
 * VehicleListener is an interface for constructing standard responses from the vehicle call.
 */
sealed interface VehicleListener {
    /**
     * Callback invoked when a exception occurs.
     */
    fun onFailed(e: Exception)
}