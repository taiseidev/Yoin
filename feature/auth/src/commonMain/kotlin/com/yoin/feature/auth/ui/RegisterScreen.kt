package com.yoin.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinFontSizes
import com.yoin.core.design.theme.YoinSizes
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.domain.auth.model.User
import com.yoin.feature.auth.viewmodel.RegisterContract
import com.yoin.feature.auth.viewmodel.RegisterViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * 新規登録画面（基本情報入力）
 *
 * 機能:
 * - 名前とメールアドレスの入力
 * - パスワード設定画面への遷移
 *
 * @param viewModel RegisterViewModel
 * @param onNavigateToPasswordScreen パスワード設定画面への遷移コールバック
 * @param onNavigateBack 前画面への遷移コールバック
 */
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onNavigateToPasswordScreen: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is RegisterContract.Effect.NavigateToPasswordScreen -> onNavigateToPasswordScreen()
                is RegisterContract.Effect.NavigateToHome -> {}  // Not used in this simplified screen
                is RegisterContract.Effect.NavigateToLogin -> onNavigateBack()
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
                .padding(horizontal = YoinSpacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ヘッダー（戻るボタン）
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

            // タイトルとフォーム（中央配置）
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                // タイトル
                Text(
                    text = "基本情報入力",
                    fontSize = YoinFontSizes.displaySmall.value.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(YoinSpacing.md))

                // サブタイトル
                Text(
                    text = "お名前とメールアドレスを入力してください",
                    fontSize = YoinFontSizes.bodySmall.value.sp,
                    color = YoinColors.TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(YoinSpacing.xxxl))

            // 名前入力
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onIntent(RegisterContract.Intent.OnNameChanged(it)) },
                label = { Text("名前") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Name",
                        tint = YoinColors.Primary,
                        modifier = Modifier.size(YoinSizes.iconSmall)
                    )
                },
                isError = state.nameError != null,
                supportingText = state.nameError?.let { { Text(it, color = YoinColors.Error) } },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(YoinSpacing.md),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = YoinColors.Primary,
                    unfocusedBorderColor = YoinColors.AccentGold,
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
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = "Email",
                        tint = YoinColors.Primary,
                        modifier = Modifier.size(YoinSizes.iconSmall)
                    )
                },
                isError = state.emailError != null,
                supportingText = state.emailError?.let { { Text(it, color = YoinColors.Error) } },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(YoinSpacing.md),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = YoinColors.Primary,
                    unfocusedBorderColor = YoinColors.AccentGold,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            }

            // 次へボタン
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(YoinSpacing.md)
            ) {
                Button(
                    onClick = { viewModel.onIntent(RegisterContract.Intent.OnNextPressed) },
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
                            text = "次へ",
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
private fun RegisterScreenPreview() {
    MaterialTheme {
        RegisterScreen(
            viewModel = RegisterViewModel(
                registerWithEmailUseCase = com.yoin.domain.auth.usecase.RegisterWithEmailUseCase(
                    authRepository = object : com.yoin.domain.auth.repository.AuthRepository {
                        override fun getCurrentUser() = kotlinx.coroutines.flow.flowOf(null)
                        override suspend fun createGuestUser(displayName: String) = com.yoin.domain.auth.model.AuthResult.Error("")
                        override suspend fun registerWithEmail(email: String, password: String, displayName: String) = com.yoin.domain.auth.model.AuthResult.Error("")
                        override suspend fun loginWithEmail(email: String, password: String) = com.yoin.domain.auth.model.AuthResult.Error("")
                        override suspend fun logout() = Result.success(Unit)
                        override suspend fun convertGuestToRegistered(email: String, password: String) = com.yoin.domain.auth.model.AuthResult.Error("")
                        override suspend fun updateUser(
                            displayName: String?,
                            avatarUrl: String?
                        ): Result<User> {
                            TODO("Not yet implemented")
                        }
                    }
                )
            ),
            onNavigateToPasswordScreen = {},
            onNavigateBack = {}
        )
    }
}
