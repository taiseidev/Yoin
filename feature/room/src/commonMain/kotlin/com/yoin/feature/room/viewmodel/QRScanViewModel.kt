package com.yoin.feature.room.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * QRスキャン画面のViewModel
 *
 * 機能:
 * - QRコードの検出と検証
 * - カメラ権限の管理
 * - トーチ（ライト）の制御
 * - 画像からのQRコード読み取り
 */
class QRScanViewModel : ScreenModel {
    private val _state = MutableStateFlow(QRScanContract.State())
    val state: StateFlow<QRScanContract.State> = _state.asStateFlow()

    private val _effect = Channel<QRScanContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /**
     * Intentを処理
     */
    fun onIntent(intent: QRScanContract.Intent) {
        when (intent) {
            is QRScanContract.Intent.OnClosePressed -> handleClosePressed()
            is QRScanContract.Intent.OnQRCodeDetected -> handleQRCodeDetected(intent.code)
            is QRScanContract.Intent.OnTorchToggled -> handleTorchToggled()
            is QRScanContract.Intent.OnImagePickerPressed -> handleImagePickerPressed()
            is QRScanContract.Intent.OnManualInputPressed -> handleManualInputPressed()
            is QRScanContract.Intent.OnCameraPermissionGranted -> handleCameraPermissionGranted()
            is QRScanContract.Intent.OnCameraPermissionDenied -> handleCameraPermissionDenied()
            is QRScanContract.Intent.OnScreenDisplayed -> handleScreenDisplayed()
        }
    }

    /**
     * 画面表示時の処理
     */
    private fun handleScreenDisplayed() {
        screenModelScope.launch {
            // カメラ権限をリクエスト
            _effect.send(QRScanContract.Effect.RequestCameraPermission)
        }
    }

    /**
     * 閉じるボタンの処理
     */
    private fun handleClosePressed() {
        screenModelScope.launch {
            _effect.send(QRScanContract.Effect.NavigateBack)
        }
    }

    /**
     * QRコード検出時の処理
     */
    private fun handleQRCodeDetected(code: String) {
        if (_state.value.isProcessing) {
            return // 処理中は無視
        }

        _state.value = _state.value.copy(
            isProcessing = true,
            detectedCode = code
        )

        screenModelScope.launch {
            try {
                // QRコードの検証とルームID抽出
                val roomId = extractRoomIdFromQRCode(code)

                if (roomId != null) {
                    // 参加確認画面に遷移
                    _effect.send(QRScanContract.Effect.NavigateToJoinConfirm(roomId))
                } else {
                    // 無効なQRコード
                    _effect.send(QRScanContract.Effect.ShowError("無効なQRコードです"))
                    _state.value = _state.value.copy(
                        isProcessing = false,
                        detectedCode = null
                    )
                }
            } catch (e: Exception) {
                _effect.send(QRScanContract.Effect.ShowError("QRコードの読み取りに失敗しました"))
                _state.value = _state.value.copy(
                    isProcessing = false,
                    detectedCode = null,
                    error = e.message
                )
            }
        }
    }

    /**
     * トーチ切り替えの処理
     */
    private fun handleTorchToggled() {
        val newTorchState = !_state.value.isTorchEnabled

        _state.value = _state.value.copy(isTorchEnabled = newTorchState)

        screenModelScope.launch {
            if (newTorchState) {
                _effect.send(QRScanContract.Effect.EnableTorch)
            } else {
                _effect.send(QRScanContract.Effect.DisableTorch)
            }
        }
    }

    /**
     * 画像ピッカーの処理
     */
    private fun handleImagePickerPressed() {
        screenModelScope.launch {
            _effect.send(QRScanContract.Effect.OpenImagePicker)
        }
    }

    /**
     * 手動入力の処理
     */
    private fun handleManualInputPressed() {
        screenModelScope.launch {
            _effect.send(QRScanContract.Effect.NavigateToManualInput)
        }
    }

    /**
     * カメラ権限付与時の処理
     */
    private fun handleCameraPermissionGranted() {
        _state.value = _state.value.copy(
            isCameraPermissionGranted = true,
            isScanning = true
        )
    }

    /**
     * カメラ権限拒否時の処理
     */
    private fun handleCameraPermissionDenied() {
        _state.value = _state.value.copy(
            isCameraPermissionGranted = false,
            isScanning = false
        )

        screenModelScope.launch {
            _effect.send(QRScanContract.Effect.ShowError("カメラの権限が必要です"))
        }
    }

    /**
     * QRコードからルームIDを抽出
     *
     * QRコードのフォーマット:
     * - "yoin://room/join/{roomId}"
     * - "https://yoin.app/room/join/{roomId}"
     *
     * @param code QRコード文字列
     * @return ルームID（抽出できない場合はnull）
     */
    private fun extractRoomIdFromQRCode(code: String): String? {
        return when {
            code.startsWith("yoin://room/join/") -> {
                code.removePrefix("yoin://room/join/")
            }
            code.startsWith("https://yoin.app/room/join/") -> {
                code.removePrefix("https://yoin.app/room/join/")
            }
            else -> null
        }
    }
}
