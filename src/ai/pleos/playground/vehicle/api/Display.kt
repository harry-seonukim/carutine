package ai.pleos.playground.vehicle.api

import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.unit.TemperatureUnit
import ai.pleos.playground.vehicle.listener.TemperatureDisplayUnitListener
import androidx.annotation.RequiresPermission

/**
 * Provides APIs for temperature display unit.
 *
 * Obtain an instance from [ai.pleos.playground.vehicle.Vehicle.getDisplay].
 */
interface Display {

    /**
     * Gets temperature display unit capabilities.
     *
     * **Permission:** [PleosPermission.CAR_INFO] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_INFO" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the TemperatureDisplayUnit capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the temperature display unit capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_INFO)
    fun checkTemperatureDisplayUnitCapabilities(onComplete: (CapabilityConfig) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Gets current temperature units for display.
     *
     * **Permission:** [PleosPermission.CAR_INFO] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_INFO" />
     * ```
     *
     * @param onComplete [TemperatureUnit]: Callback invoked when the current temperature unit for display is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the current temperature unit for display fails.
     */
    @RequiresPermission(PleosPermission.CAR_INFO)
    fun getCurrentTemperatureDisplayUnit(onComplete: (TemperatureUnit?) -> Unit, onFailed: (Exception) -> Unit)


    /**
     * Registers TemperatureDisplayUnit to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_INFO] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_INFO" />
     * ```
     *
     * @param listener [TemperatureDisplayUnitListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_INFO)
    fun registerTemperatureDisplayUnit(listener: TemperatureDisplayUnitListener)

    /**
     * Stops getting property updates for the given TemperatureDisplayUnit.
     *
     * **Permission:** [PleosPermission.CAR_INFO] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_INFO" />
     * ```
     *
     * @param listener [TemperatureDisplayUnitListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_INFO)
    fun unregisterTemperatureDisplayUnit(listener: TemperatureDisplayUnitListener)

}
