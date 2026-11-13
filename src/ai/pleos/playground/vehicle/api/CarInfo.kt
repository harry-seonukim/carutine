package ai.pleos.playground.vehicle.api

import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import androidx.annotation.RequiresPermission
import androidx.annotation.RestrictTo

/**
 * Provides APIs for vehicle identification information and capability info.
 *
 * Obtain an instance from [ai.pleos.playground.vehicle.Vehicle.getCarInfo].
 */
interface CarInfo {

    /**
     * Gets CRN info capabilities.
     *
     * **Permission:** [PleosPermission.CAR_INFO] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_INFO" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the CRNInfo capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the CRN info capability information fails.
     *
     * @deprecated Use [checkVINInfoCapabilities] to check VIN capability.
     */
    @Deprecated(
        message = "Use checkVINInfoCapabilities to check VIN capability.",
        replaceWith = ReplaceWith("checkVINInfoCapabilities(onComplete, onFailed)"),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_INFO)
    fun checkCRNInfoCapabilities(onComplete: (CapabilityConfig) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Gets VIN info capabilities.
     *
     * **Permission:** [PleosPermission.CAR_INFO] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_INFO" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the VIN capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the VIN capability information fails.
     */
    @RequiresPermission(PleosPermission.CAR_INFO)
    fun checkVINInfoCapabilities(onComplete: (CapabilityConfig) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Gets the Vehicle Identification Number (VIN).
     *
     * **Permission:** [PleosPermission.CAR_INFO] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_INFO" />
     * ```
     *
     * @param onComplete [String]: Callback invoked when the Vehicle Identification Number (VIN)
     * is successfully retrieved. The value may be `null`.
     * @param onFailed [Exception]: Callback invoked if retrieving the Vehicle Identification Number (VIN) fails.
     */
    @RequiresPermission(PleosPermission.CAR_INFO)
    fun getVINInfo(onComplete: (String?) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Gets the Connected Resource Name (CRN) Info.
     *
     * **Permission:** [PleosPermission.CAR_INFO] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_INFO" />
     * ```
     *
     * @param onComplete [String]: Callback invoked when the Connected Resource Name (CRN) information is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the Connected Resource Name (CRN) information fails.
     *
     * @deprecated Use [getVINInfo] to retrieve the Vehicle Identification Number (VIN).
     */
    @Deprecated(
        message = "Use getVINInfo to retrieve the Vehicle Identification Number (VIN).",
        replaceWith = ReplaceWith("getVINInfo(onComplete, onFailed)"),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_INFO)
    fun getCRNInfo(onComplete: (String?) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * @suppress
     * @hide
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @RequiresPermission(PleosPermission.PRIVATE_CAR_INFO)
    fun getCapabilityInfo(onComplete: (Map<String, ai.pleos.playground.vehicle.data.CapabilityConfig<Any>>?) -> Unit, onFailed: (Exception) -> Unit)

}
