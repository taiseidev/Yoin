package com.yoin.feature.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.auth.viewmodel.LoginContract
import com.yoin.feature.auth.viewmodel.LoginViewModel

/**
 * ログイン画面
 *
 * Apple / Google / ゲストログインの3つのオプションを提供
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToHome: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    // Effect の処理
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LoginContract.Effect.NavigateToHome -> onNavigateToHome()
                is LoginContract.Effect.ShowError -> {
                    // TODO: エラー表示（Snackbarなど）
                }
            }
        }
    }

    LoginScreenContent(
        isLoading = state.isLoading,
        error = state.error,
        onSignInWithApple = { viewModel.handleIntent(LoginContract.Intent.SignInWithApple) },
        onSignInWithGoogle = { viewModel.handleIntent(LoginContract.Intent.SignInWithGoogle) },
        onSignInAsGuest = { viewModel.handleIntent(LoginContract.Intent.SignInAsGuest) }
    )
}

/**
 * プレビュー
 */
@PhonePreview
@Composable
private fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreenContent(
            isLoading = false,
            error = null,
            onSignInWithApple = {},
            onSignInWithGoogle = {},
            onSignInAsGuest = {}
        )
    }
}

/**
 * ログイン画面のコンテンツ（Previewフレンドリー版）
 */
@Composable
private fun LoginScreenContent(
    isLoading: Boolean,
    error: String?,
    onSignInWithApple: () -> Unit,
    onSignInWithGoogle: () -> Unit,
    onSignInAsGuest: () -> Unit,
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ロゴとタイトル
            Text(
                text = "🎞",
                fontSize = 64.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Yoin.",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "旅の思い出を、フィルムで。",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(64.dp))

            // ローディング表示
            if (isLoading) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Apple でログイン
            Button(
                onClick = onSignInWithApple,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                enabled = !isLoading
            ) {
                Text(
                    text = " Apple でログイン",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Google でログイン
            OutlinedButton(
                onClick = onSignInWithGoogle,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading
            ) {
                Text(
                    text = "🔷 Google でログイン",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ゲストとして続ける
            TextButton(
                onClick = onSignInAsGuest,
                enabled = !isLoading
            ) {
                Text(
                    text = "ゲストとして続ける",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 注意書き
            Text(
                text = "ゲストログインでは一部機能に制限があります",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            // エラー表示
            error?.let { errorMessage ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
