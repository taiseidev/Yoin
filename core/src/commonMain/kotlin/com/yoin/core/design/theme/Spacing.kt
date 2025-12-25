package com.yoin.core.design.theme

import androidx.compose.ui.unit.dp

/**
 * Yoinアプリのスペーシングシステム
 *
 * 8dpベースのスペーシングシステムを採用
 */
object YoinSpacing {
    val none = 0.dp
    val xs = 4.dp      // Extra Small
    val sm = 8.dp      // Small
    val md = 12.dp     // Medium
    val lg = 16.dp     // Large
    val xl = 20.dp     // Extra Large
    val xxl = 24.dp    // 2X Large
    val xxxl = 32.dp   // 3X Large
    val huge = 40.dp   // Huge
    val massive = 48.dp // Massive
}

/**
 * コンポーネントサイズの定数
 */
object YoinSizes {
    // ヘッダー・AppBar高さ
    val headerHeight = 56.dp
    val statusBarHeight = 32.dp  // YoinSpacing.xxl相当

    // ボタン高さ
    val buttonHeightLarge = 54.dp
    val buttonHeightMedium = 48.dp
    val buttonHeightSmall = 40.dp

    // アイコンサイズ
    val iconSmall = 20.dp
    val iconMedium = 24.dp
    val iconLarge = 32.dp
    val iconXLarge = 48.dp

    // ロゴサイズ
    val logoSmall = 80.dp
    val logoMedium = 100.dp
    val logoLarge = 120.dp

    // インジケーター
    val indicatorSmall = 8.dp
    val indicatorLarge = 10.dp

    // テキストフィールド高さ
    val textFieldHeight = 54.dp
}

/**
 * フォントサイズの定数
 */
object YoinFontSizes {
    val displayLarge = 40.dp   // 大見出し
    val displayMedium = 32.dp  // 中見出し
    val displaySmall = 28.dp   // 小見出し

    val headingLarge = 26.dp   // 大タイトル
    val headingMedium = 22.dp  // 中タイトル
    val headingSmall = 18.dp   // 小タイトル

    val bodyLarge = 17.dp      // 大本文
    val bodyMedium = 16.dp     // 中本文
    val bodySmall = 15.dp      // 小本文

    val labelLarge = 14.dp     // 大ラベル
    val labelMedium = 13.dp    // 中ラベル
    val labelSmall = 12.dp     // 小ラベル

    val caption = 11.dp        // キャプション
}
