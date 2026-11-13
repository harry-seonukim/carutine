package ai.pleos.playground.vehicle.listener

import ai.pleos.playground.vehicle.constant.area.TurnSignalValue

/**
 * Interface definition for the TurnSignalState updates
 */
interface TurnSignalStateListener : VehicleListener {

    /**
     * Callback invoked when TurnSignalStates updates.
     */
    fun onTurnSignalStateUpdated(state: TurnSignalValue?)
}