package ai.pleos.playground.vehicle.api.model.capability

/**
 * Represents the configuration for a specific capability.
 *
 * @property accessType The level of access granted. Defaults to AccessType.NONE.
 */
data class CapabilityConfig(val accessType: AccessType = AccessType.NONE)
