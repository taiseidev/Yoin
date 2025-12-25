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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.auth.viewmodel.LoginContract
import com.yoin.feature.auth.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * ログイン画面
 *
 * 機能:
 * - メール/パスワードログイン
 * - Google/Appleソーシャルログイン
 * - 新規登録への遷移
 * - ゲストログイン
 *
 * @param viewModel LoginViewModel
 * @param onNavigateToHome ホーム画面への遷移コールバック
 * @param onNavigateToRegister 新規登録画面への遷移コールバック
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit = {},
    onNavigateToPasswordReset: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is LoginContract.Effect.NavigateToHome -> onNavigateToHome()
                is LoginContract.Effect.NavigateToRegister -> onNavigateToRegister()
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
                    text = "🎞",
                    fontSize = 40.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // タイトル
            Text(
                text = "Yoin.",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.Primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // サブタイトル
            Text(
                text = "~ 旅の余韻を楽しむ ~",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.Primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // メールアドレス入力
            EmailField(
                email = state.email,
                error = state.emailError,
                onEmailChanged = { email ->
                    viewModel.handleIntent(LoginContract.Intent.OnEmailChanged(email))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(8.dp))

            // パスワードを忘れた
            Text(
                text = "パスワードを忘れた",
                fontSize = 12.sp,
                color = YoinColors.Primary,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable {
                        viewModel.handleIntent(LoginContract.Intent.OnForgotPasswordPressed)
                    }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ログインボタン
            Button(
                onClick = {
                    viewModel.handleIntent(LoginContract.Intent.OnLoginPressed)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = YoinColors.Primary
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
                        text = "ログイン",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.OnPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // または区切り線
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = YoinColors.TextSecondary
                )
                Text(
                    text = "または",
                    fontSize = 12.sp,
                    color = YoinColors.TextSecondary
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = YoinColors.TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Googleでログインボタン
            OutlinedButton(
                onClick = {
                    viewModel.handleIntent(LoginContract.Intent.SignInWithGoogle)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = YoinColors.Surface,
                    contentColor = YoinColors.TextPrimary
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, YoinColors.SurfaceVariant),
                shape = RoundedCornerShape(12.dp),
                enabled = !state.isLoading
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Googleアイコン（簡易実装）
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color(0xFFEA4335), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "G",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "Googleでログイン",
                        fontSize = 14.sp,
                        color = YoinColors.TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Appleでログインボタン
            Button(
                onClick = {
                    viewModel.handleIntent(LoginContract.Intent.SignInWithApple)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !state.isLoading
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🍎",
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Appleでログイン",
                        fontSize = 14.sp,
                        color = YoinColors.OnPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 新規登録セクション
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(color = YoinColors.SurfaceVariant)

                Text(
                    text = "アカウントをお持ちでない方",
                    fontSize = 13.sp,
                    color = YoinColors.TextSecondary,
                    textAlign = TextAlign.Center
                )

                OutlinedButton(
                    onClick = {
                        viewModel.handleIntent(LoginContract.Intent.OnRegisterPressed)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = YoinColors.Primary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, YoinColors.Primary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !state.isLoading
                ) {
                    Text(
                        text = "新規登録",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = {
                        viewModel.handleIntent(LoginContract.Intent.SignInAsGuest)
                    },
                    enabled = !state.isLoading
                ) {
                    Text(
                        text = "ゲストで始める",
                        fontSize = 13.sp,
                        color = YoinColors.TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ホームインジケーター
            Box(
                modifier = Modifier
                    .width(134.dp)
                    .height(5.dp)
                    .background(Color.Black, RoundedCornerShape(100.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))
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
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "メールアドレス",
            fontSize = 12.sp,
            color = YoinColors.TextSecondary
        )

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChanged,
            placeholder = {
                Text(
                    text = "email@example.com",
                    fontSize = 14.sp,
                    color = YoinColors.TextSecondary
                )
            },
            leadingIcon = {
                Text(
                    text = "📧",
                    fontSize = 16.sp
                )
            },
            isError = error != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (error != null) YoinColors.Error else YoinColors.Primary,
                unfocusedBorderColor = if (error != null) YoinColors.Error else YoinColors.SurfaceVariant,
                errorBorderColor = YoinColors.Error
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )

        if (error != null) {
            Text(
                text = error,
                fontSize = 12.sp,
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
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "パスワード",
            fontSize = 12.sp,
            color = YoinColors.TextSecondary
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChanged,
            placeholder = {
                Text(
                    text = "••••••••",
                    fontSize = 14.sp,
                    color = YoinColors.TextSecondary
                )
            },
            leadingIcon = {
                Text(
                    text = "🔒",
                    fontSize = 16.sp
                )
            },
            trailingIcon = {
                Text(
                    text = if (isPasswordVisible) "👁" else "👁",
                    fontSize = 16.sp,
                    color = YoinColors.TextSecondary,
                    modifier = Modifier.clickable(onClick = onPasswordVisibilityToggled)
                )
            },
            visualTransformation = if (isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            isError = error != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (error != null) YoinColors.Error else YoinColors.Primary,
                unfocusedBorderColor = if (error != null) YoinColors.Error else YoinColors.SurfaceVariant,
                errorBorderColor = YoinColors.Error
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )

        if (error != null) {
            Text(
                text = error,
                fontSize = 12.sp,
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
