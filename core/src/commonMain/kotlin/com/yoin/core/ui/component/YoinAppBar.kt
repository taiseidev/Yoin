package com.yoin.core.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinFontSizes
import com.yoin.core.design.theme.YoinSizes
import com.yoin.core.design.theme.YoinSpacing

/**
 * Yoinアプリケーションの統一AppBarコンポーネント
 *
 * 全画面で一貫したヘッダーデザインを提供します。
 *
 * 主な機能:
 * - ステータスバー領域の確保
 * - 戻るボタン・タイトル・アクションボタンの配置
 * - 統一されたスタイル（高さ、背景色、シャドウ、区切り線）
 *
 * @param title ヘッダーのタイトル
 * @param modifier Modifier
 * @param navigationIcon 左側のナビゲーションアイコン（通常は戻るボタン）
 * @param actions 右側のアクションボタン群
 * @param backgroundColor 背景色（デフォルト: YoinColors.Surface）
 * @param elevation シャドウの高さ（デフォルト: 1.dp）
 * @param showDivider 区切り線を表示するか（デフォルト: true）
 * @param titleCentered タイトルを中央配置するか（デフォルト: false）
 * @param includeStatusBarPadding ステータスバーパディングを含めるか（デフォルト: true）
 */
@Composable
fun YoinAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = YoinColors.Surface,
    elevation: Dp = 1.dp,
    showDivider: Boolean = true,
    titleCentered: Boolean = false,
    includeStatusBarPadding: Boolean = true
) {
    Surface(
        color = backgroundColor,
        shadowElevation = elevation,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // ステータスバーパディング
            if (includeStatusBarPadding) {
                Spacer(modifier = Modifier.height(YoinSizes.statusBarHeight))
            }

            // ヘッダーコンテンツ
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(YoinSizes.headerHeight)
                    .padding(horizontal = YoinSpacing.lg),
                horizontalArrangement = if (titleCentered) Arrangement.SpaceBetween else Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ナビゲーションアイコン（左側）
                if (navigationIcon != null) {
                    navigationIcon()
                    if (!titleCentered) {
                        Spacer(modifier = Modifier.width(YoinSpacing.md))
                    }
                }

                // タイトル
                if (titleCentered) {
                    // 中央配置の場合
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            fontSize = YoinFontSizes.headingSmall.value.sp,
                            fontWeight = FontWeight.Bold,
                            color = YoinColors.TextPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // 左寄せの場合
                    Text(
                        text = title,
                        fontSize = YoinFontSizes.headingSmall.value.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                // アクションボタン（右側）
                Row(
                    horizontalArrangement = Arrangement.spacedBy(YoinSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    actions()
                }
            }

            // 区切り線
            if (showDivider) {
                HorizontalDivider(
                    color = YoinColors.SurfaceVariant,
                    thickness = 0.65.dp
                )
            }
        }
    }
}

/**
 * シンプルなヘッダー（タイトルのみ）
 *
 * @param title タイトル
 * @param modifier Modifier
 */
@Composable
fun YoinSimpleAppBar(
    title: String,
    modifier: Modifier = Modifier
) {
    YoinAppBar(
        title = title,
        modifier = modifier,
        titleCentered = true
    )
}
