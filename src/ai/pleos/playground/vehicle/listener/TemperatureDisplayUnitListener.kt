package ai.pleos.playground.vehicle.listener

import ai.pleos.playground.vehicle.constant.unit.TemperatureUnit

/**
 * Interface definition for the TemperatureDisplayUnit updates
 */
interface TemperatureDisplayUnitListener : VehicleListener {

    /**
     * Callback invoked when TemperatureDisplayUnit updates.
     */
    fun onTemperatureDisplayUnitUpdated(unit: TemperatureUnit?)
}