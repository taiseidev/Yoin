package com.yoin.feature.timeline.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.yoin.feature.timeline.util.currentTimeMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * タイムライン（アルバム）画面のViewModel
 */
class TimelineViewModel : ScreenModel {
    private val _state = MutableStateFlow(TimelineContract.State())
    val state: StateFlow<TimelineContract.State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<TimelineContract.Effect>()
    val effect: SharedFlow<TimelineContract.Effect> = _effect.asSharedFlow()

    // すべての写真データ（フィルタ前）
    private var allPhotos: List<TimelineContract.Photo> = emptyList()

    fun onIntent(intent: TimelineContract.Intent) {
        when (intent) {
            is TimelineContract.Intent.OnScreenDisplayed -> handleScreenDisplayed()
            is TimelineContract.Intent.OnRefresh -> handleRefresh()
            is TimelineContract.Intent.OnPhotoClick -> handlePhotoClick(intent.photoId)
            is TimelineContract.Intent.OnTabChange -> handleTabChange(intent.tab)
            is TimelineContract.Intent.OnTripSelect -> handleTripSelect(intent.tripId)
            is TimelineContract.Intent.OnSortChange -> handleSortChange(intent.sortOption)
            is TimelineContract.Intent.OnSearch -> handleSearch(intent.query)
            is TimelineContract.Intent.OnToggleFavorite -> handleToggleFavorite(intent.photoId)
            is TimelineContract.Intent.OnToggleFavoritesFilter -> handleToggleFavoritesFilter()
        }
    }

    private fun handleScreenDisplayed() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                delay(500) // ローディングシミュレーション

                // サンプルデータを生成
                val trips = generateSampleTrips()
                val photos = generateSamplePhotos(trips)

                allPhotos = photos

