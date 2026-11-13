package ai.pleos.playground.vehicle.utils

import ai.pleos.playground.vehicle.api.model.area.Area
import ai.pleos.playground.vehicle.api.model.capability.AccessType
import ai.pleos.playground.vehicle.api.model.capability.AreaBounds
import ai.pleos.playground.vehicle.common.catching
import ai.pleos.playground.vehicle.constant.PleosPropertyIds
import ai.pleos.playground.vehicle.data.CapabilityAreaConfig
import ai.pleos.playground.vehicle.data.CapabilityConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

internal object CapabilityUtil {
    val capabilityGson: Gson = GsonBuilder()
        .disableHtmlEscaping()
        .registerTypeAdapter(Class::class.java, ClazzDeserializer())
        .serializeNulls()
        .create()
//    private val tag = StringUtils.TAG(this::class.java.simpleName)
    private var configMap: Map<String, CapabilityConfig<Any>>? = null

    const val NOT_SUPPORT_ERROR = "This specification is not supported in this vehicle."
    const val FAILED_RETRIEVE_CAPABILITIES = "Failed to retrieve capabilities."

    fun setConfigMap(configMap: Map<String, CapabilityConfig<Any>>) {
        this.configMap = configMap
    }

    fun getAccess(config: CapabilityConfig<Any>): AccessType {
        return catching {
            AccessType.findById(config.access) ?: AccessType.NONE

        }.getOrDefault(AccessType.NONE)
    }

    inline fun <reified T : Area> getAreaIds(
        config: CapabilityConfig<Any>
    ): List<T> {
        return catching {
            val areaIds = mutableListOf<T>()
            for (id in config.getAreas()) {
                val area = ImplementUtil.getSdkAreaInstance<T>(id) ?: continue
                areaIds.add(area)
            }

            areaIds
        }.getOrDefault(emptyList())
    }

    fun <T> getGlobalAreaConfig(
        config: CapabilityConfig<Any>
    ): CapabilityAreaConfig<T>? {
        return catching {
            return getCapabilityAreaConfig(config, 0)
        }.getOrDefault(null)
    }

    inline fun <reified T : Area, R> getAreaConfigs(
        sdkPropKey: PleosPropertyIds
    ): Map<T, CapabilityAreaConfig<R>> {
        return catching {
            val config = configMap?.let {
                it[sdkPropKey.name] ?: throw Exception(NOT_SUPPORT_ERROR)
            } ?: throw Exception(FAILED_RETRIEVE_CAPABILITIES)

            return getAreaConfigs(config)
        }.getOrDefault(mutableMapOf())
    }

    inline fun <reified T : Area, R> getAreaConfigs(
        config: CapabilityConfig<Any>
    ): Map<T, CapabilityAreaConfig<R>> {
        return catching {
            val configMapLocal = mutableMapOf<T, CapabilityAreaConfig<R>>()
            for (id in config.getAreas()) {
                val area = ImplementUtil.getSdkAreaInstance<T>(id) ?: continue
                configMapLocal[area] = getCapabilityAreaConfig(config, id) ?: continue
            }
            return configMapLocal
        }.getOrDefault(mutableMapOf())
    }

    fun <T> getCapabilityAreaConfig(
        config: CapabilityConfig<Any>, areaId: Int
    ): CapabilityAreaConfig<T>? {
        return when (config.type.kotlin) {
            Int::class,
            Float::class,
            Double::class,
            Long::class,
            Byte::class,
                -> {
                var supportValueList = config.getSupportValueList(areaId)
                if (supportValueList.isNullOrEmpty()) {
                    supportValueList = config.configArray.map { it.toString() }
                }
                val minValue = convertNumericValue<T>(config.getMinValue(areaId), config.type)
                val maxValue = convertNumericValue<T>(config.getMaxValue(areaId), config.type)
                CapabilityAreaConfig(
                    minValue = minValue,
                    maxValue = maxValue,
                    supportValueList
                ) as CapabilityAreaConfig<T>
            }

            else -> null
        }
    }

    inline fun <reified V> convertList(
        input: List<String>?,
        crossinline idResolver: (String) -> V?
    ): List<V> {
        if (input == null) return emptyList()

        return input.mapNotNull { value ->
            idResolver(value)
        }
    }

    fun <R> createAreaBounds(config: CapabilityAreaConfig<R>): AreaBounds<R>? {
        val min = config.minValue
        val max = config.maxValue
        return if (min != null && max != null) {
            AreaBounds(min, max)
        } else {
            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> convertNumericValue(value: Any?, type: Class<*>): T? {
        if (value == null) return null

        return when {
            type == Int::class.java || type == Int::class.javaObjectType -> (value as? Number)?.toInt() as T?
            type == Float::class.java || type == Float::class.javaObjectType -> (value as? Number)?.toFloat() as T?
            type == Double::class.java || type == Double::class.javaObjectType -> (value as? Number)?.toDouble() as T?
            type == Long::class.java || type == Long::class.javaObjectType -> (value as? Number)?.toLong() as T?
            type == Byte::class.java || type == Byte::class.javaObjectType -> (value as? Number)?.toByte() as T?
            else -> value as? T
        }
    }
}

@Suppress("UNCHECKED_CAST")
private class ClazzDeserializer : JsonDeserializer<Class<Any>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Class<Any> {
        var name = Any::class.java.name
        if (json?.isJsonPrimitive == true) {
            name = json.asJsonPrimitive.asString
        }

        return Class.forName(name) as Class<Any>
    }
}
