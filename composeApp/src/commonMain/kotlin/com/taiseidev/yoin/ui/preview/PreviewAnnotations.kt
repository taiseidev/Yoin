package com.taiseidev.yoin.ui.preview

import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * スマートフォンサイズでのPreview
 * デフォルト: 360x800 (一般的なスマホサイズ)
 */
@Preview(
    name = "Phone",
    widthDp = 360,
    heightDp = 800
)
annotation class PhonePreview

/**
 * ライトモードとダークモードの両方でPreview
 * ※実際のテーマ切り替えは、Composable内でMaterialThemeを使って実装する必要があります
 */
@Preview(
    name = "Light Mode",
    widthDp = 360,
    heightDp = 800
)
@Preview(
    name = "Dark Mode",
    widthDp = 360,
    heightDp = 800
)
annotation class LightAndDarkPreview

/**
 * 様々なデバイスサイズでのPreview
 */
@Preview(
    name = "Normal Font (360×800)",
    widthDp = 360,
    heightDp = 800
)
@Preview(
    name = "Large Font Display (360×800)",
    widthDp = 360,
    heightDp = 800
)
@Preview(
    name = "Extra Large Font Display (360×800)",
    widthDp = 360,
    heightDp = 800
)
annotation class FontScalePreview

/**
 * 包括的なPreview（様々なデバイスサイズとシナリオ）
 */
@Preview(
    name = "Phone - Portrait",
    widthDp = 360,
    heightDp = 800
)
@Preview(
    name = "Phone - Landscape",
    widthDp = 800,
    heightDp = 360
)
@Preview(
    name = "Small Phone",
    widthDp = 320,
    heightDp = 640
)
@Preview(
    name = "Large Phone",
    widthDp = 412,
    heightDp = 915
)
@Preview(
    name = "Tablet",
    widthDp = 768,
    heightDp = 1024
)
annotation class ComprehensivePreview
