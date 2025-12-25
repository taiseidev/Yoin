package com.yoin.feature.notifications.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.notifications.model.Notification
import com.yoin.feature.notifications.model.NotificationGroup
import com.yoin.feature.notifications.model.NotificationSection
import com.yoin.feature.notifications.model.NotificationType
import com.yoin.feature.notifications.viewmodel.NotificationContract
import com.yoin.feature.notifications.viewmodel.NotificationViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * 通知画面
 *
 * 機能:
 * - 通知リスト表示（セクション分け）
 * - すべて既読機能
 * - 通知タップで詳細画面へ遷移
 *
 * @param viewModel NotificationViewModel
 * @param onNavigateBack 戻るボタンのコールバック
 * @param onNavigateToTripDetail トリップ詳細画面への遷移コールバック
 * @param onNavigateToSettings 通知設定画面への遷移コールバック
 */
@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToTripDetail: (String) -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is NotificationContract.Effect.NavigateBack -> onNavigateBack()
                is NotificationContract.Effect.NavigateToTripDetail -> onNavigateToTripDetail(effect.tripId)
                is NotificationContract.Effect.NavigateToPhotoDetail -> {
                    // TODO: 写真詳細画面への遷移
                }
                is NotificationContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is NotificationContract.Effect.ShowSuccess -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(YoinColors.Surface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ステータスバー風の時刻表示
            Text(
                text = "9:41",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = YoinColors.TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ナビゲーションバー
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 戻るボタン
                Text(
                    text = "←",
                    fontSize = 20.sp,
                    color = YoinColors.TextPrimary,
                    modifier = Modifier.clickable {
                        viewModel.handleIntent(NotificationContract.Intent.OnBackPressed)
                    }
                )

                Spacer(modifier = Modifier.width(16.dp))

                // タイトル
                Text(
                    text = "通知",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )

                Spacer(modifier = Modifier.weight(1f))

                // 設定アイコン
                Text(
                    text = "⚙",
                    fontSize = 20.sp,
                    color = YoinColors.TextSecondary,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clickable {
                            onNavigateToSettings()
                        }
                )

                // すべて既読ボタン
                Text(
                    text = "すべて既読",
                    fontSize = 14.sp,
                    color = YoinColors.Primary,
                    modifier = Modifier.clickable {
                        viewModel.handleIntent(NotificationContract.Intent.OnMarkAllAsRead)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = YoinColors.SurfaceVariant)

            // 通知リスト
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = YoinColors.Primary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    state.notificationGroups.forEach { group ->
                        // セクションヘッダー
                        item {
                            SectionHeader(section = group.section)
                        }

                        // 通知アイテム
                        items(
                            items = group.notifications,
                            key = { it.id }
                        ) { notification ->
                            NotificationItem(
                                notification = notification,
                                onClick = {
                                    viewModel.handleIntent(
                                        NotificationContract.Intent.OnNotificationClicked(notification)
                                    )
                                }
                            )
                        }
                    }

                    // 底部スペース
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        // ホームインジケーター
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .width(134.dp)
                .height(5.dp)
                .background(Color.Black, RoundedCornerShape(100.dp))
        )

        // スナックバー
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * セクションヘッダー
 */
@Composable
private fun SectionHeader(section: NotificationSection) {
    val sectionText = when (section) {
        NotificationSection.TODAY -> "今日"
        NotificationSection.YESTERDAY -> "昨日"
        NotificationSection.OLDER -> "それ以前"
    }

    Text(
        text = sectionText,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = YoinColors.TextSecondary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    )
}

/**
 * 通知アイテム
 */
@Composable
private fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        !notification.isRead && notification.type == NotificationType.PHOTO_DEVELOPED -> Color(0xFFF0F9EE)
        !notification.isRead && notification.type == NotificationType.MEMBER_JOINED -> Color(0xFFFAF8F3)
        else -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 未読インジケーター
        if (!notification.isRead) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color(0xFFFF6B35), CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
        } else {
            Spacer(modifier = Modifier.width(24.dp))
        }

        // アイコン or アバター
        NotificationIcon(
            icon = notification.icon,
            avatarText = notification.avatarText,
            type = notification.type
        )

        Spacer(modifier = Modifier.width(16.dp))

        // 通知内容
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = notification.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            notification.message.split("\n").forEach { line ->
                Text(
                    text = line,
                    fontSize = if (line.contains("前") || line.contains("昨日")) 11.sp else 13.sp,
                    color = if (line.contains("前") || line.contains("昨日")) YoinColors.TextSecondary else YoinColors.TextSecondary
                )
            }
        }

        // 右矢印（未読の場合のみ）
        if (!notification.isRead) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "〉",
                fontSize = 14.sp,
                color = YoinColors.TextSecondary
            )
        }
    }

    HorizontalDivider(
        color = YoinColors.SurfaceVariant,
        modifier = Modifier.padding(start = 64.dp)
    )
}

/**
 * 通知アイコン
 */
@Composable
private fun NotificationIcon(
    icon: String?,
    avatarText: String?,
    type: NotificationType
) {
    val iconBackgroundColor = when (type) {
        NotificationType.PHOTO_DEVELOPED -> YoinColors.Primary
        NotificationType.MEMBER_JOINED -> Color(0xFFE5DCC8)
        NotificationType.INVITATION -> Color(0xFFE5DCC8)
        NotificationType.TRIP_REMINDER -> Color(0xFF3B82F6)
        NotificationType.SYSTEM -> YoinColors.SurfaceVariant
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .background(iconBackgroundColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        when {
            icon != null -> {
                Text(
                    text = icon,
                    fontSize = 20.sp
                )
            }
            avatarText != null -> {
                Text(
                    text = avatarText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )
            }
        }
    }
}

/**
 * プレビュー
 */
@PhonePreview
@Composable
private fun NotificationScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Surface)
        ) {
            Text("Notification Screen Preview")
        }
    }
}
