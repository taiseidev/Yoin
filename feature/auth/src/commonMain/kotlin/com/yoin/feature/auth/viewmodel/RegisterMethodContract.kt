package com.yoin.feature.auth.viewmodel

/**
 * Register Method画面のContract
 *
 * 登録方法選択画面
 */
object RegisterMethodContract {
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
        data object OnEmailRegisterPressed : Intent
        data object OnGoogleRegisterPressed : Intent
        data object OnAppleRegisterPressed : Intent
    }

    /**
     * Effect
     */
    sealed interface Effect {
        data object NavigateToEmailRegister : Effect
        data object NavigateToHome : Effect
        data class ShowError(val message: String) : Effect
        data class ShowSuccess(val message: String) : Effect
    }
}
