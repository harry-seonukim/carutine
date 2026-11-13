package ai.pleos.playground.vehicle.api

import ai.pleos.playground.vehicle.api.model.area.WindowArea
import ai.pleos.playground.vehicle.api.model.capability.AreaBounds
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.api.model.capability.SmartGlassConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.control.MoveControl
import ai.pleos.playground.vehicle.constant.control.SmartGlassZoneCommand
import ai.pleos.playground.vehicle.listener.SmartGlassLevelListener
import ai.pleos.playground.vehicle.listener.SmartGlassStateListener
import ai.pleos.playground.vehicle.listener.WindowLockStateListener
import ai.pleos.playground.vehicle.listener.WindowPositionListener
import androidx.annotation.RequiresPermission

/**
 * Provides APIs for window position, lock, blinds, and smart glass.
 *
 * Obtain an instance from [ai.pleos.playground.vehicle.Vehicle.getWindow].
 */
interface Window {

    /**
     * Gets move window blind capabilities.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[WindowArea]>: Callback invoked when the MoveWindowBlind capability configuration and supported window areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the move window blind capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun checkMoveWindowBlindCapabilities(
        onComplete: (CapabilityConfig, List<WindowArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets window blind position capabilities.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[WindowArea], [AreaBounds]<[Int]>>:
     * Callback invoked when the WindowBlindPosition capability configuration and the supported window areas with their corresponding bounds values are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the window blind position capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun checkWindowBlindPositionCapabilities(
        onComplete: (CapabilityConfig, Map<WindowArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /**
     * Gets smart glass state capabilities.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the SmartGlassState capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the smart glass state capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun checkSmartGlassStateCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets smart glass zone state capabilities.
     *
     * Smart glass zone state support does not imply smart glass level support; check [checkSmartGlassLevelCapabilities] separately.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[WindowArea]>: Callback invoked when the SmartGlassZoneState capability configuration and supported window areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the smart glass zone state capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun checkSmartGlassZoneStateCapabilities(
        onComplete: (CapabilityConfig, List<WindowArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets smart glass level capabilities.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[WindowArea], [AreaBounds]<[Int]>>:
     * Callback invoked when the SmartGlassLevel capability configuration and the supported window areas with their corresponding bounds values are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the smart glass level capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun checkSmartGlassLevelCapabilities(
        onComplete: (CapabilityConfig, Map<WindowArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    @Deprecated(
        message = "Use the new checkWindowLockCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkWindowLockCapabilities({ capabilityConfig, list }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun checkWindowLockCapabilities(
        onComplete: (List<WindowArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets window lock capabilities.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[WindowArea]>: Callback invoked when the WindowLock capability configuration and supported window areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the window lock capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun checkWindowLockCapabilities(onComplete: (CapabilityConfig, List<WindowArea>) -> Unit, onFailed: (Exception) -> Unit)


    @Deprecated(
        message = "Use the new checkWindowPositionCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkWindowPositionCapabilities({ capabilityConfig, list }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun checkWindowPositionCapabilities(
        onComplete: (Map<WindowArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets window position capabilities.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[WindowArea], [AreaBounds]<[Int]>>: Callback invoked when the WindowPosition capability configuration and the supported window areas with their corresponding bounds values are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the window position capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun checkWindowPositionCapabilities(onComplete: (CapabilityConfig, Map<WindowArea, AreaBounds<Int>>) -> Unit, onFailed: (Exception) -> Unit)


    /**
     * Sets the current window lock state.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_WINDOWS" />
     * ```
     *
     * @param area [WindowArea]: The window area for the vehicle.
     * @param value [Boolean]: The state to set for the window lock.
     * @param onComplete [WindowArea], [Boolean]: Callback invoked when the window lock has been successfully set.  
     * @param onFailed [Exception]: Callback invoked when setting the window lock fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_WINDOWS)
    fun setWindowLock(
        area: WindowArea,
        value: Boolean,
        onComplete: (WindowArea, Boolean?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current window lock state.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param area [WindowArea]: The window area for the vehicle.
     * @param onComplete [Boolean]: Callback invoked when the current window lock state value is successfully retrieved.  
     * @param onFailed [Exception]: Callback invoked when retrieving the current window lock state fails.
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun getCurrentWindowLockState(
        area: WindowArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets the current window position.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param area [WindowArea]: The window area for the vehicle.
     * @param onComplete [Int]: Callback invoked when the current window position value is successfully retrieved.  
     * @param onFailed [Exception]: Callback invoked when retrieving the current window position fails.
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun getCurrentWindowPosition(
        area: WindowArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Sets the current window position state.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_WINDOWS" />
     * ```
     *
     * @param area [WindowArea]: The window area for the vehicle.
     * @param value [Int]: The position value to set for the window.
     * @param onComplete [WindowArea], [Int]: Callback invoked when the window position has been successfully set.  
     * @param onFailed [Exception]: Callback invoked when setting the window position fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_WINDOWS)
    fun setWindowPosition(
        area: WindowArea,
        value: Int,
        onComplete: (WindowArea, Int?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current Window Blind Position for the specified window area.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param area [WindowArea]: The window area for which to retrieve the current window blind position.
     * @param onComplete [Int]: Callback invoked when the current window blind position is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the current window blind position fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun getCurrentWindowBlindPosition(
        area: WindowArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /**
     * Moves the Window Blind for the specified window area.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_WINDOWS" />
     * ```
     *
     * @param area [WindowArea]: The window area for which to move the window blind.
     * @param value [MoveControl]: The move control command to apply.
     * @param onComplete ([WindowArea], [MoveControl]): Callback invoked when the window blind move command is successfully applied.
     * The callback provides the window area and the applied move control value.
     * @param onFailed [Exception]: Callback invoked if moving the window blind fails.
     *
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_WINDOWS)
    fun moveWindowBlind(
        area: WindowArea,
        value: MoveControl,
        onComplete: (WindowArea, MoveControl?) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /**
     * Gets the current Smart Glass state.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param onComplete [Boolean]: Callback invoked when the current Smart Glass state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the current Smart Glass state fails.
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun getCurrentSmartGlassState(
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /**
     * Sets the Smart Glass state.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_WINDOWS] (protection level: signature|privileged)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_WINDOWS" />
     * ```
     *
     * @param value The Smart Glass state to set.
     * @param onComplete [Boolean]: Callback invoked when the Smart Glass state is successfully set.
     * @param onFailed [Exception]: Callback invoked if setting the Smart Glass state fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_WINDOWS)
    fun setSmartGlassState(
        value: Boolean,
        onComplete: (Boolean?) -> Unit = { _ -> },
        onFailed: (Exception) -> Unit = {}
    )


    /**
     * Sets the Smart Glass zone state by submitting one command per smart glass panel for the specified window area.
     *
     * Commands are submitted in panel order. Every position, including [SmartGlassZoneCommand.SKIP], is preserved,
     * and a list that contains only [SmartGlassZoneCommand.SKIP] is still submitted to the service.
     * The SDK does not query [getSmartGlassConfig] or check capability support before submitting the request.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_WINDOWS" />
     * ```
     *
     * @param area [WindowArea]: The window area for which to set the smart glass zone state. It must contain exactly one window area value.
     * @param value [List]<[SmartGlassZoneCommand]>: The commands to apply in panel order, with one entry per zone reported by [SmartGlassConfig.zoneCount]. It must not be empty.
     * @param onComplete [WindowArea], [List]<[SmartGlassZoneCommand]>: Callback invoked when the smart glass zone commands have been successfully submitted.
     * The callback provides the window area and the submitted commands, not the resulting panel state.
     * @param onFailed [Exception]: Callback invoked if the input is invalid or setting the smart glass zone state fails.
     *
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_WINDOWS)
    fun setSmartGlassZoneState(
        area: WindowArea,
        value: List<SmartGlassZoneCommand>,
        onComplete: (WindowArea, List<SmartGlassZoneCommand>?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the Smart Glass configuration for the specified window area.
     *
     * The configuration is decoded from the vehicle's `[zoneCount, levelCount]` value. For example, `[4, 1]` is returned as
     * `SmartGlassConfig(zoneCount = 4, levelCount = 1)`.
     * Use [SmartGlassConfig.zoneCount] to size the command list for [setSmartGlassZoneState].
     * Smart glass level support may be absent even when this configuration is available; check [checkSmartGlassLevelCapabilities] separately.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param area [WindowArea]: The window area for which to retrieve the smart glass configuration. It must contain exactly one window area value.
     * @param onComplete [SmartGlassConfig]: Callback invoked when the smart glass configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if the input is invalid, retrieving the smart glass configuration fails,
     * or the service returns an empty or invalid configuration.
     *
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun getSmartGlassConfig(
        area: WindowArea,
        onComplete: (SmartGlassConfig?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets the current Smart Glass Level for the specified window area.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param area [WindowArea]: The window area for which to retrieve the current smart glass level.
     * @param onComplete [Int]: Callback invoked when the current smart glass level is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the current smart glass level fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun getCurrentSmartGlassLevel(
        area: WindowArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Sets the Smart Glass Level for the specified window area.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_WINDOWS" />
     * ```
     *
     * @param area [WindowArea]: The window area for which to set the smart glass level.
     * @param value [Int]: The smart glass level value to set.
     * @param onComplete ([WindowArea], [Int]): Callback invoked when the smart glass level is successfully set.
     * The callback provides the window area and the applied smart glass level value.
     * @param onFailed [Exception]: Callback invoked if setting the smart glass level fails.
     *
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_WINDOWS)
    fun setSmartGlassLevel(
        area: WindowArea,
        value: Int,
        onComplete: (WindowArea, Int?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Registers the window lock state to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param area [WindowArea]: The window area for the vehicle.
     * @param listener [WindowLockStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun registerWindowLockState(area: WindowArea, listener: WindowLockStateListener)

    /**
     * Stops getting property updates for the given window lock state.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param area [WindowArea]: The window area for the vehicle.
     * @param listener [WindowLockStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun unregisterWindowLockState(area: WindowArea, listener: WindowLockStateListener)

    /**
     * Registers the window area to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param area [WindowArea]: The window area for the vehicle.
     * @param listener [WindowPositionListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun registerWindowPosition(area: WindowArea, listener: WindowPositionListener)

    /**
     * Stops getting property updates for the given window position.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param area [WindowArea]: The window area for the vehicle.
     * @param listener [WindowPositionListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun unregisterWindowPosition(area: WindowArea, listener: WindowPositionListener)

    /**
     * Registers a listener to receive Window Blind Position updates for the specified window area.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param area [WindowArea]: The window area to observe window blind position changes.
     * @param listener [WindowPositionListener]: The listener to register for window blind position updates.
     *
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun registerWindowBlindPosition(area: WindowArea, listener: WindowPositionListener)

    /**
     * Unregisters a previously registered Window Blind Position listener for the specified window area.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param area [WindowArea]: The window area that was being observed.
     * @param listener [WindowPositionListener]: The listener to unregister.
     *
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun unregisterWindowBlindPosition(area: WindowArea, listener: WindowPositionListener)

    /**
     * Registers a listener to receive Smart Glass Level updates for the specified window area.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param area [WindowArea]: The window area to observe smart glass level changes.
     * @param listener [SmartGlassLevelListener]: The listener to register for smart glass level updates.
     *
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun registerSmartGlassLevel(area: WindowArea, listener: SmartGlassLevelListener)

    /**
     * Unregisters a previously registered Smart Glass Level listener for the specified window area.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param area [WindowArea]: The window area that was being observed.
     * @param listener [SmartGlassLevelListener]: The listener to unregister.
     *
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun unregisterSmartGlassLevel(area: WindowArea, listener: SmartGlassLevelListener)


    /**
     * Registers Smart Glass state listener.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param listener [SmartGlassStateListener]: The listener to receive Smart Glass state updates.
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun registerSmartGlassState(listener: SmartGlassStateListener)


    /**
     * Unregisters Smart Glass state listener.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param listener [SmartGlassStateListener]: The listener to unregister.
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun unregisterSmartGlassState(listener: SmartGlassStateListener)

}
