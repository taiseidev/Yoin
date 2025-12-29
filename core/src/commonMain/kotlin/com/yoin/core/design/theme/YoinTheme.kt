package com.yoin.core.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Yoinアプリのカラーパレット - Modern Cinematic with Amber Accent
 *
 * 写真を最大限に引き立てるダークベースのスタイリッシュなデザイン
 * - 純黒背景で写真を際立たせる
 * - iOSダークモード準拠のグレースケール
 * - アンバー/琥珀色のアクセントで「余韻」を表現
 *   - 夕暮れの残光（旅の終わりの美しさ）
 *   - フィルム写真の温かみ（ノスタルジック）
 *   - 記憶の中で輝く瞬間（柔らかく儚い）
 * - 高コントラストでプロフェッショナルな視認性
 */
object YoinColors {
    // Background colors - ダークでモダン、写真を引き立てる
    val Background = Color(0xFF000000)        // #000000 - 純黒（写真を最大限引き立てる）
    val Surface = Color(0xFF1C1C1E)           // #1c1c1e - iOS準拠のダークサーフェス
    val SurfaceVariant = Color(0xFF2C2C2E)    // #2c2c2e - 少し明るいダークグレー
    val SurfaceBright = Color(0xFF3A3A3C)     // #3a3a3c - 明るいダークグレー（強調用）

    // Text colors - ハイコントラスト
    val TextPrimary = Color(0xFFFFFFFF)       // #ffffff - 純白（最高の視認性）
    val TextSecondary = Color(0xFF8E8E93)     // #8e8e93 - iOS準拠のセカンダリグレー
    val TextTertiary = Color(0xFF636366)      // #636366 - iOS準拠のターシャリグレー
    val OnPrimary = Color(0xFF000000)         // #000000 - 黒文字（明るい背景上）

    // Primary colors - アンバー/琥珀色（「余韻」の温かい記憶、夕暮れの残光）
    val Primary = Color(0xFFFF6B35)           // #ff6b35 - アンバーオレンジ（夕暮れの残光）
    val PrimaryVariant = Color(0xFFE85A24)    // #e85a24 - 濃いアンバー（情熱的な記憶）
    val PrimaryLight = Color(0xFFFF8C5A)      // #ff8c5a - 明るいアンバー（柔らかい余韻）

    // Accent colors - ノスタルジックで温かみのある色彩
    val AccentCopper = Color(0xFFD4886C)      // #d4886c - コッパー（フィルム写真の温かみ）
    val AccentRoseGold = Color(0xFFE8A598)    // #e8a598 - ローズゴールド（儚い余韻）
    val AccentSepia = Color(0xFFB87F6A)       // #b87f6a - セピア（記憶の色褪せ）
    val AccentGray = Color(0xFF48484A)        // #48484a - ミディアムグレー

    // Additional colors - iOS準拠
    val Error = Color(0xFFFF453A)             // #ff453a - iOS準拠のレッド
    val OnError = Color(0xFF000000)           // #000000 - 黒文字
    val Success = Color(0xFF32D74B)           // #32d74b - iOS準拠のグリーン
    val Warning = Color(0xFFFF9500)           // #ff9500 - iOS準拠のオレンジ

    // Legacy colors - 互換性のため残す
    @Deprecated("Use Primary instead", ReplaceWith("Primary"))
    val AccentPeach = Primary
    @Deprecated("Use Primary instead", ReplaceWith("Primary"))
    val AccentCoral = Primary
}

/**
 * ダークテーマのカラースキーム（メイン）
 * 写真アプリとして最適化されたモダンシネマティックデザイン
 */
private val DarkColorScheme = darkColorScheme(
    // Primary
    primary = YoinColors.Primary,
    onPrimary = YoinColors.OnPrimary,
    primaryContainer = YoinColors.PrimaryVariant,
    onPrimaryContainer = YoinColors.OnPrimary,

    // Secondary
    secondary = YoinColors.AccentCopper,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = YoinColors.SurfaceVariant,
    onSecondaryContainer = YoinColors.TextPrimary,

    // Tertiary
    tertiary = YoinColors.AccentRoseGold,
    onTertiary = Color(0xFF000000),

    // Background
    background = YoinColors.Background,
    onBackground = YoinColors.TextPrimary,

    // Surface
    surface = YoinColors.Surface,
    onSurface = YoinColors.TextPrimary,
    surfaceVariant = YoinColors.SurfaceVariant,
    onSurfaceVariant = YoinColors.TextSecondary,

    // Error
    error = YoinColors.Error,
    onError = YoinColors.OnError,

    // Outline
    outline = YoinColors.SurfaceVariant,
    outlineVariant = YoinColors.AccentGray,
)

/**
 * ライトテーマのカラースキーム（サブ）
 * ダークテーマを基本とするが、ライトモード対応
 */
private val LightColorScheme = lightColorScheme(
    // Primary
    primary = YoinColors.PrimaryVariant,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = YoinColors.PrimaryLight,
    onPrimaryContainer = Color(0xFF1A1A1A),

    // Secondary
    secondary = YoinColors.AccentCopper,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF5F5F5),
    onSecondaryContainer = Color(0xFF1A1A1A),

    // Tertiary
    tertiary = YoinColors.AccentRoseGold,
    onTertiary = Color(0xFF000000),

    // Background
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1A1A1A),

    // Surface
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF666666),

    // Error
    error = Color(0xFFFF3B30),
    onError = Color(0xFFFFFFFF),

    // Outline
    outline = Color(0xFFE0E0E0),
    outlineVariant = Color(0xFFF0F0F0),
)

/**
 * Yoinアプリのマテリアルテーマ
 *
 * デフォルトでダークテーマを使用（写真アプリとして最適）
 *
 * @param darkTheme ダークテーマを使用するかどうか（デフォルト: true - 写真を引き立てるため）
 * @param content テーマを適用するコンテンツ
 */
@Composable
fun YoinTheme(
    darkTheme: Boolean = true, // 写真アプリとしてダークテーマをデフォルトに
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
