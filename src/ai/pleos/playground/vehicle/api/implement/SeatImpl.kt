package ai.pleos.playground.vehicle.api.implement

import ai.pleos.playground.vehicle.api.Seat
import ai.pleos.playground.vehicle.api.model.area.SeatArea
import ai.pleos.playground.vehicle.api.model.capability.AreaBounds
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.common.catching
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.PleosPropertyIds
import ai.pleos.playground.vehicle.constant.VehicleAreaCustomType
import ai.pleos.playground.vehicle.constant.control.MoveControl
import ai.pleos.playground.vehicle.listener.HvacSeatTemperatureListener
import ai.pleos.playground.vehicle.listener.HvacSeatVentilationListener
import ai.pleos.playground.vehicle.listener.SeatBackrestAnglePositionListener
import ai.pleos.playground.vehicle.listener.SeatBeltBuckledStateListener
import ai.pleos.playground.vehicle.listener.SeatBeltHeightPositionListener
import ai.pleos.playground.vehicle.listener.SeatDepthPositionListener
import ai.pleos.playground.vehicle.listener.SeatForeAftPositionListener
import ai.pleos.playground.vehicle.listener.SeatHeadrestAnglePositionListener
import ai.pleos.playground.vehicle.listener.SeatHeadrestForeAftPositionListener
import ai.pleos.playground.vehicle.listener.SeatHeadrestHeightAftPositionListener
import ai.pleos.playground.vehicle.listener.SeatHeightPositionListener
import ai.pleos.playground.vehicle.listener.SeatLumbarForeAftPositionListener
import ai.pleos.playground.vehicle.listener.SeatLumbarSideSupportPositionListener
import ai.pleos.playground.vehicle.listener.SeatOccupancyListener
import ai.pleos.playground.vehicle.listener.SeatTiltPositionListener
import ai.pleos.playground.vehicle.permission.RequiresCustomPermission
import ai.pleos.playground.vehicle.service.VehicleServiceManager
import ai.pleos.playground.vehicle.utils.ImplementUtil
import android.annotation.SuppressLint

