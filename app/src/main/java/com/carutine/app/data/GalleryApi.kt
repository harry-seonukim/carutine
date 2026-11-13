package com.carutine.app.data

import com.carutine.model.Cap
import com.carutine.model.Category
import com.carutine.model.CompatLevel
import com.carutine.model.RoutineJson
import com.carutine.model.RoutineSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.put
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/** 갤러리 카드 하나. 작성자 정보는 없다(공식 배지와 '내 루틴'만). 수치는 서버 값이 있을 때만 쓴다. */
data class GalleryItem(
    val id: String?,
    val name: String,
    val description: String,
    val category: Category,
    val tags: List<String>,
    val triggerType: String?,
    val actionTypes: List<String>,
    val isOfficial: Boolean,
    val isMine: Boolean,
    /** 오프라인(시드)은 처음부터 있고, 서버 카드는 상세를 열 때 받는다. */
    val spec: RoutineSpec?,
    /** 서버가 계산한 호환(caps를 보냈을 때). spec이 있으면 앱이 직접 계산한 값을 쓴다. */
    val serverCompat: CompatLevel?,
    val featuredRank: Int? = null,
)

data class Shelf(val id: String, val title: String, val items: List<GalleryItem>)

class ApiException(message: String, val status: Int = 0) : Exception(message)

/**
 * 백엔드 API(backend/openapi.yaml) 최소 클라이언트. 의존성을 늘리지 않으려고 HttpURLConnection을 쓴다.
 * 기기 토큰은 앱 내부 파일에만 저장한다. 보내는 데이터: Cap 키, 계약 spec(id·좌표·작성자 없음)뿐.
 */
class GalleryApi(baseUrl: String, private val dir: File, private val appVersion: String) {
    private val base = baseUrl.trim().trimEnd('/')
    val enabled: Boolean get() = base.isNotEmpty()
    private val json = Json { ignoreUnknownKeys = true }
    private val tokenFile = JsonFile(File(dir, "device.json"))

    suspend fun shelves(caps: Set<Cap>, season: String, hour: Int): List<Shelf> {
        val body = call("GET", "/v1/gallery/shelves?caps=${enc(Cap.toQuery(caps))}&season=$season&hour=$hour", null)
        val arr = (body as? JsonObject)?.get("shelves") as? JsonArray ?: throw ApiException("응답 형식이 달라요")
        return arr.mapNotNull { el ->
            val o = el as? JsonObject ?: return@mapNotNull null
            Shelf(
                id = o.s("id") ?: return@mapNotNull null,
                title = o.s("title") ?: "",
                items = (o["items"] as? JsonArray)?.mapNotNull { cardFrom(it) } ?: emptyList(),
            )
        }
    }

    suspend fun detail(id: String, caps: Set<Cap>): RoutineSpec {
        val body = call("GET", "/v1/routines/${enc(id)}?caps=${enc(Cap.toQuery(caps))}", null) as? JsonObject
            ?: throw ApiException("응답 형식이 달라요")
        val spec = body["spec"] ?: throw ApiException("루틴 내용이 없어요")
        return when (val r = RoutineJson.parse(spec)) {
            is RoutineJson.Result.Ok -> r.spec
            is RoutineJson.Result.Error -> throw ApiException("이 앱 버전에서 읽을 수 없는 루틴이에요 (${r.path})")
        }
    }

    /** 공유(올리기). 성공하면 서버 id(rt_…)를 돌려준다. */
    suspend fun share(uploadSpec: JsonObject, caps: Set<Cap>): String {
        val req = buildJsonObject { put("spec", uploadSpec) }
        val body = call("POST", "/v1/routines?caps=${enc(Cap.toQuery(caps))}", req) as? JsonObject
            ?: throw ApiException("응답 형식이 달라요")
        return body.s("id") ?: throw ApiException("응답에 id가 없어요")
    }

    /** 가져오기 집계(기기당 1회, 여러 번 불러도 안전). 실패해도 가져오기 자체는 막지 않는다. */
    suspend fun markImported(id: String) {
        call("POST", "/v1/routines/${enc(id)}/import", null)
    }

