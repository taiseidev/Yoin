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
 * ホーム画面のViewModel
 *
 * MVIパターンに基づいた状態管理:
 * - State: UI状態
 * - Intent: ユーザーアクション
 * - Effect: 一時的なイベント
 */
class HomeViewModel : ScreenModel {
    private val _state = MutableStateFlow(HomeContract.State())
    val state: StateFlow<HomeContract.State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HomeContract.Effect>()
    val effect: SharedFlow<HomeContract.Effect> = _effect.asSharedFlow()

    /**
     * ユーザーインテントを処理
     */
    fun onIntent(intent: HomeContract.Intent) {
        when (intent) {
            is HomeContract.Intent.OnScreenDisplayed -> handleScreenDisplayed()
            is HomeContract.Intent.OnRefresh -> handleRefresh()
            is HomeContract.Intent.OnTripTapped -> handleTripTapped(intent.tripId)
            is HomeContract.Intent.OnViewAllTapped -> handleViewAllTapped(intent.section)
            is HomeContract.Intent.OnNotificationTapped -> handleNotificationTapped()
        }
    }

    private fun handleScreenDisplayed() {
        // 初期データの読み込みなどを実装
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                // TODO: 実際のデータ取得ロジックを実装
                // 仮実装としてサンプルデータを設定
                kotlinx.coroutines.delay(500)

                _state.value = _state.value.copy(
                    isLoading = false,
                    hasNotification = true,
                    ongoingTrips = listOf(
                        HomeContract.TripItem(
                            id = "1",
                            emoji = "🏔",
                            title = "北海道旅行2025",
                            dateRange = "7/1〜7/5",
                            location = "札幌",
                            progress = 0.6f,
                            daysUntilDevelopment = 3,
                            memberAvatars = emptyList(),
                            additionalMemberCount = 3
                        )
                    ),
                    completedTrips = listOf(
                        HomeContract.TripItem(
                            id = "2",
                            emoji = "🏖",
                            title = "沖縄旅行2025",
                            dateRange = "5/1〜5/4",
                            location = "沖縄",
                            photoCount = 48
                        ),
                        HomeContract.TripItem(
                            id = "3",
                            emoji = "🗼",
                            title = "東京観光",
                            dateRange = "4/10〜4/12",
                            location = "東京",
                            photoCount = 32
                        )
                    )
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
                _effect.emit(HomeContract.Effect.ShowError(e.message ?: "エラーが発生しました"))
            }
        }
    }

    private fun handleRefresh() {
        // リフレッシュは初期表示と同じロジック
        handleScreenDisplayed()
    }

    private fun handleTripTapped(tripId: String) {
        screenModelScope.launch {
            _effect.emit(HomeContract.Effect.NavigateToTripDetail(tripId))
        }
    }

    private fun handleViewAllTapped(section: HomeContract.TripSection) {
        screenModelScope.launch {
            _effect.emit(HomeContract.Effect.NavigateToTripList(section))
        }
    }

    private fun handleNotificationTapped() {
        screenModelScope.launch {
            _effect.emit(HomeContract.Effect.NavigateToNotifications)
        }
    }
}
