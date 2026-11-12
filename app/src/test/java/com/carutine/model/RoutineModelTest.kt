package com.carutine.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RoutineModelTest {

    private val template1 = """
        {
          "schemaVersion": 1,
          "id": "rt_01HF8Z",
          "name": "퇴근길 겨울",
          "description": "회사를 나설 때 추우면 핸들과 시트를 데워요",
          "category": "commute",
          "tags": ["겨울", "출퇴근"],
          "trigger": { "type": "location_exit", "place": "work" },
          "conditions": [ { "type": "cabin_temp", "op": "below", "value": 15 } ],
          "actions": [
            { "type": "steering_heater", "on": true,
              "fallback": { "type": "hvac_temp", "mode": "delta", "value": 2 } },
            { "type": "seat_heater", "seat": "driver", "level": 2 },
            { "type": "ambient_light", "on": true, "color": "warm" },
            { "type": "speak", "text": "퇴근 루틴을 실행했어요" }
          ],
          "unknownFutureField": 1
        }
    """.trimIndent()

    private fun parseOk(json: String) = (RoutineJson.parse(json) as RoutineJson.Result.Ok).spec

    @Test fun `계약 예시 JSON을 읽고 다시 쓰면 같은 값이 된다`() {
        val spec = parseOk(template1)
        assertEquals("퇴근길 겨울", spec.name)
        assertEquals(Trigger.LocationExit(PlaceKey.WORK), spec.trigger)
        assertEquals(Action.HvacTemp(HvacTempMode.DELTA, 2.0), spec.actions[0].fallback)
        assertEquals(spec, parseOk(RoutineJson.encode(spec)))
        assertTrue(RoutineValidator.validate(spec).isEmpty())
    }

    @Test fun `모르는 동작 타입과 이중 fallback, 잘못된 시각은 거부한다`() {
        val unknown = template1.replace("\"seat_heater\"", "\"seat_massage\"")
        assertTrue(RoutineJson.parse(unknown) is RoutineJson.Result.Error)

        val nested = template1.replace(
            "\"value\": 2 } }",
            "\"value\": 2, \"fallback\": { \"type\": \"hvac_fan\", \"level\": 1 } } }",
        )
        assertTrue(RoutineJson.parse(nested) is RoutineJson.Result.Error)

        val badTime = """{"schemaVersion":1,"name":"a","trigger":{"type":"wiper_on"},
            "conditions":[{"type":"time_window","days":["mon"],"start":"7:00","end":"09:00"}],
            "actions":[{"type":"hvac_fan","level":1}]}"""
        assertTrue(RoutineJson.parse(badTime) is RoutineJson.Result.Error)
    }

    @Test fun `계약 필드 규칙을 서버와 같은 기준으로 검사한다`() {
        val base = parseOk(template1)
        fun reasons(s: RoutineSpec) = RoutineValidator.validate(s).map { it.field to it.reason }

        assertTrue(reasons(base.copy(name = "가".repeat(21))).contains("name" to ValidationError.Reason.TOO_LONG))
        assertTrue(reasons(base.copy(trigger = null)).contains("conditions" to ValidationError.Reason.CONDITIONS_WITHOUT_TRIGGER))
        assertTrue(reasons(base.copy(conditions = base.conditions + base.conditions)).contains("conditions" to ValidationError.Reason.DUPLICATE))
        assertTrue(reasons(base.copy(actions = List(6) { base.actions[1] })).contains("actions" to ValidationError.Reason.TOO_MANY))
        assertTrue(reasons(base.copy(actions = listOf(ActionSpec(Action.HvacTemp(HvacTempMode.DELTA, 0.0)))))
            .contains("actions[0].value" to ValidationError.Reason.OUT_OF_RANGE))
        assertTrue(reasons(base.copy(actions = listOf(ActionSpec(Action.Speak("가".repeat(41))))))
            .contains("actions[0].text" to ValidationError.Reason.TOO_LONG))
        assertTrue(reasons(base.copy(tags = List(6) { "태그" }))
            .contains("tags" to ValidationError.Reason.TOO_MANY))
    }

    @Test fun `편집 화면 이름 규칙은 2~12자와 중복 금지다`() {
        assertEquals("2~12자로 지어 주세요", EditorRules.nameError("퇴", emptyList()))
        assertEquals("같은 이름의 루틴이 있어요", EditorRules.nameError("퇴근 루틴", listOf("퇴근 루틴")))
        assertEquals(null, EditorRules.nameError("퇴근 루틴", listOf("비 오는 밤")))
    }

    @Test fun `호환 판정은 백엔드 의사코드와 같다`() {
        val spec = parseOk(template1)
        val all = Cap.entries.toSet()
        assertEquals(CompatLevel.FULL, CompatChecker.check(spec, all).level)

        // 핸들 열선 없음 → fallback(설정 온도)로 대체 → partial
        val noSteer = all - Cap.STEERING_HEATER
        assertEquals(CompatLevel.PARTIAL, CompatChecker.check(spec, noSteer).level)
        assertEquals(listOf("steering_heater"), CompatChecker.fallbackActions(spec, noSteer))
        assertEquals(emptyList<String>(), CompatChecker.unsupportedActions(spec, noSteer))

        // 앰비언트 없음 → 미지원 1개 → partial
        val noAmbient = all - Cap.LIGHT_AMBIENT
        assertEquals(CompatLevel.PARTIAL, CompatChecker.check(spec, noAmbient).level)
        assertEquals(listOf("ambient_light"), CompatChecker.unsupportedActions(spec, noAmbient))

        // 실내 온도를 못 읽음 → 조건 미지원 → none
        assertEquals(CompatLevel.NONE, CompatChecker.check(spec, all - Cap.HVAC_CABIN_TEMP).level)

        // 실행 가능한 동작 0개 → none
        val onlyFan = spec.copy(actions = listOf(ActionSpec(Action.SeatHeater(Seat.DRIVER, 1))))
        assertEquals(CompatLevel.NONE, CompatChecker.check(onlyFan, all - Cap.SEAT_HEATER).level)
    }

    @Test fun `caps 쿼리 문자열은 고정 순서다`() {
        assertEquals("location,hvac.temp,tts", Cap.toQuery(setOf(Cap.TTS, Cap.HVAC_TEMP, Cap.LOCATION)))
    }
}
