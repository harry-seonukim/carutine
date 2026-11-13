package ai.pleos.playground.vehicle.api.implement

import ai.pleos.playground.vehicle.api.Safety
import ai.pleos.playground.vehicle.api.model.area.SeatArea
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.api.model.telltale.Telltales
import ai.pleos.playground.vehicle.common.catching
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.PleosPropertyIds
import ai.pleos.playground.vehicle.constant.VehicleAreaCustomType
import ai.pleos.playground.vehicle.constant.VehicleAreaValue
import ai.pleos.playground.vehicle.constant.control.OilLevelState
import ai.pleos.playground.vehicle.listener.LowWasherFluidWaringListener
import ai.pleos.playground.vehicle.listener.OilLevelStateListener
import ai.pleos.playground.vehicle.listener.PowerDownWarningIndicatorListener
import ai.pleos.playground.vehicle.listener.SeatAirbagDeployedListener
import ai.pleos.playground.vehicle.listener.SeatAirbagEnabledListener
import ai.pleos.playground.vehicle.listener.SeatBeltNotBuckledWarningListener
import ai.pleos.playground.vehicle.listener.TelltaleInfoListener
import ai.pleos.playground.vehicle.permission.RequiresCustomPermission
import ai.pleos.playground.vehicle.service.VehicleServiceManager
import ai.pleos.playground.vehicle.utils.CapabilityUtil
import ai.pleos.playground.vehicle.utils.ImplementUtil
import ai.pleos.playground.vehicle.utils.TelltaleUtil
import android.annotation.SuppressLint

