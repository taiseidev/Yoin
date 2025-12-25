package com.yoin.core.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * ボトムナビゲーションのアイテム定義
 *
 * @property route ナビゲーションルート
 * @property label 表示ラベル
 * @property icon アイコン
 */
sealed class BottomNavigationItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    /**
     * ホームタブ
     */
    data object Home : BottomNavigationItem(
        route = "home",
        label = "ホーム",
        icon = Icons.Default.Home
    )

    /**
     * アルバム（タイムライン）タブ
     */
    data object Album : BottomNavigationItem(
        route = "timeline",
        label = "アルバム",
        icon = Icons.Default.Image
    )

    /**
     * Shopタブ
     */
    data object Shop : BottomNavigationItem(
        route = "shop",
        label = "Shop",
        icon = Icons.Default.ShoppingBag
    )

    /**
     * 設定タブ
     */
    data object Settings : BottomNavigationItem(
        route = "settings",
        label = "設定",
        icon = Icons.Default.Settings
    )

    companion object {
        /**
         * すべてのナビゲーションアイテム（中央のFABボタンを除く）
         */
        val items = listOf(Home, Album, Shop, Settings)
    }
}
