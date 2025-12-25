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
 * 現像後のルーム詳細画面のViewModel
 *
 * MVIパターンに基づいた状態管理:
 * - State: 画面の状態（読み込み中、データ、表示モード）
 * - Intent: ユーザーアクション
 * - Effect: 一時的なイベント（ナビゲーション、トースト表示）
 */
class RoomDetailAfterViewModel : ScreenModel {
    private val _state = MutableStateFlow(RoomDetailAfterContract.State())
    val state: StateFlow<RoomDetailAfterContract.State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<RoomDetailAfterContract.Effect>()
    val effect: SharedFlow<RoomDetailAfterContract.Effect> = _effect.asSharedFlow()

    fun onIntent(intent: RoomDetailAfterContract.Intent) {
        when (intent) {
            is RoomDetailAfterContract.Intent.OnScreenDisplayed -> handleScreenDisplayed(intent.roomId)
            is RoomDetailAfterContract.Intent.OnBackPressed -> handleBackPressed()
            is RoomDetailAfterContract.Intent.OnViewModeChanged -> handleViewModeChanged(intent.mode)
            is RoomDetailAfterContract.Intent.OnPhotoClicked -> handlePhotoClicked(intent.photoId)
            is RoomDetailAfterContract.Intent.OnDownloadPhoto -> handleDownloadPhoto(intent.photoId)
            is RoomDetailAfterContract.Intent.OnDownloadAll -> handleDownloadAll()
        }
    }

    private fun handleScreenDisplayed(roomId: String) {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                // TODO: 実際のデータ取得ロジックを実装
                delay(500)

                // サンプルデータ
                val roomInfo = RoomDetailAfterContract.RoomInfo(
                    id = roomId,
                    emoji = "🏔️",
                    title = "北海道旅行",
                    dateRange = "2024/12/20 〜 2024/12/23",
                    location = "札幌・小樽",
                    memberCount = 4,
                    photoCount = 48,
                    developedAt = "2024/12/24 09:00"
                )

                val samplePhotos = listOf(
                    RoomDetailAfterContract.DevelopedPhoto(
                        id = "1",
                        imageUrl = "",
                        photographerName = "田中太郎",
                        photographerAvatar = "",
                        location = "札幌市中央区",
                        timestamp = "2024/12/20 14:30",
                        latitude = 43.0642,
                        longitude = 141.3469
                    ),
                    RoomDetailAfterContract.DevelopedPhoto(
                        id = "2",
                        imageUrl = "",
                        photographerName = "佐藤花子",
                        photographerAvatar = "",
                        location = "小樽市",
                        timestamp = "2024/12/20 16:15",
                        latitude = 43.1907,
                        longitude = 140.9947
                    ),
                    RoomDetailAfterContract.DevelopedPhoto(
                        id = "3",
                        imageUrl = "",
                        photographerName = "鈴木一郎",
                        photographerAvatar = "",
                        location = "札幌市北区",
                        timestamp = "2024/12/20 18:00",
                        latitude = 43.0900,
                        longitude = 141.3400
                    ),
                    RoomDetailAfterContract.DevelopedPhoto(
                        id = "4",
                        imageUrl = "",
                        photographerName = "田中太郎",
                        photographerAvatar = "",
                        location = "すすきの",
                        timestamp = "2024/12/20 20:30",
                        latitude = 43.0530,
                        longitude = 141.3533
                    ),
                    RoomDetailAfterContract.DevelopedPhoto(
                        id = "5",
                        imageUrl = "",
                        photographerName = "高橋美咲",
                        photographerAvatar = "",
                        location = "円山公園",
                        timestamp = "2024/12/21 10:00",
                        latitude = 43.0500,
                        longitude = 141.3200
                    ),
                    RoomDetailAfterContract.DevelopedPhoto(
                        id = "6",
                        imageUrl = "",
                        photographerName = "佐藤花子",
                        photographerAvatar = "",
                        location = "白い恋人パーク",
                        timestamp = "2024/12/21 13:45",
                        latitude = 43.0800,
                        longitude = 141.2900
                    )
                )

                _state.value = _state.value.copy(
                    isLoading = false,
                    roomInfo = roomInfo,
                    photos = samplePhotos
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
                _effect.emit(RoomDetailAfterContract.Effect.ShowError(e.message ?: "データの取得に失敗しました"))
            }
        }
    }

    private fun handleBackPressed() {
        screenModelScope.launch {
            _effect.emit(RoomDetailAfterContract.Effect.NavigateBack)
        }
    }

    private fun handleViewModeChanged(mode: RoomDetailAfterContract.ViewMode) {
        _state.value = _state.value.copy(viewMode = mode)
    }

    private fun handlePhotoClicked(photoId: String) {
        screenModelScope.launch {
            _effect.emit(RoomDetailAfterContract.Effect.NavigateToPhotoDetail(photoId))
        }
    }

    private fun handleDownloadPhoto(photoId: String) {
        screenModelScope.launch {
            try {
                // TODO: 実際のダウンロードロジックを実装
                delay(500)

                // ダウンロード済みフラグを更新
                val updatedPhotos = _state.value.photos.map { photo ->
                    if (photo.id == photoId) {
                        photo.copy(isDownloaded = true)
                    } else {
                        photo
                    }
                }
                _state.value = _state.value.copy(photos = updatedPhotos)

                _effect.emit(RoomDetailAfterContract.Effect.ShowDownloadSuccess("写真を保存しました"))
            } catch (e: Exception) {
                _effect.emit(RoomDetailAfterContract.Effect.ShowError("ダウンロードに失敗しました"))
            }
        }
    }

    private fun handleDownloadAll() {
        screenModelScope.launch {
            try {
                // TODO: 実際の一括ダウンロードロジックを実装
                delay(1000)

                val updatedPhotos = _state.value.photos.map { it.copy(isDownloaded = true) }
                _state.value = _state.value.copy(photos = updatedPhotos)

                _effect.emit(RoomDetailAfterContract.Effect.ShowDownloadSuccess("全ての写真を保存しました"))
            } catch (e: Exception) {
                _effect.emit(RoomDetailAfterContract.Effect.ShowError("一括ダウンロードに失敗しました"))
            }
        }
    }
}
