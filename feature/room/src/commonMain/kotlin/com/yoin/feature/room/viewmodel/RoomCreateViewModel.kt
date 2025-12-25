package com.yoin.feature.room.viewmodel

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
import kotlin.random.Random

/**
 * ルーム作成画面のViewModel
 *
 * MVIパターンに基づいた状態管理:
 * - State: フォームの入力状態とバリデーションエラー
 * - Intent: ユーザーアクション
 * - Effect: 一時的なイベント（ピッカー表示、ナビゲーション）
 */
class RoomCreateViewModel : ScreenModel {
    private val _state = MutableStateFlow(RoomCreateContract.State())
    val state: StateFlow<RoomCreateContract.State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<RoomCreateContract.Effect>()
    val effect: SharedFlow<RoomCreateContract.Effect> = _effect.asSharedFlow()

    fun onIntent(intent: RoomCreateContract.Intent) {
        when (intent) {
            is RoomCreateContract.Intent.OnScreenDisplayed -> handleScreenDisplayed()
            is RoomCreateContract.Intent.OnTripTitleChanged -> handleTripTitleChanged(intent.title)
            is RoomCreateContract.Intent.OnEmojiSelected -> handleEmojiSelected(intent.emoji)
            is RoomCreateContract.Intent.OnStartDateChanged -> handleStartDateChanged(intent.date)
            is RoomCreateContract.Intent.OnEndDateChanged -> handleEndDateChanged(intent.date)
            is RoomCreateContract.Intent.OnDestinationChanged -> handleDestinationChanged(intent.destination)
            is RoomCreateContract.Intent.OnStartDatePickerClicked -> handleStartDatePickerClicked()
            is RoomCreateContract.Intent.OnEndDatePickerClicked -> handleEndDatePickerClicked()
            is RoomCreateContract.Intent.OnEmojiPickerClicked -> handleEmojiPickerClicked()
            is RoomCreateContract.Intent.OnCreateButtonClicked -> handleCreateButtonClicked()
            is RoomCreateContract.Intent.OnBackPressed -> handleBackPressed()
        }
    }

    private fun handleScreenDisplayed() {
        // 初期化処理（必要に応じて）
    }

    private fun handleTripTitleChanged(title: String) {
        _state.value = _state.value.copy(
            tripTitle = title,
            titleError = if (title.isBlank()) "旅行名を入力してください" else null
        )
        validateForm()
    }

    private fun handleEmojiSelected(emoji: String) {
        _state.value = _state.value.copy(emoji = emoji)
    }

    private fun handleStartDateChanged(date: String) {
        val endDate = _state.value.endDate
        val startDateError = when {
            date.isBlank() -> "開始日を選択してください"
            else -> null
        }
        val endDateError = if (endDate.isNotBlank() && date > endDate) {
            "終了日は開始日以降を選択してください"
        } else null

        _state.value = _state.value.copy(
            startDate = date,
            startDateError = startDateError,
            endDateError = endDateError
        )
        validateForm()
    }

    private fun handleEndDateChanged(date: String) {
        val startDate = _state.value.startDate
        val endDateError = when {
            date.isBlank() -> "終了日を選択してください"
            startDate.isNotBlank() && date < startDate -> "終了日は開始日以降を選択してください"
            else -> null
        }

        _state.value = _state.value.copy(
            endDate = date,
            endDateError = endDateError
        )
        validateForm()
    }

    private fun handleDestinationChanged(destination: String) {
        _state.value = _state.value.copy(
            destination = destination,
            destinationError = if (destination.isBlank()) "目的地を入力してください" else null
        )
        validateForm()
    }

    private fun handleStartDatePickerClicked() {
        screenModelScope.launch {
            _effect.emit(RoomCreateContract.Effect.ShowStartDatePicker)
        }
    }

    private fun handleEndDatePickerClicked() {
        screenModelScope.launch {
            _effect.emit(RoomCreateContract.Effect.ShowEndDatePicker)
        }
    }

    private fun handleEmojiPickerClicked() {
        screenModelScope.launch {
            _effect.emit(RoomCreateContract.Effect.ShowEmojiPicker)
        }
    }

    private fun handleCreateButtonClicked() {
        screenModelScope.launch {
            val state = _state.value

            // 最終バリデーション
            if (!state.isFormValid) {
                _effect.emit(RoomCreateContract.Effect.ShowError("全ての項目を正しく入力してください"))
                return@launch
            }

            _state.value = _state.value.copy(isLoading = true)

            try {
                // TODO: 実際のルーム作成API呼び出し
                delay(1000)

                // 仮実装：ランダムIDを生成
                val roomId = "room_${Random.nextInt(100000, 999999)}"

                _state.value = _state.value.copy(isLoading = false)
                _effect.emit(RoomCreateContract.Effect.ShowSuccess("ルームを作成しました"))
                _effect.emit(RoomCreateContract.Effect.NavigateToRoomDetail(roomId))
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false)
                _effect.emit(RoomCreateContract.Effect.ShowError(e.message ?: "ルームの作成に失敗しました"))
            }
        }
    }

    private fun handleBackPressed() {
        screenModelScope.launch {
            _effect.emit(RoomCreateContract.Effect.NavigateBack)
        }
    }

    private fun validateForm() {
        val state = _state.value
        val isValid = state.tripTitle.isNotBlank() &&
                state.startDate.isNotBlank() &&
                state.endDate.isNotBlank() &&
                state.destination.isNotBlank() &&
                state.titleError == null &&
                state.startDateError == null &&
                state.endDateError == null &&
                state.destinationError == null

        _state.value = _state.value.copy(isFormValid = isValid)
    }
}
