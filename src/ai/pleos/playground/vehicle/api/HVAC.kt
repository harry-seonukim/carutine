package ai.pleos.playground.vehicle.api

import ai.pleos.playground.vehicle.api.model.area.SeatArea
import ai.pleos.playground.vehicle.api.model.area.WindowArea
import ai.pleos.playground.vehicle.api.model.capability.AreaBounds
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.api.model.capability.HvacTemperatureConfigArray
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.control.FanDirectionType
import ai.pleos.playground.vehicle.constant.unit.TemperatureUnit
import ai.pleos.playground.vehicle.listener.HvacAcStateListener
import ai.pleos.playground.vehicle.listener.HvacAirCleanerStateListener
import ai.pleos.playground.vehicle.listener.HvacAutoStateListener
import ai.pleos.playground.vehicle.listener.HvacDefrosterStateListener
import ai.pleos.playground.vehicle.listener.HvacDualStateListener
import ai.pleos.playground.vehicle.listener.HvacFanSpeedListener
import ai.pleos.playground.vehicle.listener.HvacMaxAcStateListener
import ai.pleos.playground.vehicle.listener.HvacMaxDefrostStateListener
import ai.pleos.playground.vehicle.listener.HvacPowerStateListener
import ai.pleos.playground.vehicle.listener.HvacRecirculationStateListener
import ai.pleos.playground.vehicle.listener.HvacTemperatureListener
import ai.pleos.playground.vehicle.listener.HvacTemperatureValueListener
import androidx.annotation.RequiresPermission

/**
 * Provides APIs for climate control (HVAC).
 *
 * Obtain an instance from [ai.pleos.playground.vehicle.Vehicle.getHVAC].
 */
interface HVAC {

