package com.yoin.feature.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.ui.component.YoinAppBar
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.settings.viewmodel.ChangePasswordContract
import com.yoin.feature.settings.viewmodel.ChangePasswordViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * パスワード変更画面
 *
 * 機能:
 * - 現在のパスワード入力
 * - 新しいパスワード入力
 * - 確認用パスワード入力
 * - パスワードバリデーション
 * - パスワード表示/非表示切り替え
 *
 * @param viewModel ChangePasswordViewModel
 * @param onNavigateBack 戻るボタンのコールバック
 */
@Composable
fun ChangePasswordScreen(
    viewModel: ChangePasswordViewModel,
    onNavigateBack: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ChangePasswordContract.Effect.NavigateBack -> onNavigateBack()
                is ChangePasswordContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is ChangePasswordContract.Effect.ShowSuccess -> {
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
                title = "パスワード変更",
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.handleIntent(ChangePasswordContract.Intent.OnBackPressed) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = YoinColors.TextPrimary
                        )
                    }
                }
            )

            // フォームコンテンツ
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // 説明文
                Text(
                    text = "新しいパスワードは8文字以上で、英字と数字を含める必要があります。",
                    fontSize = 14.sp,
                    color = YoinColors.TextSecondary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 現在のパスワード
                PasswordInputField(
                    label = "現在のパスワード",
                    value = state.currentPassword,
                    onValueChange = {
                        viewModel.handleIntent(ChangePasswordContract.Intent.OnCurrentPasswordChanged(it))
                    },
                    errorMessage = state.validationErrors.currentPasswordError,
                    enabled = !state.isLoading
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 新しいパスワード
                PasswordInputField(
                    label = "新しいパスワード",
                    value = state.newPassword,
                    onValueChange = {
                        viewModel.handleIntent(ChangePasswordContract.Intent.OnNewPasswordChanged(it))
                    },
                    errorMessage = state.validationErrors.newPasswordError,
                    enabled = !state.isLoading
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 確認用パスワード
                PasswordInputField(
                    label = "新しいパスワード（確認）",
                    value = state.confirmPassword,
                    onValueChange = {
                        viewModel.handleIntent(ChangePasswordContract.Intent.OnConfirmPasswordChanged(it))
                    },
                    errorMessage = state.validationErrors.confirmPasswordError,
                    enabled = !state.isLoading
                )

                Spacer(modifier = Modifier.height(40.dp))

                // 変更ボタン
                Button(
                    onClick = {
                        viewModel.handleIntent(ChangePasswordContract.Intent.OnChangePasswordPressed)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YoinColors.Primary,
                        contentColor = YoinColors.OnPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = YoinColors.OnPrimary
                        )
                    } else {
                        Text(
                            text = "パスワードを変更",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
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
 * パスワード入力フィールド
 */
@Composable
private fun PasswordInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    errorMessage: String?,
    enabled: Boolean = true
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = YoinColors.TextPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = YoinColors.TextSecondary
                    )
                }
            },
            isError = errorMessage != null,
            enabled = enabled,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = YoinColors.SurfaceVariant,
                focusedBorderColor = YoinColors.Primary,
                errorBorderColor = MaterialTheme.colorScheme.error
            )
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp, start = 16.dp)
            )
        }
    }
}

/**
 * プレビュー
 */
@PhonePreview
@Composable
private fun ChangePasswordScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            Text("Change Password Screen Preview")
        }
    }
}
