package ai.pleos.playground.vehicle.api

import ai.pleos.playground.vehicle.api.model.area.WindowArea
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.control.WiperState
import ai.pleos.playground.vehicle.constant.control.WiperSwitch
import ai.pleos.playground.vehicle.listener.WiperStateListener
import androidx.annotation.RequiresPermission

/**
 * Provides APIs for wiper state and switch.
 *
 * Obtain an instance from [ai.pleos.playground.vehicle.Vehicle.getWiper].
 */
interface Wiper {

    @Deprecated(
        message = "Use the new checkWiperStateCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkWiperStateCapabilities({ capabilityConfig, map }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun checkWiperStateCapabilities(
        onComplete: (Map<WindowArea, List<WiperState>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets wiper state capabilities.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[WindowArea], [List]<[WiperState]>>: Callback invoked when the WiperState capability configuration and the supported window areas with their corresponding wiper states are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the wiper state capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun checkWiperStateCapabilities(onComplete: (CapabilityConfig, Map<WindowArea, List<WiperState>>) -> Unit, onFailed: (Exception) -> Unit)


    @Deprecated(
        message = "Use the new checkWiperSwitchCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkWiperSwitchCapabilities({ capabilityConfig, map }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun checkWiperSwitchCapabilities(
        onComplete: (Map<WindowArea, List<WiperSwitch>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets wiper switch capabilities.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[WindowArea], [List]<[WiperSwitch]>>: Callback invoked when the WiperSwitch capability configuration and the supported window areas with their corresponding wiper switches are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the wiper switch capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun checkWiperSwitchCapabilities(onComplete: (CapabilityConfig, Map<WindowArea, List<WiperSwitch>>) -> Unit, onFailed: (Exception) -> Unit)


    /**
     * Gets the current wiper mode state.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param area [WindowArea]: The window area for the vehicle.
     * @param onComplete [WiperState]: Callback invoked when the current wiper mode state is successfully retrieved.  
     * @param onFailed [Exception]: Callback invoked when retrieving the current wiper mode state fails.
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun getCurrentWiperState(
        area: WindowArea,
        onComplete: (WiperState?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets the current Wiper Switch state for the specified window area.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param area [WindowArea]: The window area for which to retrieve the current wiper switch state.
     * @param onComplete [WiperSwitch]: Callback invoked when the current wiper switch state for the specified area is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the current wiper switch state fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun getCurrentWiperSwitch(area: WindowArea, onComplete: (WiperSwitch?) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Sets the current wiper switch.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_WINDOWS" />
     * ```
     *
     * @param area [WindowArea]: The window area for the vehicle.
     * @param value [WiperSwitch]: The wiper mode to set.
     * @param onComplete [WindowArea], [WiperSwitch]: Callback invoked when the wiper mode has been successfully switched.  
     * @param onFailed [Exception]: Callback invoked when setting the wiper mode state fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_WINDOWS)
    fun setWiperSwitch(
        area: WindowArea,
        value: WiperSwitch,
        onComplete: (WindowArea, WiperSwitch?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Registers the wiper state to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param area [WindowArea]: The window area for the vehicle.
     * @param listener [WiperStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun registerWiperState(area: WindowArea, listener: WiperStateListener)

    /**
     * Stops getting property updates for the given wiper state.
     *
     * **Permission:** [PleosPermission.CAR_WINDOWS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_WINDOWS" />
     * ```
     *
     * @param area [WindowArea]: The window area for the vehicle.
     * @param listener [WiperStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_WINDOWS)
    fun unregisterWiperState(area: WindowArea, listener: WiperStateListener)

}
