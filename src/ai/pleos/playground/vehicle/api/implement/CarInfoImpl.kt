package ai.pleos.playground.vehicle.api.implement

import ai.pleos.playground.vehicle.api.CarInfo
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.PleosPropertyIds
import ai.pleos.playground.vehicle.constant.VehicleAreaCustomType
import ai.pleos.playground.vehicle.constant.VehicleAreaValue
import ai.pleos.playground.vehicle.permission.RequiresCustomPermission
import ai.pleos.playground.vehicle.service.VehicleServiceManager
import ai.pleos.playground.vehicle.utils.StringUtils

@Suppress("OVERRIDE_DEPRECATION")
internal class CarInfoImpl(private val serviceManager: VehicleServiceManager) : CarInfo {
    private val tag = StringUtils.TAG(this.javaClass.simpleName)

    @RequiresCustomPermission(PleosPermission.CAR_INFO)
    override fun checkCRNInfoCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAccessCapability(
            instance = this,
            propId = PleosPropertyIds.INFO_CRN,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_INFO)
    override fun checkVINInfoCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAccessCapability(
            instance = this,
            propId = PleosPropertyIds.INFO_VIN,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_INFO)
    override fun getVINInfo(onComplete: (String?) -> Unit, onFailed: (Exception) -> Unit) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.INFO_VIN,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(it) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_INFO)
    override fun getCRNInfo(onComplete: (String?) -> Unit, onFailed: (Exception) -> Unit) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.INFO_CRN,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(it) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.PRIVATE_CAR_INFO)
    override fun getCapabilityInfo(
        onComplete: (Map<String, ai.pleos.playground.vehicle.data.CapabilityConfig<Any>>?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestCapability(
            instance = this,
            action = onComplete,
            onFailed = onFailed
        )
    }
}
