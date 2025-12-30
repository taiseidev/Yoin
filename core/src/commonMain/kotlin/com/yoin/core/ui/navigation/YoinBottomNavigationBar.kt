package com.yoin.core.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors

/**
 * Yoinアプリのボトムナビゲーションバー - Modern Cinematic Design
 *
 * デザイン仕様:
 * - 5つのアイテム: ホーム、アルバム、中央のFAB、Shop、設定
 * - 中央のFABはアンバーグラデーション（Primary -> PrimaryVariant）
 * - 選択されたアイテム: アンバー色 + インジケーター
 * - 未選択: TextSecondary
 * - ダークサーフェス背景（#1C1C1E）
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
        color = YoinColors.Surface,
        tonalElevation = 0.dp,
        shadowElevation = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .height(72.dp)
                .padding(horizontal = 8.dp, vertical = 8.dp),
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

            // 中央のFABボタン - Amber Gradient
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    YoinColors.Primary,
                                    YoinColors.PrimaryVariant
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onCreatePost,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "投稿を作成",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
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
 * ボトムナビゲーションの個別アイテム - Modern Cinematic Design
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
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                modifier = Modifier.size(26.dp),
                tint = if (isSelected) YoinColors.Primary else YoinColors.TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = item.label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) YoinColors.Primary else YoinColors.TextSecondary
        )

        // 選択インジケーター
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                YoinColors.Primary,
                                YoinColors.PrimaryVariant
                            )
                        )
                    )
            )
        }
    }
}
