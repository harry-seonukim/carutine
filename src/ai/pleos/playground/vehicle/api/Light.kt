package ai.pleos.playground.vehicle.api

import ai.pleos.playground.vehicle.api.model.area.SeatArea
import ai.pleos.playground.vehicle.api.model.capability.AreaBounds
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.control.LightState
import ai.pleos.playground.vehicle.constant.control.LightSwitch
import ai.pleos.playground.vehicle.listener.AmbientColorAutoByDriveModeEnabledListener
import ai.pleos.playground.vehicle.listener.AmbientLightBrightnessColorListener
import ai.pleos.playground.vehicle.listener.AmbientLightBrightnessListener
import ai.pleos.playground.vehicle.listener.LightStateListener
import androidx.annotation.RequiresPermission

/**
 * Provides APIs for exterior, cabin, and ambient lights.
 *
 * Obtain an instance from [ai.pleos.playground.vehicle.Vehicle.getLight].
 */
interface Light {
    @Deprecated(
        message = "Use the new checkReadingLightSwitchCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkReadingLightSwitchCapabilities({ capabilityConfig, map }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun checkReadingLightSwitchCapabilities(
        onComplete: (Map<SeatArea, List<LightSwitch>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets reading light switch capabilities.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[SeatArea], [List]<[LightSwitch]>>: Callback invoked when the ReadingLightSwitch capability configuration and supported light switches are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun checkReadingLightSwitchCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, List<LightSwitch>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    @Deprecated(
        message = "Use the new checkCabinLightSwitchCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkCabinLightSwitchCapabilities({ capabilityConfig, list }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun checkCabinLightSwitchCapabilities(
        onComplete: (List<LightSwitch>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets cabin light switch capabilities.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[LightSwitch]>: Callback invoked when the CabinLightSwitch capability configuration and supported light switches are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun checkCabinLightSwitchCapabilities(
        onComplete: (CapabilityConfig, List<LightSwitch>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    @Deprecated(
        message = "Use the new checkHighBeamLightSwitchCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkHighBeamLightSwitchCapabilities({ capabilityConfig, list }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun checkHighBeamLightSwitchCapabilities(
        onComplete: (List<LightSwitch>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets high beam light switch capabilities.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[LightSwitch]>: Callback invoked when the HighBeamLightSwitch capability configuration and supported light switches are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun checkHighBeamLightSwitchCapabilities(
        onComplete: (CapabilityConfig, List<LightSwitch>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    @Deprecated(
        message = "Use the new checkLowBeamLightSwitchCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkLowBeamLightSwitchCapabilities({ capabilityConfig, list }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun checkLowBeamLightSwitchCapabilities(
        onComplete: (List<LightSwitch>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets low beam light switch capabilities.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[LightSwitch]>: Callback invoked when the LowBeamLightSwitch capability configuration and supported light switches are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun checkLowBeamLightSwitchCapabilities(
        onComplete: (CapabilityConfig, List<LightSwitch>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    @Deprecated(
        message = "Use the new checkTailLampLightSwitchCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkTailLampLightSwitchCapabilities({ capabilityConfig, list }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun checkTailLampLightSwitchCapabilities(
        onComplete: (List<LightSwitch>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets tail lamp light switch capabilities.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[LightSwitch]>: Callback invoked when the TailLampLightSwitch capability configuration and supported light switches are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun checkTailLampLightSwitchCapabilities(
        onComplete: (CapabilityConfig, List<LightSwitch>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    @Deprecated(
        message = "Use the new checkFrontFogLightSwitchCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkFrontFogLightSwitchCapabilities({ capabilityConfig, list }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun checkFrontFogLightSwitchCapabilities(
        onComplete: (List<LightSwitch>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets front fog light switch capabilities.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[LightSwitch]>: Callback invoked when the FrontFogLightSwitch capability configuration and supported light switches are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun checkFrontFogLightSwitchCapabilities(
        onComplete: (CapabilityConfig, List<LightSwitch>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    @Deprecated(
        message = "Use the new checkRearFogLightSwitchCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkRearFogLightSwitchCapabilities({ capabilityConfig, list }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )

    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun checkRearFogLightSwitchCapabilities(
        onComplete: (List<LightSwitch>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets rear fog light switch capabilities.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[LightSwitch]>: Callback invoked when the RearFogLightSwitch capability configuration and supported light switches are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun checkRearFogLightSwitchCapabilities(
        onComplete: (CapabilityConfig, List<LightSwitch>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets reading light state capabilities.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[SeatArea], [List]<[LightState]>>: Callback invoked when the ReadingLightState capability configuration and supported light states are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun checkReadingLightStateCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, List<LightState>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets cabin light state capabilities.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[LightState]>: Callback invoked when the CabinLightState capability configuration and supported light states are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun checkCabinLightStateCapabilities(
        onComplete: (CapabilityConfig, List<LightState>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets high beam light state capabilities.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[LightState]>: Callback invoked when the HighBeamLightState capability configuration and supported light states are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun checkHighBeamLightStateCapabilities(
        onComplete: (CapabilityConfig, List<LightState>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets low beam light state capabilities.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[LightState]>: Callback invoked when the LowBeamLightState capability configuration and supported light states are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun checkLowBeamLightStateCapabilities(
        onComplete: (CapabilityConfig, List<LightState>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets tail lamp light state capabilities.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[LightState]>: Callback invoked when the TailLampLightState capability configuration and supported light states are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun checkTailLampLightStateCapabilities(
        onComplete: (CapabilityConfig, List<LightState>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets front fog light state capabilities.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[LightState]>: Callback invoked when the FrontFogLightState capability configuration and supported light states are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun checkFrontFogLightStateCapabilities(
        onComplete: (CapabilityConfig, List<LightState>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets rear fog light state capabilities.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[LightState]>: Callback invoked when the RearFogLightState capability configuration and supported light states are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun checkRearFogLightStateCapabilities(
        onComplete: (CapabilityConfig, List<LightState>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets ambient light brightness capabilities.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[SeatArea], [AreaBounds]<[Int]>>: Callback invoked when the AmbientLightBrightness capability configuration and the supported areas with their corresponding bounds values are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun checkAmbientLightBrightnessCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets ambient light brightness color capabilities.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[SeatArea]>: Callback invoked when the AmbientLightBrightnessColor capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the ambient light brightness color capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun checkAmbientLightBrightnessColorCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets ambient light switch capabilities.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[SeatArea], [List]<[LightSwitch]>>:
     * Callback invoked when the AmbientLightSwitch capability configuration and the supported switch values for each seat area are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the ambient light switch capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun checkAmbientLightSwitchCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, List<LightSwitch>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets ambient light state capabilities.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[SeatArea], [List]<[LightState]>>:
     * Callback invoked when the AmbientLightState capability configuration and the supported state values for each seat area are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the ambient light state capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun checkAmbientLightStateCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, List<LightState>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets Ambient Color Auto By Drive Mode Enabled capabilities.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the AmbientColorAutoByDriveModeEnabled capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the ambient color auto by drive mode enabled capability information fails.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun checkAmbientColorAutoByDriveModeEnabledCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    )


    // Get / Set
    /**
     * Gets the current reading light switch.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     * @param area [SeatArea] The seat area for the vehicle.
     * @param onComplete [LightSwitch]: Callback invoked when the current reading light switch state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current reading light switch state fails.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun getReadingLightSwitch(
        area: SeatArea,
        onComplete: (LightSwitch?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Sets the reading light switch.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_LIGHTS" />
     * ```
     *
     * @param area [SeatArea]: The seat area of the vehicle.
     * @param value [LightSwitch]: The reading light switch state to set.
     * @param onComplete [LightSwitch]: Callback invoked when the reading light switch state is successfully set.
     * @param onFailed [Exception]: Callback invoked if setting the reading light switch state fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_LIGHTS)
    fun setReadingLightSwitch(
        area: SeatArea,
        value: LightSwitch,
        onComplete: (LightSwitch?) -> Unit = {},
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current reading light state.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param area [SeatArea]: The seat area of the vehicle.
     * @param onComplete [LightState]: Callback invoked when the current reading light state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current reading light state fails.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun getCurrentReadingLightState(
        area: SeatArea,
        onComplete: (LightState?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets the current cabin light switch.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     * @param onComplete [LightSwitch]: Callback invoked when the current cabin light switch state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the cabin light switch state fails.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun getCabinLightSwitch(onComplete: (LightSwitch?) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Sets the cabin light switch.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_LIGHTS" />
     * ```
     *
     * @param value [LightSwitch]: The cabin light value to set.
     * @param onComplete [LightSwitch]: Callback invoked when the cabin light switch has been successfully set.
     * @param onFailed [Exception]: Callback invoked when setting the cabin light switch fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_LIGHTS)
    fun setCabinLightSwitch(
        value: LightSwitch,
        onComplete: (LightSwitch?) -> Unit = {},
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current cabin light state.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     * @param onComplete [LightState]: Callback invoked when the current cabin light state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current cabin light state fails.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun getCurrentCabinLightState(
        onComplete: (LightState?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets the current high beam light state.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     * @param onComplete [LightState]: Callback invoked when the current high beam light state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current high beam light state fails.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun getCurrentHighBeamLightState(
        onComplete: (LightState?) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /**
     * Gets the current high beam light switch.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     * @param onComplete [LightState]: Callback invoked when the current high beam light state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current high beam light state fails.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun getHighBeamLightSwitch(onComplete: (LightSwitch?) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Sets the high beam light switch.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_LIGHTS" />
     * ```
     *
     * @param value [LightSwitch]: The high beam light value to set.
     * @param onComplete [LightSwitch]: Callback invoked when the high beam light switch has been successfully set.
     * @param onFailed [Exception]: Callback invoked when setting the high beam light switch fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_LIGHTS)
    fun setHighBeamLightSwitch(
        value: LightSwitch,
        onComplete: (LightSwitch?) -> Unit = {},
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Sets the low beam light switch.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_LIGHTS" />
     * ```
     *
     * @param value [LightSwitch]: The low beam light value to set.
     * @param onComplete [LightSwitch]: Callback invoked when the low beam light switch has been successfully set.
     * @param onFailed [Exception]: Callback invoked when setting the low beam light switch fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_LIGHTS)
    fun setLowBeamLightSwitch(
        value: LightSwitch,
        onComplete: (LightSwitch?) -> Unit = {},
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current low beam light switch.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     * @param onComplete [LightSwitch]: Callback invoked when the low beam light state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the low beam light switch state fails.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun getLowBeamLightSwitch(onComplete: (LightSwitch?) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Gets the current low beam light state.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     * @param onComplete [LightState]: Callback invoked when the current low beam light state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current low beam light state fails.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun getCurrentLowBeamLightState(
        onComplete: (LightState?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets the current tail lamp light state.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     * @param onComplete [LightState]: Callback invoked when the current tail lamp light state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current tail lamp light state fails.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun getCurrentTailLampLightState(
        onComplete: (LightState?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets the current tail lamp light switch.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     * @param onComplete [LightSwitch]: Callback invoked when the current tail lamp light switch state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the tail lamp light switch state fails.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun getTailLampLightSwitch(onComplete: (LightSwitch?) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Sets the tail lamp light switch.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_LIGHTS" />
     * ```
     *
     * @param value [LightSwitch]: The tail lamp light value to set.
     * @param onComplete [LightSwitch]: Callback invoked when the tail lamp light switch has been successfully set.
     * @param onFailed [Exception]: Callback invoked when setting the tail lamp light switch fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_LIGHTS)
    fun setTailLampLightSwitch(
        value: LightSwitch,
        onComplete: (LightSwitch?) -> Unit = {},
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current front fog light state.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     * @param onComplete [LightState]: Callback invoked when the current front fog light state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current front fog light state fails.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun getCurrentFrontFogLightState(
        onComplete: (LightState?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets the current front fog light switch.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     * @param onComplete [LightState]: Callback invoked when the current front fog light switch state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the front fog light switch state fails.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun getFrontFogLightSwitch(onComplete: (LightSwitch?) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Sets the front fog light switch.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_LIGHTS" />
     * ```
     *
     * @param value [LightSwitch]: The front fog light switch value to set.
     * @param onComplete [LightSwitch]: Callback invoked when the front fog light switch has been successfully set.
     * @param onFailed [Exception]: Callback invoked when setting the front fog light switch fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_LIGHTS)
    fun setFrontFogLightSwitch(
        value: LightSwitch,
        onComplete: (LightSwitch?) -> Unit = {},
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current rear fog light state.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     * @param onComplete [LightState]: Callback invoked when the current rear fog light state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current rear fog light state fails.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun getCurrentRearFogLightState(
        onComplete: (LightState?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets the current rear fog light switch.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     * @param onComplete [LightState]: Callback invoked when the current rear fog light switch state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the rear fog light switch state fails.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun getRearFogLightSwitch(onComplete: (LightSwitch?) -> Unit, onFailed: (Exception) -> Unit)


    /**
     * Sets the rear fog light switch.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_LIGHTS" />
     * ```
     *
     * @param value [LightSwitch]: The rear fog light switch value to set.
     * @param onComplete [LightSwitch]: Callback invoked when the rear fog light switch has been successfully set.
     * @param onFailed [Exception]: Callback invoked when setting the rear fog light switch fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_LIGHTS)
    fun setRearFogLightSwitch(
        value: LightSwitch,
        onComplete: (LightSwitch?) -> Unit = {},
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current Ambient Light Brightness for the specified seat area.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param area [SeatArea]: The seat area for which to retrieve the ambient light brightness.
     * @param onComplete [Int]: Callback invoked when the current ambient light brightness is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the ambient light brightness fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun getAmbientLightBrightness(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Sets the Ambient Light Brightness for the specified seat area.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param area [SeatArea]: The seat area for which to set the ambient light brightness.
     * @param value [Int]: The brightness value to set.
     * @param onComplete ([SeatArea], [Int]): Callback invoked when the ambient light brightness is successfully set.
     * @param onFailed [Exception]: Callback invoked if setting the ambient light brightness fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun setAmbientLightBrightness(
        area: SeatArea,
        value: Int,
        onComplete: ((SeatArea, Int) -> Unit),
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets the current Ambient Light Brightness Color (32-bit ARGB) for the specified seat area.
     *
     * Alpha is fixed to 0xFF (fully opaque).
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param area [SeatArea]: The seat area for which to retrieve the ambient light brightness color.
     * @param onComplete [Int]: Callback invoked when the current ambient light brightness color is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the ambient light brightness color fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun getAmbientLightBrightnessColor(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Sets the Ambient Light Brightness Color for the specified seat area.
     *
     * The value is a 32-bit ARGB color. Alpha is fixed to 0xFF (fully opaque).
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param area [SeatArea]: The seat area for which to set the ambient light brightness color.
     * @param value [Int]: The 32-bit ARGB color value to set.
     * @param onComplete [SeatArea], [Int]: Callback invoked when the ambient light brightness color is successfully set.
     * @param onFailed [Exception]: Callback invoked if setting the ambient light brightness color fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun setAmbientLightBrightnessColor(
        area: SeatArea,
        value: Int,
        onComplete: ((SeatArea, Int) -> Unit),
        onFailed: (Exception) -> Unit
    )


    /**
     * Gets the current Ambient Light Switch for the specified seat area.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param area [SeatArea]: The seat area for which to retrieve the ambient light switch.
     * @param onComplete [LightSwitch]: Callback invoked when the current ambient light switch is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the ambient light switch fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun getAmbientLightSwitch(
        area: SeatArea,
        onComplete: (LightSwitch?) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /**
     * Sets the Ambient Light Switch for the specified seat area.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param area [SeatArea]: The seat area for which to set the ambient light switch.
     * @param value [LightSwitch]: The switch value to set.
     * @param onComplete [SeatArea], [LightSwitch]: Callback invoked when the ambient light switch is successfully set.
     * The callback provides the seat area and the applied switch value.
     * @param onFailed [Exception]: Callback invoked if setting the ambient light switch fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun setAmbientLightSwitch(
        area: SeatArea,
        value: LightSwitch,
        onComplete: ((SeatArea, LightSwitch?) -> Unit),
        onFailed: (Exception) -> Unit
    )



    /**
     * Gets the current Ambient Light State for the specified seat area.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param area [SeatArea]: The seat area for which to retrieve the ambient light state.
     * @param onComplete [LightState]: Callback invoked when the current ambient light state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the ambient light state fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun getAmbientLightState(
        area: SeatArea,
        onComplete: (LightState?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets whether Ambient Color Auto By Drive Mode is enabled.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param onComplete [Boolean]: Callback invoked when the ambient color auto by drive mode enabled state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the ambient color auto by drive mode enabled state fails.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun getAmbientColorAutoByDriveModeEnabled(
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Sets whether Ambient Color Auto By Drive Mode is enabled.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_LIGHTS" />
     * ```
     *
     * @param value [Boolean]: The ambient color auto by drive mode enabled state to set.
     * @param onComplete [Boolean]: Callback invoked when the ambient color auto by drive mode enabled state is successfully set.
     * @param onFailed [Exception]: Callback invoked if setting the ambient color auto by drive mode enabled state fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_LIGHTS)
    fun setAmbientColorAutoByDriveModeEnabled(
        value: Boolean,
        onComplete: (Boolean?) -> Unit = {},
        onFailed: (Exception) -> Unit = {}
    )


    /**
     * Registers the HighBeamLightState to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param listener [LightStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun registerHighBeamLightState(listener: LightStateListener)

    /**
     * Stops getting property updates for the given HighBeamLightState.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param listener [LightStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun unregisterHighBeamLightState(listener: LightStateListener)

    /**
     * Registers the LowBeamLightState to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param listener [LightStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun registerLowBeamLightState(listener: LightStateListener)

    /**
     * Stops getting property updates for the given LowBeamLightState.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param listener [LightStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun unregisterLowBeamLightState(listener: LightStateListener)

    /**
     * Registers the TailLampLightState to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param listener [LightStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun registerTailLampLightState(listener: LightStateListener)

    /**
     * Stops getting property updates for the given TailLampLightState.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param listener [LightStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun unregisterTailLampLightState(listener: LightStateListener)

    /**
     * Registers the FrontFogLightState to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param listener [LightStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun registerFrontFogLightState(listener: LightStateListener)

    /**
     * Stops getting property updates for the given FrontFogLightState.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param listener [LightStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun unregisterFrontFogLightState(listener: LightStateListener)

    /**
     * Registers the RearFogLightState to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param listener [LightStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun registerRearFogLightState(listener: LightStateListener)

    /**
     * Stops getting property updates for the given RearFogLightState.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param listener [LightStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun unregisterRearFogLightState(listener: LightStateListener)

    /**
     * Registers the ReadingLightState to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [LightStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun registerReadingLightState(area: SeatArea, listener: LightStateListener)

    /**
     * Stops getting property updates for the given ReadingLightState.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [LightStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun unregisterReadingLightState(area: SeatArea, listener: LightStateListener)

    /**
     * Registers the CabinLightState to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param listener [LightStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun registerCabinLightState(listener: LightStateListener)

    /**
     * Stops getting property updates for the given CabinLightState.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param listener [LightStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun unregisterCabinLightState(listener: LightStateListener)

    /**
     * Registers a listener to receive Ambient Light Brightness updates for the specified seat area.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param area [SeatArea]: The seat area to observe ambient light brightness changes.
     * @param listener [AmbientLightBrightnessListener]: The listener to register.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun registerAmbientLightBrightnessListener(
        area: SeatArea,
        listener: AmbientLightBrightnessListener
    )


    /**
     * Unregisters a previously registered Ambient Light Brightness listener for the specified seat area.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param area [SeatArea]: The seat area that was being observed.
     * @param listener [AmbientLightBrightnessListener]: The listener to unregister.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun unregisterAmbientLightBrightnessListener(
        area: SeatArea,
        listener: AmbientLightBrightnessListener
    )


    /**
     * Registers a listener to receive Ambient Light Brightness Color updates for the specified seat area.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param area [SeatArea]: The seat area to observe ambient light brightness color changes.
     * @param listener [AmbientLightBrightnessColorListener]: The listener to register.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun registerAmbientLightBrightnessColorListener(
        area: SeatArea,
        listener: AmbientLightBrightnessColorListener
    )


    /**
     * Unregisters a previously registered Ambient Light Brightness Color listener for the specified seat area.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param area [SeatArea]: The seat area that was being observed.
     * @param listener [AmbientLightBrightnessColorListener]: The listener to unregister.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun unregisterAmbientLightBrightnessColorListener(
        area: SeatArea,
        listener: AmbientLightBrightnessColorListener
    )



    /**
     * Registers a listener to receive Ambient Light State updates for the specified seat area.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param area [SeatArea]: The seat area to observe ambient light state changes.
     * @param listener [LightStateListener]: The listener to register.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun registerAmbientLightStateListener(
        area: SeatArea,
        listener: LightStateListener
    )


    /**
     * Unregisters a previously registered Ambient Light State listener for the specified seat area.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param area [SeatArea]: The seat area that was being observed.
     * @param listener [LightStateListener]: The listener to unregister.
     *
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun unregisterAmbientLightStateListener(
        area: SeatArea,
        listener: LightStateListener
    )

    /**
     * Registers a listener to receive Ambient Color Auto By Drive Mode Enabled updates.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param listener [AmbientColorAutoByDriveModeEnabledListener]: The listener to register.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun registerAmbientColorAutoByDriveModeEnabled(
        listener: AmbientColorAutoByDriveModeEnabledListener
    )

    /**
     * Unregisters a previously registered Ambient Color Auto By Drive Mode Enabled listener.
     *
     * **Permission:** [PleosPermission.CAR_LIGHTS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_LIGHTS" />
     * ```
     *
     * @param listener [AmbientColorAutoByDriveModeEnabledListener]: The listener to unregister.
     */
    @RequiresPermission(PleosPermission.CAR_LIGHTS)
    fun unregisterAmbientColorAutoByDriveModeEnabled(
        listener: AmbientColorAutoByDriveModeEnabledListener
    )



}
