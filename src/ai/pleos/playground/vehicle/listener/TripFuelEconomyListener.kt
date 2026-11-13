package ai.pleos.playground.vehicle.listener

import ai.pleos.playground.vehicle.api.model.common.TripStatistics

/**
 * Interface definition for the trip fuel economy updates.
 */
interface TripFuelEconomyListener : VehicleListener {

    /**
     * Callback invoked when trip fuel economy updates.
     *
     * @param statistics [TripStatistics]<[Float]>: Updated trip fuel economy values in L/100 km.
     */
    fun onTripFuelEconomyUpdated(statistics: TripStatistics<Float>?)
}
