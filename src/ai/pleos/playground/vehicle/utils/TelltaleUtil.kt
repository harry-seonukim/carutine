package ai.pleos.playground.vehicle.utils

import ai.pleos.playground.vehicle.api.model.telltale.TelltaleData
import ai.pleos.playground.vehicle.api.model.telltale.Telltales
import ai.pleos.playground.vehicle.constant.telltale.TelltaleColor
import ai.pleos.playground.vehicle.constant.telltale.TelltaleState
import ai.pleos.playground.vehicle.constant.telltale.TelltaleType
import ai.pleos.playground.vehicle.utils.GsonUtil.gson

internal object TelltaleUtil {
    private const val TELLTALE_BLOCK_SIZE = 7
    fun parseTelltales(json: String?): Telltales {
        if (json.isNullOrBlank()) return Telltales()

        val arr: IntArray = gson.fromJson(json, IntArray::class.java)
        if (arr.size <= 1) return Telltales()

        val declaredCount = arr[0].coerceAtLeast(0)
        val data = arr.drop(1)
        val availableBlocks = data.size / TELLTALE_BLOCK_SIZE
        val count = minOf(declaredCount, availableBlocks)

        val telltales = (0 until count).map { index ->
            val base = index * TELLTALE_BLOCK_SIZE

            val typeId       = data[base]
            val stateId      = data[base + 1]
            val colorId      = data[base + 2]
            val priority     = data[base + 3]
            val warningCount = data[base + 6]

            val type = TelltaleType.findById(typeId)
                ?: error("Unknown typeId=$typeId")
            val state = TelltaleState.findById(stateId)
                ?: error("Unknown stateId=$stateId")
            val color = TelltaleColor.findById(colorId)
                ?: error("Unknown colorId=$colorId")

            TelltaleData(
                type = type,
                state = state,
                color = color.id,
                priority = priority,
                warningCount = warningCount
            )
        }

        return Telltales(telltales)
    }
}