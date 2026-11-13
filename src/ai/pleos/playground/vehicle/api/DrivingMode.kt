package ai.pleos.playground.vehicle.api

import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.control.CruiseControl
import ai.pleos.playground.vehicle.constant.control.DrivingModeType
import ai.pleos.playground.vehicle.listener.CruiseControlStateListener
import ai.pleos.playground.vehicle.listener.CruiseControlTargetSpeedListener
import ai.pleos.playground.vehicle.listener.DrivingModeStateListener
import androidx.annotation.RequiresPermission

/**
 * Provides APIs for driving mode and cruise control.
 *
 * Obtain an instance from [ai.pleos.playground.vehicle.Vehicle.getDrivingMode].
 */
interface DrivingMode {

    /**
     * Gets CruiseControlTargetSpeed capabilities.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the capability configuration for CruiseControlTargetSpeed is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun checkCruiseControlTargetSpeedCapabilities(onComplete: (CapabilityConfig) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Gets CruiseControlState capabilities.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the capability configuration for CruiseControlState is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun checkCruiseControlStateCapabilities(onComplete: (CapabilityConfig) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Gets DrivingModeState capabilities.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the capability configuration for DrivingModeState is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun checkDrivingModeStateCapabilities(onComplete: (CapabilityConfig) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Gets the current cruise control target speed in meters per second (m/s).
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     * @param onComplete [Float]: Callback invoked with the cruise control target speed in
     * meters per second (m/s), or `null` if no value is available.
     * @param onFailed [Exception]: Callback invoked if retrieving the cruise control target speed fails.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun getCruiseControlTargetSpeed(onComplete: (Float?) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Gets the current cruise control state.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     * @param onComplete [CruiseControl]: Callback invoked when the cruise control state value is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the cruise control state fails.

     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun getCruiseControlState(onComplete: (CruiseControl?) -> Unit, onFailed: (Exception) -> Unit)


    /**
     * Gets current driving mode state.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     * @param onComplete [DrivingModeType]: Callback invoked when the current driving mode state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the current driving mode state fails.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun getCurrentDrivingModeState(onComplete: (DrivingModeType?) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Registers a listener for cruise control target speed updates in meters per second (m/s).
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener [CruiseControlTargetSpeedListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun registerCruiseControlTargetSpeed(listener: CruiseControlTargetSpeedListener)

    /**
     * Stops getting property updates for the given CruiseControlTargetSpeed.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener [CruiseControlTargetSpeedListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun unregisterCruiseControlTargetSpeed(listener: CruiseControlTargetSpeedListener)

    /**
     * Registers CruiseControlState to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener [CruiseControlStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun registerCruiseControlState(listener: CruiseControlStateListener)

    /**
     * Stops getting property updates for the given CruiseControlState.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener [CruiseControlStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun unregisterCruiseControlState(listener: CruiseControlStateListener)

    /**
     * Registers DrivingModeState to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener [DrivingModeStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun registerDrivingModeState(listener: DrivingModeStateListener)

    /**
     * Stops getting property updates for the given DrivingModeState.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener [DrivingModeStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun unregisterDrivingModeState(listener: DrivingModeStateListener)

}
