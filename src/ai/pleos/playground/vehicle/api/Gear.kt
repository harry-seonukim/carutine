package ai.pleos.playground.vehicle.api

import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.control.VehicleGear
import ai.pleos.playground.vehicle.listener.GearSelectionListener
import androidx.annotation.RequiresPermission

/**
 * @suppress
 * @hide
 * Gear
 *
 */
interface Gear {
    /**
     * @suppress
     * @hide
     * Gets the current gear selection state.
     *
     * getCurrentGearSelectionState and setGearSelection value may not match that of getCurrentGearState.
     * For example, if the GearSelection is [VehicleGear]#GEAR_DRIVE in a vehicle with an automatic transmission,
     * the CurrentGearState will be one of [VehicleGear]#GEAR_FIRST, [VehicleGear]#GEAR_SECOND, etc.,
     * which reflects the actual gear the transmission is currently running in.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param onComplete [VehicleGear]: Callback invoked when the current gear selection state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the current gear selection state fails.
     */
    @Deprecated(
        "This API is deprecated, use the Google GEAR_SELECTION property.",
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun getCurrentGearSelectionState(onComplete: (VehicleGear?) -> Unit, onFailed: (Exception) -> Unit)


    /* ================ register / unregister ================ */

    /**
     * @suppress
     * @hide
     * Registers for gear selection state property updates.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener [GearSelectionListener]: Listener is registered.
     */
    @Deprecated(
        "This API is deprecated, use the Google GEAR_SELECTION property.",
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun registerGearSelection(listener: GearSelectionListener)

    /**
     * @suppress
     * @hide
     * Stops receiving property updates for the gear selection state.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener [GearSelectionListener]: Listener is unregistered.
     */
    @Deprecated(
        "This API is deprecated, use the Google GEAR_SELECTION property.",
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun unregisterGearSelection(listener: GearSelectionListener)

}