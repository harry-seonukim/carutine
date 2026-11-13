package ai.pleos.playground.vehicle.api.implement

import ai.pleos.playground.vehicle.api.Fuel
import ai.pleos.playground.vehicle.api.model.capability.AccessType
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.PleosPropertyIds
import ai.pleos.playground.vehicle.constant.VehicleAreaCustomType
import ai.pleos.playground.vehicle.constant.VehicleAreaValue
import ai.pleos.playground.vehicle.constant.control.TripStatisticType
import ai.pleos.playground.vehicle.listener.FuelEconomyListener
import ai.pleos.playground.vehicle.listener.FuelPercentListener
import ai.pleos.playground.vehicle.listener.TripFuelEconomyListener
import ai.pleos.playground.vehicle.permission.RequiresCustomPermission
import ai.pleos.playground.vehicle.service.VehicleServiceManager
import ai.pleos.playground.vehicle.utils.CapabilityUtil
import ai.pleos.playground.vehicle.utils.TripStatisticsUtil
import android.annotation.SuppressLint

@SuppressLint("MissingPermission")
internal class FuelImpl(private val serviceManager: VehicleServiceManager) : Fuel {
    private val tripCurrentFuelEconomyListenerMap = mutableMapOf<FuelEconomyListener, TripFuelEconomyListener>()

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    private fun requestTripFuelEconomyCapability(
        tripStatisticType: TripStatisticType? = null,
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestCapability(
            instance = this,
            propId = PleosPropertyIds.TRIP_FUEL_ECONOMY,
            action = { ret ->
                TripStatisticsUtil.updateConfigArray(
                    propertyKey = PleosPropertyIds.TRIP_FUEL_ECONOMY.name,
                    configArray = ret.configArray
                )
                onComplete(
                    if (tripStatisticType != null &&
                        !TripStatisticsUtil.supportsType(PleosPropertyIds.TRIP_FUEL_ECONOMY.name, tripStatisticType)
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
    override fun checkInstantaneousFuelEconomyCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAccessCapability(
            instance = this,
            propId = PleosPropertyIds.INSTANTANEOUS_FUEL_ECONOMY,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun checkSinceChargeFuelEconomyCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAccessCapability(
            instance = this,
            propId = PleosPropertyIds.SINCE_REFUEL_FUEL_ECONOMY,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun checkCumulativeFuelEconomyCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAccessCapability(
            instance = this,
            propId = PleosPropertyIds.CUMULATIVE_FUEL_ECONOMY,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun checkCurrentFuelEconomyCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) = requestTripFuelEconomyCapability(
        tripStatisticType = TripStatisticType.CURRENT,
        onComplete = onComplete,
        onFailed = onFailed
    )

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun checkFuelPercentCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAccessCapability(
            instance = this,
            propId = PleosPropertyIds.FUEL_PERCENT,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun getInstantaneousFuelEconomy(
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.INSTANTANEOUS_FUEL_ECONOMY,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(it?.toFloat()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun getSinceChargeFuelEconomy(
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.SINCE_REFUEL_FUEL_ECONOMY,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(it?.toFloat()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun getCumulativeFuelEconomy(
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.CUMULATIVE_FUEL_ECONOMY,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(it?.toFloat()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    private fun getTripFuelEconomyValue(
        tripStatisticType: TripStatisticType,
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        requestTripFuelEconomyCapability(
            onComplete = {
                serviceManager.getProperty(
                    propId = PleosPropertyIds.TRIP_FUEL_ECONOMY,
                    areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
                    areaId = VehicleAreaValue.DEFAULT,
                    action = {
                        onComplete(
                            TripStatisticsUtil.parseFloatStatistics(
                                PleosPropertyIds.TRIP_FUEL_ECONOMY.name,
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
    override fun getCurrentFuelEconomy(
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    ) = getTripFuelEconomyValue(TripStatisticType.CURRENT, onComplete, onFailed)

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun getFuelPercent(
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.FUEL_PERCENT,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(it?.toInt()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun registerInstantaneousFuelEconomy(listener: FuelEconomyListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.INSTANTANEOUS_FUEL_ECONOMY,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun unregisterInstantaneousFuelEconomy(listener: FuelEconomyListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.INSTANTANEOUS_FUEL_ECONOMY,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun registerSinceChargeFuelEconomy(listener: FuelEconomyListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.SINCE_REFUEL_FUEL_ECONOMY,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun unregisterSinceChargeFuelEconomy(listener: FuelEconomyListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.SINCE_REFUEL_FUEL_ECONOMY,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun registerCumulativeFuelEconomy(listener: FuelEconomyListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.CUMULATIVE_FUEL_ECONOMY,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun unregisterCumulativeFuelEconomy(listener: FuelEconomyListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.CUMULATIVE_FUEL_ECONOMY,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    private fun registerTripFuelEconomyValue(
        tripStatisticType: TripStatisticType,
        listener: FuelEconomyListener,
        listenerMap: MutableMap<FuelEconomyListener, TripFuelEconomyListener>
    ) {
        val wrapper = listenerMap.getOrPut(listener) {
            object : TripFuelEconomyListener {
                override fun onTripFuelEconomyUpdated(statistics: ai.pleos.playground.vehicle.api.model.common.TripStatistics<Float>?) {
                    listener.onFuelEconomyUpdated(statistics?.get(tripStatisticType))
                }

                override fun onFailed(e: Exception) {
                    listener.onFailed(e)
                }
            }
        }

        requestTripFuelEconomyCapability(
            onComplete = {
                serviceManager.addListener(
                    propId = PleosPropertyIds.TRIP_FUEL_ECONOMY,
                    areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
                    areaId = VehicleAreaValue.DEFAULT,
                    listener = wrapper
                )
            },
            onFailed = { listener.onFailed(it) }
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    private fun unregisterTripFuelEconomyValue(
        listener: FuelEconomyListener,
        listenerMap: MutableMap<FuelEconomyListener, TripFuelEconomyListener>
    ) {
        val wrapper = listenerMap.remove(listener) ?: return

        serviceManager.removeListener(
            propId = PleosPropertyIds.TRIP_FUEL_ECONOMY,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = wrapper
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun registerCurrentFuelEconomy(listener: FuelEconomyListener) {
        registerTripFuelEconomyValue(TripStatisticType.CURRENT, listener, tripCurrentFuelEconomyListenerMap)
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun unregisterCurrentFuelEconomy(listener: FuelEconomyListener) {
        unregisterTripFuelEconomyValue(listener, tripCurrentFuelEconomyListenerMap)
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun registerFuelPercent(listener: FuelPercentListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.FUEL_PERCENT,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_ENERGY)
    override fun unregisterFuelPercent(listener: FuelPercentListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.FUEL_PERCENT,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }


}
