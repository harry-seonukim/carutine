package ai.pleos.playground.vehicle.constant.control

/**
 * Represents the command for a single smart glass panel.
 *
 * Used by [ai.pleos.playground.vehicle.api.Window.setSmartGlassZoneState] with one entry per panel in panel order.
 */
enum class SmartGlassZoneCommand(internal val id: Int) {
    /** Requests no command for the panel and leaves it unchanged. */
    SKIP(-1),

    /** Turns the panel off. */
    OFF(0),

    /** Turns the panel on. */
    ON(1)
}
