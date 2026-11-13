package ai.pleos.playground.vehicle.api.implement

import ai.pleos.playground.vehicle.api.HVAC
import ai.pleos.playground.vehicle.api.model.area.SeatArea
import ai.pleos.playground.vehicle.api.model.area.WindowArea
import ai.pleos.playground.vehicle.api.model.capability.AreaBounds
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.api.model.capability.HvacTemperatureConfigArray
import ai.pleos.playground.vehicle.common.catching
import ai.pleos.playground.vehicle.common.toCelsius
import ai.pleos.playground.vehicle.common.toFahrenheit
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.PleosPropertyIds
import ai.pleos.playground.vehicle.constant.VehicleAreaCustomType
import ai.pleos.playground.vehicle.constant.control.FanDirectionType
import ai.pleos.playground.vehicle.constant.unit.TemperatureUnit
import ai.pleos.playground.vehicle.listener.HvacAcStateListener
import ai.pleos.playground.vehicle.listener.HvacAirCleanerStateListener
import ai.pleos.playground.vehicle.listener.HvacAutoStateListener
import ai.pleos.playground.vehicle.listener.HvacDefrosterStateListener
import ai.pleos.playground.vehicle.listener.HvacDualStateListener
import ai.pleos.playground.vehicle.listener.HvacFanSpeedListener
import ai.pleos.playground.vehicle.listener.HvacMaxAcStateListener
import ai.pleos.playground.vehicle.listener.HvacMaxDefrostStateListener
import ai.pleos.playground.vehicle.listener.HvacPowerStateListener
import ai.pleos.playground.vehicle.listener.HvacRecirculationStateListener
import ai.pleos.playground.vehicle.listener.HvacTemperatureListener
import ai.pleos.playground.vehicle.listener.HvacTemperatureValueListener
import ai.pleos.playground.vehicle.permission.RequiresCustomPermission
import ai.pleos.playground.vehicle.service.VehicleServiceManager
import ai.pleos.playground.vehicle.utils.CapabilityUtil
import ai.pleos.playground.vehicle.utils.ImplementUtil
import android.annotation.SuppressLint

@SuppressLint("MissingPermission")
@Suppress("OVERRIDE_DEPRECATION")
internal class HVACImpl(private val serviceManager: VehicleServiceManager) : HVAC {
    private data class HvacTemperatureValueListenerKey(
        val areaId: Int,
        val listener: HvacTemperatureValueListener
    )

