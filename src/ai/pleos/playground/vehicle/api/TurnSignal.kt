package ai.pleos.playground.vehicle.api

import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.area.TurnSignalValue
import ai.pleos.playground.vehicle.listener.TurnSignalStateListener
import androidx.annotation.RequiresPermission

/**
 * Provides APIs for turn signal state.
 *
 * Obtain an instance from [ai.pleos.playground.vehicle.Vehicle.getTurnSignal].
 */
interface TurnSignal {

    /**
     * Gets turn signal state capabilities.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the TurnSignalState capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the turn signal state capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun checkTurnSignalStateCapabilities(onComplete: (CapabilityConfig) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Gets the current turn signal state.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     * @param onComplete [TurnSignalValue]: Callback invoked when the current turn signal value is successfully retrieved.  
     * @param onFailed [Exception]: Callback invoked when retrieving the current turn signal state fails.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun getCurrentTurnSignalState(onComplete: (TurnSignalValue?) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Registers the turn signal state to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param listener [TurnSignalStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun registerTurnSignalState(listener: TurnSignalStateListener)

    /**
     * Stops getting property updates for the given turn signal state.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param listener [TurnSignalStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun unregisterTurnSignalState(listener: TurnSignalStateListener)
}
