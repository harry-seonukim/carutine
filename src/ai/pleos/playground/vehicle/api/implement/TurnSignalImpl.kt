package ai.pleos.playground.vehicle.api.implement

import ai.pleos.playground.vehicle.api.TurnSignal
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.PleosPropertyIds
import ai.pleos.playground.vehicle.constant.VehicleAreaCustomType
import ai.pleos.playground.vehicle.constant.VehicleAreaValue
import ai.pleos.playground.vehicle.constant.area.TurnSignalValue
import ai.pleos.playground.vehicle.listener.TurnSignalStateListener
import ai.pleos.playground.vehicle.permission.RequiresCustomPermission
import ai.pleos.playground.vehicle.service.VehicleServiceManager

internal class TurnSignalImpl(private val serviceManager: VehicleServiceManager) : TurnSignal {

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun checkTurnSignalStateCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAccessCapability(
            instance = this,
            propId = PleosPropertyIds.TURN_SIGNAL_STATE,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun getCurrentTurnSignalState(onComplete: (TurnSignalValue?) -> Unit, onFailed: (Exception) -> Unit) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.TURN_SIGNAL_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(TurnSignalValue.findById(it?.toInt())) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun registerTurnSignalState(listener: TurnSignalStateListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.TURN_SIGNAL_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun unregisterTurnSignalState(listener: TurnSignalStateListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.TURN_SIGNAL_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }
}
