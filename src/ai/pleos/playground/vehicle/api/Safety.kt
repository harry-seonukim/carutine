package ai.pleos.playground.vehicle.api

import ai.pleos.playground.vehicle.api.model.area.SeatArea
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.api.model.telltale.Telltales
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.control.OilLevelState
import ai.pleos.playground.vehicle.listener.LowWasherFluidWaringListener
import ai.pleos.playground.vehicle.listener.OilLevelStateListener
import ai.pleos.playground.vehicle.listener.PowerDownWarningIndicatorListener
import ai.pleos.playground.vehicle.listener.SeatAirbagDeployedListener
import ai.pleos.playground.vehicle.listener.SeatAirbagEnabledListener
import ai.pleos.playground.vehicle.listener.SeatBeltNotBuckledWarningListener
import ai.pleos.playground.vehicle.listener.TelltaleInfoListener
import androidx.annotation.RequiresPermission

/**
 * Provides APIs for airbags, warnings, fluid levels, and telltales.
 *
 * Obtain an instance from [ai.pleos.playground.vehicle.Vehicle.getSafety].
 */
interface Safety {

    @Deprecated(
        message = "Use the new checkAirbagCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkAirbagCapabilities({ capabilityConfig, list }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun checkAirbagCapabilities(
        onComplete: (List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets airbag capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[SeatArea]>: Callback invoked when the Airbag capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun checkAirbagCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /**
     * Gets airbag deployed capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[SeatArea]>: Callback invoked when the AirbagDeployed capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the airbag deployed capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun checkAirbagDeployedCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )


    @Deprecated(
        message = "Use the new checkSeatBuckledCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkSeatBuckledCapabilities({ capabilityConfig, list }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun checkSeatBuckledCapabilities(
        onComplete: (List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets seat buckled capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[SeatArea]>: Callback invoked when the SeatBuckled capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun checkSeatBuckledCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets seat not buckled warning capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[SeatArea]>: Callback invoked when the SeatNotBuckledWarning capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the seat not buckled warning capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun checkSeatNotBuckledWarningCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets engine oil level state capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[OilLevelState]>: Callback invoked when the EngineOilLevelState capability configuration and supported oil level states are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun checkEngineOilLevelStateCapabilities(
        onComplete: (CapabilityConfig, List<OilLevelState>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets brake fluid level state capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[OilLevelState]>: Callback invoked when the BrakeFluidLevelState capability configuration and supported oil level states are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun checkBrakeFluidLevelStateCapabilities(
        onComplete: (CapabilityConfig, List<OilLevelState>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets transmission oil level state capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[OilLevelState]>: Callback invoked when the TransmissionOilLevelState capability configuration and supported oil level states are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun checkTransmissionOilLevelStateCapabilities(
        onComplete: (CapabilityConfig, List<OilLevelState>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets low washer fluid warning state capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the LowWasherFluidWarningState capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun checkLowWasherFluidWarningStateCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets power down warning indicator capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the PowerDownWarningIndicator capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun checkPowerDownWarningIndicatorCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets telltales capabilities.
     *
     * **Permission:** [PleosPermission.PRIVATE_CAR_SAFETY] (protection level: signature|privileged)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.PRIVATE_CAR_SAFETY" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the Telltales capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the telltales capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.PRIVATE_CAR_SAFETY)
    fun checkTelltalesCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /**
     * Gets the current engine oil level.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param onComplete [OilLevelState]: Callback invoked when the current engine oil level state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current engine oil level state fails.
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun getEngineOilLevelState(onComplete: (OilLevelState?) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Gets the current brake fluid level.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param onComplete [OilLevelState]: Callback invoked when the current brake fluid level state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current brake fluid level state fails.
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun getBrakeFluidLevelState(onComplete: (OilLevelState?) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Gets the current transmission oil level.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param onComplete [OilLevelState]: Callback invoked when the current transmission oil level state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current transmission oil level state fails.
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun getTransmissionOilLevelState(
        onComplete: (OilLevelState?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Checks the current washer fluid low warning status.
     * If the status is true, there is an issue with the washer fluid
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param onComplete [Boolean]: Callback invoked when the low washer fluid state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the low washer fluid state fails.
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun hasLowWasherFluidWarning(onComplete: (Boolean?) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Gets the current airbag deployment status.
     * If the state is true, the airbag is deployed.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param onComplete [Boolean]: Callback invoked when the airbag deployment state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the airbag deployment state fails.
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun hasSeatAirbagBeenDeployed(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /**
     * Gets the current power down warning indicator state. If the state is true, there is a problem with the engine.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param onComplete [Boolean]: Callback invoked when the current power down warning indicator state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current power down warning indicator state fails.
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun hasPowerDownWarningIndicator(onComplete: (Boolean?) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Gets the seat airbag enabled state. If the state is true, there is a problem with the seat airbag in the [area].
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param onComplete [Boolean]: Callback invoked when the seat airbag enabled state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the seat airbag enabled state fails.
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun hasSeatAirbagEnabled(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets the seat belt buckled warning state. If the state is true, the seat belt in the [area] is not buckled.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param onComplete [Boolean]: Callback invoked when the seat belt buckled warning state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the seat belt buckled warning state fails.
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun hasSeatBeltNotBuckledWarning(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /**
     * Gets telltales info.
     *
     * **Permission:** [PleosPermission.PRIVATE_CAR_SAFETY] (protection level: signature|privileged)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.PRIVATE_CAR_SAFETY" />
     * ```
     *
     * @param onComplete [Telltales]: Callback invoked when the current telltales information is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the telltales information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun getTelltalesInfo(onComplete: (telltales: Telltales) -> Unit, onFailed: (Exception) -> Unit)


    /**
     * Registers the PowerDownWarningIndicator to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param listener [PowerDownWarningIndicatorListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun registerPowerDownWarningIndicator(listener: PowerDownWarningIndicatorListener)

    /**
     * Stops getting property updates for the given PowerDownWarningIndicator.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param listener [PowerDownWarningIndicatorListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun unregisterPowerDownWarningIndicator(listener: PowerDownWarningIndicatorListener)

    /**
     * Registers the SeatAirbagEnabled to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatAirbagEnabledListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun registerSeatAirbagEnabled(area: SeatArea, listener: SeatAirbagEnabledListener)

    /**
     * Stops getting property updates for the given SeatAirbagEnabled.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatAirbagEnabledListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun unregisterSeatAirbagEnabled(area: SeatArea, listener: SeatAirbagEnabledListener)

    /**
     * Registers the SeatBeltNotBuckledWarning to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatBeltNotBuckledWarningListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun registerSeatBeltNotBuckledWarning(
        area: SeatArea,
        listener: SeatBeltNotBuckledWarningListener
    )

    /**
     * Stops getting property updates for the given SeatBeltNotBuckledWarning.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatBeltNotBuckledWarningListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun unregisterSeatBeltNotBuckledWarning(
        area: SeatArea,
        listener: SeatBeltNotBuckledWarningListener
    )

    /**
     * Registers the LowWasherFluidWaringState to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param listener [LowWasherFluidWaringListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun registerLowWasherFluidWaringState(listener: LowWasherFluidWaringListener)

    /**
     * Stops getting property updates for the given LowWasherFluidWaringState.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param listener [LowWasherFluidWaringListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun unregisterLowWasherFluidWaringState(listener: LowWasherFluidWaringListener)

    /**
     * Registers the BrakeFluidLevelState to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param listener [OilLevelStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun registerBrakeFluidLevelState(listener: OilLevelStateListener)

    /**
     * Stops getting property updates for the given BrakeFluidLevelState.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param listener [OilLevelStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun unregisterBrakeFluidLevelState(listener: OilLevelStateListener)

    /**
     * Registers the TransmissionOilLevel to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param listener [OilLevelStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun registerTransmissionOilLevelState(listener: OilLevelStateListener)

    /**
     * Stops getting property updates for the given TransmissionOilLevel.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param listener [OilLevelStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun unregisterTransmissionOilLevelState(listener: OilLevelStateListener)

    /**
     * Registers the EngineOilLevelState to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param listener [OilLevelStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun registerEngineOilLevelState(listener: OilLevelStateListener)

    /**
     * Stops getting property updates for the given EngineOilLevelState.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param listener [OilLevelStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun unregisterEngineOilLevelState(listener: OilLevelStateListener)

    /**
     * Registers the SeatAirbagDeployed to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatAirbagDeployedListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun registerSeatAirbagDeployed(area: SeatArea, listener: SeatAirbagDeployedListener)

    /**
     * Stops getting property updates for the given SeatAirbagDeployed.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatAirbagDeployedListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun unregisterSeatAirbagDeployed(area: SeatArea, listener: SeatAirbagDeployedListener)

    /**
     * Registers a Telltales listener to receive telltale information updates.
     *
     * **Permission:** [PleosPermission.PRIVATE_CAR_SAFETY] (protection level: signature|privileged)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.PRIVATE_CAR_SAFETY" />
     * ```
     *
     * @param listener [TelltaleInfoListener]: The listener to register for telltale updates.
     */
    @RequiresPermission(PleosPermission.PRIVATE_CAR_SAFETY)
    fun registerTelltales(listener: TelltaleInfoListener)

    /**
     * Unregisters a previously registered Telltales listener.
     *
     * **Permission:** [PleosPermission.PRIVATE_CAR_SAFETY] (protection level: signature|privileged)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.PRIVATE_CAR_SAFETY" />
     * ```
     *
     * @param listener [TelltaleInfoListener]: The listener to unregister from telltale updates.
     */
    @RequiresPermission(PleosPermission.PRIVATE_CAR_SAFETY)
    fun unregisterTelltales(listener: TelltaleInfoListener)

}
