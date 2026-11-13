package ai.pleos.playground.vehicle.api

import ai.pleos.playground.vehicle.api.model.capability.AreaBounds
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.listener.HvacSteeringWheelHeatStateListener
import ai.pleos.playground.vehicle.listener.SteeringWheelAngleListener
import androidx.annotation.RequiresPermission

/**
 * Provides APIs for the front-wheel steering angle and steering wheel heating.
 *
 * Obtain an instance from [ai.pleos.playground.vehicle.Vehicle.getSteeringWheel].
 */
interface SteeringWheel {

    @Deprecated(
        message = "Use the new checkHvacSteeringWheelHeatCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkHvacSteeringWheelHeatCapabilities({ capabilityConfig, areaBounds }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacSteeringWheelHeatCapabilities(
        onComplete: (AreaBounds<Int>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets hvac steering wheel heat capabilities.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [AreaBounds]<[Int]>: Callback invoked when the HvacSteeringWheelHeat capability configuration and the supported bounds values are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the steering wheel heat capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun checkHvacSteeringWheelHeatCapabilities(onComplete: (CapabilityConfig, AreaBounds<Int>) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Gets the capabilities for the front-wheel steering angle.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param onComplete Callback invoked with the capability configuration
     * and the supported angle bounds in degrees.
     * @param onFailed Callback invoked if the capability information cannot be
     * retrieved.
     *
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun checkSteeringWheelAngleCapabilities(onComplete: (CapabilityConfig, AreaBounds<Float>) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Gets the current SteeringWheel heating/cooling state.
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param onComplete [Int]: Callback invoked when the current steering wheel heating/cooling state value is successfully retrieved.  
     * @param onFailed [Exception]: Callback invoked when retrieving the current steering wheel heating/cooling state fails.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun getHvacSteeringWheelHeatState(onComplete: (Int?) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Sets SteeringWheel heating/cooling temperature.
     * **Permission:** [PleosPermission.CONTROL_CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_CLIMATE" />
     * ```
     *
     * @param value [Int]: The temperature level to set for the steering wheel heating or cooling.
     * @param onComplete [Int]: Callback invoked when the steering wheel heating/cooling has been successfully set.  
     * @param onFailed [Exception]: Callback invoked when setting the steering wheel heating/cooling fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    fun setHvacSteeringWheelHeat(
        value: Int,
        onComplete: (Int?) -> Unit = {},
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current front-wheel steering angle in degrees.
     *
     * Leftward steering angles are negative. This value does not represent
     * the rotation angle of the steering wheel.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param onComplete Callback invoked with the current front-wheel steering
     * angle in degrees, or `null` if no value is available.
     * @param onFailed Callback invoked if the angle cannot be retrieved.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun getCurrentSteeringWheelAngle(onComplete: (Float?) -> Unit, onFailed: (Exception) -> Unit)


    /**
     * Registers a listener to receive front-wheel steering angle updates.
     *
     * Angles are reported in degrees, and leftward steering angles are negative.
     * The reported value does not represent the rotation angle of the steering wheel.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener Listener to register.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun registerSteeringWheelAngle(listener: SteeringWheelAngleListener)

    /**
     * Unregisters the given listener from front-wheel steering angle updates.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener Listener to unregister.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun unregisterSteeringWheelAngle(listener: SteeringWheelAngleListener)

    /**
     * Registers Hvac SteeringWheel heat state to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param listener [HvacSteeringWheelHeatStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun registerHvacSteeringWheelHeatState(listener: HvacSteeringWheelHeatStateListener)

    /**
     * Stops getting property updates for the given Hvac SteeringWheel heat state.
     *
     * **Permission:** [PleosPermission.CAR_CLIMATE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_CLIMATE" />
     * ```
     *
     * @param listener [HvacSteeringWheelHeatStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_CLIMATE)
    fun unregisterHvacSteeringWheelHeatState(listener: HvacSteeringWheelHeatStateListener)
}
