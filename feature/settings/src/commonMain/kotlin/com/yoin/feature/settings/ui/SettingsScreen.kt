package com.yoin.feature.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.feature.settings.viewmodel.SettingsContract
import com.yoin.feature.settings.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * 設定画面
 *
 * 機能:
 * - ユーザープロフィール表示
 * - プラン管理
 * - 通知設定
 * - ダークモード切り替え
 * - サポートページへのナビゲーション
 *
 * @param viewModel SettingsViewModel
 * @param onNavigateToNotificationSettings 通知設定画面への遷移コールバック
 * @param onNavigateToProfileEdit プロフィール編集画面への遷移コールバック
 * @param onNavigateToPremium プレミアムプラン画面への遷移コールバック
 * @param onNavigateToHelp ヘルプ・FAQ画面への遷移コールバック
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToNotificationSettings: () -> Unit = {},
    onNavigateToProfileEdit: (String) -> Unit = {},
    onNavigateToPremium: () -> Unit = {},
    onNavigateToHelp: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is SettingsContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }

                is SettingsContract.Effect.NavigateToProfile -> {
//                    state.userProfile?.id?.let { userId ->
//                        onNavigateToProfileEdit(userId)
//                    }
                }

                is SettingsContract.Effect.NavigateToPlan -> {
                    onNavigateToPremium()
                }

                is SettingsContract.Effect.NavigateToNotification -> {
                    onNavigateToNotificationSettings()
                }

                is SettingsContract.Effect.NavigateToHelp -> {
                    onNavigateToHelp()
                }

                is SettingsContract.Effect.NavigateToContact -> {
                    snackbarHostState.showSnackbar("お問い合わせ画面は未実装です")
                }

                is SettingsContract.Effect.NavigateToTerms -> {
                    snackbarHostState.showSnackbar("利用規約画面は未実装です")
                }

                is SettingsContract.Effect.NavigateToPrivacyPolicy -> {
                    snackbarHostState.showSnackbar("プライバシーポリシー画面は未実装です")
                }
            }
        }
    }

    // 画面表示時の初期化
    LaunchedEffect(Unit) {
        viewModel.onIntent(SettingsContract.Intent.OnScreenDisplayed)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(YoinColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // ステータスバー風の時刻表示
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(YoinColors.Surface)
                    .padding(top = 24.dp)
            ) {
                Text(
                    text = "9:41",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = YoinColors.TextPrimary,
                    letterSpacing = (-0.15).sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ヘッダー
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(YoinColors.Surface)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "設定",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )
            }

            HorizontalDivider(
                color = YoinColors.SurfaceVariant,
                thickness = 0.65.dp
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = YoinColors.Primary)
                }
            } else {
                // ユーザープロフィール
                state.userProfile?.let { profile ->
                    UserProfileCard(
                        profile = profile,
                        onProfilePressed = {
                            viewModel.onIntent(SettingsContract.Intent.OnProfilePressed)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // プラン
                SectionHeader(title = "プラン")

                Spacer(modifier = Modifier.height(8.dp))

                state.plan?.let { plan ->
                    PlanCard(
                        plan = plan,
                        onPlanPressed = {
                            viewModel.onIntent(SettingsContract.Intent.OnPlanPressed)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 一般
                SectionHeader(title = "一般")

                Spacer(modifier = Modifier.height(8.dp))

                GeneralSettingsCard(
                    isDarkModeEnabled = state.isDarkModeEnabled,
                    onNotificationPressed = {
                        viewModel.onIntent(SettingsContract.Intent.OnNotificationPressed)
                    },
                    onDarkModeToggled = { enabled ->
                        viewModel.onIntent(SettingsContract.Intent.OnDarkModeToggled(enabled))
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // サポート
                SectionHeader(title = "サポート")

                Spacer(modifier = Modifier.height(8.dp))

                SupportCard(
                    onHelpPressed = {
                        viewModel.onIntent(SettingsContract.Intent.OnHelpPressed)
                    },
                    onContactPressed = {
                        viewModel.onIntent(SettingsContract.Intent.OnContactPressed)
                    },
                    onTermsPressed = {
                        viewModel.onIntent(SettingsContract.Intent.OnTermsPressed)
                    },
                    onPrivacyPolicyPressed = {
                        viewModel.onIntent(SettingsContract.Intent.OnPrivacyPolicyPressed)
                    }
                )

                Spacer(modifier = Modifier.height(100.dp)) // ボトムナビゲーション用の余白
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
 * セクションヘッダー
 */
@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = YoinColors.TextSecondary,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

/**
 * ユーザープロフィールカード
 */
@Composable
private fun UserProfileCard(
    profile: SettingsContract.UserProfile,
    onProfilePressed: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onProfilePressed),
        shape = RoundedCornerShape(12.dp),
        color = YoinColors.Surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // アバター
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(YoinColors.AccentLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profile.initial,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )
            }

            // 名前とメール
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = profile.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )
                Text(
                    text = profile.email,
                    fontSize = 13.sp,
                    color = YoinColors.TextSecondary
                )
            }

            // 矢印アイコン
            Text(
                text = "›",
                fontSize = 16.sp,
                color = YoinColors.TextSecondary
            )
        }
    }
}

/**
 * プランカード
 */
@Composable
private fun PlanCard(
    plan: SettingsContract.PlanInfo,
    onPlanPressed: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onPlanPressed),
        shape = RoundedCornerShape(12.dp),
        color = YoinColors.Surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // アイコン
            Text(
                text = "👑",
                fontSize = 20.sp
            )

            // プラン情報
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = plan.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )
                Text(
                    text = plan.description,
                    fontSize = 12.sp,
                    color = YoinColors.Primary
                )
            }

            // 矢印アイコン
            Text(
                text = "›",
                fontSize = 16.sp,
                color = YoinColors.Primary
            )
        }
    }
}

