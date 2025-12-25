package com.yoin.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.auth.viewmodel.RegisterContract
import com.yoin.feature.auth.viewmodel.RegisterViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * 新規登録画面
 *
 * 機能:
 * - メール/パスワード新規登録
 * - Google/Appleソーシャル登録
 * - ログイン画面への遷移
 *
 * @param viewModel RegisterViewModel
 * @param onNavigateToHome ホーム画面への遷移コールバック
 * @param onNavigateToLogin ログイン画面への遷移コールバック
 */
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is RegisterContract.Effect.NavigateToHome -> onNavigateToHome()
                is RegisterContract.Effect.NavigateToLogin -> onNavigateToLogin()
                is RegisterContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is RegisterContract.Effect.ShowSuccess -> {
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
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ステータスバー風の時刻表示
            Text(
                text = "9:41",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = YoinColors.TextPrimary,
                letterSpacing = (-0.15).sp,
                modifier = Modifier.padding(top = 24.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ロゴアイコン（緑の角丸四角背景 + フィルム絵文字）
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(YoinColors.Primary, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🎞️",
                    fontSize = 40.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // タイトル
            Text(
                text = "Yoin.",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // サブタイトル
            Text(
                text = "~ 旅の余韻を楽しむ ~",
                fontSize = 14.sp,
                color = YoinColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 新規登録ヘッダー
            Text(
                text = "新規登録",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 名前入力
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onIntent(RegisterContract.Intent.OnNameChanged(it)) },
                label = { Text("名前") },
                leadingIcon = {
                    Text(
                        text = "👤",
                        fontSize = 20.sp
                    )
                },
                isError = state.nameError != null,
                supportingText = state.nameError?.let { { Text(it, color = YoinColors.Error) } },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = YoinColors.Primary,
                    unfocusedBorderColor = YoinColors.SurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // メールアドレス入力
            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.onIntent(RegisterContract.Intent.OnEmailChanged(it)) },
                label = { Text("メールアドレス") },
                leadingIcon = {
                    Text(
                        text = "📧",
                        fontSize = 20.sp
                    )
                },
                isError = state.emailError != null,
                supportingText = state.emailError?.let { { Text(it, color = YoinColors.Error) } },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = YoinColors.Primary,
                    unfocusedBorderColor = YoinColors.SurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // パスワード入力
            OutlinedTextField(
                value = state.password,
                onValueChange = { viewModel.onIntent(RegisterContract.Intent.OnPasswordChanged(it)) },
                label = { Text("パスワード") },
                leadingIcon = {
                    Text(
                        text = "🔒",
                        fontSize = 20.sp
                    )
                },
                trailingIcon = {
                    Text(
                        text = if (state.isPasswordVisible) "👁️" else "👁️‍🗨️",
                        fontSize = 20.sp,
                        modifier = Modifier.clickable {
                            viewModel.onIntent(RegisterContract.Intent.OnPasswordVisibilityToggled)
                        }
                    )
                },
                visualTransformation = if (state.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                isError = state.passwordError != null,
                supportingText = state.passwordError?.let { { Text(it, color = YoinColors.Error) } },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = YoinColors.Primary,
                    unfocusedBorderColor = YoinColors.SurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // パスワード確認入力
            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = { viewModel.onIntent(RegisterContract.Intent.OnConfirmPasswordChanged(it)) },
                label = { Text("パスワード（確認）") },
                leadingIcon = {
                    Text(
                        text = "🔒",
                        fontSize = 20.sp
                    )
                },
                trailingIcon = {
                    Text(
                        text = if (state.isConfirmPasswordVisible) "👁️" else "👁️‍🗨️",
                        fontSize = 20.sp,
                        modifier = Modifier.clickable {
                            viewModel.onIntent(RegisterContract.Intent.OnConfirmPasswordVisibilityToggled)
                        }
                    )
                },
                visualTransformation = if (state.isConfirmPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                isError = state.confirmPasswordError != null,
                supportingText = state.confirmPasswordError?.let { { Text(it, color = YoinColors.Error) } },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = YoinColors.Primary,
                    unfocusedBorderColor = YoinColors.SurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 登録ボタン
            Button(
                onClick = { viewModel.onIntent(RegisterContract.Intent.OnRegisterPressed) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = YoinColors.Primary,
                    contentColor = YoinColors.Surface
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = YoinColors.Surface
                    )
                } else {
                    Text(
                        text = "登録する",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 区切り線と「または」
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = YoinColors.SurfaceVariant,
                    thickness = 0.65.dp
                )
                Text(
                    text = "または",
                    fontSize = 14.sp,
                    color = YoinColors.TextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = YoinColors.SurfaceVariant,
                    thickness = 0.65.dp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Googleで登録ボタン
            OutlinedButton(
                onClick = { viewModel.onIntent(RegisterContract.Intent.OnGoogleRegisterPressed) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = YoinColors.Surface,
                    contentColor = YoinColors.TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = YoinColors.SurfaceVariant
                ),
                enabled = !state.isLoading
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🔴",
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Googleで登録",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Appleで登録ボタン
            Button(
                onClick = { viewModel.onIntent(RegisterContract.Intent.OnAppleRegisterPressed) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = YoinColors.TextPrimary,
                    contentColor = YoinColors.Surface
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !state.isLoading
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🍎",
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Appleで登録",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ログインリンク
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "アカウントをお持ちですか？",
                    fontSize = 14.sp,
                    color = YoinColors.TextSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ログイン",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.Primary,
                    modifier = Modifier.clickable {
                        viewModel.onIntent(RegisterContract.Intent.OnLoginPressed)
                    }
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
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
private fun RegisterScreenPreview() {
    RegisterScreen(
        viewModel = RegisterViewModel(),
        onNavigateToHome = {},
        onNavigateToLogin = {}
    )
}
