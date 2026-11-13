package ai.pleos.playground.vehicle.api

import ai.pleos.playground.vehicle.api.model.area.WheelArea
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.listener.TirePressureListener
import androidx.annotation.RequiresPermission

/**
 * Provides APIs for tire pressure.
 *
 * Obtain an instance from [ai.pleos.playground.vehicle.Vehicle.getTire].
 */
interface Tire {

    @Deprecated(
        "This API is deprecated, use checkLowTirePressureCapabilities instead.",
        replaceWith = ReplaceWith(
            "checkLowTirePressureCapabilities({ capabilityConfig, map }, onFailed)",
            "ai.pleos.playground.vehicle.api.model.capability"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun checkWheelAreaCapabilities(
        onComplete: (List<WheelArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets low tire pressure capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[WheelArea]>: Callback invoked when the LowTirePressure capability configuration and supported wheel areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the low tire pressure capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun checkLowTirePressureCapabilities(onComplete: (CapabilityConfig, List<WheelArea>) -> Unit, onFailed: (Exception) -> Unit)


    /**
     * Gets the critically low tire pressure threshold in kilopascals (kPa).
     * 
     * **Permission:** [PleosPermission.CAR_SAFETY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SAFETY" />
     * ```
     * @param area [WheelArea]: The wheel area for which to retrieve the threshold.
     * @param onComplete [Float]: Callback invoked with the critically low tire pressure threshold
     * in kilopascals (kPa), or `null` if no value is available.
     * @param onFailed [Exception]: Callback invoked when retrieving the threshold fails.
     */
    @RequiresPermission(PleosPermission.CAR_SAFETY)
    fun getLowTirePressure(area: WheelArea, onComplete: (Float?) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * @suppress
     * @hide
     * Gets Tire Pressure Capabilities
     *
     * **Permission:** [PleosPermission.PRIVATE_CAR_SAFETY] (protection level: signature|privileged)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.PRIVATE_CAR_SAFETY" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[WheelArea]>: Callback invoked when the TirePressure capability configuration and supported wheel areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the tire pressure capability information fails.
     */
    @RequiresPermission(PleosPermission.PRIVATE_CAR_SAFETY)
    fun checkTirePressureCapabilities(
        onComplete: (CapabilityConfig, List<WheelArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * @suppress
     * @hide
     * Gets the current tire pressure in kilopascals (kPa).
     *
     * **Permission:** [PleosPermission.PRIVATE_CAR_SAFETY] (protection level: signature|privileged)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.PRIVATE_CAR_SAFETY" />
     * ```
     *
     * @param area [WheelArea]: The wheel area for which to retrieve the current tire pressure.
     * @param onComplete [Float]: Callback invoked with the current tire pressure in kilopascals
     * (kPa), or `null` if no value is available.
     * @param onFailed [Exception]: Callback invoked when retrieving the current tire pressure fails.
     */
    @RequiresPermission(PleosPermission.PRIVATE_CAR_SAFETY)
    fun getTirePressure(
        area: WheelArea,
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * @suppress
     * @hide
     * Registers a listener to receive tire pressure updates for the specified wheel area.
     *
     * **Permission:** [PleosPermission.PRIVATE_CAR_SAFETY] (protection level: signature|privileged)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.PRIVATE_CAR_SAFETY" />
     * ```
     *
     * @param area [WheelArea]: The wheel area to observe tire pressure changes.
     * @param listener [TirePressureListener]: The listener to register for tire pressure updates.
     */
    @RequiresPermission(PleosPermission.PRIVATE_CAR_SAFETY)
    fun registerTirePressure(area: WheelArea, listener: TirePressureListener)

    /**
     * @suppress
     * @hide
     * Unregisters a previously registered tire pressure listener for the specified wheel area.
     *
     * **Permission:** [PleosPermission.PRIVATE_CAR_SAFETY] (protection level: signature|privileged)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.PRIVATE_CAR_SAFETY" />
     * ```
     *
     * @param area [WheelArea]: The wheel area that was being observed.
     * @param listener [TirePressureListener]: The listener to unregister.
     */
    @RequiresPermission(PleosPermission.PRIVATE_CAR_SAFETY)
    fun unregisterTirePressure(area: WheelArea, listener: TirePressureListener)
}
