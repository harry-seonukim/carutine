package ai.pleos.playground.vehicle.constant.control

import ai.pleos.playground.vehicle.constant.VehicleEvChargingConnectorType

/**
 * Represents the EV charging connector type.
 */
enum class EvConnectorType(internal val id: Int) {
    UNKNOWN(VehicleEvChargingConnectorType.UNKNOWN),
    IEC_TYPE_1_AC(VehicleEvChargingConnectorType.IEC_TYPE_1_AC),
    IEC_TYPE_2_AC(VehicleEvChargingConnectorType.IEC_TYPE_2_AC),
    IEC_TYPE_3_AC(VehicleEvChargingConnectorType.IEC_TYPE_3_AC),
    IEC_TYPE_4_DC(VehicleEvChargingConnectorType.IEC_TYPE_4_DC),
    IEC_TYPE_1_CCS_DC(VehicleEvChargingConnectorType.IEC_TYPE_1_CCS_DC),
    IEC_TYPE_2_CCS_DC(VehicleEvChargingConnectorType.IEC_TYPE_2_CCS_DC),
    TESLA_ROADSTER(VehicleEvChargingConnectorType.TESLA_ROADSTER),
    TESLA_HPWC(VehicleEvChargingConnectorType.TESLA_HPWC),
    TESLA_SUPERCHARGER(VehicleEvChargingConnectorType.TESLA_SUPERCHARGER),
    GBT_AC(VehicleEvChargingConnectorType.GBT_AC),
    GBT_DC(VehicleEvChargingConnectorType.GBT_DC),
    OTHER(VehicleEvChargingConnectorType.OTHER)
    ;

    internal companion object {
        fun Collection<Int>?.toEvConnectorArrayOrNull(): List<EvConnectorType>? {
            return this?.let {
                val mapping = entries.associateBy { it.id }
                this.filter { mapping.containsKey(it) }
                    .map { mapping[it] ?: UNKNOWN }
            }
        }
    }
}
