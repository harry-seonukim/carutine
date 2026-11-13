package com.carutine.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 디자인 토큰(디자인 문서·HTML 데모의 차량 야간 테마 `data-car-theme="night"` 값).
 * 차량 화면은 항상 어두운 테마로 둔다(야간 눈부심·주의 분산 최소화).
 */
object Tok {
    val bg = Color(0xFF0F1C2E)
    val surface = Color(0xFF1E3350)
    val sunken = Color(0xFF172A42)
    val border = Color(0xFF2A3A50)
    val text = Color(0xFFFFFFFF)
    val text2 = Color(0xFF9FB0C6)
    val brand = Color(0xFF9CC0E8)
    val onBrand = Color(0xFF0F1C2E)
    val accent = Color(0xFFF28C38)
    val onAccent = Color(0xFF13233A)
    val accentTint = Color(0xFF3A2616)
    val accentInk = Color(0xFFF6B17A)
    val comm = Color(0xFF7FD1C8)
    val commTint = Color(0xFF133A3A)
    val toast = Color(0xFF0A1422)
    val toastMuted = Color(0xFFB9C7D9)
    val danger = Color(0xFFFF8A80)

    // 호환 배지(야간)
    val fullBg = Color(0xFF16362A); val fullFg = Color(0xFF7FD1A0)
    val partBg = Color(0xFF3A2616); val partFg = Color(0xFFF6B17A)
    val noneBg = Color(0xFF172A42); val noneFg = Color(0xFF9FB0C6)

    /** 터치 영역(디자인 문서: 76dp 이상 권장) */
    val touch = 76.dp
    val touchLg = 88.dp
    val radius = 12.dp
}

private val scheme = darkColorScheme(
    primary = Tok.accent,
    onPrimary = Tok.onAccent,
    primaryContainer = Tok.accentTint,
    onPrimaryContainer = Tok.accentInk,
    secondary = Tok.brand,
    onSecondary = Tok.onBrand,
    secondaryContainer = Tok.surface,
    onSecondaryContainer = Tok.text,
    tertiary = Tok.comm,
    background = Tok.bg,
    onBackground = Tok.text,
    surface = Tok.surface,
    onSurface = Tok.text,
    surfaceVariant = Tok.sunken,
    onSurfaceVariant = Tok.text2,
    surfaceContainer = Tok.surface,
    surfaceContainerHigh = Tok.surface,
    surfaceContainerHighest = Tok.sunken,
    surfaceContainerLow = Tok.sunken,
    outline = Tok.border,
    outlineVariant = Tok.border,
    error = Tok.danger,
)

// 차량 화면은 거리가 있어 글자를 크게 둔다(HTML 데모 28px@720dp 기준을 dp 환산).
private val typo = Typography(
    headlineMedium = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, lineHeight = 34.sp),
    headlineSmall = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, lineHeight = 30.sp),
    titleLarge = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold, lineHeight = 28.sp),
    titleMedium = TextStyle(fontSize = 19.sp, fontWeight = FontWeight.Bold, lineHeight = 25.sp),
    bodyLarge = TextStyle(fontSize = 18.sp, lineHeight = 25.sp),
    bodyMedium = TextStyle(fontSize = 16.sp, lineHeight = 22.sp),
    bodySmall = TextStyle(fontSize = 14.sp, lineHeight = 19.sp),
    labelLarge = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, lineHeight = 22.sp),
    labelMedium = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, lineHeight = 19.sp),
    labelSmall = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, lineHeight = 17.sp),
)

@Composable
fun CarutineTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = scheme, typography = typo, content = content)
}
