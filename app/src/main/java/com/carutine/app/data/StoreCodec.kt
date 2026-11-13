package com.carutine.app.data

import com.carutine.engine.ActionOutcome
import com.carutine.engine.DeviceKey
import com.carutine.engine.DeviceValue
import com.carutine.engine.Place
import com.carutine.engine.RoutineSource
import com.carutine.engine.RunLog
import com.carutine.engine.StoredRoutine
import com.carutine.engine.TriggerEvent
import com.carutine.engine.UndoSnapshot
import com.carutine.model.Action
import com.carutine.model.ActionSpec
import com.carutine.model.AirMode
import com.carutine.model.AmbientBrightness
import com.carutine.model.AmbientColor
import com.carutine.model.PlaceKey
import com.carutine.model.RoutineJson
import com.carutine.model.RoutineSpec
import com.carutine.model.Seat
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put
import java.time.Instant

/**
 * 기기 내부 저장 형식(JSON). 루틴 본문은 계약 JSON([RoutineJson]) 그대로 넣고,
 * 그 밖의 앱 전용 필드(enabled, priority 등)만 여기서 정한다. 서버로 보내지 않는 형식이다.
 * 모든 read 함수는 깨진 항목을 null로 돌려 건너뛴다(파일 일부가 깨져도 앱이 뜨게).
 */
internal object StoreCodec {

    // ---------- routines ----------

    fun routineToJson(r: StoredRoutine): JsonObject = buildJsonObject {
        put("localId", r.localId)
        put("enabled", r.enabled)
        put("priority", r.priority)
        put("source", r.source.wire)
        r.remoteId?.let { put("remoteId", it) }
        put("spec", RoutineJson.toJson(r.spec))
    }

    fun routineFromJson(e: JsonElement): StoredRoutine? {
        val o = e as? JsonObject ?: return null
        val spec = when (val res = RoutineJson.parse(o["spec"] ?: return null)) {
            is RoutineJson.Result.Ok -> res.spec
            is RoutineJson.Result.Error -> return null
        }
        return StoredRoutine(
            localId = o.str("localId") ?: return null,
            spec = spec,
            enabled = o.bool("enabled") ?: true,
            priority = o.int("priority") ?: 0,
            source = RoutineSource.entries.firstOrNull { it.wire == o.str("source") } ?: RoutineSource.LOCAL,
            remoteId = o.str("remoteId"),
        )
    }

    // ---------- places ----------

    fun placeToJson(p: Place): JsonObject = buildJsonObject {
        put("key", p.key.wire)
        put("label", p.label)
        put("lat", p.lat)
        put("lng", p.lng)
        put("radiusM", p.radiusM)
    }

    fun placeFromJson(e: JsonElement): Place? {
        val o = e as? JsonObject ?: return null
        return Place(
            key = PlaceKey.fromWire(o.str("key") ?: return null) ?: return null,
            label = o.str("label") ?: "",
            lat = o.num("lat") ?: return null,
            lng = o.num("lng") ?: return null,
            radiusM = o.num("radiusM") ?: 200.0,
        )
    }

    // ---------- undo ----------

    fun undoToJson(s: UndoSnapshot): JsonObject = buildJsonObject {
        put("at", s.at.toEpochMilli())
        put("routineIds", buildJsonArray { s.routineIds.forEach { add(JsonPrimitive(it)) } })
        put("entries", buildJsonArray {
            s.entries.forEach { en ->
                add(buildJsonObject {
                    put("key", deviceKeyToJson(en.key))
                    put("before", deviceValueToJson(en.before))
                    put("applied", actionToJson(en.applied))
                })
            }
        })
    }

    fun undoFromJson(e: JsonElement): UndoSnapshot? {
        val o = e as? JsonObject ?: return null
        val at = o.long("at") ?: return null
        val ids = (o["routineIds"] as? JsonArray)?.mapNotNull { (it as? JsonPrimitive)?.contentOrNull } ?: emptyList()
        val entries = (o["entries"] as? JsonArray)?.mapNotNull { el ->
            val eo = el as? JsonObject ?: return@mapNotNull null
            val key = deviceKeyFromJson(eo["key"]) ?: return@mapNotNull null
            val before = deviceValueFromJson(eo["before"]) ?: return@mapNotNull null
            val applied = actionFromJson(eo["applied"]) ?: return@mapNotNull null
            UndoSnapshot.Entry(key, before, applied)
        } ?: emptyList()
        return UndoSnapshot(Instant.ofEpochMilli(at), ids, entries)
    }

    fun deviceKeyToJson(k: DeviceKey): JsonObject = buildJsonObject {
        when (k) {
            DeviceKey.HvacTemp -> put("type", "hvac_temp")
            DeviceKey.HvacFan -> put("type", "hvac_fan")
            DeviceKey.HvacAir -> put("type", "hvac_air")
            is DeviceKey.SeatHeater -> { put("type", "seat_heater"); put("seat", k.seat.wire) }
            DeviceKey.SteeringHeater -> put("type", "steering_heater")
            DeviceKey.MirrorHeater -> put("type", "mirror_heater")
            DeviceKey.AmbientLight -> put("type", "ambient_light")
        }
    }

