package com.carutine.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.carutine.app.AppController
import com.carutine.app.Labels
import com.carutine.app.ui.theme.Tok
import com.carutine.engine.StoredRoutine
import com.carutine.model.Action
import com.carutine.model.ActionSpec
import com.carutine.model.AirMode
import com.carutine.model.AmbientBrightness
import com.carutine.model.AmbientColor
import com.carutine.model.Cap
import com.carutine.model.Condition
import com.carutine.model.EditorRules
import com.carutine.model.EnergySource
import com.carutine.model.HvacTempMode
import com.carutine.model.PlaceKey
import com.carutine.model.RoutineSpec
import com.carutine.model.RoutineValidator
import com.carutine.model.Seat
import com.carutine.model.TempOp
import com.carutine.model.Trigger
import com.carutine.model.ValidationError
import java.time.DayOfWeek
import java.time.LocalTime

private enum class TKind(val label: String) {
    MANUAL("버튼·말로만"), ENTER("장소에 도착"), EXIT("장소를 떠남"), ENERGY("배터리·연료 부족"), WIPER("와이퍼 켜짐"),
}

/** 편집 중 상태(불변). 저장할 때 계약 RoutineSpec으로 바꾼다. */
private data class Draft(
    val name: String = "",
    val kind: TKind = TKind.MANUAL,
    val place: PlaceKey = PlaceKey.HOME,
    val source: EnergySource = EnergySource.EV_BATTERY,
    val percent: Int = 20,
    val timeOn: Boolean = false,
    val days: Set<DayOfWeek> = Labels.WEEKDAYS,
    val start: LocalTime = LocalTime.of(7, 0),
    val end: LocalTime = LocalTime.of(9, 0),
    val cabinOn: Boolean = false,
    val cabinOp: TempOp = TempOp.BELOW,
    val cabinValue: Int = 15,
    val actions: List<ActionSpec> = emptyList(),
) {
    fun trigger(): Trigger? = when (kind) {
        TKind.MANUAL -> null
        TKind.ENTER -> Trigger.LocationEnter(place)
        TKind.EXIT -> Trigger.LocationExit(place)
        TKind.ENERGY -> Trigger.EnergyBelow(source, percent)
        TKind.WIPER -> Trigger.WiperOn
    }

    fun conditions(): List<Condition> = if (kind == TKind.MANUAL) emptyList() else buildList {
        if (timeOn) add(Condition.TimeWindow(days, start, end))
        if (cabinOn) add(Condition.CabinTemp(cabinOp, cabinValue))
    }

    fun toSpec(base: RoutineSpec?): RoutineSpec = RoutineSpec(
        id = base?.id,
        name = name.trim(),
        description = base?.description ?: "",
        category = base?.category ?: com.carutine.model.Category.ETC,
        tags = base?.tags ?: emptyList(),
        trigger = trigger(),
        conditions = conditions(),
        actions = actions,
    )

    companion object {
        fun from(spec: RoutineSpec): Draft {
            var d = Draft(name = spec.name, actions = spec.actions)
            d = when (val t = spec.trigger) {
                null -> d
                is Trigger.LocationEnter -> d.copy(kind = TKind.ENTER, place = t.place)
                is Trigger.LocationExit -> d.copy(kind = TKind.EXIT, place = t.place)
                is Trigger.EnergyBelow -> d.copy(kind = TKind.ENERGY, source = t.source, percent = t.percent)
                Trigger.WiperOn -> d.copy(kind = TKind.WIPER)
            }
            spec.conditions.forEach { c ->
                d = when (c) {
                    is Condition.TimeWindow -> d.copy(timeOn = true, days = c.days, start = c.start, end = c.end)
                    is Condition.CabinTemp -> d.copy(cabinOn = true, cabinOp = c.op, cabinValue = c.value)
                }
            }
            return d
        }

        fun defaultAction(type: String): Action = when (type) {
            "hvac_temp" -> Action.HvacTemp(HvacTempMode.SET, 22.0)
            "hvac_fan" -> Action.HvacFan(3)
            "hvac_air" -> Action.HvacAir(AirMode.RECIRC)
            "seat_heater" -> Action.SeatHeater(Seat.DRIVER, 2)
            "steering_heater" -> Action.SteeringHeater(true)
            "mirror_heater" -> Action.MirrorHeater(true)
            "ambient_light" -> Action.AmbientLight(true, AmbientColor.WARM, null)
            "speak" -> Action.Speak("루틴을 실행했어요")
            else -> Action.SuggestCharger
        }

        val ACTION_TYPES = listOf(
            "hvac_temp" to Cap.HVAC_TEMP, "hvac_fan" to Cap.HVAC_FAN, "hvac_air" to Cap.HVAC_AIR,
            "seat_heater" to Cap.SEAT_HEATER, "steering_heater" to Cap.STEERING_HEATER, "mirror_heater" to Cap.MIRROR_HEATER,
            "ambient_light" to Cap.LIGHT_AMBIENT, "speak" to Cap.TTS, "suggest_charger" to Cap.NAVI_WAYPOINT,
        )
    }
}