                _state.update {
                    it.copy(
                        isLoading = false,
                        trips = trips,
                        photos = applyFilters(photos, it)
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
                _effect.emit(TimelineContract.Effect.ShowError(e.message ?: "エラーが発生しました"))
            }
        }
    }

    private fun handleRefresh() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                delay(1000)

                val trips = generateSampleTrips()
                val photos = generateSamplePhotos(trips)

                allPhotos = photos

                _state.update {
                    it.copy(
                        isLoading = false,
                        trips = trips,
                        photos = applyFilters(photos, it)
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
                _effect.emit(TimelineContract.Effect.ShowError(e.message ?: "エラーが発生しました"))
            }
        }
    }

    private fun handlePhotoClick(photoId: String) {
        screenModelScope.launch {
            val photo = allPhotos.find { it.id == photoId }
            if (photo != null) {
                // roomIdはtripIdと同じと仮定
                _effect.emit(TimelineContract.Effect.NavigateToPhotoDetail(photoId, photo.tripId))
            }
        }
    }

    private fun handleTabChange(tab: TimelineContract.AlbumTab) {
        _state.update {
            it.copy(
                selectedTab = tab,
                selectedTrip = null, // タブ変更時は旅行選択をリセット
                photos = applyFilters(allPhotos, it.copy(selectedTab = tab, selectedTrip = null))
            )
        }
    }

    private fun handleTripSelect(tripId: String?) {
        _state.update {
            it.copy(
                selectedTrip = tripId,
                photos = applyFilters(allPhotos, it.copy(selectedTrip = tripId))
            )
        }
    }

    private fun handleSortChange(sortOption: TimelineContract.SortOption) {
        _state.update {
            it.copy(
                sortOption = sortOption,
                photos = applyFilters(allPhotos, it.copy(sortOption = sortOption))
            )
        }
    }

    private fun handleSearch(query: String) {
        _state.update {
            it.copy(
                searchQuery = query,
                photos = applyFilters(allPhotos, it.copy(searchQuery = query))
            )
        }
    }

    private fun handleToggleFavorite(photoId: String) {
        allPhotos = allPhotos.map { photo ->
            if (photo.id == photoId) {
                photo.copy(isFavorite = !photo.isFavorite)
            } else {
                photo
            }
        }

        _state.update {
            it.copy(photos = applyFilters(allPhotos, it))
        }
    }

    private fun handleToggleFavoritesFilter() {
        _state.update {
            val newShowFavoritesOnly = !it.showFavoritesOnly
            it.copy(
                showFavoritesOnly = newShowFavoritesOnly,
                photos = applyFilters(allPhotos, it.copy(showFavoritesOnly = newShowFavoritesOnly))
            )
        }
    }

    /**
     * フィルタとソートを適用
     */
    private fun applyFilters(
        photos: List<TimelineContract.Photo>,
        state: TimelineContract.State
    ): List<TimelineContract.Photo> {
        var filteredPhotos = photos

        // タブによるフィルタ
        filteredPhotos = when (state.selectedTab) {
            TimelineContract.AlbumTab.ALL -> filteredPhotos
            TimelineContract.AlbumTab.BY_TRIP -> {
                if (state.selectedTrip != null) {
                    filteredPhotos.filter { it.tripId == state.selectedTrip }
                } else {
                    filteredPhotos
                }
            }
            TimelineContract.AlbumTab.FAVORITES -> filteredPhotos.filter { it.isFavorite }
        }

        // お気に入りフィルタ
        if (state.showFavoritesOnly) {
            filteredPhotos = filteredPhotos.filter { it.isFavorite }
        }

        // 検索クエリによるフィルタ
        if (state.searchQuery.isNotBlank()) {
            filteredPhotos = filteredPhotos.filter {
                it.location.contains(state.searchQuery, ignoreCase = true) ||
                it.tripName.contains(state.searchQuery, ignoreCase = true) ||
                it.caption.contains(state.searchQuery, ignoreCase = true)
            }
        }

        // ソート
        filteredPhotos = when (state.sortOption) {
            TimelineContract.SortOption.DATE_DESC -> filteredPhotos.sortedByDescending { it.takenAt }
            TimelineContract.SortOption.DATE_ASC -> filteredPhotos.sortedBy { it.takenAt }
            TimelineContract.SortOption.LOCATION -> filteredPhotos.sortedBy { it.location }
        }

        return filteredPhotos
    }

    /**
     * サンプル旅行データを生成
     */
    private fun generateSampleTrips(): List<TimelineContract.Trip> {
        val now = currentTimeMillis()
        val dayInMs = 24 * 60 * 60 * 1000L

        return listOf(
            TimelineContract.Trip(
                id = "trip1",
                name = "京都・奈良旅行",
                startDate = now - 30 * dayInMs,
                endDate = now - 28 * dayInMs,
                photoCount = 15,
                coverPhotoUrl = "https://picsum.photos/seed/kyoto/400/400"
            ),
            TimelineContract.Trip(
                id = "trip2",
                name = "北海道旅行",
                startDate = now - 60 * dayInMs,
                endDate = now - 55 * dayInMs,
                photoCount = 24,
                coverPhotoUrl = "https://picsum.photos/seed/hokkaido/400/400"
            ),
            TimelineContract.Trip(
                id = "trip3",
                name = "沖縄旅行",
                startDate = now - 90 * dayInMs,
                endDate = now - 87 * dayInMs,
                photoCount = 18,
                coverPhotoUrl = "https://picsum.photos/seed/okinawa/400/400"
            )
        )
    }

    /**
     * サンプル写真データを生成
     */
    private fun generateSamplePhotos(trips: List<TimelineContract.Trip>): List<TimelineContract.Photo> {
        val photos = mutableListOf<TimelineContract.Photo>()
        val locations = listOf(
            "清水寺" to Pair(34.9948, 135.7850),
            "金閣寺" to Pair(35.0394, 135.7292),
            "嵐山" to Pair(35.0094, 135.6686),
            "札幌時計台" to Pair(43.0625, 141.3539),
            "函館夜景" to Pair(41.7688, 140.7298),
            "美ら海水族館" to Pair(26.6939, 127.8786),
            "首里城" to Pair(26.2173, 127.7192),
            "竹富島" to Pair(24.3239, 124.0842)
        )

        trips.forEachIndexed { tripIndex, trip ->
            val photoCount = trip.photoCount
            repeat(photoCount) { photoIndex ->
                val location = locations[photoIndex % locations.size]
                val dayOffset = photoIndex * (24 * 60 * 60 * 1000L) / photoCount

                photos.add(
                    TimelineContract.Photo(
                        id = "photo_${trip.id}_$photoIndex",
                        tripId = trip.id,
                        tripName = trip.name,
                        imageUrl = "https://picsum.photos/seed/${trip.id}_$photoIndex/800/800",
                        thumbnailUrl = "https://picsum.photos/seed/${trip.id}_$photoIndex/200/200",
                        location = location.first,
                        latitude = location.second.first,
                        longitude = location.second.second,
                        takenAt = trip.startDate + dayOffset,
                        isFavorite = photoIndex % 4 == 0, // 4枚に1枚お気に入り
                        caption = if (photoIndex % 3 == 0) "素敵な景色" else ""
                    )
                )
            }
        }

        return photos
    }
}
