package com.yoin.core.ui.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ripple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Yoin タブコンポーネント
 *
 * @param tabs タブラベルリスト
 * @param selectedIndex 選択中のタブインデックス
 * @param onTabSelected タブ選択時のコールバック
 * @param modifier カスタムModifier
 */
@Composable
fun YoinTabRow(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    require(tabs.isNotEmpty()) { "tabs must not be empty" }
    require(selectedIndex in tabs.indices) { "selectedIndex must be within tabs range" }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(YoinColors.Surface)
    ) {
        // タブ行
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, label ->
                YoinTab(
                    label = label,
                    selected = index == selectedIndex,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // インジケーターを描画（BoxWithConstraintsで幅を取得）
        TabIndicator(
            selectedIndex = selectedIndex,
            tabCount = tabs.size
        )
    }
}

/**
 * 個別タブ
 */
@Composable
private fun YoinTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .height(48.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = true,
                    color = YoinColors.Primary.copy(alpha = 0.3f)
                ),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) YoinColors.Primary else YoinColors.TextSecondary
        )
    }
}

/**
 * タブインジケーター（アニメーション付き）
 */
@Composable
private fun TabIndicator(
    selectedIndex: Int,
    tabCount: Int
) {
    androidx.compose.foundation.layout.BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {
        val tabWidth = maxWidth / tabCount
        val indicatorWidth = tabWidth

        // アニメーション付きオフセット
        val animatedOffset by animateDpAsState(
            targetValue = tabWidth * selectedIndex,
            animationSpec = tween(durationMillis = 200),
            label = "indicatorOffset"
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = animatedOffset)
                .width(indicatorWidth)
                .height(3.dp)
                .background(YoinColors.Primary)
        )
    }
}

// ============================================
// Previews
// ============================================

/**
 * 2タブのプレビュー
 */
@Preview
@Composable
private fun YoinTabRowPreview2Tabs() {
    var selectedIndex by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.background(YoinColors.Background)) {
        YoinTabRow(
            tabs = listOf("アカウント", "プライバシー"),
            selectedIndex = selectedIndex,
            onTabSelected = { selectedIndex = it }
        )
    }
}

/**
 * 3タブのプレビュー（TimelineScreen用）
 */
@Preview
@Composable
private fun YoinTabRowPreview3Tabs() {
    var selectedIndex by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.background(YoinColors.Background)) {
        YoinTabRow(
            tabs = listOf("全て", "フィルムカメラ", "動画"),
            selectedIndex = selectedIndex,
            onTabSelected = { selectedIndex = it }
        )
    }
}

/**
 * 4タブのプレビュー
 */
@Preview
@Composable
private fun YoinTabRowPreview4Tabs() {
    var selectedIndex by remember { mutableIntStateOf(1) }

    Column(modifier = Modifier.background(YoinColors.Background)) {
        YoinTabRow(
            tabs = listOf("すべて", "旅行別", "お気に入り", "アーカイブ"),
            selectedIndex = selectedIndex,
            onTabSelected = { selectedIndex = it }
        )
    }
}

/**
 * 選択状態比較プレビュー
 */
@Preview
@Composable
private fun YoinTabRowPreviewSelectionStates() {
    Column(modifier = Modifier.background(YoinColors.Background)) {
        Text(
            text = "1番目選択",
            color = YoinColors.TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        YoinTabRow(
            tabs = listOf("アカウント", "プライバシー", "通知"),
            selectedIndex = 0,
            onTabSelected = {}
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "2番目選択",
            color = YoinColors.TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        YoinTabRow(
            tabs = listOf("アカウント", "プライバシー", "通知"),
            selectedIndex = 1,
            onTabSelected = {}
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "3番目選択",
            color = YoinColors.TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        YoinTabRow(
            tabs = listOf("アカウント", "プライバシー", "通知"),
            selectedIndex = 2,
            onTabSelected = {}
        )
    }
}
