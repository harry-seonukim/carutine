package ai.pleos.playground.vehicle.api.model.capability

/**
 * Describes the smart glass zones and levels for a window area.
 *
 * @property zoneCount Number of smart glass zones (panels) in the window area. Pass this many commands to
 * [ai.pleos.playground.vehicle.api.Window.setSmartGlassZoneState].
 * @property levelCount Number of smart glass levels reported by the vehicle. This value does not indicate
 * smart glass level support; check [ai.pleos.playground.vehicle.api.Window.checkSmartGlassLevelCapabilities] separately.
 */
data class SmartGlassConfig(
    val zoneCount: Int,
    val levelCount: Int
)
