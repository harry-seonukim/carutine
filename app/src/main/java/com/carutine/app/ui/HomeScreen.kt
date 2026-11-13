package com.carutine.app.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.carutine.app.AppController
import com.carutine.app.BannerTone
import com.carutine.app.Labels
import com.carutine.app.Screen
import com.carutine.app.ui.theme.Tok
import com.carutine.engine.RoutineSource
import com.carutine.engine.StoredRoutine
import com.carutine.model.CompatChecker
import com.carutine.model.CompatLevel

/** 홈/내 루틴(S02). 주행 중에는 이름만 남은 큰 실행 타일로 바뀐다(주행 매트릭스). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(c: AppController) {
    val driving = c.restrictsUi
    LazyVerticalGrid(
        columns = GridCells.Adaptive(if (driving) 280.dp else 360.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (!driving) {
                    BrandButton("루틴 갤러리", { c.navigate(Screen.Gallery) })
                    SecondaryButton("직접 만들기", { c.navigate(Screen.Editor(null)) })
                    SecondaryButton("공유", { c.navigate(Screen.Share) }, enabled = c.routines.isNotEmpty())
                    SecondaryButton("설정", { c.navigate(Screen.Settings) })
                }
                if (c.canUndo) PrimaryButton("원래대로", { c.undo() })
                VoiceButton(c)
            }
        }
        if (driving) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Note("주행 중에는 루틴 실행만 할 수 있어요. 편집·갤러리는 정차 후에 열려요.", warn = true)
            }
        }
        c.voiceStatus?.let { s ->
            item(span = { GridItemSpan(maxLineSpan) }) { Note("음성: $s") }
        }
        if (c.routines.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) { EmptyHome(c, driving) }
        } else {
            items(c.routines, key = { it.localId }) { r ->
                if (driving) DriveTile(c, r) else RoutineCard(c, r)
            }
        }
    }
}

@Composable
private fun EmptyHome(c: AppController, driving: Boolean) {
    CarCard(Modifier.fillMaxWidth(), dashed = true) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("아직 내 루틴이 없어요", style = MaterialTheme.typography.headlineSmall)
            Text(
                if (driving) "정차한 뒤 갤러리에서 루틴을 가져와 주세요."
                else "갤러리에서 공식 루틴을 가져오거나, 조건과 동작을 골라 직접 만들어 보세요.",
                style = MaterialTheme.typography.bodyLarge, color = Tok.text2, textAlign = TextAlign.Center,
            )
            if (!driving) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PrimaryButton("갤러리에서 가져오기", { c.navigate(Screen.Gallery) })
                    SecondaryButton("직접 만들기", { c.navigate(Screen.Editor(null)) })
                }
            }
        }
    }
}

@Composable
private fun RoutineCard(c: AppController, r: StoredRoutine) {
    val compat = remember(r.spec, c.caps) { CompatChecker.check(r.spec, c.caps) }
    val index = c.routines.indexOfFirst { it.localId == r.localId }
    CarCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(r.spec.name, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (r.source == RoutineSource.IMPORTED) Tag("가져옴") else Tag("내 루틴", Tok.commTint, Tok.comm)
            }
            Text(Labels.specSummary(r.spec), style = MaterialTheme.typography.bodyMedium, color = Tok.text2, maxLines = 2, overflow = TextOverflow.Ellipsis)
            if (compat.level != CompatLevel.FULL) CompatBadge(compat.level)
            val missing = r.spec.places().filter { it !in c.places }
            if (missing.isNotEmpty() && r.enabled) {
                Text("장소 미지정: ${missing.joinToString { Labels.place(it) }} (자동 실행 안 됨)", color = Tok.accentInk, style = MaterialTheme.typography.bodySmall)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PrimaryButton("실행", { c.runRoutine(r.localId) }, enabled = compat.level != CompatLevel.NONE)
                SecondaryButton("편집", { c.navigate(Screen.Editor(r.localId)) })
                Box(Modifier.weight(1f))
                if (r.spec.trigger != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Switch(
                            checked = r.enabled,
                            onCheckedChange = { c.setEnabled(r.localId, it) },
                            colors = SwitchDefaults.colors(checkedTrackColor = Tok.brand, checkedThumbColor = Tok.onBrand),
                        )
                        Text(if (r.enabled) "자동 켜짐" else "자동 꺼짐", style = MaterialTheme.typography.labelSmall, color = Tok.text2)
                    }
                } else {
                    Tag("수동")
                }
            }
            if (c.routines.size > 1) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("우선순위 ${index + 1}", style = MaterialTheme.typography.labelMedium, color = Tok.text2, modifier = Modifier.weight(1f))
                    SecondaryButton("▲", { c.move(r.localId, -1) }, enabled = index > 0)
                    SecondaryButton("▼", { c.move(r.localId, 1) }, enabled = index < c.routines.lastIndex)
                }
            }
        }
    }
}

@Composable
private fun DriveTile(c: AppController, r: StoredRoutine) {
    Surface(
        onClick = { c.runRoutine(r.localId) },
        color = Tok.surface,
        contentColor = Tok.text,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().heightIn(min = 140.dp),
    ) {
        Box(Modifier.padding(24.dp), contentAlignment = Alignment.CenterStart) {
            Text(r.spec.name, style = MaterialTheme.typography.headlineMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

/** '음성으로 실행'. 인식기가 없으면 버튼을 숨긴다. 마이크 권한은 누를 때 묻는다. */
@Composable
fun VoiceButton(c: AppController) {
    val ctx = LocalContext.current
    val available = remember { c.listener.available }
    if (!available) return
    val start = {
        c.listener.listen(
            onPartial = { c.voiceStatus = it },
            onResult = { c.onVoiceResult(it) },
            onError = { c.voiceStatus = null; c.showBanner(it, tone = BannerTone.WARN, durationMs = 3000) },
        )
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) start() else c.showBanner("마이크 권한이 없어 음성 실행을 쓸 수 없어요", tone = BannerTone.WARN)
    }
    BrandButton(if (c.voiceStatus != null) "듣는 중…" else "음성으로 실행", {
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) start()
        else launcher.launch(Manifest.permission.RECORD_AUDIO)
    })
}
