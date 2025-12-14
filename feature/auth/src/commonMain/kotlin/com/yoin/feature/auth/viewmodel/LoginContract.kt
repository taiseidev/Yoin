package com.yoin.feature.auth.viewmodel

/**
 * ログイン画面のMVIコントラクト
 */
interface LoginContract {

    /**
     * 画面の状態
     */
    data class State(
        val isLoading: Boolean = false,
        val error: String? = null,
    )

    /**
     * ユーザーの意図（アクション）
     */
    sealed interface Intent {
        data object SignInWithApple : Intent
        data object SignInWithGoogle : Intent
        data object SignInAsGuest : Intent
    }

    /**
     * 副作用（ナビゲーションなど）
     */
    sealed interface Effect {
        data object NavigateToHome : Effect
        data class ShowError(val message: String) : Effect
    }
}
