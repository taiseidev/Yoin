package com.yoin.feature.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.design.theme.YoinSizes
import com.yoin.core.design.theme.YoinFontSizes
import com.yoin.core.design.theme.YoinTheme
import com.yoin.core.ui.component.YoinSimpleAppBar
import com.yoin.core.ui.preview.PhonePreview
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
            // ヘッダー
            YoinSimpleAppBar(title = "設定")

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
 * セクションヘッダー - Modern Cinematic Design
 */
@Composable
private fun SectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = YoinSpacing.lg),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = YoinColors.TextSecondary,
            letterSpacing = 1.sp
        )

        // アクセントライン
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            YoinColors.Primary.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

/**
 * ユーザープロフィールカード - Modern Cinematic Design
 */
@Composable
private fun UserProfileCard(
    profile: SettingsContract.UserProfile,
    onProfilePressed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = YoinSpacing.lg)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        YoinColors.Surface,
                        YoinColors.SurfaceVariant
                    )
                )
            )
            .clickable(onClick = onProfilePressed)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // アバター（大きく強調）
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                YoinColors.Primary,
                                YoinColors.AccentCopper
                            )
                        ),
                        CircleShape
                    )
                    .border(3.dp, YoinColors.Surface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profile.initial,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // 編集ボタン
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(YoinColors.Primary)
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit Profile",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // 名前とメール
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = profile.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary,
                letterSpacing = (-0.3).sp
            )
            Text(
                text = profile.email,
                fontSize = 14.sp,
                color = YoinColors.TextSecondary,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

/**
 * プランカード - Modern Cinematic Premium Design
 */
@Composable
private fun PlanCard(
    plan: SettingsContract.PlanInfo,
    onPlanPressed: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = YoinSpacing.lg)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (plan.isPremium) {
                    Brush.linearGradient(
                        colors = listOf(
                            YoinColors.Primary,
                            YoinColors.PrimaryVariant
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            YoinColors.Surface,
                            YoinColors.SurfaceVariant
                        )
                    )
                }
            )
            .clickable(onClick = onPlanPressed)
    ) {
        // グラデーションオーバーレイ
        if (plan.isPremium) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            radius = 600f
                        )
                    )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // プレミアムアイコン（大きく表示）
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        if (plan.isPremium) {
                            Color.White.copy(alpha = 0.2f)
                        } else {
                            YoinColors.Background.copy(alpha = 0.5f)
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Premium Plan",
                    tint = if (plan.isPremium) Color.White else YoinColors.Primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            // プラン情報
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = plan.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (plan.isPremium) Color.White else YoinColors.TextPrimary
                    )
                    if (plan.isPremium) {
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "有効",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
                Text(
                    text = plan.description,
                    fontSize = 13.sp,
                    color = if (plan.isPremium) {
                        Color.White.copy(alpha = 0.9f)
                    } else {
                        YoinColors.TextSecondary
                    }
                )
            }

            // 矢印アイコン
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Navigate",
                tint = if (plan.isPremium) Color.White else YoinColors.TextSecondary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * 一般設定カード - Modern Cinematic Design
 */
@Composable
private fun GeneralSettingsCard(
    isDarkModeEnabled: Boolean,
    onNotificationPressed: () -> Unit,
    onDarkModeToggled: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = YoinSpacing.lg)
            .clip(RoundedCornerShape(16.dp))
            .background(YoinColors.Surface),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // 通知設定
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onNotificationPressed)
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(YoinColors.Primary.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notifications",
                    tint = YoinColors.Primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                text = "通知設定",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = YoinColors.TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Navigate",
                tint = YoinColors.TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(start = 76.dp),
            color = YoinColors.SurfaceVariant,
            thickness = 1.dp
        )

        // ダークモード
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(YoinColors.Primary.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.DarkMode,
                    contentDescription = "Dark Mode",
                    tint = YoinColors.Primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                text = "ダークモード",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = YoinColors.TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = isDarkModeEnabled,
                onCheckedChange = onDarkModeToggled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = YoinColors.Primary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = YoinColors.SurfaceVariant
                )
            )
        }
    }
}

/**
 * サポートカード - Modern Cinematic Design
 */
@Composable
private fun SupportCard(
    onHelpPressed: () -> Unit,
    onContactPressed: () -> Unit,
    onTermsPressed: () -> Unit,
    onPrivacyPolicyPressed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = YoinSpacing.lg)
            .clip(RoundedCornerShape(16.dp))
            .background(YoinColors.Surface),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // ヘルプ
        SettingItem(
            icon = Icons.Filled.Help,
            label = "ヘルプ",
            onClick = onHelpPressed
        )

        HorizontalDivider(
            modifier = Modifier.padding(start = 76.dp),
            color = YoinColors.SurfaceVariant,
            thickness = 1.dp
        )

        // お問い合わせ
        SettingItem(
            icon = Icons.Filled.Email,
            label = "お問い合わせ",
            onClick = onContactPressed
        )

        HorizontalDivider(
            modifier = Modifier.padding(start = 76.dp),
            color = YoinColors.SurfaceVariant,
            thickness = 1.dp
        )

        // 利用規約
        SettingItem(
            icon = Icons.Filled.Article,
            label = "利用規約",
            onClick = onTermsPressed
        )

        HorizontalDivider(
            modifier = Modifier.padding(start = 76.dp),
            color = YoinColors.SurfaceVariant,
            thickness = 1.dp
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

/**
 * 設定項目 - Modern Cinematic Design
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
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(YoinColors.Primary.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = YoinColors.Primary,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = YoinColors.TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = "Navigate",
            tint = YoinColors.TextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * アプリ情報セクション - Modern Cinematic Design
 *
 * アプリのバージョン情報とコピーライトを表示
 */
@Composable
private fun AppInfoSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ロゴ
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            YoinColors.Primary.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Yoin.",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.Primary,
                letterSpacing = (-0.5).sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "余韻を残す旅の記録",
            fontSize = 13.sp,
            color = YoinColors.TextSecondary,
            fontStyle = FontStyle.Italic
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Version 1.0.0",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = YoinColors.TextTertiary
        )
        Text(
            text = "© 2024 Yoin Team",
            fontSize = 12.sp,
            color = YoinColors.TextTertiary
        )
    }
}

// Previews

@PhonePreview
@Composable
private fun UserProfileCardPreview() {
    YoinTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
                .padding(YoinSpacing.lg)
        ) {
            UserProfileCard(
                profile = SettingsContract.UserProfile(
                    name = "山田太郎",
                    email = "yamada@example.com",
                    initial = "山"
                ),
                onProfilePressed = {}
            )
        }
    }
}

@PhonePreview
@Composable
private fun PlanCardPreview() {
    YoinTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
                .padding(YoinSpacing.lg)
        ) {
            PlanCard(
                plan = SettingsContract.PlanInfo(
                    name = "プレミアムプラン",
                    description = "無制限に写真を撮影できます",
                    isPremium = true
                ),
                onPlanPressed = {}
            )
        }
    }
}

@PhonePreview
@Composable
private fun GeneralSettingsCardPreview() {
    YoinTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
                .padding(YoinSpacing.lg)
        ) {
            GeneralSettingsCard(
                isDarkModeEnabled = false,
                onNotificationPressed = {},
                onDarkModeToggled = {}
            )
        }
    }
}

@PhonePreview
@Composable
private fun SupportCardPreview() {
    YoinTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
                .padding(YoinSpacing.lg)
        ) {
            SupportCard(
                onHelpPressed = {},
                onContactPressed = {},
                onTermsPressed = {},
                onPrivacyPolicyPressed = {}
            )
        }
    }
}
