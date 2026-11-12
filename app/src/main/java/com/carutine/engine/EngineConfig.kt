package com.carutine.engine

import java.time.Duration

/**
 * 엔진 숫자 값. UX 기획자가 바꿀 수 있으므로 상수 한 곳에 모은다.
 * 근거: 작업 문서 5장, UX 결정 D4(되돌리기 10분, 재실행 방지 30분은 자동 실행만), D5(제안 10초), D8(반경 200m 고정).
 */
data class EngineConfig(
    val radiusM: Double = 200.0,
    val hysteresisM: Double = 50.0,
    val cooldown: Duration = Duration.ofMinutes(30),
    val undoWindow: Duration = Duration.ofMinutes(10),
    val proposalTimeout: Duration = Duration.ofSeconds(10),
    /** delta 계산 결과를 자르는 범위. capability(HvacTemperatureConfigArray)를 읽으면 그 값으로 바꾼다. */
    val hvacTempMin: Double = 16.0,
    val hvacTempMax: Double = 30.0,
    /** 되돌리기 때 '사용자가 직접 바꿨는지' 비교할 온도 허용 오차(차량이 0.5℃ 단위로 반올림할 수 있음). */
    val tempTolerance: Double = 0.25,
)
