package ai.pleos.playground.vehicle.constant.telltale

/**
 * Represents the telltale color.
 */
enum class TelltaleColor(internal val id: Int) {
    WHITE(0),
    GREEN(1),
    BLUE(2),
    YELLOW(3),
    RED(4),
    GRAY(5);


    internal companion object {
        fun findById(id: Int?): TelltaleColor? {
            return entries.find { it.id == id }
        }
    }
}