    fun deviceKeyFromJson(e: JsonElement?): DeviceKey? {
        val o = e as? JsonObject ?: return null
        return when (o.str("type")) {
            "hvac_temp" -> DeviceKey.HvacTemp
            "hvac_fan" -> DeviceKey.HvacFan
            "hvac_air" -> DeviceKey.HvacAir
            "seat_heater" -> DeviceKey.SeatHeater(Seat.fromWire(o.str("seat") ?: return null) ?: return null)
            "steering_heater" -> DeviceKey.SteeringHeater
            "mirror_heater" -> DeviceKey.MirrorHeater
            "ambient_light" -> DeviceKey.AmbientLight
            else -> null
        }
    }

    fun deviceValueToJson(v: DeviceValue): JsonObject = buildJsonObject {
        when (v) {
            is DeviceValue.Temperature -> { put("kind", "temp"); put("c", v.celsius) }
            is DeviceValue.Level -> { put("kind", "level"); put("v", v.level) }
            is DeviceValue.Air -> { put("kind", "air"); put("mode", v.mode.wire) }
            is DeviceValue.OnOff -> { put("kind", "onoff"); put("on", v.on) }
            is DeviceValue.Ambient -> {
                put("kind", "ambient"); put("on", v.on)
                v.color?.let { put("color", it.wire) }
                v.brightness?.let { put("brightness", it.wire) }
            }
        }
    }

    fun deviceValueFromJson(e: JsonElement?): DeviceValue? {
        val o = e as? JsonObject ?: return null
        return when (o.str("kind")) {
            "temp" -> DeviceValue.Temperature(o.num("c") ?: return null)
            "level" -> DeviceValue.Level(o.int("v") ?: return null)
            "air" -> DeviceValue.Air(AirMode.fromWire(o.str("mode") ?: return null) ?: return null)
            "onoff" -> DeviceValue.OnOff(o.bool("on") ?: return null)
            "ambient" -> DeviceValue.Ambient(
                on = o.bool("on") ?: return null,
                color = o.str("color")?.let { AmbientColor.fromWire(it) },
                brightness = o.str("brightness")?.let { AmbientBrightness.fromWire(it) },
            )
            else -> null
        }
    }

    /** 동작 하나의 계약 JSON. RoutineJson의 동작 변환을 그대로 쓰려고 동작 1개짜리 루틴으로 감싼다. */
    fun actionToJson(a: Action): JsonElement {
        val wrapper = RoutineSpec(name = "_", trigger = null, actions = listOf(ActionSpec(a)))
        return (RoutineJson.toJson(wrapper)["actions"] as JsonArray).first()
    }

    fun actionFromJson(e: JsonElement?): Action? {
        if (e == null || e is JsonNull) return null
        val wrapper = buildJsonObject {
            put("schemaVersion", 1)
            put("name", "_")
            put("trigger", JsonNull)
            put("actions", buildJsonArray { add(e) })
        }
        return when (val r = RoutineJson.parse(wrapper)) {
            is RoutineJson.Result.Ok -> r.spec.actions.first().action
            is RoutineJson.Result.Error -> null
        }
    }

    // ---------- run log (요약만 남긴다) ----------

    fun runLogToJson(l: RunLog): JsonObject = buildJsonObject {
        put("at", l.at.toEpochMilli())
        put("source", l.source.name)
        put("routineIds", buildJsonArray { l.routineIds.forEach { add(JsonPrimitive(it)) } })
        put("trigger", l.trigger?.let { triggerLabel(it) } ?: "")
        put("outcomes", buildJsonArray { l.outcomes.forEach { add(outcomeToJson(it)) } })
    }

    private fun outcomeToJson(o: ActionOutcome): JsonObject = buildJsonObject {
        put("routineId", o.routineId)
        put("type", o.spec.action.type)
        put("status", o.status.name)
        o.executed?.let { put("executed", it.type) }
        o.reason?.let { put("reason", it) }
    }

    fun triggerLabel(t: TriggerEvent): String = when (t) {
        is TriggerEvent.LocationEntered -> "enter:${t.place.wire}"
        is TriggerEvent.LocationExited -> "exit:${t.place.wire}"
        is TriggerEvent.EnergyDropped -> "energy:${t.source.wire}:${t.from}->${t.to}"
        TriggerEvent.WiperTurnedOn -> "wiper_on"
    }

    // ---------- helpers ----------

    private fun JsonObject.str(k: String): String? = (this[k] as? JsonPrimitive)?.takeIf { it.isString }?.content
    private fun JsonObject.int(k: String): Int? = (this[k] as? JsonPrimitive)?.takeIf { !it.isString }?.intOrNull
    private fun JsonObject.long(k: String): Long? = (this[k] as? JsonPrimitive)?.takeIf { !it.isString }?.longOrNull
    private fun JsonObject.num(k: String): Double? = (this[k] as? JsonPrimitive)?.takeIf { !it.isString }?.doubleOrNull
    private fun JsonObject.bool(k: String): Boolean? = (this[k] as? JsonPrimitive)?.takeIf { !it.isString }?.booleanOrNull
}
