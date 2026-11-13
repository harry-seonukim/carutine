package ai.pleos.playground.vehicle.api.implement

import ai.pleos.playground.vehicle.api.Display
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.PleosPropertyIds
import ai.pleos.playground.vehicle.constant.VehicleAreaCustomType
import ai.pleos.playground.vehicle.constant.VehicleAreaValue
import ai.pleos.playground.vehicle.constant.unit.TemperatureUnit
import ai.pleos.playground.vehicle.listener.TemperatureDisplayUnitListener
import ai.pleos.playground.vehicle.permission.RequiresCustomPermission
import ai.pleos.playground.vehicle.service.VehicleServiceManager

internal class DisplayImpl(private val serviceManager: VehicleServiceManager) : Display {


    @RequiresCustomPermission(PleosPermission.CAR_INFO)
    override fun checkTemperatureDisplayUnitCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAccessCapability(
            instance = this,
            propId = PleosPropertyIds.HVAC_TEMPERATURE_DISPLAY_UNITS,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_INFO)
    override fun getCurrentTemperatureDisplayUnit(
        onComplete: (TemperatureUnit?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.HVAC_TEMPERATURE_DISPLAY_UNITS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(TemperatureUnit.findById(it?.toInt())) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_INFO)
    override fun registerTemperatureDisplayUnit(listener: TemperatureDisplayUnitListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_TEMPERATURE_DISPLAY_UNITS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_INFO)
    override fun unregisterTemperatureDisplayUnit(listener: TemperatureDisplayUnitListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_TEMPERATURE_DISPLAY_UNITS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }
}
