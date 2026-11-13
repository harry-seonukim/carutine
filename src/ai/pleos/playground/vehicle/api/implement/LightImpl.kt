package ai.pleos.playground.vehicle.api.implement

import ai.pleos.playground.vehicle.api.Light
import ai.pleos.playground.vehicle.api.model.area.SeatArea
import ai.pleos.playground.vehicle.api.model.capability.AreaBounds
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.PleosPropertyIds
import ai.pleos.playground.vehicle.constant.VehicleAreaCustomType
import ai.pleos.playground.vehicle.constant.VehicleAreaValue
import ai.pleos.playground.vehicle.constant.control.LightState
import ai.pleos.playground.vehicle.constant.control.LightSwitch
import ai.pleos.playground.vehicle.listener.AmbientColorAutoByDriveModeEnabledListener
import ai.pleos.playground.vehicle.listener.AmbientLightBrightnessColorListener
import ai.pleos.playground.vehicle.listener.AmbientLightBrightnessListener
import ai.pleos.playground.vehicle.listener.LightStateListener
import ai.pleos.playground.vehicle.permission.RequiresCustomPermission
import ai.pleos.playground.vehicle.service.VehicleServiceManager
import ai.pleos.playground.vehicle.utils.ImplementUtil
import android.annotation.SuppressLint

