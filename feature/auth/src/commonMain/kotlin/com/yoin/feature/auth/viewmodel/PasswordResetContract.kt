package com.yoin.feature.auth.viewmodel

/**
 * パスワードリセット画面のMVIコントラクト
 */
interface PasswordResetContract {

    /**
     * 画面の状態
     */
    data class State(
        val isLoading: Boolean = false,
        val email: String = "",
        val emailError: String? = null,
        val isEmailSent: Boolean = false,
        val error: String? = null,
    )

    /**
     * ユーザーの意図（アクション）
     */
    sealed interface Intent {
        data class OnEmailChanged(val email: String) : Intent
        data object OnSendResetLinkPressed : Intent
        data object OnBackToLoginPressed : Intent
    }

    /**
     * 副作用（ナビゲーションなど）
     */
    sealed interface Effect {
        data object NavigateToLogin : Effect
        data class ShowError(val message: String) : Effect
        data class ShowSuccess(val message: String) : Effect
    }
}
