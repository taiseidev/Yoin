package com.yoin.feature.timeline.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.design.theme.YoinSizes
import com.yoin.core.design.theme.YoinFontSizes
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.timeline.viewmodel.TimelineContract
import com.yoin.feature.timeline.viewmodel.TimelineViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * タイムライン（アルバム）画面 - マガジンレイアウト
 *
 * 革新的な写真中心のデザイン:
 * - マガジンスタイルのレイアウト（様々なサイズの写真）
 * - 日付ヘッダーで時系列グループ化
 * - ミニマルなUI、写真にフォーカス
 * - 大きなスペーシングで視認性向上
 * - フルワイドヒーロー、2列、3列の混合レイアウト
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    viewModel: TimelineViewModel,
    onNavigateToPhotoDetail: (photoId: String, roomId: String) -> Unit = { _, _ -> }
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is TimelineContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }

                is TimelineContract.Effect.NavigateToPhotoDetail -> {
                    onNavigateToPhotoDetail(effect.photoId, effect.roomId)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onIntent(TimelineContract.Intent.OnScreenDisplayed)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = YoinColors.Background
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = {
                viewModel.onIntent(TimelineContract.Intent.OnRefresh)
            },
            modifier = Modifier.padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // ミニマルヘッダー
                MinimalHeader(
                    searchQuery = state.searchQuery,
                    onSearchChange = { query ->
                        viewModel.onIntent(TimelineContract.Intent.OnSearch(query))
                    }
                )

                // シンプルタブ
                SimpleTabs(
                    selectedTab = state.selectedTab,
                    onTabChange = { tab ->
                        viewModel.onIntent(TimelineContract.Intent.OnTabChange(tab))
                    }
                )

                // 旅行リスト（旅行別タブの時のみ）
                // お気に入りタブは旅行区分けなしでフラット表示
                if (state.selectedTab == TimelineContract.AlbumTab.BY_TRIP) {
                    TripList(
                        trips = state.trips,
                        selectedTripId = state.selectedTrip,
                        onTripSelect = { tripId ->
                            viewModel.onIntent(TimelineContract.Intent.OnTripSelect(tripId))
                        }
                    )
                } else if (state.selectedTab == TimelineContract.AlbumTab.FAVORITES) {
                    // お気に入りタブ用のヘッダー
                    FavoritesHeader()
                }

                // マガジンレイアウト
                if (state.isLoading && state.photos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(YoinSpacing.xxxl),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = YoinColors.Primary)
                    }
                } else if (state.photos.isEmpty()) {
                    EmptyState(selectedTab = state.selectedTab)
                } else {
                    MagazineLayout(
                        photos = state.photos,
                        onPhotoClick = { photoId ->
                            viewModel.onIntent(TimelineContract.Intent.OnPhotoClick(photoId))
                        },
                        onToggleFavorite = { photoId ->
                            viewModel.onIntent(TimelineContract.Intent.OnToggleFavorite(photoId))
                        }
                    )
                }
            }
        }
    }
}

/**
 * ミニマルヘッダー（検索のみ）
 */
@Composable
private fun MinimalHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(YoinColors.Surface)
            .padding(horizontal = YoinSpacing.lg, vertical = YoinSpacing.md)
    ) {
        Text(
            text = "アルバム",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = YoinColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(YoinSpacing.sm))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("検索", color = YoinColors.TextSecondary, fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "検索",
                    tint = YoinColors.TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = YoinColors.Background,
                unfocusedContainerColor = YoinColors.Background,
                focusedBorderColor = YoinColors.Primary,
                unfocusedBorderColor = Color.Transparent
            )
        )
    }
}

/**
 * シンプルタブ
 */
