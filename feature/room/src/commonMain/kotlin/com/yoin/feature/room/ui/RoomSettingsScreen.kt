package com.yoin.feature.room.ui

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.room.viewmodel.RoomSettingsContract
import com.yoin.feature.room.viewmodel.RoomSettingsViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * ルーム設定画面
 *
 * 機能:
 * - ルーム名・目的地の編集
 * - ルームアイコンの変更
 * - メンバー管理
 * - 招待リンク再発行
 * - ルーム退出・削除
 *
 * @param viewModel RoomSettingsViewModel
 * @param onNavigateBack 戻るボタンのコールバック
 */
@Composable
fun RoomSettingsScreen(
    viewModel: RoomSettingsViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToMemberList: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showLeaveConfirmDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is RoomSettingsContract.Effect.NavigateBack -> onNavigateBack()
                is RoomSettingsContract.Effect.NavigateToMemberList -> onNavigateToMemberList()
                is RoomSettingsContract.Effect.NavigateToInviteLinkRegenerate -> {
                    snackbarHostState.showSnackbar("招待リンク再発行機能は未実装です")
                }
                is RoomSettingsContract.Effect.ShowLeaveRoomConfirmation -> {
                    showLeaveConfirmDialog = true
                }
                is RoomSettingsContract.Effect.ShowDeleteRoomConfirmation -> {
                    showDeleteConfirmDialog = true
                }
                is RoomSettingsContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is RoomSettingsContract.Effect.ShowSuccess -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(YoinColors.Background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ステータスバー風の時刻表示
            Text(
                text = "9:41",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = YoinColors.TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ナビゲーションバー
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // キャンセルボタン
                Text(
                    text = "キャンセル",
                    fontSize = 16.sp,
                    color = YoinColors.TextSecondary,
                    modifier = Modifier.clickable {
                        viewModel.handleIntent(RoomSettingsContract.Intent.OnCancelPressed)
                    }
                )

                // タイトル
                Text(
                    text = "ルーム設定",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )

                // 保存ボタン
                Text(
                    text = "保存",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.Primary,
                    modifier = Modifier.clickable {
                        viewModel.handleIntent(RoomSettingsContract.Intent.OnSavePressed)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = YoinColors.SurfaceVariant)

            // コンテンツ
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // ルームアイコン
                Box(
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(YoinColors.AccentLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.roomInfo?.icon ?: "🏔",
                            fontSize = 40.sp
                        )
                    }

                    // 編集ボタン
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(YoinColors.Primary, CircleShape)
                            .clickable {
                                viewModel.handleIntent(RoomSettingsContract.Intent.OnIconEditPressed)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✎",
                            fontSize = 12.sp,
                            color = YoinColors.OnPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 旅行の名前
                InputField(
                    label = "旅行の名前",
                    value = state.roomName,
                    onValueChange = { value ->
                        viewModel.handleIntent(RoomSettingsContract.Intent.OnRoomNameChanged(value))
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 目的地
                InputField(
                    label = "目的地",
                    value = state.destination,
                    leadingIcon = "📍",
                    onValueChange = { value ->
                        viewModel.handleIntent(RoomSettingsContract.Intent.OnDestinationChanged(value))
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 旅行期間（変更不可）
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "旅行期間",
                            fontSize = 12.sp,
                            color = YoinColors.TextSecondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "（変更不可）",
                            fontSize = 11.sp,
                            color = YoinColors.TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = YoinColors.Background
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📅",
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${state.roomInfo?.startDate} 〜 ${state.roomInfo?.endDate}",
                                fontSize = 14.sp,
                                color = YoinColors.TextSecondary
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "🔒",
                                fontSize = 12.sp,
                                color = YoinColors.TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // メンバー管理セクション
                SectionHeader(title = "メンバー管理")

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    SettingItem(
                        icon = "👥",
                        title = "メンバー一覧",
                        subtitle = "${state.roomInfo?.memberCount ?: 0}人",
                        onClick = {
                            viewModel.handleIntent(RoomSettingsContract.Intent.OnMemberListPressed)
                        }
                    )

                    SettingItem(
                        icon = "🔗",
                        title = "招待リンクを再発行",
                        onClick = {
                            viewModel.handleIntent(RoomSettingsContract.Intent.OnRegenerateInviteLinkPressed)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 危険な操作セクション
                SectionHeader(title = "危険な操作", color = YoinColors.Error)

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    SettingItem(
                        icon = "🚪",
                        title = "ルームから退出",
                        titleColor = YoinColors.Error,
                        iconColor = YoinColors.Error,
                        chevronColor = YoinColors.Error,
                        backgroundColor = YoinColors.Error.copy(alpha = 0.1f),
                        onClick = {
                            viewModel.handleIntent(RoomSettingsContract.Intent.OnLeaveRoomPressed)
                        }
                    )

                    SettingItem(
                        icon = "🗑",
                        title = "ルームを削除",
                        subtitle = "オーナーのみ",
                        titleColor = YoinColors.Error,
                        iconColor = YoinColors.Error,
                        chevronColor = YoinColors.Error,
                        backgroundColor = YoinColors.Error.copy(alpha = 0.1f),
                        onClick = {
                            viewModel.handleIntent(RoomSettingsContract.Intent.OnDeleteRoomPressed)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(80.dp))
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

    // 退出確認ダイアログ
    if (showLeaveConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLeaveConfirmDialog = false },
            title = { Text("ルームから退出しますか？") },
            text = { Text("退出後は、招待リンクから再参加できます。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLeaveConfirmDialog = false
                        // TODO: 退出処理を実装
                    }
                ) {
                    Text("退出", color = YoinColors.Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveConfirmDialog = false }) {
                    Text("キャンセル")
                }
            }
        )
    }

    // 削除確認ダイアログ
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("ルームを削除しますか？") },
            text = { Text("この操作は取り消せません。すべての写真とデータが削除されます。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        // TODO: 削除処理を実装
                    }
                ) {
                    Text("削除", color = YoinColors.Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("キャンセル")
                }
            }
        )
    }
}

/**
 * セクションヘッダー
 */
@Composable
private fun SectionHeader(
    title: String,
    color: Color = YoinColors.TextSecondary
) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * 入力フィールド
 */
@Composable
private fun InputField(
    label: String,
    value: String,
    leadingIcon: String? = null,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = YoinColors.TextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            leadingIcon = leadingIcon?.let {
                {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        color = YoinColors.TextSecondary
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = YoinColors.Primary,
                unfocusedBorderColor = YoinColors.SurfaceVariant
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * 設定項目
 */
@Composable
private fun SettingItem(
    icon: String,
    title: String,
    subtitle: String? = null,
    titleColor: Color = YoinColors.TextPrimary,
    iconColor: Color = Color.Black,
    chevronColor: Color = YoinColors.TextSecondary,
    backgroundColor: Color = Color.White,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = 18.sp,
                color = iconColor
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                fontSize = 15.sp,
                color = titleColor,
                modifier = Modifier.weight(1f)
            )

            subtitle?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    color = YoinColors.TextSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = "›",
                fontSize = 16.sp,
                color = chevronColor
            )
        }
    }
}

/**
 * プレビュー
 */
@PhonePreview
@Composable
private fun RoomSettingsScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            Text("Room Settings Screen Preview")
        }
    }
}
