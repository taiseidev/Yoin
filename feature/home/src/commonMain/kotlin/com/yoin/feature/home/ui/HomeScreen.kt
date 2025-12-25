package com.yoin.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CameraRoll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoCamera
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
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.design.theme.YoinSizes
import com.yoin.core.design.theme.YoinFontSizes
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
                        start = YoinSpacing.lg,
                        end = YoinSpacing.lg,
                        top = YoinSpacing.xxxl,
                        bottom = 96.dp // ボトムナビゲーションバーのスペース確保
                    ),
                    verticalArrangement = Arrangement.spacedBy(YoinSpacing.xxxl)
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
            Spacer(modifier = Modifier.height(YoinSpacing.lg))

            // ヘッダーコンテンツ
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = YoinSpacing.lg, vertical = YoinSpacing.sm + YoinSpacing.xs),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ロゴ
                Text(
                    text = "Yoin.",
                    fontSize = YoinFontSizes.displaySmall.value.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextSecondary,
                    letterSpacing = 0.07.sp
                )

                // 通知アイコン
                Box(
                    modifier = Modifier
                        .size(YoinSizes.iconXLarge)
                        .background(YoinColors.Background, RoundedCornerShape(YoinSpacing.sm + YoinSpacing.xs))
                        .clickable(onClick = onNotificationClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "Notifications",
                        tint = YoinColors.Primary,
                        modifier = Modifier.size(YoinSizes.iconMedium)
                    )

                    // 通知バッジ
                    if (hasNotification) {
                        Box(
                            modifier = Modifier
                                .size(YoinSizes.indicatorSmall)
                                .offset(x = YoinSpacing.sm, y = (-YoinSpacing.sm))
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
        verticalArrangement = Arrangement.spacedBy(YoinSpacing.lg)
    ) {
        // セクションヘッダー
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = YoinFontSizes.bodyMedium.value.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary,
                letterSpacing = (-0.31).sp
            )

            // すべてボタン
            Row(
                modifier = Modifier.clickable(onClick = onViewAllClick),
                horizontalArrangement = Arrangement.spacedBy(YoinSpacing.xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "すべて",
                    fontSize = YoinFontSizes.labelLarge.value.sp,
                    color = YoinColors.TextSecondary,
                    letterSpacing = (-0.15).sp
                )
                Text(
                    text = "›",
                    fontSize = YoinFontSizes.bodyMedium.value.sp,
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
        shape = RoundedCornerShape(YoinSpacing.md),
        border = BorderStroke(0.65.dp, YoinColors.SurfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(YoinSpacing.lg + 1.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(YoinSpacing.md)
            ) {
                // 絵文字
                Text(
                    text = trip.emoji,
                    fontSize = YoinFontSizes.displayMedium.value.sp,
                    modifier = Modifier.size(YoinSizes.iconLarge)
                )

                // メイン情報
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(YoinSpacing.xs)
                ) {
                    // タイトル
                    Text(
                        text = trip.title,
                        fontSize = YoinFontSizes.bodyMedium.value.sp,
                        color = YoinColors.TextPrimary,
                        letterSpacing = (-0.31).sp
                    )

                    // 日付・場所
                    Text(
                        text = "${trip.dateRange} • ${trip.location}",
                        fontSize = YoinFontSizes.labelSmall.value.sp,
                        color = YoinColors.TextSecondary
                    )

                    // プログレスバー（進行中の場合）
                    if (trip.progress != null) {
                        Spacer(modifier = Modifier.height(YoinSpacing.xs))
                        LinearProgressIndicator(
                            progress = { trip.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(YoinSizes.indicatorSmall)
                                .clip(RoundedCornerShape(100.dp)),
                            color = YoinColors.Primary,
                            trackColor = YoinColors.SurfaceVariant
                        )
                    }

                    // 残り日数または写真枚数
                    trip.daysUntilDevelopment?.let { days ->
                        Spacer(modifier = Modifier.height(YoinSpacing.xs))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(YoinSpacing.xs),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccessTime,
                                contentDescription = "Development time",
                                tint = YoinColors.Primary,
                                modifier = Modifier.size(YoinSizes.iconSmall)
                            )
                            Text(
                                text = "残り${days}日で現像",
                                fontSize = YoinFontSizes.labelSmall.value.sp,
                                color = YoinColors.Primary
                            )
                        }
                    }

                    trip.photoCount?.let { count ->
                        Spacer(modifier = Modifier.height(YoinSpacing.xs))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(YoinSpacing.xs),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CameraRoll,
                                contentDescription = "Photo count",
                                tint = YoinColors.Primary,
                                modifier = Modifier.size(YoinSizes.iconSmall)
                            )
                            Text(
                                text = "${count}枚の思い出",
                                fontSize = YoinFontSizes.labelSmall.value.sp,
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
        horizontalArrangement = Arrangement.spacedBy((-YoinSpacing.sm))
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
                    fontSize = YoinFontSizes.caption.value.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}
