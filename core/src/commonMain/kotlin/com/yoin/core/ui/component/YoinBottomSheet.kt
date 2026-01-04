package com.yoin.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinTheme
import com.yoin.core.ui.preview.PhonePreview

/**
 * Yoinアプリのボトムシートコンポーネント
 *
 * Modern Cinematic with Amber Accentデザインに基づいたボトムシート
 * - 下からスライド表示
 * - 背景: Surface (#1C1C1E)
 * - 角丸: 上部のみ16dp
 * - ハンドル: 中央に4dp × 40dpの灰色バー
 * - スワイプで閉じる対応
 * - スクリム: #000000 60%透明度
 *
 * 使用例:
 * - 写真オプション（お気に入り、削除）
 * - 旅行設定メニュー
 * - 並び替えオプション
 *
 * @param isVisible ボトムシートの表示状態
 * @param onDismiss 閉じる時のコールバック
 * @param sheetState ボトムシートの状態（スキップ可能なPartiallyExpanded状態など）
 * @param content ボトムシートのコンテンツ
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YoinBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    content: @Composable ColumnScope.() -> Unit
) {
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = YoinColors.Surface,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            scrimColor = Color.Black.copy(alpha = 0.6f),
            dragHandle = {
                YoinBottomSheetHandle()
            }
        ) {
            content()
        }
    }
}

/**
 * ボトムシートのドラッグハンドル
 */
@Composable
fun YoinBottomSheetHandle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp),
            color = YoinColors.SurfaceVariant,
            shape = RoundedCornerShape(2.dp)
        ) {}
    }
}

/**
 * ボトムシートのメニューアイテム
 *
 * @param icon アイコン
 * @param title タイトル
 * @param onClick クリック時のコールバック
 * @param iconTint アイコンの色
 * @param titleColor タイトルの色
 * @param enabled 有効/無効
 */
@Composable
fun YoinBottomSheetItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    iconTint: Color = YoinColors.TextPrimary,
    titleColor: Color = YoinColors.TextPrimary,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (enabled) iconTint else YoinColors.TextTertiary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = if (enabled) titleColor else YoinColors.TextTertiary
        )
    }
}

/**
 * 破壊的アクション用のボトムシートメニューアイテム
 *
 * @param icon アイコン
 * @param title タイトル
 * @param onClick クリック時のコールバック
 */
@Composable
fun YoinBottomSheetDestructiveItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    YoinBottomSheetItem(
        icon = icon,
        title = title,
        onClick = onClick,
        iconTint = YoinColors.Error,
        titleColor = YoinColors.Error
    )
}

/**
 * ボトムシートのセクションヘッダー
 *
 * @param title セクションタイトル
 */
@Composable
fun YoinBottomSheetHeader(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = YoinColors.TextSecondary,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
    )
}

/**
 * ボトムシートのセパレーター
 */
@Composable
fun YoinBottomSheetDivider() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(1.dp),
        color = YoinColors.SurfaceVariant
    ) {}
}

// Previews

@OptIn(ExperimentalMaterial3Api::class)
@PhonePreview
@Composable
private fun YoinBottomSheetPreview() {
    YoinTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            // プレビュー用にシートコンテンツを直接表示
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        color = YoinColors.Surface,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
            ) {
                YoinBottomSheetHandle()

                YoinBottomSheetHeader(title = "写真オプション")

                YoinBottomSheetItem(
                    icon = Icons.Filled.Favorite,
                    title = "お気に入りに追加",
                    onClick = {}
                )

                YoinBottomSheetItem(
                    icon = Icons.Filled.Share,
                    title = "シェア",
                    onClick = {}
                )

                YoinBottomSheetDivider()

                YoinBottomSheetDestructiveItem(
                    icon = Icons.Filled.Delete,
                    title = "削除",
                    onClick = {}
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PhonePreview
@Composable
private fun YoinBottomSheetSortPreview() {
    YoinTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            // プレビュー用にシートコンテンツを直接表示
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        color = YoinColors.Surface,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
            ) {
                YoinBottomSheetHandle()

                YoinBottomSheetHeader(title = "並び替え")

                YoinBottomSheetItem(
                    icon = Icons.AutoMirrored.Filled.Sort,
                    title = "撮影日（新しい順）",
                    onClick = {},
                    iconTint = YoinColors.Primary,
                    titleColor = YoinColors.Primary
                )

                YoinBottomSheetItem(
                    icon = Icons.AutoMirrored.Filled.Sort,
                    title = "撮影日（古い順）",
                    onClick = {}
                )

                YoinBottomSheetItem(
                    icon = Icons.AutoMirrored.Filled.Sort,
                    title = "場所",
                    onClick = {}
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
