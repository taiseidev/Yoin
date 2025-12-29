package com.yoin.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraRoll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinFontSizes
import com.yoin.core.design.theme.YoinSizes
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.auth.viewmodel.WelcomeContract
import com.yoin.feature.auth.viewmodel.WelcomeViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Welcome画面
 *
 * スクロールなしでログイン/登録の選択肢を表示
 *
 * @param viewModel WelcomeViewModel
 * @param onNavigateToEmailLogin メールログイン画面への遷移
 * @param onNavigateToRegister 登録方法選択画面への遷移
 * @param onNavigateToHome ホーム画面への遷移
 */
@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel,
    onNavigateToEmailLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effect監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is WelcomeContract.Effect.NavigateToEmailLogin -> onNavigateToEmailLogin()
                is WelcomeContract.Effect.NavigateToRegister -> onNavigateToRegister()
                is WelcomeContract.Effect.NavigateToHome -> onNavigateToHome()
                is WelcomeContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is WelcomeContract.Effect.ShowSuccess -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = YoinSpacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(YoinSpacing.xxxl))

            // ロゴとタイトルセクション
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                // ロゴアイコン
                Box(
                    modifier = Modifier
                        .size(YoinSizes.logoLarge)
                        .background(YoinColors.Primary, RoundedCornerShape(YoinSpacing.xxl)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CameraRoll,
                        contentDescription = "Yoin Logo",
                        tint = Color.White,
                        modifier = Modifier.size(YoinSizes.iconXLarge)
                    )
                }

                Spacer(modifier = Modifier.height(YoinSpacing.xl))

                // タイトル
                Text(
                    text = "Yoin.",
                    fontSize = YoinFontSizes.displayLarge.value.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(YoinSpacing.md))

                // サブタイトル
                Text(
                    text = "旅の余韻を楽しむ",
                    fontSize = YoinFontSizes.bodyLarge.value.sp,
                    color = YoinColors.TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            // ボタンセクション
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(YoinSpacing.md)
            ) {
                // Googleでログイン（ゴールドアクセント）
                OutlinedButton(
                    onClick = { viewModel.handleIntent(WelcomeContract.Intent.OnGoogleSignInPressed) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(YoinSizes.buttonHeightMedium),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = YoinColors.TextPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, YoinColors.Primary),
                    shape = RoundedCornerShape(YoinSpacing.md),
                    enabled = !state.isLoading
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(YoinSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(YoinSizes.iconSmall)
                                .background(Color(0xFFEA4335), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "G",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "Googleでログイン",
                            fontSize = YoinFontSizes.bodySmall.value.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Appleでログイン
                Button(
                    onClick = { viewModel.handleIntent(WelcomeContract.Intent.OnAppleSignInPressed) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(YoinSizes.buttonHeightMedium),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    ),
                    shape = RoundedCornerShape(YoinSpacing.md),
                    enabled = !state.isLoading
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(YoinSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🍎",
                            fontSize = YoinSizes.iconSmall.value.sp
                        )
                        Text(
                            text = "Appleでログイン",
                            fontSize = YoinFontSizes.bodySmall.value.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }

                // メールでログイン
                Button(
                    onClick = { viewModel.handleIntent(WelcomeContract.Intent.OnEmailLoginPressed) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(YoinSizes.buttonHeightMedium),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YoinColors.Primary
                    ),
                    shape = RoundedCornerShape(YoinSpacing.md),
                    enabled = !state.isLoading
                ) {
                    Text(
                        text = "メールでログイン",
                        fontSize = YoinFontSizes.bodyMedium.value.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(YoinSpacing.sm))

                // 区切り線（ゴールドアクセント）
                HorizontalDivider(color = YoinColors.Primary)

                Spacer(modifier = Modifier.height(YoinSpacing.sm))

                // 新規登録ボタン
                OutlinedButton(
                    onClick = { viewModel.handleIntent(WelcomeContract.Intent.OnRegisterPressed) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(YoinSizes.buttonHeightMedium),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = YoinColors.Primary
                    ),
                    border = androidx.compose.foundation.BorderStroke(2.dp, YoinColors.Primary),
                    shape = RoundedCornerShape(YoinSpacing.md),
                    enabled = !state.isLoading
                ) {
                    Text(
                        text = "新規登録",
                        fontSize = YoinFontSizes.bodyMedium.value.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // ゲストで始める
                TextButton(
                    onClick = { viewModel.handleIntent(WelcomeContract.Intent.OnGuestPressed) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                ) {
                    Text(
                        text = "ゲストで始める",
                        fontSize = YoinFontSizes.labelLarge.value.sp,
                        fontWeight = FontWeight.Medium,
                        color = YoinColors.TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(YoinSpacing.xl))
        }

        // スナックバー
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // ローディング
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = YoinColors.Primary)
            }
        }
    }
}

/**
 * プレビュー
 */
@PhonePreview
@Composable
private fun WelcomeScreenPreview() {
    MaterialTheme {
        WelcomeScreen(
            viewModel = WelcomeViewModel(),
            onNavigateToEmailLogin = {},
            onNavigateToRegister = {},
            onNavigateToHome = {}
        )
    }
}
