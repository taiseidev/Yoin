package com.yoin.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraRoll
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
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
                        .weight(1f)
                        .windowInsetsPadding(WindowInsets.navigationBars),
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
 * ホーム画面ヘッダー - Modern Cinematic Design
 */
@Composable
private fun HomeHeader(
    hasNotification: Boolean,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ロゴとサブタイトル
        Column {
            Text(
                text = "Yoin.",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = "余韻を残す旅の記録",
                fontSize = 12.sp,
                color = YoinColors.TextSecondary,
                fontStyle = FontStyle.Italic
            )
        }

        // 通知アイコン
        Box {
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier
                    .size(44.dp)
                    .background(YoinColors.Surface, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notifications",
                    tint = YoinColors.TextPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            // 通知バッジ
            if (hasNotification) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .offset(x = 30.dp, y = 8.dp)
                        .background(YoinColors.Primary, CircleShape)
                        .border(2.dp, YoinColors.Background, CircleShape)
                )
            }
        }
    }
}

/**
 * セクションヘッダー - Modern Cinematic Design
 */
@Composable
private fun SectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = YoinColors.TextPrimary,
            letterSpacing = (-0.3).sp
        )

        // アクセントライン
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            YoinColors.Primary.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

/**
 * 旅行カード - Modern Cinematic Design with Photo Preview
 */
@Composable
private fun TripCard(
    trip: HomeContract.TripItem,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(YoinColors.Surface)
            .clickable(onClick = onClick)
    ) {
        // 写真プレビューエリア（実際の画像 or グラデーション背景）
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            // 実際の写真がある場合は表示、なければグラデーション
            val imageUrl = getTripImageUrl(trip.id)
            if (imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = trip.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    getTripGradientColor(trip.id).first,
                                    getTripGradientColor(trip.id).second
                                )
                            )
                        )
                )
            }

            // グラデーションオーバーレイ（写真を暗くして文字を読みやすく）
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.3f),
                                YoinColors.Surface.copy(alpha = 0.85f)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )

            // 絵文字アイコン（写真がない場合のみ表示）
            if (imageUrl.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = trip.emoji,
                        fontSize = 64.sp,
                        modifier = Modifier.offset(y = (-20).dp)
                    )
                }
            }

            // ステータスバッジ（進行中 or 完了）
            if (trip.progress != null) {
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                        .background(YoinColors.Primary, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "進行中",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            } else if (trip.photoCount != null) {
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                        .background(YoinColors.AccentCopper, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "現像済み",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // カード情報エリア
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // タイトルと場所
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = trip.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = YoinColors.TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = trip.location,
                            fontSize = 13.sp,
                            color = YoinColors.TextSecondary
                        )
                    }
                }

                // メンバーアバター
                if (trip.additionalMemberCount > 0) {
                    MemberAvatars(count = trip.additionalMemberCount)
                }
            }

            // 日付
            Text(
                text = trip.dateRange,
                fontSize = 13.sp,
                color = YoinColors.TextSecondary,
                fontWeight = FontWeight.Medium
            )

            // プログレスまたは写真情報
            trip.progress?.let { progress ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // プログレスバー
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(100.dp)),
                        color = YoinColors.Primary,
                        trackColor = YoinColors.SurfaceVariant
                    )

                    // 残り日数
                    trip.daysUntilDevelopment?.let { days ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AccessTime,
                                    contentDescription = null,
                                    tint = YoinColors.Primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "現像まで",
                                    fontSize = 13.sp,
                                    color = YoinColors.TextSecondary
                                )
                            }
                            Text(
                                text = "残り${days}日",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = YoinColors.Primary
                            )
                        }
                    }
                }
            }

            // 写真枚数（完了済みの場合）
            trip.photoCount?.let { count ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(YoinColors.SurfaceVariant, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Photo,
                            contentDescription = null,
                            tint = YoinColors.Primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "${count}枚の思い出",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = YoinColors.TextPrimary
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = YoinColors.TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * 旅行IDに基づいてグラデーション色を取得
 */
private fun getTripGradientColor(tripId: String): Pair<Color, Color> {
    val gradients = listOf(
        Pair(Color(0xFFFF6B35), Color(0xFFE85A24)), // Sunset Amber
        Pair(Color(0xFFD4886C), Color(0xFFB87F6A)), // Copper/Sepia
        Pair(Color(0xFFE8A598), Color(0xFFD4886C)), // Rose Gold/Copper
        Pair(Color(0xFF2C2C2E), Color(0xFF48484A)), // Dark Gray
        Pair(Color(0xFF34C759), Color(0xFF248A3D)), // Forest Green
        Pair(Color(0xFF007AFF), Color(0xFF0051D5))  // Ocean Blue
    )
    val index = tripId.hashCode().mod(gradients.size).let { if (it < 0) it + gradients.size else it }
    return gradients[index]
}

/**
 * 旅行IDに基づいて実際の画像URLを取得（Unsplash - 日本の風景）
 */
private fun getTripImageUrl(tripId: String): String {
    // 日本の美しい風景写真（Unsplash）
    val images = listOf(
        "https://images.unsplash.com/photo-1545569341-9eb8b30979d9?w=800", // 金閣寺（京都）
        "https://images.unsplash.com/photo-1542640244-7e672d6cef4e?w=800", // 富士山と桜
        "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?w=800", // 渋谷スクランブル交差点
        "https://images.unsplash.com/photo-1480796927426-f609979314bd?w=800", // 東京タワー夜景
        "https://images.unsplash.com/photo-1528164344705-47542687000d?w=800", // 姫路城
        "https://images.unsplash.com/photo-1513407030348-c983a97b98d8?w=800", // 竹林の道（嵐山）
        "https://images.unsplash.com/photo-1490806843957-31f4c9a91c65?w=800", // 東京都庁夜景
        "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?w=800", // 厳島神社（広島）
        "https://images.unsplash.com/photo-1492571350019-22de08371fd3?w=800", // 浅草寺（東京）
        "https://images.unsplash.com/photo-1524413840807-0c3cb6fa808d?w=800", // 札幌雪まつり
        "https://images.unsplash.com/photo-1478436127897-769e1b3f0f36?w=800", // 奈良公園の鹿
        "https://images.unsplash.com/photo-1481043801543-e0618e7d24af?w=800"  // 大阪城
    )
    val index = tripId.hashCode().mod(images.size).let { if (it < 0) it + images.size else it }
    return images[index]
}

/**
 * メンバーアバター - Modern Design
 */
@Composable
private fun MemberAvatars(count: Int) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        YoinColors.Primary,
                        YoinColors.AccentCopper
                    )
                ),
                CircleShape
            )
            .border(2.dp, YoinColors.Surface, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "+$count",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

/**
 * 空状態 - Modern Cinematic Design
 */
@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 80.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // アイコン背景
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                YoinColors.Primary.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CameraRoll,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = YoinColors.Primary.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "旅はまだありません",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary,
                letterSpacing = (-0.3).sp
            )

            Text(
                text = "新しい旅を作成して\n思い出を記録しましょう",
                fontSize = 14.sp,
                color = YoinColors.TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // CTAボタン
            Button(
                onClick = { /* TODO: 旅行作成 */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = YoinColors.Primary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "旅を作成",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
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
