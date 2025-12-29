package com.yoin.core.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * バッジ・タグコンポーネント用のカラー定義
 */
object BadgeColors {
    val ModernAccentPrimary = Color(0xFFFF6B35)  // オレンジ
    val ModernGold = Color(0xFFF7B801)           // ゴールド
    val ModernPurple = Color(0xFF6C63FF)         // パープル
    val ModernDark = Color(0xFF2C2C2E)           // ダーク
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF000000)
}

/**
 * 通知バッジコンポーネント
 *
 * 通知数を表示するためのバッジ。
 * - count = null の場合: ドット表示（8dp × 8dp）
 * - count = 0 の場合: 非表示
 * - count > 0 の場合: カウント表示（最小20dp × 20dp）
 * - count > 99 の場合: "99+" で表示
 *
 * @param count 表示するカウント（nullの場合はドット表示）
 * @param showBorder 白いボーダーを表示するか（親要素と区別するため）
 * @param modifier Modifier
 */
@Composable
fun YoinBadge(
    count: Int? = null,
    showBorder: Boolean = true,
    modifier: Modifier = Modifier
) {
    // count = 0 の場合は表示しない
    if (count == 0) return

    val displayText = when {
        count == null -> null  // ドット表示
        count > 99 -> "99+"
        else -> count.toString()
    }

    val borderModifier = if (showBorder) {
        Modifier.border(2.dp, Color.White, CircleShape)
    } else {
        Modifier
    }

    if (displayText == null) {
        // ドット表示
        Box(
            modifier = modifier
                .size(8.dp)
                .then(borderModifier)
                .clip(CircleShape)
                .background(BadgeColors.ModernAccentPrimary)
        )
    } else {
        // カウント表示
        Box(
            modifier = modifier
                .defaultMinSize(minWidth = 20.dp, minHeight = 20.dp)
                .then(borderModifier)
                .clip(CircleShape)
                .background(BadgeColors.ModernAccentPrimary)
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = displayText,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

/**
 * タグのスタイルバリエーション
 */
enum class YoinTagStyle {
    Default,   // #2C2C2E背景、白文字
    Orange,    // #FF6B35背景、白文字
    Gold,      // #F7B801背景、黒文字
    Purple     // #6C63FF背景、白文字
}

/**
 * ステータスタグコンポーネント
 *
 * カプセル型のタグで、ステータスやカテゴリを表示する。
 * 4つのカラーバリエーションをサポート。
 *
 * @param text 表示するテキスト
 * @param style タグのスタイル
 * @param modifier Modifier
 */
@Composable
fun YoinTag(
    text: String,
    style: YoinTagStyle = YoinTagStyle.Default,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (style) {
        YoinTagStyle.Default -> BadgeColors.ModernDark to BadgeColors.White
        YoinTagStyle.Orange -> BadgeColors.ModernAccentPrimary to BadgeColors.White
        YoinTagStyle.Gold -> BadgeColors.ModernGold to BadgeColors.Black
        YoinTagStyle.Purple -> BadgeColors.ModernPurple to BadgeColors.White
    }

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 24.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

// ========================================
// プレビュー
// ========================================

@Preview
@Composable
private fun YoinBadgeDotPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Badge - Dot", fontWeight = FontWeight.Bold)
        YoinBadge(count = null)
    }
}

@Preview
@Composable
private fun YoinBadgeCountPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Badge - Count", fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            YoinBadge(count = 1)
            YoinBadge(count = 9)
            YoinBadge(count = 42)
            YoinBadge(count = 99)
            YoinBadge(count = 100)  // 99+で表示
        }
    }
}

@Preview
@Composable
private fun YoinTagPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Tag Styles", fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            YoinTag(text = "Default", style = YoinTagStyle.Default)
            YoinTag(text = "Orange", style = YoinTagStyle.Orange)
            YoinTag(text = "Gold", style = YoinTagStyle.Gold)
            YoinTag(text = "Purple", style = YoinTagStyle.Purple)
        }
    }
}

@Preview
@Composable
private fun YoinTagUsagePreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Usage Examples", fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            YoinTag(text = "進行中", style = YoinTagStyle.Orange)
            YoinTag(text = "現像待ち", style = YoinTagStyle.Gold)
            YoinTag(text = "完了", style = YoinTagStyle.Default)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            YoinTag(text = "オーナー", style = YoinTagStyle.Purple)
            YoinTag(text = "メンバー", style = YoinTagStyle.Default)
        }
    }
}