@Composable
private fun SimpleTabs(
    selectedTab: TimelineContract.AlbumTab,
    onTabChange: (TimelineContract.AlbumTab) -> Unit
) {
    val tabs = TimelineContract.AlbumTab.entries
    val selectedIndex = tabs.indexOf(selectedTab)

    TabRow(
        selectedTabIndex = selectedIndex,
        containerColor = YoinColors.Surface,
        contentColor = YoinColors.Primary,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                height = 2.dp,
                color = YoinColors.Primary
            )
        },
        divider = {}
    ) {
        tabs.forEach { tab ->
            Tab(
                selected = tab == selectedTab,
                onClick = { onTabChange(tab) },
                text = {
                    Text(
                        text = when (tab) {
                            TimelineContract.AlbumTab.ALL -> "すべて"
                            TimelineContract.AlbumTab.BY_TRIP -> "旅行別"
                            TimelineContract.AlbumTab.FAVORITES -> "お気に入り"
                        },
                        fontSize = 14.sp,
                        fontWeight = if (tab == selectedTab) FontWeight.Bold else FontWeight.Normal
                    )
                },
                selectedContentColor = YoinColors.Primary,
                unselectedContentColor = YoinColors.TextSecondary
            )
        }
    }
}

/**
 * お気に入りタブ用のヘッダー
 */
@Composable
private fun FavoritesHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(YoinColors.Background)
            .padding(horizontal = YoinSpacing.lg, vertical = YoinSpacing.md)
    ) {
        Text(
            text = "特別な瞬間",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = YoinColors.Primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "あなたがお気に入りした写真",
            fontSize = 12.sp,
            color = YoinColors.TextSecondary
        )
    }
}

/**
 * 旅行リスト（横スクロール）
 */
@Composable
private fun TripList(
    trips: List<TimelineContract.Trip>,
    selectedTripId: String?,
    onTripSelect: (String?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(YoinColors.Background)
            .padding(vertical = YoinSpacing.md)
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = YoinSpacing.lg),
            horizontalArrangement = Arrangement.spacedBy(YoinSpacing.md)
        ) {
            // すべて表示
            item {
                TripCard(
                    trip = null,
                    isSelected = selectedTripId == null,
                    onClick = { onTripSelect(null) }
                )
            }

            items(trips) { trip ->
                TripCard(
                    trip = trip,
                    isSelected = trip.id == selectedTripId,
                    onClick = { onTripSelect(trip.id) }
                )
            }
        }
    }
}

/**
 * 旅行カード
 */
@Composable
private fun TripCard(
    trip: TimelineContract.Trip?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(120.dp)
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) YoinColors.Primary else YoinColors.Surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (trip == null) {
            // すべて表示カード
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "すべて",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else YoinColors.TextPrimary
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(YoinSpacing.md),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = trip.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else YoinColors.TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${trip.photoCount}枚",
                    fontSize = 10.sp,
                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else YoinColors.TextSecondary
                )
            }
        }
    }
}

/**
 * マガジンレイアウト（革新的な写真配置）
 *
 * レイアウトパターン:
 * - フルワイドヒーロー（1枚）
 * - 2列レイアウト（2枚並び）
 * - 3列グリッド（3枚並び）
 *
 * パターンを交互に配置して視覚的な変化を作る
 */
@Composable
private fun MagazineLayout(
    photos: List<TimelineContract.Photo>,
    onPhotoClick: (String) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(YoinColors.Background),
        contentPadding = PaddingValues(YoinSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(YoinSpacing.lg)
    ) {
        // 写真をグループ化してパターン配置
        val photoGroups = groupPhotosForMagazine(photos)

        photoGroups.forEach { group ->
            when (group) {
                is PhotoGroup.Hero -> {
                    item {
                        HeroPhoto(
                            photo = group.photo,
                            onPhotoClick = onPhotoClick,
                            onToggleFavorite = onToggleFavorite
                        )
                    }
                }
                is PhotoGroup.TwoColumn -> {
                    item {
                        TwoColumnPhotos(
                            photos = group.photos,
                            onPhotoClick = onPhotoClick,
                            onToggleFavorite = onToggleFavorite
                        )
                    }
                }
                is PhotoGroup.ThreeColumn -> {
                    item {
                        ThreeColumnPhotos(
                            photos = group.photos,
                            onPhotoClick = onPhotoClick,
                            onToggleFavorite = onToggleFavorite
                        )
                    }
                }
            }
        }
    }
}

