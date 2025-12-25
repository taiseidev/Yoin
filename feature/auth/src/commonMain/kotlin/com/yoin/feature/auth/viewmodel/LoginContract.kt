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
        val email: String = "",
        val emailError: String? = null,
        val password: String = "",
        val passwordError: String? = null,
        val isPasswordVisible: Boolean = false,
        val error: String? = null,
    )

    /**
     * ユーザーの意図（アクション）
     */
    sealed interface Intent {
        data class OnEmailChanged(val email: String) : Intent
        data class OnPasswordChanged(val password: String) : Intent
        data object OnPasswordVisibilityToggled : Intent
        data object OnLoginPressed : Intent
        data object OnForgotPasswordPressed : Intent
        data object OnRegisterPressed : Intent
        data object SignInWithApple : Intent
        data object SignInWithGoogle : Intent
        data object SignInAsGuest : Intent
    }

    /**
     * 副作用（ナビゲーションなど）
     */
    sealed interface Effect {
        data object NavigateToHome : Effect
        data object NavigateToRegister : Effect
        data object NavigateToForgotPassword : Effect
        data class ShowError(val message: String) : Effect
        data class ShowSuccess(val message: String) : Effect
    }
}
