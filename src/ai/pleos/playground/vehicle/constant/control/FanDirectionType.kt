package ai.pleos.playground.vehicle.constant.control

import ai.pleos.playground.vehicle.constant.VehicleFanDirectionTypeValue

/**
 * Represents the HVAC fan direction.
 */
enum class FanDirectionType (internal val id: Int){
    UNKNOWN(VehicleFanDirectionTypeValue.UNKNOWN),
    FACE(VehicleFanDirectionTypeValue.FACE),
    FLOOR(VehicleFanDirectionTypeValue.FLOOR),
    FACE_AND_FLOOR(VehicleFanDirectionTypeValue.FACE_AND_FLOOR),
    DEFROST(VehicleFanDirectionTypeValue.DEFROST),
    FACE_AND_DEFROST(VehicleFanDirectionTypeValue.FACE_DEFROST),
    DEFROST_AND_FLOOR(VehicleFanDirectionTypeValue.FLOOR_DEFROST),
    FACE_AND_FLOOR_AND_DEFROST(VehicleFanDirectionTypeValue.FLOOR_DEFROST_FACE)
    ;

    internal companion object {
        fun findById(id: Int?): FanDirectionType? {
            return entries.find { it.id == id }
        }
    }
}
