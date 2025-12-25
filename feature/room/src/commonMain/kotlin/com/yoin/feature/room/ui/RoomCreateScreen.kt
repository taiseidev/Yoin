package com.yoin.feature.room.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.feature.room.viewmodel.RoomCreateContract
import com.yoin.feature.room.viewmodel.RoomCreateViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * ルーム作成画面
 *
 * 機能:
 * - 旅行名、絵文字、期間、目的地の入力
 * - バリデーション表示
 * - ルーム作成処理
 *
 * @param viewModel RoomCreateViewModel
 * @param onNavigateBack 戻るボタンのコールバック
 * @param onNavigateToRoomDetail ルーム詳細画面への遷移コールバック
 */
@Composable
fun RoomCreateScreen(
    viewModel: RoomCreateViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToRoomDetail: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // 日付ピッカーの状態
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showEmojiPicker by remember { mutableStateOf(false) }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is RoomCreateContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is RoomCreateContract.Effect.NavigateBack -> {
                    onNavigateBack()
                }
                is RoomCreateContract.Effect.ShowStartDatePicker -> {
                    showStartDatePicker = true
                }
                is RoomCreateContract.Effect.ShowEndDatePicker -> {
                    showEndDatePicker = true
                }
                is RoomCreateContract.Effect.ShowEmojiPicker -> {
                    showEmojiPicker = true
                }
                is RoomCreateContract.Effect.NavigateToRoomDetail -> {
                    onNavigateToRoomDetail(effect.roomId)
                }
                is RoomCreateContract.Effect.ShowSuccess -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    // 画面表示時の初期化
    LaunchedEffect(Unit) {
        viewModel.onIntent(RoomCreateContract.Intent.OnScreenDisplayed)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(YoinColors.Surface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ヘッダー
            RoomCreateHeader(
                onBackPressed = {
                    viewModel.onIntent(RoomCreateContract.Intent.OnBackPressed)
                }
            )

            // フォームコンテンツ
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // タイトルセクション
                Text(
                    text = "新しい旅行を作成",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )

                // 絵文字選択
                EmojiSelector(
                    selectedEmoji = state.emoji,
                    onEmojiClick = {
                        viewModel.onIntent(RoomCreateContract.Intent.OnEmojiPickerClicked)
                    }
                )

                // 旅行名入力
                FormField(
                    label = "旅行名",
                    value = state.tripTitle,
                    onValueChange = {
                        viewModel.onIntent(RoomCreateContract.Intent.OnTripTitleChanged(it))
                    },
                    placeholder = "例: 北海道旅行",
                    error = state.titleError
                )

                // 期間入力
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DateField(
                        label = "開始日",
                        value = state.startDate,
                        onClick = {
                            viewModel.onIntent(RoomCreateContract.Intent.OnStartDatePickerClicked)
                        },
                        error = state.startDateError,
                        modifier = Modifier.weight(1f)
                    )
                    DateField(
                        label = "終了日",
                        value = state.endDate,
                        onClick = {
                            viewModel.onIntent(RoomCreateContract.Intent.OnEndDatePickerClicked)
                        },
                        error = state.endDateError,
                        modifier = Modifier.weight(1f)
                    )
                }

                // 目的地入力
                FormField(
                    label = "目的地",
                    value = state.destination,
                    onValueChange = {
                        viewModel.onIntent(RoomCreateContract.Intent.OnDestinationChanged(it))
                    },
                    placeholder = "例: 札幌・小樽",
                    error = state.destinationError
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 作成ボタン
                Button(
                    onClick = {
                        viewModel.onIntent(RoomCreateContract.Intent.OnCreateButtonClicked)
                    },
                    enabled = state.isFormValid && !state.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YoinColors.Primary,
                        disabledContainerColor = YoinColors.SurfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = YoinColors.OnPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "ルームを作成",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (state.isFormValid) Color.White else YoinColors.TextSecondary
                        )
                    }
                }
            }
        }

        // スナックバー
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    // 絵文字ピッカーダイアログ
    if (showEmojiPicker) {
        EmojiPickerDialog(
            onEmojiSelected = { emoji ->
                viewModel.onIntent(RoomCreateContract.Intent.OnEmojiSelected(emoji))
                showEmojiPicker = false
            },
            onDismiss = {
                showEmojiPicker = false
            }
        )
    }

    // 開始日ピッカー（簡易実装）
    if (showStartDatePicker) {
        SimpleDatePickerDialog(
            onDateSelected = { date ->
                viewModel.onIntent(RoomCreateContract.Intent.OnStartDateChanged(date))
                showStartDatePicker = false
            },
            onDismiss = {
                showStartDatePicker = false
            }
        )
    }

    // 終了日ピッカー（簡易実装）
    if (showEndDatePicker) {
        SimpleDatePickerDialog(
            onDateSelected = { date ->
                viewModel.onIntent(RoomCreateContract.Intent.OnEndDateChanged(date))
                showEndDatePicker = false
            },
            onDismiss = {
                showEndDatePicker = false
            }
        )
    }
}

