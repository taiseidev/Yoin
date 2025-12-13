package com.taiseidev.yoin.ui.screens.splash

import com.yoin.domain.model.InitializationState

/**
 * スプラッシュ画面のMVI Contract
 */
object SplashContract {
    /**
     * UI状態
     */
    data class State(
        val initializationState: InitializationState = InitializationState.NotStarted
    )

    /**
     * ユーザーアクション
     */
    sealed interface Intent {
        /**
         * 初期化開始
         */
        data object StartInitialization : Intent
    }

    /**
     * 一時的なイベント
     */
    sealed interface Effect {
        /**
         * メイン画面へ遷移
         */
        data object NavigateToMain : Effect

        /**
         * エラー表示
         */
        data class ShowError(val message: String) : Effect
    }
}
