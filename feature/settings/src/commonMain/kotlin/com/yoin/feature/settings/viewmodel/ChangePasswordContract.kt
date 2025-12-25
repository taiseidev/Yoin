package com.yoin.feature.settings.viewmodel

/**
 * パスワード変更画面のMVIコントラクト
 */
interface ChangePasswordContract {

    /**
     * 画面の状態
     */
    data class State(
        val currentPassword: String = "",
        val newPassword: String = "",
        val confirmPassword: String = "",
        val isLoading: Boolean = false,
        val validationErrors: ValidationErrors = ValidationErrors(),
    )

    /**
     * バリデーションエラー
     */
    data class ValidationErrors(
        val currentPasswordError: String? = null,
        val newPasswordError: String? = null,
        val confirmPasswordError: String? = null,
    )

    /**
     * ユーザーの意図（アクション）
     */
    sealed interface Intent {
        data object OnBackPressed : Intent
        data class OnCurrentPasswordChanged(val password: String) : Intent
        data class OnNewPasswordChanged(val password: String) : Intent
        data class OnConfirmPasswordChanged(val password: String) : Intent
        data object OnChangePasswordPressed : Intent
    }

    /**
     * 副作用（ナビゲーションなど）
     */
    sealed interface Effect {
        data object NavigateBack : Effect
        data class ShowError(val message: String) : Effect
        data class ShowSuccess(val message: String) : Effect
    }
}
