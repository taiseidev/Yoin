package com.yoin.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CameraRoll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.design.theme.YoinSizes
import com.yoin.core.ui.preview.PhonePreview
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
                        top = 16.dp,
                        bottom = 100.dp // ボトムナビゲーションバーのスペース確保
                    ),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // 進行中の旅セクション
                    if (state.ongoingTrips.isNotEmpty()) {
                        item {
                            SectionHeader(title = "進行中の旅")
                        }
                        items(state.ongoingTrips) { trip ->
                            TripCard(
                                trip = trip,
                                onClick = {
                                    viewModel.onIntent(HomeContract.Intent.OnTripTapped(trip.id))
                                }
                            )
                        }
                    }

                    // 現像済みセクション
                    if (state.completedTrips.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            SectionHeader(title = "現像済み")
                        }
                        items(state.completedTrips) { trip ->
                            TripCard(
                                trip = trip,
                                onClick = {
                                    viewModel.onIntent(HomeContract.Intent.OnTripTapped(trip.id))
                                }
                            )
                        }
                    }

                    // 空状態
                    if (state.ongoingTrips.isEmpty() && state.completedTrips.isEmpty()) {
                        item {
                            EmptyState()
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ロゴ
        Text(
            text = "Yoin",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = YoinColors.TextPrimary
        )

        // 通知アイコン
        Box {
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notifications",
                    tint = YoinColors.TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }

            // 通知バッジ
            if (hasNotification) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .offset(x = 26.dp, y = 8.dp)
                        .background(YoinColors.AccentCoral, CircleShape)
                )
            }
        }
    }
}

/**
 * セクションヘッダー
 */
@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = YoinColors.TextPrimary
    )
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
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 絵文字アイコン
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(YoinColors.SurfaceVariant, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = trip.emoji,
                    fontSize = 24.sp
                )
            }

            // メイン情報
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // タイトル
                Text(
                    text = trip.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = YoinColors.TextPrimary
                )

                // 日付・場所
                Text(
                    text = "${trip.dateRange} • ${trip.location}",
                    fontSize = 13.sp,
                    color = YoinColors.TextSecondary
                )

                // プログレスバー（進行中の場合）
                trip.progress?.let { progress ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(100.dp)),
                            color = YoinColors.Primary,
                            trackColor = YoinColors.SurfaceVariant
                        )
                    }
                }

                // 残り日数
                trip.daysUntilDevelopment?.let { days ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccessTime,
                            contentDescription = null,
                            tint = YoinColors.Primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "残り${days}日で現像",
                            fontSize = 12.sp,
                            color = YoinColors.Primary
                        )
                    }
                }

                // 写真枚数（完了済みの場合）
                trip.photoCount?.let { count ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CameraRoll,
                            contentDescription = null,
                            tint = YoinColors.Primary,
                            modifier = Modifier.size(14.dp)
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
                    count = trip.additionalMemberCount
                )
            }
        }
    }
}

/**
 * メンバーアバター
 */
@Composable
private fun MemberAvatars(count: Int) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(YoinColors.Primary, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "+$count",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

/**
 * 空状態
 */
@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.CameraRoll,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = YoinColors.TextSecondary.copy(alpha = 0.5f)
        )
        Text(
            text = "旅はまだありません",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = YoinColors.TextPrimary
        )
        Text(
            text = "新しい旅を作成してみましょう",
            fontSize = 14.sp,
            color = YoinColors.TextSecondary
        )
    }
}

/**
 * プレビュー: ホーム画面
 */
@PhonePreview
@Composable
private fun HomeScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    SectionHeader(title = "進行中の旅")
                }

                item {
                    TripCard(
                        trip = HomeContract.TripItem(
                            id = "1",
                            emoji = "🏔️",
                            title = "北海道旅行2025",
                            dateRange = "7/1〜7/5",
                            location = "札幌",
                            progress = 0.6f,
                            daysUntilDevelopment = 3,
                            memberAvatars = emptyList(),
                            additionalMemberCount = 3
                        ),
                        onClick = {}
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    SectionHeader(title = "現像済み")
                }

                item {
                    TripCard(
                        trip = HomeContract.TripItem(
                            id = "2",
                            emoji = "🏖️",
                            title = "沖縄旅行2025",
                            dateRange = "5/1〜5/4",
                            location = "沖縄",
                            photoCount = 48
                        ),
                        onClick = {}
                    )
                }
            }
        }
    }
}
