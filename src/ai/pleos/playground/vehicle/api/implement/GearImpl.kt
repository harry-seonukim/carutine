package ai.pleos.playground.vehicle.api.implement

import ai.pleos.playground.vehicle.api.Gear
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.PleosPropertyIds
import ai.pleos.playground.vehicle.constant.VehicleAreaCustomType
import ai.pleos.playground.vehicle.constant.VehicleAreaValue
import ai.pleos.playground.vehicle.constant.control.VehicleGear
import ai.pleos.playground.vehicle.listener.GearSelectionListener
import ai.pleos.playground.vehicle.permission.RequiresCustomPermission
import ai.pleos.playground.vehicle.service.VehicleServiceManager

internal class GearImpl(private val serviceManager: VehicleServiceManager) : Gear {


    @Deprecated(
        "This API is deprecated, use the Google GEAR_SELECTION property.",
        level = DeprecationLevel.WARNING
    )
    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun getCurrentGearSelectionState(
        onComplete: (VehicleGear?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.VENDOR_GEAR_SELECTION,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(VehicleGear.findById(it?.toInt())) },
            onFailed = onFailed
        )
    }

    @Deprecated(
        "This API is deprecated, use the Google GEAR_SELECTION property.",
        level = DeprecationLevel.WARNING
    )
    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun registerGearSelection(listener: GearSelectionListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.VENDOR_GEAR_SELECTION,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @Deprecated(
        "This API is deprecated, use the Google GEAR_SELECTION property.",
        level = DeprecationLevel.WARNING
    )
    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun unregisterGearSelection(listener: GearSelectionListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.VENDOR_GEAR_SELECTION,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }
}