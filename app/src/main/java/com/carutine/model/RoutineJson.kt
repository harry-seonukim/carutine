package com.carutine.model

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.put
import java.time.DayOfWeek
import java.time.LocalTime

/**
 * 계약 JSON <-> [RoutineSpec] 변환.
 *
 * kotlinx.serialization의 JsonElement 트리 API만 쓴다(컴파일러 플러그인 불필요).
 * 모르는 키는 무시해 서버가 필드를 추가해도 앱이 깨지지 않게 하고, 모르는 type 값은 실패로 돌려
 * 엔진이 해석할 수 없는 루틴을 저장하지 않는다.
 */
object RoutineJson {

    sealed interface Result {
        data class Ok(val spec: RoutineSpec) : Result
        data class Error(val path: String, val message: String) : Result
    }

    private class ParseError(val path: String, msg: String) : Exception(msg)

    private val json = Json { prettyPrint = false }

    fun parse(text: String): Result = try {
        parse(json.parseToJsonElement(text))
    } catch (e: ParseError) {
        Result.Error(e.path, e.message ?: "parse error")
    } catch (e: IllegalArgumentException) {
        Result.Error("$", e.message ?: "invalid json")
    }

    fun parse(element: JsonElement): Result = try {
        Result.Ok(readSpec(element.obj("$")))
    } catch (e: ParseError) {
        Result.Error(e.path, e.message ?: "parse error")
    }

    /** 시드 파일처럼 배열로 된 루틴 목록. 하나라도 실패하면 (index, Error) 목록을 함께 돌려준다. */
    fun parseList(text: String): Pair<List<RoutineSpec>, List<Pair<Int, Result.Error>>> {
        val root = json.parseToJsonElement(text)
        val arr = when (root) {
            is JsonArray -> root
            is JsonObject -> (root["routines"] as? JsonArray) ?: throw IllegalArgumentException("routines 배열 없음")
            else -> throw IllegalArgumentException("배열이 아님")
        }
        val ok = mutableListOf<RoutineSpec>()
        val bad = mutableListOf<Pair<Int, Result.Error>>()
        arr.forEachIndexed { i, el ->
            val target = (el as? JsonObject)?.get("spec") ?: el   // { spec: {...} } 래퍼도 허용
            when (val r = parse(target)) {
                is Result.Ok -> ok += r.spec
                is Result.Error -> bad += i to r
            }
        }
        return ok to bad
    }

    fun encode(spec: RoutineSpec): String = json.encodeToString(JsonElement.serializer(), toJson(spec))

    fun toJson(spec: RoutineSpec): JsonObject = buildJsonObject {
        put("schemaVersion", spec.schemaVersion)
        spec.id?.let { put("id", it) }
        put("name", spec.name)
        put("description", spec.description)
        put("category", spec.category.wire)
        put("tags", buildJsonArray { spec.tags.forEach { add(JsonPrimitive(it)) } })
        put("trigger", spec.trigger?.let { triggerJson(it) } ?: JsonNull)
        put("conditions", buildJsonArray { spec.conditions.forEach { add(conditionJson(it)) } })
        put("actions", buildJsonArray {
            spec.actions.forEach { a ->
                val base = actionJson(a.action)
                add(if (a.fallback == null) base else JsonObject(base + ("fallback" to actionJson(a.fallback))))
            }
        })
    }

    // ---------- read ----------

    private fun readSpec(o: JsonObject): RoutineSpec {
        val trigger = o["trigger"].let { if (it == null || it is JsonNull) null else readTrigger(it.obj("$.trigger")) }
        return RoutineSpec(
            schemaVersion = o.int("schemaVersion", "$"),
            id = o.optString("id"),
            name = o.str("name", "$"),
            description = o.optString("description") ?: "",
            category = o.optString("category")?.let {
                Category.fromWire(it) ?: throw ParseError("$.category", "unknown category: $it")
            } ?: Category.ETC,
            tags = (o["tags"] as? JsonArray)?.mapIndexed { i, t -> t.prim("$.tags[$i]").content } ?: emptyList(),
            trigger = trigger,
            conditions = (o["conditions"] as? JsonArray)?.mapIndexed { i, c -> readCondition(c.obj("$.conditions[$i]"), "$.conditions[$i]") }
                ?: emptyList(),
            actions = (o["actions"] as? JsonArray ?: throw ParseError("$.actions", "actions 배열 없음"))
                .mapIndexed { i, a ->
                    val ao = a.obj("$.actions[$i]")
                    val fb = ao["fallback"]?.takeIf { it !is JsonNull }?.obj("$.actions[$i].fallback")
                    if (fb != null && fb.containsKey("fallback")) throw ParseError("$.actions[$i].fallback", "fallback 안에 fallback 불가")
                    ActionSpec(readAction(ao, "$.actions[$i]"), fb?.let { readAction(it, "$.actions[$i].fallback") })
                },
        )
    }

