package com.yoin.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.yoin.core.design.theme.YoinFontSizes
import com.yoin.core.design.theme.YoinSizes
import com.yoin.core.design.theme.YoinSpacing
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

            Spacer(modifier = Modifier.height(YoinSpacing.massive))

            // ロゴアイコン（緑の角丸四角背景 + フィルム絵文字）
            Box(
                modifier = Modifier
                    .size(YoinSizes.logoSmall)
                    .background(YoinColors.Primary, RoundedCornerShape(YoinSpacing.lg)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🎞️",
                    fontSize = YoinSizes.iconXLarge.value.sp
                )
            }

            Spacer(modifier = Modifier.height(YoinSpacing.xxl))

            // タイトル
            Text(
                text = "Yoin.",
                fontSize = YoinFontSizes.displayMedium.value.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(YoinSpacing.sm))

            // サブタイトル
            Text(
                text = "~ 旅の余韻を楽しむ ~",
                fontSize = YoinFontSizes.labelLarge.value.sp,
                color = YoinColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(YoinSpacing.huge))

            // 新規登録ヘッダー
            Text(
                text = "新規登録",
                fontSize = YoinFontSizes.headingMedium.value.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(YoinSpacing.xxl))

            // 名前入力
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onIntent(RegisterContract.Intent.OnNameChanged(it)) },
                label = { Text("名前") },
                leadingIcon = {
                    Text(
                        text = "👤",
                        fontSize = 18.sp
                    )
                },
                isError = state.nameError != null,
                supportingText = state.nameError?.let { { Text(it, color = YoinColors.Error) } },
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

            // メールアドレス入力
            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.onIntent(RegisterContract.Intent.OnEmailChanged(it)) },
                label = { Text("メールアドレス") },
                leadingIcon = {
                    Text(
                        text = "📧",
                        fontSize = 18.sp
                    )
                },
                isError = state.emailError != null,
                supportingText = state.emailError?.let { { Text(it, color = YoinColors.Error) } },
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

            // パスワード入力
            OutlinedTextField(
                value = state.password,
                onValueChange = { viewModel.onIntent(RegisterContract.Intent.OnPasswordChanged(it)) },
                label = { Text("パスワード") },
                leadingIcon = {
                    Text(
                        text = "🔒",
                        fontSize = 18.sp
                    )
                },
                trailingIcon = {
                    Text(
                        text = if (state.isPasswordVisible) "👁" else "👁",
                        fontSize = 18.sp,
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
                onValueChange = { viewModel.onIntent(RegisterContract.Intent.OnConfirmPasswordChanged(it)) },
                label = { Text("パスワード（確認）") },
                leadingIcon = {
                    Text(
                        text = "🔒",
                        fontSize = 18.sp
                    )
                },
                trailingIcon = {
                    Text(
                        text = if (state.isConfirmPasswordVisible) "👁" else "👁",
                        fontSize = 18.sp,
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
                shape = RoundedCornerShape(YoinSpacing.md),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = YoinColors.Primary,
                    unfocusedBorderColor = YoinColors.AccentPeach,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(YoinSpacing.xxl))

            // 登録ボタン
            Button(
                onClick = { viewModel.onIntent(RegisterContract.Intent.OnRegisterPressed) },
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
                        text = "登録する",
                        fontSize = YoinFontSizes.bodyLarge.value.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(YoinSpacing.xxl))

            // 区切り線と「または」
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(YoinSpacing.sm)
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = YoinColors.TextSecondary
                )
                Text(
                    text = "または",
                    fontSize = YoinFontSizes.labelSmall.value.sp,
                    color = YoinColors.TextSecondary
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = YoinColors.TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(YoinSpacing.xxl))

            // Googleで登録ボタン
            OutlinedButton(
                onClick = { viewModel.onIntent(RegisterContract.Intent.OnGoogleRegisterPressed) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(YoinSizes.buttonHeightLarge),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = YoinColors.TextPrimary
                ),
                shape = RoundedCornerShape(YoinSpacing.lg),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.5.dp,
                    color = YoinColors.AccentPeach
                ),
                enabled = !state.isLoading
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(YoinSpacing.md)
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
                        fontSize = YoinFontSizes.bodySmall.value.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(YoinSpacing.lg))

            // Appleで登録ボタン
            Button(
                onClick = { viewModel.onIntent(RegisterContract.Intent.OnAppleRegisterPressed) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(YoinSizes.buttonHeightLarge),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),
                shape = RoundedCornerShape(YoinSpacing.lg),
                enabled = !state.isLoading
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(YoinSpacing.md)
                ) {
                    Text(
                        text = "🍎",
                        fontSize = YoinSizes.iconSmall.value.sp
                    )
                    Text(
                        text = "Appleで登録",
                        fontSize = YoinFontSizes.bodySmall.value.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(YoinSpacing.xxxl))

            // ログインリンク
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "アカウントをお持ちですか？",
                    fontSize = YoinFontSizes.labelLarge.value.sp,
                    color = YoinColors.TextSecondary
                )
                Spacer(modifier = Modifier.width(YoinSpacing.sm))
                Text(
                    text = "ログイン",
                    fontSize = YoinFontSizes.labelLarge.value.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.Primary,
                    modifier = Modifier.clickable {
                        viewModel.onIntent(RegisterContract.Intent.OnLoginPressed)
                    }
                )
            }

            Spacer(modifier = Modifier.height(YoinSpacing.massive))
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
