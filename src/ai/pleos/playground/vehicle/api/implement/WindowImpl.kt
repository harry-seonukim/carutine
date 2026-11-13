package ai.pleos.playground.vehicle.api.implement

import ai.pleos.playground.vehicle.api.Window
import ai.pleos.playground.vehicle.api.model.area.WindowArea
import ai.pleos.playground.vehicle.api.model.capability.AreaBounds
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.api.model.capability.SmartGlassConfig
import ai.pleos.playground.vehicle.common.fromJsonOrNull
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.PleosPropertyIds
import ai.pleos.playground.vehicle.constant.VehicleAreaCustomType
import ai.pleos.playground.vehicle.constant.VehicleAreaValue
import ai.pleos.playground.vehicle.constant.control.MoveControl
import ai.pleos.playground.vehicle.constant.control.SmartGlassZoneCommand
import ai.pleos.playground.vehicle.listener.SmartGlassLevelListener
import ai.pleos.playground.vehicle.listener.SmartGlassStateListener
import ai.pleos.playground.vehicle.listener.WindowLockStateListener
import ai.pleos.playground.vehicle.listener.WindowPositionListener
import ai.pleos.playground.vehicle.permission.RequiresCustomPermission
import ai.pleos.playground.vehicle.service.VehicleServiceManager
import ai.pleos.playground.vehicle.utils.ImplementUtil
import android.annotation.SuppressLint

