package ai.pleos.playground.vehicle.api.implement

import ai.pleos.playground.vehicle.api.DrivingMode
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.PleosPropertyIds
import ai.pleos.playground.vehicle.constant.VehicleAreaCustomType
import ai.pleos.playground.vehicle.constant.VehicleAreaValue
import ai.pleos.playground.vehicle.constant.control.CruiseControl
import ai.pleos.playground.vehicle.constant.control.DrivingModeType
import ai.pleos.playground.vehicle.listener.CruiseControlStateListener
import ai.pleos.playground.vehicle.listener.CruiseControlTargetSpeedListener
import ai.pleos.playground.vehicle.listener.DrivingModeStateListener
import ai.pleos.playground.vehicle.permission.RequiresCustomPermission
import ai.pleos.playground.vehicle.service.VehicleServiceManager

internal class DrivingModeImpl(private val serviceManager: VehicleServiceManager) : DrivingMode {


    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun checkCruiseControlTargetSpeedCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAccessCapability(
            instance = this,
            propId = PleosPropertyIds.CRUISE_CONTROL_TARGET_SPEED,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun checkCruiseControlStateCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAccessCapability(
            instance = this,
            propId = PleosPropertyIds.CRUISE_CONTROL_STATE,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun checkDrivingModeStateCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAccessCapability(
            instance = this,
            propId = PleosPropertyIds.DRIVING_MODE,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun getCruiseControlTargetSpeed(
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.CRUISE_CONTROL_TARGET_SPEED,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(it?.toFloat()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun getCruiseControlState(
        onComplete: (CruiseControl?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.CRUISE_CONTROL_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(CruiseControl.findById(it?.toInt())) },
            onFailed = onFailed
        )
    }


    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun getCurrentDrivingModeState(
        onComplete: (DrivingModeType?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.DRIVING_MODE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(DrivingModeType.findById(it?.toInt())) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun registerCruiseControlTargetSpeed(listener: CruiseControlTargetSpeedListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.CRUISE_CONTROL_TARGET_SPEED,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun unregisterCruiseControlTargetSpeed(listener: CruiseControlTargetSpeedListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.CRUISE_CONTROL_TARGET_SPEED,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun registerCruiseControlState(listener: CruiseControlStateListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.CRUISE_CONTROL_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun unregisterCruiseControlState(listener: CruiseControlStateListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.CRUISE_CONTROL_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun registerDrivingModeState(listener: DrivingModeStateListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.DRIVING_MODE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun unregisterDrivingModeState(listener: DrivingModeStateListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.DRIVING_MODE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }
}
