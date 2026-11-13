package ai.pleos.playground.vehicle.api.implement

import ai.pleos.playground.vehicle.api.Toll
import ai.pleos.playground.vehicle.api.model.capability.AreaBounds
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.PleosPropertyIds
import ai.pleos.playground.vehicle.constant.VehicleAreaCustomType
import ai.pleos.playground.vehicle.constant.VehicleAreaValue
import ai.pleos.playground.vehicle.listener.EtcsVoiceGuidanceVolumeListener
import ai.pleos.playground.vehicle.permission.RequiresCustomPermission
import ai.pleos.playground.vehicle.service.VehicleServiceManager

internal class TollImpl(private val serviceManager: VehicleServiceManager) : Toll {

    @RequiresCustomPermission(PleosPermission.CAR_TOLL)
    override fun checkEtcsVoiceGuidanceVolumeCapabilities(
        onComplete: (CapabilityConfig, AreaBounds<Int>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestGlobalBoundsCapability(
            instance = this,
            propId = PleosPropertyIds.ETCS_VOICE_GUIDANCE_VOLUME,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_TOLL)
    override fun getCurrentEtcsVoiceGuidanceVolume(
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.ETCS_VOICE_GUIDANCE_VOLUME,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(it?.toInt()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_TOLL)
    override fun setEtcsVoiceGuidanceVolume(
        value: Int,
        onComplete: () -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.ETCS_VOICE_GUIDANCE_VOLUME,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            value = value,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_TOLL)
    override fun registerEtcsVoiceGuidanceVolume(listener: EtcsVoiceGuidanceVolumeListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.ETCS_VOICE_GUIDANCE_VOLUME,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_TOLL)
    override fun unregisterEtcsVoiceGuidanceVolume(listener: EtcsVoiceGuidanceVolumeListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.ETCS_VOICE_GUIDANCE_VOLUME,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }
}
