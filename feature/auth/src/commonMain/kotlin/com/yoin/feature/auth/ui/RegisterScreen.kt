package com.yoin.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraRoll
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.domain.auth.model.User
import com.yoin.feature.auth.viewmodel.RegisterContract
import com.yoin.feature.auth.viewmodel.RegisterViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * 新規登録画面 - Modern Cinematic Design
 *
 * デザインコンセプト:
 * - 黒背景 + グラデーション
 * - 透明度のあるテキストフィールド
 * - グラデーションボタン
 * - シネマティックでスタイリッシュなUI
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
                is RegisterContract.Effect.NavigateToHome -> {}
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
                text = "はじめまして",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // サブタイトル
            Text(
                text = "余韻の旅を始めましょう",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                fontStyle = FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 名前入力
            CinematicNameField(
                name = state.name,
                error = state.nameError,
                onNameChanged = { viewModel.onIntent(RegisterContract.Intent.OnNameChanged(it)) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // メールアドレス入力
            CinematicRegisterEmailField(
                email = state.email,
                error = state.emailError,
                onEmailChanged = { viewModel.onIntent(RegisterContract.Intent.OnEmailChanged(it)) }
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 次へボタン（グラデーション）
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
                    onClick = { viewModel.onIntent(RegisterContract.Intent.OnNextPressed) },
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
                            text = "次へ",
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
 * シネマティックな名前入力フィールド
 */
@Composable
private fun CinematicNameField(
    name: String,
    error: String?,
    onNameChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "名前",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.9f),
            fontWeight = FontWeight.Medium
        )

        OutlinedTextField(
            value = name,
            onValueChange = onNameChanged,
            placeholder = {
                Text(
                    text = "山田 太郎",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Name",
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
 * シネマティックなメールアドレス入力フィールド
 */
@Composable
private fun CinematicRegisterEmailField(
    email: String,
    error: String?,
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
