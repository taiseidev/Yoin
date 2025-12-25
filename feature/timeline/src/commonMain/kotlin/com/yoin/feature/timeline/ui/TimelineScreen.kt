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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.design.theme.YoinSizes
import com.yoin.core.design.theme.YoinFontSizes
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.timeline.viewmodel.TimelineContract
import com.yoin.feature.timeline.viewmodel.TimelineViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * タイムライン（アルバム）画面
 *
 * 高機能アルバムビューアを提供:
 * - タブ切り替え（すべて/旅行別/お気に入り）
 * - 検索機能（場所、旅行名、キャプション）
 * - ソート機能（日付昇順/降順、場所順）
 * - お気に入りフィルタ
 * - プルトゥリフレッシュ
 * - 写真グリッド表示（3列）
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
                // ヘッダー
                AlbumHeader(
                    searchQuery = state.searchQuery,
                    onSearchChange = { query ->
                        viewModel.onIntent(TimelineContract.Intent.OnSearch(query))
                    }
                )

                // タブ
                AlbumTabs(
                    selectedTab = state.selectedTab,
                    onTabChange = { tab ->
                        viewModel.onIntent(TimelineContract.Intent.OnTabChange(tab))
                    }
                )

                // フィルタ/ソートバー
                FilterSortBar(
                    sortOption = state.sortOption,
                    showFavoritesOnly = state.showFavoritesOnly,
                    onSortChange = { sort ->
                        viewModel.onIntent(TimelineContract.Intent.OnSortChange(sort))
                    },
                    onToggleFavoritesFilter = {
                        viewModel.onIntent(TimelineContract.Intent.OnToggleFavoritesFilter)
                    }
                )

                // 旅行リスト（旅行別タブの時のみ）
                if (state.selectedTab == TimelineContract.AlbumTab.BY_TRIP) {
                    TripList(
                        trips = state.trips,
                        selectedTripId = state.selectedTrip,
                        onTripSelect = { tripId ->
                            viewModel.onIntent(TimelineContract.Intent.OnTripSelect(tripId))
                        }
                    )
                }

                // 写真グリッド
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
                    PhotoGrid(
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
 * アルバムヘッダー（検索バー付き）
 */
@Composable
private fun AlbumHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(YoinColors.Surface)
            .padding(YoinSpacing.lg)
    ) {
        Text(
            text = "アルバム",
            fontSize = YoinFontSizes.displaySmall.value.sp,
            fontWeight = FontWeight.Bold,
            color = YoinColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(YoinSpacing.md))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("場所や旅行名で検索", color = YoinColors.TextSecondary) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "検索",
                    tint = YoinColors.TextSecondary
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(YoinSpacing.md),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = YoinColors.Background,
                unfocusedContainerColor = YoinColors.Background,
                focusedBorderColor = YoinColors.Primary,
                unfocusedBorderColor = YoinColors.SurfaceVariant
            )
        )
    }
}

/**
 * アルバムタブ
 */
@Composable
private fun AlbumTabs(
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
                color = YoinColors.Primary
            )
        }
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
 * フィルタ/ソートバー
 */
@Composable
private fun FilterSortBar(
    sortOption: TimelineContract.SortOption,
    showFavoritesOnly: Boolean,
    onSortChange: (TimelineContract.SortOption) -> Unit,
    onToggleFavoritesFilter: () -> Unit
) {
    var showSortMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(YoinColors.Surface)
            .padding(horizontal = YoinSpacing.lg, vertical = YoinSpacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // お気に入りフィルタ
        FilterChip(
            selected = showFavoritesOnly,
            onClick = onToggleFavoritesFilter,
            label = { Text("お気に入りのみ", fontSize = YoinFontSizes.labelMedium.value.sp) },
            leadingIcon = {
                Icon(
                    imageVector = if (showFavoritesOnly) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "お気に入り",
                    modifier = Modifier.size(YoinSizes.iconMedium)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = YoinColors.Primary.copy(alpha = 0.2f),
                selectedLabelColor = YoinColors.Primary,
                selectedLeadingIconColor = YoinColors.Primary
            )
        )

        // ソート
        Box {
            TextButton(
                onClick = { showSortMenu = true }
            ) {
                Text(
                    text = sortOption.displayName,
                    color = YoinColors.Primary,
                    fontSize = YoinFontSizes.labelMedium.value.sp
                )
                Text(
                    text = " ▼",
                    color = YoinColors.Primary,
                    fontSize = YoinFontSizes.caption.value.sp
                )
            }

            DropdownMenu(
                expanded = showSortMenu,
                onDismissRequest = { showSortMenu = false }
            ) {
                TimelineContract.SortOption.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.displayName) },
                        onClick = {
                            onSortChange(option)
                            showSortMenu = false
                        }
                    )
                }
            }
        }
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
            .padding(vertical = YoinSpacing.sm)
    ) {
        Text(
            text = "旅行を選択",
            fontSize = YoinFontSizes.labelLarge.value.sp,
            fontWeight = FontWeight.Bold,
            color = YoinColors.TextPrimary,
            modifier = Modifier.padding(horizontal = YoinSpacing.lg, vertical = YoinSpacing.xs)
        )

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
            .width(140.dp)
            .height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) YoinColors.Primary.copy(alpha = 0.1f) else YoinColors.Surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) YoinSpacing.xs else 1.dp
        ),
        shape = RoundedCornerShape(YoinSpacing.md)
    ) {
        if (trip == null) {
            // すべて表示カード
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "すべて",
                        tint = if (isSelected) YoinColors.Primary else YoinColors.TextSecondary,
                        modifier = Modifier.size(YoinSizes.iconLarge)
                    )
                    Spacer(modifier = Modifier.height(YoinSpacing.xs))
                    Text(
                        text = "すべて",
                        fontSize = YoinFontSizes.labelLarge.value.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) YoinColors.Primary else YoinColors.TextPrimary
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.padding(YoinSpacing.sm)
            ) {
                Text(
                    text = trip.name,
                    fontSize = YoinFontSizes.labelMedium.value.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) YoinColors.Primary else YoinColors.TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(YoinSpacing.xs))
                Text(
                    text = "${trip.photoCount}枚",
                    fontSize = YoinFontSizes.caption.value.sp,
                    color = YoinColors.TextSecondary
                )
                Spacer(modifier = Modifier.height(YoinSpacing.xs))
                Text(
//                    text = formatDate(trip.startDate),
                    text = "2025/06/12",
                    fontSize = YoinFontSizes.caption.value.sp,
                    color = YoinColors.TextSecondary
                )
            }
        }
    }
}

