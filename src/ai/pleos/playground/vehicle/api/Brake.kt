package ai.pleos.playground.vehicle.api

import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.listener.BrakePedalStatusListener
import androidx.annotation.RequiresPermission

/**
 * Provides APIs for brake pedal status.
 *
 * Obtain an instance from [ai.pleos.playground.vehicle.Vehicle.getBrake].
 */
interface Brake {

    /**
     * Gets the brake pedal status capabilities.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the BrakePedalStatus capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the brake pedal status capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun checkBrakePedalStatusCapabilities(onComplete: (CapabilityConfig) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Gets the brake pedal status. If true, the brake pedal is being pressed.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param onComplete [Boolean]: Callback invoked when the brake pedal status is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the brake pedal status fails.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun getCurrentBrakePedalStatus(onComplete: (Boolean?) -> Unit, onFailed: (Exception) -> Unit)


    /**
     * Registers a listener for brake pedal status property updates.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener [BrakePedalStatusListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun registerBrakePedalStatus(listener: BrakePedalStatusListener)


    /**
     * Unregisters the listener so brake pedal status property updates stop.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener [BrakePedalStatusListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun unregisterBrakePedalStatus(listener: BrakePedalStatusListener)

}
