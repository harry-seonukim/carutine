package ai.pleos.playground.vehicle.constant.control

import ai.pleos.playground.vehicle.constant.VehicleWiperSwitchValue

/**
 * Represents the wiper switch state.
 */
enum class WiperSwitch(internal val id: Int) {

    OTHER(VehicleWiperSwitchValue.OTHER),
    OFF(VehicleWiperSwitchValue.OFF),
    MIST(VehicleWiperSwitchValue.MIST),
    INTERMITTENT_LEVEL_1(VehicleWiperSwitchValue.INTERMITTENT_LEVEL_1),
    INTERMITTENT_LEVEL_2(VehicleWiperSwitchValue.INTERMITTENT_LEVEL_2),
    INTERMITTENT_LEVEL_3(VehicleWiperSwitchValue.INTERMITTENT_LEVEL_3),
    INTERMITTENT_LEVEL_4(VehicleWiperSwitchValue.INTERMITTENT_LEVEL_4),
    INTERMITTENT_LEVEL_5(VehicleWiperSwitchValue.INTERMITTENT_LEVEL_5),
    CONTINUOUS_LEVEL_1(VehicleWiperSwitchValue.CONTINUOUS_LEVEL_1),
    CONTINUOUS_LEVEL_2(VehicleWiperSwitchValue.CONTINUOUS_LEVEL_2),
    CONTINUOUS_LEVEL_3(VehicleWiperSwitchValue.CONTINUOUS_LEVEL_3),
    CONTINUOUS_LEVEL_4(VehicleWiperSwitchValue.CONTINUOUS_LEVEL_4),
    CONTINUOUS_LEVEL_5(VehicleWiperSwitchValue.CONTINUOUS_LEVEL_5),
    AUTO(VehicleWiperSwitchValue.AUTO),
    SERVICE(VehicleWiperSwitchValue.SERVICE),
    ;

    internal companion object {
        fun findById(id: Int?): WiperSwitch? {
            return entries.find { it.id == id }
        }
    }
}