@SuppressLint("MissingPermission")
@Suppress("OVERRIDE_DEPRECATION")
internal class WindowImpl(private val serviceManager: VehicleServiceManager) : Window {

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun checkMoveWindowBlindCapabilities(
        onComplete: (CapabilityConfig, List<WindowArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(
            instance = this,
            propId = PleosPropertyIds.WINDOW_BLIND_MOVE,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun checkWindowBlindPositionCapabilities(
        onComplete: (CapabilityConfig, Map<WindowArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaBoundsCapability(
            instance = this,
            propId = PleosPropertyIds.WINDOW_BLIND_POS,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun checkSmartGlassStateCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAccessCapability(
            instance = this,
            propId = PleosPropertyIds.SMART_GLASS_ON,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun checkSmartGlassZoneStateCapabilities(
        onComplete: (CapabilityConfig, List<WindowArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(
            instance = this,
            propId = PleosPropertyIds.SMART_GLASS_ZONE_ON,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun checkSmartGlassLevelCapabilities(
        onComplete: (CapabilityConfig, Map<WindowArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaBoundsCapability(
            instance = this,
            propId = PleosPropertyIds.SMART_GLASS_LEVEL,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun checkWindowLockCapabilities(
        onComplete: (List<WindowArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkWindowLockCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun checkWindowLockCapabilities(
        onComplete: (CapabilityConfig, List<WindowArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(
            instance = this,
            propId = PleosPropertyIds.WINDOW_LOCK,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun checkWindowPositionCapabilities(
        onComplete: (Map<WindowArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkWindowPositionCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun checkWindowPositionCapabilities(
        onComplete: (CapabilityConfig, Map<WindowArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaBoundsCapability(
            instance = this,
            propId = PleosPropertyIds.WINDOW_POS,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun getCurrentWindowBlindPosition(
        area: WindowArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.WINDOW_BLIND_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WINDOW,
            areaId = areaId,
            action = { onComplete(it?.toInt()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_WINDOWS)
    override fun moveWindowBlind(
        area: WindowArea,
        value: MoveControl,
        onComplete: (WindowArea, MoveControl?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.WINDOW_BLIND_MOVE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WINDOW,
            areaId = areaId,
            value = value.id,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun getCurrentSmartGlassState(
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.SMART_GLASS_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(it?.toBoolean()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_WINDOWS)
    override fun setSmartGlassState(
        value: Boolean,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.SMART_GLASS_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            value = value,
            onComplete = { onComplete(value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun getSmartGlassConfig(
        area: WindowArea,
        onComplete: (SmartGlassConfig?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        if (area.values.size != 1) {
            onFailed(IllegalArgumentException("Exactly one window area is required"))
            return
        }
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.SMART_GLASS_CONFIG,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WINDOW,
            areaId = areaId,
            action = {
                it.toSmartGlassConfigOrNull()?.let(onComplete)
                    ?: onFailed(Exception("Smart Glass configuration payload is empty or invalid."))
            },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_WINDOWS)
    override fun setSmartGlassZoneState(
        area: WindowArea,
        value: List<SmartGlassZoneCommand>,
        onComplete: (WindowArea, List<SmartGlassZoneCommand>?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        if (area.values.size != 1) {
            onFailed(IllegalArgumentException("Exactly one window area is required"))
            return
        }
        if (value.isEmpty()) {
            onFailed(IllegalArgumentException("Smart glass zone commands must not be empty"))
            return
        }
        val commands = value.toList()
        val payload = commands.map { it.id }.toIntArray()
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.SMART_GLASS_ZONE_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WINDOW,
            areaId = areaId,
            value = payload,
            onComplete = { onComplete(area, commands) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun getCurrentSmartGlassLevel(
        area: WindowArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.SMART_GLASS_LEVEL,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WINDOW,
            areaId = areaId,
            action = { onComplete(it?.toInt()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_WINDOWS)
    override fun setSmartGlassLevel(
        area: WindowArea,
        value: Int,
        onComplete: (WindowArea, Int?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.SMART_GLASS_LEVEL,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WINDOW,
            areaId = areaId,
            value = value,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_WINDOWS)
    override fun setWindowLock(
        area: WindowArea,
        value: Boolean,
        onComplete: (WindowArea, Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.WINDOW_LOCK,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WINDOW,
            areaId = areaId,
            value = value,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }


    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun getCurrentWindowLockState(
        area: WindowArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.WINDOW_LOCK,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WINDOW,
            areaId = areaId,
            action = { onComplete(it?.toBoolean()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun getCurrentWindowPosition(
        area: WindowArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.WINDOW_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WINDOW,
            areaId = areaId,
            action = { onComplete(it?.toInt()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_WINDOWS)
    override fun setWindowPosition(
        area: WindowArea,
        value: Int,
        onComplete: (WindowArea, Int?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.WINDOW_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WINDOW,
            areaId = areaId,
            value = value,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }


    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun registerWindowLockState(area: WindowArea, listener: WindowLockStateListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.WINDOW_LOCK,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WINDOW,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun unregisterWindowLockState(area: WindowArea, listener: WindowLockStateListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.WINDOW_LOCK,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WINDOW,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun registerWindowPosition(area: WindowArea, listener: WindowPositionListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.WINDOW_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WINDOW,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun unregisterWindowPosition(area: WindowArea, listener: WindowPositionListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.WINDOW_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WINDOW,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun registerWindowBlindPosition(area: WindowArea, listener: WindowPositionListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.WINDOW_BLIND_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WINDOW,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun unregisterWindowBlindPosition(area: WindowArea, listener: WindowPositionListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.WINDOW_BLIND_POS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WINDOW,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun registerSmartGlassLevel(area: WindowArea, listener: SmartGlassLevelListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.SMART_GLASS_LEVEL,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WINDOW,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun unregisterSmartGlassLevel(area: WindowArea, listener: SmartGlassLevelListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.SMART_GLASS_LEVEL,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WINDOW,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun registerSmartGlassState(listener: SmartGlassStateListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.SMART_GLASS_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_WINDOWS)
    override fun unregisterSmartGlassState(listener: SmartGlassStateListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.SMART_GLASS_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    private fun String?.toSmartGlassConfigOrNull(): SmartGlassConfig? {
        val config = runCatching { fromJsonOrNull<IntArray>() }.getOrNull() ?: return null
        if (config.size != 2) return null
        return SmartGlassConfig(zoneCount = config[0], levelCount = config[1])
    }
}
