package ai.pleos.playground.vehicle.api.implement

import ai.pleos.playground.vehicle.api.Wiper
import ai.pleos.playground.vehicle.api.model.area.WindowArea
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.PleosPropertyIds
import ai.pleos.playground.vehicle.constant.VehicleAreaCustomType
import ai.pleos.playground.vehicle.constant.control.WiperState
import ai.pleos.playground.vehicle.constant.control.WiperSwitch
import ai.pleos.playground.vehicle.listener.WiperStateListener
import ai.pleos.playground.vehicle.permission.RequiresCustomPermission
import ai.pleos.playground.vehicle.service.VehicleServiceManager
import ai.pleos.playground.vehicle.utils.ImplementUtil
import android.annotation.SuppressLint

@SuppressLint("MissingPermission")
@Suppress("OVERRIDE_DEPRECATION")
internal class WiperImpl(private val serviceManager: VehicleServiceManager) : Wiper {

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun checkWiperStateCapabilities(
        onComplete: (Map<WindowArea, List<WiperState>>) -> Unit, onFailed: (Exception) -> Unit
    ) {
        checkWiperStateCapabilities(
            onComplete = { _, map -> onComplete(map) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun checkWiperStateCapabilities(
        onComplete: (CapabilityConfig, Map<WindowArea, List<WiperState>>) -> Unit, onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaSupportedListCapability<WindowArea, WiperState>(
            instance = this,
            propId = PleosPropertyIds.WINDSHIELD_WIPERS_STATE,
            idResolver = { id -> WiperState.findById(id.toInt()) },
            onComplete = onComplete,
            onFailed = onFailed
        )
    }


    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun checkWiperSwitchCapabilities(
        onComplete: (Map<WindowArea, List<WiperSwitch>>) -> Unit, onFailed: (Exception) -> Unit
    ) {
        checkWiperSwitchCapabilities(
            onComplete = { _, map -> onComplete(map) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun checkWiperSwitchCapabilities(
        onComplete: (CapabilityConfig, Map<WindowArea, List<WiperSwitch>>) -> Unit, onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaSupportedListCapability<WindowArea, WiperSwitch>(
            instance = this,
            propId = PleosPropertyIds.WINDSHIELD_WIPERS_SWITCH,
            idResolver = { id -> WiperSwitch.findById(id.toInt()) },
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun getCurrentWiperState(
        area: WindowArea, onComplete: (WiperState?) -> Unit, onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.WINDSHIELD_WIPERS_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WINDOW,
            areaId = areaId,
            action = { onComplete(WiperState.findById(it?.toInt())) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun getCurrentWiperSwitch(
        area: WindowArea, onComplete: (WiperSwitch?) -> Unit, onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.WINDSHIELD_WIPERS_SWITCH,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WINDOW,
            areaId = areaId,
            action = { onComplete(WiperSwitch.findById(it?.toInt())) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_WINDOWS)
    override fun setWiperSwitch(
        area: WindowArea,
        value: WiperSwitch,
        onComplete: (WindowArea, WiperSwitch?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.WINDSHIELD_WIPERS_SWITCH,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WINDOW,
            areaId = areaId,
            value = value.id,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }


    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun registerWiperState(area: WindowArea, listener: WiperStateListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.WINDSHIELD_WIPERS_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WINDOW,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun unregisterWiperState(area: WindowArea, listener: WiperStateListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.WINDSHIELD_WIPERS_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WINDOW,
            areaId = areaId,
            listener = listener
        )
    }
}
