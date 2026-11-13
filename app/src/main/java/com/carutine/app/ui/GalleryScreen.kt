package com.carutine.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.carutine.app.AppController
import com.carutine.app.ImportTarget
import com.carutine.app.Labels
import com.carutine.app.Screen
import com.carutine.app.VehicleMode
import com.carutine.app.data.GalleryItem
import com.carutine.app.ui.theme.Tok
import com.carutine.engine.ImportPlanner
import com.carutine.engine.Place
import com.carutine.model.ActionSupport
import com.carutine.model.CompatChecker
import com.carutine.model.CompatLevel
import com.carutine.model.PlaceKey
import com.carutine.model.RoutineSpec

private fun GalleryItem.compat(caps: Set<com.carutine.model.Cap>): CompatLevel? =
    spec?.let { CompatChecker.check(it, caps).level } ?: serverCompat

/** 갤러리 홈(S03): 선반 + 호환 배지. 서버가 없거나 실패하면 assets 시드로 대체한다. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GalleryScreen(c: AppController) {
    LaunchedEffect(Unit) { if (c.shelves.isEmpty()) c.loadGallery() }
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                c.galleryNote?.let { Note(it) }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if (c.hideIncompatible) "내 차에서 쓸 수 있는 루틴만 보여요" else "모든 루틴을 보여요",
                        style = MaterialTheme.typography.bodyMedium, color = Tok.text2, modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = { c.updateHideIncompatible(!c.hideIncompatible) }, modifier = Modifier.heightIn(min = Tok.touch)) {
                        Text(if (c.hideIncompatible) "모두 보기" else "쓸 수 있는 것만", color = Tok.brand, style = MaterialTheme.typography.labelLarge)
                    }
                }
                if (c.galleryLoading && c.shelves.isEmpty()) CircularProgressIndicator(color = Tok.accent)
            }
        }
        items(c.shelves, key = { it.id }) { shelf ->
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(shelf.title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 16.dp))
                if (shelf.items.isEmpty()) {
                    Note("이 선반에 보여 줄 루틴이 없어요", Modifier.padding(horizontal = 16.dp))
                } else {
                    // 좁은 앱 영역(Pleos 가로 약 1670px)에서도 잘리지 않게 가로 스크롤 대신 폭에 맞춘 격자로 놓는다
                    BoxWithConstraints(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                        val gap = 14.dp
                        val cols = ((maxWidth + gap) / (280.dp + gap)).toInt().coerceIn(1, 4)
                        val cardW = (maxWidth - gap * (cols - 1)) / cols
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(gap),
                            verticalArrangement = Arrangement.spacedBy(gap),
                            maxItemsInEachRow = cols,
                        ) {
                            shelf.items.forEach { item ->
                                GalleryCard(item, item.compat(c.caps), Modifier.width(cardW)) { c.navigate(Screen.Detail(item)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GalleryCard(item: GalleryItem, compat: CompatLevel?, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Tok.surface,
        contentColor = Tok.text,
        shape = RoundedCornerShape(Tok.radius),
        modifier = modifier.heightIn(min = 200.dp),
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(item.name, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                when {
                    item.isMine -> Tag("내 루틴", Tok.commTint, Tok.comm)
                    item.isOfficial -> Tag("공식")
                }
            }
            Text(item.description, style = MaterialTheme.typography.bodyMedium, color = Tok.text2, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(
                item.spec?.let { Labels.specSummary(it) } ?: Labels.triggerTypeSummary(item.triggerType),
                style = MaterialTheme.typography.bodySmall, color = Tok.text2, maxLines = 1, overflow = TextOverflow.Ellipsis,
            )
            Box(Modifier.weight(1f, fill = false))
            compat?.let { CompatBadge(it) }
        }
    }
}

/** 상세(S04~S06 축소): 조건·동작 목록에 내 차 지원 여부를 표시하고, 가져오기로 이어진다. */
@Composable
fun DetailScreen(c: AppController, item: GalleryItem) {
    var spec by remember(item) { mutableStateOf(item.spec) }
    var error by remember(item) { mutableStateOf<String?>(null) }
    LaunchedEffect(item) {
        if (spec == null) {
            c.specFor(item).onSuccess { spec = it }.onFailure { error = it.message ?: "불러오지 못했어요" }
        }
    }
    val s = spec
    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(item.name, style = MaterialTheme.typography.headlineMedium, color = Tok.text, modifier = Modifier.weight(1f))
                if (item.isOfficial) Tag("공식")
                if (item.isMine) Tag("내 루틴", Tok.commTint, Tok.comm)
            }
            if (item.description.isNotBlank()) Text(item.description, style = MaterialTheme.typography.bodyLarge, color = Tok.text2)
            when {
                error != null -> Note(error ?: "", warn = true)
                s == null -> CircularProgressIndicator(color = Tok.accent)
                else -> SpecDetail(c, s)
            }
        }
        // 주 버튼은 하단 고정(스크롤 없이 항상 보임)
        if (s != null) {
            val level = remember(s, c.caps) { CompatChecker.check(s, c.caps).level }
            BottomBar {
                if (level == CompatLevel.NONE) {
                    PrimaryButton("이 차에서는 쓸 수 없어요", {}, Modifier.weight(1f), enabled = false)
                } else {
                    PrimaryButton("내 루틴으로 가져오기", { c.startImport(s, item.id) }, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SpecDetail(c: AppController, s: RoutineSpec) {
    val compat = remember(s, c.caps) { CompatChecker.check(s, c.caps) }
    CompatBadge(compat.level, large = true)
    Text(
        when (compat.level) {
            CompatLevel.FULL -> "모든 동작을 내 차에서 실행할 수 있어요"
            CompatLevel.PARTIAL -> if (compat.perAction.none { it == ActionSupport.UNSUPPORTED })
                "내 차에 없는 기능은 비슷한 동작으로 바꿔 실행해요"
            else "이 차에서는 ${compat.perAction.count { it == ActionSupport.UNSUPPORTED }}개 동작이 빠져요. 나머지는 그대로 실행돼요"
            CompatLevel.NONE -> if (!compat.triggerOk)
                "이 차는 조건에 필요한 기능(${compat.missingTriggerCaps.joinToString { Labels.cap(it) }})이 없어요"
            else "이 차에서 실행할 수 있는 동작이 없어요"
        },
        style = MaterialTheme.typography.bodyLarge,
        color = Tok.text,
    )
    SectionTitle("이럴 때")
    val t = s.trigger
    if (t == null) DetailRow("버튼·말로 실행", null, ok = true)
    else {
        DetailRow(Labels.trigger(t), null, ok = t.requiredCap in c.caps)
        s.conditions.forEach { cond -> DetailRow("그리고 " + Labels.condition(cond), null, ok = cond.requiredCap in c.caps) }
    }
    SectionTitle("이렇게 해요")
    s.actions.forEachIndexed { i, a ->
        when (compat.perAction[i]) {
            ActionSupport.SUPPORTED -> DetailRow(Labels.action(a.action), null, ok = true)
            ActionSupport.VIA_FALLBACK -> DetailRow(Labels.action(a.action), "이 차에 없어서 대신 ${Labels.action(a.fallback!!)}", ok = true, alt = true)
            ActionSupport.UNSUPPORTED -> DetailRow(Labels.action(a.action), "이 차에서는 빠져요", ok = false)
        }
    }
}

@Composable
private fun DetailRow(title: String, sub: String?, ok: Boolean, alt: Boolean = false) {
    CarCard(Modifier.fillMaxWidth(), dashed = !ok) {
        Column(Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = if (ok) Tok.text else Tok.text2)
            sub?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = if (alt) Tok.fullFg else Tok.partFg) }
        }
    }
}

/** S10 축소판: 장소(집/회사) 지정 + 자동 실행 스위치. 좌표는 기기에만 저장된다. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ImportSheet(c: AppController, target: ImportTarget) {
    val sections = remember(target) { ImportPlanner.sections(target.spec) }
    val assigned = remember(target) { mutableStateMapOf<PlaceKey, Place>() }
    var autoRun by remember(target) { mutableStateOf(true) }
    // 별도 창(Dialog)으로 띄워 뒤 화면의 버튼이 눌리거나 접근성 트리에 섞이지 않게 한다
    androidx.compose.ui.window.Dialog(
        onDismissRequest = { c.importTarget = null },
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
    ) {
    Box(
        Modifier.fillMaxSize().clickable(onClick = { c.importTarget = null }),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Surface(
            color = Tok.surface,
            contentColor = Tok.text,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            modifier = Modifier.fillMaxWidth().widthIn(max = 1100.dp).clickable(enabled = false, onClick = {}),
        ) {
          Column {
            Column(
                Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text("'${target.spec.name}' 내 루틴에 추가", style = MaterialTheme.typography.headlineSmall, color = Tok.text)
                if (sections.placeKeys.isNotEmpty()) {
                    SectionTitle("장소 지정")
                    Note("장소 좌표는 이 차에만 저장되고 서버로 보내지 않아요.")
                    sections.placeKeys.forEach { key ->
                        val registered = c.places[key]
                        val picked = assigned[key]
                        CarCard(Modifier.fillMaxWidth()) {
                            FlowRow(
                                Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    when {
                                        picked != null -> "'${Labels.place(key)}' 지정됨"
                                        registered != null -> "'${Labels.place(key)}' 등록되어 있어요"
                                        else -> "'${Labels.place(key)}' 위치가 필요해요"
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (picked != null || registered != null) Tok.fullFg else Tok.text,
                                    modifier = Modifier.padding(top = 12.dp),
                                )
                                if (registered == null && picked == null) {
                                    val cand = c.placeCandidate(key)
                                    BrandButton(
                                        if (c.mode == VehicleMode.SIMULATOR) "가상 위치로 지정" else "현재 위치로 지정",
                                        { cand?.let { assigned[key] = it } },
                                        enabled = cand != null,
                                    )
                                    if (cand == null) Text("차량 위치를 아직 받지 못했어요", color = Tok.accentInk, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
                if (sections.askAutoRun) {
                    SectionTitle("자동 실행")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "조건이 맞으면 자동으로 실행할까요? 끄면 버튼·음성으로만 실행돼요.",
                            style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f),
                        )
                        Switch(
                            checked = autoRun, onCheckedChange = { autoRun = it },
                            colors = SwitchDefaults.colors(checkedTrackColor = Tok.brand, checkedThumbColor = Tok.onBrand),
                        )
                    }
                }
            }
            val missing = sections.placeKeys.filter { it !in c.places && it !in assigned }
            Row(Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SecondaryButton("취소", { c.importTarget = null })
                PrimaryButton(
                    if (missing.isEmpty()) "가져오기" else "장소를 지정해 주세요",
                    { c.confirmImport(target, assigned.toMap(), autoRun) },
                    Modifier.weight(1f),
                    enabled = missing.isEmpty(),
                )
            }
          }
        }
    }
    }
}
