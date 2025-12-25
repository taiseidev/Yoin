package com.yoin.feature.camera.viewmodel

/**
 * カメラ画面のMVI Contract
 */
object CameraContract {
    /**
     * UI状態
     */
    data class State(
        val isLoading: Boolean = false,
        val remainingPhotos: Int = 12,
        val location: String? = null,
        val isLocationLoading: Boolean = true,
        val flashMode: FlashMode = FlashMode.OFF,
        val cameraFacing: CameraFacing = CameraFacing.BACK,
        val errorMessage: String? = null
    )

    /**
     * フラッシュモード
     */
    enum class FlashMode {
        OFF,
        ON,
        AUTO
    }

    /**
     * カメラの向き
     */
    enum class CameraFacing {
        FRONT,
        BACK
    }

    /**
     * ユーザーインテント（アクション）
     */
    sealed interface Intent {
        /**
         * 画面が表示された
         */
        data class OnScreenDisplayed(val tripId: String) : Intent

        /**
         * 閉じるボタンをタップ
         */
        data object OnClosePressed : Intent

        /**
         * シャッターボタンをタップ
         */
        data object OnShutterPressed : Intent

        /**
         * フラッシュボタンをタップ
         */
        data object OnFlashToggle : Intent

        /**
         * カメラ切り替えボタンをタップ
         */
        data object OnCameraSwitch : Intent
    }

    /**
     * 一時的なイベント（副作用）
     */
    sealed interface Effect {
        /**
         * エラーメッセージを表示
         */
        data class ShowError(val message: String) : Effect

        /**
         * 前の画面に戻る
         */
        data object NavigateBack : Effect

        /**
         * 写真撮影成功
         */
        data class PhotoCaptured(val photoPath: String) : Effect

        /**
         * プレビュー画面へ遷移
         */
        data class NavigateToPreview(val photoPath: String) : Effect
    }
}
