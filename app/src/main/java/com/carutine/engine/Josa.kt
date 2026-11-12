package com.carutine.engine

/**
 * 루틴 이름 뒤 조사 처리(UX 1차 산출물 0장 문구 규칙: 을/를, 과/와, 은/는).
 * 마지막 글자 받침으로 고른다. 한글이 아니면 숫자는 읽는 소리 기준, 그 외는 받침 없음으로 본다.
 */
object Josa {
    enum class Pair(val withFinal: String, val withoutFinal: String) {
        EUL_REUL("을", "를"), GWA_WA("과", "와"), EUN_NEUN("은", "는"), I_GA("이", "가"),
    }

    fun attach(word: String, pair: Pair): String = word + (if (hasFinal(word)) pair.withFinal else pair.withoutFinal)

    fun hasFinal(word: String): Boolean {
        val c = word.trimEnd().lastOrNull() ?: return false
        return when {
            c in '가'..'힣' -> (c - '가') % 28 != 0
            c.isDigit() -> c in "013678"   // 영 일 삼 육 칠 팔
            else -> false
        }
    }
}