    private fun readTrigger(o: JsonObject): Trigger {
        val p = "$.trigger"
        return when (val type = o.str("type", p)) {
            "location_enter" -> Trigger.LocationEnter(place(o, p))
            "location_exit" -> Trigger.LocationExit(place(o, p))
            "energy_below" -> Trigger.EnergyBelow(
                EnergySource.fromWire(o.str("source", p)) ?: throw ParseError("$p.source", "unknown source"),
                o.int("percent", p),
            )
            "wiper_on" -> Trigger.WiperOn
            else -> throw ParseError("$p.type", "trigger 타입이 아님: $type")
        }
    }

    private fun place(o: JsonObject, p: String): PlaceKey =
        o.str("place", p).let { PlaceKey.fromWire(it) ?: throw ParseError("$p.place", "허용되지 않은 장소 키: $it") }

    private fun readCondition(o: JsonObject, p: String): Condition = when (val type = o.str("type", p)) {
        "time_window" -> Condition.TimeWindow(
            days = (o["days"] as? JsonArray ?: throw ParseError("$p.days", "days 배열 없음"))
                .mapIndexed { i, d -> day(d.prim("$p.days[$i]").content, "$p.days[$i]") }
                .also { if (it.toSet().size != it.size) throw ParseError("$p.days", "중복 요일") }
                .toSet(),
            start = hhmm(o.str("start", p), "$p.start"),
            end = hhmm(o.str("end", p), "$p.end"),
        )
        "cabin_temp" -> Condition.CabinTemp(
            TempOp.fromWire(o.str("op", p)) ?: throw ParseError("$p.op", "unknown op"),
            o.int("value", p),
        )
        else -> throw ParseError("$p.type", "condition 타입이 아님: $type")
    }

    private fun readAction(o: JsonObject, p: String): Action = when (val type = o.str("type", p)) {
        "hvac_temp" -> Action.HvacTemp(
            HvacTempMode.fromWire(o.str("mode", p)) ?: throw ParseError("$p.mode", "unknown mode"),
            o.num("value", p),
        )
        "hvac_fan" -> Action.HvacFan(o.int("level", p))
        "hvac_air" -> Action.HvacAir(AirMode.fromWire(o.str("mode", p)) ?: throw ParseError("$p.mode", "unknown mode"))
        "seat_heater" -> Action.SeatHeater(
            Seat.fromWire(o.str("seat", p)) ?: throw ParseError("$p.seat", "unknown seat"),
            o.int("level", p),
        )
        "steering_heater" -> Action.SteeringHeater(o.bool("on", p))
        "mirror_heater" -> Action.MirrorHeater(o.bool("on", p))
        "ambient_light" -> Action.AmbientLight(
            on = o.bool("on", p),
            color = o.optString("color")?.let { AmbientColor.fromWire(it) ?: throw ParseError("$p.color", "unknown color") },
            brightness = o.optString("brightness")?.let { AmbientBrightness.fromWire(it) ?: throw ParseError("$p.brightness", "unknown brightness") },
        )
        "speak" -> Action.Speak(o.str("text", p))
        "suggest_charger" -> Action.SuggestCharger
        else -> throw ParseError("$p.type", "action 타입이 아님: $type")
    }

    private val DAYS = listOf(
        "mon" to DayOfWeek.MONDAY, "tue" to DayOfWeek.TUESDAY, "wed" to DayOfWeek.WEDNESDAY,
        "thu" to DayOfWeek.THURSDAY, "fri" to DayOfWeek.FRIDAY, "sat" to DayOfWeek.SATURDAY, "sun" to DayOfWeek.SUNDAY,
    )

