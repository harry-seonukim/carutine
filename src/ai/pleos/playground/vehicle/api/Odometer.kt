package ai.pleos.playground.vehicle.api

import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.listener.DistanceDrivenListener
import ai.pleos.playground.vehicle.listener.DrivingTimeListener
import ai.pleos.playground.vehicle.listener.RangeRemainingListener
import androidx.annotation.RequiresPermission

/**
 * Provides APIs for odometer and trip data.
 *
 * Obtain an instance from [ai.pleos.playground.vehicle.Vehicle.getOdometer].
 */
interface Odometer {

    /**
     * Gets current distance driven capabilities.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the CurrentDistanceDriven capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     *
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun checkCurrentDistanceDrivenCapabilities(onComplete: (CapabilityConfig) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Current odometer value of the vehicle in kilometers (km).
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param onComplete [Float]: Callback invoked with the current odometer value in kilometers (km),
     * or `null` if no value is available.
     * @param onFailed [Exception]: Callback invoked when retrieving the current odometer value fails.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun getCurrentDistanceDriven(onComplete: (Float?) -> Unit, onFailed: (Exception) -> Unit)

    /**
     * Gets Trip Driving Time capabilities.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the Trip Driving Time capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun checkTripDrivingTimeCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets Trip Current Driving Time capabilities.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the Trip Current Driving Time capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun checkTripCurrentDrivingTimeCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets Trip Cumulative Driving Time capabilities.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the Trip Cumulative Driving Time capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun checkTripCumulativeDrivingTimeCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets Trip Since Refuel Driving Time capabilities.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the Trip Since Refuel Driving Time capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun checkTripSinceRefillDrivingTimeCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets trip current driving time.
     *
     * Trip current driving time in minutes.
     *
     * The driving time of the vehicle during the current driving session.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param onComplete [Int]: Callback invoked when the trip current driving time is successfully retrieved.
     * The value represents driving time in minutes.
     * @param onFailed [Exception]: Callback invoked if retrieving the trip current driving time fails.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun getTripCurrentDrivingTime(
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets trip cumulative driving time.
     *
     * Trip cumulative driving time in minutes.
     *
     * The cumulative driving time of the vehicle calculated over its operating period.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param onComplete [Int]: Callback invoked when the trip cumulative driving time is successfully retrieved.
     * The value represents driving time in minutes.
     * @param onFailed [Exception]: Callback invoked if retrieving the trip cumulative driving time fails.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun getTripCumulativeDrivingTime(
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets trip since refuel driving time.
     *
     * Trip driving time since refuel in minutes.
     *
     * The driving time of the vehicle measured since the last refuel.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param onComplete [Int]: Callback invoked when the trip driving time since refuel is successfully retrieved.
     * The value represents driving time in minutes.
     * @param onFailed [Exception]: Callback invoked if retrieving the trip driving time since refuel fails.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun getTripSinceRefillDrivingTime(
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets Trip Driving Distance capabilities.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the Trip Driving Distance capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun checkTripDrivingDistanceCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets Trip Current Driving Distance capabilities.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the Trip Current Driving Distance capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun checkTripCurrentDrivingDistanceCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets Trip Instantaneous Driving Distance capabilities.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the Trip Instantaneous Driving Distance capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    @Deprecated("This API is deprecated because it uses TripStatisticType.INSTANTANEOUS.")
    fun checkTripInstantaneousDrivingDistanceCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets Trip Cumulative Driving Distance capabilities.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the Trip Cumulative Driving Distance capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun checkTripCumulativeDrivingDistanceCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets Trip Since Refuel Driving Distance capabilities.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the Trip Since Refuel Driving Distance capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability information fails.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun checkTripSinceRefillDrivingDistanceCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets trip current driving distance in meters (m).
     *
     * Trip current driving distance in meters (m).
     *
     * The driving distance of the vehicle during the current driving session.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param onComplete [Float]: Callback invoked when the trip current driving distance is successfully retrieved.
     * The value represents driving distance in meters (m).
     * @param onFailed [Exception]: Callback invoked if retrieving the trip current driving distance fails.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun getTripCurrentDrivingDistance(
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets trip instantaneous driving distance in meters (m).
     *
     * Trip instantaneous driving distance in meters (m).
     *
     * The instantaneous driving distance of the vehicle for the current trip.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param onComplete [Float]: Callback invoked when the trip instantaneous driving distance is successfully retrieved.
     * The value represents driving distance in meters (m).
     * @param onFailed [Exception]: Callback invoked if retrieving the trip instantaneous driving distance fails.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    @Deprecated("This API is deprecated because it uses TripStatisticType.INSTANTANEOUS.")
    fun getTripInstantaneousDrivingDistance(
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets trip cumulative driving distance in meters (m).
     *
     * Trip cumulative driving distance in meters (m).
     *
     * The cumulative driving distance of the vehicle calculated over its operating period.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param onComplete [Float]: Callback invoked when the trip cumulative driving distance is successfully retrieved.
     * The value represents driving distance in meters (m).
     * @param onFailed [Exception]: Callback invoked if retrieving the trip cumulative driving distance fails.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun getTripCumulativeDrivingDistance(
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets trip since refuel driving distance in meters (m).
     *
     * Trip driving distance since refuel in meters (m).
     *
     * The driving distance of the vehicle measured since the last refuel.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param onComplete [Float]: Callback invoked when the trip driving distance since refuel is successfully retrieved.
     * The value represents driving distance in meters (m).
     * @param onFailed [Exception]: Callback invoked if retrieving the trip driving distance since refuel fails.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun getTripSinceRefillDrivingDistance(
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Sets the estimated remaining range in meters (m).
     *
     * @param value Remaining range in meters (m).
     * @param onComplete Callback invoked with the remaining range in meters (m) after it is set.
     * @param onFailed Callback invoked if setting the remaining range fails.
     */
    @Deprecated(
        "This API is deprecated",
        level = DeprecationLevel.ERROR
    )
    @RequiresPermission(PleosPermission.CONTROL_CAR_DRIVE)
    fun setRangeRemainingDistance(
        value: Float,
        onComplete: (Float?) -> Unit = {},
        onFailed: (Exception) -> Unit = {}
    )

