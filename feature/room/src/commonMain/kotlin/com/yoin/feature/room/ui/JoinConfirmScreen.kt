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
import com.yoin.feature.room.viewmodel.JoinConfirmContract
import com.yoin.feature.room.viewmodel.JoinConfirmViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * ルーム参加確認画面
 *
 * 機能:
 * - 招待されたルーム情報の表示
 * - ニックネーム入力
 * - 参加方法の選択（ログイン/新規登録/ゲスト）
 *
 * @param roomId 招待されたルームID
 * @param viewModel JoinConfirmViewModel
 * @param onNavigateToLogin ログイン画面への遷移コールバック
 * @param onNavigateToRegister 新規登録画面への遷移コールバック
 * @param onNavigateToRoomDetail ルーム詳細画面への遷移コールバック
 * @param onNavigateBack 戻るコールバック
 */
@Composable
fun JoinConfirmScreen(
    roomId: String,
    viewModel: JoinConfirmViewModel,
    onNavigateToLogin: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onNavigateToRoomDetail: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is JoinConfirmContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is JoinConfirmContract.Effect.NavigateToLogin -> {
                    onNavigateToLogin()
                }
                is JoinConfirmContract.Effect.NavigateToRegister -> {
                    onNavigateToRegister()
                }
                is JoinConfirmContract.Effect.NavigateToRoomDetail -> {
                    onNavigateToRoomDetail(effect.roomId)
                }
                is JoinConfirmContract.Effect.NavigateBack -> {
                    onNavigateBack()
                }
            }
        }
    }

    // 画面表示時の初期化
    LaunchedEffect(roomId) {
        viewModel.onIntent(JoinConfirmContract.Intent.OnScreenDisplayed(roomId))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(YoinColors.Surface)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = YoinColors.Primary
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // ヘッダー
                JoinConfirmHeader(
                    onClosePressed = {
                        viewModel.onIntent(JoinConfirmContract.Intent.OnClosePressed)
                    }
                )

                // コンテンツ
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    state.roomInfo?.let { roomInfo ->
                        // 絵文字アイコン
                        Text(
                            text = roomInfo.emoji,
                            fontSize = 56.sp
                        )

                        // タイトル
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "「${roomInfo.title}」に",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = YoinColors.TextPrimary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "参加しますか？",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = YoinColors.TextPrimary,
                                textAlign = TextAlign.Center
                            )
                        }

                        // ルーム情報カード
                        RoomInfoCard(roomInfo)

                        // ニックネーム入力
                        NicknameField(
                            nickname = state.nickname,
                            error = state.nicknameError,
                            onNicknameChanged = { nickname ->
                                viewModel.onIntent(JoinConfirmContract.Intent.OnNicknameChanged(nickname))
                            }
                        )

                        // ログインして参加ボタン
                        Button(
                            onClick = {
                                viewModel.onIntent(JoinConfirmContract.Intent.OnLoginAndJoinPressed)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = YoinColors.Primary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Text(
                                text = "ログインして参加",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = YoinColors.OnPrimary
                            )
                        }

                        // 新規登録して参加ボタン
                        OutlinedButton(
                            onClick = {
                                viewModel.onIntent(JoinConfirmContract.Intent.OnRegisterAndJoinPressed)
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = YoinColors.Primary
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, YoinColors.Primary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Text(
                                text = "新規登録して参加",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // ゲストで参加ボタン
                        TextButton(
                            onClick = {
                                viewModel.onIntent(JoinConfirmContract.Intent.OnGuestJoinPressed)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Text(
                                text = "ゲストで参加",
                                fontSize = 16.sp,
                                color = YoinColors.TextSecondary
                            )
                        }

                        // ゲスト制限の注意書き
                        Text(
                            text = "ⓘ ゲストは撮影5枚まで、ダウンロード不可",
                            fontSize = 11.sp,
                            color = YoinColors.TextSecondary,
                            textAlign = TextAlign.Center
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
}

/**
 * 参加確認ヘッダー
 */
@Composable
private fun JoinConfirmHeader(
    onClosePressed: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 16.dp)
    ) {
        // 時刻表示（中央）
        Text(
            text = "9:41",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            color = YoinColors.TextPrimary,
            modifier = Modifier.align(Alignment.Center)
        )

        // 閉じるボタン（右上）
        Text(
            text = "✕",
            fontSize = 18.sp,
            color = YoinColors.TextSecondary,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clickable(onClick = onClosePressed)
        )
    }
}

/**
 * ルーム情報カード
 */
@Composable
private fun RoomInfoCard(roomInfo: JoinConfirmContract.RoomInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = YoinColors.Background),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 期間
            RoomInfoRow(
                icon = "📅",
                label = "期間",
                value = roomInfo.dateRange
            )

            // 目的地
            RoomInfoRow(
                icon = "📍",
                label = "目的地",
                value = roomInfo.destination
            )

            // メンバー
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "👥",
                        fontSize = 16.sp
                    )
                    Text(
                        text = "メンバー",
                        fontSize = 13.sp,
                        color = YoinColors.TextSecondary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // メンバーアバター（簡易実装）
                    repeat(minOf(3, roomInfo.memberCount)) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(YoinColors.Primary, CircleShape)
                        )
                    }

                    Text(
                        text = "${roomInfo.memberCount}人参加中",
                        fontSize = 14.sp,
                        color = YoinColors.TextPrimary
                    )
                }
            }

            // 現像予定
            RoomInfoRow(
                icon = "📸",
                label = "現像予定",
                value = roomInfo.developmentDateTime,
                valueColor = YoinColors.Primary
            )
        }
    }
}

