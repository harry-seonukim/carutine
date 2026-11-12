package com.carutine.engine

import com.carutine.engine.Josa.Pair.EUL_REUL
import com.carutine.engine.Josa.Pair.EUN_NEUN
import com.carutine.engine.Josa.Pair.GWA_WA
import com.carutine.model.Action

/** S12 실행 알림 종류(UX 1차 산출물 6장). */
enum class NoticeKind { R1, R2, R3, R4, R5, R6, R7 }

data class Notice(val kind: NoticeKind, val text: String)

/**
 * S12 주 문구 = 음성 문장(UX 6장 '화면 = 음성'). 문구 원문은 UX 정의서 그대로이며 바뀌면 이 파일만 고친다.
 */
object Announcer {

    /** 동작 이름: UX 작업 문서 3장 기능 목록의 이름. */
    fun actionName(type: String): String = when (type) {
        "hvac_temp" -> "설정 온도"
        "hvac_fan" -> "풍량"
        "hvac_air" -> "내기/외기"
        "seat_heater" -> "시트 열선"
        "steering_heater" -> "핸들 열선"
        "mirror_heater" -> "사이드미러 열선"
        "ambient_light" -> "앰비언트 라이트"
        "speak" -> "음성 안내"
        "suggest_charger" -> "근처 충전소 경유지 제안"
        else -> type
    }

    /**
     * @param names 실행된 루틴 이름(우선순위 순)
     * @param speakTexts 루틴 하나일 때 A8 문장(D10: 있으면 기본 결과 문장 대신 읽음)
     * @param appliedCount 실제로 바뀐 장치 수
     * @param failed 실패한 동작(실행 순서대로)
     */
    fun executionNotice(
        names: List<String>,
        speakTexts: List<String>,
        appliedCount: Int,
        failed: List<Action>,
    ): Notice {
        require(names.isNotEmpty())
        val first = names.first()
        // R3: 실행된 동작이 없음. speak만 있는 수동 루틴(예: '연료 부족 알림')은 실패가 아니므로 R1로 둔다.
        if (appliedCount == 0 && failed.isNotEmpty()) {
            return Notice(NoticeKind.R3, "${Josa.attach(first, EUL_REUL)} 실행하지 못했어요")
        }
        val base = when {
            names.size == 1 && speakTexts.isNotEmpty() -> speakTexts.joinToString(" ")
            names.size == 1 -> "${Josa.attach(first, EUL_REUL)} 실행했어요"
            names.size == 2 -> "${Josa.attach(first, GWA_WA)} ${Josa.attach(names[1], EUL_REUL)} 실행했어요"
            else -> "루틴 ${names.size}개를 실행했어요"
        }
        return when {
            failed.isEmpty() -> Notice(if (names.size > 1) NoticeKind.R4 else NoticeKind.R1, base)
            failed.size == 1 -> {
                val f = failed.single()
                Notice(NoticeKind.R2, "${base.trimEnd('.')}. ${Josa.attach(actionName(f.type), EUN_NEUN)} ${failVerb(f)}")
            }
            else -> Notice(NoticeKind.R2, "${base.trimEnd('.')}. ${failed.size}개 동작은 실행하지 못했어요")
        }
    }

    /** UX 6장: 열선·앰비언트는 '켜지 않았어요', 온도·풍량·내기/외기는 '바꾸지 못했어요'. */
    private fun failVerb(a: Action): String = when (a) {
        is Action.HvacTemp, is Action.HvacFan, is Action.HvacAir -> "바꾸지 못했어요"
        else -> "켜지 않았어요"
    }

    const val PROPOSAL = "근처 충전소를 경유지로 추가할까요?"
    const val PROPOSAL_ACCEPTED = "경유지로 추가했어요"
    const val PROPOSAL_REJECTED = "알겠어요"
    const val PROPOSAL_FAILED = "경유지를 추가하지 못했어요"
    const val UNDO_DONE = "원래대로 돌렸어요"
    const val UNDO_NOTHING = "되돌릴 루틴이 없어요"
}
