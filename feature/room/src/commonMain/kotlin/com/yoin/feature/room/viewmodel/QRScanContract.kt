package com.yoin.feature.room.viewmodel

/**
 * QRスキャン画面のMVIコントラクト
 *
 * 機能:
 * - QRコードのスキャン
 * - トーチ（ライト）の切り替え
 * - 画像からのQRコード読み取り
 * - 手動入力への遷移
 */
interface QRScanContract {
    /**
     * 画面の状態
     */
    data class State(
        val isScanning: Boolean = true,
        val isCameraPermissionGranted: Boolean = false,
        val isTorchEnabled: Boolean = false,
        val detectedCode: String? = null,
        val error: String? = null,
        val isProcessing: Boolean = false,
    )

    /**
     * ユーザーの操作
     */
    sealed interface Intent {
        /**
         * 閉じるボタンがタップされた
         */
        data object OnClosePressed : Intent

        /**
         * QRコードが検出された
         */
        data class OnQRCodeDetected(val code: String) : Intent

        /**
         * トーチ（ライト）の切り替え
         */
        data object OnTorchToggled : Intent

        /**
         * 画像ピッカーボタンがタップされた
         */
        data object OnImagePickerPressed : Intent

        /**
         * 手動入力ボタンがタップされた
         */
        data object OnManualInputPressed : Intent

        /**
         * カメラ権限が付与された
         */
        data object OnCameraPermissionGranted : Intent

        /**
         * カメラ権限が拒否された
         */
        data object OnCameraPermissionDenied : Intent

        /**
         * 画面が表示された
         */
        data object OnScreenDisplayed : Intent
    }

    /**
     * 副作用
     */
    sealed interface Effect {
        /**
         * 前の画面に戻る
         */
        data object NavigateBack : Effect

        /**
         * 参加確認画面に遷移
         */
        data class NavigateToJoinConfirm(val roomId: String) : Effect

        /**
         * 手動入力画面に遷移
         */
        data object NavigateToManualInput : Effect

        /**
         * カメラ権限をリクエスト
         */
        data object RequestCameraPermission : Effect

        /**
         * 画像ピッカーを開く
         */
        data object OpenImagePicker : Effect

        /**
         * エラーメッセージを表示
         */
        data class ShowError(val message: String) : Effect

        /**
         * トーチを有効化
         */
        data object EnableTorch : Effect

        /**
         * トーチを無効化
         */
        data object DisableTorch : Effect
    }
}
