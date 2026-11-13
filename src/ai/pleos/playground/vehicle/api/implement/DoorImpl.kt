package ai.pleos.playground.vehicle.api.implement

import ai.pleos.playground.vehicle.api.Door
import ai.pleos.playground.vehicle.api.model.area.DoorArea
import ai.pleos.playground.vehicle.api.model.area.SeatArea
import ai.pleos.playground.vehicle.api.model.capability.AreaBounds
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.PleosPropertyIds
import ai.pleos.playground.vehicle.constant.VehicleAreaCustomType
import ai.pleos.playground.vehicle.constant.area.SeatValue
import ai.pleos.playground.vehicle.listener.DoorLockStateListener
import ai.pleos.playground.vehicle.listener.DoorPositionListener
import ai.pleos.playground.vehicle.listener.GloveBoxLockStateListener
import ai.pleos.playground.vehicle.permission.RequiresCustomPermission
import ai.pleos.playground.vehicle.service.VehicleServiceManager
import ai.pleos.playground.vehicle.utils.CapabilityUtil
import ai.pleos.playground.vehicle.utils.ImplementUtil
import android.annotation.SuppressLint

@SuppressLint("MissingPermission")
@Suppress("OVERRIDE_DEPRECATION")
internal class DoorImpl(private val serviceManager: VehicleServiceManager) : Door {

    @RequiresCustomPermission(PleosPermission.CAR_DOORS)
    override fun checkGloveBoxLockCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAccessCapability(
            instance = this,
            propId = PleosPropertyIds.GLOVE_BOX_LOCKED,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DOORS)
    override fun checkDoorLockCapabilities(
        onComplete: (List<DoorArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkDoorLockCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DOORS)
    override fun checkDoorLockCapabilities(
        onComplete: (CapabilityConfig, List<DoorArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(
            instance = this,
            propId = PleosPropertyIds.DOOR_LOCK,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DOORS)
    override fun checkDoorPositionCapabilities(
        onComplete: (Map<DoorArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkDoorPositionCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DOORS)
    override fun checkDoorPositionCapabilities(
        onComplete: (CapabilityConfig, Map<DoorArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaBoundsCapability(
            instance = this,
            propId = PleosPropertyIds.DOOR_POS,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DOORS)
    override fun getDoorLockState(
        area: DoorArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.DOOR_LOCK,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_DOOR,
            areaId = areaId,
            action = { onComplete(it?.toBoolean()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_DOORS)
    override fun setGloveBoxLock(
        value: Boolean,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val propId = PleosPropertyIds.GLOVE_BOX_LOCKED
        serviceManager.requestCapability(instance = this, propId, { ret ->
            val areas = CapabilityUtil.getAreaIds<DoorArea>(ret)

            val area = areas.getOrNull(0) ?: SeatArea(SeatValue.ROW_1_RIGHT)
            val areaId = ImplementUtil.getAreaId(area)
            serviceManager.setPropertyAsync(
                propId = propId,
                areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
                areaId = areaId,
                value = value,
                onComplete = { onComplete(value) },
                onFailed = onFailed
            )
        }, onFailed)
    }


    @RequiresCustomPermission(PleosPermission.CAR_DOORS)
    override fun getCurrentGloveBoxLockState(
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val propId = PleosPropertyIds.GLOVE_BOX_LOCKED
        serviceManager.requestCapability(instance = this, propId, { ret ->
            val areas = CapabilityUtil.getAreaIds<SeatArea>(ret)
            val area = areas.getOrNull(0) ?: SeatArea(SeatValue.ROW_1_RIGHT)
            val areaId = ImplementUtil.getAreaId(area)
            serviceManager.getProperty(
                propId = propId,
                areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
                areaId = areaId,
                action = { onComplete(it?.toBoolean()) },
                onFailed = onFailed
            )
        }, onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_DOORS)
    override fun getCurrentDoorPosition(
        area: DoorArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.DOOR_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_DOOR,
            areaId = areaId,
            action = { onComplete(it?.toInt()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_DOORS)
    override fun setDoorLock(
        area: DoorArea,
        value: Boolean,
        onComplete: (DoorArea, Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.DOOR_LOCK,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_DOOR,
            areaId = areaId,
            value = value,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_DOORS)
    override fun setDoorPosition(
        area: DoorArea,
        value: Int,
        onComplete: (DoorArea, Int?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.DOOR_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_DOOR,
            areaId = areaId,
            value = value,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DOORS)
    override fun registerDoorPosition(area: DoorArea, listener: DoorPositionListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.DOOR_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_DOOR,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DOORS)
    override fun unregisterDoorPosition(area: DoorArea, listener: DoorPositionListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.DOOR_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_DOOR,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DOORS)
    override fun registerDoorLockState(area: DoorArea, listener: DoorLockStateListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.DOOR_LOCK,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_DOOR,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DOORS)
    override fun unregisterDoorLockState(area: DoorArea, listener: DoorLockStateListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.DOOR_LOCK,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_DOOR,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DOORS)
    override fun registerGloveBoxLockState(listener: GloveBoxLockStateListener) {
        val propId = PleosPropertyIds.GLOVE_BOX_LOCKED
        serviceManager.requestCapability(instance = this, propId, { ret ->
            val areas = CapabilityUtil.getAreaIds<DoorArea>(ret)
            val area = areas.getOrNull(0) ?: SeatArea(SeatValue.ROW_1_RIGHT)
            val areaId = ImplementUtil.getAreaId(area)
            serviceManager.addListener(
                propId = propId,
                areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
                areaId = areaId,
                listener = listener
            )
        }, { e -> listener.onFailed(e) })

    }

    @RequiresCustomPermission(PleosPermission.CAR_DOORS)
    override fun unregisterGloveBoxLockState(listener: GloveBoxLockStateListener) {
        val propId = PleosPropertyIds.GLOVE_BOX_LOCKED
        serviceManager.requestCapability(instance = this, propId, { ret ->
            val areas = CapabilityUtil.getAreaIds<DoorArea>(ret)
            val area = areas.getOrNull(0) ?: SeatArea(SeatValue.ROW_1_RIGHT)
            val areaId = ImplementUtil.getAreaId(area)
            serviceManager.removeListener(
                propId = propId,
                areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
                areaId = areaId,
                listener = listener
            )
        }, { e -> listener.onFailed(e) })
    }
}
