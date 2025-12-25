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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.design.theme.YoinSizes
import com.yoin.core.design.theme.YoinFontSizes
import com.yoin.feature.room.viewmodel.RoomCreateContract
import com.yoin.feature.room.viewmodel.RoomCreateViewModel
import com.yoin.core.ui.preview.PhonePreview
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
                    .padding(horizontal = YoinSpacing.xl, vertical = YoinSpacing.xxl),
                verticalArrangement = Arrangement.spacedBy(YoinSpacing.xxl)
            ) {
                // タイトルセクション
                Text(
                    text = "新しい旅行を作成",
                    fontSize = YoinFontSizes.headingMedium.value.sp,
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
                    horizontalArrangement = Arrangement.spacedBy(YoinSpacing.md)
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

                Spacer(modifier = Modifier.height(YoinSpacing.sm))

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
                    shape = RoundedCornerShape(YoinSpacing.md),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(YoinSizes.buttonHeightLarge)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = YoinColors.OnPrimary,
                            modifier = Modifier.size(YoinSizes.iconMedium)
                        )
                    } else {
                        Text(
                            text = "ルームを作成",
                            fontSize = YoinFontSizes.bodyMedium.value.sp,
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
            // ヘッダーコンテンツ
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = YoinSpacing.lg, end = YoinSpacing.lg, top = YoinSpacing.xxl, bottom = YoinSpacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 戻るボタン
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "戻る",
                        tint = YoinColors.TextPrimary
                    )
                }

                // タイトル
                Text(
                    text = "ルーム作成",
                    fontSize = YoinFontSizes.headingSmall.value.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )

                // 右側スペーサー
                Spacer(modifier = Modifier.width(YoinSpacing.xl))
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
        verticalArrangement = Arrangement.spacedBy(YoinSpacing.sm)
    ) {
        Text(
            text = "絵文字",
            fontSize = YoinFontSizes.labelLarge.value.sp,
            fontWeight = FontWeight.SemiBold,
            color = YoinColors.TextPrimary
        )

        Box(
            modifier = Modifier
                .size(YoinSizes.logoSmall)
                .background(YoinColors.Background, RoundedCornerShape(YoinSpacing.md))
                .border(1.dp, YoinColors.SurfaceVariant, RoundedCornerShape(YoinSpacing.md))
                .clickable(onClick = onEmojiClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = selectedEmoji,
                fontSize = YoinSpacing.massive.value.sp
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
        verticalArrangement = Arrangement.spacedBy(YoinSpacing.sm)
    ) {
        Text(
            text = label,
            fontSize = YoinFontSizes.labelLarge.value.sp,
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
                    fontSize = YoinFontSizes.labelLarge.value.sp
                )
            },
            isError = error != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (error != null) YoinColors.Error else YoinColors.Primary,
                unfocusedBorderColor = if (error != null) YoinColors.Error else YoinColors.SurfaceVariant,
                errorBorderColor = YoinColors.Error
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
        verticalArrangement = Arrangement.spacedBy(YoinSpacing.sm)
    ) {
        Text(
            text = label,
            fontSize = YoinFontSizes.labelLarge.value.sp,
            fontWeight = FontWeight.SemiBold,
            color = YoinColors.TextPrimary
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(YoinSizes.buttonHeightLarge)
                .background(Color.White, RoundedCornerShape(YoinSpacing.md))
                .border(
                    width = 1.dp,
                    color = if (error != null) YoinColors.Error else YoinColors.SurfaceVariant,
                    shape = RoundedCornerShape(YoinSpacing.md)
                )
                .clickable(onClick = onClick)
                .padding(horizontal = YoinSpacing.lg),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = value.ifBlank { "選択してください" },
                fontSize = YoinFontSizes.labelLarge.value.sp,
                color = if (value.isBlank()) YoinColors.TextSecondary else YoinColors.TextPrimary
            )
        }

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
                horizontalArrangement = Arrangement.spacedBy(YoinSpacing.sm),
                contentPadding = PaddingValues(vertical = YoinSpacing.sm)
            ) {
                items(RoomCreateContract.POPULAR_EMOJIS) { emoji ->
                    Box(
                        modifier = Modifier
                            .size(YoinSizes.iconXLarge + YoinSpacing.md)
                            .background(YoinColors.Background, CircleShape)
                            .clickable { onEmojiSelected(emoji) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = emoji,
                            fontSize = YoinFontSizes.displayMedium.value.sp
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
                verticalArrangement = Arrangement.spacedBy(YoinSpacing.sm)
            ) {
                Text(
                    text = "形式: YYYY/MM/DD",
                    fontSize = YoinFontSizes.labelSmall.value.sp,
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

/**
 * プレビュー: 絵文字セレクター
 */
@PhonePreview
@Composable
private fun EmojiSelectorPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Surface)
                .padding(16.dp)
        ) {
            EmojiSelector(
                selectedEmoji = "🏝️",
                onEmojiClick = {}
            )
        }
    }
}

/**
 * プレビュー: フォーム入力フィールド
 */
@PhonePreview
@Composable
private fun FormFieldPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Surface)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                FormField(
                    label = "旅行名",
                    value = "北海道旅行",
                    onValueChange = {},
                    placeholder = "例: 北海道旅行"
                )
                FormField(
                    label = "目的地",
                    value = "",
                    onValueChange = {},
                    placeholder = "例: 札幌・小樽",
                    error = "目的地を入力してください"
                )
            }
        }
    }
}

/**
 * プレビュー: 日付フィールド
 */
@PhonePreview
@Composable
private fun DateFieldPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Surface)
                .padding(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DateField(
                    label = "開始日",
                    value = "2024/12/25",
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
                DateField(
                    label = "終了日",
                    value = "",
                    onClick = {},
                    error = "終了日を選択してください",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
