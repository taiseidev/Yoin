package com.yoin.feature.room.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.room.model.RoomMember
import com.yoin.feature.room.viewmodel.RoomDetailBeforeContract
import com.yoin.feature.room.viewmodel.RoomDetailBeforeViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * ルーム詳細（現像前）画面
 *
 * 旅行中にユーザーが最も頻繁に見る画面
 * Modern Cinematic with Amber Accent デザイン適用
 *
 * @param roomId ルームID
 * @param viewModel RoomDetailBeforeViewModel
 * @param onNavigateBack 戻るコールバック
 * @param onNavigateToCamera カメラ画面への遷移コールバック
 * @param onNavigateToMemberList メンバー一覧画面への遷移コールバック
 * @param onNavigateToSettings 設定画面への遷移コールバック
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomDetailBeforeScreen(
    roomId: String,
    viewModel: RoomDetailBeforeViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToCamera: (String) -> Unit = {},
    onNavigateToMemberList: (String) -> Unit = {},
    onNavigateToSettings: (String) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is RoomDetailBeforeContract.Effect.NavigateBack -> onNavigateBack()
                is RoomDetailBeforeContract.Effect.NavigateToCamera -> onNavigateToCamera(effect.roomId)
                is RoomDetailBeforeContract.Effect.NavigateToMemberList -> onNavigateToMemberList(effect.roomId)
                is RoomDetailBeforeContract.Effect.NavigateToSettings -> onNavigateToSettings(effect.roomId)
                is RoomDetailBeforeContract.Effect.ShowInviteDialog -> {
                    snackbarHostState.showSnackbar("招待機能は未実装です")
                }
                is RoomDetailBeforeContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = state.roomInfo?.name ?: "",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = YoinColors.TextPrimary
                        )
                        state.roomInfo?.let { roomInfo ->
                            Text(
                                text = "${roomInfo.startDate}〜${roomInfo.endDate} / ${roomInfo.destination}",
                                fontSize = 12.sp,
                                color = YoinColors.TextSecondary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleIntent(RoomDetailBeforeContract.Intent.OnBackPressed) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "戻る",
                            tint = YoinColors.TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.handleIntent(RoomDetailBeforeContract.Intent.OnInvitePressed) }) {
                        Icon(
                            imageVector = Icons.Filled.PersonAdd,
                            contentDescription = "招待",
                            tint = YoinColors.Primary
                        )
                    }
                    IconButton(onClick = { viewModel.handleIntent(RoomDetailBeforeContract.Intent.OnSettingsPressed) }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "設定",
                            tint = YoinColors.TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = YoinColors.Background
                )
            )
        },
        floatingActionButton = {
            // 撮影FABボタン（旅行中のみ有効）
            if (state.tripStatus == RoomDetailBeforeContract.TripStatus.IN_PROGRESS) {
                FloatingActionButton(
                    onClick = { viewModel.handleIntent(RoomDetailBeforeContract.Intent.OnCameraPressed) },
                    containerColor = YoinColors.Primary,
                    contentColor = YoinColors.OnPrimary,
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = "撮影",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = YoinColors.Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // メンバーバー
            MemberBar(
                members = state.members,
                totalCount = state.roomInfo?.memberCount ?: 0,
                onClick = { viewModel.handleIntent(RoomDetailBeforeContract.Intent.OnMemberBarPressed) }
            )

            Spacer(modifier = Modifier.height(YoinSpacing.xxxl))

            // 現像カウントダウンセクション
            DevelopmentCountdownSection(
                tripStatus = state.tripStatus,
                daysUntilDevelopment = state.daysUntilDevelopment,
                developmentDateTime = state.developmentDateTime
            )

            Spacer(modifier = Modifier.height(YoinSpacing.xxxl))

            // 撮影進捗カード
            PhotoProgressCard(
                tripStatus = state.tripStatus,
                todayPhotoCount = state.todayPhotoCount,
                dailyPhotoLimit = state.dailyPhotoLimit,
                modifier = Modifier.padding(horizontal = YoinSpacing.lg)
            )

            Spacer(modifier = Modifier.height(100.dp)) // FABの分のスペース
        }
    }
}

/**
 * メンバーバー
 */
@Composable
private fun MemberBar(
    members: List<RoomMember>,
    totalCount: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = YoinSpacing.lg, vertical = YoinSpacing.sm)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = YoinColors.Surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(YoinSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // メンバーアバター
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy((-8).dp)
            ) {
                items(members.take(6)) { member ->
                    MemberAvatar(member = member)
                }
            }

            // 残りメンバー数
            if (totalCount > 6) {
                Text(
                    text = "+${totalCount - 6}",
                    fontSize = 14.sp,
                    color = YoinColors.TextSecondary,
                    modifier = Modifier.padding(horizontal = YoinSpacing.sm)
                )
            }

            // 矢印
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = YoinColors.TextSecondary
            )
        }
    }
}

