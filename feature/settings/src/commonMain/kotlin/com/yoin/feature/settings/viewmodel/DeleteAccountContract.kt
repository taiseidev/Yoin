package com.yoin.feature.settings.viewmodel

/**
 * アカウント削除画面のMVIコントラクト
 */
interface DeleteAccountContract {

    /**
     * 画面の状態
     */
    data class State(
        val password: String = "",
        val confirmationText: String = "",
        val reasonForDeletion: String = "",
        val isLoading: Boolean = false,
        val showConfirmDialog: Boolean = false,
        val validationErrors: ValidationErrors = ValidationErrors(),
    )

    /**
     * バリデーションエラー
     */
    data class ValidationErrors(
        val passwordError: String? = null,
        val confirmationTextError: String? = null,
    )

    /**
     * ユーザーの意図（アクション）
     */
    sealed interface Intent {
        data object OnBackPressed : Intent
        data class OnPasswordChanged(val password: String) : Intent
        data class OnConfirmationTextChanged(val text: String) : Intent
        data class OnReasonChanged(val reason: String) : Intent
        data object OnDeleteAccountPressed : Intent
        data object OnConfirmDialogDismissed : Intent
        data object OnConfirmDialogConfirmed : Intent
    }

    /**
     * 副作用（ナビゲーションなど）
     */
    sealed interface Effect {
        data object NavigateBack : Effect
        data object NavigateToLogin : Effect
        data class ShowError(val message: String) : Effect
        data class ShowWarning(val message: String) : Effect
    }
}
