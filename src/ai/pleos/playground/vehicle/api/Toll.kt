package ai.pleos.playground.vehicle.api

import ai.pleos.playground.vehicle.api.model.capability.AreaBounds
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.listener.EtcsVoiceGuidanceVolumeListener
import androidx.annotation.RequiresPermission

/**
 * Provides APIs for ETCS voice guidance volume.
 *
 * Obtain an instance from [ai.pleos.playground.vehicle.Vehicle.getToll].
 */
interface Toll {

    /**
     * Gets ETCS voice guidance volume capabilities.
     *
     * **Permission:** [PleosPermission.CAR_TOLL] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_TOLL" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [AreaBounds]<[Int]>: Callback invoked when the ETCS voice guidance volume capability configuration and supported bounds are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     */
    @RequiresPermission(PleosPermission.CAR_TOLL)
    fun checkEtcsVoiceGuidanceVolumeCapabilities(
        onComplete: (CapabilityConfig, AreaBounds<Int>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets current ETCS voice guidance volume.
     *
     * **Permission:** [PleosPermission.CAR_TOLL] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_TOLL" />
     * ```
     *
     * @param onComplete [Int]: Callback invoked when the current ETCS voice guidance volume is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the current ETCS voice guidance volume fails.
     */
    @RequiresPermission(PleosPermission.CAR_TOLL)
    fun getCurrentEtcsVoiceGuidanceVolume(onComplete: (Int?) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Sets ETCS voice guidance volume.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_TOLL] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_TOLL" />
     * ```
     *
     * @param value [Int]: ETCS voice guidance volume value.
     * @param onComplete [Unit]: Callback invoked when setting ETCS voice guidance volume succeeds.
     * @param onFailed [Exception]: Callback invoked if setting ETCS voice guidance volume fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_TOLL)
    fun setEtcsVoiceGuidanceVolume(value: Int, onComplete: () -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Registers ETCS voice guidance volume updates.
     *
     * **Permission:** [PleosPermission.CAR_TOLL] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_TOLL" />
     * ```
     *
     * @param listener [EtcsVoiceGuidanceVolumeListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_TOLL)
    fun registerEtcsVoiceGuidanceVolume(listener: EtcsVoiceGuidanceVolumeListener)

    /**
     * Stops ETCS voice guidance volume updates.
     *
     * **Permission:** [PleosPermission.CAR_TOLL] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_TOLL" />
     * ```
     *
     * @param listener [EtcsVoiceGuidanceVolumeListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_TOLL)
    fun unregisterEtcsVoiceGuidanceVolume(listener: EtcsVoiceGuidanceVolumeListener)
}
