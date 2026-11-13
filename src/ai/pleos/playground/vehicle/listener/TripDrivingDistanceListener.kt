package ai.pleos.playground.vehicle.listener

import ai.pleos.playground.vehicle.api.model.common.TripStatistics

/**
 * Interface definition for the trip driving distance updates.
 */
interface TripDrivingDistanceListener : VehicleListener {

    /**
     * Callback invoked when trip driving distance updates.
     *
     * @param statistics [TripStatistics]<[Float]>: Updated trip driving distance values in meters.
     */
    fun onTripDrivingDistanceUpdated(statistics: TripStatistics<Float>?)
}
