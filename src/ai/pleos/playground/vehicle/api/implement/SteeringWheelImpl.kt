package ai.pleos.playground.vehicle.api.implement

import ai.pleos.playground.vehicle.api.SteeringWheel
import ai.pleos.playground.vehicle.api.model.capability.AreaBounds
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.common.catching
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.PleosPropertyIds
import ai.pleos.playground.vehicle.constant.VehicleAreaCustomType
import ai.pleos.playground.vehicle.constant.VehicleAreaValue
import ai.pleos.playground.vehicle.listener.HvacSteeringWheelHeatStateListener
import ai.pleos.playground.vehicle.listener.SteeringWheelAngleListener
import ai.pleos.playground.vehicle.permission.RequiresCustomPermission
import ai.pleos.playground.vehicle.service.VehicleServiceManager

internal class SteeringWheelImpl(private val serviceManager: VehicleServiceManager) :
    SteeringWheel {

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacSteeringWheelHeatCapabilities(
        onComplete: (AreaBounds<Int>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkHvacSteeringWheelHeatCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacSteeringWheelHeatCapabilities(
        onComplete: (CapabilityConfig, AreaBounds<Int>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestGlobalBoundsCapability(
            instance = this,
            propId = PleosPropertyIds.HVAC_STEERING_WHEEL_HEAT,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun checkSteeringWheelAngleCapabilities(
        onComplete: (CapabilityConfig, AreaBounds<Float>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestGlobalBoundsCapability(
            instance = this,
            propId = PleosPropertyIds.PERF_STEERING_ANGLE,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun getHvacSteeringWheelHeatState(
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.HVAC_STEERING_WHEEL_HEAT,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(it?.toInt()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    override fun setHvacSteeringWheelHeat(
        value: Int,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        catching("setHvacSteeringWheelHeat") {
            serviceManager.setPropertyAsync(
                instance = this,
                propId = PleosPropertyIds.HVAC_STEERING_WHEEL_HEAT,
                areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
                areaId = VehicleAreaValue.DEFAULT,
                value = value,
                onComplete = { onComplete(value) },
                onFailed = onFailed
            )
        }.onFailure { onFailed(Exception(it)) }
    }


    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun getCurrentSteeringWheelAngle(
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.PERF_STEERING_ANGLE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(it?.toFloat()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun registerSteeringWheelAngle(listener: SteeringWheelAngleListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.PERF_STEERING_ANGLE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun unregisterSteeringWheelAngle(listener: SteeringWheelAngleListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.PERF_STEERING_ANGLE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun registerHvacSteeringWheelHeatState(listener: HvacSteeringWheelHeatStateListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_STEERING_WHEEL_HEAT,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun unregisterHvacSteeringWheelHeatState(listener: HvacSteeringWheelHeatStateListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_STEERING_WHEEL_HEAT,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }
}
