package com.yoin.feature.room.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.design.theme.YoinSizes
import com.yoin.core.design.theme.YoinFontSizes
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.room.viewmodel.RoomCreatedContract
import com.yoin.feature.room.viewmodel.RoomCreatedViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * ルーム作成完了画面
 *
 * 機能:
 * - ルーム作成成功メッセージの表示
 * - 招待リンク/QRコードの表示
 * - リンクコピー機能
 * - ルーム詳細画面への遷移
 *
 * @param roomId 作成されたルームID
 * @param viewModel RoomCreatedViewModel
 * @param onNavigateToRoomDetail ルーム詳細画面への遷移コールバック
 * @param onNavigateToHome ホーム画面への遷移コールバック
 */
@Composable
fun RoomCreatedScreen(
    roomId: String,
    viewModel: RoomCreatedViewModel,
    onNavigateToRoomDetail: (String) -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is RoomCreatedContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is RoomCreatedContract.Effect.ShowSuccess -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is RoomCreatedContract.Effect.NavigateToRoomDetail -> {
                    onNavigateToRoomDetail(effect.roomId)
                }
                is RoomCreatedContract.Effect.NavigateToHome -> {
                    onNavigateToHome()
                }
                is RoomCreatedContract.Effect.ShareInviteLink -> {
                    // TODO: プラットフォーム固有の共有機能を実装
                    snackbarHostState.showSnackbar("共有機能は今後実装予定です")
                }
            }
        }
    }

    // 画面表示時の初期化
    LaunchedEffect(roomId) {
        viewModel.onIntent(RoomCreatedContract.Intent.OnScreenDisplayed(roomId))
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
                RoomCreatedHeader()

                // コンテンツ
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = YoinSpacing.xl, vertical = YoinSpacing.xxxl),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(YoinSpacing.xxl)
                ) {
                    // 成功アイコン
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "成功",
                        tint = YoinColors.Primary,
                        modifier = Modifier.size(YoinSizes.logoSmall)
                    )

                    // 成功メッセージ
                    Text(
                        text = "ルームを作成しました！",
                        fontSize = YoinFontSizes.headingMedium.value.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    state.roomInfo?.let { roomInfo ->
                        // ルーム情報カード
                        RoomInfoCard(roomInfo)

                        Spacer(modifier = Modifier.height(YoinSpacing.sm))

                        // 招待セクション
                        InvitationSection(
                            inviteLink = state.inviteLink,
                            onCopyLink = {
                                viewModel.onIntent(RoomCreatedContract.Intent.OnCopyLinkPressed)
                            },
                            onShareQR = {
                                viewModel.onIntent(RoomCreatedContract.Intent.OnShareQRPressed)
                            }
                        )

                        Spacer(modifier = Modifier.height(YoinSpacing.lg))

                        // ルームに移動ボタン
                        Button(
                            onClick = {
                                viewModel.onIntent(RoomCreatedContract.Intent.OnGoToRoomPressed)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = YoinColors.Primary
                            ),
                            shape = RoundedCornerShape(YoinSpacing.md),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(YoinSizes.buttonHeightLarge)
                        ) {
                            Text(
                                text = "ルームを見る",
                                fontSize = YoinFontSizes.bodyMedium.value.sp,
                                fontWeight = FontWeight.Bold,
                                color = YoinColors.OnPrimary
                            )
                        }

                        // ホームに戻るボタン
                        TextButton(
                            onClick = {
                                viewModel.onIntent(RoomCreatedContract.Intent.OnBackToHomePressed)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "ホームに戻る",
                                fontSize = YoinFontSizes.labelLarge.value.sp,
                                color = YoinColors.TextSecondary
                            )
                        }
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
 * ルーム作成完了ヘッダー
 */
@Composable
private fun RoomCreatedHeader() {
    Surface(
        color = YoinColors.OnPrimary,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // ヘッダーコンテンツ
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = YoinSpacing.lg, end = YoinSpacing.lg, top = YoinSpacing.xxl, bottom = YoinSpacing.md),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "作成完了",
                    fontSize = YoinFontSizes.headingSmall.value.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )
            }

            HorizontalDivider(
                color = YoinColors.SurfaceVariant,
                thickness = 0.65.dp
            )
        }
    }
}

/**
 * ルーム情報カード
 */
