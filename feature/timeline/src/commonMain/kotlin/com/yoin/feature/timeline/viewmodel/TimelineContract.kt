package com.yoin.feature.timeline.viewmodel

/**
 * タイムライン（アルバム）画面のMVI Contract
 */
object TimelineContract {
    data class State(
        val isLoading: Boolean = false,
        val photos: List<Photo> = emptyList(),
        val trips: List<Trip> = emptyList(),
        val selectedTab: AlbumTab = AlbumTab.ALL,
        val selectedTrip: String? = null,
        val sortOption: SortOption = SortOption.DATE_DESC,
        val searchQuery: String = "",
        val showFavoritesOnly: Boolean = false,
        val errorMessage: String? = null
    )

    /**
     * 写真データ
     */
    data class Photo(
        val id: String,
        val tripId: String,
        val tripName: String,
        val imageUrl: String,
        val thumbnailUrl: String,
        val location: String,
        val latitude: Double,
        val longitude: Double,
        val takenAt: Long, // Unix timestamp
        val isFavorite: Boolean,
        val caption: String = ""
    )

    /**
     * 旅行データ
     */
    data class Trip(
        val id: String,
        val name: String,
        val startDate: Long,
        val endDate: Long,
        val photoCount: Int,
        val coverPhotoUrl: String?
    )

    /**
     * アルバムタブ
     */
    enum class AlbumTab {
        ALL,        // すべての写真
        BY_TRIP,    // 旅行別
        FAVORITES   // お気に入り
    }

    /**
     * ソートオプション
     */
    enum class SortOption(val displayName: String) {
        DATE_DESC("新しい順"),
        DATE_ASC("古い順"),
        LOCATION("場所順")
    }

    sealed interface Intent {
        data object OnScreenDisplayed : Intent
        data object OnRefresh : Intent
        data class OnPhotoClick(val photoId: String) : Intent
        data class OnTabChange(val tab: AlbumTab) : Intent
        data class OnTripSelect(val tripId: String?) : Intent
        data class OnSortChange(val sortOption: SortOption) : Intent
        data class OnSearch(val query: String) : Intent
        data class OnToggleFavorite(val photoId: String) : Intent
        data object OnToggleFavoritesFilter : Intent
    }

    sealed interface Effect {
        data class ShowError(val message: String) : Effect
        data class NavigateToPhotoDetail(val photoId: String, val roomId: String) : Effect
    }
}