/**
 * 写真グループ（レイアウトパターン）
 */
sealed class PhotoGroup {
    data class Hero(val photo: TimelineContract.Photo) : PhotoGroup()
    data class TwoColumn(val photos: List<TimelineContract.Photo>) : PhotoGroup()
    data class ThreeColumn(val photos: List<TimelineContract.Photo>) : PhotoGroup()
}

/**
 * 写真をマガジンレイアウト用にグループ化
 * パターン: Hero → 2列 → 3列 → Hero → ...
 */
private fun groupPhotosForMagazine(photos: List<TimelineContract.Photo>): List<PhotoGroup> {
    val groups = mutableListOf<PhotoGroup>()
    var index = 0

    while (index < photos.size) {
        when {
            // パターン1: ヒーロー写真（1枚）
            index % 6 == 0 && index < photos.size -> {
                groups.add(PhotoGroup.Hero(photos[index]))
                index++
            }
            // パターン2: 2列レイアウト（2枚）
            index % 6 in 1..2 && index + 1 < photos.size -> {
                groups.add(PhotoGroup.TwoColumn(photos.subList(index, minOf(index + 2, photos.size))))
                index += 2
            }
            // パターン3: 3列グリッド（3枚）
            index % 6 in 3..5 && index + 2 < photos.size -> {
                groups.add(PhotoGroup.ThreeColumn(photos.subList(index, minOf(index + 3, photos.size))))
                index += 3
            }
            // 残りの写真
            else -> {
                val remaining = photos.size - index
                when {
                    remaining == 1 -> {
                        groups.add(PhotoGroup.Hero(photos[index]))
                        index++
                    }
                    remaining == 2 -> {
                        groups.add(PhotoGroup.TwoColumn(photos.subList(index, index + 2)))
                        index += 2
                    }
                    else -> {
                        groups.add(PhotoGroup.ThreeColumn(photos.subList(index, minOf(index + 3, photos.size))))
                        index += 3
                    }
                }
            }
        }
    }

    return groups
}

/**
 * 写真IDに基づいてグラデーションカラーを取得
 * 様々な旅の雰囲気を表現する色彩
 */
private fun getPhotoGradient(photoId: String): Brush {
    val gradients = listOf(
        // 夕暮れの海
        Brush.verticalGradient(
            colors = listOf(Color(0xFFFF6B35), Color(0xFFE85A24))
        ),
        // 夜の街
        Brush.verticalGradient(
            colors = listOf(Color(0xFF2C2C2E), Color(0xFF48484A))
        ),
        // 森林
        Brush.verticalGradient(
            colors = listOf(Color(0xFF34C759), Color(0xFF248A3D))
        ),
        // 桜
        Brush.verticalGradient(
            colors = listOf(Color(0xFFE8A598), Color(0xFFD4886C))
        ),
        // 砂漠
        Brush.verticalGradient(
            colors = listOf(Color(0xFFFFB800), Color(0xFFD4886C))
        ),
        // 雪山
        Brush.verticalGradient(
            colors = listOf(Color(0xFFE6E1E5), Color(0xFF8E8E93))
        )
    )

    val index = photoId.hashCode().mod(gradients.size).let { if (it < 0) it + gradients.size else it }
    return gradients[index]
}

/**
 * ヒーロー写真（フルワイド、大きい）
 */
