package com.yoin.feature.camera.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 写真確認画面のScreenModel
 *
 * 責務:
 * - 撮影した写真のプレビュー情報を管理
 * - 写真の保存処理
 * - 写真の削除処理
 */
class PhotoConfirmViewModel : ScreenModel {

    private val _state = MutableStateFlow(PhotoConfirmContract.State())
    val state: StateFlow<PhotoConfirmContract.State> = _state.asStateFlow()

    private val _effect = Channel<PhotoConfirmContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /**
     * ユーザーの意図を処理
     */
    fun onIntent(intent: PhotoConfirmContract.Intent) {
        when (intent) {
            is PhotoConfirmContract.Intent.OnScreenDisplayed -> onScreenDisplayed(intent.photoPath, intent.tripId)
            is PhotoConfirmContract.Intent.OnSavePressed -> onSavePressed()
            is PhotoConfirmContract.Intent.OnDeletePressed -> onDeletePressed()
            is PhotoConfirmContract.Intent.OnClosePressed -> onClosePressed()
        }
    }

    private fun onScreenDisplayed(photoPath: String, tripId: String) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // TODO: 実際の位置情報取得処理と時刻取得処理を実装
                delay(500)

                // モックの時刻データ
                val timestamp = "14:32"

                _state.update {
                    it.copy(
                        isLoading = false,
                        photoPath = photoPath,
                        location = "北海道・札幌市", // モックデータ
                        timestamp = timestamp
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _effect.send(PhotoConfirmContract.Effect.ShowError("写真情報の取得に失敗しました"))
            }
        }
    }

    private fun onSavePressed() {
        screenModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            try {
                // TODO: 実際の写真保存処理を実装
                delay(1000) // API呼び出しをシミュレート

                _state.update { it.copy(isSaving = false) }
                _effect.send(PhotoConfirmContract.Effect.ShowSuccess("写真を保存しました"))
                delay(500)
                _effect.send(PhotoConfirmContract.Effect.NavigateToRoomDetail)
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false) }
                _effect.send(PhotoConfirmContract.Effect.ShowError("写真の保存に失敗しました"))
            }
        }
    }

    private fun onDeletePressed() {
        screenModelScope.launch {
            try {
                // TODO: 実際の写真削除処理を実装
                _effect.send(PhotoConfirmContract.Effect.ShowSuccess("写真を削除しました"))
                delay(300)
                _effect.send(PhotoConfirmContract.Effect.NavigateToCamera)
            } catch (e: Exception) {
                _effect.send(PhotoConfirmContract.Effect.ShowError("写真の削除に失敗しました"))
            }
        }
    }

    private fun onClosePressed() {
        screenModelScope.launch {
            _effect.send(PhotoConfirmContract.Effect.NavigateToCamera)
        }
    }
}
