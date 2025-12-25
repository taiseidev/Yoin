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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.feature.auth.viewmodel.PasswordResetContract
import com.yoin.feature.auth.viewmodel.PasswordResetViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * パスワードリセット画面
 *
 * 機能:
 * - メールアドレス入力
 * - パスワードリセットリンク送信
 * - ログイン画面への戻る
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
        modifier = Modifier.fillMaxSize().background(YoinColors.Surface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally
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

            Spacer(modifier = Modifier.height(32.dp))

            // ナビゲーションバー
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                // 戻るボタン
                Text(
                    text = "←",
                    fontSize = 20.sp,
                    color = YoinColors.TextPrimary,
                    modifier = Modifier.clickable {
                        viewModel.handleIntent(PasswordResetContract.Intent.OnBackToLoginPressed)
                    })

                Spacer(modifier = Modifier.width(16.dp))

                // タイトル
                Text(
                    text = "パスワードをリセット",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )


                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider(
                    color = YoinColors.SurfaceVariant, thickness = 0.65.dp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 鍵アイコン
                Box(
                    modifier = Modifier.size(120.dp)
                        .background(YoinColors.AccentPeach, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔑", fontSize = 40.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // タイトル
                Text(
                    text = "パスワードをお忘れですか？",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 説明文1
                Text(
                    text = "登録したメールアドレスを入力してください。",
                    fontSize = 14.sp,
                    color = YoinColors.TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 説明文2
                Text(
                    text = "パスワード再設定用のリンクをお送りします。",
                    fontSize = 14.sp,
                    color = YoinColors.TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                // メールアドレス入力
                EmailField(
                    email = state.email,
                    error = state.emailError,
                    enabled = !state.isLoading && !state.isEmailSent,
                    onEmailChanged = { email ->
                        viewModel.handleIntent(PasswordResetContract.Intent.OnEmailChanged(email))
                    })

                Spacer(modifier = Modifier.height(32.dp))

                // リセットリンク送信ボタン
                Button(
                    onClick = {
                        viewModel.handleIntent(PasswordResetContract.Intent.OnSendResetLinkPressed)
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YoinColors.Primary,
                        disabledContainerColor = YoinColors.TextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !state.isLoading && !state.isEmailSent
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp), color = YoinColors.Surface
                        )
                    } else {
                        Text(
                            text = if (state.isEmailSent) "送信完了" else "リセットリンクを送信",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = YoinColors.Surface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ログインに戻るリンク
                Text(
                    text = "← ログインに戻る",
                    fontSize = 14.sp,
                    color = YoinColors.Primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.clickable {
                        viewModel.handleIntent(PasswordResetContract.Intent.OnBackToLoginPressed)
                    })

                Spacer(modifier = Modifier.weight(1f))

                // ホームインジケーター
                Box(
                    modifier = Modifier.width(134.dp).height(5.dp)
                        .background(Color.Black, RoundedCornerShape(100.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // スナックバー
            SnackbarHost(
                hostState = snackbarHostState,
//                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    /**
     * メールアドレス入力フィールド
     */
    @Composable
    fun EmailField(
        email: String, error: String?, enabled: Boolean, onEmailChanged: (String) -> Unit
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "メールアドレス", fontSize = 12.sp, color = YoinColors.TextSecondary
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
                        text = "📧", fontSize = 16.sp
                    )
                },
                isError = error != null,
                enabled = enabled,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (error != null) YoinColors.Error else YoinColors.Primary,
                    unfocusedBorderColor = if (error != null) YoinColors.Error else YoinColors.SurfaceVariant,
                    errorBorderColor = YoinColors.Error,
                    disabledBorderColor = YoinColors.SurfaceVariant
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )

            if (error != null) {
                Text(
                    text = error, fontSize = 12.sp, color = YoinColors.Error
                )
            }
        }
    }
}