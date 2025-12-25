package com.yoin.feature.timeline.viewmodel

/**
 * 写真詳細画面のMVI Contract
 *
 * 機能:
 * - フルスクリーンでの写真表示
 * - 写真のメタデータ表示（撮影者、日時、位置情報）
 * - 写真間のナビゲーション
 * - 写真のダウンロード
 */
object PhotoDetailContract {
    data class State(
        val isLoading: Boolean = false,
        val photoDetail: PhotoDetail? = null,
        val currentPhotoIndex: Int = 0,
        val totalPhotos: Int = 0,
        val errorMessage: String? = null
    )

    /**
     * 写真詳細情報
     */
    data class PhotoDetail(
        val id: String,
        val imageUrl: String,
        val photographerName: String,
        val photographerInitial: String,
        val dateTime: String,
        val location: String,
        val subLocation: String,
        val dateWatermark: String,
        val isDownloaded: Boolean = false
    )

    sealed interface Intent {
        data class OnScreenDisplayed(val roomId: String, val photoId: String) : Intent
        data object OnBackPressed : Intent
        data object OnDownloadPressed : Intent
        data object OnPreviousPhotoPressed : Intent
        data object OnNextPhotoPressed : Intent
    }

    sealed interface Effect {
        data class ShowError(val message: String) : Effect
        data class ShowSuccess(val message: String) : Effect
        data object NavigateBack : Effect
    }
}
