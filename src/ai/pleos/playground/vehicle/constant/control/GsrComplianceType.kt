package ai.pleos.playground.vehicle.constant.control

import ai.pleos.playground.vehicle.constant.VehicleGsrComplianceTypeValue

/**
 * Represents the GSR compliance type.
 */
enum class GsrComplianceType(internal val id: Int) {
    GSR_COMPLIANCE_TYPE_NOT_REQUIRED(VehicleGsrComplianceTypeValue.GSR_COMPLIANCE_TYPE_NOT_REQUIRED),
    GSR_COMPLIANCE_TYPE_REQUIRED_V1(VehicleGsrComplianceTypeValue.GSR_COMPLIANCE_TYPE_REQUIRED_V1),
    ;

    internal companion object {
        fun findById(id: Int?): GsrComplianceType? {
            return entries.find { it.id == id }
        }
    }
}