    private fun day(v: String, p: String) = DAYS.firstOrNull { it.first == v }?.second ?: throw ParseError(p, "unknown day: $v")
    private fun dayWire(d: DayOfWeek) = DAYS.first { it.second == d }.first

    private val HHMM = Regex("^([01]\\d|2[0-3]):([0-5]\\d)$")
    private fun hhmm(v: String, p: String): LocalTime {
        val m = HHMM.matchEntire(v) ?: throw ParseError(p, "HH:mm 형식 아님: $v")
        return LocalTime.of(m.groupValues[1].toInt(), m.groupValues[2].toInt())
    }

    // ---------- write ----------

    private fun triggerJson(t: Trigger): JsonObject = buildJsonObject {
        put("type", t.type)
        when (t) {
            is Trigger.LocationEnter -> put("place", t.place.wire)
            is Trigger.LocationExit -> put("place", t.place.wire)
            is Trigger.EnergyBelow -> { put("source", t.source.wire); put("percent", t.percent) }
            Trigger.WiperOn -> Unit
        }
    }

    private fun conditionJson(c: Condition): JsonObject = buildJsonObject {
        put("type", c.type)
        when (c) {
            is Condition.TimeWindow -> {
                put("days", buildJsonArray { DayOfWeek.entries.filter { it in c.days }.forEach { add(JsonPrimitive(dayWire(it))) } })
                put("start", "%02d:%02d".format(c.start.hour, c.start.minute))
                put("end", "%02d:%02d".format(c.end.hour, c.end.minute))
            }
            is Condition.CabinTemp -> { put("op", c.op.wire); put("value", c.value) }
        }
    }

    private fun actionJson(a: Action): JsonObject = buildJsonObject {
        put("type", a.type)
        when (a) {
            is Action.HvacTemp -> {
                put("mode", a.mode.wire)
                if (a.value % 1.0 == 0.0) put("value", a.value.toInt()) else put("value", a.value)
            }
            is Action.HvacFan -> put("level", a.level)
            is Action.HvacAir -> put("mode", a.mode.wire)
            is Action.SeatHeater -> { put("seat", a.seat.wire); put("level", a.level) }
            is Action.SteeringHeater -> put("on", a.on)
            is Action.MirrorHeater -> put("on", a.on)
            is Action.AmbientLight -> {
                put("on", a.on)
                a.color?.let { put("color", it.wire) }
                a.brightness?.let { put("brightness", it.wire) }
            }
            is Action.Speak -> put("text", a.text)
            Action.SuggestCharger -> Unit
        }
    }

    // ---------- helpers ----------

    private fun JsonElement.obj(p: String) = this as? JsonObject ?: throw ParseError(p, "객체가 아님")
    private fun JsonElement.prim(p: String) = this as? JsonPrimitive ?: throw ParseError(p, "값이 아님")
    private fun JsonObject.str(k: String, p: String): String {
        val v = this[k]?.prim("$p.$k") ?: throw ParseError("$p.$k", "필수 필드 없음")
        if (!v.isString) throw ParseError("$p.$k", "문자열 아님")
        return v.content
    }
    private fun JsonObject.optString(k: String): String? =
        (this[k] as? JsonPrimitive)?.takeIf { it.isString }?.content
    private fun JsonObject.int(k: String, p: String): Int {
        val v = this[k]?.prim("$p.$k") ?: throw ParseError("$p.$k", "필수 필드 없음")
        if (v.isString) throw ParseError("$p.$k", "정수 아님")
        return v.intOrNull ?: throw ParseError("$p.$k", "정수 아님")
    }
    private fun JsonObject.num(k: String, p: String): Double {
        val v = this[k]?.prim("$p.$k") ?: throw ParseError("$p.$k", "필수 필드 없음")
        if (v.isString) throw ParseError("$p.$k", "숫자 아님")
        return v.doubleOrNull ?: throw ParseError("$p.$k", "숫자 아님")
    }
    private fun JsonObject.bool(k: String, p: String): Boolean {
        val v = this[k]?.prim("$p.$k") ?: throw ParseError("$p.$k", "필수 필드 없음")
        if (v.isString) throw ParseError("$p.$k", "boolean 아님")
        return v.booleanOrNull ?: throw ParseError("$p.$k", "boolean 아님")
    }
}
