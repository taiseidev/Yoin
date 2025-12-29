package com.yoin.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.yoin.feature.auth.viewmodel.RegisterMethodContract
import com.yoin.feature.auth.viewmodel.RegisterMethodViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * 登録方法選択画面
 *
 * Google/Apple/メールでの登録方法を選択
 *
 * @param viewModel RegisterMethodViewModel
 * @param onNavigateToEmailRegister メール登録画面への遷移
 * @param onNavigateToHome ホーム画面への遷移
 * @param onNavigateBack 前画面への遷移
 */
@Composable
fun RegisterMethodScreen(
    viewModel: RegisterMethodViewModel,
    onNavigateToEmailRegister: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effect監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is RegisterMethodContract.Effect.NavigateToEmailRegister -> onNavigateToEmailRegister()
                is RegisterMethodContract.Effect.NavigateToHome -> onNavigateToHome()
                is RegisterMethodContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is RegisterMethodContract.Effect.ShowSuccess -> {
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
            // ヘッダー
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = YoinSpacing.xl),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = YoinColors.TextPrimary
                    )
                }
            }

            // タイトルとサブタイトル
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "新規登録",
                    fontSize = YoinFontSizes.displaySmall.value.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(YoinSpacing.md))

                Text(
                    text = "登録方法を選択してください",
                    fontSize = YoinFontSizes.bodyMedium.value.sp,
                    color = YoinColors.TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            // ボタンセクション
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(YoinSpacing.md)
            ) {
                // Googleで登録（ゴールドアクセント）
                OutlinedButton(
                    onClick = { viewModel.handleIntent(RegisterMethodContract.Intent.OnGoogleRegisterPressed) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(YoinSizes.buttonHeightLarge),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = YoinColors.TextPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, YoinColors.Primary),
                    shape = RoundedCornerShape(YoinSpacing.md),
                    enabled = !state.isLoading
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(YoinSpacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(YoinSizes.iconMedium)
                                .background(Color(0xFFEA4335), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "G",
                                fontSize = YoinFontSizes.labelLarge.value.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "Googleで登録",
                            fontSize = YoinFontSizes.bodyMedium.value.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Appleで登録
                Button(
                    onClick = { viewModel.handleIntent(RegisterMethodContract.Intent.OnAppleRegisterPressed) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(YoinSizes.buttonHeightLarge),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    ),
                    shape = RoundedCornerShape(YoinSpacing.md),
                    enabled = !state.isLoading
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(YoinSpacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🍎",
                            fontSize = YoinSizes.iconMedium.value.sp
                        )
                        Text(
                            text = "Appleで登録",
                            fontSize = YoinFontSizes.bodyMedium.value.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(YoinSpacing.sm))

                // 区切り線（ゴールドアクセント）
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(YoinSpacing.sm)
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = YoinColors.Primary
                    )
                    Text(
                        text = "または",
                        fontSize = YoinFontSizes.labelSmall.value.sp,
                        color = YoinColors.TextSecondary
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = YoinColors.Primary
                    )
                }

                Spacer(modifier = Modifier.height(YoinSpacing.sm))

                // メールで登録
                Button(
                    onClick = { viewModel.handleIntent(RegisterMethodContract.Intent.OnEmailRegisterPressed) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(YoinSizes.buttonHeightLarge),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YoinColors.Primary
                    ),
                    shape = RoundedCornerShape(YoinSpacing.md),
                    enabled = !state.isLoading
                ) {
                    Text(
                        text = "メールで登録",
                        fontSize = YoinFontSizes.bodyMedium.value.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(YoinSpacing.xxxl))
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
private fun RegisterMethodScreenPreview() {
    MaterialTheme {
        RegisterMethodScreen(
            viewModel = RegisterMethodViewModel(),
            onNavigateToEmailRegister = {},
            onNavigateToHome = {},
            onNavigateBack = {}
        )
    }
}
