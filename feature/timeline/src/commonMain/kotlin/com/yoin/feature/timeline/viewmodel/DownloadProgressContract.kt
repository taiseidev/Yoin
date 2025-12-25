package com.yoin.feature.timeline.viewmodel

/**
 * ダウンロード進捗ダイアログのMVIコントラクト
 *
 * 機能:
 * - ダウンロード進捗の表示
 * - ダウンロードのキャンセル
 */
interface DownloadProgressContract {
    /**
     * 画面の状態
     */
    data class State(
        val isDownloading: Boolean = true,
        val currentCount: Int = 0,
        val totalCount: Int = 0,
        val progressPercentage: Int = 0,
        val estimatedSeconds: Int = 0,
    ) {
        val progressText: String
            get() = "$currentCount / $totalCount 枚完了"

        val estimatedTimeText: String
            get() = "残り約 $estimatedSeconds 秒"

        val progressFloat: Float
            get() = if (totalCount > 0) currentCount.toFloat() / totalCount else 0f
    }

    /**
     * ユーザーの操作
     */
    sealed interface Intent {
        /**
         * ダイアログが表示された
         */
        data object OnDialogDisplayed : Intent

        /**
         * キャンセルボタンがタップされた
         */
        data object OnCancelPressed : Intent

        /**
         * 進捗が更新された（内部用）
         */
        data class OnProgressUpdated(
            val currentCount: Int,
            val totalCount: Int
        ) : Intent
    }

    /**
     * 副作用
     */
    sealed interface Effect {
        /**
         * ダウンロードをキャンセル
         */
        data object CancelDownload : Effect

        /**
         * ダイアログを閉じる
         */
        data object DismissDialog : Effect

        /**
         * ダウンロード完了を通知
         */
        data object ShowDownloadComplete : Effect

        /**
         * エラーメッセージを表示
         */
        data class ShowError(val message: String) : Effect
    }
}
