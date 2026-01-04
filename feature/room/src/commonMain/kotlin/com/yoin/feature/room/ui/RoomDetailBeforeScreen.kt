package com.yoin.feature.room.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.room.viewmodel.RoomDetailBeforeContract
import com.yoin.feature.room.viewmodel.RoomDetailBeforeViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * ルーム詳細（現像前）画面 - Modern Cinematic with Amber Accent
 *
 * 旅行中にユーザーが最も頻繁に見る画面:
 * - 撮影進捗の確認
 * - 現像までのカウントダウン
 * - カメラへの導線
 *
 * デザイン原則:
 * - 純黒背景で写真を引き立てる
 * - アンバー色で「余韻」の温かみを表現
 * - 高コントラストで視認性を確保
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomDetailBeforeScreen(
    roomId: String,
    viewModel: RoomDetailBeforeViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToCamera: (String) -> Unit = {},
    onNavigateToSettings: (String) -> Unit = {},
    onNavigateToInvite: (String) -> Unit = {},
    onNavigateToMembers: (String) -> Unit = {},
    onNavigateToMap: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is RoomDetailBeforeContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is RoomDetailBeforeContract.Effect.NavigateBack -> {
                    onNavigateBack()
                }
                is RoomDetailBeforeContract.Effect.NavigateToInvite -> {
                    onNavigateToInvite(effect.roomId)
                }
                is RoomDetailBeforeContract.Effect.NavigateToMembers -> {
                    onNavigateToMembers(effect.roomId)
                }
                is RoomDetailBeforeContract.Effect.NavigateToCamera -> {
                    onNavigateToCamera(effect.roomId)
                }
                is RoomDetailBeforeContract.Effect.NavigateToSettings -> {
                    onNavigateToSettings(effect.roomId)
                }
                is RoomDetailBeforeContract.Effect.NavigateToMap -> {
                    onNavigateToMap(effect.roomId)
                }
            }
        }
    }

    // 画面表示時の初期化
    LaunchedEffect(roomId) {
        viewModel.onIntent(RoomDetailBeforeContract.Intent.OnScreenDisplayed(roomId))
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = YoinColors.Background,
        floatingActionButton = {
            state.roomDetail?.let { detail ->
                CameraFab(
                    canTakePhoto = detail.canTakePhoto,
                    onClick = {
                        viewModel.onIntent(RoomDetailBeforeContract.Intent.OnCameraPressed)
                    }
                )
            }
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = {
                viewModel.onIntent(RoomDetailBeforeContract.Intent.OnRefresh)
            },
            modifier = Modifier.padding(paddingValues)
        ) {
            if (state.isLoading && state.roomDetail == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = YoinColors.Primary)
                }
            } else {
                state.roomDetail?.let { detail ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // ヘッダー
                        RoomHeader(
                            detail = detail,
                            onBackPressed = {
                                viewModel.onIntent(RoomDetailBeforeContract.Intent.OnBackPressed)
                            },
                            onInvitePressed = {
                                viewModel.onIntent(RoomDetailBeforeContract.Intent.OnInvitePressed)
                            },
                            onSettingsPressed = {
                                viewModel.onIntent(RoomDetailBeforeContract.Intent.OnSettingsPressed)
                            }
                        )

                        // メンバーバー
                        MemberBar(
                            members = detail.members,
                            onMembersClick = {
                                viewModel.onIntent(RoomDetailBeforeContract.Intent.OnMembersPressed)
                            }
                        )

                        Spacer(modifier = Modifier.height(YoinSpacing.xl))

                        // カウントダウンセクション
                        CountdownSection(
                            daysUntil = detail.daysUntilDevelopment,
                            developmentDateTime = detail.developmentDateTime,
                            roomStatus = detail.roomStatus
                        )

                        Spacer(modifier = Modifier.height(YoinSpacing.lg))

                        // 撮影進捗セクション
                        PhotoProgressSection(
                            todayPhotos = detail.todayPhotos,
                            maxPhotos = detail.maxPhotos,
                            progress = detail.photoProgress,
                            remainingPhotos = detail.remainingPhotos,
                            roomStatus = detail.roomStatus
                        )

                        // FABのスペース
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

/**
 * ルームヘッダー - Modern Cinematic Design
 */
@Composable
private fun RoomHeader(
    detail: RoomDetailBeforeContract.RoomDetail,
    onBackPressed: () -> Unit,
    onInvitePressed: () -> Unit,
    onSettingsPressed: () -> Unit
) {
    Surface(
        color = YoinColors.Surface,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 戻るボタン
                IconButton(
                    onClick = onBackPressed,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "戻る",
                        tint = YoinColors.TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // タイトルと日付
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = detail.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.TextPrimary,
                        letterSpacing = (-0.3).sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${detail.dateRange} • ${detail.location}",
                        fontSize = 13.sp,
                        color = YoinColors.TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                // アクションボタン
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 設定ボタン
                    IconButton(
                        onClick = onSettingsPressed,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "設定",
                            tint = YoinColors.TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // 招待ボタン
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        YoinColors.Primary,
                                        YoinColors.PrimaryVariant
                                    )
                                )
                            )
                            .clickable(onClick = onInvitePressed)
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PersonAdd,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "招待",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * メンバーバー - 横スクロール対応
 */
@Composable
private fun MemberBar(
    members: List<RoomDetailBeforeContract.Member>,
    onMembersClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onMembersClick),
        color = YoinColors.Surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // アイコン
            Icon(
                imageVector = Icons.Filled.People,
                contentDescription = null,
                tint = YoinColors.Primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // メンバーチップ
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(end = 8.dp)
            ) {
                items(members.take(5)) { member ->
                    MemberChip(
                        name = member.name,
                        isCurrentUser = member.isCurrentUser
                    )
                }

                // 追加メンバー表示
                if (members.size > 5) {
                    item {
                        MemberChip(
                            name = "+${members.size - 5}",
                            isCurrentUser = false
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 矢印アイコン
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = YoinColors.TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * メンバーチップ
 */
@Composable
private fun MemberChip(
    name: String,
    isCurrentUser: Boolean
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isCurrentUser) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            YoinColors.Primary,
                            YoinColors.PrimaryVariant
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(
                            YoinColors.SurfaceVariant,
                            YoinColors.SurfaceVariant
                        )
                    )
                }
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = name,
            fontSize = 12.sp,
            fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.Medium,
            color = if (isCurrentUser) Color.White else YoinColors.TextPrimary
        )
    }
}

/**
 * カウントダウンセクション - アンバーアクセント
 */
@Composable
private fun CountdownSection(
    daysUntil: Int,
    developmentDateTime: String,
    roomStatus: RoomDetailBeforeContract.RoomStatus
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        color = YoinColors.Surface,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            YoinColors.Surface,
                            YoinColors.SurfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // フィルムアイコン
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    YoinColors.Primary.copy(alpha = 0.2f),
                                    YoinColors.PrimaryVariant.copy(alpha = 0.2f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = YoinColors.Primary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ステータスに応じたメッセージ
                when (roomStatus) {
                    RoomDetailBeforeContract.RoomStatus.UPCOMING -> {
                        Text(
                            text = "旅行開始まであと",
                            fontSize = 14.sp,
                            color = YoinColors.TextSecondary,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        )
                    }
                    RoomDetailBeforeContract.RoomStatus.ACTIVE,
                    RoomDetailBeforeContract.RoomStatus.PENDING_DEVELOPMENT -> {
                        Text(
                            text = "現像まであと",
                            fontSize = 14.sp,
                            color = YoinColors.TextSecondary,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        )
                    }
                    RoomDetailBeforeContract.RoomStatus.DEVELOPED -> {
                        Text(
                            text = "現像完了",
                            fontSize = 14.sp,
                            color = YoinColors.Primary,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 日数表示
                if (roomStatus != RoomDetailBeforeContract.RoomStatus.DEVELOPED) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = daysUntil.toString(),
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = YoinColors.Primary,
                            letterSpacing = (-1).sp
                        )
                        Text(
                            text = "日",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = YoinColors.Primary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 現像日時
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = null,
                            tint = YoinColors.TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = developmentDateTime,
                            fontSize = 13.sp,
                            color = YoinColors.TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

/**
 * 撮影進捗セクション - アンバープログレスバー
 */
@Composable
private fun PhotoProgressSection(
    todayPhotos: Int,
    maxPhotos: Int,
    progress: Float,
    remainingPhotos: Int,
    roomStatus: RoomDetailBeforeContract.RoomStatus
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = YoinColors.Surface,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // ヘッダー
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // カメラアイコン
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(YoinColors.Primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhotoCamera,
                        contentDescription = null,
                        tint = YoinColors.Primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "今日の撮影",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.TextPrimary,
                        letterSpacing = (-0.2).sp
                    )
                    Text(
                        text = when (roomStatus) {
                            RoomDetailBeforeContract.RoomStatus.UPCOMING ->
                                "旅行が始まったら撮影できます"
                            RoomDetailBeforeContract.RoomStatus.ACTIVE ->
                                if (remainingPhotos > 0) "残り${remainingPhotos}枚撮影できます"
                                else "本日の撮影は終了です"
                            RoomDetailBeforeContract.RoomStatus.PENDING_DEVELOPMENT ->
                                "現像をお楽しみに！"
                            RoomDetailBeforeContract.RoomStatus.DEVELOPED ->
                                "現像済みです"
                        },
                        fontSize = 12.sp,
                        color = YoinColors.TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                // 撮影枚数
                if (roomStatus == RoomDetailBeforeContract.RoomStatus.ACTIVE ||
                    roomStatus == RoomDetailBeforeContract.RoomStatus.PENDING_DEVELOPMENT) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = todayPhotos.toString(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = YoinColors.Primary,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "/ $maxPhotos",
                            fontSize = 16.sp,
                            color = YoinColors.TextSecondary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // プログレスバー（アンバー色）
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(YoinColors.SurfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    YoinColors.Primary,
                                    YoinColors.PrimaryVariant
                                )
                            )
                        )
                )
            }
        }
    }
}

/**
 * カメラFABボタン - 72dp円形、アンバー背景
 */
@Composable
private fun CameraFab(
    canTakePhoto: Boolean,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier
            .padding(bottom = 16.dp)
            .size(72.dp),
        containerColor = if (canTakePhoto) YoinColors.Primary else YoinColors.SurfaceVariant,
        contentColor = if (canTakePhoto) Color.White else YoinColors.TextSecondary,
        shape = CircleShape
    ) {
        Icon(
            imageVector = Icons.Filled.CameraAlt,
            contentDescription = "撮影",
            modifier = Modifier.size(32.dp)
        )
    }
}

/**
 * プレビュー: ルーム詳細画面（現像前）
 */
@PhonePreview
@Composable
private fun RoomDetailBeforeScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .verticalScroll(rememberScrollState())
            ) {
                // ダミーヘッダー
                Surface(
                    color = YoinColors.Surface,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = YoinColors.TextPrimary
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "北海道旅行2025",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = YoinColors.TextPrimary
                            )
                            Text(
                                text = "7/1〜7/5 • 札幌",
                                fontSize = 13.sp,
                                color = YoinColors.TextSecondary
                            )
                        }
                        Row {
                            IconButton(onClick = {}) {
                                Icon(
                                    imageVector = Icons.Filled.Settings,
                                    contentDescription = null,
                                    tint = YoinColors.TextPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(YoinSpacing.md))

                // メンバーバープレビュー
                MemberBar(
                    members = listOf(
                        RoomDetailBeforeContract.Member("1", "田中太郎", isCurrentUser = true),
                        RoomDetailBeforeContract.Member("2", "山田花子"),
                        RoomDetailBeforeContract.Member("3", "佐藤次郎"),
                        RoomDetailBeforeContract.Member("4", "鈴木さくら"),
                        RoomDetailBeforeContract.Member("5", "高橋健太"),
                        RoomDetailBeforeContract.Member("6", "伊藤美咲"),
                        RoomDetailBeforeContract.Member("7", "渡辺翔"),
                        RoomDetailBeforeContract.Member("8", "中村愛")
                    ),
                    onMembersClick = {}
                )

                Spacer(modifier = Modifier.height(YoinSpacing.xl))

                // カウントダウンセクションプレビュー
                CountdownSection(
                    daysUntil = 3,
                    developmentDateTime = "2025/7/6 AM9:00",
                    roomStatus = RoomDetailBeforeContract.RoomStatus.ACTIVE
                )

                Spacer(modifier = Modifier.height(YoinSpacing.lg))

                // 撮影進捗セクションプレビュー
                PhotoProgressSection(
                    todayPhotos = 12,
                    maxPhotos = 24,
                    progress = 0.5f,
                    remainingPhotos = 12,
                    roomStatus = RoomDetailBeforeContract.RoomStatus.ACTIVE
                )

                Spacer(modifier = Modifier.height(100.dp))
            }

            // FABプレビュー
            FloatingActionButton(
                onClick = {},
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .size(72.dp),
                containerColor = YoinColors.Primary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

/**
 * プレビュー: 旅行開始前の状態
 */
@PhonePreview
@Composable
private fun RoomDetailBeforeUpcomingPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
                .padding(16.dp)
        ) {
            Column {
                CountdownSection(
                    daysUntil = 5,
                    developmentDateTime = "2025/7/10 AM9:00",
                    roomStatus = RoomDetailBeforeContract.RoomStatus.UPCOMING
                )

                Spacer(modifier = Modifier.height(16.dp))

                PhotoProgressSection(
                    todayPhotos = 0,
                    maxPhotos = 24,
                    progress = 0f,
                    remainingPhotos = 24,
                    roomStatus = RoomDetailBeforeContract.RoomStatus.UPCOMING
                )
            }
        }
    }
}
