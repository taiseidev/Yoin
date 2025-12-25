package com.yoin.feature.map.viewmodel

import com.yoin.feature.map.model.MapMember
import com.yoin.feature.map.model.PhotoLocation

/**
 * 地図フルスクリーン画面のMVIコントラクト
 */
interface MapFullscreenContract {

    /**
     * 画面の状態
     */
    data class State(
        val isLoading: Boolean = false,
        val roomTitle: String = "",
        val photos: List<PhotoLocation> = emptyList(),
        val members: List<MapMember> = emptyList(),
        val selectedMemberId: String? = null, // nullの場合は「全員」が選択
        val selectedPhotoId: String? = null,
        val zoomLevel: Float = 12f,
        val centerLatitude: Double = 43.064170,
        val centerLongitude: Double = 141.346936,
        val totalPhotoCount: Int = 0,
        val error: String? = null
    ) {
        /**
         * 現在選択されている写真
         */
        val selectedPhoto: PhotoLocation?
            get() = photos.firstOrNull { it.photoId == selectedPhotoId }

        /**
         * フィルタリングされた写真リスト
         */
        val filteredPhotos: List<PhotoLocation>
            get() = if (selectedMemberId == null) {
                photos
            } else {
                // TODO: 実際にはメンバーIDで写真をフィルタリング
                photos
            }
    }

    /**
     * ユーザーの意図（アクション）
     */
    sealed interface Intent {
        data object OnBackPressed : Intent
        data object OnMenuPressed : Intent
        data class OnMemberFilterSelected(val memberId: String?) : Intent
        data class OnPhotoMarkerTapped(val photoId: String) : Intent
        data object OnZoomInPressed : Intent
        data object OnZoomOutPressed : Intent
        data object OnCurrentLocationPressed : Intent
        data object OnPhotoCardTapped : Intent
    }

    /**
     * 副作用（ナビゲーションなど）
     */
    sealed interface Effect {
        data object NavigateBack : Effect
        data class ShowMenu(val roomId: String) : Effect
        data class NavigateToPhotoDetail(val roomId: String, val photoId: String) : Effect
        data class ShowError(val message: String) : Effect
        data object MoveToCurrentLocation : Effect
    }
}
