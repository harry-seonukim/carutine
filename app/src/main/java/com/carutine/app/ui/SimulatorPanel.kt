package com.carutine.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.carutine.app.AppController
import com.carutine.app.Labels
import com.carutine.app.SimPosition
import com.carutine.app.SimProfile
import com.carutine.app.SimSnapshot
import com.carutine.app.ui.theme.Tok
import com.carutine.model.AirMode
import com.carutine.model.AmbientBrightness
import com.carutine.model.PlaceKey

/** 세로(좁은) 화면: 아래쪽 접이식 시뮬레이터. */
@Composable
fun SimulatorDrawer(c: AppController, open: Boolean, onToggle: () -> Unit, maxHeight: Dp) {
    Column(Modifier.fillMaxWidth().background(Tok.sunken)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "시뮬레이터 · ${c.simPosition.label} · 실내 ${Labels.fmt(c.sim.cabinTemp)}℃",
                style = MaterialTheme.typography.titleMedium, color = Tok.accentInk, modifier = Modifier.weight(1f),
            )
            SecondaryButton(if (open) "접기" else "펼치기", onToggle)
        }
        if (open) SimulatorPanel(c, Modifier.fillMaxWidth().heightIn(max = maxHeight))
    }
}

/** 시뮬레이터 패널(시뮬레이터 모드에서만). 사건 버튼 → FakeVehicleGateway.emit → 엔진 자동 실행. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SimulatorPanel(c: AppController, modifier: Modifier = Modifier) {
    Column(
        modifier.background(Tok.sunken).verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("차량 상태 시뮬레이터", style = MaterialTheme.typography.titleLarge)
        Text("실제 차량이 아닌 가상 차량이에요. 눌러서 상황을 바꿔 보세요.", style = MaterialTheme.typography.bodySmall, color = Tok.text2)

        SimLabel("주행 상태")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Choice("정차", !c.driveState.restrictsUi, { c.simSetDriving(false) })
            Choice("주행 중", c.driveState.restrictsUi, { c.simSetDriving(true) })
        }

        SimLabel("장소 (가상 좌표, 기기에만 저장)")
        val hasPlaces = PlaceKey.HOME in c.places && PlaceKey.WORK in c.places
        if (!hasPlaces) BrandButton("집·회사 가상 위치 저장", { c.registerSimPlaces() }, Modifier.fillMaxWidth())
        Text("현재 위치: ${c.simPosition.label}", style = MaterialTheme.typography.bodyMedium)

        SimLabel("사건 넣기")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            EventButton("회사 출발") { c.simDepart(SimPosition.WORK) }
            EventButton("집 도착") { c.simArrive(SimPosition.HOME) }
            EventButton("집 출발") { c.simDepart(SimPosition.HOME) }
            EventButton("회사 도착") { c.simArrive(SimPosition.WORK) }
            EventButton(if (c.simWiper) "와이퍼 끄기" else "와이퍼 켜짐") { c.simSetWiper(!c.simWiper) }
            EventButton("배터리 14%") { c.simSetBattery(14) }
            EventButton("배터리 80%로 충전") { c.simSetBattery(80) }
            EventButton("연료 14%") { c.simSetFuel(14) }
            EventButton("연료 80%로 주유") { c.simSetFuel(80) }
        }
        Text("배터리 ${c.simBattery}% · 연료 ${c.simFuel}%", style = MaterialTheme.typography.bodySmall, color = Tok.text2)

        SimLabel("실내 온도 (조건 T6)")
        Stepper(
            "${Labels.fmt(c.sim.cabinTemp)}℃",
            { c.simSetCabin(c.sim.cabinTemp - 1) },
            { c.simSetCabin(c.sim.cabinTemp + 1) },
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SecondaryButton("추움 10℃", { c.simSetCabin(10.0) })
            SecondaryButton("더움 30℃", { c.simSetCabin(30.0) })
        }

        SimLabel("현재 차량 설정값")
        StateGrid(c.sim)

        SimLabel("차량 사양 (호환 배지 시연)")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SimProfile.entries.forEach { p -> Choice(p.label, c.simProfile == p, { c.simSetProfile(p) }) }
        }

        SimLabel("기록")
        c.simEvents.forEach { Text("· $it", style = MaterialTheme.typography.bodySmall, color = Tok.text2) }
        SecondaryButton("재실행 방지(30분) 기록 지우기", { c.simResetCooldowns() }, Modifier.fillMaxWidth())
    }
}

@Composable
private fun SimLabel(text: String) {
    Text(text, style = MaterialTheme.typography.labelLarge, color = Tok.text2, modifier = Modifier.padding(top = 4.dp))
}

@Composable
private fun EventButton(text: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Tok.accentTint,
        contentColor = Tok.accentInk,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.heightIn(min = 64.dp).border(1.dp, Tok.accent, RoundedCornerShape(10.dp)),
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(horizontal = 14.dp, vertical = 18.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StateGrid(s: SimSnapshot) {
    val cells = listOf(
        "설정 온도" to "${Labels.fmt(s.hvacTemp)}℃",
        "풍량" to "${s.fan}단",
        "내기/외기" to if (s.air == AirMode.RECIRC) "내기" else "외기",
        "운전석 열선" to if (s.seatDriver > 0) "${s.seatDriver}단" else "끔",
        "동승석 열선" to if (s.seatPassenger > 0) "${s.seatPassenger}단" else "끔",
        "핸들 열선" to if (s.steering) "켜짐" else "끔",
        "미러 열선" to if (s.mirror) "켜짐" else "끔",
        "앰비언트" to if (!s.ambientOn) "끔" else listOfNotNull(
            s.ambientColor?.let { Labels.color(it) },
            if (s.ambientBrightness == AmbientBrightness.LOW) "낮게" else null,
        ).joinToString(" ").ifEmpty { "켜짐" },
        "경유지" to "${s.waypoints}건",
    )
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        cells.forEach { (k, v) ->
            Surface(color = Tok.surface, shape = RoundedCornerShape(8.dp)) {
                Column(Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                    Text(k, style = MaterialTheme.typography.labelSmall, color = Tok.text2)
                    Text(v, style = MaterialTheme.typography.labelLarge, color = Tok.text)
                }
            }
        }
    }
}
