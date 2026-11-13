package ai.pleos.playground.vehicle.api

import ai.pleos.playground.vehicle.api.model.area.DoorArea
import ai.pleos.playground.vehicle.api.model.capability.AreaBounds
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.listener.DoorLockStateListener
import ai.pleos.playground.vehicle.listener.DoorPositionListener
import ai.pleos.playground.vehicle.listener.GloveBoxLockStateListener
import androidx.annotation.RequiresPermission

/**
 * Provides APIs for door lock, door position, and glove box.
 *
 * Obtain an instance from [ai.pleos.playground.vehicle.Vehicle.getDoor].
 */
interface Door {

    /**
     * Gets the glove box lock capabilities.
     *
     * **Permission:** [PleosPermission.CAR_DOORS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DOORS" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Failure to get the capabilities. Value is lambda callback.
     *
     */
    @RequiresPermission(PleosPermission.CAR_DOORS)
    fun checkGloveBoxLockCapabilities(onComplete: (CapabilityConfig) -> Unit, onFailed: (Exception) -> Unit)

    @Deprecated(
        message = "Use the new checkDoorLockCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkDoorLockCapabilities({ capabilityConfig, list }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_DOORS)
    fun checkDoorLockCapabilities(onComplete: (List<DoorArea>) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Gets DoorLock capabilities.
     *
     * **Permission:** [PleosPermission.CAR_DOORS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DOORS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[DoorArea]>: Callback invoked when the DoorLock capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the DoorLock capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_DOORS)
    fun checkDoorLockCapabilities(onComplete: (CapabilityConfig, List<DoorArea>) -> Unit, onFailed: (Exception) -> Unit)

    @Deprecated(
        message = "Use the new checkDoorPositionCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkDoorPositionCapabilities({ capabilityConfig, map }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_DOORS)
    fun checkDoorPositionCapabilities(
        onComplete: (Map<DoorArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets DoorPosition capabilities.
     *
     * The callback receives [CapabilityConfig] (access/support) and a
     * [Map] of [DoorArea] to [AreaBounds] with per-area [AreaBounds.minValue]/[AreaBounds.maxValue].
     *
     * **Permission:** [PleosPermission.CAR_DOORS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DOORS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[DoorArea]>: Callback invoked when the DoorLock capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the Door Position capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_DOORS)
    fun checkDoorPositionCapabilities(onComplete: (CapabilityConfig, Map<DoorArea, AreaBounds<Int>>) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Gets current door lock state.
     *
     * **Permission:** [PleosPermission.CAR_DOORS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DOORS" />
     * ```
     *
     * @param area [DoorArea]: The door area of the vehicle.
     * @param onComplete [Boolean]: Callback invoked when the door lock state value is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the door lock state information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_DOORS)
    fun getDoorLockState(
        area: DoorArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Sets the current glove lock state.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_DOORS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_DOORS" />
     * ```
     *
     * @param value [Boolean]: Indicates whether to enable or disable the glove lock. Set to true to lock, or false to unlock.
     * @param onComplete [Boolean]: Callback invoked when the glove lock state has been successfully set.
     * @param onFailed [Exception]: Callback invoked if setting the glove lock state fails.
     *
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_DOORS)
    fun setGloveBoxLock(
        value: Boolean,
        onComplete: (Boolean?) -> Unit = {},
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current glove lock state.
     *
     * **Permission:** [PleosPermission.CAR_DOORS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DOORS" />
     * ```
     *
     * @param onComplete [Boolean]: Callback invoked when the glove lock state value is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the glove lock state value fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_DOORS)
    fun getCurrentGloveBoxLockState(onComplete: (Boolean?) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Gets the current door position state.
     *
     * **Permission:** [PleosPermission.CAR_DOORS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DOORS" />
     * ```
     *
     * @param area [DoorArea]: The door area of the vehicle.
     * @param onComplete [Int]: Callback invoked when the current door position value is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the door position state fails.
     */
    @RequiresPermission(PleosPermission.CAR_DOORS)
    fun getCurrentDoorPosition(
        area: DoorArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Sets the current door lock state.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_DOORS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_DOORS" />
     * ```
     *
     * @param area [DoorArea]: The door area of the vehicle.
     * @param value [Boolean]: Indicates whether to lock or unlock the door. Set to true to lock, or false to unlock.
     * @param onComplete [DoorArea], [Boolean]: Callback invoked when the door lock state has been successfully set.
     * @param onFailed [Exception]: Callback invoked if setting the door lock state fails.
     *
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_DOORS)
    fun setDoorLock(
        area: DoorArea,
        value: Boolean,
        onComplete: (DoorArea, Boolean?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Sets the current door position state.
     * The maximum (fully open) value is vehicle-dependent and is not fixed.
     * Call [checkDoorPositionCapabilities] first. That API's callback returns
     * [CapabilityConfig] plus a [Map]<[DoorArea], [AreaBounds]<[Int]>>; read each area's
     * [AreaBounds.minValue]/[AreaBounds.maxValue] from that map (not from [CapabilityConfig]).
     * Min is typically 0 for fully closed.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_DOORS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_DOORS" />
     * ```
     *
     * @param area [DoorArea]: The door area of the vehicle.
     * @param value [Int]: The door position.
     * @param onComplete [DoorArea], [Int]: Callback invoked when the door position state has been successfully set.
     * @param onFailed [Exception]: Callback invoked if setting the door position state fails.
     *
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_DOORS)
    fun setDoorPosition(
        area: DoorArea,
        value: Int,
        onComplete: (DoorArea, Int?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )


    /* ============ register/unregister ============ */

    /**
     * Registers the door area to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_DOORS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DOORS" />
     * ```
     *
     * @param area [DoorArea]: The door area for the vehicle.
     * @param listener [DoorPositionListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_DOORS)
    fun registerDoorPosition(area: DoorArea, listener: DoorPositionListener)

    /**
     * Stops getting property updates for the given door area.
     *
     * **Permission:** [PleosPermission.CAR_DOORS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DOORS" />
     * ``` 
     * 
     * @param area [DoorArea]: The door area for the vehicle.
     * @param listener [DoorPositionListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_DOORS)
    fun unregisterDoorPosition(area: DoorArea, listener: DoorPositionListener)

    /**
     * Registers the door lock state to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_DOORS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DOORS" />
     * ``` 
     *
     * @param area [DoorArea]: The door area for the vehicle.
     * @param listener [DoorLockStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_DOORS)
    fun registerDoorLockState(area: DoorArea, listener: DoorLockStateListener)

    /**
     * Stops getting property updates for the given door lock state.
     *
     * **Permission:** [PleosPermission.CAR_DOORS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DOORS" />
     * ``` 
     * 
     * @param area [DoorArea]: The door area for the vehicle.
     * @param listener [DoorLockStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_DOORS)
    fun unregisterDoorLockState(area: DoorArea, listener: DoorLockStateListener)

    /**
     * Registers the glove box lock state to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_DOORS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DOORS" />
     * ``` 
     * 
     * @param listener [GloveBoxLockStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_DOORS)
    fun registerGloveBoxLockState(listener: GloveBoxLockStateListener)

    /**
     * Stops getting property updates for the given glove box lock state.
     *
     * **Permission:** [PleosPermission.CAR_DOORS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DOORS" />
     * ``` 
     * 
     * @param listener [GloveBoxLockStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_DOORS)
    fun unregisterGloveBoxLockState(listener: GloveBoxLockStateListener)
}
