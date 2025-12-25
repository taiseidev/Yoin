package com.yoin.feature.camera.viewmodel

/**
 * 写真確認画面のMVIコントラクト
 *
 * 撮影した写真のプレビューと保存確認を行う画面
 * Yoinの特徴として、一度撮影したら撮り直しはできない
 */
object PhotoConfirmContract {

    /**
     * UI状態
     */
    data class State(
        val isLoading: Boolean = false,
        val photoPath: String = "",
        val location: String? = null,
        val timestamp: String = "",
        val isSaving: Boolean = false,
        val errorMessage: String? = null
    )

    /**
     * ユーザーインテント（アクション）
     */
    sealed interface Intent {
        /**
         * 画面が表示された
         */
        data class OnScreenDisplayed(val photoPath: String, val tripId: String) : Intent

        /**
         * 写真を保存
         */
        data object OnSavePressed : Intent

        /**
         * 写真を削除してカメラに戻る
         */
        data object OnDeletePressed : Intent

        /**
         * 閉じるボタンをタップ
         */
        data object OnClosePressed : Intent
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
         * 成功メッセージを表示
         */
        data class ShowSuccess(val message: String) : Effect

        /**
         * カメラ画面に戻る
         */
        data object NavigateToCamera : Effect

        /**
         * ルーム詳細画面に戻る
         */
        data object NavigateToRoomDetail : Effect

        /**
         * 確認ダイアログを表示
         */
        data class ShowConfirmDialog(val message: String) : Effect
    }
}
