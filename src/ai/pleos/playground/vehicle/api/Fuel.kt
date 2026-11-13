package ai.pleos.playground.vehicle.api

import ai.pleos.playground.vehicle.api.model.capability.CapabilityConfig
import ai.pleos.playground.vehicle.constant.PleosPermission
import ai.pleos.playground.vehicle.listener.FuelEconomyListener
import ai.pleos.playground.vehicle.listener.FuelPercentListener
import androidx.annotation.RequiresPermission

/**
 * Provides APIs for fuel economy.
 *
 * Obtain an instance from [ai.pleos.playground.vehicle.Vehicle.getFuel].
 */
interface Fuel {

    /**
     * Gets Instantaneous Fuel Economy capabilities.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the Instantaneous Fuel Economy capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability configuration fails.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun checkInstantaneousFuelEconomyCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /**
     * Gets Fuel Economy Since Refuel capabilities.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the Fuel Economy Since Refuel capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability configuration fails.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun checkSinceChargeFuelEconomyCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /**
     * Gets Cumulative Fuel Economy capabilities.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the Cumulative Fuel Economy capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability configuration fails.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun checkCumulativeFuelEconomyCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets Current Fuel Economy capabilities.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the Current Fuel Economy capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability configuration fails.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun checkCurrentFuelEconomyCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets Fuel Percent capabilities.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [CapabilityConfig]: Callback invoked when the Fuel Percent capability configuration is successfully retrieved.
     * @param onFailed [Exception]: Callback invoked if retrieving the capability configuration fails.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun checkFuelPercentCapabilities(
        onComplete: (CapabilityConfig) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /**
     * Gets instantaneous fuel economy.
     *
     * Instantaneous Fuel Economy in L/100 km.
     *
     * The instantaneous fuel economy of the vehicle is reported in units of L/100 km.
     * This API will be deprecated in the future, as the latest AAOS provides
     * `INSTANTANEOUS_FUEL_ECONOMY` as a standard vehicle property.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [Float]: Callback invoked when the instantaneous fuel economy is successfully retrieved.
     * The value represents fuel economy in L/100 km.
     * @param onFailed [Exception]: Callback invoked if retrieving the instantaneous fuel economy fails.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun getInstantaneousFuelEconomy(
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /**
     * Gets fuel economy since refuel.
     *
     * Fuel economy after refuel.
     *
     * The fuel economy of the vehicle measured since the last refuel.
     * For instantaneous fuel economy, see the `INSTANTANEOUS_FUEL_ECONOMY` vehicle property.
     * Units follow L/100 km, the same as `INSTANTANEOUS_FUEL_ECONOMY`.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [Float]: Callback invoked when the fuel economy since refuel is successfully retrieved.
     * The value represents fuel economy in L/100 km.
     * @param onFailed [Exception]: Callback invoked if retrieving the fuel economy since refuel fails.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun getSinceChargeFuelEconomy(
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /**
     * Gets cumulative fuel economy.
     *
     * Cumulative fuel economy.
     *
     * The cumulative fuel economy of the vehicle calculated over its operating period.
     * For instantaneous fuel economy, see the `INSTANTANEOUS_FUEL_ECONOMY` vehicle property.
     * Units follow L/100 km, the same as `INSTANTANEOUS_FUEL_ECONOMY`.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [Float]: Callback invoked when the cumulative fuel economy is successfully retrieved.
     * The value represents fuel economy in L/100 km.
     * @param onFailed [Exception]: Callback invoked if retrieving the cumulative fuel economy fails.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun getCumulativeFuelEconomy(
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets current fuel economy.
     *
     * Current fuel economy in L/100 km.
     *
     * The fuel economy of the vehicle during the current driving session.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [Float]: Callback invoked when the current fuel economy is successfully retrieved.
     * The value represents fuel economy in L/100 km.
     * @param onFailed [Exception]: Callback invoked if retrieving the current fuel economy fails.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun getCurrentFuelEconomy(
        onComplete: (Float?) -> Unit,
        onFailed: (Exception) -> Unit
    )

    /**
     * Gets the fuel level as a percentage (%).
     *
     * Fuel percentage.
     *
     * The current fuel level of the vehicle is reported as a percentage (%).
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param onComplete [Int]: Callback invoked when the fuel percentage is successfully retrieved.
     * The value represents the fuel level as a percentage (%).
     * @param onFailed [Exception]: Callback invoked if retrieving the fuel percentage fails.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun getFuelPercent(
        onComplete: (Int?) -> Unit,
        onFailed: (Exception) -> Unit
    )


    /* ================ register / unregister ================ */


    /**
     * Registers Instantaneous Fuel Economy listener.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param listener [FuelEconomyListener]: The listener to receive instantaneous fuel economy updates.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun registerInstantaneousFuelEconomy(listener: FuelEconomyListener)


    /**
     * Unregisters Instantaneous Fuel Economy listener.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param listener [FuelEconomyListener]: The listener to unregister.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun unregisterInstantaneousFuelEconomy(listener: FuelEconomyListener)


    /**
     * Registers Fuel Economy Since Refuel listener.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param listener [FuelEconomyListener]: The listener to receive fuel economy since refuel updates.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun registerSinceChargeFuelEconomy(listener: FuelEconomyListener)


    /**
     * Unregisters Fuel Economy Since Refuel listener.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param listener [FuelEconomyListener]: The listener to unregister.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun unregisterSinceChargeFuelEconomy(listener: FuelEconomyListener)


    /**
     * Registers Cumulative Fuel Economy listener.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param listener [FuelEconomyListener]: The listener to receive cumulative fuel economy updates.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun registerCumulativeFuelEconomy(listener: FuelEconomyListener)


    /**
     * Unregisters Cumulative Fuel Economy listener.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param listener [FuelEconomyListener]: The listener to unregister.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun unregisterCumulativeFuelEconomy(listener: FuelEconomyListener)

    /**
     * Registers Current Fuel Economy listener.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param listener [FuelEconomyListener]: The listener to receive current fuel economy updates.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun registerCurrentFuelEconomy(listener: FuelEconomyListener)

    /**
     * Unregisters Current Fuel Economy listener.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param listener [FuelEconomyListener]: The listener to unregister.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun unregisterCurrentFuelEconomy(listener: FuelEconomyListener)

    /**
     * Registers a listener for fuel level updates expressed as a percentage (%).
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param listener [FuelPercentListener]: The listener to receive fuel level updates as a percentage (%).
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun registerFuelPercent(listener: FuelPercentListener)

    /**
     * Unregisters Fuel Percent listener.
     *
     * **Permission:** [PleosPermission.CAR_ENERGY] (protection level: normal)
     * ```xml
     * <uses-permission android:name="pleos.car.permission.CAR_ENERGY" />
     * ```
     *
     * @param listener [FuelPercentListener]: The listener to unregister.
     */
    @RequiresPermission(PleosPermission.CAR_ENERGY)
    fun unregisterFuelPercent(listener: FuelPercentListener)

}
