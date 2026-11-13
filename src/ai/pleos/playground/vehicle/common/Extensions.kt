package ai.pleos.playground.vehicle.common

import ai.pleos.playground.vehicle.constant.VehicleValueType
import ai.pleos.playground.vehicle.utils.GsonUtil
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

internal inline fun <reified T, R> T.catching(errorLogPrefix: String = "", block: () -> R): Result<R> {
    val tag = "[Pleos][SDK]"
    return try {
        Result.success(block())
    } catch (t: Throwable) {
        errorLogPrefix.let {
            Log.e(tag, "$it  : ${t.localizedMessage}")
        }
        Result.failure(t)
    }
}

internal inline fun <T> Result<T>.finally(block: (Result<T>) -> Unit): Result<T> = also(block)

internal fun Float.toCelsius(): Float {
    return (this - 32) * 5 / 9
}

internal fun Float.toFahrenheit(): Float {
    return (this * 9 / 5) + 32
}

@Throws(Exception::class, JsonParseException::class)
internal fun <T : Any> T.makeVehicleTypeAndValue(gson: Gson = GsonUtil.gson): Pair<Int, String> {
    return when (this) {
        is String -> VehicleValueType.STRING to this.toString()
        is Int -> VehicleValueType.INT to this.toString()
        is Float -> VehicleValueType.FLOAT to this.toString()
        is Double -> VehicleValueType.DOUBLE to this.toString()
        is Long -> VehicleValueType.LONG to this.toString()
        is Boolean -> VehicleValueType.BOOLEAN to this.toString()
        is IntArray -> VehicleValueType.INT_ARRAY to gson.toJson(this)
        is FloatArray -> VehicleValueType.FLOAT_ARRAY to gson.toJson(this)
        is LongArray -> VehicleValueType.LONG_ARRAY to gson.toJson(this)
        else -> throw IllegalArgumentException("UNKNOWN or unsupported type. Check 'VehicleValueType.class' for the types available.")
    }
}

@Throws(JsonSyntaxException::class)
internal inline fun <reified T> String?.fromJsonOrNull(gson: Gson = GsonUtil.gson): T? {
    return this?.let { gson.fromJson(it, object : TypeToken<T>() {}.type) }
}

@Throws(NullPointerException::class, JsonSyntaxException::class)
internal inline fun <reified T> String?.fromJson(gson: Gson = GsonUtil.gson): T {
    return fromJsonOrNull<T>(gson) ?: throw NullPointerException("Check your String value.")
}