@SuppressLint("MissingPermission")
@Suppress("OVERRIDE_DEPRECATION")
internal class SeatImpl(private val serviceManager: VehicleServiceManager) : Seat {


    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatOccupancyCapabilities(
        onComplete: (List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkSeatOccupancyCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatOccupancyCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(instance = this, propId = PleosPropertyIds.SEAT_OCCUPANCY, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatBeltBuckledCapabilities(
        onComplete: (List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkSeatBeltBuckledCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatBeltBuckledCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(instance = this, propId = PleosPropertyIds.SEAT_BELT_BUCKLED, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatBackrestAngleCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkSeatBackrestAnglePositionCapabilities(
            onComplete = { _, map -> onComplete(map) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatBackrestAnglePositionCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaBoundsCapability(instance = this, propId = PleosPropertyIds.SEAT_BACKREST_ANGLE_1_POS, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatHeadrestAngleCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkSeatHeadrestAnglePositionCapabilities(
            onComplete = { _, map -> onComplete(map) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatHeadrestAnglePositionCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaBoundsCapability(instance = this, propId = PleosPropertyIds.SEAT_HEADREST_ANGLE_POS, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatHeadrestForeAftCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkSeatHeadrestForeAftPositionCapabilities(
            onComplete = { _, map -> onComplete(map) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatHeadrestForeAftPositionCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaBoundsCapability(instance = this, propId = PleosPropertyIds.SEAT_HEADREST_FORE_AFT_POS, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatHeadrestHeightCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkSeatHeadrestHeightPositionCapabilities(
            onComplete = { _, map -> onComplete(map) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatHeadrestHeightPositionCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaBoundsCapability(instance = this, propId = PleosPropertyIds.SEAT_HEADREST_HEIGHT_POS_V2, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatHeightCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkSeatHeightPositionCapabilities(
            onComplete = { _, map -> onComplete(map) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatHeightPositionCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaBoundsCapability(instance = this, propId = PleosPropertyIds.SEAT_HEIGHT_POS, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatLumbarForeAftCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkSeatLumbarForeAftPositionCapabilities(
            onComplete = { _, map -> onComplete(map) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatLumbarForeAftPositionCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaBoundsCapability(instance = this, propId = PleosPropertyIds.SEAT_LUMBAR_FORE_AFT_POS, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatLumbarSideSupportCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkSeatLumbarSideSupportPositionCapabilities(
            onComplete = { _, map -> onComplete(map) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatLumbarSideSupportPositionCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaBoundsCapability(instance = this, propId = PleosPropertyIds.SEAT_LUMBAR_SIDE_SUPPORT_POS, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatTiltCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkSeatTiltPositionCapabilities(
            onComplete = { _, map -> onComplete(map) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatTiltPositionCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaBoundsCapability(instance = this, propId = PleosPropertyIds.SEAT_TILT_POS, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatDepthCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkSeatDepthPositionCapabilities(
            onComplete = { _, map -> onComplete(map) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatDepthPositionCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaBoundsCapability(instance = this, propId = PleosPropertyIds.SEAT_DEPTH_POS, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatForeAftCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkSeatForeAftPositionCapabilities(
            onComplete = { _, map -> onComplete(map) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatForeAftPositionCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaBoundsCapability(instance = this, propId = PleosPropertyIds.SEAT_FORE_AFT_POS, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatBeltHeightCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkSeatBeltHeightPositionCapabilities(
            onComplete = { _, map -> onComplete(map) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatBeltHeightPositionCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaBoundsCapability(instance = this, propId = PleosPropertyIds.SEAT_BELT_HEIGHT_POS, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatTemperatureCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkSeatTemperatureCapabilities(
            onComplete = { _, map -> onComplete(map) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatTemperatureCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaBoundsCapability(instance = this, propId = PleosPropertyIds.HVAC_SEAT_TEMPERATURE, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatVentilationCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkSeatVentilationCapabilities(
            onComplete = { _, map -> onComplete(map) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatVentilationCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaBoundsCapability(instance = this, propId = PleosPropertyIds.HVAC_SEAT_VENTILATION, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkMoveSeatBackrestAngleCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(instance = this, propId = PleosPropertyIds.SEAT_BACKREST_ANGLE_1_MOVE, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkMoveSeatHeadrestForeAftCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(instance = this, propId = PleosPropertyIds.SEAT_HEADREST_FORE_AFT_MOVE, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkMoveSeatBeltHeightCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(instance = this, propId = PleosPropertyIds.SEAT_BELT_HEIGHT_MOVE, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkMoveSeatHeadrestHeightCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(instance = this, propId = PleosPropertyIds.SEAT_HEADREST_HEIGHT_MOVE, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkMoveSeatHeightCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(instance = this, propId = PleosPropertyIds.SEAT_HEIGHT_MOVE, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkMoveSeatLumbarForeAftCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(instance = this, propId = PleosPropertyIds.SEAT_LUMBAR_FORE_AFT_MOVE, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkMoveSeatLumbarSideSupportCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(instance = this, propId = PleosPropertyIds.SEAT_LUMBAR_SIDE_SUPPORT_MOVE, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkMoveSeatTiltCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(instance = this, propId = PleosPropertyIds.SEAT_TILT_MOVE, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkMoveSeatDepthCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(instance = this, propId = PleosPropertyIds.SEAT_DEPTH_MOVE, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkMoveSeatHeadrestAngleCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(instance = this, propId = PleosPropertyIds.SEAT_HEADREST_ANGLE_MOVE, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkMoveSeatForeAftCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(instance = this, propId = PleosPropertyIds.SEAT_FORE_AFT_MOVE, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun checkSeatMemoryCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaBoundsCapability(
            instance = this,
            propId = PleosPropertyIds.SEAT_MEMORY_SELECT,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun getCurrentSeatOccupancy(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.SEAT_OCCUPANCY,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toIntOrNull() == 1) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_SEATS)
    override fun moveSeatBackrestAngle(
        area: SeatArea,
        value: MoveControl,
        onComplete: (SeatArea, MoveControl?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.SEAT_BACKREST_ANGLE_1_MOVE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            value = value.id,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun getCurrentSeatBackrestAnglePosition(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.SEAT_BACKREST_ANGLE_1_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toInt()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun getCurrentSeatBeltBuckledState(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.SEAT_BELT_BUCKLED,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toBoolean()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_SEATS)
    override fun moveSeatBeltHeight(
        area: SeatArea,
        value: MoveControl,
        onComplete: (SeatArea, MoveControl?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.SEAT_BELT_HEIGHT_MOVE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            value = value.id,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }

    @Deprecated(
        "This API is deprecated, use moveSeatBeltHeight instead.",
        replaceWith = ReplaceWith(
            "moveSeatBeltHeight(area, value, onComplete, onFailed)",
            "ai.pleos.playground.vehicle.api.model.area.SeatArea",
            "ai.pleos.playground.vehicle.constant.control.MoveControl"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun getCurrentSeatBeltHeightPosition(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.SEAT_BELT_HEIGHT_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toInt()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_SEATS)
    override fun moveSeatDepth(
        area: SeatArea,
        value: MoveControl,
        onComplete: (SeatArea, MoveControl?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.SEAT_DEPTH_MOVE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            value = value.id,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun getCurrentSeatDepthPosition(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.SEAT_DEPTH_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toInt()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_SEATS)
    override fun moveSeatForeAft(
        area: SeatArea,
        value: MoveControl,
        onComplete: (SeatArea, MoveControl?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.SEAT_FORE_AFT_MOVE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            value = value.id,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }


    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun getCurrentSeatForeAftPosition(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.SEAT_FORE_AFT_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toInt()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_SEATS)
    override fun moveSeatHeadrestAngle(
        area: SeatArea,
        value: MoveControl,
        onComplete: (SeatArea, MoveControl?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.SEAT_HEADREST_ANGLE_MOVE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            value = value.id,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun getCurrentSeatHeadrestAnglePosition(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.SEAT_HEADREST_ANGLE_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toInt()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_SEATS)
    override fun moveSeatHeadrestForeAft(
        area: SeatArea,
        value: MoveControl,
        onComplete: (SeatArea, MoveControl?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.SEAT_HEADREST_FORE_AFT_MOVE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            value = value.id,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun getCurrentSeatHeadrestForeAftPosition(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.SEAT_HEADREST_FORE_AFT_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toInt()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_SEATS)
    override fun moveSeatHeadrestHeight(
        area: SeatArea,
        value: MoveControl,
        onComplete: (SeatArea, MoveControl?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.SEAT_HEADREST_HEIGHT_MOVE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            value = value.id,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }


    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun getCurrentSeatHeadrestHeightAftPosition(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.SEAT_HEADREST_HEIGHT_POS_V2,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toInt()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_SEATS)
    override fun moveSeatHeight(
        area: SeatArea,
        value: MoveControl,
        onComplete: (SeatArea, MoveControl?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.SEAT_HEIGHT_MOVE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            value = value.id,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }


    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun getCurrentSeatHeightPosition(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.SEAT_HEIGHT_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toInt()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_SEATS)
    override fun moveSeatLumbarForeAft(
        area: SeatArea,
        value: MoveControl,
        onComplete: (SeatArea, MoveControl?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.SEAT_LUMBAR_FORE_AFT_MOVE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            value = value.id,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }


    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun getCurrentSeatLumbarForeAftPosition(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.SEAT_LUMBAR_FORE_AFT_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toInt()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_SEATS)
    override fun moveSeatLumbarSideSupport(
        area: SeatArea,
        value: MoveControl,
        onComplete: (SeatArea, MoveControl?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.SEAT_LUMBAR_SIDE_SUPPORT_MOVE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            value = value.id,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }


    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun getCurrentSeatLumbarSideSupportPosition(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.SEAT_LUMBAR_SIDE_SUPPORT_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toInt()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_SEATS)
    override fun selectSeatMemory(
        area: SeatArea,
        value: Int,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setProperty(
            instance = this,
            propId = PleosPropertyIds.SEAT_MEMORY_SELECT,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            value = value,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_SEATS)
    override fun moveSeatTilt(
        area: SeatArea,
        value: MoveControl,
        onComplete: (SeatArea, MoveControl?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.SEAT_TILT_MOVE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            value = value.id,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun getCurrentSeatTiltPosition(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.SEAT_TILT_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toInt()) },
            onFailed = onFailed
        )
    }


    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_SEATS)
    override fun setHvacSeatTemperature(
        area: SeatArea,
        value: Int,
        onComplete: (SeatArea, Int?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        catching("setHvacSeatTemperature") {
            val areaId = ImplementUtil.getAreaId(area)
            serviceManager.setPropertyAsync(
                instance = this,
                propId = PleosPropertyIds.HVAC_SEAT_TEMPERATURE,
                areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
                areaId = areaId,
                value = value,
                onComplete = { onComplete(area, value) },
                onFailed = onFailed
            )
        }.onFailure { onFailed(Exception(it)) }
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_SEATS)
    override fun setHvacSeatVentilation(
        area: SeatArea,
        value: Int,
        onComplete: (SeatArea, Int?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        catching("setHvacSeatVentilation") {
            val areaId = ImplementUtil.getAreaId(area)
            serviceManager.setPropertyAsync(
                instance = this,
                propId = PleosPropertyIds.HVAC_SEAT_VENTILATION,
                areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
                areaId = areaId,
                value = value,
                onComplete = { onComplete(area, value) },
                onFailed = onFailed
            )
        }.onFailure { onFailed(Exception(it)) }
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun getHvacSeatTemperature(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.HVAC_SEAT_TEMPERATURE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toInt()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun getHvacSeatVentilationState(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.HVAC_SEAT_VENTILATION,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toInt()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun registerSeatOccupancy(area: SeatArea, listener: SeatOccupancyListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_OCCUPANCY,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun unregisterSeatOccupancy(area: SeatArea, listener: SeatOccupancyListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_OCCUPANCY,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun registerSeatBackrestAnglePosition(
        area: SeatArea,
        listener: SeatBackrestAnglePositionListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_BACKREST_ANGLE_1_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun unregisterSeatBackrestAnglePosition(
        area: SeatArea,
        listener: SeatBackrestAnglePositionListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_BACKREST_ANGLE_1_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun registerSeatBeltBuckledState(
        area: SeatArea,
        listener: SeatBeltBuckledStateListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_BELT_BUCKLED,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun unregisterSeatBeltBuckledState(
        area: SeatArea,
        listener: SeatBeltBuckledStateListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_BELT_BUCKLED,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun registerSeatBeltHeightPosition(
        area: SeatArea,
        listener: SeatBeltHeightPositionListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_BELT_HEIGHT_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun unregisterSeatBeltHeightPosition(
        area: SeatArea,
        listener: SeatBeltHeightPositionListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_BELT_HEIGHT_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun registerSeatDepthPosition(area: SeatArea, listener: SeatDepthPositionListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_DEPTH_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun unregisterSeatDepthPosition(area: SeatArea, listener: SeatDepthPositionListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_DEPTH_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun registerSeatForeAftPosition(
        area: SeatArea,
        listener: SeatForeAftPositionListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_FORE_AFT_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun unregisterSeatForeAftPosition(
        area: SeatArea,
        listener: SeatForeAftPositionListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_FORE_AFT_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun registerSeatHeadrestAnglePosition(
        area: SeatArea,
        listener: SeatHeadrestAnglePositionListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_HEADREST_ANGLE_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun unregisterSeatHeadrestAnglePosition(
        area: SeatArea,
        listener: SeatHeadrestAnglePositionListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_HEADREST_ANGLE_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun registerSeatHeadrestForeAftPosition(
        area: SeatArea,
        listener: SeatHeadrestForeAftPositionListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_HEADREST_FORE_AFT_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun unregisterSeatHeadrestForeAftPosition(
        area: SeatArea,
        listener: SeatHeadrestForeAftPositionListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_HEADREST_FORE_AFT_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun registerSeatHeadrestHeightAftPosition(
        area: SeatArea,
        listener: SeatHeadrestHeightAftPositionListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_HEADREST_HEIGHT_POS_V2,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun unregisterSeatHeadrestHeightAftPosition(
        area: SeatArea,
        listener: SeatHeadrestHeightAftPositionListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_HEADREST_HEIGHT_POS_V2,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun registerSeatHeightPosition(area: SeatArea, listener: SeatHeightPositionListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_HEIGHT_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun unregisterSeatHeightPosition(
        area: SeatArea,
        listener: SeatHeightPositionListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_HEIGHT_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun registerSeatLumbarForeAftPosition(
        area: SeatArea,
        listener: SeatLumbarForeAftPositionListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_LUMBAR_FORE_AFT_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun unregisterSeatLumbarForeAftPosition(
        area: SeatArea,
        listener: SeatLumbarForeAftPositionListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_LUMBAR_FORE_AFT_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun registerSeatLumbarSideSupportPosition(
        area: SeatArea,
        listener: SeatLumbarSideSupportPositionListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_LUMBAR_SIDE_SUPPORT_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun unregisterSeatLumbarSideSupportPosition(
        area: SeatArea,
        listener: SeatLumbarSideSupportPositionListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_LUMBAR_SIDE_SUPPORT_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun registerSeatTiltPosition(area: SeatArea, listener: SeatTiltPositionListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_TILT_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun unregisterSeatTiltPosition(area: SeatArea, listener: SeatTiltPositionListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_TILT_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun registerHvacSeatTemperature(
        area: SeatArea,
        listener: HvacSeatTemperatureListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_SEAT_TEMPERATURE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun unregisterHvacSeatTemperature(
        area: SeatArea,
        listener: HvacSeatTemperatureListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_SEAT_TEMPERATURE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun registerHvacSeatVentilation(
        area: SeatArea,
        listener: HvacSeatVentilationListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_SEAT_VENTILATION,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SEATS)
    override fun unregisterHvacSeatVentilation(
        area: SeatArea,
        listener: HvacSeatVentilationListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_SEAT_VENTILATION,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }
}
