package com.yoin.core.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Yoinアプリのカラーパレット
 * Figmaデザインシステムに基づく
 */
object YoinColors {
    // Primary colors - Figmaデザインから抽出
    val Primary = Color(0xFFC4895F)       // #c4895f - ブラウン/ゴールド
    val PrimaryVariant = Color(0xFF8B7355) // #8b7355 - セカンダリテキスト

    // Background colors
    val Background = Color(0xFFF5EDE3)     // #f5ede3 - メイン背景
    val Surface = Color(0xFFFAF7F2)        // #faf7f2 - カード背景
    val SurfaceVariant = Color(0xFFE5D5C3) // #e5d5c3 - カード枠線

    // Text colors
    val TextPrimary = Color(0xFF1A1A1A)    // #1a1a1a - メインテキスト
    val TextSecondary = Color(0xFF8B7355)  // #8b7355 - セカンダリテキスト
    val TextTertiary = Color(0xFF666666)   // #666 - 非選択ナビゲーション
    val OnPrimary = Color(0xFFFFFFFF)      // 白文字

    // Accent colors
    val AccentBrown = Color(0xFFC4895F)    // #c4895f
    val AccentLight = Color(0xFFE8D4BC)    // #e8d4bc
    val AccentMedium = Color(0xFFD4B896)   // #d4b896

    // Additional colors
    val Error = Color(0xFFFF3B30)          // #ff3b30 - 通知バッジ
    val OnError = Color(0xFFFFFFFF)
}

/**
 * ライトテーマのカラースキーム
 */
private val LightColorScheme = lightColorScheme(
    primary = YoinColors.Primary,
    onPrimary = YoinColors.OnPrimary,
    primaryContainer = YoinColors.AccentLight,
    onPrimaryContainer = YoinColors.TextPrimary,

    secondary = YoinColors.PrimaryVariant,
    onSecondary = YoinColors.OnPrimary,
    secondaryContainer = YoinColors.Surface,
    onSecondaryContainer = YoinColors.TextSecondary,

    background = YoinColors.Background,
    onBackground = YoinColors.TextPrimary,

    surface = YoinColors.Surface,
    onSurface = YoinColors.TextPrimary,
    surfaceVariant = YoinColors.SurfaceVariant,
    onSurfaceVariant = YoinColors.TextSecondary,

    error = YoinColors.Error,
    onError = YoinColors.OnError,
)

/**
 * ダークテーマのカラースキーム
 * TODO: ダークテーマ対応時に適切な色を設定
 */
private val DarkColorScheme = darkColorScheme(
    primary = YoinColors.Primary,
    onPrimary = YoinColors.OnPrimary,
    primaryContainer = YoinColors.AccentMedium,
    onPrimaryContainer = Color(0xFFE6E1E5),

    secondary = YoinColors.PrimaryVariant,
    onSecondary = YoinColors.OnPrimary,
    secondaryContainer = Color(0xFF2C2420),
    onSecondaryContainer = Color(0xFFCAC4D0),

    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),

    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
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
