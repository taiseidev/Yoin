package com.yoin.feature.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.ui.component.YoinAppBar
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.settings.viewmodel.NotificationSettingsContract
import com.yoin.feature.settings.viewmodel.NotificationSettingsViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * 通知設定画面
 *
 * 機能:
 * - プッシュ通知のマスタートグル
 * - アクティビティ通知の個別設定
 * - お知らせ通知の個別設定
 *
 * @param viewModel NotificationSettingsViewModel
 * @param onNavigateBack 戻るボタンのコールバック
 */
@Composable
fun NotificationSettingsScreen(
    viewModel: NotificationSettingsViewModel,
    onNavigateBack: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is NotificationSettingsContract.Effect.NavigateBack -> onNavigateBack()
                is NotificationSettingsContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is NotificationSettingsContract.Effect.ShowSuccess -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(YoinColors.Background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ヘッダー
            YoinAppBar(
                title = "通知設定",
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.handleIntent(NotificationSettingsContract.Intent.OnBackPressed) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = YoinColors.TextPrimary
                        )
                    }
                }
            )

            // 設定リスト
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // プッシュ通知（マスタースイッチ）
                SettingCard {
                    SettingItemWithSwitch(
                        iconVector = Icons.Filled.Notifications,
                        title = "プッシュ通知",
                        description = "すべての通知を受け取る",
                        checked = state.settings.pushNotificationEnabled,
                        onCheckedChange = { enabled ->
                            viewModel.handleIntent(
                                NotificationSettingsContract.Intent.OnPushNotificationToggled(enabled)
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // アクティビティセクション
                SectionHeader(title = "アクティビティ")

                Spacer(modifier = Modifier.height(8.dp))

                SettingCard {
                    Column {
                        SettingItemWithSwitch(
                            title = "旅行への招待",
                            checked = state.settings.tripInvitationEnabled,
                            enabled = state.settings.pushNotificationEnabled,
                            onCheckedChange = { enabled ->
                                viewModel.handleIntent(
                                    NotificationSettingsContract.Intent.OnTripInvitationToggled(enabled)
                                )
                            }
                        )

                        HorizontalDivider(
                            color = YoinColors.SurfaceVariant,
                            thickness = 0.65.dp,
                            modifier = Modifier.padding(start = 16.dp)
                        )

                        SettingItemWithSwitch(
                            title = "メンバーの参加",
                            checked = state.settings.memberJoinedEnabled,
                            enabled = state.settings.pushNotificationEnabled,
                            onCheckedChange = { enabled ->
                                viewModel.handleIntent(
                                    NotificationSettingsContract.Intent.OnMemberJoinedToggled(enabled)
                                )
                            }
                        )

                        HorizontalDivider(
                            color = YoinColors.SurfaceVariant,
                            thickness = 0.65.dp,
                            modifier = Modifier.padding(start = 16.dp)
                        )

                        SettingItemWithSwitch(
                            title = "現像完了",
                            checked = state.settings.photoDevelopedEnabled,
                            enabled = false, // 常にON、変更不可
                            onCheckedChange = { /* 何もしない */ }
                        )

                        HorizontalDivider(
                            color = YoinColors.SurfaceVariant,
                            thickness = 0.65.dp,
                            modifier = Modifier.padding(start = 16.dp)
                        )

                        SettingItemWithSwitch(
                            title = "旅行リマインダー",
                            checked = state.settings.tripReminderEnabled,
                            enabled = state.settings.pushNotificationEnabled,
                            onCheckedChange = { enabled ->
                                viewModel.handleIntent(
                                    NotificationSettingsContract.Intent.OnTripReminderToggled(enabled)
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // お知らせセクション
                SectionHeader(title = "お知らせ")

                Spacer(modifier = Modifier.height(8.dp))

                SettingCard {
                    Column {
                        SettingItemWithSwitch(
                            title = "新機能・アップデート",
                            checked = state.settings.newFeatureEnabled,
                            enabled = state.settings.pushNotificationEnabled,
                            onCheckedChange = { enabled ->
                                viewModel.handleIntent(
                                    NotificationSettingsContract.Intent.OnNewFeatureToggled(enabled)
                                )
                            }
                        )

                        HorizontalDivider(
                            color = YoinColors.SurfaceVariant,
                            thickness = 0.65.dp,
                            modifier = Modifier.padding(start = 16.dp)
                        )

                        SettingItemWithSwitch(
                            title = "キャンペーン・セール",
                            checked = state.settings.campaignEnabled,
                            enabled = state.settings.pushNotificationEnabled,
                            onCheckedChange = { enabled ->
                                viewModel.handleIntent(
                                    NotificationSettingsContract.Intent.OnCampaignToggled(enabled)
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 情報バナー
                InfoBanner(
                    icon = Icons.Filled.Lightbulb,
                    message = "現像完了通知はオフにできません。\n大切な思い出をお届けします。"
                )

                Spacer(modifier = Modifier.height(80.dp))
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
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = YoinColors.TextSecondary,
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

/**
 * 設定カード
 */
@Composable
private fun SettingCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = YoinColors.Surface,
        shadowElevation = 1.dp
    ) {
        content()
    }
}

/**
 * スイッチ付き設定項目
 */
@Composable
private fun SettingItemWithSwitch(
    iconVector: androidx.compose.ui.graphics.vector.ImageVector? = null,
    title: String,
    description: String? = null,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // アイコン
        iconVector?.let {
            Icon(
                imageVector = it,
                contentDescription = title,
                tint = if (enabled) YoinColors.TextPrimary else YoinColors.TextSecondary,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 12.dp)
            )
        }

        // テキスト
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = if (enabled) YoinColors.TextPrimary else YoinColors.TextSecondary
            )

            description?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = YoinColors.TextSecondary
                )
            }
        }

        // スイッチ
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = YoinColors.OnPrimary,
                checkedTrackColor = YoinColors.Primary,
                uncheckedThumbColor = YoinColors.OnPrimary,
                uncheckedTrackColor = YoinColors.SurfaceVariant,
                disabledCheckedThumbColor = YoinColors.OnPrimary,
                disabledCheckedTrackColor = YoinColors.Primary.copy(alpha = 0.5f),
                disabledUncheckedThumbColor = YoinColors.OnPrimary,
                disabledUncheckedTrackColor = YoinColors.SurfaceVariant
            )
        )
    }
}

/**
 * 情報バナー
 */
@Composable
private fun InfoBanner(
    icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Filled.Info,
    message: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = YoinColors.Primary
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Info",
                tint = YoinColors.TextPrimary,
                modifier = Modifier
                    .size(16.dp)
                    .padding(end = 8.dp)
            )

            Text(
                text = message,
                fontSize = 12.sp,
                color = YoinColors.TextPrimary,
                lineHeight = 18.sp
            )
        }
    }
}

/**
 * プレビュー
 */
@PhonePreview
@Composable
private fun NotificationSettingsScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            Text("Notification Settings Screen Preview")
        }
    }
}
