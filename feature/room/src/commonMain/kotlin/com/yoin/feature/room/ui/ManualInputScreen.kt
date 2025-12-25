package com.yoin.feature.room.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.ui.component.YoinAppBar
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.room.viewmodel.ManualInputContract
import com.yoin.feature.room.viewmodel.ManualInputViewModel

/**
 * 手動入力画面
 *
 * QRコードスキャンに失敗した場合や、手動でルームコードを入力したい場合に使用します。
 *
 * @param viewModel ManualInputViewModel
 * @param onNavigateBack 戻るボタンのコールバック
 * @param onNavigateToJoinConfirm 参加確認画面への遷移コールバック
 */
@Composable
fun ManualInputScreen(
    viewModel: ManualInputViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToJoinConfirm: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ManualInputContract.Effect.NavigateBack -> onNavigateBack()
                is ManualInputContract.Effect.NavigateToJoinConfirm -> {
                    onNavigateToJoinConfirm(effect.roomId)
                }
                is ManualInputContract.Effect.ShowError -> {
                    // エラーはステートで表示されるのでここでは何もしない
                }
            }
        }
    }

    ManualInputContent(
        state = state,
        onIntent = viewModel::handleIntent,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManualInputContent(
    state: ManualInputContract.State,
    onIntent: (ManualInputContract.Intent) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
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
                title = "ルームコード入力",
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = YoinColors.TextPrimary
                        )
                    }
                }
            )

            // コンテンツ
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(48.dp))

                // アイコン
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = YoinColors.Primary.copy(alpha = 0.1f),
                    modifier = Modifier.size(120.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.QrCode,
                            contentDescription = null,
                            tint = YoinColors.Primary,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // タイトル
                Text(
                    text = "ルームコードを入力",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 説明
                Text(
                    text = "招待されたルームのコードを入力してください",
                    fontSize = 14.sp,
                    color = YoinColors.TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                // ルームコード入力フィールド
                OutlinedTextField(
                    value = state.roomCode,
                    onValueChange = {
                        onIntent(ManualInputContract.Intent.OnRoomCodeChanged(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("ルームコード") },
                    placeholder = { Text("例: ABC123XYZ") },
                    isError = !state.isCodeValid,
                    supportingText = {
                        if (state.errorMessage != null) {
                            Text(
                                text = state.errorMessage,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Text(
                                text = "6文字以上の英数字",
                                color = YoinColors.TextSecondary
                            )
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (state.roomCode.isNotBlank() && state.isCodeValid) {
                                onIntent(ManualInputContract.Intent.OnJoinPressed)
                            }
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = YoinColors.Primary,
                        unfocusedBorderColor = YoinColors.TextSecondary.copy(alpha = 0.3f),
                        errorBorderColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 参加ボタン
                Button(
                    onClick = {
                        onIntent(ManualInputContract.Intent.OnJoinPressed)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !state.isLoading && state.roomCode.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YoinColors.Primary,
                        disabledContainerColor = YoinColors.TextSecondary.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "ルームに参加",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // キャンセルボタン
                TextButton(
                    onClick = {
                        onIntent(ManualInputContract.Intent.OnCancelPressed)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "キャンセル",
                        fontSize = 14.sp,
                        color = YoinColors.TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // ヒントカード
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = YoinColors.Surface,
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "ヒント",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = YoinColors.TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• ルームコードは招待者から共有されます\n• 英数字6文字以上で構成されています\n• 大文字小文字は区別されません",
                            fontSize = 12.sp,
                            color = YoinColors.TextSecondary,
                            lineHeight = 20.sp
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
    }
}

/**
 * プレビュー
 */
@PhonePreview
@Composable
private fun ManualInputScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            ManualInputContent(
                state = ManualInputContract.State(
                    roomCode = "ABC123",
                    isCodeValid = true
                )
            )
        }
    }
}
