package com.yoin.feature.room.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 手動入力画面のScreenModel
 */
class ManualInputViewModel : ScreenModel {

    private val _state = MutableStateFlow(ManualInputContract.State())
    val state: StateFlow<ManualInputContract.State> = _state.asStateFlow()

    private val _effect = Channel<ManualInputContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /**
     * ユーザーの意図を処理
     */
    fun handleIntent(intent: ManualInputContract.Intent) {
        when (intent) {
            is ManualInputContract.Intent.OnRoomCodeChanged -> onRoomCodeChanged(intent.code)
            is ManualInputContract.Intent.OnJoinPressed -> onJoinPressed()
            is ManualInputContract.Intent.OnCancelPressed -> onCancelPressed()
        }
    }

    private fun onRoomCodeChanged(code: String) {
        // 英数字のみ許可（大文字変換）
        val sanitizedCode = code.filter { it.isLetterOrDigit() }.uppercase()
        _state.update {
            it.copy(
                roomCode = sanitizedCode,
                isCodeValid = true,
                errorMessage = null
            )
        }
    }

    private fun onJoinPressed() {
        screenModelScope.launch {
            val currentState = _state.value

            if (currentState.roomCode.isBlank()) {
                _state.update {
                    it.copy(
                        isCodeValid = false,
                        errorMessage = "ルームコードを入力してください"
                    )
                }
                return@launch
            }

            if (currentState.roomCode.length < 6) {
                _state.update {
                    it.copy(
                        isCodeValid = false,
                        errorMessage = "ルームコードは6文字以上である必要があります"
                    )
                }
                return@launch
            }

            _state.update { it.copy(isLoading = true) }

            // TODO: 実際のルーム検証処理を実装
            kotlinx.coroutines.delay(500)

            // 仮でルームIDとして使用
            val roomId = currentState.roomCode

            _state.update { it.copy(isLoading = false) }
            _effect.send(ManualInputContract.Effect.NavigateToJoinConfirm(roomId))
        }
    }

    private fun onCancelPressed() {
        screenModelScope.launch {
            _effect.send(ManualInputContract.Effect.NavigateBack)
        }
    }
}
