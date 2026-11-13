package com.carutine.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carutine.app.AppController
import com.carutine.app.Banner
import com.carutine.app.BannerTone
import com.carutine.app.Labels
import com.carutine.app.ProposalUi
import com.carutine.app.Screen
import com.carutine.app.VehicleMode
import com.carutine.app.ui.theme.Tok
import com.carutine.engine.Announcer
import com.carutine.engine.DriveState
import kotlinx.coroutines.delay

@Composable
fun AppRoot(c: AppController, openDiagnostics: () -> Unit) {
    BackHandler(enabled = c.importTarget != null || c.backStack.size > 1) { c.back() }
    LocationPermissionRequest(c)

    // 루트 Surface가 LocalContentColor를 Tok.text로 정한다(색을 지정하지 않은 Text가 검정으로 나오던 문제)
    Surface(Modifier.fillMaxSize(), color = Tok.bg, contentColor = Tok.text) {
    Box(Modifier.fillMaxSize().safeDrawingPadding()) {
        if (c.mode == VehicleMode.CONNECTING) {
            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Tok.accent)
                Text("차량에 연결하는 중…", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp))
            }
        } else BoxWithConstraints(Modifier.fillMaxSize()) {
            val wide = maxWidth >= 900.dp
            val drawerMax = maxHeight * 0.55f
            val showSim = c.mode == VehicleMode.SIMULATOR
            if (wide && showSim) {
                Row(Modifier.fillMaxSize()) {
                    Column(Modifier.weight(1f).fillMaxHeight()) { MainArea(c, openDiagnostics) }
                    SimulatorPanel(c, Modifier.width(380.dp).fillMaxHeight())
                }
            } else {
                Column(Modifier.fillMaxSize()) {
                    Column(Modifier.weight(1f).fillMaxWidth()) { MainArea(c, openDiagnostics) }
                    if (showSim) {
                        var open by rememberSaveable { mutableStateOf(false) }
                        SimulatorDrawer(c, open, onToggle = { open = !open }, maxHeight = drawerMax)
                    }
                }
            }
        }
        // S12 배너·충전소 제안은 맨 위에 겹친다
        Column(Modifier.align(Alignment.TopCenter).fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            c.proposal?.let { ProposalBanner(it, c) }
            c.banner?.let { BannerView(it, c) }
        }
        c.importTarget?.let { ImportSheet(c, it) }
    }
    }
}

@Composable
private fun MainArea(c: AppController, openDiagnostics: () -> Unit) {
    TopBar(c)
    Box(Modifier.fillMaxSize()) {
        when (val s = c.screen) {
            Screen.Home -> HomeScreen(c)
            Screen.Gallery -> GalleryScreen(c)
            is Screen.Detail -> DetailScreen(c, s.item)
            is Screen.Editor -> EditorScreen(c, s.localId)
            Screen.Share -> ShareScreen(c)
            Screen.Settings -> SettingsScreen(c, openDiagnostics)
        }
    }
}

@Composable
private fun TopBar(c: AppController) {
    Row(
        Modifier.fillMaxWidth().heightIn(min = 72.dp).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (c.backStack.size > 1) {
            TextButton(onClick = { c.back() }, modifier = Modifier.heightIn(min = Tok.touch)) {
                Text("‹ 뒤로", style = MaterialTheme.typography.titleLarge, color = Tok.brand)
            }
            TextButton(onClick = { c.goHome() }, modifier = Modifier.heightIn(min = Tok.touch)) {
                Text("홈", style = MaterialTheme.typography.titleLarge, color = Tok.brand)
            }
        }
        Text(
            when (c.screen) {
                Screen.Home -> "Carutine"
                Screen.Gallery -> "루틴 갤러리"
                is Screen.Detail -> "루틴 상세"
                is Screen.Editor -> "직접 만들기"
                Screen.Share -> "갤러리에 공유"
                Screen.Settings -> "설정"
            },
            style = MaterialTheme.typography.headlineMedium,
            color = if (c.screen == Screen.Home) Tok.accent else Tok.text,
            modifier = Modifier.weight(1f),
            maxLines = 1,
        )
        DriveChip(c.driveState)
        ModeBadge(c.mode)
    }
}