@SuppressLint("MissingPermission")
@Suppress("OVERRIDE_DEPRECATION")
internal class LightImpl(private val serviceManager: VehicleServiceManager) : Light {

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun checkReadingLightSwitchCapabilities(
        onComplete: (Map<SeatArea, List<LightSwitch>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkReadingLightSwitchCapabilities(
            onComplete = { _, map -> onComplete(map) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun checkReadingLightSwitchCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, List<LightSwitch>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaSupportedListCapability<SeatArea, LightSwitch>(
            instance = this,
            propId = PleosPropertyIds.READING_LIGHTS_SWITCH,
            idResolver = { id -> LightSwitch.findById(id.toInt()) },
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun checkCabinLightSwitchCapabilities(
        onComplete: (List<LightSwitch>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkCabinLightSwitchCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun checkCabinLightSwitchCapabilities(
        onComplete: (CapabilityConfig, List<LightSwitch>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestGlobalSupportedListCapability(
            instance = this,
            propId = PleosPropertyIds.CABIN_LIGHTS_SWITCH,
            idResolver = { id -> LightSwitch.findById(id.toInt()) },
            onComplete = onComplete,
            onFailed = onFailed
        )
    }


    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun checkHighBeamLightSwitchCapabilities(
        onComplete: (List<LightSwitch>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkHighBeamLightSwitchCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun checkHighBeamLightSwitchCapabilities(
        onComplete: (CapabilityConfig, List<LightSwitch>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestGlobalSupportedListCapability(
            instance = this,
            propId = PleosPropertyIds.HIGH_BEAM_LIGHTS_SWITCH,
            idResolver = { id -> LightSwitch.findById(id.toInt()) },
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun checkLowBeamLightSwitchCapabilities(
        onComplete: (List<LightSwitch>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkLowBeamLightSwitchCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun checkLowBeamLightSwitchCapabilities(
        onComplete: (CapabilityConfig, List<LightSwitch>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestGlobalSupportedListCapability(
            instance = this,
            propId = PleosPropertyIds.LOW_BEAM_LIGHTS_SWITCH,
            idResolver = { id -> LightSwitch.findById(id.toInt()) },
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun checkTailLampLightSwitchCapabilities(
        onComplete: (List<LightSwitch>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkTailLampLightSwitchCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun checkTailLampLightSwitchCapabilities(
        onComplete: (CapabilityConfig, List<LightSwitch>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestGlobalSupportedListCapability(
            instance = this,
            propId = PleosPropertyIds.TAIL_LAMP_LIGHTS_SWITCH,
            idResolver = { id -> LightSwitch.findById(id.toInt()) },
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun checkFrontFogLightSwitchCapabilities(
        onComplete: (List<LightSwitch>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkFrontFogLightSwitchCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun checkFrontFogLightSwitchCapabilities(
        onComplete: (CapabilityConfig, List<LightSwitch>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestGlobalSupportedListCapability(
            instance = this,
            propId = PleosPropertyIds.FRONT_FOG_LIGHTS_SWITCH,
            idResolver = { id -> LightSwitch.findById(id.toInt()) },
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun checkRearFogLightSwitchCapabilities(
        onComplete: (List<LightSwitch>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkRearFogLightSwitchCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun checkRearFogLightSwitchCapabilities(
        onComplete: (CapabilityConfig, List<LightSwitch>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestGlobalSupportedListCapability(
            instance = this,
            propId = PleosPropertyIds.REAR_FOG_LIGHTS_SWITCH,
            idResolver = { id -> LightSwitch.findById(id.toInt()) },
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun checkReadingLightStateCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, List<LightState>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaSupportedListCapability<SeatArea, LightState>(
            instance = this,
            propId = PleosPropertyIds.READING_LIGHTS_STATE,
            idResolver = { id -> LightState.findById(id.toInt()) },
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun checkCabinLightStateCapabilities(
        onComplete: (CapabilityConfig, List<LightState>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestGlobalSupportedListCapability(
            instance = this,
            propId = PleosPropertyIds.CABIN_LIGHTS_STATE,
            idResolver = { id -> LightState.findById(id.toInt()) },
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun checkHighBeamLightStateCapabilities(
        onComplete: (CapabilityConfig, List<LightState>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestGlobalSupportedListCapability(
            instance = this,
            propId = PleosPropertyIds.HIGH_BEAM_LIGHTS_STATE,
            idResolver = { id -> LightState.findById(id.toInt()) },
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun checkLowBeamLightStateCapabilities(
        onComplete: (CapabilityConfig, List<LightState>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestGlobalSupportedListCapability(
            instance = this,
            propId = PleosPropertyIds.LOW_BEAM_LIGHTS_STATE,
            idResolver = { id -> LightState.findById(id.toInt()) },
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun checkTailLampLightStateCapabilities(
        onComplete: (CapabilityConfig, List<LightState>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestGlobalSupportedListCapability(
            instance = this,
            propId = PleosPropertyIds.TAIL_LAMP_LIGHTS_STATE,
            idResolver = { id -> LightState.findById(id.toInt()) },
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun checkFrontFogLightStateCapabilities(
        onComplete: (CapabilityConfig, List<LightState>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestGlobalSupportedListCapability(
            instance = this,
            propId = PleosPropertyIds.FRONT_FOG_LIGHTS_STATE,
            idResolver = { id -> LightState.findById(id.toInt()) },
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun checkRearFogLightStateCapabilities(
        onComplete: (CapabilityConfig, List<LightState>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestGlobalSupportedListCapability(
            instance = this,
            propId = PleosPropertyIds.REAR_FOG_LIGHTS_STATE,
            idResolver = { id -> LightState.findById(id.toInt()) },
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun checkAmbientLightBrightnessCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaBoundsCapability(
            instance = this,
            propId = PleosPropertyIds.INTERIOR_LIGHTS_AMBIENT_BRIGHTNESS,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun checkAmbientLightBrightnessColorCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(
            instance = this,
            propId = PleosPropertyIds.INTERIOR_LIGHTS_AMBIENT_BRIGHTNESS_COLOR,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun checkAmbientLightSwitchCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, List<LightSwitch>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaSupportedListCapability<SeatArea, LightSwitch>(
            instance = this,
            propId = PleosPropertyIds.INTERIOR_LIGHTS_AMBIENT_SWITCH,
            idResolver = { id -> LightSwitch.findById(id.toInt()) },
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun checkAmbientLightStateCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, List<LightState>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaSupportedListCapability<SeatArea, LightState>(
            instance = this,
            propId = PleosPropertyIds.INTERIOR_LIGHTS_AMBIENT_STATE,
            idResolver = { id -> LightState.findById(id.toInt()) },
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun checkAmbientColorAutoByDriveModeEnabledCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAccessCapability(
            instance = this,
            propId = PleosPropertyIds.AMBIENT_COLOR_AUTO_BY_DRIVE_MODE_ENABLED,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun getReadingLightSwitch(
        area: SeatArea,
        onComplete: (LightSwitch?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.READING_LIGHTS_SWITCH,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(LightSwitch.findById(it?.toInt())) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_LIGHTS)
    override fun setReadingLightSwitch(
        area: SeatArea,
        value: LightSwitch,
        onComplete: (LightSwitch?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.READING_LIGHTS_SWITCH,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            value = value.id,
            onComplete = { onComplete(value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun getCurrentReadingLightState(
        area: SeatArea,
        onComplete: (LightState?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.READING_LIGHTS_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(LightState.findById(it?.toInt())) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun getCabinLightSwitch(
        onComplete: (LightSwitch?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.CABIN_LIGHTS_SWITCH,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(LightSwitch.findById(it?.toInt())) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_LIGHTS)
    override fun setCabinLightSwitch(
        value: LightSwitch,
        onComplete: (LightSwitch?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.CABIN_LIGHTS_SWITCH,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            value = value.id,
            onComplete = { onComplete(value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun getCurrentCabinLightState(
        onComplete: (LightState?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.CABIN_LIGHTS_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(LightState.findById(it?.toInt())) },
            onFailed = onFailed
        )
    }


    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_LIGHTS)
    override fun setHighBeamLightSwitch(
        value: LightSwitch,
        onComplete: (LightSwitch?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.HIGH_BEAM_LIGHTS_SWITCH,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            value = value.id,
            onComplete = { onComplete(value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun getHighBeamLightSwitch(
        onComplete: (LightSwitch?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.HIGH_BEAM_LIGHTS_SWITCH,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(LightSwitch.findById(it?.toInt())) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun getCurrentHighBeamLightState(
        onComplete: (LightState?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.HIGH_BEAM_LIGHTS_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(LightState.findById(it?.toInt())) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_LIGHTS)
    override fun setLowBeamLightSwitch(
        value: LightSwitch,
        onComplete: (LightSwitch?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.LOW_BEAM_LIGHTS_SWITCH,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            value = value.id,
            onComplete = { onComplete(value) },
            onFailed = onFailed
        )
    }


    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun getLowBeamLightSwitch(
        onComplete: (LightSwitch?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.LOW_BEAM_LIGHTS_SWITCH,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(LightSwitch.findById(it?.toInt())) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun getCurrentLowBeamLightState(
        onComplete: (LightState?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.LOW_BEAM_LIGHTS_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(LightState.findById(it?.toInt())) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun getCurrentTailLampLightState(
        onComplete: (LightState?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.TAIL_LAMP_LIGHTS_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(LightState.findById(it?.toInt())) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun getTailLampLightSwitch(
        onComplete: (LightSwitch?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.TAIL_LAMP_LIGHTS_SWITCH,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(LightSwitch.findById(it?.toInt())) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_LIGHTS)
    override fun setTailLampLightSwitch(
        value: LightSwitch,
        onComplete: (LightSwitch?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.TAIL_LAMP_LIGHTS_SWITCH,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            value = value.id,
            onComplete = { onComplete(value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun getCurrentFrontFogLightState(
        onComplete: (LightState?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.FRONT_FOG_LIGHTS_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(LightState.findById(it?.toInt())) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun getFrontFogLightSwitch(
        onComplete: (LightSwitch?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.FRONT_FOG_LIGHTS_SWITCH,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(LightSwitch.findById(it?.toInt())) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_LIGHTS)
    override fun setFrontFogLightSwitch(
        value: LightSwitch,
        onComplete: (LightSwitch?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.FRONT_FOG_LIGHTS_SWITCH,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            value = value.id,
            onComplete = { onComplete(value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun getCurrentRearFogLightState(
        onComplete: (LightState?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.REAR_FOG_LIGHTS_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(LightState.findById(it?.toInt())) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun getRearFogLightSwitch(
        onComplete: (LightSwitch?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.REAR_FOG_LIGHTS_SWITCH,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(LightSwitch.findById(it?.toInt())) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_LIGHTS)
    override fun setRearFogLightSwitch(
        value: LightSwitch,
        onComplete: (LightSwitch?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.REAR_FOG_LIGHTS_SWITCH,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            value = value.id,
            onComplete = { onComplete(value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun getAmbientLightBrightness(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.INTERIOR_LIGHTS_AMBIENT_BRIGHTNESS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toInt()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_LIGHTS)
    override fun setAmbientLightBrightness(
        area: SeatArea,
        value: Int,
        onComplete: (SeatArea, Int) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.INTERIOR_LIGHTS_AMBIENT_BRIGHTNESS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            value = value,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun getAmbientLightBrightnessColor(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.INTERIOR_LIGHTS_AMBIENT_BRIGHTNESS_COLOR,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toInt()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_LIGHTS)
    override fun setAmbientLightBrightnessColor(
        area: SeatArea,
        value: Int,
        onComplete: ((SeatArea, Int) -> Unit),
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.INTERIOR_LIGHTS_AMBIENT_BRIGHTNESS_COLOR,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            value = value,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun getAmbientLightSwitch(
        area: SeatArea,
        onComplete: (LightSwitch?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.INTERIOR_LIGHTS_AMBIENT_SWITCH,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(LightSwitch.findById(it?.toInt())) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_LIGHTS)
    override fun setAmbientLightSwitch(
        area: SeatArea,
        value: LightSwitch,
        onComplete: ((SeatArea, LightSwitch?) -> Unit),
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.INTERIOR_LIGHTS_AMBIENT_SWITCH,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            value = value.id,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun getAmbientLightState(
        area: SeatArea,
        onComplete: (LightState?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.INTERIOR_LIGHTS_AMBIENT_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(LightState.findById(it?.toInt())) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun getAmbientColorAutoByDriveModeEnabled(
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.AMBIENT_COLOR_AUTO_BY_DRIVE_MODE_ENABLED,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(it?.toBoolean()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_LIGHTS)
    override fun setAmbientColorAutoByDriveModeEnabled(
        value: Boolean,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.AMBIENT_COLOR_AUTO_BY_DRIVE_MODE_ENABLED,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            value = value,
            onComplete = { onComplete(value) },
            onFailed = onFailed
        )
    }


    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun registerHighBeamLightState(listener: LightStateListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.HIGH_BEAM_LIGHTS_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun unregisterHighBeamLightState(listener: LightStateListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.HIGH_BEAM_LIGHTS_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun registerLowBeamLightState(listener: LightStateListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.LOW_BEAM_LIGHTS_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun unregisterLowBeamLightState(listener: LightStateListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.LOW_BEAM_LIGHTS_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun registerTailLampLightState(listener: LightStateListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.TAIL_LAMP_LIGHTS_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun unregisterTailLampLightState(listener: LightStateListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.TAIL_LAMP_LIGHTS_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun registerFrontFogLightState(listener: LightStateListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.FRONT_FOG_LIGHTS_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun unregisterFrontFogLightState(listener: LightStateListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.FRONT_FOG_LIGHTS_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun registerRearFogLightState(listener: LightStateListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.REAR_FOG_LIGHTS_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun unregisterRearFogLightState(listener: LightStateListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.REAR_FOG_LIGHTS_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun registerReadingLightState(area: SeatArea, listener: LightStateListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.READING_LIGHTS_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun unregisterReadingLightState(area: SeatArea, listener: LightStateListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.READING_LIGHTS_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun registerCabinLightState(listener: LightStateListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.CABIN_LIGHTS_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun unregisterCabinLightState(listener: LightStateListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.CABIN_LIGHTS_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun registerAmbientLightBrightnessListener(area: SeatArea, listener: AmbientLightBrightnessListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.INTERIOR_LIGHTS_AMBIENT_BRIGHTNESS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun unregisterAmbientLightBrightnessListener(area: SeatArea, listener: AmbientLightBrightnessListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.INTERIOR_LIGHTS_AMBIENT_BRIGHTNESS,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun registerAmbientLightBrightnessColorListener(area: SeatArea, listener: AmbientLightBrightnessColorListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.INTERIOR_LIGHTS_AMBIENT_BRIGHTNESS_COLOR,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun unregisterAmbientLightBrightnessColorListener(area: SeatArea, listener: AmbientLightBrightnessColorListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.INTERIOR_LIGHTS_AMBIENT_BRIGHTNESS_COLOR,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun registerAmbientLightStateListener(area: SeatArea, listener: LightStateListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.INTERIOR_LIGHTS_AMBIENT_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun unregisterAmbientLightStateListener(area: SeatArea, listener: LightStateListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.INTERIOR_LIGHTS_AMBIENT_STATE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun registerAmbientColorAutoByDriveModeEnabled(listener: AmbientColorAutoByDriveModeEnabledListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.AMBIENT_COLOR_AUTO_BY_DRIVE_MODE_ENABLED,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_LIGHTS)
    override fun unregisterAmbientColorAutoByDriveModeEnabled(listener: AmbientColorAutoByDriveModeEnabledListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.AMBIENT_COLOR_AUTO_BY_DRIVE_MODE_ENABLED,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }
}
