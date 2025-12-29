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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.design.theme.YoinSizes
import com.yoin.core.design.theme.YoinFontSizes
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
            Spacer(modifier = Modifier.height(YoinSpacing.xxl))

            // ヘッダー
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(YoinColors.Surface)
                    .padding(horizontal = YoinSpacing.lg, vertical = YoinSpacing.lg)
            ) {
                Text(
                    text = "設定",
                    fontSize = YoinFontSizes.headingLarge.value.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )
            }

            HorizontalDivider(
                color = YoinColors.SurfaceVariant,
                thickness = 0.65.dp
            )

            Spacer(modifier = Modifier.height(YoinSpacing.sm))

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(YoinSpacing.xxxl),
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

                Spacer(modifier = Modifier.height(YoinSpacing.lg))

                // プラン
                SectionHeader(title = "プラン")

                Spacer(modifier = Modifier.height(YoinSpacing.sm))

                state.plan?.let { plan ->
                    PlanCard(
                        plan = plan,
                        onPlanPressed = {
                            viewModel.onIntent(SettingsContract.Intent.OnPlanPressed)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(YoinSpacing.lg))

                // 一般
                SectionHeader(title = "一般")

                Spacer(modifier = Modifier.height(YoinSpacing.sm))

                GeneralSettingsCard(
                    isDarkModeEnabled = state.isDarkModeEnabled,
                    onNotificationPressed = {
                        viewModel.onIntent(SettingsContract.Intent.OnNotificationPressed)
                    },
                    onDarkModeToggled = { enabled ->
                        viewModel.onIntent(SettingsContract.Intent.OnDarkModeToggled(enabled))
                    }
                )

                Spacer(modifier = Modifier.height(YoinSpacing.lg))

                // サポート
                SectionHeader(title = "サポート")

                Spacer(modifier = Modifier.height(YoinSpacing.sm))

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

                Spacer(modifier = Modifier.height(YoinSpacing.lg))

                // アプリ情報
                AppInfoSection()

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
        fontSize = YoinFontSizes.labelMedium.value.sp,
        fontWeight = FontWeight.Bold,
        color = YoinColors.TextSecondary,
        modifier = Modifier.padding(horizontal = YoinSpacing.lg)
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
            .padding(horizontal = YoinSpacing.lg)
            .clickable(onClick = onProfilePressed),
        shape = RoundedCornerShape(YoinSpacing.md),
        color = YoinColors.Surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(YoinSpacing.lg),
            horizontalArrangement = Arrangement.spacedBy(YoinSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // アバター
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(YoinColors.AccentPeach, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profile.initial,
                    fontSize = YoinFontSizes.headingSmall.value.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )
            }

            // 名前とメール
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(YoinSpacing.xs)
            ) {
                Text(
                    text = profile.name,
                    fontSize = YoinFontSizes.headingSmall.value.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )
                Text(
                    text = profile.email,
                    fontSize = YoinFontSizes.labelMedium.value.sp,
                    color = YoinColors.TextSecondary
                )
            }

            // 矢印アイコン
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Navigate",
                tint = YoinColors.TextSecondary,
                modifier = Modifier.size(YoinSizes.iconSmall)
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
            .padding(horizontal = YoinSpacing.lg)
            .clickable(onClick = onPlanPressed),
        shape = RoundedCornerShape(YoinSpacing.md),
        color = YoinColors.Surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(YoinSpacing.lg),
            horizontalArrangement = Arrangement.spacedBy(YoinSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // アイコン
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Premium Plan",
                tint = YoinColors.Primary,
                modifier = Modifier.size(YoinSizes.iconMedium)
            )

            // プラン情報
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(YoinSpacing.xs)
            ) {
                Text(
                    text = plan.name,
                    fontSize = YoinFontSizes.bodySmall.value.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )
                Text(
                    text = plan.description,
                    fontSize = YoinFontSizes.labelSmall.value.sp,
                    color = YoinColors.Primary
                )
            }

            // 矢印アイコン
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Navigate",
                tint = YoinColors.Primary,
                modifier = Modifier.size(YoinSizes.iconSmall)
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
            .padding(horizontal = YoinSpacing.lg),
        shape = RoundedCornerShape(YoinSpacing.md),
        color = YoinColors.Surface,
        shadowElevation = 1.dp
    ) {
        Column {
            // 通知設定
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNotificationPressed)
                    .padding(YoinSpacing.lg),
                horizontalArrangement = Arrangement.spacedBy(YoinSpacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notifications",
                    tint = YoinColors.TextPrimary,
                    modifier = Modifier.size(YoinSizes.iconMedium)
                )
                Text(
                    text = "通知設定",
                    fontSize = YoinFontSizes.bodySmall.value.sp,
                    color = YoinColors.TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "Navigate",
                    tint = YoinColors.TextSecondary,
                    modifier = Modifier.size(YoinSizes.iconSmall)
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
                    .padding(YoinSpacing.lg),
                horizontalArrangement = Arrangement.spacedBy(YoinSpacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.DarkMode,
                    contentDescription = "Dark Mode",
                    tint = YoinColors.TextPrimary,
                    modifier = Modifier.size(YoinSizes.iconMedium)
                )
                Text(
                    text = "ダークモード",
                    fontSize = YoinFontSizes.bodySmall.value.sp,
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
            .padding(horizontal = YoinSpacing.lg),
        shape = RoundedCornerShape(YoinSpacing.md),
        color = YoinColors.Surface,
        shadowElevation = 1.dp
    ) {
        Column {
            // ヘルプ
            SettingItem(
                icon = Icons.Filled.Help,
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
                icon = Icons.Filled.ContactSupport,
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
                icon = Icons.Filled.Article,
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
                icon = Icons.Filled.Lock,
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
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    showDivider: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(YoinSpacing.lg),
        horizontalArrangement = Arrangement.spacedBy(YoinSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = YoinColors.TextPrimary,
            modifier = Modifier.size(YoinSizes.iconMedium)
        )
        Text(
            text = label,
            fontSize = YoinFontSizes.bodySmall.value.sp,
            color = YoinColors.TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = "Navigate",
            tint = YoinColors.TextSecondary,
            modifier = Modifier.size(YoinSizes.iconSmall)
        )
    }
}

/**
 * アプリ情報セクション
 *
 * アプリのバージョン情報とコピーライトを表示
 */
@Composable
private fun AppInfoSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = YoinSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(YoinSpacing.xs)
    ) {
        Text(
            text = "Yoin",
            fontSize = YoinFontSizes.bodySmall.value.sp,
            fontWeight = FontWeight.Medium,
            color = YoinColors.TextSecondary
        )
        Text(
            text = "Version 1.0.0",
            fontSize = YoinFontSizes.labelSmall.value.sp,
            color = YoinColors.TextTertiary
        )
        Text(
            text = "\u00a9 2024 Yoin Team",
            fontSize = YoinFontSizes.labelSmall.value.sp,
            color = YoinColors.TextTertiary
        )
    }
}
