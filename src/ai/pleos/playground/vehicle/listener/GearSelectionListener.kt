package ai.pleos.playground.vehicle.listener

import ai.pleos.playground.vehicle.constant.control.VehicleGear

/**
 * @suppress
 * @hide
 * Interface definition for the gear selection changes.
 */
interface GearSelectionListener : VehicleListener {
    /**
     * Callback invoked when the gear selection changes.
     */
    fun onSelected(gear: VehicleGear?)
}