private fun reasonText(e: ValidationError): String {
    val what = when {
        e.field.startsWith("name") -> "이름"
        e.field.startsWith("actions") -> "동작"
        e.field.startsWith("conditions") -> "상태 조건"
        e.field.startsWith("trigger") -> "조건"
        else -> e.field
    }
    val why = when (e.reason) {
        ValidationError.Reason.BLANK -> "비어 있어요"
        ValidationError.Reason.TOO_LONG -> "너무 길어요"
        ValidationError.Reason.TOO_SHORT -> "너무 짧아요"
        ValidationError.Reason.TOO_MANY -> "개수가 너무 많아요"
        ValidationError.Reason.TOO_FEW -> "하나 이상 넣어 주세요"
        ValidationError.Reason.OUT_OF_RANGE -> "값의 범위를 벗어났어요"
        ValidationError.Reason.DUPLICATE -> "같은 종류가 중복됐어요"
        ValidationError.Reason.CONDITIONS_WITHOUT_TRIGGER -> "버튼·말로만 실행하는 루틴에는 상태 조건을 넣을 수 없어요"
        ValidationError.Reason.UNSUPPORTED_SCHEMA_VERSION -> "지원하지 않는 형식이에요"
    }
    return "$what: $why"
}

/** 직접 만들기(S09 루틴 편집기). 저장 시 계약 검증(RoutineValidator)과 편집 규칙(EditorRules)을 통과해야 한다. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditorScreen(c: AppController, localId: String?) {
    val existing: StoredRoutine? = remember(localId) { localId?.let { id -> c.routines.firstOrNull { it.localId == id } } }
    var d by remember(localId) { mutableStateOf(existing?.let { Draft.from(it.spec) } ?: Draft()) }
    var errors by remember(localId) { mutableStateOf<List<String>>(emptyList()) }
    var editing by remember(localId) { mutableStateOf<Int?>(null) }
    var confirmDelete by remember { mutableStateOf(false) }
    val caps = c.caps

    Column(Modifier.fillMaxSize()) {
    Column(
        Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // ---- 이름
        SectionTitle("이름 (2~12자)")
        OutlinedTextField(
            value = d.name,
            onValueChange = { if (it.length <= 20) d = d.copy(name = it) },
            singleLine = true,
            textStyle = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("예: 퇴근길 히터") },
        )

        // ---- 이럴 때 (사건 1개)
        SectionTitle("이럴 때")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            TKind.entries.forEach { k -> Choice(k.label, d.kind == k, { d = d.copy(kind = k) }) }
        }
        val trig = d.trigger()
        if (trig != null && trig.requiredCap !in caps) {
            Note("내 차는 '${Labels.cap(trig.requiredCap)}'을(를) 지원하지 않아 자동 실행되지 않아요", warn = true)
        }
        when (d.kind) {
            TKind.ENTER, TKind.EXIT -> {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(PlaceKey.HOME, PlaceKey.WORK).forEach { p -> Choice(Labels.place(p), d.place == p, { d = d.copy(place = p) }) }
                }
                if (d.place !in c.places) Note("'${Labels.place(d.place)}' 위치가 아직 없어요. 시뮬레이터나 가져오기 시트에서 지정하면 자동 실행돼요.", warn = true)
            }
            TKind.ENERGY -> {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    EnergySource.entries.forEach { s -> Choice(Labels.energy(s), d.source == s, { d = d.copy(source = s) }) }
                }
                Stepper("${d.percent}% 아래", { d = d.copy(percent = (d.percent - 5).coerceAtLeast(5)) }, { d = d.copy(percent = (d.percent + 5).coerceAtMost(95)) })
            }
            else -> Unit
        }

        // ---- 그리고 (상태 조건 0~2)
        if (d.kind != TKind.MANUAL) {
            SectionTitle("그리고 (선택, 최대 2개)")
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Choice("시간대", d.timeOn, { d = d.copy(timeOn = !d.timeOn) })
                Choice("실내 온도", d.cabinOn, { d = d.copy(cabinOn = !d.cabinOn) })
            }
            if (d.timeOn) {
                CarCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (Cap.TIME !in caps) Note("내 차는 시간 조건을 지원하지 않아요", warn = true)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            listOf("매일" to DayOfWeek.entries.toSet(), "평일" to Labels.WEEKDAYS, "주말" to Labels.WEEKEND).forEach { (label, set) ->
                                Choice(label, d.days == set, { d = d.copy(days = set) })
                            }
                        }
                        Text("시작", style = MaterialTheme.typography.labelLarge, color = Tok.text2)
                        Stepper(hm(d.start), { d = d.copy(start = d.start.minusMinutes(30)) }, { d = d.copy(start = d.start.plusMinutes(30)) })
                        Text("끝", style = MaterialTheme.typography.labelLarge, color = Tok.text2)
                        Stepper(hm(d.end), { d = d.copy(end = d.end.minusMinutes(30)) }, { d = d.copy(end = d.end.plusMinutes(30)) })
                        if (d.end < d.start) Text("자정을 넘겨요", style = MaterialTheme.typography.bodySmall, color = Tok.text2)
                    }
                }
            }
            if (d.cabinOn) {
                CarCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (Cap.HVAC_CABIN_TEMP !in caps) Note("내 차는 실내 온도를 읽지 못해 이 조건은 맞지 않아요", warn = true)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Choice("이하", d.cabinOp == TempOp.BELOW, { d = d.copy(cabinOp = TempOp.BELOW) })
                            Choice("이상", d.cabinOp == TempOp.ABOVE, { d = d.copy(cabinOp = TempOp.ABOVE) })
                        }
                        Stepper("${d.cabinValue}℃", { d = d.copy(cabinValue = (d.cabinValue - 1).coerceAtLeast(-20)) }, { d = d.copy(cabinValue = (d.cabinValue + 1).coerceAtMost(50)) })
                    }
                }
            }
        }

        // ---- 이렇게 (동작 1~5)
        SectionTitle("이렇게 해요 (1~5개)")
        d.actions.forEachIndexed { i, a ->
            val supported = a.action.requiredCap in caps || (a.fallback?.requiredCap?.let { it in caps } ?: false)
            CarCard(Modifier.fillMaxWidth(), dashed = !supported) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(Labels.action(a.action), style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                        if (a.action !is Action.SuggestCharger) {
                            TextButton(onClick = { editing = if (editing == i) null else i }) {
                                Text(if (editing == i) "완료" else "바꾸기", color = Tok.brand, style = MaterialTheme.typography.labelLarge)
                            }
                        }
                        TextButton(onClick = {
                            d = d.copy(actions = d.actions.filterIndexed { j, _ -> j != i }); editing = null
                        }) { Text("삭제", color = Tok.danger, style = MaterialTheme.typography.labelLarge) }
                    }
                    a.fallback?.let { Text("이 차에 없으면 대신: ${Labels.action(it)}", style = MaterialTheme.typography.bodySmall, color = Tok.text2) }
                    if (!supported) Text("내 차에서는 지원하지 않아 실행할 때 빠져요", style = MaterialTheme.typography.bodySmall, color = Tok.partFg)
                    if (editing == i) {
                        ActionParams(a.action) { na -> d = d.copy(actions = d.actions.mapIndexed { j, x -> if (j == i) x.copy(action = na) else x }) }
                    }
                }
            }
        }
        if (d.actions.size < 5) {
            Text("동작 추가", style = MaterialTheme.typography.labelLarge, color = Tok.text2)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Draft.ACTION_TYPES.forEach { (type, cap) ->
                    val used = d.actions.any { it.action.type == type }
                    Choice(
                        Labels.actionName(type) + if (cap !in caps) " (미지원)" else "",
                        false,
                        {
                            d = d.copy(actions = d.actions + ActionSpec(Draft.defaultAction(type)))
                            editing = d.actions.lastIndex
                            if (cap !in caps) c.showBanner("내 차는 '${Labels.actionName(type)}'을(를) 지원하지 않아요", "저장은 되지만 실행할 때 빠져요", com.carutine.app.BannerTone.WARN)
                        },
                        enabled = !used,
                    )
                }
            }
        }

        // ---- 저장
        errors.forEach { Text(it, color = Tok.danger, style = MaterialTheme.typography.bodyMedium) }
        androidx.compose.foundation.layout.Spacer(Modifier.padding(bottom = 16.dp))
    }
        // 저장은 하단 고정(스크롤 없이 항상 보임)
        BottomBar {
            if (errors.isNotEmpty()) Text("${errors.size}개 항목을 고쳐 주세요", color = Tok.danger, style = MaterialTheme.typography.bodyMedium)
            if (existing != null) SecondaryButton("삭제", { confirmDelete = true })
            PrimaryButton("저장", {
                val spec = d.toSpec(existing?.spec)
                val others = c.routines.filter { it.localId != existing?.localId }.map { it.spec.name }
                val errs = buildList {
                    EditorRules.nameError(spec.name, others)?.let { add("이름: $it") }
                    RoutineValidator.validate(spec).forEach { add(reasonText(it)) }
                    spec.actions.forEach { a -> (a.action as? Action.Speak)?.let { s -> if (s.text.length > EditorRules.SPEAK_MAX) add("음성 안내: ${EditorRules.SPEAK_MAX}자 이하로 써 주세요") } }
                }.distinct()
                errors = errs
                if (errs.isEmpty()) {
                    c.saveRoutine(existing, spec)
                    c.back()
                }
            }, Modifier.weight(1f))
        }
    }

    if (confirmDelete && existing != null) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("'${existing.spec.name}' 루틴을 지울까요?") },
            confirmButton = {
                TextButton(onClick = { confirmDelete = false; c.delete(existing.localId); c.back() }) { Text("지우기", color = Tok.danger) }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("취소") } },
            containerColor = Tok.surface,
        )
    }
}

private fun hm(t: LocalTime) = "%02d:%02d".format(t.hour, t.minute)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ActionParams(a: Action, onChange: (Action) -> Unit) {
    val rowMod = Modifier
    when (a) {
        is Action.HvacTemp -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Choice("온도 지정", a.mode == HvacTempMode.SET, { onChange(Action.HvacTemp(HvacTempMode.SET, 22.0)) })
                Choice("지금보다 올리기/내리기", a.mode == HvacTempMode.DELTA, { onChange(Action.HvacTemp(HvacTempMode.DELTA, 2.0)) })
            }
            if (a.mode == HvacTempMode.SET) {
                Stepper("${Labels.fmt(a.value)}℃", { onChange(a.copy(value = (a.value - 0.5).coerceAtLeast(16.0))) }, { onChange(a.copy(value = (a.value + 0.5).coerceAtMost(30.0))) })
            } else {
                Stepper(
                    (if (a.value > 0) "+" else "") + Labels.fmt(a.value) + "℃",
                    { val v = a.value - 1; onChange(a.copy(value = (if (v == 0.0) -1.0 else v).coerceAtLeast(-3.0))) },
                    { val v = a.value + 1; onChange(a.copy(value = (if (v == 0.0) 1.0 else v).coerceAtMost(3.0))) },
                )
            }
        }
        is Action.HvacFan -> Stepper("${a.level}단", { onChange(Action.HvacFan((a.level - 1).coerceAtLeast(0))) }, { onChange(Action.HvacFan((a.level + 1).coerceAtMost(10))) })
        is Action.HvacAir -> Row(rowMod, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Choice("내기", a.mode == AirMode.RECIRC, { onChange(Action.HvacAir(AirMode.RECIRC)) })
            Choice("외기", a.mode == AirMode.FRESH, { onChange(Action.HvacAir(AirMode.FRESH)) })
        }
        is Action.SeatHeater -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Seat.entries.forEach { s -> Choice(Labels.seat(s), a.seat == s, { onChange(a.copy(seat = s)) }) }
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                (0..3).forEach { l -> Choice(if (l == 0) "끄기" else "${l}단", a.level == l, { onChange(a.copy(level = l)) }) }
            }
        }
        is Action.SteeringHeater -> Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Choice("켜기", a.on, { onChange(Action.SteeringHeater(true)) })
            Choice("끄기", !a.on, { onChange(Action.SteeringHeater(false)) })
        }
        is Action.MirrorHeater -> Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Choice("켜기", a.on, { onChange(Action.MirrorHeater(true)) })
            Choice("끄기", !a.on, { onChange(Action.MirrorHeater(false)) })
        }
        is Action.AmbientLight -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Choice("켜기", a.on, { onChange(a.copy(on = true)) })
                Choice("끄기", !a.on, { onChange(Action.AmbientLight(false)) })
            }
            if (a.on) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Choice("현재 색", a.color == null, { onChange(a.copy(color = null)) })
                    AmbientColor.entries.forEach { col -> Choice(Labels.color(col), a.color == col, { onChange(a.copy(color = col)) }) }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Choice("밝기 그대로", a.brightness == null, { onChange(a.copy(brightness = null)) })
                    Choice("밝기 낮게", a.brightness == AmbientBrightness.LOW, { onChange(a.copy(brightness = AmbientBrightness.LOW)) })
                }
            }
        }
        is Action.Speak -> OutlinedTextField(
            value = a.text,
            onValueChange = { if (it.length <= EditorRules.SPEAK_MAX) onChange(Action.Speak(it)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("읽어 줄 문장 (${EditorRules.SPEAK_MAX}자 이하)") },
        )
        Action.SuggestCharger -> Unit
    }
}
