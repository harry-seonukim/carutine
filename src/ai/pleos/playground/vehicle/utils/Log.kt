package ai.pleos.playground.vehicle.utils

internal object Log {
    fun d(tag: String, msg: String) {
        android.util.Log.d(tag, "[${Thread.currentThread().name}]$msg")
    }

    fun i(tag: String, msg: String) {
        android.util.Log.i(tag, "[${Thread.currentThread().name}]$msg")
    }

    fun e(tag: String, msg: String) {
        android.util.Log.e(tag, "[${Thread.currentThread().name}]$msg")
    }

    fun w(tag: String, msg: String) {
        android.util.Log.w(tag, "[${Thread.currentThread().name}]$msg")
    }
}