/**
 * 写真グリッド（3列）
 */
@Composable
private fun PhotoGrid(
    photos: List<TimelineContract.Photo>,
    onPhotoClick: (String) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(YoinSpacing.xs),
        verticalArrangement = Arrangement.spacedBy(YoinSpacing.xs),
        contentPadding = PaddingValues(YoinSpacing.xs),
        modifier = Modifier
            .fillMaxSize()
            .background(YoinColors.Background)
    ) {
        items(photos, key = { it.id }) { photo ->
            PhotoItem(
                photo = photo,
                onPhotoClick = { onPhotoClick(photo.id) },
                onToggleFavorite = { onToggleFavorite(photo.id) }
            )
        }
    }
}

/**
 * 写真アイテム
 */
@Composable
private fun PhotoItem(
    photo: TimelineContract.Photo,
    onPhotoClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(YoinSpacing.sm))
            .background(YoinColors.SurfaceVariant)
            .clickable(onClick = onPhotoClick)
    ) {
        // 写真
//        val painter = rememberAsyncImagePainter(photo.thumbnailUrl)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.PhotoCamera,
                contentDescription = "写真",
                tint = YoinColors.TextSecondary,
                modifier = Modifier.size(YoinSizes.iconXLarge)
            )
        }
//        when (painter.state) {
//            is AsyncImagePainter.State.Loading -> {
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    CircularProgressIndicator(
//                        modifier = Modifier.size(24.dp),
//                        color = YoinColors.Primary
//                    )
//                }
//            }
//
//            is AsyncImagePainter.State.Error -> {
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = "📷",
//                        fontSize = 32.sp
//                    )
//                }
//            }
//
//            else -> {
//                Image(
//                    painter = painter,
//                    contentDescription = photo.location,
//                    modifier = Modifier.fillMaxSize(),
//                    contentScale = ContentScale.Crop
//                )
//            }
//        }

        // お気に入りボタン
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(YoinSpacing.xs),
            color = Color.Black.copy(alpha = 0.5f),
            shape = CircleShape
        ) {
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (photo.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "お気に入り",
                    tint = if (photo.isFavorite) Color(0xFFFF6B6B) else Color.White,
                    modifier = Modifier.size(YoinSpacing.lg)
                )
            }
        }

        // 場所ラベル
        Surface(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(YoinSpacing.xs),
            color = Color.Black.copy(alpha = 0.6f),
            shape = RoundedCornerShape(YoinSpacing.xs)
        ) {
            Text(
                text = photo.location,
                fontSize = YoinFontSizes.caption.value.sp,
                color = Color.White,
                modifier = Modifier.padding(horizontal = YoinSpacing.xs + 2.dp, vertical = 2.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
            verticalArrangement = Arrangement.spacedBy(YoinSpacing.sm)
        ) {
            Icon(
                imageVector = Icons.Filled.PhotoCamera,
                contentDescription = "写真なし",
                tint = YoinColors.TextSecondary,
                modifier = Modifier.size(YoinSizes.logoMedium)
            )
            Text(
                text = when (selectedTab) {
                    TimelineContract.AlbumTab.ALL -> "写真がありません"
                    TimelineContract.AlbumTab.BY_TRIP -> "この旅行の写真がありません"
                    TimelineContract.AlbumTab.FAVORITES -> "お気に入りの写真がありません"
                },
                fontSize = YoinFontSizes.bodyMedium.value.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary
            )
            Text(
                text = "写真を撮影してアルバムに追加しましょう",
                fontSize = YoinFontSizes.labelMedium.value.sp,
                color = YoinColors.TextSecondary
            )
        }
    }
}

/**
 * 日付フォーマット
 */
//private fun formatDate(timestamp: Long): String {
//    val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN)
//    return sdf.format(Date(timestamp))
//}

/**
 * プレビュー: アルバムヘッダー
 */
@PhonePreview
@Composable
private fun AlbumHeaderPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            AlbumHeader(
                searchQuery = "",
                onSearchChange = {}
            )
        }
    }
}

/**
 * プレビュー: アルバムタブ
 */
@PhonePreview
@Composable
private fun AlbumTabsPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            AlbumTabs(
                selectedTab = TimelineContract.AlbumTab.ALL,
                onTabChange = {}
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