@Composable
private fun DriveChip(d: DriveState) {
    val driving = d.restrictsUi
    Surface(
        color = if (driving) Tok.accent else Tok.surface,
        contentColor = if (driving) Tok.onAccent else Tok.text,
        shape = RoundedCornerShape(999.dp),
    ) {
        Text(Labels.drive(d), style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
    }
}

@Composable
private fun BannerView(b: Banner, c: AppController) {
    LaunchedEffect(b.id) {
        delay(b.durationMs)
        c.dismissBanner(b.id)
    }
    val accent = when (b.tone) {
        BannerTone.SUCCESS -> Tok.fullFg
        BannerTone.WARN -> Tok.accentInk
        BannerTone.FAIL -> Tok.danger
        BannerTone.INFO -> Tok.brand
    }
    Surface(color = Tok.toast, contentColor = Tok.text, shape = RoundedCornerShape(16.dp), shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(start = 20.dp, end = 12.dp, top = 12.dp, bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(6.dp).heightIn(min = 48.dp).background(accent, RoundedCornerShape(3.dp)))
            Column(Modifier.weight(1f).padding(start = 14.dp)) {
                Text(b.text, style = MaterialTheme.typography.titleLarge)
                b.sub?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = Tok.toastMuted) }
            }
            if (b.showUndo && c.canUndo) {
                PrimaryButton("원래대로", onClick = { c.dismissBanner(b.id); c.undo() })
            }
            TextButton(onClick = { c.dismissBanner(b.id) }, modifier = Modifier.heightIn(min = Tok.touch)) {
                Text("닫기", color = Tok.toastMuted, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

/** 충전소 경유지 제안(10초). 무응답이면 조용히 사라진다. */
@Composable
private fun ProposalBanner(p: ProposalUi, c: AppController) {
    var progress by remember(p.startedAt) { mutableFloatStateOf(1f) }
    LaunchedEffect(p.startedAt) {
        while (true) {
            val left = (p.durationMs - (System.currentTimeMillis() - p.startedAt)).coerceAtLeast(0)
            progress = left.toFloat() / p.durationMs
            if (left <= 0L) break
            delay(100)
        }
    }
    Surface(color = Tok.toast, contentColor = Tok.text, shape = RoundedCornerShape(16.dp), shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(Announcer.PROPOSAL, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            if (p.routineName.isNotEmpty()) Text("'${p.routineName}' 루틴의 제안", color = Tok.toastMuted, style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PrimaryButton("경유지 추가", onClick = { c.answerProposal(true) }, modifier = Modifier.weight(1f))
                SecondaryButton("아니요", onClick = { c.answerProposal(false) })
            }
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth(), color = Tok.accent, trackColor = Tok.border)
        }
    }
}

/** 실제 차량 모드: 위치 트리거(T1·T2)에 필요한 위치 권한을 한 번 묻고, 허용되면 차량에 다시 연결한다. */
@Composable
private fun LocationPermissionRequest(c: AppController) {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions(),
    ) { result -> if (result.values.any { it }) c.reconnectAfterPermission() }
    LaunchedEffect(c.mode) {
        if (c.mode == VehicleMode.REAL) {
            val fine = android.Manifest.permission.ACCESS_FINE_LOCATION
            val coarse = android.Manifest.permission.ACCESS_COARSE_LOCATION
            val granted = listOf(fine, coarse).any {
                androidx.core.content.ContextCompat.checkSelfPermission(ctx, it) == android.content.pm.PackageManager.PERMISSION_GRANTED
            }
            if (!granted) launcher.launch(arrayOf(fine, coarse))
        }
    }
}