/**
 * ルーム情報行
 */
@Composable
private fun RoomInfoRow(
    icon: String,
    label: String,
    value: String,
    valueColor: Color = YoinColors.TextPrimary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = icon,
                fontSize = 16.sp
            )
            Text(
                text = label,
                fontSize = 13.sp,
                color = YoinColors.TextSecondary
            )
        }

        Text(
            text = value,
            fontSize = 14.sp,
            color = valueColor
        )
    }
}

/**
 * ニックネーム入力フィールド
 */
@Composable
private fun NicknameField(
    nickname: String,
    error: String?,
    onNicknameChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "ニックネーム",
                fontSize = 12.sp,
                color = YoinColors.TextSecondary
            )
            Text(
                text = "*",
                fontSize = 12.sp,
                color = YoinColors.Error
            )
        }

        OutlinedTextField(
            value = nickname,
            onValueChange = onNicknameChanged,
            placeholder = {
                Text(
                    text = "表示名を入力",
                    fontSize = 14.sp,
                    color = YoinColors.TextSecondary
                )
            },
            isError = error != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (error != null) YoinColors.Error else YoinColors.Primary,
                unfocusedBorderColor = if (error != null) YoinColors.Error else YoinColors.SurfaceVariant,
                errorBorderColor = YoinColors.Error
            ),
            shape = RoundedCornerShape(8.dp),
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
 * プレビュー: ルーム情報行
 */
@PhonePreview
@Composable
private fun RoomInfoRowPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                RoomInfoRow(icon = "📅", label = "期間", value = "12/25 - 12/28")
                RoomInfoRow(icon = "📍", label = "目的地", value = "沖縄県")
                RoomInfoRow(icon = "📸", label = "現像予定", value = "1/1 10:00", valueColor = YoinColors.Primary)
            }
        }
    }
}

/**
 * プレビュー: ルーム情報カード
 */
@PhonePreview
@Composable
private fun RoomInfoCardPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Surface)
                .padding(16.dp)
        ) {
            RoomInfoCard(
                roomInfo = JoinConfirmContract.RoomInfo(
                    id = "room123",
                    title = "沖縄旅行",
                    emoji = "🏝️",
                    dateRange = "12/25 - 12/28",
                    destination = "沖縄県",
                    memberCount = 3,
                    developmentDateTime = "1/1 10:00"
                )
            )
        }
    }
}

/**
 * プレビュー: ニックネーム入力フィールド
 */
@PhonePreview
@Composable
private fun NicknameFieldPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Surface)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                NicknameField(
                    nickname = "太郎",
                    error = null,
                    onNicknameChanged = {}
                )
                NicknameField(
                    nickname = "",
                    error = "ニックネームを入力してください",
                    onNicknameChanged = {}
                )
            }
        }
    }
}