    @Deprecated(
        message = "Use the new checkHvacAcCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkHvacAcCapabilities({ capabilityConfig, list }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacAcCapabilities(
        onComplete: (List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets HvacAC capabilities.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[SeatArea]>: Callback invoked when the HvacAC capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacAcCapabilities(onComplete: (CapabilityConfig, List<SeatArea>) -> Unit, onFailed: (Exception) -> Unit)

    @Deprecated(
        message = "Use the new checkHvacAutoCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkHvacAutoCapabilities({ capabilityConfig, list }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacAutoCapabilities(
        onComplete: (List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets HvacAuto capabilities.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[SeatArea]>: Callback invoked when the HvacAuto capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacAutoCapabilities(onComplete: (CapabilityConfig, List<SeatArea>) -> Unit, onFailed: (Exception) -> Unit)

    @Deprecated(
        message = "Use the new checkHvacRecirculationCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkHvacRecirculationCapabilities({ capabilityConfig, list }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacRecirculationCapabilities(
        onComplete: (List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets HVAC recirculation capabilities.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[SeatArea]>: Callback invoked when the HvacRecirculation capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacRecirculationCapabilities(onComplete: (CapabilityConfig, List<SeatArea>) -> Unit, onFailed: (Exception) -> Unit)

    @Deprecated(
        message = "Use the new checkHvacDefrosterCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkHvacDefrosterCapabilities({ capabilityConfig, list }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacDefrosterCapabilities(
        onComplete: (List<WindowArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets HVAC defroster capabilities.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[WindowArea]>: Callback invoked when the HvacDefroster capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacDefrosterCapabilities(onComplete: (CapabilityConfig, List<WindowArea>) -> Unit, onFailed: (Exception) -> Unit)

    @Deprecated(
        message = "Use the new checkHvacDualCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkHvacDualCapabilities({ capabilityConfig, list }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacDualCapabilities(
        onComplete: (List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets HVAC dual capabilities.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[SeatArea]>: Callback invoked when the HvacDual capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacDualCapabilities(onComplete: (CapabilityConfig, List<SeatArea>) -> Unit, onFailed: (Exception) -> Unit)

    @Deprecated(
        message = "Use the new checkHvacFanDirectionCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkHvacFanDirectionCapabilities({ capabilityConfig, list }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacFanDirectionCapabilities(
        onComplete: (Map<SeatArea, List<FanDirectionType>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets HVAC fan direction capabilities.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[SeatArea], [List]<[FanDirectionType]>>: Callback invoked when the HvacFanDirection capability configuration and supported fan directions are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacFanDirectionCapabilities(onComplete: (CapabilityConfig, Map<SeatArea, List<FanDirectionType>>) -> Unit, onFailed: (Exception) -> Unit)

    @Deprecated(
        message = "Use the new checkHvacFanSpeedCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkHvacFanSpeedCapabilities({ capabilityConfig, map }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacFanSpeedCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets HVAC fan speed capabilities.
     *
     * The callback receives [CapabilityConfig] (access/support) and a
     * [Map] of [SeatArea] to [AreaBounds] with per-area [AreaBounds.minValue]/[AreaBounds.maxValue].
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[SeatArea], [AreaBounds]<[Int]>>: Callback invoked when the HvacFanSpeed capability configuration and supported fan speed levels are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacFanSpeedCapabilities(onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit, onFailed: (Exception) -> Unit)

    @Deprecated(
        message = "Use the new checkHvacMaxAcCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkHvacMaxAcCapabilities({ capabilityConfig, list }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacMaxAcCapabilities(
        onComplete: (List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets HVAC max AC capabilities.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[SeatArea]>: Callback invoked when the HvacMaxAC capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacMaxAcCapabilities(onComplete: (CapabilityConfig, List<SeatArea>) -> Unit, onFailed: (Exception) -> Unit)

    @Deprecated(
        message = "Use the new checkHvacMaxDefrostCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkHvacMaxDefrostCapabilities({ capabilityConfig, list }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacMaxDefrostCapabilities(
        onComplete: (List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets HVAC max defrost capabilities.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[SeatArea]>: Callback invoked when the HvacMaxDefrost capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacMaxDefrostCapabilities(onComplete: (CapabilityConfig, List<SeatArea>) -> Unit, onFailed: (Exception) -> Unit)

    @Deprecated(
        message = "Use the new checkHvacPowerCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkHvacPowerCapabilities({ capabilityConfig, list }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacPowerCapabilities(
        onComplete: (List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets HVAC power capabilities.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[SeatArea]>: Callback invoked when the HvacPower capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacPowerCapabilities(onComplete: (CapabilityConfig, List<SeatArea>) -> Unit, onFailed: (Exception) -> Unit)

    @Deprecated(
        message = "Use the new checkHvacAutoRecirculationCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkHvacAutoRecirculationCapabilities({ capabilityConfig, list }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacAutoRecirculationCapabilities(
        onComplete: (List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets HVAC auto recirculation capabilities.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[SeatArea]>: Callback invoked when the HvacAutoRecirculation capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacAutoRecirculationCapabilities(onComplete: (CapabilityConfig, List<SeatArea>) -> Unit, onFailed: (Exception) -> Unit)

    @Deprecated(
        message = "Use the new checkHvacAirCleanerCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkHvacAirCleanerCapabilities({ capabilityConfig, list }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacAirCleanerCapabilities(
        onComplete: (List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets HVAC air cleaner capabilities.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[SeatArea]>: Callback invoked when the HvacAirCleaner capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacAirCleanerCapabilities(onComplete: (CapabilityConfig, List<SeatArea>) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Gets the supported target temperature ranges in degrees Celsius (°C).
     *
     * @param onComplete Callback invoked with the supported target temperature range for each
     * seat area, expressed in degrees Celsius (°C).
     * @param onFailed Callback invoked if retrieving the capability information fails.
     */
    @Deprecated(
        message = "Use the new checkHvacTemperatureCapabilities API that returns CapabilityConfig, temperature AreaBounds, and HvacTemperatureConfigArray.",
        replaceWith = ReplaceWith(
            "checkHvacTemperatureCapabilities({ capabilityConfig, areaBounds, configArray }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacTemperatureCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Float>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets HVAC temperature range capabilities as AreaBounds in degrees Celsius (°C).
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[SeatArea], [AreaBounds]<[Float]>>: Callback
     * invoked with the capability configuration and supported target temperature ranges for each
     * seat area, expressed in degrees Celsius (°C).
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @Deprecated(
        message = "Use the overload that returns CapabilityConfig, temperature AreaBounds, and HvacTemperatureConfigArray for HVAC_TEMPERATURE_SET.",
        replaceWith = ReplaceWith(
            "checkHvacTemperatureCapabilities({ capabilityConfig, areaBounds, configArray }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacTemperatureCapabilities(onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Float>>) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Gets HVAC temperature capabilities including supported temperature ranges in degrees Celsius (°C) and the vehicle-specific temperature conversion config array.
     *
     * The returned [HvacTemperatureConfigArray] is derived from the vehicle's temperature setting
     * configuration array and is used to convert temperature values between Celsius and
     * Fahrenheit using the vehicle-specific rule (step/increment based mapping), instead of the
     * generic conversion formula.
     *
     * [HvacTemperatureConfigArray] is the parsed form of the vehicle's temperature conversion
     * config array and maps to the original indexes as follows:
     * - `[0]` -> [HvacTemperatureConfigArray.minCelsius]
     * - `[1]` -> [HvacTemperatureConfigArray.maxCelsius]
     * - `[2]` -> [HvacTemperatureConfigArray.incrementCelsius]
     * - `[3]` -> [HvacTemperatureConfigArray.minFahrenheit]
     * - `[4]` -> [HvacTemperatureConfigArray.maxFahrenheit]
     * - `[5]` -> [HvacTemperatureConfigArray.incrementFahrenheit]
     *
     * Each value is normalized to `Float` (`raw / 10f`). The original integer VHAL values are
     * preserved in [HvacTemperatureConfigArray.raw].
     *
     * Typical usage:
     * - [CapabilityConfig] reports access/support only; [AreaBounds] comes from the second callback argument.
     * - Use each area's [AreaBounds.minValue]/[AreaBounds.maxValue] for the supported Celsius range.
     * - Use [HvacTemperatureConfigArray] to convert Celsius <-> Fahrenheit in the same way as the
     *   vehicle UI (when provided by the vehicle).
     * - If [HvacTemperatureConfigArray] is null, fall back to the standard conversion formula.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[SeatArea], [AreaBounds]<[Float]>>, [HvacTemperatureConfigArray]:
     * Callback invoked with the capability configuration, supported target temperature ranges
     * for each seat area in degrees Celsius (°C), and the temperature conversion configuration.
     * The temperature conversion configuration is `null` if the vehicle does not provide it.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacTemperatureCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Float>>, HvacTemperatureConfigArray?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets HVAC temperature current capabilities.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the HvacTemperatureCurrent capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the Hvac temperature current capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacTemperatureCurrentCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

//    /**
//     * Gets HVAC temperature Value Suggestion Capabilities
//     *
//     * This method is protected by permissions
//     * ```
//     * Adds permissions in AndroidManifest.xml
//     * Protection level: Normal
//     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE"/>
//     * ```
//     *
//     * @param onComplete [CapabilityConfig]: Callback invoked when the HvacTemperatureValueSuggestion capability configuration is successfully retrieved.
//     * @param onFailed [Exception]: Callback invoked if retrieving the Hvac temperature value suggestion capability information fails.
//     *
//     */
//    @RequiresPermission(PleosPermission.CAR_CLIMATE)
//    fun checkHvacTemperatureValueSuggestionCapabilities(onComplete: (CapabilityConfig) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Sets AC to On/Off for the designated areaId.
     * **Permission:** [PleosPermission.CONTROL_CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area of the vehicle.  
     * @param value [Boolean]: The on/off state of the AC.  
     * @param onComplete [SeatArea], [Boolean]: Callback invoked when the AC has been set to on or off for the specified area.  
     * @param onFailed [Exception]: Callback invoked when setting the AC to on or off for the specified area fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    fun setHvacAc(
        area: SeatArea,
        value: Boolean,
        onComplete: (SeatArea, Boolean?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current AC state.
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area of the vehicle.  
     * @param onComplete [Boolean]: Callback invoked when the current AC state is successfully retrieved.  
     * @param onFailed [Exception]: Callback invoked when retrieving the current AC state fails.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun getHvacAcState(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Sets automatic mode to On/Off.
     * **Permission:** [PleosPermission.CONTROL_CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area of the vehicle.  
     * @param value [Boolean]: The on/off state of the automatic mode.  
     * @param onComplete [SeatArea], [Boolean]: Callback invoked when the automatic mode has been set to on or off for the specified area.  
     * @param onFailed [Exception]: Callback invoked when setting the automatic mode to on or off fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    fun setHvacAuto(
        area: SeatArea,
        value: Boolean,
        onComplete: (SeatArea, Boolean?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current automatic mode.
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area of the vehicle.  
     * @param onComplete [Boolean]: Callback invoked when the current automatic mode value is successfully retrieved.  
     * @param onFailed [Exception]: Callback invoked when retrieving the current automatic mode value fails.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun getHvacAutoState(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Sets the automatic recirculation to On/Off.
     * **Permission:** [PleosPermission.CONTROL_CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area of the vehicle.  
     * @param value [Boolean]: The on/off state of the automatic recirculation.  
     * @param onComplete [SeatArea], [Boolean]: Callback invoked when the automatic recirculation has been set to on or off for the specified area.  
     * @param onFailed [Exception]: Callback invoked when setting the automatic recirculation to on or off fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    fun setHvacAutoRecirculation(
        area: SeatArea,
        value: Boolean,
        onComplete: (SeatArea, Boolean?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current automatic recirculation.
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area of the vehicle.  
     * @param onComplete [Boolean]: Callback invoked when the current automatic recirculation value is successfully retrieved.  
     * @param onFailed [Exception]: Callback invoked when retrieving the current automatic recirculation value fails.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun getHvacAutoRecirculationState(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Sets the defroster to On/Off.
     * **Permission:** [PleosPermission.CONTROL_CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_CLIMATE" />
     * ```
     *
     * @param area [WindowArea]: The window area of the vehicle.  
     * @param value [Boolean]: The on/off state of the defroster.  
     * @param onComplete [WindowArea], [Boolean]: Callback invoked when the defroster has been set to on or off for the specified area.  
     * @param onFailed [Exception]: Callback invoked when setting the defroster to on or off fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    fun setHvacDefroster(
        area: WindowArea,
        value: Boolean,
        onComplete: (WindowArea, Boolean?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current defroster state.
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [WindowArea]: The window area of the vehicle.  
     * @param onComplete [Boolean]: Callback invoked when the current defroster state is successfully retrieved.  
     * @param onFailed [Exception]: Callback invoked when retrieving the current defroster state fails.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun getHvacDefrosterState(
        area: WindowArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Enable temperature coupling between areas.
     * **Permission:** [PleosPermission.CONTROL_CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area of the vehicle.  
     * @param value [Boolean]: The on/off state of temperature coupling between areas.  
     * @param onComplete [SeatArea], [Boolean]: Callback invoked when the temperature coupling between areas has been set to on or off for the specified area.  
     * @param onFailed [Exception]: Callback invoked when enabling or disabling the temperature coupling between areas fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    fun setHvacDual(
        area: SeatArea,
        value: Boolean,
        onComplete: (SeatArea, Boolean?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )


    /**
     * Gets the current temperature coupling state between areas.
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area of the vehicle.  
     * @param onComplete [Boolean]: Callback invoked when the current temperature coupling state between areas is successfully retrieved.  
     * @param onFailed [Exception]: Callback invoked when retrieving the current temperature coupling state between areas fails.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun getHvacDualState(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /**
     * Sets fan direction.
     * **Permission:** [PleosPermission.CONTROL_CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea] The seat area for the vehicle.
     * @param value [FanDirectionType]: The fan direction.
     * @param onComplete [SeatArea], [FanDirectionType]: Callback invoked when the fan direction type has been set for the specified area. 
     * @param onFailed [Exception]: Callback invoked when setting the fan direction type fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    fun setHvacFanDirection(
        area: SeatArea,
        value: FanDirectionType,
        onComplete: (SeatArea, FanDirectionType?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current fan direction.
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea] The seat area for the vehicle.
     * @param onComplete [SeatArea], [FanDirectionType]: Callback invoked when the fan direction type has been set for the specified area. 
     * @param onFailed [Exception]: Callback invoked when setting the fan direction type fails.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun getHvacFanDirection(
        area: SeatArea,
        onComplete: (FanDirectionType?) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /**
     * Sets the fan speed.
     * The supported fan speed range is vehicle-dependent and is not fixed.
     * Call [checkHvacFanSpeedCapabilities] first. That API's callback returns
     * [CapabilityConfig] plus a [Map]<[SeatArea], [AreaBounds]<[Int]>>; read each area's
     * [AreaBounds.minValue]/[AreaBounds.maxValue] from that map (not from [CapabilityConfig]).
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area of the vehicle.
     * @param value [Int]: The fan speed.
     * @param onComplete [SeatArea], [Int]: Callback invoked when the fan speed has been set for the specified area.
     * @param onFailed [Exception]: Callback invoked when setting the fan speed fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    fun setHvacFanSpeed(
        area: SeatArea,
        value: Int,
        onComplete: (SeatArea, Int?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current fan speed.
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area of the vehicle.  
     * @param onComplete [Int]: Callback invoked when the current fan speed value is successfully retrieved.  
     * @param onFailed [Exception]: Callback invoked when retrieving the current fan speed value fails.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun getHvacFanSpeed(area: SeatArea, onComplete: (Int?) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Sets the max AC to On/Off.
     * **Permission:** [PleosPermission.CONTROL_CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area of the vehicle.  
     * @param value [Boolean]: The on/off state of the maximum AC.  
     * @param onComplete [SeatArea], [Boolean]: Callback invoked when the maximum AC has been set to on or off for the specified area.  
     * @param onFailed [Exception]: Callback invoked when setting the maximum AC to on or off fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    fun setHvacMaxAc(
        area: SeatArea,
        value: Boolean,
        onComplete: (SeatArea, Boolean?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current max AC state.
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area of the vehicle.  
     * @param onComplete [Boolean]: Callback invoked when the current maximum AC state is successfully retrieved.  
     * @param onFailed [Exception]: Callback invoked when retrieving the current maximum AC state fails.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun getHvacMaxAcState(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Sets the max defrost to On/Off.
     * **Permission:** [PleosPermission.CONTROL_CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea] The seat area for the vehicle.
     * @param value [Boolean]: Max defrost state.
     * @param onComplete [SeatArea], [Boolean]: Callback invoked when the maximum defrost setting has been set to on or off for the specified area.  
     * @param onFailed [Exception]: Callback invoked when setting the maximum defrost to on or off fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    fun setHvacMaxDefrost(
        area: SeatArea,
        value: Boolean,
        onComplete: (SeatArea, Boolean?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current max defrost state.
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area of the vehicle.  
     * @param onComplete [Boolean]: Callback invoked when the current maximum defrost state is successfully retrieved.  
     * @param onFailed [Exception]: Callback invoked when retrieving the current maximum defrost state fails.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun getHvacMaxDefrostState(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Sets the global power state to On/Off.
     * **Permission:** [PleosPermission.CONTROL_CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea] The seat area for the vehicle.
     * @param value [Boolean]: Global power state.
     * @param onComplete [SeatArea], [Boolean]: Callback invoked when the global power state has been set to on or off for the specified area.  
     * @param onFailed [Exception]: Callback invoked when setting the global power state to on or off fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    fun setHvacPower(
        area: SeatArea,
        value: Boolean,
        onComplete: (SeatArea, Boolean?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )


    /**
     * Gets the current global power state.
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea] The seat area for the vehicle.
     * @param onComplete [Boolean]: Callback invoked when the current global power state is successfully retrieved. 
     * @param onFailed [Exception]: Callback invoked when retrieving the current global power state fails.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun getHvacPowerState(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /**
     * Sets recirculation to On/Off.
     * **Permission:** [PleosPermission.CONTROL_CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area of the vehicle.  
     * @param value [Boolean]: The on/off state of the recirculation.  
     * @param onComplete [SeatArea], [Boolean]: Callback invoked when the recirculation has been set to on or off for the specified area.  
     * @param onFailed [Exception]: Callback invoked when setting the recirculation to on or off fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    fun setHvacRecirculation(
        area: SeatArea,
        value: Boolean,
        onComplete: (SeatArea, Boolean?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current recirculation state.
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea] The seat area for the vehicle.
     * @param onComplete [Boolean]: Callback invoked when the current recirculation state is successfully retrieved.  
     * @param onFailed [Exception]: Callback invoked when retrieving the current recirculation state fails.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun getHvacRecirculationState(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /**
     * Sets the air cleaner to On/Off.
     * **Permission:** [PleosPermission.CONTROL_CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area of the vehicle.  
     * @param value [Boolean]: The on/off state of the air cleaner.  
     * @param onComplete [SeatArea], [Boolean]: Callback invoked when the air cleaner has been set to on or off for the specified area.  
     * @param onFailed [Exception]: Callback invoked when setting the air cleaner to on or off fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    fun setHvacAirCleaner(
        area: SeatArea,
        value: Boolean,
        onComplete: (SeatArea, Boolean?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current air cleaner state.
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea] The seat area for the vehicle.
     * @param onComplete [Boolean]: Callback invoked when the current air cleaner state is successfully retrieved. 
     * @param onFailed [Exception]: Callback invoked when retrieving the current air cleaner state fails.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun getHvacAirCleanerState(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    )

//    /**
//     * Gets the suggested closest values for setting the HVAC temperature.
//     * This method is protected by permissions
//     *
//     * ```
//     * Adds permissions in AndroidManifest.xml
//     * Protection level: Normal
//     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE"/>
//     * ```
//     *
//     * @param unit [TemperatureUnit]: Specifies the unit used for the temperature value.
//     * @param onComplete [Float]: Callback invoked when the suggested closest temperature values for HVAC settings are successfully retrieved.
//     * @param onFailed [Exception]: Callback invoked when retrieving the suggested temperature values for HVAC settings fails.
//     */
//    @RequiresPermission(PleosPermission.CAR_CLIMATE)
//    fun getHvacTemperatureValueSuggestion(
//        unit: TemperatureUnit,
//        onComplete: (Float?) -> Unit,
//        onFailed: (Exception) -> Unit
//    )

    /**
     * Gets the target temperature in the specified `unit`.
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea] The seat area for the vehicle.
     * @param unit [TemperatureUnit]: Specifies the unit used for the temperature value.
     * @param onComplete [Float]: Callback invoked with the current target temperature in the
     * requested `unit`, or `null` if no value is available.
     * @param onFailed [Exception]: Callback invoked when retrieving the current target temperature value fails.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun getHvacTemperature(
        area: SeatArea,
        unit: TemperatureUnit,
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Sets the target temperature in the specified [unit].
     * The supported temperature range is vehicle-dependent and is not fixed.
     * Call [checkHvacTemperatureCapabilities] first. That API's callback returns
     * [CapabilityConfig], a [Map]<[SeatArea], [AreaBounds]<[Float]>>, and optional
     * [HvacTemperatureConfigArray]; read each area's [AreaBounds.minValue]/[AreaBounds.maxValue]
     * from that map (not from [CapabilityConfig]). AreaBounds are in degrees Celsius (°C); use the config array
     * when converting to Fahrenheit.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea] The seat area for the vehicle.
     * @param value [Float]: Target temperature in the specified [unit].
     * @param unit [TemperatureUnit]: Specifies the unit used for the temperature value.
     * @param onComplete [SeatArea], [Float]: Callback invoked with the seat area and the requested
     * target temperature in the specified [unit] after the setting operation succeeds.
     * @param onFailed [Exception]: Callback invoked when setting the target temperature fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    fun setHvacTemperature(
        area: SeatArea,
        value: Float,
        unit: TemperatureUnit,
        onComplete: (SeatArea, Float?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current internal temperature of the vehicle in degrees Celsius (°C).
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param onComplete [Float]: Callback invoked when the current temperature value in degrees Celsius (°C) is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current temperature value in degrees Celsius (°C) fails.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun getHvacTemperatureCurrent(
        area: SeatArea,
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets the current internal temperature of the vehicle in the requested unit.
     *
     * The current temperature is read in degrees Celsius (°C), and a Fahrenheit value is returned by applying
     * the vehicle-specific conversion rule when available (otherwise using a formula fallback).
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param unit [TemperatureUnit]: The temperature unit to return.
     * @param onComplete [Float]: Callback invoked when the current temperature value in the requested unit is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current temperature value in the requested unit fails.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun getHvacTemperatureCurrent(
        area: SeatArea,
        unit: TemperatureUnit,
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /**
     * Registers the HvacAC state to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacAcStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun registerHvacAcState(area: SeatArea, listener: HvacAcStateListener)

    /**
     * Stops getting property updates for the given HvacAC state.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacAcStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun unregisterHvacAcState(area: SeatArea, listener: HvacAcStateListener)

    /**
     * Registers the HvacAuto state to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacAutoStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun registerHvacAutoState(area: SeatArea, listener: HvacAutoStateListener)

    /**
     * Stops getting property updates for the given HvacAuto state.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacAutoStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun unregisterHvacAutoState(area: SeatArea, listener: HvacAutoStateListener)

    /**
     * Registers the HvacAutoRecirculation state to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacRecirculationStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun registerHvacAutoRecirculationState(
        area: SeatArea,
        listener: HvacRecirculationStateListener
    )

    /**
     * Stops getting property updates for the given HvacAutoRecirculation state.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacRecirculationStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun unregisterHvacAutoRecirculationState(
        area: SeatArea,
        listener: HvacRecirculationStateListener
    )

    /**
     * Registers the HvacDefroster state to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [WindowArea]: The seat area for the vehicle.
     * @param listener [HvacDefrosterStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun registerHvacDefrosterState(area: WindowArea, listener: HvacDefrosterStateListener)

    /**
     * Stops getting property updates for the given HvacDefroster state.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [WindowArea]: The seat area for the vehicle.
     * @param listener [HvacDefrosterStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun unregisterHvacDefrosterState(area: WindowArea, listener: HvacDefrosterStateListener)

    /**
     * Registers the HvacDual state to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacDualStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun registerHvacDualState(area: SeatArea, listener: HvacDualStateListener)

    /**
     * Stops getting property updates for the given HvacDual state.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacDualStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun unregisterHvacDualState(area: SeatArea, listener: HvacDualStateListener)

    /**
     * Registers the HvacMaxAc state to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacMaxAcStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun registerHvacMaxAcState(area: SeatArea, listener: HvacMaxAcStateListener)

    /**
     * Stop getting property updates for the given HvacMaxAc state.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacMaxAcStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun unregisterHvacMaxAcState(area: SeatArea, listener: HvacMaxAcStateListener)


    /**
     * Registers the HvacMaxDefrost state to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacMaxDefrostStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun registerHvacMaxDefrostState(area: SeatArea, listener: HvacMaxDefrostStateListener)

    /**
     * Stops getting property updates for the given HvacMaxDefrost state.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacMaxDefrostStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun unregisterHvacMaxDefrostState(area: SeatArea, listener: HvacMaxDefrostStateListener)

    /**
     * Registers the HvacPower state to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacPowerStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun registerHvacPowerState(area: SeatArea, listener: HvacPowerStateListener)

    /**
     * Stops getting property updates for the given HvacPower state.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacPowerStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun unregisterHvacPowerState(area: SeatArea, listener: HvacPowerStateListener)

    /**
     * Registers the HvacAirCleaner state to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacAirCleanerStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun registerHvacAirCleanerState(area: SeatArea, listener: HvacAirCleanerStateListener)

    /**
     * Stops getting property updates for the given HvacAirCleaner state.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacAirCleanerStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun unregisterHvacAirCleanerState(area: SeatArea, listener: HvacAirCleanerStateListener)

    /**
     * Registers a listener for current interior temperature updates in degrees Celsius (°C).
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacTemperatureListener]: Listener that receives current interior temperature values in degrees Celsius (°C).
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun registerHvacTemperatureCurrent(area: SeatArea, listener: HvacTemperatureListener)

    /**
     * Registers HvacTemperatureCurrent updates in degrees Celsius (°C) and degrees Fahrenheit (°F).
     *
     * The current temperature is delivered in degrees Celsius (°C), and a value in degrees Fahrenheit (°F)
     * is provided by applying the vehicle-specific conversion rule when available (otherwise using a
     * formula fallback).
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacTemperatureValueListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun registerHvacTemperatureCurrent(area: SeatArea, listener: HvacTemperatureValueListener)

    /**
     * Stops getting property updates for the given HvacTemperatureCurrent.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacTemperatureListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun unregisterHvacTemperatureCurrent(area: SeatArea, listener: HvacTemperatureListener)

    /**
     * Stops getting HvacTemperatureCurrent updates for the listener receiving both Celsius and Fahrenheit values.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacTemperatureValueListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun unregisterHvacTemperatureCurrent(area: SeatArea, listener: HvacTemperatureValueListener)

    /**
     * Registers a listener for target temperature updates in degrees Celsius (°C) only.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacTemperatureListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun registerHvacTemperature(area: SeatArea, listener: HvacTemperatureListener)

    /**
     * Registers the HvacTemperature to get property updates in degrees Celsius (°C) and degrees Fahrenheit (°F).
     *
     * The currently set temperature is delivered in degrees Celsius (°C), and a value in degrees Fahrenheit (°F)
     * is provided by applying the vehicle-specific conversion rule when available (otherwise using a
     * formula fallback).
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacTemperatureValueListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun registerHvacTemperature(area: SeatArea, listener: HvacTemperatureValueListener)

    /**
     * Stops getting property updates for the given HvacTemperature.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacTemperatureListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun unregisterHvacTemperature(area: SeatArea, listener: HvacTemperatureListener)

    /**
     * Stops getting HVAC temperature updates for the given listener that receives both Celsius and Fahrenheit values.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacTemperatureValueListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun unregisterHvacTemperature(area: SeatArea, listener: HvacTemperatureValueListener)

    /**
     * Registers the HvacFanSpeed to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacFanSpeedListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun registerHvacFanSpeed(area: SeatArea, listener: HvacFanSpeedListener)

    /**
     * Stops getting property updates for the given HvacFanSpeed.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacFanSpeedListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun unregisterHvacFanSpeed(area: SeatArea, listener: HvacFanSpeedListener)

    /**
     * Registers the HvacRecirculation to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacRecirculationStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun registerHvacRecirculationState(area: SeatArea, listener: HvacRecirculationStateListener)

    /**
     * Stops getting property updates for the given HvacRecirculation.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param listener [HvacRecirculationStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun unregisterHvacRecirculationState(area: SeatArea, listener: HvacRecirculationStateListener)
}
