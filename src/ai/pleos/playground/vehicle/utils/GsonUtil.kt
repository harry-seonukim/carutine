package ai.pleos.playground.vehicle.utils

import com.google.gson.GsonBuilder

internal object GsonUtil {
    val gson = GsonBuilder().disableHtmlEscaping().create()
}