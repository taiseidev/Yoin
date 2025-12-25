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
import com.yoin.core.design.theme.YoinFontSizes
import com.yoin.core.design.theme.YoinSizes
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.ui.preview.PhonePreview
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
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = YoinSpacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ステータスバー風の時刻表示
            Text(
                text = "9:41",
                fontSize = YoinFontSizes.labelLarge.value.sp,
                fontWeight = FontWeight.Medium,
                color = YoinColors.TextPrimary,
                modifier = Modifier.padding(top = YoinSpacing.xxl)
            )

            Spacer(modifier = Modifier.height(YoinSpacing.xxxl))

            // ナビゲーションバー
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 戻るボタン
                Text(
                    text = "←",
                    fontSize = YoinFontSizes.headingMedium.value.sp,
                    color = YoinColors.TextPrimary,
                    modifier = Modifier.clickable {
                        viewModel.handleIntent(PasswordResetContract.Intent.OnBackToLoginPressed)
                    }
                )

                Spacer(modifier = Modifier.width(YoinSpacing.lg))

                // タイトル
                Text(
                    text = "パスワードをリセット",
                    fontSize = YoinFontSizes.headingSmall.value.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(YoinSpacing.xxl))

            HorizontalDivider(
                color = YoinColors.AccentPeach
            )

            Spacer(modifier = Modifier.height(YoinSpacing.xxxl))

            // 鍵アイコン
            Box(
                modifier = Modifier
                    .size(YoinSizes.logoLarge)
                    .background(YoinColors.AccentPeach, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🔑",
                    fontSize = YoinSizes.iconXLarge.value.sp
                )
            }

            Spacer(modifier = Modifier.height(YoinSpacing.xxxl))

            // タイトル
            Text(
                text = "パスワードをお忘れですか？",
                fontSize = YoinFontSizes.bodyLarge.value.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(YoinSpacing.lg))

            // 説明文1
            Text(
                text = "登録したメールアドレスを入力してください。",
                fontSize = YoinFontSizes.bodyMedium.value.sp,
                color = YoinColors.TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(YoinSpacing.sm))

            // 説明文2
            Text(
                text = "パスワード再設定用のリンクをお送りします。",
                fontSize = YoinFontSizes.bodyMedium.value.sp,
                color = YoinColors.TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(YoinSpacing.huge))

            // メールアドレス入力
            PasswordResetEmailField(
                email = state.email,
                error = state.emailError,
                enabled = !state.isLoading && !state.isEmailSent,
                onEmailChanged = {
                    viewModel.handleIntent(PasswordResetContract.Intent.OnEmailChanged(it))
                }
            )

            Spacer(modifier = Modifier.height(YoinSpacing.xxxl))

            // リセットリンク送信ボタン
            Button(
                onClick = {
                    viewModel.handleIntent(PasswordResetContract.Intent.OnSendResetLinkPressed)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(YoinSizes.buttonHeightLarge),
                colors = ButtonDefaults.buttonColors(
                    containerColor = YoinColors.Primary,
                    disabledContainerColor = YoinColors.TextSecondary
                ),
                shape = RoundedCornerShape(YoinSpacing.lg),
                enabled = !state.isLoading && !state.isEmailSent
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(YoinSizes.iconMedium),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = if (state.isEmailSent) "送信完了" else "リセットリンクを送信",
                        fontSize = YoinFontSizes.bodyLarge.value.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(YoinSpacing.xxl))

            // ログインに戻るリンク
            Text(
                text = "← ログインに戻る",
                fontSize = YoinFontSizes.labelLarge.value.sp,
                color = YoinColors.Primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable {
                    viewModel.handleIntent(PasswordResetContract.Intent.OnBackToLoginPressed)
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            // ホームインジケーター
            Box(
                modifier = Modifier
                    .width(134.dp)
                    .height(5.dp)
                    .background(Color.Black, RoundedCornerShape(100.dp))
            )

            Spacer(modifier = Modifier.height(YoinSpacing.lg))
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
private fun PasswordResetEmailField(
    email: String,
    error: String?,
    enabled: Boolean,
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
                Text(
                    text = "📧",
                    fontSize = 18.sp
                )
            },
            isError = error != null,
            enabled = enabled,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (error != null) YoinColors.Error else YoinColors.Primary,
                unfocusedBorderColor = if (error != null) YoinColors.Error else YoinColors.AccentPeach,
                errorBorderColor = YoinColors.Error,
                disabledBorderColor = YoinColors.AccentPeach,
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
private fun PasswordResetScreenPreview() {
    PasswordResetScreen(
        viewModel = PasswordResetViewModel(),
        onNavigateToLogin = {}
    )
}