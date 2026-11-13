package com.carutine.vehicle.pleos

import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * 차량(Pleos SDK) 호출 진단 로그. 최근 [CAPACITY]줄만 메모리에 둔다(파일·서버로 보내지 않는다. 좌표는 기록하지 않는다).
 *
 * 남기는 것: 모든 SDK 호출(이름, area, 값, onComplete/onFailed/timeout, 예외 메시지, 소요 ms),
 * capability 조회 결과(accessType·영역·범위), HVAC 전원 상태, set 후 반영 확인(read-back) 결과,
 * 리스너 등록 성공/실패, 와이퍼 콜백 원시값→매핑 결과→VehicleEvent 방출 여부.
 * 시동 상태(IgnitionState)는 SDK에 공개 조회 API가 없어(@hide 열거형만 있음) 기록하지 않는다.
 *
 * 앱(설정 > 진단)에서 쓰는 법:
 * ```
 * // Compose: 화면을 열 때·새로고침 버튼에서 다시 읽는다(스냅샷이라 자동 갱신되지 않음)
 * var lines by remember { mutableStateOf(PleosDiagnostics.lines()) }
 * lines.asReversed().forEach { Text(it, style = MaterialTheme.typography.bodySmall) }  // 최신이 위
 * ```
 * logcat에도 같은 내용이 태그 `PleosGateway`로 나간다.
 */
object PleosDiagnostics {
    const val CAPACITY = 200

    private val buf = ArrayDeque<String>(CAPACITY)
    private val fmt = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")

    /** 한 줄 추가(앞에 시각을 붙인다). 가득 차면 가장 오래된 줄을 버린다. */
    fun log(message: String) {
        val line = "${LocalTime.now().format(fmt)} $message"
        synchronized(buf) {
            if (buf.size >= CAPACITY) buf.removeFirst()
            buf.addLast(line)
        }
    }

    /** 최근 로그 스냅샷(오래된 것 → 최신 순). */
    fun lines(): List<String> = synchronized(buf) { buf.toList() }

    fun clear() = synchronized(buf) { buf.clear() }
}
