package ai.pleos.playground.vehicle.api.implement

import ai.pleos.playground.vehicle.api.SideMirror
import ai.pleos.playground.vehicle.api.model.area.MirrorArea
import ai.pleos.playground.vehicle.api.model.capability.AreaBounds
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.PleosPropertyIds
import ai.pleos.playground.vehicle.constant.VehicleAreaCustomType
import ai.pleos.playground.vehicle.listener.HvacSideMirrorHeatStateListener
import ai.pleos.playground.vehicle.permission.RequiresCustomPermission
import ai.pleos.playground.vehicle.service.VehicleServiceManager
import ai.pleos.playground.vehicle.utils.ImplementUtil
import android.annotation.SuppressLint

@SuppressLint("MissingPermission")
@Suppress("OVERRIDE_DEPRECATION")
internal class SideMirrorImpl(private val serviceManager: VehicleServiceManager) : SideMirror {

    @RequiresCustomPermission(PleosPermission.CAR_MIRRORS)
    override fun checkHvacSideMirrorHeatCapabilities(
        onComplete: (Map<MirrorArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkHvacSideMirrorHeatCapabilities(
            onComplete = { _, map -> onComplete(map) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_MIRRORS)
    override fun checkHvacSideMirrorHeatCapabilities(
        onComplete: (CapabilityConfig, Map<MirrorArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaBoundsCapability(
            instance = this,
            propId = PleosPropertyIds.HVAC_SIDE_MIRROR_HEAT,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_MIRRORS)
    override fun getHvacSideMirrorHeatState(
        area: MirrorArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.HVAC_SIDE_MIRROR_HEAT,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_MIRROR,
            areaId = areaId,
            action = { onComplete(it?.toInt()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_MIRRORS)
    override fun setHvacSideMirrorHeat(
        area: MirrorArea,
        value: Int,
        onComplete: (MirrorArea, Int?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.HVAC_SIDE_MIRROR_HEAT,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_MIRROR,
            areaId = areaId,
            value = value,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }


    @RequiresCustomPermission(PleosPermission.CAR_MIRRORS)
    override fun registerHvacSideMirrorHeatState(
        area: MirrorArea,
        listener: HvacSideMirrorHeatStateListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_SIDE_MIRROR_HEAT,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_MIRROR,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_MIRRORS)
    override fun unregisterHvacSideMirrorHeatState(
        area: MirrorArea,
        listener: HvacSideMirrorHeatStateListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_SIDE_MIRROR_HEAT,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_MIRROR,
            areaId = areaId,
            listener = listener
        )
    }
}
