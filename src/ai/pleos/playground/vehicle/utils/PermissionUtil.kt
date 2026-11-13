package ai.pleos.playground.vehicle.utils

import ai.pleos.playground.vehicle.permission.RequiresCustomPermission
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlin.reflect.full.findAnnotation

internal object PermissionUtil {

    private fun checkCustomPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun <T : Any> getMethodAnnotationValue(
        instance: T,
        methodName: String
    ): Array<out String>? {
        return instance::class.members
            .filter { it.name == methodName }
            .firstNotNullOfOrNull { it.findAnnotation<RequiresCustomPermission>()?.permissions }
    }

    private fun <T : Any> resolvePermissionTarget(instance: T): Pair<String, Array<out String>>? {
        val targetClassName = instance::class.java.name
        val stackTrace = Throwable().stackTrace

        for (stackFrame in stackTrace) {
            if (stackFrame.className != targetClassName) {
                continue
            }

            val permissions = getMethodAnnotationValue(instance, stackFrame.methodName) ?: continue
            return stackFrame.methodName to permissions
        }

        return null
    }

    fun <T> getVehiclePermissionException(context: Context, instance: T): SecurityException? {
        val targetInstance = instance as? Any
            ?: return SecurityException("Permission metadata not found for unknown instance")

        val (methodName, permissions) = resolvePermissionTarget(targetInstance)
            ?: return SecurityException("Permission metadata not found for ${targetInstance::class.java.simpleName}")

        val securityException = SecurityException("$methodName need Permission")
        permissions.forEach {
            if (!checkCustomPermission(context, it)) {
                return securityException
            }
        }

        return null
    }
}