/**
 * ルーム作成ヘッダー
 */
@Composable
private fun RoomCreateHeader(
    onBackPressed: () -> Unit
) {
    Surface(
        color = YoinColors.OnPrimary,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // ステータスバー領域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "9:41",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = YoinColors.TextPrimary
                )
            }

            // ヘッダーコンテンツ
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 戻るボタン
                Text(
                    text = "←",
                    fontSize = 20.sp,
                    color = YoinColors.TextPrimary,
                    modifier = Modifier.clickable(onClick = onBackPressed)
                )

                // タイトル
                Text(
                    text = "ルーム作成",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )

                // 右側スペーサー
                Spacer(modifier = Modifier.width(20.dp))
            }

            HorizontalDivider(
                color = YoinColors.SurfaceVariant,
                thickness = 0.65.dp
            )
        }
    }
}

/**
 * 絵文字セレクター
 */
@Composable
private fun EmojiSelector(
    selectedEmoji: String,
    onEmojiClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "絵文字",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = YoinColors.TextPrimary
        )

        Box(
            modifier = Modifier
                .size(80.dp)
                .background(YoinColors.Background, RoundedCornerShape(12.dp))
                .border(1.dp, YoinColors.SurfaceVariant, RoundedCornerShape(12.dp))
                .clickable(onClick = onEmojiClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = selectedEmoji,
                fontSize = 48.sp
            )
        }
    }
}

/**
 * フォーム入力フィールド
 */
@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String? = null
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = YoinColors.TextPrimary
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = YoinColors.TextSecondary,
                    fontSize = 14.sp
                )
            },
            isError = error != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (error != null) YoinColors.Error else YoinColors.Primary,
                unfocusedBorderColor = if (error != null) YoinColors.Error else YoinColors.SurfaceVariant,
                errorBorderColor = YoinColors.Error
            ),
            shape = RoundedCornerShape(12.dp),
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
 * 日付フィールド
 */
@Composable
private fun DateField(
    label: String,
    value: String,
    onClick: () -> Unit,
    error: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = YoinColors.TextPrimary
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = if (error != null) YoinColors.Error else YoinColors.SurfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = value.ifBlank { "選択してください" },
                fontSize = 14.sp,
                color = if (value.isBlank()) YoinColors.TextSecondary else YoinColors.TextPrimary
            )
        }

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
 * 絵文字ピッカーダイアログ
 */
@Composable
private fun EmojiPickerDialog(
    onEmojiSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "絵文字を選択",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(RoomCreateContract.POPULAR_EMOJIS) { emoji ->
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(YoinColors.Background, CircleShape)
                            .clickable { onEmojiSelected(emoji) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = emoji,
                            fontSize = 32.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("閉じる")
            }
        }
    )
}

/**
 * 簡易日付ピッカーダイアログ（仮実装）
 */
@Composable
private fun SimpleDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var dateInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "日付を入力",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "形式: YYYY/MM/DD",
                    fontSize = 12.sp,
                    color = YoinColors.TextSecondary
                )
                OutlinedTextField(
                    value = dateInput,
                    onValueChange = { dateInput = it },
                    placeholder = {
                        Text(
                            text = "2024/12/24",
                            color = YoinColors.TextSecondary
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (dateInput.isNotBlank()) {
                        onDateSelected(dateInput)
                    }
                }
            ) {
                Text("決定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("キャンセル")
            }
        }
    )
}
