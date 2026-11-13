package ai.pleos.playground.vehicle.constant.area

import ai.pleos.playground.vehicle.constant.VehicleAreaValue.MirrorConstant

/**
 * Represents available mirror area values.
 */
enum class MirrorValue(override val id: Int) : AreaValue {
    DRIVER_LEFT(MirrorConstant.DRIVER_LEFT), DRIVER_RIGHT(MirrorConstant.DRIVER_RIGHT),
    DRIVER_CENTER(MirrorConstant.DRIVER_CENTER)
}
