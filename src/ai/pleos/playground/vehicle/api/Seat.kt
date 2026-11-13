package ai.pleos.playground.vehicle.api

import ai.pleos.playground.vehicle.api.model.area.SeatArea
import ai.pleos.playground.vehicle.api.model.capability.AreaBounds
import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.constant.control.MoveControl
import ai.pleos.playground.vehicle.listener.HvacSeatTemperatureListener
import ai.pleos.playground.vehicle.listener.HvacSeatVentilationListener
import ai.pleos.playground.vehicle.listener.SeatBackrestAnglePositionListener
import ai.pleos.playground.vehicle.listener.SeatBeltBuckledStateListener
import ai.pleos.playground.vehicle.listener.SeatBeltHeightPositionListener
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
import androidx.annotation.RequiresPermission

/**
 * Provides APIs for seat position, occupancy, seat belt, and seat climate.
 *
 * Obtain an instance from [ai.pleos.playground.vehicle.Vehicle.getSeat].
 */
interface Seat {

    @Deprecated(
        message = "Use the new checkSeatOccupancyCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkSeatOccupancyCapabilities({ capabilityConfig, list }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatOccupancyCapabilities(
        onComplete: (List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets seat occupancy capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[SeatArea]>: Callback invoked when the SeatOccupancy capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatOccupancyCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    @Deprecated(
        message = "Use the new checkSeatBeltBuckledCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkSeatBeltBuckledCapabilities({ capabilityConfig, list }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatBeltBuckledCapabilities(
        onComplete: (List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets seat belt buckled capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[SeatArea]>: Callback invoked when the SeatBeltBuckled capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatBeltBuckledCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )


    @Deprecated(
        "This API is deprecated, use checkSeatBackrestAnglePositionCapabilities instead.",
        replaceWith = ReplaceWith(
            "checkSeatBackrestAnglePositionCapabilities({ capabilityConfig, map }, onFailed)",
            "ai.pleos.playground.vehicle.api.model.capability"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatBackrestAngleCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets seat BackrestAnglePosition capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[SeatArea], [AreaBounds]<[Int]>>: Callback invoked when the SeatBackrestAnglePosition capability configuration and the supported areas with their corresponding bounds values are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the backrest angle position capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatBackrestAnglePositionCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    @Deprecated(
        "This API is deprecated, use checkSeatHeadrestAnglePositionCapabilities instead.",
        replaceWith = ReplaceWith(
            "checkSeatHeadrestAnglePositionCapabilities({ capabilityConfig, map }, onFailed)",
            "ai.pleos.playground.vehicle.api.model.capability"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatHeadrestAngleCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets seat HeadrestAnglePosition capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[SeatArea], [AreaBounds]<[Int]>>: Callback invoked when the SeatHeadrestAnglePosition capability configuration and the supported areas with their corresponding bounds values are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the headrest angle position capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatHeadrestAnglePositionCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    @Deprecated(
        "This API is deprecated, use checkSeatHeadrestForeAftPositionCapabilities instead.",
        replaceWith = ReplaceWith(
            "checkSeatHeadrestForeAftPositionCapabilities({ capabilityConfig, map }, onFailed)",
            "ai.pleos.playground.vehicle.api.model.capability"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatHeadrestForeAftCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets seat HeadrestForeAftPosition capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[SeatArea], [AreaBounds]<[Int]>>: Callback invoked when the SeatHeadrestForeAftPosition capability configuration and the supported areas with their corresponding bounds values are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the headrest fore-aft position capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatHeadrestForeAftPositionCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    @Deprecated(
        "This API is deprecated, use checkSeatHeadrestHeightPositionCapabilities instead.",
        replaceWith = ReplaceWith(
            "checkSeatHeadrestHeightPositionCapabilities({ capabilityConfig, map }, onFailed)",
            "ai.pleos.playground.vehicle.api.model.capability"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatHeadrestHeightCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets seat HeadrestHeightPosition capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[SeatArea], [AreaBounds]<[Int]>>: Callback invoked when the SeatHeadrestHeightPosition capability configuration and the supported areas with their corresponding bounds values are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the headrest height position capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatHeadrestHeightPositionCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    @Deprecated(
        "This API is deprecated, use checkSeatHeightPositionCapabilities instead.",
        replaceWith = ReplaceWith(
            "checkSeatHeightPositionCapabilities({ capabilityConfig, map }, onFailed)",
            "ai.pleos.playground.vehicle.api.model.capability"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatHeightCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets seat height position capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[SeatArea], [AreaBounds]<[Int]>>: Callback invoked when the SeatHeightPosition capability configuration and the supported areas with their corresponding bounds values are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the seat height position capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatHeightPositionCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    @Deprecated(
        "This API is deprecated, use checkSeatLumbarForeAftPositionCapabilities instead.",
        replaceWith = ReplaceWith(
            "checkSeatLumbarForeAftPositionCapabilities({ capabilityConfig, map }, onFailed)",
            "ai.pleos.playground.vehicle.api.model.capability"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatLumbarForeAftCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets seat LumbarForeAftPosition capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[SeatArea], [AreaBounds]<[Int]>>: Callback invoked when the SeatLumbarForeAftPosition capability configuration and the supported areas with their corresponding bounds values are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the lumbar fore-aft position capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatLumbarForeAftPositionCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )


    @Deprecated(
        "This API is deprecated, use checkSeatLumbarSideSupportPositionCapabilities instead.",
        replaceWith = ReplaceWith(
            "checkSeatLumbarSideSupportPositionCapabilities({ capabilityConfig, map }, onFailed)",
            "ai.pleos.playground.vehicle.api.model.capability"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatLumbarSideSupportCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets seat LumbarSideSupportPosition capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[SeatArea], [AreaBounds]<[Int]>>: Callback invoked when the SeatLumbarSideSupportPosition capability configuration and the supported areas with their corresponding bounds values are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the lumbar side support position capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatLumbarSideSupportPositionCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    @Deprecated(
        "This API is deprecated, use checkSeatTiltPositionCapabilities instead.",
        replaceWith = ReplaceWith(
            "checkSeatTiltPositionCapabilities({ capabilityConfig, map }, onFailed)",
            "ai.pleos.playground.vehicle.api.model.capability"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatTiltCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets seat tilt position capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[SeatArea], [AreaBounds]<[Int]>>: Callback invoked when the SeatTiltPosition capability configuration and the supported areas with their corresponding bounds values are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the seat tilt position capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatTiltPositionCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    @Deprecated(
        "This API is deprecated, use checkSeatDepthPositionCapabilities instead.",
        replaceWith = ReplaceWith(
            "checkSeatDepthPositionCapabilities({ capabilityConfig, map }, onFailed)",
            "ai.pleos.playground.vehicle.api.model.capability"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatDepthCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets seat depth position capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[SeatArea], [AreaBounds]<[Int]>>: Callback invoked when the SeatDepthPosition capability configuration and the supported areas with their corresponding bounds values are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the seat depth position capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatDepthPositionCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    @Deprecated(
        "This API is deprecated, use checkSeatForeAftPositionCapabilities instead.",
        replaceWith = ReplaceWith(
            "checkSeatForeAftPositionCapabilities({ capabilityConfig, map }, onFailed)",
            "ai.pleos.playground.vehicle.api.model.capability"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatForeAftCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets seat ForeAft position capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[SeatArea], [AreaBounds]<[Int]>>: Callback invoked when the SeatForeAftPosition capability configuration and the supported areas with their corresponding bounds values are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the seat fore-aft position capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatForeAftPositionCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )


    @Deprecated(
        "This API is deprecated, use checkSeatBeltHeightPositionCapabilities instead.",
        replaceWith = ReplaceWith(
            "checkSeatBeltHeightPositionCapabilities({ capabilityConfig, map }, onFailed)",
            "ai.pleos.playground.vehicle.api.model.capability"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatBeltHeightCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets seat belt height position capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[SeatArea], [AreaBounds]<[Int]>>: Callback invoked when the SeatBeltHeightPosition capability configuration and the supported areas with their corresponding bounds values are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the seat belt height position capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatBeltHeightPositionCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )


    @Deprecated(
        message = "Use the new checkSeatTemperatureCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkSeatTemperatureCapabilities({ capabilityConfig, map }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatTemperatureCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets seat temperature capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[SeatArea], [AreaBounds]<[Int]>>: Callback invoked when the SeatTemperature capability configuration and the supported areas with their corresponding bounds values are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the seat temperature capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatTemperatureCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )


    @Deprecated(
        message = "Use the new checkSeatVentilationCapabilities API that returns CapabilityConfig.",
        replaceWith = ReplaceWith(
            "checkSeatVentilationCapabilities({ capabilityConfig, map }, onFailed)"
        ),
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatVentilationCapabilities(
        onComplete: (Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets seat ventilation capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[SeatArea], [AreaBounds]<[Int]>>: Callback invoked when the SeatVentilation capability configuration and the supported areas with their corresponding bounds values are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the seat ventilation capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatVentilationCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets move seat BackrestAngle capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[SeatArea]>: Callback invoked when the MoveSeatBackrestAngle capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the move seat backrest angle capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkMoveSeatBackrestAngleCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets move seat HeadrestForeAft capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[SeatArea]>: Callback invoked when the MoveSeatHeadrestForeAft capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the move seat headrest fore-aft capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkMoveSeatHeadrestForeAftCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets move seat belt height capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[SeatArea]>: Callback invoked when the MoveSeatBeltHeight capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the move seat belt height capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkMoveSeatBeltHeightCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets move seat headrest height capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[SeatArea]>: Callback invoked when the MoveSeatHeadrestHeight capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the move seat headrest height capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkMoveSeatHeadrestHeightCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets move seat height capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[SeatArea]>: Callback invoked when the MoveSeatHeight capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the move seat height capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkMoveSeatHeightCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets move seat lumbar ForeAft capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[SeatArea]>: Callback invoked when the MoveSeatLumbarForeAft capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the move seat lumbar fore-aft capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkMoveSeatLumbarForeAftCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets move seat lumbar side support capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[SeatArea]>: Callback invoked when the MoveSeatLumbarSideSupport capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the move seat lumbar side support capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkMoveSeatLumbarSideSupportCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets move seat tilt capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[SeatArea]>: Callback invoked when the MoveSeatTilt capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the move seat tilt capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkMoveSeatTiltCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets move seat depth capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[SeatArea]>: Callback invoked when the MoveSeatDepth capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the move seat depth capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkMoveSeatDepthCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets move seat headrest angle capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[SeatArea]>: Callback invoked when the MoveSeatHeadrestAngle capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the move seat headrest angle capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkMoveSeatHeadrestAngleCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets move seat ForeAft capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [List]<[SeatArea]>: Callback invoked when the MoveSeatForeAft capability configuration and supported areas are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the move seat fore-aft capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkMoveSeatForeAftCapabilities(
        onComplete: (CapabilityConfig, List<SeatArea>) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets seat memory capabilities.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param onComplete [CapabilityConfig], [Map]<[SeatArea], [AreaBounds]<[Int]>>: Callback invoked when the SeatMemory capability configuration and the supported areas with their corresponding bounds values are successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the seat memory capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun checkSeatMemoryCapabilities(
        onComplete: (CapabilityConfig, Map<SeatArea, AreaBounds<Int>>) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /**
     * Gets current seat occupancy.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param onComplete [Boolean]: Callback invoked when the current seat occupancy state is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the seat occupancy state fails.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun getCurrentSeatOccupancy(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Move seat backrestAngle.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_SEATS" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param value [MoveControl]: The target seat backrest angle to set.
     * @param onComplete [SeatArea], [MoveControl]: Callback invoked when the seat backrest angle has been successfully moved.
     * @param onFailed [Exception]: Callback invoked when moving the seat backrest angle fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_SEATS)
    fun moveSeatBackrestAngle(
        area: SeatArea,
        value: MoveControl,
        onComplete: (SeatArea, MoveControl?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Moves seat headrest fore/aft.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_SEATS" />
     * ```
     *
     * @param area [SeatArea]: The seat area of the vehicle.
     * @param value [MoveControl]: The seat headrest fore/aft value to set.
     * @param onComplete [SeatArea], [MoveControl]: Callback invoked when the seat headrest has been successfully moved fore or aft.
     * @param onFailed [Exception]: Callback invoked when setting the seat headrest fore/aft position fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_SEATS)
    fun moveSeatHeadrestForeAft(
        area: SeatArea,
        value: MoveControl,
        onComplete: (SeatArea, MoveControl?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets current seat headrest fore/aft position.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area [SeatArea]: The seat area of the vehicle.
     * @param onComplete [Int]: Callback invoked when the current headrest fore/aft position is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current headrest fore/aft position fails.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun getCurrentSeatHeadrestForeAftPosition(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Moves seat headrest height.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_SEATS" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param value [MoveControl]: The target seat headrest height to set.
     * @param onComplete [SeatArea], [MoveControl]: Callback invoked when the seat headrest height has been successfully moved.
     * @param onFailed [Exception]: Callback invoked when moving the seat headrest height fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_SEATS)
    fun moveSeatHeadrestHeight(
        area: SeatArea,
        value: MoveControl,
        onComplete: (SeatArea, MoveControl?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current seat headrest height aft position.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param onComplete [Int]: Callback invoked when the current seat headrest height position is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current seat headrest height position fails.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun getCurrentSeatHeadrestHeightAftPosition(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Moves the seat height.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_SEATS" />
     * ```
     *
     * @param area [SeatArea]: The seat area of the vehicle.
     * @param value [MoveControl]: The seat height value to set.
     * @param onComplete [SeatArea], [MoveControl]: Callback invoked when the seat height has been successfully moved.
     * @param onFailed [Exception]: Callback invoked when moving the seat height fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_SEATS)
    fun moveSeatHeight(
        area: SeatArea,
        value: MoveControl,
        onComplete: (SeatArea, MoveControl?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current seat height position.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param onComplete [Int]: Callback invoked when the current seat height position is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current seat height position fails.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun getCurrentSeatHeightPosition(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Moves seat lumbar fore/aft.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_SEATS" />
     * ```
     *
     * @param area [SeatArea]: The seat area of the vehicle.
     * @param value [MoveControl]: The seat lumbar fore/aft value to set.
     * @param onComplete [SeatArea], [MoveControl]: Callback invoked when the seat lumbar support has been successfully moved fore or aft.
     * @param onFailed [Exception]: Callback invoked when moving the seat lumbar support fore or aft fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_SEATS)
    fun moveSeatLumbarForeAft(
        area: SeatArea,
        value: MoveControl,
        onComplete: (SeatArea, MoveControl?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current seat lumbar fore/aft position.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area [SeatArea]: The seat area of the vehicle.
     * @param onComplete [Int]: Callback invoked when the current seat lumbar fore/aft position is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current seat lumbar fore/aft position fails.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun getCurrentSeatLumbarForeAftPosition(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Moves the seat lumbar side support.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_SEATS" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param value [MoveControl]: The target seat lumbar side support level to set.
     * @param onComplete [SeatArea], [MoveControl]: Callback invoked when the seat lumbar side support has been successfully moved.
     * @param onFailed [Exception]: Callback invoked when moving the seat lumbar side support fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_SEATS)
    fun moveSeatLumbarSideSupport(
        area: SeatArea,
        value: MoveControl,
        onComplete: (SeatArea, MoveControl?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the seat lumbar side support position.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param onComplete [Int]: Callback invoked when the current seat lumbar side support position is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current seat lumbar side support position fails.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun getCurrentSeatLumbarSideSupportPosition(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Selects seat memory.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_SEATS" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param value [Int]: The seat memory number to select.
     * @param onFailed [Exception]: Callback invoked when selecting the seat memory fails.

     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_SEATS)
    fun selectSeatMemory(area: SeatArea, value: Int, onFailed: (Exception) -> Unit = {})

    /**
     * Moves seat tilt.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_SEATS" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param value [MoveControl]: The target seat tilt angle to set.
     * @param onComplete [SeatArea], [MoveControl]: Callback invoked when the seat tilt has been successfully moved.
     * @param onFailed [Exception]: Callback invoked when moving the seat tilt fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_SEATS)
    fun moveSeatTilt(
        area: SeatArea,
        value: MoveControl,
        onComplete: (SeatArea, MoveControl?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current seat tilt position.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param onComplete [Int]: Callback invoked when the current seat tilt position is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current seat tilt position fails.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun getCurrentSeatTiltPosition(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Moves the seat depth.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_SEATS" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param value [MoveControl]: The target seat depth value to set.
     * @param onComplete [SeatArea], [MoveControl]: Callback invoked when the seat depth has been successfully moved.
     * @param onFailed [Exception]: Callback invoked when moving the seat depth fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_SEATS)
    fun moveSeatDepth(
        area: SeatArea,
        value: MoveControl,
        onComplete: (SeatArea, MoveControl?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current seat depth position.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param onComplete [Int]: Callback invoked when the current seat depth position is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current seat depth position fails.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun getCurrentSeatDepthPosition(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets the current seat fore/aft position.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param onComplete [Int]: Callback invoked when the current seat fore/aft position is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current seat fore/aft position fails.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun getCurrentSeatForeAftPosition(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Moves the seat headrest angle.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_SEATS" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param value [MoveControl]: The target seat headrest angle to set.
     * @param onComplete [SeatArea], [MoveControl]: Callback invoked when the seat headrest angle has been successfully moved.
     * @param onFailed [Exception]: Callback invoked when failed to move the seat headrest angle.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_SEATS)
    fun moveSeatHeadrestAngle(
        area: SeatArea,
        value: MoveControl,
        onComplete: (SeatArea, MoveControl?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current seat headrest angle position.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param onComplete [SeatArea], [MoveControl]: Callback invoked when the seat headrest angle has been successfully moved.
     * @param onFailed [Exception]: Callback invoked when moving the seat headrest angle fails.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun getCurrentSeatHeadrestAnglePosition(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets the current seat backrest angle position.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param onComplete [Int]: Callback invoked when the current seat backrest angle position is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current seat backrest angle position fails.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun getCurrentSeatBackrestAnglePosition(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /**
     * Gets the current seat belt buckled state.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param onComplete [Boolean]: Callback invoked when the current seat belt buckled state has been successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current seat belt buckled state fails.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun getCurrentSeatBeltBuckledState(
        area: SeatArea,
        onComplete: (Boolean?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Moves the seat belt height.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_SEATS" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param value [MoveControl]: The seat belt height value to set.
     * @param onComplete [SeatArea], [MoveControl]: Callback invoked when the seat belt height has been successfully moved.
     * @param onFailed [Exception]: Callback invoked when moving the seat belt height fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_SEATS)
    fun moveSeatBeltHeight(
        area: SeatArea,
        value: MoveControl,
        onComplete: (SeatArea, MoveControl?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current seat belt height position.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param onComplete [Int]: Callback invoked when the current seat belt height position has been successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current seat belt height position fails.

     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun getCurrentSeatBeltHeightPosition(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Moves the seat fore/aft.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_SEATS" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param value [MoveControl]: The seat fore/aft value to set.
     * @param onComplete [SeatArea], [MoveControl]: Callback invoked when the seat has been successfully moved fore or aft.
     * @param onFailed [Exception]: Callback invoked when moving the seat fore or aft fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_SEATS)
    fun moveSeatForeAft(
        area: SeatArea,
        value: MoveControl,
        onComplete: (SeatArea, MoveControl?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Sets the seat heating/cooling temperature.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_SEATS" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param value [Int]: The seat temperature step value to set.
     * @param onComplete [SeatArea], [Int]: Callback invoked when the seat temperature has been successfully set.
     * @param onFailed [Exception]: Callback invoked when setting the seat temperature fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_SEATS)
    fun setHvacSeatTemperature(
        area: SeatArea,
        value: Int,
        onComplete: (SeatArea, Int?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * Gets the current seat heating/cooling temperature.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param onComplete [Int]: Callback invoked when the current seat heating/cooling temperature value is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current seat heating/cooling temperature value fails.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun getHvacSeatTemperature(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets the current seat ventilation state.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param onComplete [Int]: Callback invoked when the current seat ventilation value is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked when retrieving the current seat ventilation value fails.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun getHvacSeatVentilationState(
        area: SeatArea,
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Sets the seat ventilation.
     *
     * **Permission:** [PleosPermission.CONTROL_CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CONTROL_CAR_SEATS" />
     * ```
     *
     * @param area [SeatArea]: The seat area for the vehicle.
     * @param value [Int]: The seat ventilation value to set.
     * @param onComplete [SeatArea], [Int]: Callback invoked when the seat ventilation has been successfully set.
     * @param onFailed [Exception]: Callback invoked when setting the seat ventilation fails.
     */
    @RequiresPermission(PleosPermission.CONTROL_CAR_SEATS)
    fun setHvacSeatVentilation(
        area: SeatArea,
        value: Int,
        onComplete: (SeatArea, Int?) -> Unit = { _, _ -> },
        onFailed: (Exception) -> Unit = {}
    )


    /**
     * Registers the SeatOccupancy to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatOccupancyListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun registerSeatOccupancy(area: SeatArea, listener: SeatOccupancyListener)

    /**
     * Stops getting property updates for the given SeatOccupancy.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatOccupancyListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun unregisterSeatOccupancy(area: SeatArea, listener: SeatOccupancyListener)

    /**
     * Registers the SeatBackrestAnglePosition to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatBackrestAnglePositionListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun registerSeatBackrestAnglePosition(
        area: SeatArea,
        listener: SeatBackrestAnglePositionListener
    )

    /**
     * Stops getting property updates for the given SeatBackrestAnglePosition.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatBackrestAnglePositionListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun unregisterSeatBackrestAnglePosition(
        area: SeatArea,
        listener: SeatBackrestAnglePositionListener
    )

    /**
     * Registers the SeatBeltBuckledState to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatBeltBuckledStateListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun registerSeatBeltBuckledState(area: SeatArea, listener: SeatBeltBuckledStateListener)

    /**
     * Stops getting property updates for the given SeatBeltBuckledState.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatBeltBuckledStateListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun unregisterSeatBeltBuckledState(area: SeatArea, listener: SeatBeltBuckledStateListener)

    /**
     * Registers the SeatBeltHeightPosition to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatBeltHeightPositionListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun registerSeatBeltHeightPosition(area: SeatArea, listener: SeatBeltHeightPositionListener)

    /**
     * Stops getting property updates for the given SeatBeltHeightPosition.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatBeltHeightPositionListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun unregisterSeatBeltHeightPosition(area: SeatArea, listener: SeatBeltHeightPositionListener)

    /**
     * Registers the SeatDepthPosition to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatDepthPositionListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun registerSeatDepthPosition(area: SeatArea, listener: SeatDepthPositionListener)

    /**
     * Stops getting property updates for the given SeatDepthPosition.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatDepthPositionListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun unregisterSeatDepthPosition(area: SeatArea, listener: SeatDepthPositionListener)

    /**
     * Registers the SeatForeAftPosition to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatForeAftPositionListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun registerSeatForeAftPosition(area: SeatArea, listener: SeatForeAftPositionListener)

    /**
     * Stops getting property updates for the given SeatForeAftPosition.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatForeAftPositionListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun unregisterSeatForeAftPosition(area: SeatArea, listener: SeatForeAftPositionListener)

    /**
     * Registers the SeatHeadrestAnglePosition to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatHeadrestAnglePositionListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun registerSeatHeadrestAnglePosition(
        area: SeatArea,
        listener: SeatHeadrestAnglePositionListener
    )

    /**
     * Stops getting property updates for the given SeatHeadrestAnglePosition.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatHeadrestAnglePositionListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun unregisterSeatHeadrestAnglePosition(
        area: SeatArea,
        listener: SeatHeadrestAnglePositionListener
    )

    /**
     * Registers the SeatHeadrestForeAftPosition to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatHeadrestForeAftPositionListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun registerSeatHeadrestForeAftPosition(
        area: SeatArea,
        listener: SeatHeadrestForeAftPositionListener
    )

    /**
     * Stops getting property updates for the given SeatHeadrestForeAftPosition.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatHeadrestForeAftPositionListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun unregisterSeatHeadrestForeAftPosition(
        area: SeatArea,
        listener: SeatHeadrestForeAftPositionListener
    )

    /**
     * Registers the SeatHeadrestHeightAftPosition to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatHeadrestHeightAftPositionListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun registerSeatHeadrestHeightAftPosition(
        area: SeatArea,
        listener: SeatHeadrestHeightAftPositionListener
    )

    /**
     * Stops getting property updates for the given SeatHeadrestHeightAftPosition.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatHeadrestHeightAftPositionListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun unregisterSeatHeadrestHeightAftPosition(
        area: SeatArea,
        listener: SeatHeadrestHeightAftPositionListener
    )

    /**
     * Registers the SeatHeightPosition to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatHeightPositionListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun registerSeatHeightPosition(area: SeatArea, listener: SeatHeightPositionListener)

    /**
     * Stops getting property updates for the given SeatHeightPosition.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatHeightPositionListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun unregisterSeatHeightPosition(area: SeatArea, listener: SeatHeightPositionListener)

    /**
     * Registers the SeatLumbarForeAftPosition to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatLumbarForeAftPositionListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun registerSeatLumbarForeAftPosition(
        area: SeatArea,
        listener: SeatLumbarForeAftPositionListener
    )

    /**
     * Stops getting property updates for the given SeatLumbarForeAftPosition.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatLumbarForeAftPositionListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun unregisterSeatLumbarForeAftPosition(
        area: SeatArea,
        listener: SeatLumbarForeAftPositionListener
    )

    /**
     * Registers the SeatLumbarSideSupportPosition to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatLumbarSideSupportPositionListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun registerSeatLumbarSideSupportPosition(
        area: SeatArea,
        listener: SeatLumbarSideSupportPositionListener
    )

    /**
     * Stops getting property updates for the given SeatLumbarSideSupportPosition.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatLumbarSideSupportPositionListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun unregisterSeatLumbarSideSupportPosition(
        area: SeatArea,
        listener: SeatLumbarSideSupportPositionListener
    )

    /**
     * Registers the SeatTiltPosition to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatTiltPositionListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun registerSeatTiltPosition(area: SeatArea, listener: SeatTiltPositionListener)

    /**
     * Stops getting property updates for the given SeatTiltPosition.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [SeatTiltPositionListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun unregisterSeatTiltPosition(area: SeatArea, listener: SeatTiltPositionListener)

    /**
     * Registers the HvacSeatTemperature to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [HvacSeatTemperatureListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun registerHvacSeatTemperature(area: SeatArea, listener: HvacSeatTemperatureListener)

    /**
     * Stops getting property updates for the given HvacSeatTemperature.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [HvacSeatTemperatureListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun unregisterHvacSeatTemperature(area: SeatArea, listener: HvacSeatTemperatureListener)

    /**
     * Registers the HvacSeatVentilation to get property updates.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [HvacSeatVentilationListener]: Listener is registered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun registerHvacSeatVentilation(area: SeatArea, listener: HvacSeatVentilationListener)

    /**
     * Stops getting property updates for the given HvacSeatVentilation.
     *
     * **Permission:** [PleosPermission.CAR_SEATS] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_SEATS" />
     * ```
     *
     * @param area The seat area for the vehicle.
     * @param listener [HvacSeatVentilationListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_SEATS)
    fun unregisterHvacSeatVentilation(area: SeatArea, listener: HvacSeatVentilationListener)
}
