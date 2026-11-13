package ai.pleos.playground.vehicle.constant.control

/**
 * Represents the direction used for movement control.
 */
enum class MoveControl(internal val id: Int) {
    BACKWARD(-1),
    STOP(0),
    FORWARD(1)
    ;

    internal companion object {
        fun findById(id: Int?): MoveControl? {
            return entries.find { it.id == id }
        }
    }
}
