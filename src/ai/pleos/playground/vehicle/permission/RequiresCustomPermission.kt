package ai.pleos.playground.vehicle.permission

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
internal annotation class RequiresCustomPermission(vararg val permissions: String)