    /**
     * @suppress
     * @hide
     * Gets the estimated remaining range in meters (m).
     *
     * @param onComplete Callback invoked with the remaining range in meters (m),
     * or `null` if no value is available.
     * @param onFailed Callback invoked if retrieving the remaining range fails.
     */
    @Deprecated(
        "This API is deprecated, use the Google RANGE_REMAINING property.",
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun getRangeRemainingDistance(onComplete: (Float?) -> Unit, onFailed: (Exception) -> Unit)

    /* ================ register / unregister ================ */
    /**
     * @suppress
     * @hide
     */
    @Deprecated(
        "This API is deprecated, use the Google RANGE_REMAINING property.",
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun registerRangeRemainingListener(listener: RangeRemainingListener)

    /**
     * @suppress
     * @hide
     */
    @Deprecated(
        "This API is deprecated, use the Google RANGE_REMAINING property.",
        level = DeprecationLevel.WARNING
    )
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun unregisterRangeRemainingListener(listener: RangeRemainingListener)

    /**
     * Registers a listener for odometer updates in kilometers (km).
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener [DistanceDrivenListener]: Listener that receives odometer values in kilometers (km).
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun registerDistanceDriven(listener: DistanceDrivenListener)

    /**
     * Stops getting property updates for the given DistanceDriven.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener [DistanceDrivenListener]: Listener is unregistered.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun unregisterDistanceDriven(listener: DistanceDrivenListener)

    /**
     * Registers Trip Current Driving Time listener.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener [DrivingTimeListener]: The listener to receive trip current driving time updates.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun registerTripCurrentDrivingTime(listener: DrivingTimeListener)

    /**
     * Unregisters Trip Current Driving Time listener.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener [DrivingTimeListener]: The listener to unregister.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun unregisterTripCurrentDrivingTime(listener: DrivingTimeListener)

    /**
     * Registers Trip Cumulative Driving Time listener.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener [DrivingTimeListener]: The listener to receive trip cumulative driving time updates.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun registerTripCumulativeDrivingTime(listener: DrivingTimeListener)

    /**
     * Unregisters Trip Cumulative Driving Time listener.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener [DrivingTimeListener]: The listener to unregister.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun unregisterTripCumulativeDrivingTime(listener: DrivingTimeListener)

    /**
     * Registers Trip Since Refuel Driving Time listener.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener [DrivingTimeListener]: The listener to receive trip driving time since refuel updates.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun registerTripSinceRefillDrivingTime(listener: DrivingTimeListener)

    /**
     * Unregisters Trip Since Refuel Driving Time listener.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener [DrivingTimeListener]: The listener to unregister.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun unregisterTripSinceRefillDrivingTime(listener: DrivingTimeListener)

    /**
     * Registers a listener for trip current driving distance updates in meters (m).
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener [DistanceDrivenListener]: Listener that receives updates to the current
     * trip's driving distance, expressed in meters (m).
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun registerTripCurrentDrivingDistance(listener: DistanceDrivenListener)

    /**
     * Unregisters Trip Current Driving Distance listener.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener [DistanceDrivenListener]: The listener to unregister.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun unregisterTripCurrentDrivingDistance(listener: DistanceDrivenListener)

    /**
     * Registers a listener for trip instantaneous driving distance updates in meters (m).
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener [DistanceDrivenListener]: Listener that receives instantaneous trip driving
     * distance values, expressed in meters (m).
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    @Deprecated("This API is deprecated because it uses TripStatisticType.INSTANTANEOUS.")
    fun registerTripInstantaneousDrivingDistance(listener: DistanceDrivenListener)

    /**
     * Unregisters Trip Instantaneous Driving Distance listener.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener [DistanceDrivenListener]: The listener to unregister.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    @Deprecated("This API is deprecated because it uses TripStatisticType.INSTANTANEOUS.")
    fun unregisterTripInstantaneousDrivingDistance(listener: DistanceDrivenListener)

    /**
     * Registers a listener for trip cumulative driving distance updates in meters (m).
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener [DistanceDrivenListener]: Listener that receives cumulative trip driving
     * distance values, expressed in meters (m).
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun registerTripCumulativeDrivingDistance(listener: DistanceDrivenListener)

    /**
     * Unregisters Trip Cumulative Driving Distance listener.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener [DistanceDrivenListener]: The listener to unregister.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun unregisterTripCumulativeDrivingDistance(listener: DistanceDrivenListener)

    /**
     * Registers a listener for trip since refuel driving distance updates in meters (m).
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener [DistanceDrivenListener]: Listener that receives the distance driven since
     * the last refueling, expressed in meters (m).
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun registerTripSinceRefillDrivingDistance(listener: DistanceDrivenListener)

    /**
     * Unregisters Trip Since Refuel Driving Distance listener.
     *
     * **Permission:** [PleosPermission.CAR_DRIVE] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_DRIVE" />
     * ```
     *
     * @param listener [DistanceDrivenListener]: The listener to unregister.
     */
    @RequiresPermission(PleosPermission.CAR_DRIVE)
    fun unregisterTripSinceRefillDrivingDistance(listener: DistanceDrivenListener)
}
