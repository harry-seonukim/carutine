package ai.pleos.playground.vehicle.api.implement

import ai.pleos.playground.vehicle.api.Brake
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.PleosPropertyIds
import ai.pleos.playground.vehicle.constant.VehicleAreaCustomType
import ai.pleos.playground.vehicle.constant.VehicleAreaValue
import ai.pleos.playground.vehicle.listener.BrakePedalStatusListener
import ai.pleos.playground.vehicle.permission.RequiresCustomPermission
import ai.pleos.playground.vehicle.service.VehicleServiceManager

internal class BrakeImpl(private val serviceManager: VehicleServiceManager) : Brake {
//    private val tag = StringUtils.TAG(this.javaClass.simpleName)

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun checkBrakePedalStatusCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAccessCapability(
            instance = this,
            propId = PleosPropertyIds.BRAKE_PEDAL_STATUS,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun getCurrentBrakePedalStatus(
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.BRAKE_PEDAL_STATUS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(it?.toBoolean()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun registerBrakePedalStatus(listener: BrakePedalStatusListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.BRAKE_PEDAL_STATUS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun unregisterBrakePedalStatus(listener: BrakePedalStatusListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.BRAKE_PEDAL_STATUS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }
}
