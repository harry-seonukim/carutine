package ai.pleos.playground.vehicle.api.implement

import ai.pleos.playground.vehicle.api.Tire
import ai.pleos.playground.vehicle.api.model.area.WheelArea
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.PleosPropertyIds
import ai.pleos.playground.vehicle.constant.VehicleAreaCustomType
import ai.pleos.playground.vehicle.listener.TirePressureListener
import ai.pleos.playground.vehicle.permission.RequiresCustomPermission
import ai.pleos.playground.vehicle.service.VehicleServiceManager
import ai.pleos.playground.vehicle.utils.ImplementUtil
import android.annotation.SuppressLint

@SuppressLint("MissingPermission")
@Suppress("OVERRIDE_DEPRECATION")
internal class TireImpl(private val serviceManager: VehicleServiceManager) : Tire {

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun checkWheelAreaCapabilities(
        onComplete: (List<WheelArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkLowTirePressureCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun checkLowTirePressureCapabilities(
        onComplete: (CapabilityConfig, List<WheelArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(
            instance = this,
            propId = PleosPropertyIds.CRITICALLY_LOW_TIRE_PRESSURE,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun getLowTirePressure(area: WheelArea, onComplete: (Float?) -> Unit, onFailed: (Exception) -> Unit) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.CRITICALLY_LOW_TIRE_PRESSURE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WHEEL,
            areaId = areaId,
            action = { onComplete(it?.toFloat()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.PRIVATE_CAR_SAFETY)
    override fun checkTirePressureCapabilities(
        onComplete: (CapabilityConfig, List<WheelArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(
            instance = this,
            propId = PleosPropertyIds.TIRE_PRESSURE,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.PRIVATE_CAR_SAFETY)
    override fun getTirePressure(area: WheelArea, onComplete: (Float?) -> Unit, onFailed: (Exception) -> Unit) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.TIRE_PRESSURE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WHEEL,
            areaId = areaId,
            action = { onComplete(it?.toFloat()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.PRIVATE_CAR_SAFETY)
    override fun registerTirePressure(area: WheelArea, listener: TirePressureListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.TIRE_PRESSURE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WHEEL,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.PRIVATE_CAR_SAFETY)
    override fun unregisterTirePressure(area: WheelArea, listener: TirePressureListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.TIRE_PRESSURE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WHEEL,
            areaId = areaId,
            listener = listener
        )
    }
}
