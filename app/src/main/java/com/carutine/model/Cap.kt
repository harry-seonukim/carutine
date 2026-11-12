package com.carutine.model

/**
 * 차량 지원 기능 키. 루틴 데이터 계약 v0.1(백엔드 문서 3장)의 "Cap 키 전체"와 1:1로 같다.
 * key 문자열은 갤러리 API의 `caps` 쿼리에 그대로 쓰므로 바꾸지 않는다.
 */
enum class Cap(val key: String) {
    LOCATION("location"),
    TIME("time"),
    EV_BATTERY("ev.battery"),
    FUEL_LEVEL("fuel.level"),
    WIPER_STATE("wiper.state"),
    HVAC_CABIN_TEMP("hvac.cabinTemp"),
    HVAC_TEMP("hvac.temp"),
    HVAC_FAN("hvac.fan"),
    HVAC_AIR("hvac.air"),
    SEAT_HEATER("seat.heater"),
    STEERING_HEATER("steeringwheel.heater"),
    MIRROR_HEATER("sidemirror.heater"),
    LIGHT_AMBIENT("light.ambient"),
    TTS("tts"),
    NAVI_WAYPOINT("navi.waypoint");

    companion object {
        private val byKey = entries.associateBy { it.key }
        fun fromKey(key: String): Cap? = byKey[key]

        /** 갤러리 API `caps` 파라미터 값. 순서는 고정(enum 순서)해 캐시 키가 흔들리지 않게 한다. */
        fun toQuery(caps: Set<Cap>): String =
            entries.filter { it in caps }.joinToString(",") { it.key }
    }
}
