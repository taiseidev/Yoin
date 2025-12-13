package com.yoin.core.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Yoinアプリのカラーパレット
 */
object YoinColors {
    // Primary colors
    val Green = Color(0xFF4A7C59)
    val Orange = Color(0xFF8B6F47)

    // Surface colors
    val Background = Color(0xFFFAFAFA)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFF5F5F5)

    // Text colors
    val OnBackground = Color(0xFF1C1B1F)
    val OnSurface = Color(0xFF1C1B1F)
    val OnPrimary = Color(0xFFFFFFFF)

    // Additional colors
    val Error = Color(0xFFB00020)
    val OnError = Color(0xFFFFFFFF)
}

/**
 * ライトテーマのカラースキーム
 */
private val LightColorScheme = lightColorScheme(
    primary = YoinColors.Green,
    onPrimary = YoinColors.OnPrimary,
    primaryContainer = YoinColors.Green.copy(alpha = 0.1f),
    onPrimaryContainer = YoinColors.Green,

    secondary = YoinColors.Orange,
    onSecondary = YoinColors.OnPrimary,
    secondaryContainer = YoinColors.Orange.copy(alpha = 0.1f),
    onSecondaryContainer = YoinColors.Orange,

    background = YoinColors.Background,
    onBackground = YoinColors.OnBackground,

    surface = YoinColors.Surface,
    onSurface = YoinColors.OnSurface,
    surfaceVariant = YoinColors.SurfaceVariant,
    onSurfaceVariant = YoinColors.OnSurface.copy(alpha = 0.6f),

    error = YoinColors.Error,
    onError = YoinColors.OnError,
)

/**
 * ダークテーマのカラースキーム
 * TODO: ダークテーマ対応時に適切な色を設定
 */
private val DarkColorScheme = darkColorScheme(
    primary = YoinColors.Green,
    onPrimary = YoinColors.OnPrimary,
    primaryContainer = YoinColors.Green.copy(alpha = 0.2f),
    onPrimaryContainer = YoinColors.Green,

    secondary = YoinColors.Orange,
    onSecondary = YoinColors.OnPrimary,
    secondaryContainer = YoinColors.Orange.copy(alpha = 0.2f),
    onSecondaryContainer = YoinColors.Orange,

    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),

    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),

    error = Color(0xFFCF6679),
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
