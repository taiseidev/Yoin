package com.yoin.feature.auth.viewmodel

/**
 * RegisterPassword画面のContract
 *
 * パスワード設定画面（新規登録の最後のステップ）
 */
object RegisterPasswordContract {
    /**
     * State
     */
    data class State(
        val isLoading: Boolean = false,
        val password: String = "",
        val passwordError: String? = null,
        val confirmPassword: String = "",
        val confirmPasswordError: String? = null,
        val isPasswordVisible: Boolean = false,
        val isConfirmPasswordVisible: Boolean = false,
        // 前画面から渡された情報
        val name: String = "",
        val email: String = ""
    )

    /**
     * Intent
     */
    sealed interface Intent {
        data class OnPasswordChanged(val password: String) : Intent
        data class OnConfirmPasswordChanged(val confirmPassword: String) : Intent
        data object OnPasswordVisibilityToggled : Intent
        data object OnConfirmPasswordVisibilityToggled : Intent
        data object OnRegisterPressed : Intent
    }

    /**
     * Effect
     */
    sealed interface Effect {
        data object NavigateToHome : Effect
        data class ShowError(val message: String) : Effect
        data class ShowSuccess(val message: String) : Effect
    }
}
