package com.yoin.feature.camera.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * カメラ画面のViewModel
 *
 * MVIパターンに基づいた状態管理:
 * - State: UI状態
 * - Intent: ユーザーアクション
 * - Effect: 一時的なイベント
 */
class CameraViewModel : ScreenModel {
    private val _state = MutableStateFlow(CameraContract.State())
    val state: StateFlow<CameraContract.State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<CameraContract.Effect>()
    val effect: SharedFlow<CameraContract.Effect> = _effect.asSharedFlow()

    /**
     * ユーザーインテントを処理
     */
    fun onIntent(intent: CameraContract.Intent) {
        when (intent) {
            is CameraContract.Intent.OnScreenDisplayed -> handleScreenDisplayed(intent.tripId)
            is CameraContract.Intent.OnClosePressed -> handleClosePressed()
            is CameraContract.Intent.OnShutterPressed -> handleShutterPressed()
            is CameraContract.Intent.OnFlashToggle -> handleFlashToggle()
            is CameraContract.Intent.OnCameraSwitch -> handleCameraSwitch()
        }
    }

    private fun handleScreenDisplayed(tripId: String) {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                // TODO: 実際のデータ取得ロジックを実装
                // 位置情報の取得を開始
                kotlinx.coroutines.delay(500)

                _state.value = _state.value.copy(
                    isLoading = false,
                    remainingPhotos = 12
                )

                // 位置情報を取得（仮実装）
                kotlinx.coroutines.delay(1500)
                _state.value = _state.value.copy(
                    location = "札幌市中央区",
                    isLocationLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
                _effect.emit(CameraContract.Effect.ShowError(e.message ?: "エラーが発生しました"))
            }
        }
    }

    private fun handleClosePressed() {
        screenModelScope.launch {
            _effect.emit(CameraContract.Effect.NavigateBack)
        }
    }

    private fun handleShutterPressed() {
        screenModelScope.launch {
            try {
                // TODO: 実際の撮影処理を実装
                // 仮実装として写真パスを生成
                val randomId = Random.nextInt(100000, 999999)
                val photoPath = "photo_$randomId.jpg"

                _effect.emit(CameraContract.Effect.PhotoCaptured(photoPath))
                _effect.emit(CameraContract.Effect.NavigateToPreview(photoPath))
            } catch (e: Exception) {
                _effect.emit(CameraContract.Effect.ShowError(e.message ?: "撮影に失敗しました"))
            }
        }
    }

    private fun handleFlashToggle() {
        val currentFlash = _state.value.flashMode
        val nextFlash = when (currentFlash) {
            CameraContract.FlashMode.OFF -> CameraContract.FlashMode.ON
            CameraContract.FlashMode.ON -> CameraContract.FlashMode.AUTO
            CameraContract.FlashMode.AUTO -> CameraContract.FlashMode.OFF
        }
        _state.value = _state.value.copy(flashMode = nextFlash)
    }

    private fun handleCameraSwitch() {
        val currentFacing = _state.value.cameraFacing
        val nextFacing = when (currentFacing) {
            CameraContract.CameraFacing.BACK -> CameraContract.CameraFacing.FRONT
            CameraContract.CameraFacing.FRONT -> CameraContract.CameraFacing.BACK
        }
        _state.value = _state.value.copy(cameraFacing = nextFacing)
    }
}
