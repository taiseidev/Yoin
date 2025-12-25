package com.yoin.core.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Yoinアプリのカラーパレット
 * sassyアプリのデザインに基づくコーラル/ピーチカラースキーム
 */
object YoinColors {
    // Primary colors - コーラル/ピーチ系
    val Primary = Color(0xFFFF8B7A)        // #ff8b7a - コーラルピンク
    val PrimaryVariant = Color(0xFFFF6B5A) // #ff6b5a - 濃いコーラル
    val PrimaryLight = Color(0xFFFFAB9A)   // #ffab9a - 明るいコーラル

    // Background colors - 明るく清潔感のある背景
    val Background = Color(0xFFFFFBF8)     // #fffbf8 - オフホワイト背景
    val Surface = Color(0xFFFFFFFF)        // #ffffff - カード背景
    val SurfaceVariant = Color(0xFFFFF5F0) // #fff5f0 - ほんのりピーチ

    // Text colors
    val TextPrimary = Color(0xFF2C2C2C)    // #2c2c2c - メインテキスト
    val TextSecondary = Color(0xFF666666)  // #666666 - セカンダリテキスト
    val TextTertiary = Color(0xFF999999)   // #999999 - 非選択・ヒント
    val OnPrimary = Color(0xFFFFFFFF)      // 白文字

    // Accent colors - サブカラー
    val AccentPeach = Color(0xFFFFCCB8)    // #ffccb8 - 淡いピーチ
    val AccentCoral = Color(0xFFFF8B7A)    // #ff8b7a - アクセント
    val AccentGray = Color(0xFFF5F5F5)     // #f5f5f5 - 背景グレー

    // Additional colors
    val Error = Color(0xFFFF3B30)          // #ff3b30 - エラー
    val OnError = Color(0xFFFFFFFF)
    val Success = Color(0xFF34C759)        // #34c759 - 成功
    val Warning = Color(0xFFFFCC00)        // #ffcc00 - 警告
}

/**
 * ライトテーマのカラースキーム
 * sassyアプリのデザインに基づくコーラルカラー
 */
private val LightColorScheme = lightColorScheme(
    primary = YoinColors.Primary,
    onPrimary = YoinColors.OnPrimary,
    primaryContainer = YoinColors.AccentPeach,
    onPrimaryContainer = YoinColors.TextPrimary,

    secondary = YoinColors.PrimaryLight,
    onSecondary = YoinColors.OnPrimary,
    secondaryContainer = YoinColors.SurfaceVariant,
    onSecondaryContainer = YoinColors.TextPrimary,

    background = YoinColors.Background,
    onBackground = YoinColors.TextPrimary,

    surface = YoinColors.Surface,
    onSurface = YoinColors.TextPrimary,
    surfaceVariant = YoinColors.SurfaceVariant,
    onSurfaceVariant = YoinColors.TextSecondary,

    error = YoinColors.Error,
    onError = YoinColors.OnError,

    tertiary = YoinColors.AccentCoral,
    onTertiary = YoinColors.OnPrimary,
)

/**
 * ダークテーマのカラースキーム
 * 現在はライトテーマをベースに調整
 */
private val DarkColorScheme = darkColorScheme(
    primary = YoinColors.Primary,
    onPrimary = Color(0xFF1C1B1F),
    primaryContainer = Color(0xFF8B4A3A),
    onPrimaryContainer = Color(0xFFFFDDD6),

    secondary = YoinColors.PrimaryLight,
    onSecondary = Color(0xFF1C1B1F),
    secondaryContainer = Color(0xFF3E2723),
    onSecondaryContainer = Color(0xFFFFCCB8),

    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),

    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF2C2420),
    onSurfaceVariant = Color(0xFFCAC4D0),

    error = YoinColors.Error,
    onError = Color(0xFF000000),
)

/**
 * Yoinアプリのマテリアルテーマ
 *
 * @param darkTheme ダークテーマを使用するかどうか（デフォルトはシステム設定に従う）
 * @param content テーマを適用するコンテンツ
 */
@Composable
fun YoinTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = YoinTypography,
        shapes = YoinShapes,
        content = content
    )
}
