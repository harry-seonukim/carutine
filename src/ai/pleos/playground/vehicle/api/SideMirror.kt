package ai.pleos.playground.vehicle.api

import ai.pleos.playground.vehicle.api.model.area.MirrorArea
import ai.pleos.playground.vehicle.api.model.capability.AreaBounds
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.listener.HvacSideMirrorHeatStateListener
import androidx.annotation.RequiresPermission

/**
 * Provides APIs for side mirror heat.
 *
 * Obtain an instance from [ai.pleos.playground.vehicle.Vehicle.getSideMirror].
 */
interface SideMirror {

    @Deprecated(
        message = "Use the new checkHvacSideMirrorHeatCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkHvacSideMirrorHeatCapabilities({ capabilityConfig, map }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_MIRRORS)
    fun checkHvacSideMirrorHeatCapabilities(
        onComplete: (Map<MirrorArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets hvac side mirror heat capabilities.
     *
     * **Permission:** [PleosPermission.CAR_MIRRORS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_MIRRORS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[MirrorArea], [AreaBounds]<[Int]>>: Callback invoked when the HvacSideMirrorHeat capability configuration and the supported mirror areas with their corresponding bounds values are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the side mirror heat capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_MIRRORS)
    fun checkHvacSideMirrorHeatCapabilities(onComplete: (CapabilityConfig, Map<MirrorArea, AreaBounds<Int>>) -> Unit, onFailed: (Exception) -> Unit)


    /**
     * Gets the current side mirror heat state.
     *
     * **Permission:** [PleosPermission.CAR_MIRRORS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_MIRRORS" />
     * ```
     *
     * @param area [MirrorArea]: The mirror area for the vehicle.
     * @param onComplete [Int]: Callback invoked when the current side mirror heating value is successfully retrieved.  
     * @param onFailed [Exception]: Callback invoked when retrieving the current side mirror heating state fails.
     */
    @RequiresPermission(PleosPermission.CAR_MIRRORS)
    fun getHvacSideMirrorHeatState(
        area: MirrorArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Sets the side mirror heat state.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_MIRRORS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_MIRRORS" />
     * ```
     *
     * @param area [MirrorArea]: The mirror area for the vehicle.
     * @param value [Int]: The side mirror heat state to be set.
     * @param onComplete [MirrorArea], [Int]: Callback invoked when the side mirror heating has been successfully set.  
     * @param onFailed [Exception]: Callback invoked when setting the side mirror heating fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_MIRRORS)
    fun setHvacSideMirrorHeat(
        area: MirrorArea,
        value: Int,
        onComplete: (MirrorArea, Int?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Registers HvacSideMirrorHeatState state to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [MirrorArea]: The mirror area for the vehicle.
     * @param listener [HvacSideMirrorHeatStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun registerHvacSideMirrorHeatState(area: MirrorArea, listener: HvacSideMirrorHeatStateListener)

    /**
     * Stops getting property updates for the given HvacSideMirrorHeatState state.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [MirrorArea]: The mirror area for the vehicle.
     * @param listener [HvacSideMirrorHeatStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun unregisterHvacSideMirrorHeatState(
        area: MirrorArea,
        listener: HvacSideMirrorHeatStateListener
    )
}
