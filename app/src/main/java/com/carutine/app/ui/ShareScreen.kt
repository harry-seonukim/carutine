package com.carutine.app.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carutine.app.AppController
import com.carutine.app.BannerTone
import com.carutine.app.Labels
import com.carutine.app.SimProfile
import com.carutine.app.ui.theme.Tok
import com.carutine.model.CompatChecker
import com.carutine.model.EditorRules
import com.carutine.model.RoutineValidator
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

private val pretty = Json { prettyPrint = true }

/**
 * 공유(S11). 서버 URL이 없으면 '오프라인 미리보기'만: 업로드될 JSON(id·좌표·작성자 없음)과 예상 호환 정보를 보여 준다.
 * URL이 있으면 POST /v1/devices(토큰) → POST /v1/routines로 올린다.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShareScreen(c: AppController) {
    var selectedId by remember { mutableStateOf(c.routines.firstOrNull()?.localId) }
    var busy by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<String?>(null) }
    val selected = c.routines.firstOrNull { it.localId == selectedId }

    val scope = rememberCoroutineScope()
    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (!c.api.enabled) Note("서버 미연결: 오프라인 모드에서는 공유 미리보기만 할 수 있어요", warn = true)
            if (c.routines.isEmpty()) Note("공유할 내 루틴이 없어요")
            else {
                SectionTitle("공유할 루틴")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    c.routines.forEach { r -> Choice(r.spec.name, r.localId == selectedId, { selectedId = r.localId; result = null }) }
                }
            }
            if (selected != null) ShareBody(c, selected.spec, result)
        }
        // 공유 버튼은 하단 고정
        BottomBar {
            val spec = selected?.spec
            val ok = spec != null && RoutineValidator.validate(spec).isEmpty()
            if (c.api.enabled) {
                PrimaryButton(if (busy) "올리는 중…" else "갤러리에 공유", {
                    if (spec != null) {
                        busy = true
                        scope.launch {
                            c.share(spec)
                                .onSuccess { result = "갤러리에 올렸어요"; c.showBanner("'${spec.name}' 공유 완료", tone = BannerTone.SUCCESS, durationMs = 3000) }
                                .onFailure { result = "공유하지 못했어요: ${it.message}" }
                            busy = false
                        }
                    }
                }, Modifier.weight(1f), enabled = !busy && ok)
            } else {
                PrimaryButton("서버 연결 후 공유할 수 있어요", {}, Modifier.weight(1f), enabled = false)
            }
        }
    }
}

@Composable
private fun ShareBody(
    c: AppController,
    spec: com.carutine.model.RoutineSpec,
    result: String?,
) {
    run {
        val problems = remember(spec) {
            RoutineValidator.validate(spec).map { "계약 검증 실패 ${it.field}: ${it.reason}" }
        }

        SectionTitle("개인정보")
        Note("장소는 '집'·'회사' 같은 이름표만 올라가고 좌표는 이 차에만 남아요. 작성자·차량 정보는 보내지 않아요.")

        SectionTitle("예상 호환 (다른 차에서 보일 배지)")
        CarCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                val need = (spec.triggerCaps() + spec.actions.map { it.action.requiredCap }).toList()
                Text("필요한 기능: " + need.joinToString { Labels.cap(it) }, style = MaterialTheme.typography.bodyMedium, color = Tok.text2)
                CompatLine("이 차", CompatChecker.check(spec, c.caps).level)
                SimProfile.entries.forEach { p -> CompatLine(p.label, CompatChecker.check(spec, p.caps).level) }
            }
        }

        SectionTitle("업로드될 내용 (계약 v0.1 JSON)")
        val text = remember(spec) { pretty.encodeToString(JsonElement.serializer(), c.uploadJson(spec)) }
        Surface(color = Tok.sunken, contentColor = Tok.text, shape = RoundedCornerShape(Tok.radius), modifier = Modifier.fillMaxWidth()) {
            Text(
                text,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                modifier = Modifier.horizontalScroll(rememberScrollState()).padding(14.dp),
            )
        }

        problems.forEach { Text(it, color = Tok.danger, style = MaterialTheme.typography.bodyMedium) }
        result?.let { Note(it) }
    }
}

@Composable
private fun CompatLine(label: String, level: com.carutine.model.CompatLevel) {
    androidx.compose.foundation.layout.Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        CompatBadge(level)
    }
}
