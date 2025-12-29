package com.yoin.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.auth.viewmodel.PasswordResetContract
import com.yoin.feature.auth.viewmodel.PasswordResetViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * パスワードリセット画面 - Modern Cinematic Design
 *
 * デザインコンセプト:
 * - 黒背景 + グラデーション
 * - 透明度のあるテキストフィールド
 * - グラデーションボタン
 * - シネマティックでスタイリッシュなUI
 *
 * @param viewModel PasswordResetViewModel
 * @param onNavigateToLogin ログイン画面への遷移コールバック
 */
@Composable
fun PasswordResetScreen(
    viewModel: PasswordResetViewModel,
    onNavigateToLogin: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is PasswordResetContract.Effect.NavigateToLogin -> onNavigateToLogin()
                is PasswordResetContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is PasswordResetContract.Effect.ShowSuccess -> {
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
                IconButton(
                    onClick = {
                        viewModel.handleIntent(PasswordResetContract.Intent.OnBackToLoginPressed)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // 鍵アイコン（グラデーション背景）
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
                    imageVector = Icons.Filled.VpnKey,
                    contentDescription = "Password Reset",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // タイトル
            Text(
                text = "パスワードをリセット",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 説明文
            Text(
                text = "登録したメールアドレスを入力してください。\nパスワード再設定用のリンクをお送りします。",
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // メールアドレス入力
            CinematicPasswordResetEmailField(
                email = state.email,
                error = state.emailError,
                enabled = !state.isLoading && !state.isEmailSent,
                onEmailChanged = {
                    viewModel.handleIntent(PasswordResetContract.Intent.OnEmailChanged(it))
                }
            )

            Spacer(modifier = Modifier.height(40.dp))

            // リセットリンク送信ボタン（グラデーション）
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
                        viewModel.handleIntent(PasswordResetContract.Intent.OnSendResetLinkPressed)
                    },
                    modifier = Modifier.fillMaxSize(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(28.dp),
                    enabled = !state.isLoading && !state.isEmailSent
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (state.isEmailSent) "送信完了" else "リセットリンクを送信",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ログインに戻るリンク
            Text(
                text = "ログインに戻る",
                fontSize = 15.sp,
                color = YoinColors.Primary,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable {
                    viewModel.handleIntent(PasswordResetContract.Intent.OnBackToLoginPressed)
                }
            )

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
private fun CinematicPasswordResetEmailField(
    email: String,
    error: String?,
    enabled: Boolean,
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
            enabled = enabled,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (error != null) YoinColors.Error else YoinColors.Primary,
                unfocusedBorderColor = if (error != null) YoinColors.Error else Color.White.copy(alpha = 0.3f),
                errorBorderColor = YoinColors.Error,
                disabledBorderColor = Color.White.copy(alpha = 0.2f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                disabledTextColor = Color.White.copy(alpha = 0.6f),
                cursorColor = YoinColors.Primary,
                unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                focusedContainerColor = Color.White.copy(alpha = 0.1f),
                disabledContainerColor = Color.White.copy(alpha = 0.03f)
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
private fun PasswordResetScreenPreview() {
    PasswordResetScreen(
        viewModel = PasswordResetViewModel(),
        onNavigateToLogin = {}
    )
}
