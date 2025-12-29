package com.yoin.feature.notifications.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.notifications.model.Notification
import com.yoin.feature.notifications.model.NotificationSection
import com.yoin.feature.notifications.model.NotificationType
import com.yoin.feature.notifications.viewmodel.NotificationContract
import com.yoin.feature.notifications.viewmodel.NotificationViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * 通知画面 - Modern Cinematic Design
 *
 * デザインコンセプト:
 * - 黒背景 + グラデーション
 * - 半透明カード形式の通知アイテム
 * - Material Iconsでタイプ別アイコン
 * - グラデーション未読インジケーター
 * - 空状態のイラストレーション
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
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black,
                        YoinColors.Primary.copy(alpha = 0.1f),
                        Color.Black
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // カスタムヘッダー
            CinematicNotificationHeader(
                onBackPressed = {
                    viewModel.handleIntent(NotificationContract.Intent.OnBackPressed)
                },
                onSettingsPressed = onNavigateToSettings,
                onMarkAllAsRead = {
                    viewModel.handleIntent(NotificationContract.Intent.OnMarkAllAsRead)
                }
            )

            // 通知リスト
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = YoinColors.Primary)
                }
            } else if (state.notificationGroups.isEmpty()) {
                // 空状態
                EmptyNotificationState()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    state.notificationGroups.forEach { group ->
                        // セクションヘッダー
                        item {
                            CinematicSectionHeader(section = group.section)
                        }

                        // 通知アイテム
                        items(
                            items = group.notifications,
                            key = { it.id }
                        ) { notification ->
                            CinematicNotificationItem(
                                notification = notification,
                                onClick = {
                                    viewModel.handleIntent(
                                        NotificationContract.Intent.OnNotificationClicked(notification)
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    // 底部スペース
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        // スナックバー
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * シネマティックなヘッダー
 */
@Composable
private fun CinematicNotificationHeader(
    onBackPressed: () -> Unit,
    onSettingsPressed: () -> Unit,
    onMarkAllAsRead: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 戻るボタン
        IconButton(onClick = onBackPressed) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        // タイトル
        Text(
            text = "通知",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // すべて既読ボタン
            TextButton(onClick = onMarkAllAsRead) {
                Text(
                    text = "既読",
                    fontSize = 14.sp,
                    color = YoinColors.Primary,
                    fontWeight = FontWeight.Medium
                )
            }

            // 設定アイコン
            IconButton(onClick = onSettingsPressed) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * シネマティックなセクションヘッダー
 */
@Composable
private fun CinematicSectionHeader(section: NotificationSection) {
    val sectionText = when (section) {
        NotificationSection.TODAY -> "今日"
        NotificationSection.YESTERDAY -> "昨日"
        NotificationSection.OLDER -> "それ以前"
    }

    Text(
        text = sectionText,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White.copy(alpha = 0.9f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 16.dp)
    )
}

/**
 * シネマティックな通知アイテム
 */
@Composable
private fun CinematicNotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {
    val backgroundModifier = if (notification.isRead) {
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .clickable(onClick = onClick)
            .padding(16.dp)
    } else {
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        YoinColors.Primary.copy(alpha = 0.15f),
                        YoinColors.PrimaryVariant.copy(alpha = 0.1f)
                    )
                )
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    }

    Box(modifier = backgroundModifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // アイコン
            CinematicNotificationIcon(
                type = notification.type,
                isRead = notification.isRead
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 通知内容
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 未読インジケーター
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            YoinColors.Primary,
                                            YoinColors.PrimaryVariant
                                        )
                                    )
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(
                        text = notification.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                notification.message.split("\n").forEach { line ->
                    Text(
                        text = line,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        lineHeight = 18.sp
                    )
                }
            }

            // 右矢印（未読の場合のみ）
            if (!notification.isRead) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * シネマティックな通知アイコン
 */
@Composable
private fun CinematicNotificationIcon(
    type: NotificationType,
    isRead: Boolean
) {
    val icon: ImageVector = when (type) {
        NotificationType.PHOTO_DEVELOPED -> Icons.Filled.PhotoCamera
        NotificationType.MEMBER_JOINED -> Icons.Filled.PersonAdd
        NotificationType.INVITATION -> Icons.Filled.Mail
        NotificationType.TRIP_REMINDER -> Icons.Filled.Event
        NotificationType.SYSTEM -> Icons.Filled.Info
    }

    val iconBackgroundModifier = if (isRead) {
        Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.1f))
    } else {
        Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        YoinColors.Primary,
                        YoinColors.PrimaryVariant
                    )
                )
            )
    }

    Box(
        modifier = iconBackgroundModifier,
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isRead) Color.White.copy(alpha = 0.6f) else Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}

/**
 * 空状態
 */
@Composable
private fun EmptyNotificationState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // アイコン
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                YoinColors.Primary.copy(alpha = 0.2f),
                                YoinColors.PrimaryVariant.copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "通知はありません",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "新しい通知が届くとここに表示されます",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
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
                .background(Color.Black)
        ) {
            Text("Notification Screen Preview", color = Color.White)
        }
    }
}
