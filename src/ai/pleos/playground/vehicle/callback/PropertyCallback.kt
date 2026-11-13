package ai.pleos.playground.vehicle.callback


import ai.pleos.playground.vehicle.common.catching
import ai.pleos.playground.vehicle.common.fromJson
import ai.pleos.playground.vehicle.constant.PleosPropertyIds
import ai.pleos.playground.vehicle.constant.area.TurnSignalValue
import ai.pleos.playground.vehicle.constant.control.CruiseControl
import ai.pleos.playground.vehicle.constant.control.DrivingModeType
import ai.pleos.playground.vehicle.constant.control.LightState
import ai.pleos.playground.vehicle.constant.control.OilLevelState
import ai.pleos.playground.vehicle.constant.control.VehicleGear
import ai.pleos.playground.vehicle.constant.control.WiperState
import ai.pleos.playground.vehicle.constant.unit.TemperatureUnit
import ai.pleos.playground.vehicle.listener.AmbientColorAutoByDriveModeEnabledListener
import ai.pleos.playground.vehicle.listener.AmbientLightBrightnessColorListener
import ai.pleos.playground.vehicle.listener.AmbientLightBrightnessListener
import ai.pleos.playground.vehicle.listener.BrakePedalStatusListener
import ai.pleos.playground.vehicle.listener.CruiseControlStateListener
import ai.pleos.playground.vehicle.listener.CruiseControlTargetSpeedListener
import ai.pleos.playground.vehicle.listener.DistanceDrivenListener
import ai.pleos.playground.vehicle.listener.DoorLockStateListener
import ai.pleos.playground.vehicle.listener.DoorPositionListener
import ai.pleos.playground.vehicle.listener.DrivingModeStateListener
import ai.pleos.playground.vehicle.listener.DrivingTimeListener
import ai.pleos.playground.vehicle.listener.EtcsVoiceGuidanceVolumeListener
import ai.pleos.playground.vehicle.listener.EvBatteryEfficiencyListener
import ai.pleos.playground.vehicle.listener.EvBatteryLevelListener
import ai.pleos.playground.vehicle.listener.EvBatteryStateOfChargeListener
import ai.pleos.playground.vehicle.listener.EvChargePortOpenListener
import ai.pleos.playground.vehicle.listener.FuelEconomyListener
import ai.pleos.playground.vehicle.listener.FuelPercentListener
import ai.pleos.playground.vehicle.listener.GearSelectionListener
import ai.pleos.playground.vehicle.listener.GloveBoxLockStateListener
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
import ai.pleos.playground.vehicle.listener.HvacSeatTemperatureListener
import ai.pleos.playground.vehicle.listener.HvacSeatVentilationListener
import ai.pleos.playground.vehicle.listener.HvacSideMirrorHeatStateListener
import ai.pleos.playground.vehicle.listener.HvacSteeringWheelHeatStateListener
import ai.pleos.playground.vehicle.listener.HvacTemperatureListener
import ai.pleos.playground.vehicle.listener.HvacTemperatureValueListener
import ai.pleos.playground.vehicle.listener.LightStateListener
import ai.pleos.playground.vehicle.listener.LowWasherFluidWaringListener
import ai.pleos.playground.vehicle.listener.OilLevelStateListener
import ai.pleos.playground.vehicle.listener.PowerDownWarningIndicatorListener
import ai.pleos.playground.vehicle.listener.RangeRemainingListener
import ai.pleos.playground.vehicle.listener.SeatAirbagDeployedListener
import ai.pleos.playground.vehicle.listener.SeatAirbagEnabledListener
import ai.pleos.playground.vehicle.listener.SeatBackrestAnglePositionListener
import ai.pleos.playground.vehicle.listener.SeatBeltBuckledStateListener
import ai.pleos.playground.vehicle.listener.SeatBeltHeightPositionListener
import ai.pleos.playground.vehicle.listener.SeatBeltNotBuckledWarningListener
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
import ai.pleos.playground.vehicle.listener.SmartGlassLevelListener
import ai.pleos.playground.vehicle.listener.SmartGlassStateListener
import ai.pleos.playground.vehicle.listener.SteeringWheelAngleListener
import ai.pleos.playground.vehicle.listener.TelltaleInfoListener
import ai.pleos.playground.vehicle.listener.TemperatureDisplayUnitListener
import ai.pleos.playground.vehicle.listener.TirePressureListener
import ai.pleos.playground.vehicle.listener.TripDrivingDistanceListener
import ai.pleos.playground.vehicle.listener.TripDrivingTimeListener
import ai.pleos.playground.vehicle.listener.TripEvEfficiencyListener
import ai.pleos.playground.vehicle.listener.TripFuelEconomyListener
import ai.pleos.playground.vehicle.listener.TurnSignalStateListener
import ai.pleos.playground.vehicle.listener.VehicleListener
import ai.pleos.playground.vehicle.listener.WindowLockStateListener
import ai.pleos.playground.vehicle.listener.WindowPositionListener
import ai.pleos.playground.vehicle.listener.WiperStateListener
import ai.pleos.playground.vehicle.utils.Log
import ai.pleos.playground.vehicle.utils.StringUtils
import ai.pleos.playground.vehicle.utils.TelltaleUtil
import ai.pleos.playground.vehicle.utils.TripStatisticsUtil
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

