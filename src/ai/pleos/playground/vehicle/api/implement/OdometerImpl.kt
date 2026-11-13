package ai.pleos.playground.vehicle.api.implement

import ai.pleos.playground.vehicle.api.Odometer
import ai.pleos.playground.vehicle.api.model.capability.AccessType
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.PleosPropertyIds
import ai.pleos.playground.vehicle.constant.VehicleAreaCustomType
import ai.pleos.playground.vehicle.constant.VehicleAreaValue
import ai.pleos.playground.vehicle.constant.control.TripStatisticType
import ai.pleos.playground.vehicle.listener.DistanceDrivenListener
import ai.pleos.playground.vehicle.listener.DrivingTimeListener
import ai.pleos.playground.vehicle.listener.RangeRemainingListener
import ai.pleos.playground.vehicle.listener.TripDrivingDistanceListener
import ai.pleos.playground.vehicle.listener.TripDrivingTimeListener
import ai.pleos.playground.vehicle.permission.RequiresCustomPermission
import ai.pleos.playground.vehicle.service.VehicleServiceManager
import ai.pleos.playground.vehicle.utils.CapabilityUtil
import ai.pleos.playground.vehicle.utils.TripStatisticsUtil

internal class OdometerImpl(private val serviceManager: VehicleServiceManager) : Odometer {
    private val tripCurrentDrivingTimeListenerMap = mutableMapOf<DrivingTimeListener, TripDrivingTimeListener>()
    private val tripCumulativeDrivingTimeListenerMap = mutableMapOf<DrivingTimeListener, TripDrivingTimeListener>()
    private val tripSinceRefillDrivingTimeListenerMap = mutableMapOf<DrivingTimeListener, TripDrivingTimeListener>()
    private val tripCurrentDrivingDistanceListenerMap = mutableMapOf<DistanceDrivenListener, TripDrivingDistanceListener>()
    private val tripInstantaneousDrivingDistanceListenerMap = mutableMapOf<DistanceDrivenListener, TripDrivingDistanceListener>()
    private val tripCumulativeDrivingDistanceListenerMap = mutableMapOf<DistanceDrivenListener, TripDrivingDistanceListener>()
    private val tripSinceRefillDrivingDistanceListenerMap = mutableMapOf<DistanceDrivenListener, TripDrivingDistanceListener>()

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    private fun requestTripDrivingTimeCapability(
        tripStatisticType: TripStatisticType? = null,
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestCapability(
            instance = this,
            propId = PleosPropertyIds.TRIP_DRIVING_TIME,
            action = { ret ->
                TripStatisticsUtil.updateConfigArray(
                    propertyKey = PleosPropertyIds.TRIP_DRIVING_TIME.name,
                    configArray = ret.configArray
                )
                onComplete(
                    if (tripStatisticType != null &&
                        !TripStatisticsUtil.supportsType(PleosPropertyIds.TRIP_DRIVING_TIME.name, tripStatisticType)
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

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    private fun requestTripDrivingDistanceCapability(
        tripStatisticType: TripStatisticType? = null,
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestCapability(
            instance = this,
            propId = PleosPropertyIds.TRIP_DRIVING_DISTANCE,
            action = { ret ->
                TripStatisticsUtil.updateConfigArray(
                    propertyKey = PleosPropertyIds.TRIP_DRIVING_DISTANCE.name,
                    configArray = ret.configArray
                )
                onComplete(
                    if (tripStatisticType != null &&
                        !TripStatisticsUtil.supportsType(PleosPropertyIds.TRIP_DRIVING_DISTANCE.name, tripStatisticType)
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

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun checkCurrentDistanceDrivenCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAccessCapability(
            instance = this,
            propId = PleosPropertyIds.PERF_ODOMETER,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }


    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun getCurrentDistanceDriven(
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.PERF_ODOMETER,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(it?.toFloat()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun checkTripDrivingTimeCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) = requestTripDrivingTimeCapability(onComplete = onComplete, onFailed = onFailed)

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun checkTripCurrentDrivingTimeCapabilities(onComplete: (CapabilityConfig) -> Unit, onFailed: (Exception) -> Unit) =
        requestTripDrivingTimeCapability(
            tripStatisticType = TripStatisticType.CURRENT,
            onComplete = onComplete,
            onFailed = onFailed
        )

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun checkTripCumulativeDrivingTimeCapabilities(onComplete: (CapabilityConfig) -> Unit, onFailed: (Exception) -> Unit) =
        requestTripDrivingTimeCapability(
            tripStatisticType = TripStatisticType.CUMULATIVE,
            onComplete = onComplete,
            onFailed = onFailed
        )

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun checkTripSinceRefillDrivingTimeCapabilities(onComplete: (CapabilityConfig) -> Unit, onFailed: (Exception) -> Unit) =
        requestTripDrivingTimeCapability(
            tripStatisticType = TripStatisticType.SINCE_REFILL,
            onComplete = onComplete,
            onFailed = onFailed
        )

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    private fun getTripDrivingTimeValue(
        tripStatisticType: TripStatisticType,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        requestTripDrivingTimeCapability(
            onComplete = {
                serviceManager.getProperty(
                    propId = PleosPropertyIds.TRIP_DRIVING_TIME,
                    areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
                    areaId = VehicleAreaValue.DEFAULT,
                    action = {
                        onComplete(
                            TripStatisticsUtil.parseIntStatistics(
                                PleosPropertyIds.TRIP_DRIVING_TIME.name,
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

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun getTripCurrentDrivingTime(onComplete: (Int?) -> Unit, onFailed: (Exception) -> Unit) =
        getTripDrivingTimeValue(TripStatisticType.CURRENT, onComplete, onFailed)

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun getTripCumulativeDrivingTime(onComplete: (Int?) -> Unit, onFailed: (Exception) -> Unit) =
        getTripDrivingTimeValue(TripStatisticType.CUMULATIVE, onComplete, onFailed)

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun getTripSinceRefillDrivingTime(onComplete: (Int?) -> Unit, onFailed: (Exception) -> Unit) =
        getTripDrivingTimeValue(TripStatisticType.SINCE_REFILL, onComplete, onFailed)

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun checkTripDrivingDistanceCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) = requestTripDrivingDistanceCapability(onComplete = onComplete, onFailed = onFailed)

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun checkTripCurrentDrivingDistanceCapabilities(onComplete: (CapabilityConfig) -> Unit, onFailed: (Exception) -> Unit) =
        requestTripDrivingDistanceCapability(
            tripStatisticType = TripStatisticType.CURRENT,
            onComplete = onComplete,
            onFailed = onFailed
        )

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    @Deprecated("This API is deprecated because it uses TripStatisticType.INSTANTANEOUS.")
    override fun checkTripInstantaneousDrivingDistanceCapabilities(onComplete: (CapabilityConfig) -> Unit, onFailed: (Exception) -> Unit) =
        requestTripDrivingDistanceCapability(
            tripStatisticType = TripStatisticType.INSTANTANEOUS,
            onComplete = onComplete,
            onFailed = onFailed
        )

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun checkTripCumulativeDrivingDistanceCapabilities(onComplete: (CapabilityConfig) -> Unit, onFailed: (Exception) -> Unit) =
        requestTripDrivingDistanceCapability(
            tripStatisticType = TripStatisticType.CUMULATIVE,
            onComplete = onComplete,
            onFailed = onFailed
        )

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun checkTripSinceRefillDrivingDistanceCapabilities(onComplete: (CapabilityConfig) -> Unit, onFailed: (Exception) -> Unit) =
        requestTripDrivingDistanceCapability(
            tripStatisticType = TripStatisticType.SINCE_REFILL,
            onComplete = onComplete,
            onFailed = onFailed
        )

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    private fun getTripDrivingDistanceValue(
        tripStatisticType: TripStatisticType,
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        requestTripDrivingDistanceCapability(
            onComplete = {
                serviceManager.getProperty(
                    propId = PleosPropertyIds.TRIP_DRIVING_DISTANCE,
                    areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
                    areaId = VehicleAreaValue.DEFAULT,
                    action = {
                        onComplete(
                            TripStatisticsUtil.parseFloatStatistics(
                                PleosPropertyIds.TRIP_DRIVING_DISTANCE.name,
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

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun getTripCurrentDrivingDistance(onComplete: (Float?) -> Unit, onFailed: (Exception) -> Unit) =
        getTripDrivingDistanceValue(TripStatisticType.CURRENT, onComplete, onFailed)

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    @Deprecated("This API is deprecated because it uses TripStatisticType.INSTANTANEOUS.")
    override fun getTripInstantaneousDrivingDistance(onComplete: (Float?) -> Unit, onFailed: (Exception) -> Unit) =
        getTripDrivingDistanceValue(TripStatisticType.INSTANTANEOUS, onComplete, onFailed)

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun getTripCumulativeDrivingDistance(onComplete: (Float?) -> Unit, onFailed: (Exception) -> Unit) =
        getTripDrivingDistanceValue(TripStatisticType.CUMULATIVE, onComplete, onFailed)

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun getTripSinceRefillDrivingDistance(onComplete: (Float?) -> Unit, onFailed: (Exception) -> Unit) =
        getTripDrivingDistanceValue(TripStatisticType.SINCE_REFILL, onComplete, onFailed)

    @Deprecated("This API is deprecated", level = DeprecationLevel.ERROR,
        replaceWith = ReplaceWith("throw IllegalStateException(\"This API is deprecated\")"),
    )
    @RequiresCustomPermission(PleosPermission.CONTROL_CAR_DRIVE)
    override fun setRangeRemainingDistance(
        value: Float,
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        throw IllegalStateException("This API is deprecated")
    }

    @Deprecated(
        "This API is deprecated, use the Google RANGE_REMAINING property.",
        level = DeprecationLevel.WARNING
    )
    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun getRangeRemainingDistance(
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.RANGE_REMAINING,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(it?.toFloat()) },
            onFailed = onFailed
        )
    }

    @Deprecated(
        "This API is deprecated, use the Google RANGE_REMAINING property.",
        level = DeprecationLevel.WARNING
    )
    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun registerRangeRemainingListener(listener: RangeRemainingListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.RANGE_REMAINING,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @Deprecated(
        "This API is deprecated, use the Google RANGE_REMAINING property.",
        level = DeprecationLevel.WARNING
    )
    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun unregisterRangeRemainingListener(listener: RangeRemainingListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.RANGE_REMAINING,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun registerDistanceDriven(listener: DistanceDrivenListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.PERF_ODOMETER,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun unregisterDistanceDriven(listener: DistanceDrivenListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.PERF_ODOMETER,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    private fun registerTripDrivingTimeValue(
        tripStatisticType: TripStatisticType,
        listener: DrivingTimeListener,
        listenerMap: MutableMap<DrivingTimeListener, TripDrivingTimeListener>
    ) {
        val wrapper = listenerMap.getOrPut(listener) {
            object : TripDrivingTimeListener {
                override fun onTripDrivingTimeUpdated(statistics: ai.pleos.playground.vehicle.api.model.common.TripStatistics<Int>?) {
                    listener.onDrivingTimeUpdated(statistics?.get(tripStatisticType))
                }

                override fun onFailed(e: Exception) {
                    listener.onFailed(e)
                }
            }
        }

        requestTripDrivingTimeCapability(
            onComplete = {
                serviceManager.addListener(
                    propId = PleosPropertyIds.TRIP_DRIVING_TIME,
                    areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
                    areaId = VehicleAreaValue.DEFAULT,
                    listener = wrapper
                )
            },
            onFailed = { listener.onFailed(it) }
        )
    }

    private fun unregisterTripDrivingTimeValue(
        listener: DrivingTimeListener,
        listenerMap: MutableMap<DrivingTimeListener, TripDrivingTimeListener>
    ) {
        val wrapper = listenerMap.remove(listener) ?: return

        serviceManager.removeListener(
            propId = PleosPropertyIds.TRIP_DRIVING_TIME,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = wrapper
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun registerTripCurrentDrivingTime(listener: DrivingTimeListener) {
        registerTripDrivingTimeValue(TripStatisticType.CURRENT, listener, tripCurrentDrivingTimeListenerMap)
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun unregisterTripCurrentDrivingTime(listener: DrivingTimeListener) {
        unregisterTripDrivingTimeValue(listener, tripCurrentDrivingTimeListenerMap)
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun registerTripCumulativeDrivingTime(listener: DrivingTimeListener) {
        registerTripDrivingTimeValue(TripStatisticType.CUMULATIVE, listener, tripCumulativeDrivingTimeListenerMap)
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun unregisterTripCumulativeDrivingTime(listener: DrivingTimeListener) {
        unregisterTripDrivingTimeValue(listener, tripCumulativeDrivingTimeListenerMap)
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun registerTripSinceRefillDrivingTime(listener: DrivingTimeListener) {
        registerTripDrivingTimeValue(TripStatisticType.SINCE_REFILL, listener, tripSinceRefillDrivingTimeListenerMap)
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun unregisterTripSinceRefillDrivingTime(listener: DrivingTimeListener) {
        unregisterTripDrivingTimeValue(listener, tripSinceRefillDrivingTimeListenerMap)
    }

    private fun registerTripDrivingDistanceValue(
        tripStatisticType: TripStatisticType,
        listener: DistanceDrivenListener,
        listenerMap: MutableMap<DistanceDrivenListener, TripDrivingDistanceListener>
    ) {
        val wrapper = listenerMap.getOrPut(listener) {
            object : TripDrivingDistanceListener {
                override fun onTripDrivingDistanceUpdated(statistics: ai.pleos.playground.vehicle.api.model.common.TripStatistics<Float>?) {
                    listener.onDistanceDrivenUpdated(statistics?.get(tripStatisticType))
                }

                override fun onFailed(e: Exception) {
                    listener.onFailed(e)
                }
            }
        }

        requestTripDrivingDistanceCapability(
            onComplete = {
                serviceManager.addListener(
                    propId = PleosPropertyIds.TRIP_DRIVING_DISTANCE,
                    areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
                    areaId = VehicleAreaValue.DEFAULT,
                    listener = wrapper
                )
            },
            onFailed = { listener.onFailed(it) }
        )
    }

    private fun unregisterTripDrivingDistanceValue(
        listener: DistanceDrivenListener,
        listenerMap: MutableMap<DistanceDrivenListener, TripDrivingDistanceListener>
    ) {
        val wrapper = listenerMap.remove(listener) ?: return

        serviceManager.removeListener(
            propId = PleosPropertyIds.TRIP_DRIVING_DISTANCE,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = wrapper
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun registerTripCurrentDrivingDistance(listener: DistanceDrivenListener) {
        registerTripDrivingDistanceValue(TripStatisticType.CURRENT, listener, tripCurrentDrivingDistanceListenerMap)
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun unregisterTripCurrentDrivingDistance(listener: DistanceDrivenListener) {
        unregisterTripDrivingDistanceValue(listener, tripCurrentDrivingDistanceListenerMap)
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    @Deprecated("This API is deprecated because it uses TripStatisticType.INSTANTANEOUS.")
    override fun registerTripInstantaneousDrivingDistance(listener: DistanceDrivenListener) {
        registerTripDrivingDistanceValue(TripStatisticType.INSTANTANEOUS, listener, tripInstantaneousDrivingDistanceListenerMap)
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    @Deprecated("This API is deprecated because it uses TripStatisticType.INSTANTANEOUS.")
    override fun unregisterTripInstantaneousDrivingDistance(listener: DistanceDrivenListener) {
        unregisterTripDrivingDistanceValue(listener, tripInstantaneousDrivingDistanceListenerMap)
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun registerTripCumulativeDrivingDistance(listener: DistanceDrivenListener) {
        registerTripDrivingDistanceValue(TripStatisticType.CUMULATIVE, listener, tripCumulativeDrivingDistanceListenerMap)
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun unregisterTripCumulativeDrivingDistance(listener: DistanceDrivenListener) {
        unregisterTripDrivingDistanceValue(listener, tripCumulativeDrivingDistanceListenerMap)
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun registerTripSinceRefillDrivingDistance(listener: DistanceDrivenListener) {
        registerTripDrivingDistanceValue(TripStatisticType.SINCE_REFILL, listener, tripSinceRefillDrivingDistanceListenerMap)
    }

    @RequiresCustomPermission(PleosPermission.CAR_DRIVE)
    override fun unregisterTripSinceRefillDrivingDistance(listener: DistanceDrivenListener) {
        unregisterTripDrivingDistanceValue(listener, tripSinceRefillDrivingDistanceListenerMap)
    }
}
