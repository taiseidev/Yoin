package com.yoin.feature.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.ui.component.YoinAppBar
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.settings.viewmodel.DeleteAccountContract
import com.yoin.feature.settings.viewmodel.DeleteAccountViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * アカウント削除画面
 *
 * 機能:
 * - アカウント削除の警告表示
 * - パスワード確認
 * - 確認テキスト入力（"DELETE"）
 * - 削除理由の入力（任意）
 * - 最終確認ダイアログ
 *
 * @param viewModel DeleteAccountViewModel
 * @param onNavigateBack 戻るボタンのコールバック
 * @param onNavigateToLogin ログイン画面への遷移コールバック
 */
@Composable
fun DeleteAccountScreen(
    viewModel: DeleteAccountViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is DeleteAccountContract.Effect.NavigateBack -> onNavigateBack()
                is DeleteAccountContract.Effect.NavigateToLogin -> onNavigateToLogin()
                is DeleteAccountContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is DeleteAccountContract.Effect.ShowWarning -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    // 確認ダイアログ
    if (state.showConfirmDialog) {
        DeleteConfirmDialog(
            onDismiss = { viewModel.handleIntent(DeleteAccountContract.Intent.OnConfirmDialogDismissed) },
            onConfirm = { viewModel.handleIntent(DeleteAccountContract.Intent.OnConfirmDialogConfirmed) }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(YoinColors.Background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ヘッダー
            YoinAppBar(
                title = "アカウント削除",
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.handleIntent(DeleteAccountContract.Intent.OnBackPressed) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = YoinColors.TextPrimary
                        )
                    }
                }
            )

            // フォームコンテンツ
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // 警告バナー
                WarningBanner()

                Spacer(modifier = Modifier.height(32.dp))

                // パスワード入力
                Text(
                    text = "パスワード",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = YoinColors.TextPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = state.password,
                    onValueChange = {
                        viewModel.handleIntent(DeleteAccountContract.Intent.OnPasswordChanged(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = state.validationErrors.passwordError != null,
                    enabled = !state.isLoading,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = YoinColors.SurfaceVariant,
                        focusedBorderColor = YoinColors.Primary,
                        errorBorderColor = MaterialTheme.colorScheme.error
                    )
                )

                if (state.validationErrors.passwordError != null) {
                    Text(
                        text = state.validationErrors.passwordError!!,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp, start = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 確認テキスト入力
                Text(
                    text = "確認のため「DELETE」と入力してください",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = YoinColors.TextPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = state.confirmationText,
                    onValueChange = {
                        viewModel.handleIntent(DeleteAccountContract.Intent.OnConfirmationTextChanged(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("DELETE", color = YoinColors.TextSecondary) },
                    isError = state.validationErrors.confirmationTextError != null,
                    enabled = !state.isLoading,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = YoinColors.SurfaceVariant,
                        focusedBorderColor = YoinColors.Primary,
                        errorBorderColor = MaterialTheme.colorScheme.error
                    )
                )

                if (state.validationErrors.confirmationTextError != null) {
                    Text(
                        text = state.validationErrors.confirmationTextError!!,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp, start = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 削除理由（任意）
                Text(
                    text = "削除理由（任意）",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = YoinColors.TextPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = state.reasonForDeletion,
                    onValueChange = {
                        viewModel.handleIntent(DeleteAccountContract.Intent.OnReasonChanged(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = { Text("削除理由を教えていただけると幸いです", color = YoinColors.TextSecondary) },
                    enabled = !state.isLoading,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = YoinColors.SurfaceVariant,
                        focusedBorderColor = YoinColors.Primary
                    )
                )

                Spacer(modifier = Modifier.height(40.dp))

                // 削除ボタン
                Button(
                    onClick = {
                        viewModel.handleIntent(DeleteAccountContract.Intent.OnDeleteAccountPressed)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "アカウントを削除",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // ホームインジケーター
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .width(134.dp)
                .height(5.dp)
                .background(Color.Black, RoundedCornerShape(100.dp))
        )

        // スナックバー
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * 警告バナー
 */
@Composable
private fun WarningBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = "Warning",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )

            Column {
                Text(
                    text = "アカウントを削除すると",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = """
                        • すべての旅の記録が削除されます
                        • すべての写真が削除されます
                        • 参加中のルームから退出されます
                        • 注文履歴が削除されます
                        • この操作は取り消せません
                    """.trimIndent(),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

/**
 * 削除確認ダイアログ
 */
@Composable
private fun DeleteConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = "Warning",
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = "本当に削除しますか？",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "アカウントを削除すると、すべてのデータが完全に削除されます。この操作は取り消せません。",
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = Color.White
                )
            ) {
                Text("削除する")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("キャンセル", color = YoinColors.TextPrimary)
            }
        }
    )
}

/**
 * プレビュー
 */
@PhonePreview
@Composable
private fun DeleteAccountScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            Text("Delete Account Screen Preview")
        }
    }
}