private typealias PropertyNameToAreaType = Pair<String, Int>

internal class PropertyCallback(
    private val register: (PleosPropertyIds, Int) -> Unit,
    private val unregister: (PleosPropertyIds, Int) -> Unit,
) : IPropertyCallback.Stub() {
    private val tag by lazyOf(StringUtils.TAG(this.javaClass.simpleName))

    private val userVehicleListenerMap: ConcurrentHashMap<String, ConcurrentLinkedQueue<VehicleListener>> by lazyOf(
        ConcurrentHashMap()
    )
    private val _requestedRegistrations: ConcurrentHashMap<String, MutableSet<Int>> by lazyOf(
        ConcurrentHashMap()
    )
    val requestedRegistrations: List<PropertyNameToAreaType>
        get() = _requestedRegistrations.flatMap { (key, values) ->
            values.map { key to it }
        }

    companion object {
        private const val USER_LISTENER_KEY_DIVIDER = "and"
    }

    fun addListener(
        propId: PleosPropertyIds,
        areaType: Int,
        areaId: Int,
        listener: VehicleListener
    ) {
        Log.d(tag, "addListener: $listener")
        if (isEmptyPleosPropertyId(propId)) {
            register(propId, areaType)
        }

        addUserVehicleListener(propId, areaId, listener)
    }

    fun removeListener(
        propId: PleosPropertyIds,
        areaType: Int,
        areaId: Int,
        listener: VehicleListener
    ) {
        Log.d(tag, "removeListener: $listener")
        removeUserVehicleListener(propId, areaId, listener)

        if (isEmptyPleosPropertyId(propId)) {
            unregister(propId, areaType)
        }
    }

    fun clearAllListeners() {
        Log.d(tag, "clearAllListeners() called")
        userVehicleListenerMap.clear()
        _requestedRegistrations.clear()
    }

    override fun onChangeEvent(propIdName: String, areaId: Int, value: String?) {
        Log.d(
            tag,
            "onChangeEvent() called with: propIdName = $propIdName, areaId = $areaId, value = $value"
        )
        val key = getUserListenerKey(propIdName, areaId)
        val listeners = userVehicleListenerMap[key] ?: return

        for (listener in listeners) {
            notifyListenerValue(propIdName, value, listener)
        }
    }

    internal fun onChangeEvent(
        propIdName: String,
        areaId: Int,
        value: String?,
        listener: VehicleListener
    ) {
        Log.d(
            tag,
            "onChangeEvent(single) called with: propIdName = $propIdName, areaId = $areaId, value = $value, listener = $listener"
        )
        if (!hasListener(propIdName, areaId, listener)) return
        notifyListenerValue(propIdName, value, listener)
    }

    override fun onErrorEvent(propIdName: String, exceString: String?) {
        catching("onErrorEvent") {
            val exception = toException(exceString)

            userVehicleListenerMap
                .filterKeys { it.startsWith(propIdName) }
                .flatMap { it.value }
                .forEach { it?.onFailed(exception) }
        }.onFailure {
            Log.e(tag, "onErrorEvent Failure! $it")
        }
    }

    internal fun onErrorEvent(
        propIdName: String,
        exceString: String?,
        listener: VehicleListener
    ) {
        catching("onErrorEvent(single)") {
            Log.d(tag, "onErrorEvent(single) called with: propIdName = $propIdName, error = $exceString, listener = $listener")
            if (!hasListener(propIdName, null, listener)) return
            listener.onFailed(toException(exceString))
        }.onFailure {
            Log.e(tag, "onErrorEvent(single) Failure! $it")
        }
    }

    fun saveRequest(propIdName: String, areaType: Int) {
        Log.d(tag, "saveRequest() called with: propIdName = $propIdName, areaType = $areaType")
        _requestedRegistrations.getOrPut(propIdName) { mutableSetOf() }.add(areaType)
    }

    fun removeRequest(propIdName: String, areaType: Int) {
        Log.d(tag, "removeRequest() called with: propIdName = $propIdName, areaType = $areaType")
        _requestedRegistrations.getOrPut(propIdName) { mutableSetOf() }.remove(areaType)
    }

    internal fun isEmptyPleosPropertyId(propId: PleosPropertyIds): Boolean {
        var count = 0
        userVehicleListenerMap
            .filterKeys { it.startsWith(propId.name) }
            .map { it.value }
            .forEach { count += it.size }

        Log.d(tag, "hasPropertyIdCount : $count")
        return count == 0
    }

    private fun addUserVehicleListener(
        propId: PleosPropertyIds,
        areaId: Int,
        listener: VehicleListener
    ) {
        val key = getUserListenerKey(propId.name, areaId)
        val listeners = userVehicleListenerMap.getOrPut(key) {
            ConcurrentLinkedQueue()
        }

        if (listeners.contains(listener).not()) {
            listeners.add(listener)
        }
    }

    private fun removeUserVehicleListener(
        propId: PleosPropertyIds,
        areaId: Int,
        listener: VehicleListener
    ) {
        val key = getUserListenerKey(propId.name, areaId)
        val listeners = userVehicleListenerMap.getOrPut(key) {
            ConcurrentLinkedQueue()
        }

        if (listeners.contains(listener)) {
            listeners.remove(listener)
        }
    }

    private fun getUserListenerKey(propIdName: String, areaId: Int): String {
        return propIdName + USER_LISTENER_KEY_DIVIDER + areaId
    }

    private fun hasListener(
        propIdName: String,
        areaId: Int?,
        listener: VehicleListener
    ): Boolean {
        return if (areaId != null) {
            userVehicleListenerMap[getUserListenerKey(propIdName, areaId)]?.contains(listener) == true
        } else {
            userVehicleListenerMap
                .filterKeys { it.startsWith(propIdName) }
                .values
                .any { it.contains(listener) }
        }
    }

    private fun notifyListenerValue(
        propIdName: String,
        value: String?,
        listener: VehicleListener
    ) {
        catching("onChangeEvent") {
            when (listener) {

                is EvBatteryLevelListener -> {
                    listener.onEvBatteryLevelUpdated(value?.toFloat())
                }

                is EvBatteryStateOfChargeListener -> {
                    listener.onEvBatteryStateOfChargeUpdated(value?.toInt())
                }

                is FuelPercentListener -> {
                    listener.onFuelPercentUpdated(value?.toInt())
                }

                is DoorPositionListener -> {
                    listener.onPositionUpdated(value?.toInt())
                }

                is GearSelectionListener -> {
                    listener.onSelected(VehicleGear.findById(value?.toInt()))
                }

                is RangeRemainingListener -> {
                    listener.onRangeRemainingChanged(value?.toFloat())
                }

                is TirePressureListener -> {
                    listener.onTirePressureUpdated(value?.toFloat())
                }

                is SteeringWheelAngleListener -> {
                    listener.onSteeringWheelAngleUpdated(value?.toFloat())
                }

                is WiperStateListener -> {
                    listener.onWiperStateUpdated(WiperState.findById(value?.toInt()))
                }

                is BrakePedalStatusListener -> {
                    listener.onBrakePedalStatusUpdated(value?.toBoolean())
                }

                is WindowLockStateListener -> {
                    listener.onWindowLockStateUpdated(value?.toBoolean())
                }

                is WindowPositionListener -> {
                    listener.onWindowPositionUpdated(value?.toInt())
                }

                is TurnSignalStateListener -> {
                    listener.onTurnSignalStateUpdated(TurnSignalValue.findById(value?.toInt()))
                }

                is HvacSteeringWheelHeatStateListener -> {
                    listener.onHvacSteeringWheelHeatStateUpdated(value?.toInt())
                }

                is TemperatureDisplayUnitListener -> {
                    listener.onTemperatureDisplayUnitUpdated(TemperatureUnit.findById(value?.toInt()))
                }

                is DoorLockStateListener -> {
                    listener.onDoorLockStateUpdated(value?.toBoolean())
                }

                is GloveBoxLockStateListener -> {
                    listener.onGloveBoxLockStateUpdated(value?.toBoolean())
                }

                is SeatOccupancyListener -> {
                    listener.onSeatOccupancyUpdated(value?.toInt() == 1)
                }

                is SeatBackrestAnglePositionListener -> {
                    listener.onSeatBackrestAnglePositionUpdated(value?.toInt())
                }

                is SeatBeltBuckledStateListener -> {
                    listener.onSeatBeltBuckledStateUpdated(value?.toBoolean())
                }

                is SeatBeltHeightPositionListener -> {
                    listener.onSeatBeltHeightPositionUpdated(value?.toInt())
                }

                is SeatDepthPositionListener -> {
                    listener.onSeatDepthPositionUpdated(value?.toInt())
                }

                is SeatForeAftPositionListener -> {
                    listener.onSeatForeAftPositionUpdated(value?.toInt())
                }

                is SeatHeadrestAnglePositionListener -> {
                    listener.onSeatHeadrestAnglePositionUpdated(value?.toInt())
                }

                is SeatHeadrestForeAftPositionListener -> {
                    listener.onSeatHeadrestForeAftPositionUpdated(value?.toInt())
                }

                is SeatHeadrestHeightAftPositionListener -> {
                    listener.onSeatHeadrestHeightAftPositionUpdated(value?.toInt())
                }

                is SeatHeightPositionListener -> {
                    listener.onSeatHeightPositionUpdated(value?.toInt())
                }

                is SeatLumbarForeAftPositionListener -> {
                    listener.onSeatLumbarForeAftPositionUpdated(value?.toInt())
                }

                is SeatLumbarSideSupportPositionListener -> {
                    listener.onSeatLumbarSideSupportPositionUpdated(value?.toInt())
                }

                is SeatTiltPositionListener -> {
                    listener.onSeatTiltPositionUpdated(value?.toInt())
                }

                is HvacSeatTemperatureListener -> {
                    listener.onHvacSeatTemperatureUpdated(value?.toInt())
                }

                is HvacSeatVentilationListener -> {
                    listener.onHvacSeatVentilationUpdated(value?.toInt())
                }

                is PowerDownWarningIndicatorListener -> {
                    listener.onPowerDownWarningIndicatorUpdated(value?.toBoolean())
                }

                is SeatAirbagEnabledListener -> {
                    listener.onSeatAirbagEnabledUpdated(value?.toBoolean())
                }

                is SeatBeltNotBuckledWarningListener -> {
                    listener.onSeatBeltNotBuckledWarningUpdated(value?.toBoolean())
                }

                is DistanceDrivenListener -> {
                    listener.onDistanceDrivenUpdated(value?.toFloat())
                }

                is TripDrivingTimeListener -> {
                    listener.onTripDrivingTimeUpdated(
                        TripStatisticsUtil.parseIntStatistics(propIdName, value)
                    )
                }

                is TripDrivingDistanceListener -> {
                    listener.onTripDrivingDistanceUpdated(
                        TripStatisticsUtil.parseFloatStatistics(propIdName, value)
                    )
                }

                is DrivingTimeListener -> {
                    listener.onDrivingTimeUpdated(value?.toInt())
                }

                is HvacAcStateListener -> {
                    listener.onHvacAcStateUpdated(value?.toBoolean())
                }

                is HvacAirCleanerStateListener -> {
                    listener.onHvacAirCleanerStateUpdated(value?.toBoolean())
                }

                is HvacRecirculationStateListener -> {
                    listener.onHvacAutoRecirculationStateUpdated(value?.toBoolean())
                }

                is HvacAutoStateListener -> {
                    listener.onHvacAutoStateUpdated(value?.toBoolean())
                }

                is HvacDefrosterStateListener -> {
                    listener.onHvacDefrosterStateUpdated(value?.toBoolean())
                }

                is HvacDualStateListener -> {
                    listener.onHvacDualStateUpdated(value?.toBoolean())
                }

                is HvacFanSpeedListener -> {
                    listener.onHvacFanSpeedUpdated(value?.toInt())
                }

                is HvacMaxAcStateListener -> {
                    listener.onHvacMaxAcStateUpdated(value?.toBoolean())
                }

                is HvacMaxDefrostStateListener -> {
                    listener.onHvacMaxDefrostStateUpdated(value?.toBoolean())
                }

                is HvacPowerStateListener -> {
                    listener.onHvacPowerStateUpdated(value?.toBoolean())
                }

                is HvacTemperatureListener -> {
                    listener.onHvacTemperatureUpdated(value?.toFloat())
                }

                is HvacTemperatureValueListener -> {
                    // This listener is wrapped and handled in HVACImpl before reaching PropertyCallback.
                }

                is LightStateListener -> {
                    listener.onLightStateUpdated(LightState.findById(value?.toInt()))
                }

                is EvChargePortOpenListener -> {
                    listener.onEvChargePortOpenUpdated(value?.toBoolean())
                }

                is HvacSideMirrorHeatStateListener -> {
                    listener.onHvacSideMirrorHeatStateUpdated(value?.toInt())
                }

                is CruiseControlTargetSpeedListener -> {
                    listener.onCruiseControlTargetSpeedUpdated(value?.toFloat())
                }

                is CruiseControlStateListener -> {
                    listener.onCruiseControlStateUpdated(CruiseControl.findById(value?.toInt()))
                }

                is DrivingModeStateListener -> {
                    listener.onDrivingModeStateUpdated(DrivingModeType.findById(value?.toInt()))
                }

                is LowWasherFluidWaringListener -> {
                    listener.onLowWasherFluidWaringUpdated(value?.toBoolean())
                }

                is OilLevelStateListener -> {
                    listener.onOilLevelUpdated(OilLevelState.findById(value?.toInt()))
                }

                is SeatAirbagDeployedListener -> {
                    listener.onSeatAirbagDeployUpdated(value?.toBoolean())
                }

                is TelltaleInfoListener -> {
                    listener.onTelltaleInfoUpdated(TelltaleUtil.parseTelltales(value))
                }

                is AmbientLightBrightnessColorListener -> {
                    listener.onBrightnessColorUpdated(value?.toInt())
                }

                is AmbientLightBrightnessListener -> {
                    listener.onBrightnessUpdated(value?.toInt())
                }

                is SmartGlassLevelListener -> {
                    listener.onSmartGlassLevelUpdated(value?.toInt())
                }

                is EvBatteryEfficiencyListener -> {
                    listener.onEvBatteryEfficiencyUpdated(value?.toFloat())
                }

                is TripEvEfficiencyListener -> {
                    listener.onTripEvEfficiencyUpdated(
                        TripStatisticsUtil.parseFloatStatistics(propIdName, value)
                    )
                }

                is FuelEconomyListener -> {
                    listener.onFuelEconomyUpdated(value?.toFloat())
                }

                is TripFuelEconomyListener -> {
                    listener.onTripFuelEconomyUpdated(
                        TripStatisticsUtil.parseFloatStatistics(propIdName, value)
                    )
                }

                is SmartGlassStateListener -> {
                    listener.onSmartGlassStateUpdated(value?.toBoolean())
                }

                is EtcsVoiceGuidanceVolumeListener -> {
                    listener.onEtcsVoiceGuidanceVolumeUpdated(value?.toInt())
                }

                is AmbientColorAutoByDriveModeEnabledListener -> {
                    listener.onAmbientColorAutoByDriveModeEnabledUpdated(value?.toBoolean())
                }
            }
        }.onFailure {
            val exception = if (it is Exception) it else Exception(it.localizedMessage, it)
            listener.onFailed(exception)
        }
    }

    private fun toException(exceString: String?): Exception {
        return runCatching {
            exceString.fromJson<Exception>()
        }.getOrElse {
            Exception(exceString ?: "Unknown error")
        }
    }
}