@Composable
private fun HeroPhoto(
    photo: TimelineContract.Photo,
    onPhotoClick: (String) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 場所ラベル（写真の外側に配置）
        Text(
            text = photo.location,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = YoinColors.TextSecondary,
            modifier = Modifier.padding(bottom = YoinSpacing.xs)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)  // ワイドな比率
                .clip(RoundedCornerShape(16.dp))
                .clickable { onPhotoClick(photo.id) }
        ) {
            // 実際の写真を表示（URLがあれば）
            if (photo.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = photo.imageUrl,
                    contentDescription = photo.location,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // フォールバック: グラデーション背景
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(getPhotoGradient(photo.id)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhotoCamera,
                        contentDescription = "写真",
                        tint = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            // お気に入りボタン
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(YoinSpacing.md),
                color = Color.Black.copy(alpha = 0.5f),
                shape = CircleShape
            ) {
                IconButton(
                    onClick = { onToggleFavorite(photo.id) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (photo.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "お気に入り",
                        tint = if (photo.isFavorite) Color(0xFFFF6B6B) else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * 2列写真
 */
@Composable
private fun TwoColumnPhotos(
    photos: List<TimelineContract.Photo>,
    onPhotoClick: (String) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(YoinSpacing.sm)
    ) {
        photos.take(2).forEach { photo ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(3f / 4f)  // 縦長の比率
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onPhotoClick(photo.id) }
            ) {
                // 実際の写真を表示（URLがあれば）
                if (photo.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = photo.imageUrl,
                        contentDescription = photo.location,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // フォールバック: グラデーション背景
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(getPhotoGradient(photo.id)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PhotoCamera,
                            contentDescription = "写真",
                            tint = Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                // お気に入りボタン
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(YoinSpacing.sm),
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = CircleShape
                ) {
                    IconButton(
                        onClick = { onToggleFavorite(photo.id) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (photo.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "お気に入り",
                            tint = if (photo.isFavorite) Color(0xFFFF6B6B) else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // 場所ラベル
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(YoinSpacing.sm),
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = photo.location,
                        fontSize = 10.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = YoinSpacing.xs, vertical = 4.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * 3列写真（グリッド）
 */
@Composable
private fun ThreeColumnPhotos(
    photos: List<TimelineContract.Photo>,
    onPhotoClick: (String) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(YoinSpacing.xs)
    ) {
        photos.take(3).forEach { photo ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)  // 正方形
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onPhotoClick(photo.id) }
            ) {
                // 実際の写真を表示（URLがあれば）
                if (photo.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = photo.imageUrl,
                        contentDescription = photo.location,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // フォールバック: グラデーション背景
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(getPhotoGradient(photo.id)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PhotoCamera,
                            contentDescription = "写真",
                            tint = Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // お気に入りボタン
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp),
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = CircleShape
                ) {
                    IconButton(
                        onClick = { onToggleFavorite(photo.id) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (photo.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "お気に入り",
                            tint = if (photo.isFavorite) Color(0xFFFF6B6B) else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 空状態
 */
@Composable
private fun EmptyState(selectedTab: TimelineContract.AlbumTab) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(YoinSpacing.xxxl),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(YoinSpacing.md)
        ) {
            Icon(
                imageVector = Icons.Filled.PhotoCamera,
                contentDescription = "写真なし",
                tint = YoinColors.TextSecondary,
                modifier = Modifier.size(80.dp)
            )
            Text(
                text = when (selectedTab) {
                    TimelineContract.AlbumTab.ALL -> "写真がありません"
                    TimelineContract.AlbumTab.BY_TRIP -> "この旅行の写真がありません"
                    TimelineContract.AlbumTab.FAVORITES -> "お気に入りの写真がありません"
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary
            )
            Text(
                text = "写真を撮影してアルバムに追加しましょう",
                fontSize = 14.sp,
                color = YoinColors.TextSecondary
            )
        }
    }
}

/**
 * プレビュー: マガジンレイアウト
 */
@PhonePreview
@Composable
private fun MagazineLayoutPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            val samplePhotos = listOf(
                // 京都の寺院
                TimelineContract.Photo("1", "trip1", "京都の旅", "https://images.unsplash.com/photo-1545569341-9eb8b30979d9?w=800", "https://images.unsplash.com/photo-1545569341-9eb8b30979d9?w=400", "金閣寺", 35.0394, 135.7292, 1640000000000L, true),
                // 沖縄の海
                TimelineContract.Photo("2", "trip2", "沖縄の海", "https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=800", "https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400", "美ら海", 26.6943, 127.8774, 1640100000000L, false),
                // 京都の街並み
                TimelineContract.Photo("3", "trip1", "京都の旅", "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?w=800", "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?w=400", "清水寺", 34.9949, 135.7850, 1640200000000L, true),
                // 北海道の雪景色
                TimelineContract.Photo("4", "trip3", "北海道", "https://images.unsplash.com/photo-1605648916361-9bc12ad6a569?w=800", "https://images.unsplash.com/photo-1605648916361-9bc12ad6a569?w=400", "札幌", 43.0636, 141.3535, 1640300000000L, false),
                // 沖縄のビーチ
                TimelineContract.Photo("5", "trip2", "沖縄の海", "https://images.unsplash.com/photo-1506929562872-bb421503ef21?w=800", "https://images.unsplash.com/photo-1506929562872-bb421503ef21?w=400", "ビーチ", 26.2175, 127.7193, 1640400000000L, true),
                // 京都の鳥居
                TimelineContract.Photo("6", "trip1", "京都の旅", "https://images.unsplash.com/photo-1478436127897-769e1b3f0f36?w=800", "https://images.unsplash.com/photo-1478436127897-769e1b3f0f36?w=400", "伏見稲荷", 34.9671, 135.7727, 1640500000000L, false),
                // 東京の寺院
                TimelineContract.Photo("7", "trip4", "東京散歩", "https://images.unsplash.com/photo-1540959733332-eab4deabeeaf?w=800", "https://images.unsplash.com/photo-1540959733332-eab4deabeeaf?w=400", "浅草寺", 35.7148, 139.7967, 1640600000000L, true),
                // 北海道の夜景
                TimelineContract.Photo("8", "trip3", "北海道", "https://images.unsplash.com/photo-1554797589-7241bb691973?w=800", "https://images.unsplash.com/photo-1554797589-7241bb691973?w=400", "函館山", 41.7513, 140.7019, 1640700000000L, false),
                // 東京タワー
                TimelineContract.Photo("9", "trip4", "東京散歩", "https://images.unsplash.com/photo-1536098561742-ca998e48cbcc?w=800", "https://images.unsplash.com/photo-1536098561742-ca998e48cbcc?w=400", "東京タワー", 35.6586, 139.7454, 1640800000000L, true),
                // 沖縄の夕日
                TimelineContract.Photo("10", "trip2", "沖縄の海", "https://images.unsplash.com/photo-1528127269322-539801943592?w=800", "https://images.unsplash.com/photo-1528127269322-539801943592?w=400", "夕日", 24.3240, 124.0853, 1640900000000L, false),
                // 奈良の鹿
                TimelineContract.Photo("11", "trip5", "奈良の鹿", "https://images.unsplash.com/photo-1528360983277-13d401cdc186?w=800", "https://images.unsplash.com/photo-1528360983277-13d401cdc186?w=400", "鹿公園", 34.6889, 135.8400, 1641000000000L, true),
                // 神社
                TimelineContract.Photo("12", "trip5", "奈良の鹿", "https://images.unsplash.com/photo-1480796927426-f609979314bd?w=800", "https://images.unsplash.com/photo-1480796927426-f609979314bd?w=400", "春日大社", 34.6818, 135.8484, 1641100000000L, false)
            )
            MagazineLayout(
                photos = samplePhotos,
                onPhotoClick = {},
                onToggleFavorite = {}
            )
        }
    }
}

/**
 * プレビュー: 空の状態
 */
@PhonePreview
@Composable
private fun EmptyStatePreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            EmptyState(selectedTab = TimelineContract.AlbumTab.ALL)
        }
    }
}
