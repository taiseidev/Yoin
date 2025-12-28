package com.yoin.feature.auth.viewmodel

/**
 * Welcome画面のContract
 *
 * 最初のログイン/登録選択画面
 */
object WelcomeContract {
    /**
     * State
     */
    data class State(
        val isLoading: Boolean = false
    )

    /**
     * Intent
     */
    sealed interface Intent {
        data object OnEmailLoginPressed : Intent
        data object OnRegisterPressed : Intent
        data object OnGoogleSignInPressed : Intent
        data object OnAppleSignInPressed : Intent
        data object OnGuestPressed : Intent
    }

    /**
     * Effect
     */
    sealed interface Effect {
        data object NavigateToEmailLogin : Effect
        data object NavigateToRegister : Effect
        data object NavigateToHome : Effect
        data class ShowError(val message: String) : Effect
        data class ShowSuccess(val message: String) : Effect
    }
}
