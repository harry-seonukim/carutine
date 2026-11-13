package ai.pleos.playground.vehicle.api.model.capability

/**
 * Represents the access level for a capability.
 */
enum class AccessType(internal val id: Int) {
    NONE(0),
    READ(1),
    WRITE(2),
    READ_WRITE(3);

    internal companion object {
        fun findById(id: Int?): AccessType? {
            return AccessType.values().find { it.id == id }
        }
    }
}
