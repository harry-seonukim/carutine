package com.carutine.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.carutine.app.AppController
import com.carutine.app.BuildConfig
import com.carutine.app.Labels
import com.carutine.app.VehicleMode
import com.carutine.app.ui.theme.Tok
import com.carutine.model.Cap
import com.carutine.model.PlaceKey

@Composable
fun SettingsScreen(c: AppController, openDiagnostics: () -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SectionTitle("차량 연결")
        CarCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ModeBadge(c.mode)
                Text(
                    when (c.mode) {
                        VehicleMode.REAL -> "Pleos 차량 서비스에 연결되어 실제 차량 값을 읽고 바꿔요."
                        VehicleMode.SIMULATOR -> "차량 서비스에 연결하지 못해 시뮬레이터로 동작해요. 차량 상태와 사건은 시뮬레이터 패널에서 바꿀 수 있어요."
                        VehicleMode.CONNECTING -> "연결 중이에요."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text("주행 상태: ${Labels.drive(c.driveState)}", style = MaterialTheme.typography.bodyMedium, color = Tok.text2)
            }
        }
        SecondaryButton("차량 연결 진단 (V1 검증 화면)", openDiagnostics, Modifier.fillMaxWidth())

        SectionTitle("내 차가 지원하는 기능")
        CarCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Cap.entries.forEach { cap ->
                    val ok = cap in c.caps
                    Text(
                        (if (ok) "✓ " else "✕ ") + Labels.cap(cap),
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (ok) Tok.text else Tok.text2,
                    )
                }
            }
        }

        SectionTitle("장소 (좌표는 이 기기에만)")
        CarCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(PlaceKey.HOME, PlaceKey.WORK).forEach { k ->
                    Text("'${Labels.place(k)}': " + if (k in c.places) "지정됨" else "미지정", style = MaterialTheme.typography.bodyLarge)
                }
                Text("좌표 값은 화면·서버 어디에도 보내지 않아요.", style = MaterialTheme.typography.bodySmall, color = Tok.text2)
            }
        }

        SectionTitle("갤러리 서버")
        Note(if (c.api.enabled) "연결 대상이 설정되어 있어요" else "서버 미연결(오프라인 모드): 공식 루틴만 보이고 공유는 미리보기만 돼요")
        Text("버전 ${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.bodySmall, color = Tok.text2, modifier = Modifier.padding(bottom = 24.dp))
    }
}