    private val hvacTemperatureValueListenerMap = mutableMapOf<HvacTemperatureValueListenerKey, HvacTemperatureListener>()
    private val hvacTemperatureCurrentValueListenerMap = mutableMapOf<HvacTemperatureValueListenerKey, HvacTemperatureListener>()
    @Volatile
    private var cachedTemperatureSetConfigArray: List<Int>? = null
    @Volatile
    private var hasCachedTemperatureSetConfigArray: Boolean = false

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacAcCapabilities(
        onComplete: (List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkHvacAcCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacAcCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(instance = this, propId = PleosPropertyIds.HVAC_AC_ON, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacAutoCapabilities(
        onComplete: (List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkHvacAutoCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacAutoCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(instance = this, propId = PleosPropertyIds.HVAC_AUTO_ON, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacRecirculationCapabilities(
        onComplete: (List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkHvacRecirculationCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacRecirculationCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(instance = this, propId = PleosPropertyIds.HVAC_RECIRC_ON, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacDefrosterCapabilities(
        onComplete: (List<WindowArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkHvacDefrosterCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacDefrosterCapabilities(
        onComplete: (CapabilityConfig, List<WindowArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(instance = this, propId = PleosPropertyIds.HVAC_DEFROSTER, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacDualCapabilities(
        onComplete: (List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkHvacDualCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacDualCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(instance = this, propId = PleosPropertyIds.HVAC_DUAL_ON, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacFanDirectionCapabilities(
        onComplete: (Map<SeatArea, List<FanDirectionType>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkHvacFanDirectionCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacFanDirectionCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, List<FanDirectionType>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaSupportedListCapability<SeatArea, FanDirectionType>(
            instance = this,
            propId = PleosPropertyIds.HVAC_FAN_DIRECTION,
            idResolver = { id -> FanDirectionType.findById(id.toInt()) },
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacFanSpeedCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkHvacFanSpeedCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacFanSpeedCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaBoundsCapability(instance = this, propId = PleosPropertyIds.HVAC_FAN_SPEED, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacMaxAcCapabilities(
        onComplete: (List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkHvacMaxAcCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacMaxAcCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(instance = this, propId = PleosPropertyIds.HVAC_MAX_AC_ON, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacMaxDefrostCapabilities(
        onComplete: (List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkHvacMaxDefrostCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacMaxDefrostCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(instance = this, propId = PleosPropertyIds.HVAC_MAX_DEFROST_ON, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacPowerCapabilities(
        onComplete: (List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkHvacPowerCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacPowerCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(instance = this, propId = PleosPropertyIds.HVAC_POWER_ON, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacAutoRecirculationCapabilities(
        onComplete: (List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkHvacAutoRecirculationCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacAutoRecirculationCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(instance = this, propId = PleosPropertyIds.HVAC_AUTO_RECIRC_ON, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacAirCleanerCapabilities(
        onComplete: (List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkHvacAirCleanerCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacAirCleanerCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(instance = this, propId = PleosPropertyIds.HVAC_AIR_CLEANER, onComplete = onComplete, onFailed = onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacTemperatureCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Float>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkHvacTemperatureCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacTemperatureCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Float>>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkHvacTemperatureCapabilities(
            onComplete = { capabilityConfig, areaBounds, _ -> onComplete(capabilityConfig, areaBounds) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacTemperatureCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Float>>, HvacTemperatureConfigArray?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestCapability(
            instance = this,
            propId = PleosPropertyIds.HVAC_TEMPERATURE_SET,
            action = { ret ->
                val configs = CapabilityUtil.getAreaConfigs<SeatArea, Float>(ret)
                val areaBoundsMap = mutableMapOf<SeatArea, AreaBounds<Float>>()
                catching("checkHvacTemperatureCapabilities") {
                    for (config in configs) {
                        CapabilityUtil.createAreaBounds(config.value)?.let { bounds ->
                            areaBoundsMap[config.key] = bounds
                        }
                    }
                }.onSuccess {
                    onComplete(
                        CapabilityConfig(CapabilityUtil.getAccess(ret)),
                        areaBoundsMap,
                        ret.configArray.toHvacTemperatureConfigArrayOrNull()
                    )
                }.onFailure {
                    onFailed(Exception(it))
                }
            },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun checkHvacTemperatureCurrentCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(instance = this, propId = PleosPropertyIds.HVAC_TEMPERATURE_CURRENT, onComplete = onComplete, onFailed = onFailed)
    }

//    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
//    override fun checkHvacTemperatureValueSuggestionCapabilities(
//        onComplete: (CapabilityConfig) -> Unit,
//        onFailed: (Exception) -> Unit
//    ) {
//        serviceManager.requestAccessCapability(instance = this, propId = PleosPropertyIds.HVAC_TEMPERATURE_VALUE_SUGGESTION, onComplete = onComplete, onFailed = onFailed)
//    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    override fun setHvacAc(
        area: SeatArea,
        value: Boolean,
        onComplete: (SeatArea, Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.HVAC_AC_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            value = value,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun getHvacAcState(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.HVAC_AC_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toBoolean()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    override fun setHvacAuto(
        area: SeatArea,
        value: Boolean,
        onComplete: (SeatArea, Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.HVAC_AUTO_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            value = value,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun getHvacAutoState(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.HVAC_AUTO_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toBoolean()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    override fun setHvacAutoRecirculation(
        area: SeatArea,
        value: Boolean,
        onComplete: (SeatArea, Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.HVAC_AUTO_RECIRC_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            value = value,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun getHvacAutoRecirculationState(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.HVAC_AUTO_RECIRC_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toBoolean()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    override fun setHvacDefroster(
        area: WindowArea,
        value: Boolean,
        onComplete: (WindowArea, Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.HVAC_DEFROSTER,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WINDOW,
            areaId = areaId,
            value = value,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun getHvacDefrosterState(
        area: WindowArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.HVAC_DEFROSTER,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WINDOW,
            areaId = areaId,
            action = { onComplete(it?.toBoolean()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    override fun setHvacDual(
        area: SeatArea,
        value: Boolean,
        onComplete: (SeatArea, Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.HVAC_DUAL_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            value = value,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }


    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun getHvacDualState(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.HVAC_DUAL_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toBoolean()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    override fun setHvacFanDirection(
        area: SeatArea,
        value: FanDirectionType,
        onComplete: (SeatArea, FanDirectionType?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.HVAC_FAN_DIRECTION,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            value = value.id,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun getHvacFanDirection(
        area: SeatArea,
        onComplete: (FanDirectionType?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.HVAC_FAN_DIRECTION,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(FanDirectionType.findById(it?.toInt())) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    override fun setHvacFanSpeed(
        area: SeatArea,
        value: Int,
        onComplete: (SeatArea, Int?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        catching("setHvacFanSpeed") {
            val areaId = ImplementUtil.getAreaId(area)
            serviceManager.setPropertyAsync(
                instance = this,
                propId = PleosPropertyIds.HVAC_FAN_SPEED,
                areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
                areaId = areaId,
                value = value,
                onComplete = { onComplete(area, value) },
                onFailed = onFailed
            )
        }.onFailure { onFailed(Exception(it)) }
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun getHvacFanSpeed(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.HVAC_FAN_SPEED,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toInt()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    override fun setHvacMaxAc(
        area: SeatArea,
        value: Boolean,
        onComplete: (SeatArea, Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.HVAC_MAX_AC_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            value = value,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun getHvacMaxAcState(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.HVAC_MAX_AC_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toBoolean()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    override fun setHvacMaxDefrost(
        area: SeatArea,
        value: Boolean,
        onComplete: (SeatArea, Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.HVAC_MAX_DEFROST_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            value = value,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun getHvacMaxDefrostState(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.HVAC_MAX_DEFROST_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toBoolean()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    override fun setHvacPower(
        area: SeatArea,
        value: Boolean,
        onComplete: (SeatArea, Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.HVAC_POWER_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            value = value,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun getHvacPowerState(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.HVAC_POWER_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toBoolean()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    override fun setHvacRecirculation(
        area: SeatArea,
        value: Boolean,
        onComplete: (SeatArea, Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.HVAC_RECIRC_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            value = value,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun getHvacRecirculationState(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.HVAC_RECIRC_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toBoolean()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    override fun setHvacAirCleaner(
        area: SeatArea,
        value: Boolean,
        onComplete: (SeatArea, Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.HVAC_AIR_CLEANER,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            value = value,
            onComplete = { onComplete(area, value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun getHvacAirCleanerState(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.HVAC_AIR_CLEANER,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toBoolean()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun getHvacTemperatureCurrent(
        area: SeatArea,
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        getHvacTemperatureCurrent(
            area = area,
            unit = TemperatureUnit.CELSIUS,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun getHvacTemperatureCurrent(
        area: SeatArea,
        unit: TemperatureUnit,
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.HVAC_TEMPERATURE_CURRENT,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { value ->
                val celsiusValue = value?.toFloat()
                if (celsiusValue == null || unit == TemperatureUnit.CELSIUS) {
                    onComplete(celsiusValue)
                    return@getProperty
                }
                convertHvacTemperatureUsingVehicleRule(
                    celsiusValue = celsiusValue,
                    unit = unit,
                    onComplete = onComplete
                )
            },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    override fun setHvacTemperature(
        area: SeatArea,
        value: Float,
        unit: TemperatureUnit,
        onComplete: (SeatArea, Float?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        if (unit == TemperatureUnit.CELSIUS) {
            setHvacTemperatureSetProperty(areaId = areaId, valueCelsius = value, originalValue = value, area = area, onComplete = onComplete, onFailed = onFailed)
            return
        }

        requestTemperatureSetConfigArray(
            onComplete = { configArray ->
                val valueCelsius = convertTemperatureByConfigArray(
                    value = value,
                    fromUnit = TemperatureUnit.FAHRENHEIT,
                    toUnit = TemperatureUnit.CELSIUS,
                    configArray = configArray
                ) ?: value.toCelsius()
                setHvacTemperatureSetProperty(
                    areaId = areaId,
                    valueCelsius = valueCelsius,
                    originalValue = value,
                    area = area,
                    onComplete = onComplete,
                    onFailed = onFailed
                )
            },
            onFailed = {
                setHvacTemperatureSetProperty(
                    areaId = areaId,
                    valueCelsius = value.toCelsius(),
                    originalValue = value,
                    area = area,
                    onComplete = onComplete,
                    onFailed = onFailed
                )
            }
        )
    }


    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun getHvacTemperature(
        area: SeatArea,
        unit: TemperatureUnit,
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.HVAC_TEMPERATURE_SET,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { value ->
                catching("getHvacTemperature") {
                    val celsiusValue = value?.toFloat()
                    if (celsiusValue == null || unit == TemperatureUnit.CELSIUS) {
                        onComplete(celsiusValue)
                        return@catching
                    }

                    convertHvacTemperatureUsingVehicleRule(
                        celsiusValue = celsiusValue,
                        unit = unit,
                        onComplete = onComplete
                    )
                }.onFailure { t ->
                    onFailed(Exception(t))
                }
            },
            onFailed = onFailed
        )
    }

    private fun convertHvacTemperatureUsingVehicleRule(
        celsiusValue: Float,
        unit: TemperatureUnit,
        onComplete: (Float?) -> Unit
    ) {
        requestTemperatureSetConfigArray(
            onComplete = { configArray ->
                val converted = convertTemperatureByConfigArray(
                    value = celsiusValue,
                    fromUnit = TemperatureUnit.CELSIUS,
                    toUnit = unit,
                    configArray = configArray
                ) ?: convertTemperatureByFormula(celsiusValue, unit)
                onComplete(converted)
            },
            onFailed = {
                onComplete(convertTemperatureByFormula(celsiusValue, unit))
            }
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    private fun requestTemperatureSetConfigArray(
        onComplete: (List<Int>?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        if (hasCachedTemperatureSetConfigArray) {
            onComplete(cachedTemperatureSetConfigArray)
            return
        }

        serviceManager.requestCapability(
            instance = this,
            propId = PleosPropertyIds.HVAC_TEMPERATURE_SET,
            action = { config ->
                cachedTemperatureSetConfigArray = config.configArray
                hasCachedTemperatureSetConfigArray = true
                onComplete(config.configArray)
            },
            onFailed = onFailed
        )
    }

    private fun convertTemperatureByConfigArray(
        value: Float,
        fromUnit: TemperatureUnit,
        toUnit: TemperatureUnit,
        configArray: List<Int>?
    ): Float? {
        if (fromUnit == toUnit) return value
        if (configArray == null || configArray.size < 6) return null

        val (minFrom, incFrom, minTo, incTo) = when (fromUnit to toUnit) {
            TemperatureUnit.CELSIUS to TemperatureUnit.FAHRENHEIT -> {
                arrayOf(configArray[0] / 10f, configArray[2] / 10f, configArray[3] / 10f, configArray[5] / 10f)
            }
            TemperatureUnit.FAHRENHEIT to TemperatureUnit.CELSIUS -> {
                arrayOf(configArray[3] / 10f, configArray[5] / 10f, configArray[0] / 10f, configArray[2] / 10f)
            }
            else -> return null
        }

        if (incFrom <= 0f || incTo <= 0f) return null
        val increments = Math.round((value - minFrom) / incFrom)
        return incTo * increments + minTo
    }

    private fun convertTemperatureByFormula(celsiusValue: Float, unit: TemperatureUnit): Float {
        return if (unit == TemperatureUnit.FAHRENHEIT) celsiusValue.toFahrenheit() else celsiusValue
    }

    private fun List<Int>?.toHvacTemperatureConfigArrayOrNull(): HvacTemperatureConfigArray? {
        val raw = this ?: return null
        if (raw.size < 6) return null

        return HvacTemperatureConfigArray(
            minCelsius = raw[0] / 10f,
            maxCelsius = raw[1] / 10f,
            incrementCelsius = raw[2] / 10f,
            minFahrenheit = raw[3] / 10f,
            maxFahrenheit = raw[4] / 10f,
            incrementFahrenheit = raw[5] / 10f,
            raw = raw
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_CLIMATE)
    private fun setHvacTemperatureSetProperty(
        areaId: Int,
        valueCelsius: Float,
        originalValue: Float,
        area: SeatArea,
        onComplete: (SeatArea, Float?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.HVAC_TEMPERATURE_SET,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            value = valueCelsius,
            onComplete = { onComplete(area, originalValue) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun registerHvacAcState(area: SeatArea, listener: HvacAcStateListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_AC_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun unregisterHvacAcState(area: SeatArea, listener: HvacAcStateListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_AC_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun registerHvacAutoState(area: SeatArea, listener: HvacAutoStateListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_AUTO_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun unregisterHvacAutoState(area: SeatArea, listener: HvacAutoStateListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_AUTO_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun registerHvacAutoRecirculationState(
        area: SeatArea,
        listener: HvacRecirculationStateListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_AUTO_RECIRC_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun unregisterHvacAutoRecirculationState(
        area: SeatArea,
        listener: HvacRecirculationStateListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_AUTO_RECIRC_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun registerHvacDefrosterState(
        area: WindowArea,
        listener: HvacDefrosterStateListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_DEFROSTER,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WINDOW,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun unregisterHvacDefrosterState(
        area: WindowArea,
        listener: HvacDefrosterStateListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_DEFROSTER,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_WINDOW,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun registerHvacDualState(area: SeatArea, listener: HvacDualStateListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_DUAL_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun unregisterHvacDualState(area: SeatArea, listener: HvacDualStateListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_DUAL_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun registerHvacMaxAcState(area: SeatArea, listener: HvacMaxAcStateListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_MAX_AC_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun unregisterHvacMaxAcState(area: SeatArea, listener: HvacMaxAcStateListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_MAX_AC_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun registerHvacMaxDefrostState(
        area: SeatArea,
        listener: HvacMaxDefrostStateListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_MAX_DEFROST_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun unregisterHvacMaxDefrostState(
        area: SeatArea,
        listener: HvacMaxDefrostStateListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_MAX_DEFROST_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun registerHvacPowerState(area: SeatArea, listener: HvacPowerStateListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_POWER_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun unregisterHvacPowerState(area: SeatArea, listener: HvacPowerStateListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_POWER_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun registerHvacAirCleanerState(
        area: SeatArea,
        listener: HvacAirCleanerStateListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_AIR_CLEANER,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun unregisterHvacAirCleanerState(
        area: SeatArea,
        listener: HvacAirCleanerStateListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_AIR_CLEANER,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun registerHvacTemperatureCurrent(area: SeatArea, listener: HvacTemperatureListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_TEMPERATURE_CURRENT,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun registerHvacTemperatureCurrent(area: SeatArea, listener: HvacTemperatureValueListener) {
        val areaId = ImplementUtil.getAreaId(area)
        val key = HvacTemperatureValueListenerKey(areaId = areaId, listener = listener)
        var isNewRegistration = false
        val wrapper = synchronized(hvacTemperatureCurrentValueListenerMap) {
            hvacTemperatureCurrentValueListenerMap[key] ?: object : HvacTemperatureListener {
                override fun onHvacTemperatureUpdated(temperature: Float?) {
                    if (temperature == null) {
                        listener.onHvacTemperatureUpdated(null, null)
                        return
                    }
                    requestTemperatureSetConfigArray(
                        onComplete = { configArray ->
                            val fahrenheit = convertTemperatureByConfigArray(
                                value = temperature,
                                fromUnit = TemperatureUnit.CELSIUS,
                                toUnit = TemperatureUnit.FAHRENHEIT,
                                configArray = configArray
                            ) ?: temperature.toFahrenheit()
                            listener.onHvacTemperatureUpdated(temperature, fahrenheit)
                        },
                        onFailed = {
                            listener.onHvacTemperatureUpdated(temperature, temperature.toFahrenheit())
                        }
                    )
                }

                override fun onFailed(e: Exception) {
                    listener.onFailed(e)
                }
            }.also {
                isNewRegistration = true
                hvacTemperatureCurrentValueListenerMap[key] = it
            }
        }

        if (!isNewRegistration) return

        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_TEMPERATURE_CURRENT,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = wrapper
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun unregisterHvacTemperatureCurrent(
        area: SeatArea,
        listener: HvacTemperatureListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_TEMPERATURE_CURRENT,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun unregisterHvacTemperatureCurrent(
        area: SeatArea,
        listener: HvacTemperatureValueListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        val key = HvacTemperatureValueListenerKey(areaId = areaId, listener = listener)
        val wrapper = synchronized(hvacTemperatureCurrentValueListenerMap) {
            hvacTemperatureCurrentValueListenerMap.remove(key)
        } ?: return

        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_TEMPERATURE_CURRENT,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = wrapper
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun registerHvacTemperature(area: SeatArea, listener: HvacTemperatureListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_TEMPERATURE_SET,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun registerHvacTemperature(area: SeatArea, listener: HvacTemperatureValueListener) {
        val areaId = ImplementUtil.getAreaId(area)
        val key = HvacTemperatureValueListenerKey(areaId = areaId, listener = listener)
        var isNewRegistration = false
        val wrapper = synchronized(hvacTemperatureValueListenerMap) {
            hvacTemperatureValueListenerMap[key] ?: object : HvacTemperatureListener {
                override fun onHvacTemperatureUpdated(temperature: Float?) {
                    if (temperature == null) {
                        listener.onHvacTemperatureUpdated(null, null)
                        return
                    }

                    requestTemperatureSetConfigArray(
                        onComplete = { configArray ->
                            val fahrenheit = convertTemperatureByConfigArray(
                                value = temperature,
                                fromUnit = TemperatureUnit.CELSIUS,
                                toUnit = TemperatureUnit.FAHRENHEIT,
                                configArray = configArray
                            ) ?: temperature.toFahrenheit()
                            listener.onHvacTemperatureUpdated(temperature, fahrenheit)
                        },
                        onFailed = {
                            listener.onHvacTemperatureUpdated(temperature, temperature.toFahrenheit())
                        }
                    )
                }

                override fun onFailed(e: Exception) {
                    listener.onFailed(e)
                }
            }.also {
                isNewRegistration = true
                hvacTemperatureValueListenerMap[key] = it
            }
        }

        if (!isNewRegistration) return

        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_TEMPERATURE_SET,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = wrapper
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun unregisterHvacTemperature(area: SeatArea, listener: HvacTemperatureListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_TEMPERATURE_SET,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun unregisterHvacTemperature(area: SeatArea, listener: HvacTemperatureValueListener) {
        val areaId = ImplementUtil.getAreaId(area)
        val key = HvacTemperatureValueListenerKey(areaId = areaId, listener = listener)
        val wrapper = synchronized(hvacTemperatureValueListenerMap) {
            hvacTemperatureValueListenerMap.remove(key)
        } ?: return

        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_TEMPERATURE_SET,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = wrapper
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun registerHvacFanSpeed(area: SeatArea, listener: HvacFanSpeedListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_FAN_SPEED,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun unregisterHvacFanSpeed(area: SeatArea, listener: HvacFanSpeedListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_FAN_SPEED,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun registerHvacRecirculationState(area: SeatArea, listener: HvacRecirculationStateListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_RECIRC_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_CLIMATE)
    override fun unregisterHvacRecirculationState(area: SeatArea, listener: HvacRecirculationStateListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.HVAC_RECIRC_ON,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }
}