/**
 * メンバーアバター
 */
@Composable
private fun MemberAvatar(member: RoomMember) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(YoinColors.SurfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = member.name.take(1),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = YoinColors.TextPrimary
        )
    }
}

/**
 * 現像カウントダウンセクション
 */
@Composable
private fun DevelopmentCountdownSection(
    tripStatus: RoomDetailBeforeContract.TripStatus,
    daysUntilDevelopment: Int,
    developmentDateTime: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // フィルムアイコン
        Text(
            text = "\uD83C\uDFDE",
            fontSize = 64.sp,
            modifier = Modifier.padding(bottom = YoinSpacing.lg)
        )

        // ステータスに応じたメッセージ
        when (tripStatus) {
            RoomDetailBeforeContract.TripStatus.BEFORE_TRIP -> {
                Text(
                    text = "旅行が始まったら",
                    fontSize = 16.sp,
                    color = YoinColors.TextSecondary
                )
                Text(
                    text = "撮影できます",
                    fontSize = 16.sp,
                    color = YoinColors.TextSecondary
                )
            }
            RoomDetailBeforeContract.TripStatus.IN_PROGRESS,
            RoomDetailBeforeContract.TripStatus.LIMIT_REACHED -> {
                Text(
                    text = "現像まであと",
                    fontSize = 16.sp,
                    color = YoinColors.TextSecondary
                )
                Spacer(modifier = Modifier.height(YoinSpacing.sm))
                Text(
                    text = "${daysUntilDevelopment}日",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.Primary
                )
                Spacer(modifier = Modifier.height(YoinSpacing.sm))
                Text(
                    text = developmentDateTime,
                    fontSize = 14.sp,
                    color = YoinColors.TextSecondary
                )
            }
            RoomDetailBeforeContract.TripStatus.TRIP_ENDED -> {
                Text(
                    text = "現像をお楽しみに!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.Primary
                )
                Spacer(modifier = Modifier.height(YoinSpacing.sm))
                Text(
                    text = developmentDateTime,
                    fontSize = 14.sp,
                    color = YoinColors.TextSecondary
                )
            }
        }
    }
}

/**
 * 撮影進捗カード
 */
@Composable
private fun PhotoProgressCard(
    tripStatus: RoomDetailBeforeContract.TripStatus,
    todayPhotoCount: Int,
    dailyPhotoLimit: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (dailyPhotoLimit > 0) {
        todayPhotoCount.toFloat() / dailyPhotoLimit.toFloat()
    } else {
        0f
    }
    val remainingPhotos = (dailyPhotoLimit - todayPhotoCount).coerceAtLeast(0)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = YoinColors.Surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(YoinSpacing.lg)
        ) {
            // セクションタイトル
            Text(
                text = "今日の撮影",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = YoinColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(YoinSpacing.md))

            // カウント表示
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = null,
                    tint = YoinColors.Primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(YoinSpacing.sm))
                Text(
                    text = "$todayPhotoCount",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )
                Text(
                    text = " / $dailyPhotoLimit 枚",
                    fontSize = 18.sp,
                    color = YoinColors.TextSecondary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(YoinSpacing.md))

            // プログレスバー（アンバー色）
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = YoinColors.Primary,
                trackColor = YoinColors.SurfaceVariant,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(YoinSpacing.md))

            // ステータスメッセージ
            val statusMessage = when (tripStatus) {
                RoomDetailBeforeContract.TripStatus.BEFORE_TRIP -> "旅行が始まったら撮影できます"
                RoomDetailBeforeContract.TripStatus.IN_PROGRESS -> "残り${remainingPhotos}枚撮影できます"
                RoomDetailBeforeContract.TripStatus.LIMIT_REACHED -> "本日の撮影は終了です。また明日!"
                RoomDetailBeforeContract.TripStatus.TRIP_ENDED -> "旅行は終了しました"
            }

            Text(
                text = statusMessage,
                fontSize = 14.sp,
                color = YoinColors.TextSecondary
            )
        }
    }
}

/**
 * プレビュー
 */
@PhonePreview
@Composable
private fun RoomDetailBeforeScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "RoomDetailBefore Screen Preview",
                color = YoinColors.TextPrimary
            )
        }
    }
}
