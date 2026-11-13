package com.carutine.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.carutine.app.Labels
import com.carutine.app.VehicleMode
import com.carutine.app.ui.theme.Tok
import com.carutine.model.CompatLevel

/** 주 버튼(강조색). 높이 76dp 이상. */
@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = Tok.touch),
        shape = RoundedCornerShape(Tok.radius),
        colors = ButtonDefaults.buttonColors(containerColor = Tok.accent, contentColor = Tok.onAccent),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    ) { Text(text, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center) }
}

/** 보조 버튼(브랜드색 채움). */
@Composable
fun BrandButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = Tok.touch),
        shape = RoundedCornerShape(Tok.radius),
        colors = ButtonDefaults.buttonColors(containerColor = Tok.brand, contentColor = Tok.onBrand),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
    ) { Text(text, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center) }
}

/** 테두리 버튼. */
@Composable
fun SecondaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = Tok.touch),
        shape = RoundedCornerShape(Tok.radius),
        border = BorderStroke(2.dp, Tok.border),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Tok.text),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
    ) { Text(text, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center) }
}

/** 선택 칩(큰 터치 영역). Material 칩은 32dp라 차량용으로 직접 만든다. */
@Composable
fun Choice(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val bg = if (selected) Tok.brand else Tok.surface
    val fg = if (selected) Tok.onBrand else Tok.text
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 88.dp, minHeight = Tok.touch)
            .clip(RoundedCornerShape(Tok.radius))
            .background(if (enabled) bg else bg.copy(alpha = 0.45f))
            .border(2.dp, if (selected) Tok.brand else Tok.border, RoundedCornerShape(Tok.radius))
            .clickable(enabled = enabled, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            color = if (enabled) fg else fg.copy(alpha = 0.5f),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center,
        )
    }
}

/** − 값 + 스테퍼. */
@Composable
fun Stepper(value: String, onMinus: () -> Unit, onPlus: () -> Unit, minusEnabled: Boolean = true, plusEnabled: Boolean = true) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        SecondaryButton("−", onMinus, Modifier.widthIn(min = Tok.touch).semantics { contentDescription = "줄이기" }, enabled = minusEnabled)
        Text(value, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.widthIn(min = 96.dp), textAlign = TextAlign.Center)
        SecondaryButton("+", onPlus, Modifier.widthIn(min = Tok.touch).semantics { contentDescription = "늘리기" }, enabled = plusEnabled)
    }
}

/** 화면 하단 고정 바: 주 버튼(가져오기/저장/공유)이 스크롤과 무관하게 항상 보이게 한다. */
@Composable
fun BottomBar(content: @Composable RowScope.() -> Unit) {
    Surface(color = Tok.bg, contentColor = Tok.text) {
        androidx.compose.foundation.layout.Column {
            androidx.compose.material3.HorizontalDivider(color = Tok.border, thickness = 1.dp)
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = content,
            )
        }
    }
}

@Composable
fun Tag(text: String, bg: Color = Tok.sunken, fg: Color = Tok.text2) {
    Surface(color = bg, contentColor = fg, shape = RoundedCornerShape(6.dp)) {
        Text(text, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
    }
}

@Composable
fun CompatBadge(level: CompatLevel, large: Boolean = false) {
    val (bg, fg, mark) = when (level) {
        CompatLevel.FULL -> Triple(Tok.fullBg, Tok.fullFg, "✓")
        CompatLevel.PARTIAL -> Triple(Tok.partBg, Tok.partFg, "–")
        CompatLevel.NONE -> Triple(Tok.noneBg, Tok.noneFg, "⊘")
    }
    Surface(color = bg, contentColor = fg, shape = RoundedCornerShape(8.dp)) {
        Text(
            "$mark ${Labels.compat(level)}",
            style = if (large) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = if (large) 6.dp else 3.dp),
        )
    }
}

/** 현재 모드 배지(항상 보임). 실제 차량/시뮬레이터를 숨기지 않는다. */
@Composable
fun ModeBadge(mode: VehicleMode) {
    val (text, bg, fg) = when (mode) {
        VehicleMode.REAL -> Triple("실제 차량", Tok.fullBg, Tok.fullFg)
        VehicleMode.SIMULATOR -> Triple("시뮬레이터", Tok.accentTint, Tok.accentInk)
        VehicleMode.CONNECTING -> Triple("차량 연결 중", Tok.sunken, Tok.text2)
    }
    Surface(color = bg, contentColor = fg, shape = RoundedCornerShape(999.dp)) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(fg))
            Text(text, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(start = 6.dp))
        }
    }
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(text, style = MaterialTheme.typography.titleMedium, color = Tok.text2, modifier = modifier.padding(top = 8.dp, bottom = 4.dp))
}

@Composable
fun RowScope.Spacer1() = androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))

/** 카드 컨테이너. */
@Composable
fun CarCard(modifier: Modifier = Modifier, dashed: Boolean = false, content: @Composable () -> Unit) {
    Surface(
        modifier = modifier,
        color = if (dashed) Color.Transparent else Tok.surface,
        contentColor = Tok.text,
        shape = RoundedCornerShape(Tok.radius),
        border = BorderStroke(2.dp, Tok.border),
        content = content,
    )
}

@Composable
fun Note(text: String, modifier: Modifier = Modifier, warn: Boolean = false) {
    Surface(
        modifier = modifier,
        color = if (warn) Tok.accentTint else Tok.sunken,
        contentColor = if (warn) Tok.accentInk else Tok.text2,
        shape = RoundedCornerShape(Tok.radius),
    ) {
        Text(text, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
    }
}
