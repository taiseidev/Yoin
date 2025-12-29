package com.yoin.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraRoll
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.yoin.feature.auth.viewmodel.LoginContract
import com.yoin.feature.auth.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * メールログイン画面 - Modern Cinematic Design
 *
 * デザインコンセプト:
 * - 黒背景 + グラデーション
 * - 透明度のあるテキストフィールド
 * - グラデーションボタン
 * - シネマティックでスタイリッシュなUI
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
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black,
                        YoinColors.Primary.copy(alpha = 0.15f),
                        Color.Black
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // ナビゲーションバー
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ロゴアイコン（グラデーション背景）
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                YoinColors.Primary,
                                YoinColors.PrimaryVariant
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // ラジアルグラデーションオーバーレイ
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                )

                Icon(
                    imageVector = Icons.Filled.CameraRoll,
                    contentDescription = "Yoin Logo",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // タイトル
            Text(
                text = "おかえりなさい",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // サブタイトル
            Text(
                text = "余韻の旅を続けましょう",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                fontStyle = FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(48.dp))

            // メールアドレス入力
            CinematicEmailField(
                email = state.email,
                error = state.emailError,
                onEmailChanged = { email ->
                    viewModel.handleIntent(LoginContract.Intent.OnEmailChanged(email))
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // パスワード入力
            CinematicPasswordField(
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

            Spacer(modifier = Modifier.height(16.dp))

            // パスワードを忘れた
            Text(
                text = "パスワードをお忘れですか？",
                fontSize = 14.sp,
                color = YoinColors.Primary,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable {
                        viewModel.handleIntent(LoginContract.Intent.OnForgotPasswordPressed)
                    },
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ログインボタン（グラデーション）
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                YoinColors.Primary,
                                YoinColors.PrimaryVariant
                            )
                        )
                    )
            ) {
                Button(
                    onClick = {
                        viewModel.handleIntent(LoginContract.Intent.OnLoginPressed)
                    },
                    modifier = Modifier.fillMaxSize(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(28.dp),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "ログイン",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        // スナックバー
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * シネマティックなメールアドレス入力フィールド
 */
@Composable
private fun CinematicEmailField(
    email: String,
    error: String?,
    enabled: Boolean = true,
    onEmailChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "メールアドレス",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.9f),
            fontWeight = FontWeight.Medium
        )

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChanged,
            placeholder = {
                Text(
                    text = "email@example.com",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = "Email",
                    tint = YoinColors.Primary,
                    modifier = Modifier.size(22.dp)
                )
            },
            isError = error != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (error != null) YoinColors.Error else YoinColors.Primary,
                unfocusedBorderColor = if (error != null) YoinColors.Error else Color.White.copy(alpha = 0.3f),
                errorBorderColor = YoinColors.Error,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = YoinColors.Primary,
                unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                focusedContainerColor = Color.White.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (error != null) {
            Text(
                text = error,
                fontSize = 13.sp,
                color = YoinColors.Error,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * シネマティックなパスワード入力フィールド
 */
@Composable
private fun CinematicPasswordField(
    password: String,
    error: String?,
    isPasswordVisible: Boolean,
    onPasswordChanged: (String) -> Unit,
    onPasswordVisibilityToggled: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "パスワード",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.9f),
            fontWeight = FontWeight.Medium
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChanged,
            placeholder = {
                Text(
                    text = "••••••••",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Password",
                    tint = YoinColors.Primary,
                    modifier = Modifier.size(22.dp)
                )
            },
            trailingIcon = {
                IconButton(onClick = onPasswordVisibilityToggled) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(22.dp)
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
                unfocusedBorderColor = if (error != null) YoinColors.Error else Color.White.copy(alpha = 0.3f),
                errorBorderColor = YoinColors.Error,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = YoinColors.Primary,
                unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                focusedContainerColor = Color.White.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (error != null) {
            Text(
                text = error,
                fontSize = 13.sp,
                color = YoinColors.Error,
                fontWeight = FontWeight.Medium
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
                .background(Color.Black)
        ) {
            Text("Login Screen Preview", color = Color.White)
        }
    }
}
