package com.yoin.feature.timeline.viewmodel

/**
 * 現像後のルーム詳細画面のMVI Contract
 *
 * 機能:
 * - 現像済み写真のタイムライン表示
 * - 撮影者と撮影場所の表示
 * - 写真のダウンロード
 * - タイムライン/マップビューの切り替え
 */
object RoomDetailAfterContract {
    data class State(
        val isLoading: Boolean = true,
        val roomInfo: RoomInfo? = null,
        val photos: List<DevelopedPhoto> = emptyList(),
        val viewMode: ViewMode = ViewMode.TIMELINE,
        val errorMessage: String? = null
    )

    /**
     * ルーム基本情報
     */
    data class RoomInfo(
        val id: String,
        val emoji: String,
        val title: String,
        val dateRange: String,
        val location: String,
        val memberCount: Int,
        val photoCount: Int,
        val developedAt: String // 現像日時
    )

    /**
     * 現像済み写真データ
     */
    data class DevelopedPhoto(
        val id: String,
        val imageUrl: String, // 実際の画像URL（仮実装ではプレースホルダー）
        val photographerName: String,
        val photographerAvatar: String,
        val location: String,
        val timestamp: String, // "2024/12/24 14:30"
        val latitude: Double?,
        val longitude: Double?,
        val isDownloaded: Boolean = false
    )

    /**
     * 表示モード
     */
    enum class ViewMode {
        TIMELINE, // タイムライン表示
        MAP       // マップ表示
    }

    sealed interface Intent {
        data class OnScreenDisplayed(val roomId: String) : Intent
        data object OnBackPressed : Intent
        data class OnViewModeChanged(val mode: ViewMode) : Intent
        data class OnPhotoClicked(val photoId: String) : Intent
        data class OnDownloadPhoto(val photoId: String) : Intent
        data object OnDownloadAll : Intent
    }

    sealed interface Effect {
        data class ShowError(val message: String) : Effect
        data object NavigateBack : Effect
        data class NavigateToPhotoDetail(val photoId: String) : Effect
        data class ShowDownloadSuccess(val message: String) : Effect
    }
}
