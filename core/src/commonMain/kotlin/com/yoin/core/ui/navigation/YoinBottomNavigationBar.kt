package com.yoin.core.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Yoinアプリのボトムナビゲーションバー
 *
 * Figmaデザイン「01_home」に基づいた実装:
 * - 5つのアイテム: ホーム、アルバム、中央のFAB、Shop、設定
 * - 中央のFABは大きな丸いボタン（茶色/ブラウン系）
 * - 通常のタブはアイコン+ラベル（グレー）
 * - 白背景
 *
 * @param selectedRoute 現在選択されているルート
 * @param onNavigate ナビゲーションアイテムがクリックされたときのコールバック
 * @param onCreatePost 中央のFABボタンがクリックされたときのコールバック
 * @param modifier Modifier
 */
@Composable
fun YoinBottomNavigationBar(
    selectedRoute: String,
    onNavigate: (String) -> Unit,
    onCreatePost: () -> Unit,
    modifier: Modifier = Modifier
) {
    // companion objectの初期化順序の問題を回避するため、ここで直接リストを定義
    val items = listOf(
        BottomNavigationItem.Home,
        BottomNavigationItem.Album,
        BottomNavigationItem.Shop,
        BottomNavigationItem.Settings
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左側の2つのアイテム
            items.take(2).forEach { item ->
                BottomNavItem(
                    item = item,
                    isSelected = selectedRoute == item.route,
                    onClick = { onNavigate(item.route) },
                    modifier = Modifier.weight(1f)
                )
            }

            // 中央のFABボタン
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                FloatingActionButton(
                    onClick = onCreatePost,
                    containerColor = MaterialTheme.colorScheme.secondary, // YoinColors.Orange
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "投稿を作成",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // 右側の2つのアイテム
            items.drop(2).forEach { item ->
                BottomNavItem(
                    item = item,
                    isSelected = selectedRoute == item.route,
                    onClick = { onNavigate(item.route) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * ボトムナビゲーションの個別アイテム
 */
@Composable
private fun BottomNavItem(
    item: BottomNavigationItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                modifier = Modifier.size(24.dp),
                tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
            )
        }
        Text(
            text = item.label,
            fontSize = 12.sp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
        )
    }
}
