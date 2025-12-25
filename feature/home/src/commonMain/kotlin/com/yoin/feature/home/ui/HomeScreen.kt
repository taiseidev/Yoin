package com.yoin.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.feature.home.viewmodel.HomeContract
import com.yoin.feature.home.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * ホーム画面
 *
 * @param viewModel HomeViewModel
 * @param onNavigateToTripDetail 旅行詳細画面への遷移コールバック
 * @param onNavigateToNotifications 通知画面への遷移コールバック
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToTripDetail: (String) -> Unit = {},
    onNavigateToNotifications: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is HomeContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is HomeContract.Effect.NavigateToTripDetail -> {
                    onNavigateToTripDetail(effect.tripId)
                }
                is HomeContract.Effect.NavigateToTripList -> {
                    // TODO: 旅行リスト画面への遷移
                }
                is HomeContract.Effect.NavigateToNotifications -> {
                    onNavigateToNotifications()
                }
            }
        }
    }

    // 画面表示時の初期化
    LaunchedEffect(Unit) {
        viewModel.onIntent(HomeContract.Intent.OnScreenDisplayed)
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
            HomeHeader(
                hasNotification = state.hasNotification,
                onNotificationClick = {
                    viewModel.onIntent(HomeContract.Intent.OnNotificationTapped)
                }
            )

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = YoinColors.Primary)
                }
            } else {
                // メインコンテンツ
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 32.dp,
                        bottom = 96.dp // ボトムナビゲーションバーのスペース確保
                    ),
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    // 進行中の旅セクション
                    if (state.ongoingTrips.isNotEmpty()) {
                        item {
                            TripSection(
                                title = "進行中の旅",
                                trips = state.ongoingTrips,
                                onViewAllClick = {
                                    viewModel.onIntent(
                                        HomeContract.Intent.OnViewAllTapped(
                                            HomeContract.TripSection.ONGOING
                                        )
                                    )
                                },
                                onTripClick = { tripId ->
                                    viewModel.onIntent(HomeContract.Intent.OnTripTapped(tripId))
                                }
                            )
                        }
                    }

                    // 現像済みセクション
                    if (state.completedTrips.isNotEmpty()) {
                        item {
                            TripSection(
                                title = "現像済み",
                                trips = state.completedTrips,
                                onViewAllClick = {
                                    viewModel.onIntent(
                                        HomeContract.Intent.OnViewAllTapped(
                                            HomeContract.TripSection.COMPLETED
                                        )
                                    )
                                },
                                onTripClick = { tripId ->
                                    viewModel.onIntent(HomeContract.Intent.OnTripTapped(tripId))
                                }
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
 * ホーム画面ヘッダー
 */
@Composable
private fun HomeHeader(
    hasNotification: Boolean,
    onNotificationClick: () -> Unit
) {
    Surface(
        color = YoinColors.Surface,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // ステータスバー領域（9:41表示）
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "9:41",
                    fontSize = 14.sp,
                    color = YoinColors.TextPrimary,
                    letterSpacing = (-0.15).sp
                )
            }

            // ヘッダーコンテンツ
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ロゴ
                Text(
                    text = "Yoin.",
                    fontSize = 24.sp,
                    fontStyle = FontStyle.Italic,
                    color = YoinColors.TextSecondary,
                    letterSpacing = 0.07.sp
                )

                // 通知アイコン
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(YoinColors.Background, RoundedCornerShape(10.dp))
                        .clickable(onClick = onNotificationClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔔",
                        fontSize = 20.sp
                    )

                    // 通知バッジ
                    if (hasNotification) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .offset(x = 8.dp, y = (-8).dp)
                                .background(YoinColors.Error, CircleShape)
                                .align(Alignment.TopEnd)
                        )
                    }
                }
            }

            // 区切り線
            HorizontalDivider(
                color = YoinColors.SurfaceVariant,
                thickness = 0.65.dp
            )
        }
    }
}

/**
 * 旅行セクション
 */
@Composable
private fun TripSection(
    title: String,
    trips: List<HomeContract.TripItem>,
    onViewAllClick: () -> Unit,
    onTripClick: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // セクションヘッダー
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                color = YoinColors.TextPrimary,
                letterSpacing = (-0.31).sp
            )

            // すべてボタン
            Row(
                modifier = Modifier.clickable(onClick = onViewAllClick),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "すべて",
                    fontSize = 14.sp,
                    color = YoinColors.TextSecondary,
                    letterSpacing = (-0.15).sp
                )
                Text(
                    text = "›",
                    fontSize = 16.sp,
                    color = YoinColors.TextSecondary
                )
            }
        }

        // 旅行カードリスト
        trips.forEach { trip ->
            TripCard(
                trip = trip,
                onClick = { onTripClick(trip.id) }
            )
        }
    }
}

/**
 * 旅行カード
 */
@Composable
private fun TripCard(
    trip: HomeContract.TripItem,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = YoinColors.Surface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(0.65.dp, YoinColors.SurfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(17.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 絵文字
                Text(
                    text = trip.emoji,
                    fontSize = 32.sp,
                    modifier = Modifier.size(32.dp)
                )

                // メイン情報
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // タイトル
                    Text(
                        text = trip.title,
                        fontSize = 16.sp,
                        color = YoinColors.TextPrimary,
                        letterSpacing = (-0.31).sp
                    )

                    // 日付・場所
                    Text(
                        text = "${trip.dateRange} • ${trip.location}",
                        fontSize = 12.sp,
                        color = YoinColors.TextSecondary
                    )

                    // プログレスバー（進行中の場合）
                    if (trip.progress != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { trip.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(100.dp)),
                            color = YoinColors.Primary,
                            trackColor = YoinColors.SurfaceVariant
                        )
                    }

                    // 残り日数または写真枚数
                    trip.daysUntilDevelopment?.let { days ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "残り${days}日で現像 📸",
                                fontSize = 12.sp,
                                color = YoinColors.Primary
                            )
                        }
                    }

                    trip.photoCount?.let { count ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🎞",
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${count}枚の思い出",
                                fontSize = 12.sp,
                                color = YoinColors.Primary
                            )
                        }
                    }
                }

                // メンバーアバター（進行中の場合）
                if (trip.additionalMemberCount > 0) {
                    MemberAvatars(
                        avatars = trip.memberAvatars,
                        additionalCount = trip.additionalMemberCount
                    )
                }
            }
        }
    }
}

/**
 * メンバーアバター
 */
@Composable
private fun MemberAvatars(
    avatars: List<String>,
    additionalCount: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy((-8).dp)
    ) {
        // アバター画像（最大3つ）
        avatars.take(3).forEachIndexed { index, _ ->
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .border(2.dp, YoinColors.Surface, CircleShape)
                    .background(
                        when (index) {
                            0 -> YoinColors.AccentPeach
                            1 -> YoinColors.AccentCoral
                            else -> YoinColors.Primary
                        },
                        CircleShape
                    )
            )
        }

        // +N表示
        if (additionalCount > 0) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .border(2.dp, YoinColors.Surface, CircleShape)
                    .background(YoinColors.Primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+$additionalCount",
                    fontSize = 10.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}
