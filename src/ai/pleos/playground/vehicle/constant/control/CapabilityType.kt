package ai.pleos.playground.vehicle.constant.control

import ai.pleos.playground.vehicle.constant.VehicleCapabilityValueType


/*
    Exclude APIs
    [PO-1461](https://42dotpol.atlassian.net/browse/PO-1461)
*/
internal enum class CapabilityType(internal val id: Int) {
    SUNROOF(VehicleCapabilityValueType.SUNROOF),
    AMBIENT_LIGHT(VehicleCapabilityValueType.AMBIENT_LIGHT),
    ;

    internal companion object {
        fun findById(id: Int?): CapabilityType? {
            return entries.find { it.id == id }
        }
    }
}
