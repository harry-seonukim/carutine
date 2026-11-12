package com.carutine.model

/**
 * 호환 판정. 백엔드 문서 6장의 의사코드와 같은 규칙이다(앱과 서버가 같은 결과를 내야 한다).
 *
 * triggerOk  = triggerCaps ⊆ caps
 * supported  = action cap ∈ caps
 * viaFallback = !supported && fallback cap ∈ caps
 * none    : !triggerOk || 실행 가능 동작 0개
 * full    : 모든 동작이 원래 동작으로 지원
 * partial : 그 외 (fallback 대체 실행 포함)
 */
enum class CompatLevel(val wire: String) { FULL("full"), PARTIAL("partial"), NONE("none") }

enum class ActionSupport { SUPPORTED, VIA_FALLBACK, UNSUPPORTED }

data class CompatResult(
    val level: CompatLevel,
    val triggerOk: Boolean,
    /** spec.actions와 같은 순서. */
    val perAction: List<ActionSupport>,
    /** trigger·conditions 중 차량이 지원하지 않는 Cap. */
    val missingTriggerCaps: Set<Cap>,
) {
    val runnableCount: Int get() = perAction.count { it != ActionSupport.UNSUPPORTED }
}

object CompatChecker {

    fun supportOf(spec: ActionSpec, caps: Set<Cap>): ActionSupport = when {
        spec.action.requiredCap in caps -> ActionSupport.SUPPORTED
        spec.fallback != null && spec.fallback.requiredCap in caps -> ActionSupport.VIA_FALLBACK
        else -> ActionSupport.UNSUPPORTED
    }

    fun check(spec: RoutineSpec, caps: Set<Cap>): CompatResult {
        val missing = spec.triggerCaps() - caps
        val per = spec.actions.map { supportOf(it, caps) }
        val runnable = per.count { it != ActionSupport.UNSUPPORTED }
        val level = when {
            missing.isNotEmpty() || runnable == 0 -> CompatLevel.NONE
            per.all { it == ActionSupport.SUPPORTED } -> CompatLevel.FULL
            else -> CompatLevel.PARTIAL
        }
        return CompatResult(level, missing.isEmpty(), per, missing)
    }

    /** 서버 카드의 unsupportedActions / fallbackActions와 같은 형태. */
    fun unsupportedActions(spec: RoutineSpec, caps: Set<Cap>): List<String> =
        spec.actions.filter { supportOf(it, caps) == ActionSupport.UNSUPPORTED }.map { it.action.type }

    fun fallbackActions(spec: RoutineSpec, caps: Set<Cap>): List<String> =
        spec.actions.filter { supportOf(it, caps) == ActionSupport.VIA_FALLBACK }.map { it.action.type }
}
