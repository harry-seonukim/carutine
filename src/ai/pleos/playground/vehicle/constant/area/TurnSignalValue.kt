package ai.pleos.playground.vehicle.constant.area

import ai.pleos.playground.vehicle.constant.VehicleAreaValue.TurnSignalConstant

/**
 * Represents available turn signal area values.
 */
enum class TurnSignalValue(override val id: Int) : AreaValue {
    OFF(TurnSignalConstant.NONE),
    RIGHT(TurnSignalConstant.RIGHT),
    LEFT(TurnSignalConstant.LEFT)
    ;


    internal companion object {
        fun findById(id: Int?): TurnSignalValue? {
            return TurnSignalValue.values().find { it.id == id }
        }
    }
}
