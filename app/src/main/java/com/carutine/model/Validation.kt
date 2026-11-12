package com.carutine.model

/**
 * 계약 v0.1의 규칙(백엔드 문서 3장 표 + 6장 '구조' 규칙)을 그대로 검사한다.
 * 서버와 같은 기준이어야 앱에서 통과한 루틴이 공유(POST /v1/routines) 때 400을 받지 않는다.
 *
 * UX 화면 규칙(이름 2~12자, A8 30자 등)은 계약보다 좁은 '편집 화면 규칙'이라 [EditorRules]에 따로 둔다.
 */
data class ValidationError(val field: String, val reason: Reason) {
    enum class Reason {
        TOO_SHORT, TOO_LONG, TOO_MANY, TOO_FEW, OUT_OF_RANGE, DUPLICATE,
        CONDITIONS_WITHOUT_TRIGGER, UNSUPPORTED_SCHEMA_VERSION, BLANK,
    }
}

object RoutineValidator {
    const val SCHEMA_VERSION = 1

    fun validate(spec: RoutineSpec): List<ValidationError> = buildList {
        fun err(field: String, r: ValidationError.Reason) = add(ValidationError(field, r))

        if (spec.schemaVersion != SCHEMA_VERSION) err("schemaVersion", ValidationError.Reason.UNSUPPORTED_SCHEMA_VERSION)

        // name 1~20자
        when {
            spec.name.isBlank() -> err("name", ValidationError.Reason.BLANK)
            spec.name.codePointCount() > 20 -> err("name", ValidationError.Reason.TOO_LONG)
        }
        if (spec.description.codePointCount() > 80) err("description", ValidationError.Reason.TOO_LONG)

        // tags 최대 5개, 각 1~10자
        if (spec.tags.size > 5) err("tags", ValidationError.Reason.TOO_MANY)
        spec.tags.forEachIndexed { i, t ->
            if (t.isBlank()) err("tags[$i]", ValidationError.Reason.BLANK)
            else if (t.codePointCount() > 10) err("tags[$i]", ValidationError.Reason.TOO_LONG)
        }

        // trigger
        when (val t = spec.trigger) {
            is Trigger.EnergyBelow -> if (t.percent !in 1..99) err("trigger.percent", ValidationError.Reason.OUT_OF_RANGE)
            else -> Unit
        }

        // conditions 0~2, AND, 같은 타입 중복 금지, trigger가 null이면 비어 있어야 함
        if (spec.conditions.size > 2) err("conditions", ValidationError.Reason.TOO_MANY)
        if (spec.trigger == null && spec.conditions.isNotEmpty()) {
            err("conditions", ValidationError.Reason.CONDITIONS_WITHOUT_TRIGGER)
        }
        if (spec.conditions.map { it.type }.toSet().size != spec.conditions.size) {
            err("conditions", ValidationError.Reason.DUPLICATE)
        }
        spec.conditions.forEachIndexed { i, c ->
            when (c) {
                is Condition.TimeWindow -> if (c.days.isEmpty()) err("conditions[$i].days", ValidationError.Reason.TOO_FEW)
                is Condition.CabinTemp -> if (c.value !in -20..50) err("conditions[$i].value", ValidationError.Reason.OUT_OF_RANGE)
            }
        }

        // actions 1~5
        when {
            spec.actions.isEmpty() -> err("actions", ValidationError.Reason.TOO_FEW)
            spec.actions.size > 5 -> err("actions", ValidationError.Reason.TOO_MANY)
        }
        spec.actions.forEachIndexed { i, a ->
            validateAction(a.action, "actions[$i]").forEach { add(it) }
            a.fallback?.let { fb -> validateAction(fb, "actions[$i].fallback").forEach { add(it) } }
        }
    }

    private fun validateAction(a: Action, path: String): List<ValidationError> = buildList {
        fun err(field: String, r: ValidationError.Reason) = add(ValidationError("$path.$field", r))
        when (a) {
            is Action.HvacTemp -> when (a.mode) {
                HvacTempMode.SET -> if (a.value < 16.0 || a.value > 30.0) err("value", ValidationError.Reason.OUT_OF_RANGE)
                HvacTempMode.DELTA -> if (a.value < -3.0 || a.value > 3.0 || a.value == 0.0) err("value", ValidationError.Reason.OUT_OF_RANGE)
            }
            is Action.HvacFan -> if (a.level !in 0..10) err("level", ValidationError.Reason.OUT_OF_RANGE)
            is Action.SeatHeater -> if (a.level !in 0..3) err("level", ValidationError.Reason.OUT_OF_RANGE)
            is Action.Speak -> when {
                a.text.isBlank() -> err("text", ValidationError.Reason.BLANK)
                a.text.codePointCount() > 40 -> err("text", ValidationError.Reason.TOO_LONG)
            }
            else -> Unit
        }
    }

    private fun String.codePointCount(): Int = codePointCount(0, length)
}

/**
 * 편집 화면(S04~S06) 규칙. UX 1차 산출물 3장 기준이며 계약보다 좁다.
 * - 이름 2~12자(음성 호출·한 줄 알림), A8 문장 최대 30자.
 * 계약 검증([RoutineValidator])을 통과한 뒤 추가로 검사한다.
 */
object EditorRules {
    const val NAME_MIN = 2
    const val NAME_MAX = 12
    const val SPEAK_MAX = 30

    fun nameError(name: String, existingNames: Collection<String>): String? {
        val n = name.trim()
        val len = n.codePointCount(0, n.length)
        return when {
            len < NAME_MIN || len > NAME_MAX -> "2~12자로 지어 주세요"
            existingNames.any { it.trim() == n } -> "같은 이름의 루틴이 있어요"
            else -> null
        }
    }
}
