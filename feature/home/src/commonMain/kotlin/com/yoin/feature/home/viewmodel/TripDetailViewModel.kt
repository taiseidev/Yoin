package com.yoin.feature.home.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 旅行詳細画面のViewModel
 *
 * MVIパターンに基づいた状態管理:
 * - State: UI状態
 * - Intent: ユーザーアクション
 * - Effect: 一時的なイベント
 */
class TripDetailViewModel : ScreenModel {
    private val _state = MutableStateFlow(TripDetailContract.State())
    val state: StateFlow<TripDetailContract.State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<TripDetailContract.Effect>()
    val effect: SharedFlow<TripDetailContract.Effect> = _effect.asSharedFlow()

    /**
     * ユーザーインテントを処理
     */
    fun onIntent(intent: TripDetailContract.Intent) {
        when (intent) {
            is TripDetailContract.Intent.OnScreenDisplayed -> handleScreenDisplayed(intent.tripId)
            is TripDetailContract.Intent.OnBackPressed -> handleBackPressed()
            is TripDetailContract.Intent.OnInvitePressed -> handleInvitePressed()
            is TripDetailContract.Intent.OnMembersPressed -> handleMembersPressed()
            is TripDetailContract.Intent.OnCameraPressed -> handleCameraPressed()
            is TripDetailContract.Intent.OnSettingsPressed -> handleSettingsPressed()
            is TripDetailContract.Intent.OnMapPressed -> handleMapPressed()
        }
    }

    private fun handleScreenDisplayed(tripId: String) {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                // TODO: 実際のデータ取得ロジックを実装
                // 仮実装としてサンプルデータを設定
                kotlinx.coroutines.delay(500)

                val tripDetail = TripDetailContract.TripDetail(
                    id = tripId,
                    emoji = "🏔",
                    title = "北海道旅行2025",
                    dateRange = "7/1〜7/5",
                    location = "札幌",
                    daysUntilDevelopment = 3,
                    developmentDateTime = "2025/7/6 AM 9:00",
                    todayPhotos = 12,
                    maxPhotos = 24,
                    members = listOf(
                        TripDetailContract.Member(
                            id = "1",
                            name = "全員",
                            isCurrentUser = true
                        ),
                        TripDetailContract.Member(
                            id = "2",
                            name = "太郎"
                        ),
                        TripDetailContract.Member(
                            id = "3",
                            name = "花子"
                        ),
                        TripDetailContract.Member(
                            id = "4",
                            name = "健一"
                        ),
                        TripDetailContract.Member(
                            id = "5",
                            name = "美咲"
                        )
                    )
                )

                _state.value = _state.value.copy(
                    isLoading = false,
                    tripDetail = tripDetail
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
                _effect.emit(TripDetailContract.Effect.ShowError(e.message ?: "エラーが発生しました"))
            }
        }
    }

    private fun handleBackPressed() {
        screenModelScope.launch {
            _effect.emit(TripDetailContract.Effect.NavigateBack)
        }
    }

    private fun handleInvitePressed() {
        screenModelScope.launch {
            _state.value.tripDetail?.let { trip ->
                _effect.emit(TripDetailContract.Effect.NavigateToInvite(trip.id))
            }
        }
    }

    private fun handleMembersPressed() {
        screenModelScope.launch {
            _state.value.tripDetail?.let { trip ->
                _effect.emit(TripDetailContract.Effect.NavigateToMembers(trip.id))
            }
        }
    }

    private fun handleCameraPressed() {
        screenModelScope.launch {
            _state.value.tripDetail?.let { trip ->
                _effect.emit(TripDetailContract.Effect.NavigateToCamera(trip.id))
            }
        }
    }

    private fun handleSettingsPressed() {
        screenModelScope.launch {
            _state.value.tripDetail?.let { trip ->
                _effect.emit(TripDetailContract.Effect.NavigateToSettings(trip.id))
            }
        }
    }

    private fun handleMapPressed() {
        screenModelScope.launch {
            _state.value.tripDetail?.let { trip ->
                _effect.emit(TripDetailContract.Effect.NavigateToMap(trip.id))
            }
        }
    }
}
