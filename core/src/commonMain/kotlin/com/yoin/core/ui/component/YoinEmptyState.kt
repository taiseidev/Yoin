package com.yoin.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraRoll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinTheme
import com.yoin.core.ui.preview.PhonePreview

/**
 * Yoinアプリの統一エンプティステートコンポーネント
 *
 * データがない状態やコンテンツが空の場合に表示する汎用コンポーネント。
 * Modern Cinematic with Amber Accent デザインに基づいています。
 *
 * @param icon 表示するアイコン
 * @param title メインメッセージ
 * @param modifier Modifier
 * @param description 説明文（オプション）
 * @param actionButton アクションボタン（オプション）
 */
@Composable
fun YoinEmptyState(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    actionButton: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // アイコン（80dp × 80dp, TextSecondary 50%透明度）
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = YoinColors.TextSecondary.copy(alpha = 0.5f)
            )

            // タイトル（12dp下）
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary,
                textAlign = TextAlign.Center
            )

            // 説明文（8dp下、オプション）
            if (description != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = YoinColors.TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            // アクションボタン（24dp下、オプション）
            if (actionButton != null) {
                Spacer(modifier = Modifier.height(24.dp))
                actionButton()
            }
        }
    }
}

/**
 * デフォルトのアクションボタンコンポーネント
 *
 * YoinEmptyStateで使用する標準的なアクションボタン。
 * アンバー色（#FF6B35）の背景に黒文字。
 *
 * @param text ボタンのテキスト
 * @param onClick クリック時のコールバック
 */
@Composable
fun YoinEmptyStateActionButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = YoinColors.Primary,
            contentColor = YoinColors.OnPrimary
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Medium
        )
    }
}

// ============================================================
// Previews
// ============================================================

/**
 * Preview: 旅行なし（HomeScreen用）
 */
@PhonePreview
@Composable
private fun YoinEmptyStatePreview_NoTrips() {
    YoinTheme {
        YoinEmptyState(
            icon = Icons.Filled.CameraRoll,
            title = "旅はまだありません",
            description = "新しい旅を作成してみましょう",
            actionButton = {
                YoinEmptyStateActionButton(
                    text = "旅を作成",
                    onClick = {}
                )
            }
        )
    }
}

/**
 * Preview: 写真なし（TimelineScreen用）
 */
@PhonePreview
@Composable
private fun YoinEmptyStatePreview_NoPhotos() {
    YoinTheme {
        YoinEmptyState(
            icon = Icons.Filled.Photo,
            title = "写真がありません",
            description = "カメラで思い出を撮影しましょう"
        )
    }
}

/**
 * Preview: 通知なし（NotificationsScreen用）
 */
@PhonePreview
@Composable
private fun YoinEmptyStatePreview_NoNotifications() {
    YoinTheme {
        YoinEmptyState(
            icon = Icons.Filled.Notifications,
            title = "通知はありません"
        )
    }
}