    // ------------------------------------------------------------------ internals

    private suspend fun call(method: String, path: String, body: JsonElement?): JsonElement? {
        var token = token()
        var res = request(method, path, body, token)
        if (res.first == 401) {     // 토큰이 지워졌거나 서버 DB가 초기화됨 → 한 번만 다시 등록
            tokenFile.write(null)
            token = token()
            res = request(method, path, body, token)
        }
        val (code, text) = res
        val parsed = text.takeIf { it.isNotBlank() }?.let { runCatching { json.parseToJsonElement(it) }.getOrNull() }
        if (code !in 200..299) throw ApiException(errorMessage(parsed) ?: "서버 오류 ($code)", code)
        return parsed
    }

    private suspend fun token(): String {
        (tokenFile.read() as? JsonObject)?.s("token")?.let { return it }
        val (code, text) = request("POST", "/v1/devices", buildJsonObject { put("appVersion", appVersion) }, null)
        if (code !in 200..299) {
            val parsed = runCatching { json.parseToJsonElement(text) }.getOrNull()
            throw ApiException(errorMessage(parsed) ?: "기기 등록 실패 ($code)", code)
        }
        val o = json.parseToJsonElement(text) as? JsonObject ?: throw ApiException("기기 등록 응답 형식이 달라요")
        val t = o.s("token") ?: throw ApiException("기기 등록 응답에 토큰이 없어요")
        tokenFile.write(buildJsonObject { put("token", t) })
        return t
    }

    private suspend fun request(method: String, path: String, body: JsonElement?, token: String?): Pair<Int, String> =
        withContext(Dispatchers.IO) {
            val conn = (URL(base + path).openConnection() as HttpURLConnection)
            try {
                conn.requestMethod = method
                conn.connectTimeout = 6000
                conn.readTimeout = 10000
                conn.setRequestProperty("Accept", "application/json")
                token?.let { conn.setRequestProperty("Authorization", "Bearer $it") }
                if (body != null) {
                    conn.doOutput = true
                    conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    conn.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
                } else if (method == "POST") {
                    conn.doOutput = true
                    conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    conn.outputStream.use { it.write("{}".toByteArray(Charsets.UTF_8)) }
                }
                val code = conn.responseCode
                val stream = if (code in 200..299) conn.inputStream else conn.errorStream
                val text = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""
                code to text
            } catch (e: java.io.IOException) {
                throw ApiException("서버에 연결하지 못했어요")
            } finally {
                conn.disconnect()
            }
        }

    private fun errorMessage(e: JsonElement?): String? =
        ((e as? JsonObject)?.get("error") as? JsonObject)?.s("message")

    private fun cardFrom(e: JsonElement): GalleryItem? {
        val o = e as? JsonObject ?: return null
        return GalleryItem(
            id = o.s("id") ?: return null,
            name = o.s("name") ?: return null,
            description = o.s("description") ?: "",
            category = o.s("category")?.let { Category.fromWire(it) } ?: Category.ETC,
            tags = o.strings("tags"),
            triggerType = o.s("triggerType"),
            actionTypes = o.strings("actionTypes"),
            isOfficial = (o["isOfficial"] as? JsonPrimitive)?.booleanOrNull ?: false,
            isMine = (o["isMine"] as? JsonPrimitive)?.booleanOrNull ?: false,
            spec = null,
            serverCompat = o.s("compat")?.let { w -> CompatLevel.entries.firstOrNull { it.wire == w } },
        )
    }

    private fun JsonObject.s(k: String): String? = (this[k] as? JsonPrimitive)?.takeIf { it.isString }?.contentOrNull
    private fun JsonObject.strings(k: String): List<String> =
        (this[k] as? JsonArray)?.mapNotNull { (it as? JsonPrimitive)?.contentOrNull } ?: emptyList()

    private fun enc(s: String) = URLEncoder.encode(s, "UTF-8")
}