@Composable
private fun RoomInfoCard(roomInfo: RoomCreatedContract.RoomInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(YoinSpacing.md),
        colors = CardDefaults.cardColors(containerColor = YoinColors.Background),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(YoinSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(YoinSpacing.sm)
        ) {
            Text(
                text = roomInfo.emoji,
                fontSize = YoinSpacing.massive.value.sp
            )

            Text(
                text = roomInfo.title,
                fontSize = YoinFontSizes.headingSmall.value.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary
            )

            Text(
                text = roomInfo.dateRange,
                fontSize = YoinFontSizes.labelLarge.value.sp,
                color = YoinColors.TextSecondary
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(YoinSpacing.xs)
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "場所",
                    tint = YoinColors.TextSecondary,
                    modifier = Modifier.size(YoinSizes.iconSmall)
                )
                Text(
                    text = roomInfo.destination,
                    fontSize = YoinFontSizes.labelLarge.value.sp,
                    color = YoinColors.TextSecondary
                )
            }
        }
    }
}

/**
 * 招待セクション
 */
@Composable
private fun InvitationSection(
    inviteLink: String,
    onCopyLink: () -> Unit,
    onShareQR: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(YoinSpacing.lg)
    ) {
        // セクションタイトル
        Text(
            text = "メンバーを招待",
            fontSize = YoinFontSizes.headingSmall.value.sp,
            fontWeight = FontWeight.Bold,
            color = YoinColors.TextPrimary
        )

        // 招待リンク
        InviteLinkCard(
            link = inviteLink,
            onCopyClick = onCopyLink
        )

        // QRコード
        QRCodeCard(
            onShareClick = onShareQR
        )
    }
}

/**
 * 招待リンクカード
 */
@Composable
private fun InviteLinkCard(
    link: String,
    onCopyClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(YoinSpacing.sm)
    ) {
        Text(
            text = "招待リンク",
            fontSize = YoinFontSizes.labelLarge.value.sp,
            fontWeight = FontWeight.SemiBold,
            color = YoinColors.TextPrimary
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(YoinColors.Background, RoundedCornerShape(YoinSpacing.sm))
                .border(1.dp, YoinColors.SurfaceVariant, RoundedCornerShape(YoinSpacing.sm))
                .padding(YoinSpacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = link,
                fontSize = YoinFontSizes.labelSmall.value.sp,
                color = YoinColors.TextSecondary,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(YoinSpacing.sm))

            TextButton(
                onClick = onCopyClick,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = YoinColors.Primary
                )
            ) {
                Text(
                    text = "コピー",
                    fontSize = YoinFontSizes.labelLarge.value.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * QRコードカード
 */
@Composable
private fun QRCodeCard(
    onShareClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(YoinSpacing.sm)
    ) {
        Text(
            text = "QRコード",
            fontSize = YoinFontSizes.labelLarge.value.sp,
            fontWeight = FontWeight.SemiBold,
            color = YoinColors.TextPrimary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(YoinSpacing.md),
            colors = CardDefaults.cardColors(containerColor = YoinColors.Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(YoinSpacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(YoinSpacing.md)
            ) {
                // QRコードプレースホルダー
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(YoinColors.Background, RoundedCornerShape(YoinSpacing.sm)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(YoinSpacing.sm)
                    ) {
                        Text(
                            text = "◼◻◼",
                            fontSize = YoinSpacing.massive.value.sp,
                            color = YoinColors.TextSecondary
                        )
                        Text(
                            text = "QRコード",
                            fontSize = YoinFontSizes.labelLarge.value.sp,
                            color = YoinColors.TextSecondary
                        )
                    }
                }

                // 共有ボタン
                OutlinedButton(
                    onClick = onShareClick,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = YoinColors.Primary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, YoinColors.Primary),
                    shape = RoundedCornerShape(YoinSpacing.sm),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "QRコードを共有",
                        fontSize = YoinFontSizes.labelLarge.value.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * プレビュー: ルーム情報カード
 */
@PhonePreview
@Composable
private fun RoomInfoCardCreatedPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Surface)
                .padding(16.dp)
        ) {
            RoomInfoCard(
                roomInfo = RoomCreatedContract.RoomInfo(
                    id = "room123",
                    title = "沖縄旅行",
                    emoji = "🏝️",
                    dateRange = "12/25 - 12/28",
                    destination = "沖縄県"
                )
            )
        }
    }
}

/**
 * プレビュー: 招待リンクカード
 */
@PhonePreview
@Composable
private fun InviteLinkCardPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Surface)
                .padding(16.dp)
        ) {
            InviteLinkCard(
                link = "https://yoin.app/room/abc123",
                onCopyClick = {}
            )
        }
    }
}

/**
 * プレビュー: QRコードカード
 */
@PhonePreview
@Composable
private fun QRCodeCardPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Surface)
                .padding(16.dp)
        ) {
            QRCodeCard(
                onShareClick = {}
            )
        }
    }
}
