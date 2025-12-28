package com.yoin.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinFontSizes
import com.yoin.core.design.theme.YoinSizes
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.auth.viewmodel.RegisterPasswordContract
import com.yoin.feature.auth.viewmodel.RegisterPasswordViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * パスワード設定画面（新規登録の最終ステップ）
 *
 * @param viewModel RegisterPasswordViewModel
 * @param onNavigateToHome ホーム画面への遷移
 * @param onNavigateBack 前画面への遷移
 */
@Composable
fun RegisterPasswordScreen(
    viewModel: RegisterPasswordViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effect監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is RegisterPasswordContract.Effect.NavigateToHome -> onNavigateToHome()
                is RegisterPasswordContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is RegisterPasswordContract.Effect.ShowSuccess -> {
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

            // タイトルとフォーム
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "パスワードを設定",
                    fontSize = YoinFontSizes.displaySmall.value.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(YoinSpacing.md))

                Text(
                    text = "8文字以上で入力してください",
                    fontSize = YoinFontSizes.bodySmall.value.sp,
                    color = YoinColors.TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(YoinSpacing.xxxl))

                // パスワード入力
                OutlinedTextField(
                    value = state.password,
                    onValueChange = { viewModel.handleIntent(RegisterPasswordContract.Intent.OnPasswordChanged(it)) },
                    label = { Text("パスワード") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Password",
                            tint = YoinColors.Primary,
                            modifier = Modifier.size(YoinSizes.iconSmall)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.handleIntent(RegisterPasswordContract.Intent.OnPasswordVisibilityToggled) }) {
                            Icon(
                                imageVector = if (state.isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (state.isPasswordVisible) "Hide password" else "Show password",
                                tint = YoinColors.TextSecondary,
                                modifier = Modifier.size(YoinSizes.iconSmall)
                            )
                        }
                    },
                    visualTransformation = if (state.isPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    isError = state.passwordError != null,
                    supportingText = state.passwordError?.let { { Text(it, color = YoinColors.Error) } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(YoinSpacing.md),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = YoinColors.Primary,
                        unfocusedBorderColor = YoinColors.AccentPeach,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(YoinSpacing.lg))

                // パスワード確認入力
                OutlinedTextField(
                    value = state.confirmPassword,
                    onValueChange = { viewModel.handleIntent(RegisterPasswordContract.Intent.OnConfirmPasswordChanged(it)) },
                    label = { Text("パスワード（確認）") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Confirm Password",
                            tint = YoinColors.Primary,
                            modifier = Modifier.size(YoinSizes.iconSmall)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.handleIntent(RegisterPasswordContract.Intent.OnConfirmPasswordVisibilityToggled) }) {
                            Icon(
                                imageVector = if (state.isConfirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (state.isConfirmPasswordVisible) "Hide password" else "Show password",
                                tint = YoinColors.TextSecondary,
                                modifier = Modifier.size(YoinSizes.iconSmall)
                            )
                        }
                    },
                    visualTransformation = if (state.isConfirmPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    isError = state.confirmPasswordError != null,
                    supportingText = state.confirmPasswordError?.let { { Text(it, color = YoinColors.Error) } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(YoinSpacing.md),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = YoinColors.Primary,
                        unfocusedBorderColor = YoinColors.AccentPeach,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )
            }

            // 登録ボタン
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(YoinSpacing.md)
            ) {
                Button(
                    onClick = { viewModel.handleIntent(RegisterPasswordContract.Intent.OnRegisterPressed) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(YoinSizes.buttonHeightLarge),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YoinColors.Primary
                    ),
                    shape = RoundedCornerShape(YoinSpacing.md),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(YoinSizes.iconMedium),
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "登録する",
                            fontSize = YoinFontSizes.bodyMedium.value.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(YoinSpacing.xxxl))
        }

        // スナックバー
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * プレビュー
 */
@PhonePreview
@Composable
private fun RegisterPasswordScreenPreview() {
    MaterialTheme {
        RegisterPasswordScreen(
            viewModel = RegisterPasswordViewModel("山田太郎", "yamada@example.com"),
            onNavigateToHome = {},
            onNavigateBack = {}
        )
    }
}
