package ai.pleos.playground.vehicle.api.implement

import ai.pleos.playground.vehicle.api.EvBattery
import ai.pleos.playground.vehicle.api.model.capability.AccessType
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.common.catching
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.PleosPropertyIds
import ai.pleos.playground.vehicle.constant.VehicleAreaCustomType
import ai.pleos.playground.vehicle.constant.VehicleAreaValue
import ai.pleos.playground.vehicle.constant.control.TripStatisticType
import ai.pleos.playground.vehicle.listener.EvBatteryEfficiencyListener
import ai.pleos.playground.vehicle.listener.EvBatteryLevelListener
import ai.pleos.playground.vehicle.listener.EvBatteryStateOfChargeListener
import ai.pleos.playground.vehicle.listener.EvChargePortOpenListener
import ai.pleos.playground.vehicle.listener.TripEvEfficiencyListener
import ai.pleos.playground.vehicle.permission.RequiresCustomPermission
import ai.pleos.playground.vehicle.service.VehicleServiceManager
import ai.pleos.playground.vehicle.utils.CapabilityUtil
import ai.pleos.playground.vehicle.utils.TripStatisticsUtil
import android.annotation.SuppressLint

@SuppressLint("MissingPermission")
@Suppress("OVERRIDE_DEPRECATION")
internal class EvBatteryImpl(private val serviceManager: VehicleServiceManager) : EvBattery {
    private val tripCurrentEvEfficiencyListenerMap = mutableMapOf<EvBatteryEfficiencyListener, TripEvEfficiencyListener>()

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    private fun requestTripEvEfficiencyCapability(
        tripStatisticType: TripStatisticType? = null,
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestCapability(
            instance = this,
            propId = PleosPropertyIds.TRIP_EV_EFFICIENCY,
            action = { ret ->
                TripStatisticsUtil.updateConfigArray(
                    propertyKey = PleosPropertyIds.TRIP_EV_EFFICIENCY.name,
                    configArray = ret.configArray
                )
                onComplete(
                    if (tripStatisticType != null &&
                        !TripStatisticsUtil.supportsType(PleosPropertyIds.TRIP_EV_EFFICIENCY.name, tripStatisticType)
                    ) {
                        CapabilityConfig(AccessType.NONE)
                    } else {
                        CapabilityConfig(CapabilityUtil.getAccess(ret))
                    }
                )
            },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun checkInstantaneousEvEfficiencyCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAccessCapability(
            instance = this,
            propId = PleosPropertyIds.INSTANTANEOUS_EV_EFFICIENCY,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun checkSinceChargeEvEfficiencyCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAccessCapability(
            instance = this,
            propId = PleosPropertyIds.SINCE_CHARGE_EV_EFFICIENCY,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun checkCumulativeEvEfficiencyCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAccessCapability(
            instance = this,
            propId = PleosPropertyIds.CUMULATIVE_EV_EFFICIENCY,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun checkCurrentEvEfficiencyCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) = requestTripEvEfficiencyCapability(
        tripStatisticType = TripStatisticType.CURRENT,
        onComplete = onComplete,
        onFailed = onFailed
    )

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun checkEvBatteryStateOfChargeCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAccessCapability(
            instance = this,
            propId = PleosPropertyIds.EV_BATTERY_SOC,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }


    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun checkEvChargeCurrentDrawLimitCapabilities(
        onComplete: (List<Float>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkEvChargeCurrentDrawLimitCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun checkEvChargeCurrentDrawLimitCapabilities(
        onComplete: (CapabilityConfig, List<Float>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val propId = PleosPropertyIds.EV_CHARGE_CURRENT_DRAW_LIMIT
        serviceManager.requestCapability(instance = this, propId, { ret ->
            val config = CapabilityUtil.getGlobalAreaConfig<Nothing>(ret)
            val list = mutableListOf<Float>()
            catching {
                list.addAll(CapabilityUtil.convertList(config?.supportedValues) {
                    it.toFloat()
                })
            }
                .onSuccess { onComplete(CapabilityConfig(CapabilityUtil.getAccess(ret)), list) }
                .onFailure { onFailed(Exception(it)) }

        }, onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun checkEvChargePercentLimitCapabilities(
        onComplete: (CapabilityConfig, List<Float>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val propId = PleosPropertyIds.EV_CHARGE_PERCENT_LIMIT
        serviceManager.requestCapability(instance = this, propId, { ret ->
            val config = CapabilityUtil.getGlobalAreaConfig<Nothing>(ret)
            val list = mutableListOf<Float>()
            catching {
                list.addAll(CapabilityUtil.convertList(config?.supportedValues) {
                    it.toFloat()
                })
            }
                .onSuccess { onComplete(CapabilityConfig(CapabilityUtil.getAccess(ret)), list) }
                .onFailure { onFailed(Exception(it)) }

        }, onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun checkEvChargePortOpenCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAccessCapability(
            instance = this,
            propId = PleosPropertyIds.EV_CHARGE_PORT_OPEN,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun checkEvChargeSwitchCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAccessCapability(
            instance = this,
            propId = PleosPropertyIds.EV_CHARGE_SWITCH,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun getInstantaneousEvEfficiency(
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.INSTANTANEOUS_EV_EFFICIENCY,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(it?.toFloat()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun getSinceChargeEvEfficiency(
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.SINCE_CHARGE_EV_EFFICIENCY,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(it?.toFloat()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun getCumulativeEvEfficiency(
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.CUMULATIVE_EV_EFFICIENCY,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(it?.toFloat()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    private fun getTripEvEfficiencyValue(
        tripStatisticType: TripStatisticType,
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        requestTripEvEfficiencyCapability(
            onComplete = {
                serviceManager.getProperty(
                    propId = PleosPropertyIds.TRIP_EV_EFFICIENCY,
                    areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
                    areaId = VehicleAreaValue.DEFAULT,
                    action = {
                        onComplete(
                            TripStatisticsUtil.parseFloatStatistics(
                                PleosPropertyIds.TRIP_EV_EFFICIENCY.name,
                                it
                            )?.get(tripStatisticType)
                        )
                    },
                    onFailed = onFailed
                )
            },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun getCurrentEvEfficiency(
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    ) = getTripEvEfficiencyValue(TripStatisticType.CURRENT, onComplete, onFailed)

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun getEvBatteryStateOfCharge(
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.EV_BATTERY_SOC,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(it?.toInt()) },
            onFailed = onFailed
        )
    }

    @Deprecated(
        "This API is deprecated, use the Google EV_BATTERY_LEVEL property.",
        level = DeprecationLevel.WARNING
    )
    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun getEvBatteryLevel(onComplete: (Float?) -> Unit, onFailed: (Exception) -> Unit) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.EV_BATTERY_LEVEL,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(it?.toFloat()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_ENERGY)
    override fun setEvChargeCurrentDrawLimit(
        value: Float,
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.EV_CHARGE_CURRENT_DRAW_LIMIT,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            value = value,
            onComplete = { onComplete(value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun getEvChargeCurrentDrawLimit(
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.EV_CHARGE_CURRENT_DRAW_LIMIT,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(it?.toFloat()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_ENERGY)
    override fun setEvChargePercentLimit(
        value: Float,
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.EV_CHARGE_PERCENT_LIMIT,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            value = value,
            onComplete = { onComplete(value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_ENERGY)
    override fun setEvChargePortOpen(
        value: Boolean,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.EV_CHARGE_PORT_OPEN,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            value = value,
            onComplete = { onComplete(value) },
            onFailed = onFailed
        )
    }

    @Deprecated(
        "This API is deprecated, use the Google EV_CHARGE_PORT_OPEN property.",
        level = DeprecationLevel.WARNING
    )
    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun isEvChargePortOpen(onComplete: (Boolean?) -> Unit, onFailed: (Exception) -> Unit) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.EV_CHARGE_PORT_OPEN,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(it?.toBoolean()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_ENERGY)
    override fun setEvChargeSwitch(
        value: Boolean,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.setPropertyAsync(
            instance = this,
            propId = PleosPropertyIds.EV_CHARGE_SWITCH,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            value = value,
            onComplete = { onComplete(value) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun getEvChargeSwitchState(
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.EV_CHARGE_SWITCH,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(it?.toBoolean()) },
            onFailed = onFailed
        )
    }

    @Deprecated(
        "This API is deprecated, use the Google INFO_EV_BATTERY_CAPACITY property.",
        level = DeprecationLevel.WARNING
    )
    @RequiresCustomPermission(PleosPermission.CAR_INFO)
    override fun getEvBatteryCapacity(onComplete: (Float?) -> Unit, onFailed: (Exception) -> Unit) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.INFO_EV_BATTERY_CAPACITY,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(it?.toFloat()) },
            onFailed = onFailed
        )
    }
    // --------- register ---------------

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun registerInstantaneousEvEfficiency(listener: EvBatteryEfficiencyListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.INSTANTANEOUS_EV_EFFICIENCY,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun unregisterInstantaneousEvEfficiency(listener: EvBatteryEfficiencyListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.INSTANTANEOUS_EV_EFFICIENCY,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun registerSinceChargeEvEfficiency(listener: EvBatteryEfficiencyListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.SINCE_CHARGE_EV_EFFICIENCY,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun unregisterSinceChargeEvEfficiency(listener: EvBatteryEfficiencyListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.SINCE_CHARGE_EV_EFFICIENCY,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun registerCumulativeEvEfficiency(listener: EvBatteryEfficiencyListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.CUMULATIVE_EV_EFFICIENCY,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun unregisterCumulativeEvEfficiency(listener: EvBatteryEfficiencyListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.CUMULATIVE_EV_EFFICIENCY,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    private fun registerTripEvEfficiencyValue(
        tripStatisticType: TripStatisticType,
        listener: EvBatteryEfficiencyListener,
        listenerMap: MutableMap<EvBatteryEfficiencyListener, TripEvEfficiencyListener>
    ) {
        val wrapper = listenerMap.getOrPut(listener) {
            object : TripEvEfficiencyListener {
                override fun onTripEvEfficiencyUpdated(statistics: ai.pleos.playground.vehicle.api.model.common.TripStatistics<Float>?) {
                    listener.onEvBatteryEfficiencyUpdated(statistics?.get(tripStatisticType))
                }

                override fun onFailed(e: Exception) {
                    listener.onFailed(e)
                }
            }
        }

        requestTripEvEfficiencyCapability(
            onComplete = {
                serviceManager.addListener(
                    propId = PleosPropertyIds.TRIP_EV_EFFICIENCY,
                    areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
                    areaId = VehicleAreaValue.DEFAULT,
                    listener = wrapper
                )
            },
            onFailed = { listener.onFailed(it) }
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    private fun unregisterTripEvEfficiencyValue(
        listener: EvBatteryEfficiencyListener,
        listenerMap: MutableMap<EvBatteryEfficiencyListener, TripEvEfficiencyListener>
    ) {
        val wrapper = listenerMap.remove(listener) ?: return

        serviceManager.removeListener(
            propId = PleosPropertyIds.TRIP_EV_EFFICIENCY,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = wrapper
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun registerCurrentEvEfficiency(listener: EvBatteryEfficiencyListener) {
        registerTripEvEfficiencyValue(TripStatisticType.CURRENT, listener, tripCurrentEvEfficiencyListenerMap)
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun unregisterCurrentEvEfficiency(listener: EvBatteryEfficiencyListener) {
        unregisterTripEvEfficiencyValue(listener, tripCurrentEvEfficiencyListenerMap)
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun registerEvBatteryStateOfCharge(listener: EvBatteryStateOfChargeListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.EV_BATTERY_SOC,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun unregisterEvBatteryStateOfCharge(listener: EvBatteryStateOfChargeListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.EV_BATTERY_SOC,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }


    @Deprecated(
        "This API is deprecated, use the Google EV_BATTERY_LEVEL property.",
        level = DeprecationLevel.WARNING
    )
    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun registerEvBatteryLevel(listener: EvBatteryLevelListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.EV_BATTERY_LEVEL,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @Deprecated(
        "This API is deprecated, use the Google EV_BATTERY_LEVEL property.",
        level = DeprecationLevel.WARNING
    )
    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun unregisterEvBatteryLevel(listener: EvBatteryLevelListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.EV_BATTERY_LEVEL,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @Deprecated(
        "This API is deprecated, use the Google EV_CHARGE_PORT_OPEN property.",
        level = DeprecationLevel.WARNING
    )
    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun registerEvChargePortOpen(listener: EvChargePortOpenListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.EV_CHARGE_PORT_OPEN,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    //
    @Deprecated(
        "This API is deprecated, use the Google EV_CHARGE_PORT_OPEN property.",
        level = DeprecationLevel.WARNING
    )
    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun unregisterEvChargePortOpen(listener: EvChargePortOpenListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.EV_CHARGE_PORT_OPEN,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }
}
