package ai.pleos.playground.vehicle.api

import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.listener.EvBatteryEfficiencyListener
import ai.pleos.playground.vehicle.listener.EvBatteryLevelListener
import ai.pleos.playground.vehicle.listener.EvBatteryStateOfChargeListener
import ai.pleos.playground.vehicle.listener.EvChargePortOpenListener
import androidx.annotation.RequiresPermission

/**
 * Provides APIs for EV battery efficiency.
 *
 * Obtain an instance from [ai.pleos.playground.vehicle.Vehicle.getEvBattery].
 */
interface EvBattery {

    /**
     * Gets Instantaneous EV Efficiency capabilities.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the Instantaneous EV Efficiency capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability configuration fails.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun checkInstantaneousEvEfficiencyCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /**
     * Gets EV Efficiency Since Charge capabilities.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the EV Efficiency Since Charge capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability configuration fails.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun checkSinceChargeEvEfficiencyCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /**
     * Gets Cumulative EV Efficiency capabilities.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the Cumulative EV Efficiency capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability configuration fails.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun checkCumulativeEvEfficiencyCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets Current EV Efficiency capabilities.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the Current EV Efficiency capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability configuration fails.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun checkCurrentEvEfficiencyCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets EV Battery State Of Charge capabilities.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the EV Battery State Of Charge capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability configuration fails.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun checkEvBatteryStateOfChargeCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets the supported EV charge current draw limits in amperes (A).
     *
     * @param onComplete Callback invoked with the supported current draw limits in amperes (A).
     * @param onFailed Callback invoked if retrieving the capability information fails.
     */
    @Deprecated(
        message = "Use the new checkEvChargeCurrentDrawLimitCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkEvChargeCurrentDrawLimitCapabilities({ capabilityConfig, list }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun checkEvChargeCurrentDrawLimitCapabilities(
        onComplete: (List<Float>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets capabilities for EV charge current draw limits in amperes (A).
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[Float]>: Callback invoked with the capability
     * configuration and supported current draw limits in amperes (A).
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun checkEvChargeCurrentDrawLimitCapabilities(
        onComplete: (CapabilityConfig, List<Float>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets capabilities for EV charge limits, each expressed as a percentage (%).
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[Float]>: Callback invoked with the capability
     * configuration and supported battery state-of-charge limits, each expressed as a percentage (%).
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun checkEvChargePercentLimitCapabilities(
        onComplete: (CapabilityConfig, List<Float>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets EvChargePortOpen capabilities.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the capability configuration for EvChargePortOpen is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun checkEvChargePortOpenCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets EvChargeSwitch capabilities.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the capability configuration for EvChargeSwitch is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun checkEvChargeSwitchCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets instantaneous EV efficiency.
     *
     * Instantaneous EV efficiency in km/kWh.
     *
     * The instantaneous EV battery efficiency of the vehicle is reported in units of km/kWh.
     * This API will be deprecated in the future, as the latest AAOS provides
     * `INSTANTANEOUS_EV_EFFICIENCY` as a standard vehicle property.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [Float]: Callback invoked when the instantaneous EV efficiency is successfully retrieved.
     * The value represents efficiency in km/kWh.
     * @param onFailed [Exception]: Callback invoked if retrieving the instantaneous EV efficiency fails.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun getInstantaneousEvEfficiency(
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /**
     * Gets EV efficiency since charge.
     *
     * EV efficiency after recharge.
     *
     * The EV efficiency of the vehicle measured since the last recharge.
     * For instantaneous EV efficiency, see the `INSTANTANEOUS_EV_EFFICIENCY` vehicle property.
     * Units follow km/kWh, the same as `INSTANTANEOUS_EV_EFFICIENCY`.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [Float]: Callback invoked when the EV efficiency since charge is successfully retrieved.
     * The value represents efficiency in km/kWh.
     * @param onFailed [Exception]: Callback invoked if retrieving the EV efficiency since charge fails.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun getSinceChargeEvEfficiency(
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /**
     * Gets cumulative EV efficiency.
     *
     * Cumulative EV efficiency.
     *
     * The cumulative EV efficiency of the vehicle calculated over its operating period.
     * For instantaneous EV efficiency, see the `INSTANTANEOUS_EV_EFFICIENCY` vehicle property.
     * Units follow km/kWh, the same as `INSTANTANEOUS_EV_EFFICIENCY`.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [Float]: Callback invoked when the cumulative EV efficiency is successfully retrieved.
     * The value represents efficiency in km/kWh.
     * @param onFailed [Exception]: Callback invoked if retrieving the cumulative EV efficiency fails.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun getCumulativeEvEfficiency(
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets current EV efficiency.
     *
     * Current EV efficiency in km/kWh.
     *
     * The EV efficiency of the vehicle during the current driving session.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [Float]: Callback invoked when the current EV efficiency is successfully retrieved.
     * The value represents efficiency in km/kWh.
     * @param onFailed [Exception]: Callback invoked if retrieving the current EV efficiency fails.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun getCurrentEvEfficiency(
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets EV battery state of charge as a percentage (%).
     *
     * Current EV battery percentage.
     *
     * The EV battery state of charge of the vehicle is reported as a percentage (%).
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [Int]: Callback invoked when the EV battery state of charge is successfully retrieved.
     * The value represents battery state of charge as a percentage (%).
     * @param onFailed [Exception]: Callback invoked if retrieving the EV battery state of charge fails.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun getEvBatteryStateOfCharge(
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /**
     * @suppress
     * @hide
     * Gets the current EV battery level in watt-hours (Wh), if EV or hybrid.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [Float]: Callback invoked when the EV battery level value is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the EV battery level fails.
     */
    @Deprecated(
        message = "This API is deprecated, use the Google EV_BATTERY_LEVEL property.",
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun getEvBatteryLevel(onComplete: (Float?) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Registers Current EV Efficiency listener.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param listener [EvBatteryEfficiencyListener]: The listener to receive current EV efficiency updates.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun registerCurrentEvEfficiency(listener: EvBatteryEfficiencyListener)

    /**
     * Unregisters Current EV Efficiency listener.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param listener [EvBatteryEfficiencyListener]: The listener to unregister.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun unregisterCurrentEvEfficiency(listener: EvBatteryEfficiencyListener)

    /**
     * Registers a listener for EV battery state of charge updates expressed as a percentage (%).
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param listener [EvBatteryStateOfChargeListener]: The listener to receive EV battery state of charge updates as a percentage (%).
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun registerEvBatteryStateOfCharge(listener: EvBatteryStateOfChargeListener)

    /**
     * Unregisters EV Battery State Of Charge listener.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param listener [EvBatteryStateOfChargeListener]: The listener to unregister.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun unregisterEvBatteryStateOfCharge(listener: EvBatteryStateOfChargeListener)

    /**
     * Sets the EV charge current draw limit in amperes (A).
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_ENERGY" />
     * ```
     *
     * @param value [Float]: The EV charge current draw limit, expressed in amperes (A).
     * @param onComplete [Float]: Callback invoked with the current draw limit in amperes (A)
     * after it has been successfully set.
     * @param onFailed [Exception]: Callback invoked if setting the EV charge current draw limit fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_ENERGY)
    fun setEvChargeCurrentDrawLimit(
        value: Float,
        onComplete: (Float?) -> Unit = {},
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the EV charge current draw limit in amperes (A).
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [Float]: Callback invoked with the current draw limit in amperes (A),
     * or `null` if no value is available.
     * @param onFailed [Exception]: Callback invoked if retrieving the EV charge current draw limit fails.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun getEvChargeCurrentDrawLimit(onComplete: (Float?) -> Unit, onFailed: (Exception) -> Unit)


    /**
     * Sets the battery state-of-charge limit for EV charging, expressed as a percentage (%).
     *
     * Use [checkEvChargePercentLimitCapabilities] to retrieve the values supported by the vehicle.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_ENERGY" />
     * ```
     *
     * @param value [Float]: The battery state-of-charge limit ranges from 0 % to 100 %.
     * The value must be supported by the vehicle.
     * @param onComplete [Float]: Callback invoked with the requested state-of-charge limit,
     * expressed as a percentage (%), after the setting operation succeeds.
     * @param onFailed [Exception]: Callback invoked if setting the EV charge percent limit fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_ENERGY)
    fun setEvChargePercentLimit(
        value: Float,
        onComplete: (Float?) -> Unit = {},
        onFailed: (Exception) -> Unit = {}
    )


    /**
     * @suppress
     * @hide
     * Gets the EV charge port open state.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [Boolean]: Callback invoked when the EV charge port open state value is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the EV charge port open state fails.
     */
    @Deprecated(
        "This API is deprecated, use the Google EV_CHARGE_PORT_OPEN property.",
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun isEvChargePortOpen(onComplete: (Boolean?) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Sets the EV charge port open state to On or Off.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_ENERGY" />
     * ```
     *
     * @param value [Boolean]: The EV charge port open state to be set.
     * @param onComplete [Float]: Callback invoked when the EV charge port open state has been successfully set.
     * @param onFailed [Exception]: Callback invoked if setting the EV charge port open state fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_ENERGY)
    fun setEvChargePortOpen(
        value: Boolean,
        onComplete: (Boolean?) -> Unit = {},
        onFailed: (Exception) -> Unit = {}
    )


    /**
     * Sets whether to start or stop EV battery charging.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_ENERGY" />
     * ```
     *
     * @param value [Boolean]: Set to true to start EV battery charging, or false to stop charging.
     * @param onComplete [Float]: Callback invoked when the EV battery charging state has been successfully set.
     * @param onFailed [Exception]: Callback invoked if setting the EV battery charging state fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_ENERGY)
    fun setEvChargeSwitch(
        value: Boolean,
        onComplete: (Boolean?) -> Unit = {},
        onFailed: (Exception) -> Unit = {}
    )


    /**
     * Gets the current EV battery charging state.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [Boolean]: Callback invoked when the EV battery charging state value is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the EV battery charging state fails.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun getEvChargeSwitchState(onComplete: (Boolean?) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * @suppress
     * @hide
     * Gets the EV battery capacity in watt-hours (Wh), if EV or hybrid.
     *
     * **Permission:** [PleosPermission.CAR_INFO] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_INFO" />
     * ```
     *
     * @param onComplete [Float]: Callback invoked when the EV battery capacity in watt-hours (Wh) is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the EV battery capacity fails.
     */
    @Deprecated(
        "This API is deprecated, use the Google INFO_EV_BATTERY_CAPACITY property.",
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_INFO)
    fun getEvBatteryCapacity(onComplete: (Float?) -> Unit, onFailed: (Exception) -> Unit)

    /* ================ register / unregister ================ */


    /**
     * Registers Instantaneous EV Efficiency listener.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param listener [EvBatteryEfficiencyListener]: The listener to receive instantaneous EV efficiency updates.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun registerInstantaneousEvEfficiency(listener: EvBatteryEfficiencyListener)

    /**
     * Unregisters Instantaneous EV Efficiency listener.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param listener [EvBatteryEfficiencyListener]: The listener to unregister.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun unregisterInstantaneousEvEfficiency(listener: EvBatteryEfficiencyListener)


    /**
     * Registers EV Efficiency Since Charge listener.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param listener [EvBatteryEfficiencyListener]: The listener to receive EV efficiency since charge updates.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun registerSinceChargeEvEfficiency(listener: EvBatteryEfficiencyListener)


    /**
     * Unregisters EV Efficiency Since Charge listener.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param listener [EvBatteryEfficiencyListener]: The listener to unregister.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun unregisterSinceChargeEvEfficiency(listener: EvBatteryEfficiencyListener)


    /**
     * Registers Cumulative EV Efficiency listener.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param listener [EvBatteryEfficiencyListener]: The listener to receive cumulative EV efficiency updates.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun registerCumulativeEvEfficiency(listener: EvBatteryEfficiencyListener)


    /**
     * Unregisters Cumulative EV Efficiency listener.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param listener [EvBatteryEfficiencyListener]: The listener to unregister.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun unregisterCumulativeEvEfficiency(listener: EvBatteryEfficiencyListener)


    /**
     * @suppress
     * @hide
     * Registers for EV battery level property updates.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param listener [EvBatteryLevelListener]: Listener is registered.
     */
    @Deprecated(
        message = "This API is deprecated, use the Google EV_BATTERY_LEVEL property.",
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun registerEvBatteryLevel(listener: EvBatteryLevelListener)

    /**
     * @suppress
     * @hide
     * Stops getting property updates for the given EV battery level.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param listener [EvBatteryLevelListener]: Listener is unregistered.
     */
    @Deprecated(
        message = "This API is deprecated, use the Google EV_BATTERY_LEVEL property.",
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun unregisterEvBatteryLevel(listener: EvBatteryLevelListener)

    /**
     * @suppress
     * @hide
     * Registers for EvChargePortOpen property updates.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param listener [EvChargePortOpenListener]: Listener is registered.
     */
    @Deprecated(
        "This API is deprecated, use the Google EV_CHARGE_PORT_OPEN property.",
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun registerEvChargePortOpen(listener: EvChargePortOpenListener)

    /**
     * @suppress
     * @hide
     * Stops getting property updates for the given EV EvChargePortOpen.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param listener [EvChargePortOpenListener]: Listener is unregistered.
     */
    @Deprecated(
        "This API is deprecated, use the Google EV_CHARGE_PORT_OPEN property.",
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun unregisterEvChargePortOpen(listener: EvChargePortOpenListener)

}
