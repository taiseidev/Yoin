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

/**
 * ルーム参加確認画面のViewModel
 *
 * MVIパターンに基づいた状態管理:
 * - State: ルーム情報とニックネーム入力状態
 * - Intent: ユーザーアクション
 * - Effect: 一時的なイベント（ナビゲーション、エラー表示）
 */
class JoinConfirmViewModel : ScreenModel {
    private val _state = MutableStateFlow(JoinConfirmContract.State())
    val state: StateFlow<JoinConfirmContract.State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<JoinConfirmContract.Effect>()
    val effect: SharedFlow<JoinConfirmContract.Effect> = _effect.asSharedFlow()

    fun onIntent(intent: JoinConfirmContract.Intent) {
        when (intent) {
            is JoinConfirmContract.Intent.OnScreenDisplayed -> handleScreenDisplayed(intent.roomId)
            is JoinConfirmContract.Intent.OnNicknameChanged -> handleNicknameChanged(intent.nickname)
            is JoinConfirmContract.Intent.OnLoginAndJoinPressed -> handleLoginAndJoinPressed()
            is JoinConfirmContract.Intent.OnRegisterAndJoinPressed -> handleRegisterAndJoinPressed()
            is JoinConfirmContract.Intent.OnGuestJoinPressed -> handleGuestJoinPressed()
            is JoinConfirmContract.Intent.OnClosePressed -> handleClosePressed()
        }
    }

    private fun handleScreenDisplayed(roomId: String) {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                // TODO: 実際のルーム情報取得ロジックを実装
                delay(500)

                // サンプルデータ
                val roomInfo = JoinConfirmContract.RoomInfo(
                    id = roomId,
                    emoji = "🏔️",
                    title = "北海道旅行2025",
                    dateRange = "2025/7/1 〜 7/5",
                    destination = "北海道",
                    memberCount = 4,
                    memberAvatars = emptyList(),
                    developmentDateTime = "7/6 AM 9:00"
                )

                _state.value = _state.value.copy(
                    isLoading = false,
                    roomInfo = roomInfo
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
                _effect.emit(JoinConfirmContract.Effect.ShowError(e.message ?: "ルーム情報の取得に失敗しました"))
            }
        }
    }

    private fun handleNicknameChanged(nickname: String) {
        val error = if (nickname.isBlank()) {
            "ニックネームを入力してください"
        } else if (nickname.length > 20) {
            "ニックネームは20文字以内で入力してください"
        } else {
            null
        }

        _state.value = _state.value.copy(
            nickname = nickname,
            nicknameError = error
        )
    }

    private fun handleLoginAndJoinPressed() {
        screenModelScope.launch {
            // ログイン画面へ遷移
            _effect.emit(JoinConfirmContract.Effect.NavigateToLogin)
        }
    }

    private fun handleRegisterAndJoinPressed() {
        screenModelScope.launch {
            // 新規登録画面へ遷移
            _effect.emit(JoinConfirmContract.Effect.NavigateToRegister)
        }
    }

    private fun handleGuestJoinPressed() {
        screenModelScope.launch {
            val state = _state.value

            // ニックネームのバリデーション
            if (state.nickname.isBlank()) {
                _state.value = _state.value.copy(
                    nicknameError = "ニックネームを入力してください"
                )
                _effect.emit(JoinConfirmContract.Effect.ShowError("ニックネームを入力してください"))
                return@launch
            }

            if (state.nicknameError != null) {
                _effect.emit(JoinConfirmContract.Effect.ShowError(state.nicknameError))
                return@launch
            }

            try {
                _state.value = _state.value.copy(isLoading = true)

                // TODO: 実際のゲスト参加処理を実装
                delay(1000)

                _state.value = _state.value.copy(isLoading = false)

                // ルーム詳細画面へ遷移
                state.roomInfo?.let { roomInfo ->
                    _effect.emit(JoinConfirmContract.Effect.NavigateToRoomDetail(roomInfo.id))
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false)
                _effect.emit(JoinConfirmContract.Effect.ShowError(e.message ?: "参加に失敗しました"))
            }
        }
    }

    private fun handleClosePressed() {
        screenModelScope.launch {
            _effect.emit(JoinConfirmContract.Effect.NavigateBack)
        }
    }
}