/**
 * 一般設定カード
 */
@Composable
private fun GeneralSettingsCard(
    isDarkModeEnabled: Boolean,
    onNotificationPressed: () -> Unit,
    onDarkModeToggled: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        color = YoinColors.Surface,
        shadowElevation = 1.dp
    ) {
        Column {
            // 通知設定
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNotificationPressed)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔔",
                    fontSize = 18.sp
                )
                Text(
                    text = "通知設定",
                    fontSize = 15.sp,
                    color = YoinColors.TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "›",
                    fontSize = 16.sp,
                    color = YoinColors.TextSecondary
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(start = 46.dp),
                color = YoinColors.SurfaceVariant,
                thickness = 0.65.dp
            )

            // ダークモード
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🌙",
                    fontSize = 18.sp
                )
                Text(
                    text = "ダークモード",
                    fontSize = 15.sp,
                    color = YoinColors.TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = isDarkModeEnabled,
                    onCheckedChange = onDarkModeToggled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = YoinColors.OnPrimary,
                        checkedTrackColor = YoinColors.Primary,
                        uncheckedThumbColor = YoinColors.OnPrimary,
                        uncheckedTrackColor = YoinColors.SurfaceVariant
                    )
                )
            }
        }
    }
}

/**
 * サポートカード
 */
@Composable
private fun SupportCard(
    onHelpPressed: () -> Unit,
    onContactPressed: () -> Unit,
    onTermsPressed: () -> Unit,
    onPrivacyPolicyPressed: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        color = YoinColors.Surface,
        shadowElevation = 1.dp
    ) {
        Column {
            // ヘルプ
            SettingItem(
                icon = "❓",
                label = "ヘルプ",
                onClick = onHelpPressed
            )

            HorizontalDivider(
                modifier = Modifier.padding(start = 46.dp),
                color = YoinColors.SurfaceVariant,
                thickness = 0.65.dp
            )

            // お問い合わせ
            SettingItem(
                icon = "💬",
                label = "お問い合わせ",
                onClick = onContactPressed
            )

            HorizontalDivider(
                modifier = Modifier.padding(start = 46.dp),
                color = YoinColors.SurfaceVariant,
                thickness = 0.65.dp
            )

            // 利用規約
            SettingItem(
                icon = "📄",
                label = "利用規約",
                onClick = onTermsPressed
            )

            HorizontalDivider(
                modifier = Modifier.padding(start = 46.dp),
                color = YoinColors.SurfaceVariant,
                thickness = 0.65.dp
            )

            // プライバシーポリシー
            SettingItem(
                icon = "🔒",
                label = "プライバシーポリシー",
                onClick = onPrivacyPolicyPressed,
                showDivider = false
            )
        }
    }
}

/**
 * 設定項目
 */
@Composable
private fun SettingItem(
    icon: String,
    label: String,
    onClick: () -> Unit,
    showDivider: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 18.sp
        )
        Text(
            text = label,
            fontSize = 15.sp,
            color = YoinColors.TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "›",
            fontSize = 16.sp,
            color = YoinColors.TextSecondary
        )
    }
}
