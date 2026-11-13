package ai.pleos.playground.vehicle.callback.exception

import ai.pleos.playground.vehicle.constant.ErrorCode

/**
 * Exception type used by the vehicle SDK.
 *
 * @param error The error value used to create the exception.
 */
class VehicleException(error: String? = null) : Exception() {
    @Suppress("MemberVisibilityCanBePrivate")
    val errorCode: ErrorCode
    private val originalMessage: String

    init {
        val parsedCode = error?.toIntOrNull()
        if (parsedCode != null) {
            errorCode = ErrorCode.findById(parsedCode) ?: ErrorCode.ERROR_UNDEFINED
            originalMessage = errorCode.name
        } else {
            errorCode = ErrorCode.ERROR_UNDEFINED
            originalMessage = error ?: ErrorCode.ERROR_UNDEFINED.name
        }
    }


    override val message: String
        get() = originalMessage.ifBlank { "Error code: ${errorCode.name}" }
}
