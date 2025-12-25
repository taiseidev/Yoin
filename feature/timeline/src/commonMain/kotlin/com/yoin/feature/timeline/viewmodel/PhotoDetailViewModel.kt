package com.yoin.feature.timeline.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 写真詳細画面のViewModel
 *
 * MVIパターンに基づいた状態管理:
 * - State: 写真詳細とナビゲーション状態
 * - Intent: ユーザーアクション
 * - Effect: 一時的なイベント（ナビゲーション、成功/エラー通知）
 */
class PhotoDetailViewModel : ScreenModel {
    private val _state = MutableStateFlow(PhotoDetailContract.State())
    val state: StateFlow<PhotoDetailContract.State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<PhotoDetailContract.Effect>()
    val effect: SharedFlow<PhotoDetailContract.Effect> = _effect.asSharedFlow()

    // サンプルデータ（実際はリポジトリから取得）
    private val samplePhotos = listOf(
        PhotoDetailContract.PhotoDetail(
            id = "1",
            imageUrl = "",
            photographerName = "山田花子",
            photographerInitial = "山",
            dateTime = "2025年7月4日 12:15",
            location = "函館山展望台",
            subLocation = "北海道函館市函館山",
            dateWatermark = "25.7.4",
            isDownloaded = false
        ),
        PhotoDetailContract.PhotoDetail(
            id = "2",
            imageUrl = "",
            photographerName = "佐藤次郎",
            photographerInitial = "佐",
            dateTime = "2025年7月4日 13:45",
            location = "五稜郭公園",
            subLocation = "北海道函館市五稜郭町",
            dateWatermark = "25.7.4",
            isDownloaded = false
        ),
        PhotoDetailContract.PhotoDetail(
            id = "3",
            imageUrl = "",
            photographerName = "田中太郎",
            photographerInitial = "田",
            dateTime = "2025年7月4日 14:32",
            location = "小樽運河",
            subLocation = "北海道小樽市港町",
            dateWatermark = "25.7.4",
            isDownloaded = false
        ),
        PhotoDetailContract.PhotoDetail(
            id = "4",
            imageUrl = "",
            photographerName = "鈴木美咲",
            photographerInitial = "鈴",
            dateTime = "2025年7月4日 16:20",
            location = "札幌時計台",
            subLocation = "北海道札幌市中央区",
            dateWatermark = "25.7.4",
            isDownloaded = false
        )
    )

    fun onIntent(intent: PhotoDetailContract.Intent) {
        when (intent) {
            is PhotoDetailContract.Intent.OnScreenDisplayed -> handleScreenDisplayed(intent.roomId, intent.photoId)
            is PhotoDetailContract.Intent.OnBackPressed -> handleBackPressed()
            is PhotoDetailContract.Intent.OnDownloadPressed -> handleDownloadPressed()
            is PhotoDetailContract.Intent.OnPreviousPhotoPressed -> handlePreviousPhotoPressed()
            is PhotoDetailContract.Intent.OnNextPhotoPressed -> handleNextPhotoPressed()
        }
    }

    private fun handleScreenDisplayed(roomId: String, photoId: String) {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                // TODO: 実際のルーム写真取得ロジックを実装
                delay(300)

                // photoIdから現在の写真のインデックスを見つける
                val currentIndex = samplePhotos.indexOfFirst { it.id == photoId }
                    .takeIf { it >= 0 } ?: 0

                _state.value = _state.value.copy(
                    isLoading = false,
                    photoDetail = samplePhotos[currentIndex],
                    currentPhotoIndex = currentIndex,
                    totalPhotos = samplePhotos.size
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
                _effect.emit(PhotoDetailContract.Effect.ShowError(e.message ?: "写真の読み込みに失敗しました"))
            }
        }
    }

    private fun handleBackPressed() {
        screenModelScope.launch {
            _effect.emit(PhotoDetailContract.Effect.NavigateBack)
        }
    }

    private fun handleDownloadPressed() {
        screenModelScope.launch {
            val currentPhoto = _state.value.photoDetail ?: return@launch

            try {
                // TODO: 実際のダウンロード処理を実装
                delay(500)

                _state.value = _state.value.copy(
                    photoDetail = currentPhoto.copy(isDownloaded = true)
                )
                _effect.emit(PhotoDetailContract.Effect.ShowSuccess("写真を保存しました"))
            } catch (e: Exception) {
                _effect.emit(PhotoDetailContract.Effect.ShowError(e.message ?: "ダウンロードに失敗しました"))
            }
        }
    }

    private fun handlePreviousPhotoPressed() {
        val currentState = _state.value
        if (currentState.currentPhotoIndex > 0) {
            val newIndex = currentState.currentPhotoIndex - 1
            _state.value = currentState.copy(
                photoDetail = samplePhotos[newIndex],
                currentPhotoIndex = newIndex
            )
        }
    }

    private fun handleNextPhotoPressed() {
        val currentState = _state.value
        if (currentState.currentPhotoIndex < currentState.totalPhotos - 1) {
            val newIndex = currentState.currentPhotoIndex + 1
            _state.value = currentState.copy(
                photoDetail = samplePhotos[newIndex],
                currentPhotoIndex = newIndex
            )
        }
    }
}
