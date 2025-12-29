package com.yoin.feature.auth.ui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraRoll
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.yoin.core.design.theme.YoinFontSizes
import com.yoin.core.design.theme.YoinSizes
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.auth.viewmodel.LoginContract
import com.yoin.feature.auth.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * メールログイン画面（簡略版）
 *
 * 機能:
 * - メール/パスワードログイン
 * - パスワードリセットへの遷移
 *
 * @param viewModel LoginViewModel
 * @param onNavigateToHome ホーム画面への遷移コールバック
 * @param onNavigateToPasswordReset パスワードリセット画面への遷移コールバック
 * @param onNavigateBack 前画面への遷移コールバック
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToPasswordReset: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is LoginContract.Effect.NavigateToHome -> onNavigateToHome()
                is LoginContract.Effect.NavigateToRegister -> onNavigateBack()
                is LoginContract.Effect.NavigateToForgotPassword -> onNavigateToPasswordReset()
                is LoginContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is LoginContract.Effect.ShowSuccess -> {
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
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(YoinSpacing.xxxl))

            // ロゴアイコン（コーラル背景 + フィルムアイコン）
            Box(
                modifier = Modifier
                    .size(YoinSizes.logoMedium)
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

            Spacer(modifier = Modifier.height(28.dp))

            // タイトル
            Text(
                text = "Yoin.",
                fontSize = YoinFontSizes.displayMedium.value.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(YoinSpacing.sm))

            // サブタイトル
            Text(
                text = "旅の余韻を楽しむ",
                fontSize = YoinFontSizes.bodyMedium.value.sp,
                fontWeight = FontWeight.Normal,
                color = YoinColors.TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(YoinSpacing.huge))

            // メールアドレス入力
            EmailField(
                email = state.email,
                error = state.emailError,
                onEmailChanged = { email ->
                    viewModel.handleIntent(LoginContract.Intent.OnEmailChanged(email))
                }
            )

            Spacer(modifier = Modifier.height(YoinSpacing.lg))

            // パスワード入力
            PasswordField(
                password = state.password,
                error = state.passwordError,
                isPasswordVisible = state.isPasswordVisible,
                onPasswordChanged = { password ->
                    viewModel.handleIntent(LoginContract.Intent.OnPasswordChanged(password))
                },
                onPasswordVisibilityToggled = {
                    viewModel.handleIntent(LoginContract.Intent.OnPasswordVisibilityToggled)
                }
            )

            Spacer(modifier = Modifier.height(YoinSpacing.sm))

            // パスワードを忘れた
            Text(
                text = "パスワードを忘れた",
                fontSize = YoinFontSizes.labelSmall.value.sp,
                color = YoinColors.Primary,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable {
                        viewModel.handleIntent(LoginContract.Intent.OnForgotPasswordPressed)
                    }
            )

            Spacer(modifier = Modifier.height(YoinSpacing.xxl))

            // ログインボタン
            Button(
                onClick = {
                    viewModel.handleIntent(LoginContract.Intent.OnLoginPressed)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(YoinSizes.buttonHeightLarge),
                colors = ButtonDefaults.buttonColors(
                    containerColor = YoinColors.Primary
                ),
                shape = RoundedCornerShape(YoinSpacing.lg),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(YoinSizes.iconMedium),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "ログイン",
                        fontSize = YoinFontSizes.bodyLarge.value.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
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
    }
}

/**
 * メールアドレス入力フィールド
 */
@Composable
fun EmailField(
    email: String,
    error: String?,
    enabled: Boolean = true,
    onEmailChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(YoinSpacing.sm)
    ) {
        Text(
            text = "メールアドレス",
            fontSize = YoinFontSizes.labelSmall.value.sp,
            color = YoinColors.TextSecondary
        )

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChanged,
            placeholder = {
                Text(
                    text = "email@example.com",
                    fontSize = YoinFontSizes.bodySmall.value.sp,
                    color = YoinColors.TextTertiary
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = "Email",
                    tint = YoinColors.Primary,
                    modifier = Modifier.size(YoinSizes.iconSmall)
                )
            },
            isError = error != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (error != null) YoinColors.Error else YoinColors.Primary,
                unfocusedBorderColor = if (error != null) YoinColors.Error else YoinColors.Primary,
                errorBorderColor = YoinColors.Error,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(YoinSpacing.md),
            modifier = Modifier.fillMaxWidth()
        )

        if (error != null) {
            Text(
                text = error,
                fontSize = YoinFontSizes.labelSmall.value.sp,
                color = YoinColors.Error
            )
        }
    }
}

/**
 * パスワード入力フィールド
 */
@Composable
private fun PasswordField(
    password: String,
    error: String?,
    isPasswordVisible: Boolean,
    onPasswordChanged: (String) -> Unit,
    onPasswordVisibilityToggled: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(YoinSpacing.sm)
    ) {
        Text(
            text = "パスワード",
            fontSize = YoinFontSizes.labelSmall.value.sp,
            color = YoinColors.TextSecondary
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChanged,
            placeholder = {
                Text(
                    text = "••••••••",
                    fontSize = YoinFontSizes.bodySmall.value.sp,
                    color = YoinColors.TextTertiary
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Password",
                    tint = YoinColors.Primary,
                    modifier = Modifier.size(YoinSizes.iconSmall)
                )
            },
            trailingIcon = {
                IconButton(onClick = onPasswordVisibilityToggled) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                        tint = YoinColors.TextSecondary,
                        modifier = Modifier.size(YoinSizes.iconSmall)
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            isError = error != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (error != null) YoinColors.Error else YoinColors.Primary,
                unfocusedBorderColor = if (error != null) YoinColors.Error else YoinColors.Primary,
                errorBorderColor = YoinColors.Error,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(YoinSpacing.md),
            modifier = Modifier.fillMaxWidth()
        )

        if (error != null) {
            Text(
                text = error,
                fontSize = YoinFontSizes.labelSmall.value.sp,
                color = YoinColors.Error
            )
        }
    }
}

/**
 * プレビュー
 */
@PhonePreview
@Composable
private fun LoginScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Surface)
        ) {
            // プレビュー用の簡易表示
            Text("Login Screen Preview")
        }
    }
}
