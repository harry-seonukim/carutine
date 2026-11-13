package ai.pleos.playground.vehicle.listener

import ai.pleos.playground.vehicle.api.model.common.TripStatistics

/**
 * Interface definition for the trip EV efficiency updates.
 */
interface TripEvEfficiencyListener : VehicleListener {

    /**
     * Callback invoked when trip EV efficiency updates.
     *
     * @param statistics [TripStatistics]<[Float]>: Updated trip EV efficiency values in km/kWh.
     */
    fun onTripEvEfficiencyUpdated(statistics: TripStatistics<Float>?)
}