@SuppressLint("MissingPermission")
@Suppress("OVERRIDE_DEPRECATION")
internal class SafetyImpl(private val serviceManager: VehicleServiceManager) : Safety {

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun checkAirbagCapabilities(
        onComplete: (List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkAirbagCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun checkAirbagCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(
            instance = this,
            propId = PleosPropertyIds.SEAT_AIRBAG_ENABLED,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun checkAirbagDeployedCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(
            instance = this,
            propId = PleosPropertyIds.SEAT_AIRBAGS_DEPLOYED,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun checkSeatBuckledCapabilities(
        onComplete: (List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        checkSeatBuckledCapabilities(
            onComplete = { _, list -> onComplete(list) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun checkSeatBuckledCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(
            instance = this,
            propId = PleosPropertyIds.SEAT_BELT_BUCKLED,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun checkSeatNotBuckledWarningCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAreaListCapability(
            instance = this,
            propId = PleosPropertyIds.SEAT_BELT_NOT_BUCKLED_WARNING,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun checkEngineOilLevelStateCapabilities(
        onComplete: (CapabilityConfig, List<OilLevelState>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val propId = PleosPropertyIds.ENGINE_OIL_LEVEL
        serviceManager.requestCapability(instance = this, propId, { ret ->
            val config = CapabilityUtil.getGlobalAreaConfig<Nothing>(ret)

            catching {
                config ?: throw Exception(CapabilityUtil.FAILED_RETRIEVE_CAPABILITIES)
                val oilLevelStates = CapabilityUtil.convertList(config.supportedValues) { value ->
                    value.toInt().let { id -> OilLevelState.findById(id) }
                }
                onComplete(CapabilityConfig(CapabilityUtil.getAccess(ret)), oilLevelStates)
            }.onFailure { error ->
                onFailed(Exception(error))
            }
        }, onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun checkBrakeFluidLevelStateCapabilities(
        onComplete: (CapabilityConfig, List<OilLevelState>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val propId = PleosPropertyIds.BRAKE_FLUID_LEVEL
        serviceManager.requestCapability(instance = this, propId, { ret ->
            val config = CapabilityUtil.getGlobalAreaConfig<Nothing>(ret)

            catching {
                config ?: throw Exception(CapabilityUtil.FAILED_RETRIEVE_CAPABILITIES)
                val oilLevelStates = CapabilityUtil.convertList(config.supportedValues) { value ->
                    value.toInt().let { id -> OilLevelState.findById(id) }
                }
                onComplete(CapabilityConfig(CapabilityUtil.getAccess(ret)), oilLevelStates)
            }.onFailure { error ->
                onFailed(Exception(error))
            }
        }, onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun checkTransmissionOilLevelStateCapabilities(
        onComplete: (CapabilityConfig, List<OilLevelState>) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val propId = PleosPropertyIds.TRANSMISSION_OIL_LEVEL
        serviceManager.requestCapability(instance = this, propId, { ret ->
            val config = CapabilityUtil.getGlobalAreaConfig<Nothing>(ret)

            catching {
                config ?: throw Exception(CapabilityUtil.FAILED_RETRIEVE_CAPABILITIES)
                val oilLevelStates = CapabilityUtil.convertList(config.supportedValues) { value ->
                    value.toInt().let { id -> OilLevelState.findById(id) }
                }
                onComplete(CapabilityConfig(CapabilityUtil.getAccess(ret)), oilLevelStates)
            }.onFailure { error ->
                onFailed(Exception(error))
            }
        }, onFailed)
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun checkLowWasherFluidWarningStateCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAccessCapability(
            instance = this,
            propId = PleosPropertyIds.WASHER_FLUID_LOW,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun checkPowerDownWarningIndicatorCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAccessCapability(
            instance = this,
            propId = PleosPropertyIds.POWER_DOWN_WARNING_INDICATOR,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.PRIVATE_CAR_SAFETY)
    override fun checkTelltalesCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.requestAccessCapability(
            instance = this,
            propId = PleosPropertyIds.TELLTALES,
            onComplete = onComplete,
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun getEngineOilLevelState(
        onComplete: (OilLevelState?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.ENGINE_OIL_LEVEL,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(OilLevelState.findById(it?.toInt())) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun getBrakeFluidLevelState(
        onComplete: (OilLevelState?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.BRAKE_FLUID_LEVEL,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(OilLevelState.findById(it?.toInt())) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun getTransmissionOilLevelState(
        onComplete: (OilLevelState?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.TRANSMISSION_OIL_LEVEL,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(OilLevelState.findById(it?.toInt())) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun hasLowWasherFluidWarning(
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.WASHER_FLUID_LOW,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(it?.toBoolean()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun hasSeatAirbagBeenDeployed(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.SEAT_AIRBAGS_DEPLOYED,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toBoolean()) },
            onFailed = onFailed
        )
    }


    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun hasPowerDownWarningIndicator(
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.POWER_DOWN_WARNING_INDICATOR,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(it?.toBoolean()) },
            onFailed = onFailed
        )
    }


    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun hasSeatAirbagEnabled(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.SEAT_AIRBAG_ENABLED,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toBoolean()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun hasSeatBeltNotBuckledWarning(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit,
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.SEAT_BELT_NOT_BUCKLED_WARNING,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            action = { onComplete(it?.toBoolean()) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.PRIVATE_CAR_SAFETY)
    override fun getTelltalesInfo(
        onComplete: (telltales: Telltales) -> Unit,
        onFailed: (Exception) -> Unit
    ) {
        serviceManager.getProperty(
            instance = this,
            propId = PleosPropertyIds.TELLTALES,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            action = { onComplete(TelltaleUtil.parseTelltales(it)) },
            onFailed = onFailed
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun registerPowerDownWarningIndicator(listener: PowerDownWarningIndicatorListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.POWER_DOWN_WARNING_INDICATOR,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun unregisterPowerDownWarningIndicator(listener: PowerDownWarningIndicatorListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.POWER_DOWN_WARNING_INDICATOR,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun registerSeatAirbagEnabled(area: SeatArea, listener: SeatAirbagEnabledListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_AIRBAG_ENABLED,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun unregisterSeatAirbagEnabled(area: SeatArea, listener: SeatAirbagEnabledListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_AIRBAG_ENABLED,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun registerSeatBeltNotBuckledWarning(
        area: SeatArea,
        listener: SeatBeltNotBuckledWarningListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_BELT_NOT_BUCKLED_WARNING,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun unregisterSeatBeltNotBuckledWarning(
        area: SeatArea,
        listener: SeatBeltNotBuckledWarningListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_BELT_NOT_BUCKLED_WARNING,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun registerLowWasherFluidWaringState(listener: LowWasherFluidWaringListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.WASHER_FLUID_LOW,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun unregisterLowWasherFluidWaringState(listener: LowWasherFluidWaringListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.WASHER_FLUID_LOW,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun registerBrakeFluidLevelState(listener: OilLevelStateListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.BRAKE_FLUID_LEVEL,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun unregisterBrakeFluidLevelState(listener: OilLevelStateListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.BRAKE_FLUID_LEVEL,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun registerTransmissionOilLevelState(listener: OilLevelStateListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.TRANSMISSION_OIL_LEVEL,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun unregisterTransmissionOilLevelState(listener: OilLevelStateListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.TRANSMISSION_OIL_LEVEL,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun registerEngineOilLevelState(listener: OilLevelStateListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.ENGINE_OIL_LEVEL,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun unregisterEngineOilLevelState(listener: OilLevelStateListener) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.ENGINE_OIL_LEVEL,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }


    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun registerSeatAirbagDeployed(area: SeatArea, listener: SeatAirbagDeployedListener) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_AIRBAGS_DEPLOYED,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.CAR_SAFETY)
    override fun unregisterSeatAirbagDeployed(
        area: SeatArea,
        listener: SeatAirbagDeployedListener
    ) {
        val areaId = ImplementUtil.getAreaId(area)
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.SEAT_AIRBAGS_DEPLOYED,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_SEAT,
            areaId = areaId,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.PRIVATE_CAR_SAFETY)
    override fun registerTelltales(listener: TelltaleInfoListener) {
        serviceManager.addListener(
            instance = this,
            propId = PleosPropertyIds.TELLTALES,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }

    @RequiresCustomPermission(PleosPermission.PRIVATE_CAR_SAFETY)
    override fun unregisterTelltales(
        listener: TelltaleInfoListener
    ) {
        serviceManager.removeListener(
            instance = this,
            propId = PleosPropertyIds.TELLTALES,
            areaType = VehicleAreaCustomType.VEHICLE_AREA_TYPE_GLOBAL,
            areaId = VehicleAreaValue.DEFAULT,
            listener = listener
        )
    }
}
