package ai.pleos.playground.vehicle.listener

/**
 * @suppress
 * @hide
 * Interface definition for the range remaining changes.
 */
interface RangeRemainingListener : VehicleListener {
    /**
     * Callback invoked when the range remaining changes.
     *
     * @param rangeRemaining Estimated remaining range in meters (m),
     * or `null` if no value is available.
     */
    fun onRangeRemainingChanged(rangeRemaining: Float?)
}
