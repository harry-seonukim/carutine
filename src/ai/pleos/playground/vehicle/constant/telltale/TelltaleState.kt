package ai.pleos.playground.vehicle.constant.telltale

/**
 * Represents the telltale state.
 */
enum class TelltaleState(internal val id: Int) {
    OFF(0),
    ON(1),
    BLINK(2);

    internal companion object {
        fun findById(id: Int?): TelltaleState? {
            return entries.find { it.id == id }
        }
    }
}
