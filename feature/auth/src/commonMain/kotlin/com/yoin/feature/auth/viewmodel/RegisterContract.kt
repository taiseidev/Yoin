package com.yoin.feature.auth.viewmodel

/**
 * 新規登録画面のMVIコントラクト
 */
interface RegisterContract {

    /**
     * 画面の状態
     */
    data class State(
        val isLoading: Boolean = false,
        val name: String = "",
        val nameError: String? = null,
        val email: String = "",
        val emailError: String? = null,
        val password: String = "",
        val passwordError: String? = null,
        val confirmPassword: String = "",
        val confirmPasswordError: String? = null,
        val isPasswordVisible: Boolean = false,
        val isConfirmPasswordVisible: Boolean = false,
        val error: String? = null,
    )

    /**
     * ユーザーの意図（アクション）
     */
    sealed interface Intent {
        data class OnNameChanged(val name: String) : Intent
        data class OnEmailChanged(val email: String) : Intent
        data class OnPasswordChanged(val password: String) : Intent
        data class OnConfirmPasswordChanged(val confirmPassword: String) : Intent
        data object OnPasswordVisibilityToggled : Intent
        data object OnConfirmPasswordVisibilityToggled : Intent
        data object OnRegisterPressed : Intent
        data object OnGoogleRegisterPressed : Intent
        data object OnAppleRegisterPressed : Intent
        data object OnLoginPressed : Intent
    }

    /**
     * 副作用（ナビゲーションなど）
     */
    sealed interface Effect {
        data object NavigateToHome : Effect
        data object NavigateToLogin : Effect
        data class ShowError(val message: String) : Effect
        data class ShowSuccess(val message: String) : Effect
    }
}
