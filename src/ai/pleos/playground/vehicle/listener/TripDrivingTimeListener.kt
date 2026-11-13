package ai.pleos.playground.vehicle.listener

import ai.pleos.playground.vehicle.api.model.common.TripStatistics

/**
 * Interface definition for the trip driving time updates.
 */
interface TripDrivingTimeListener : VehicleListener {

    /**
     * Callback invoked when trip driving time updates.
     *
     * @param statistics [TripStatistics]<[Int]>: Updated trip driving time values in minutes.
     */
    fun onTripDrivingTimeUpdated(statistics: TripStatistics<Int>?)
}
