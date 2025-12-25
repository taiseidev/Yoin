package com.yoin.feature.settings.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * アカウント削除画面のScreenModel
 *
 * 注意: 現在はUI実装のみで、実際のアカウント削除ロジックは未実装です。
 */
class DeleteAccountViewModel : ScreenModel {

    private val _state = MutableStateFlow(DeleteAccountContract.State())
    val state: StateFlow<DeleteAccountContract.State> = _state.asStateFlow()

    private val _effect = Channel<DeleteAccountContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    companion object {
        private const val CONFIRMATION_TEXT = "DELETE"
    }

    /**
     * ユーザーの意図を処理
     */
    fun handleIntent(intent: DeleteAccountContract.Intent) {
        when (intent) {
            is DeleteAccountContract.Intent.OnBackPressed -> onBackPressed()
            is DeleteAccountContract.Intent.OnPasswordChanged -> onPasswordChanged(intent.password)
            is DeleteAccountContract.Intent.OnConfirmationTextChanged -> onConfirmationTextChanged(intent.text)
            is DeleteAccountContract.Intent.OnReasonChanged -> onReasonChanged(intent.reason)
            is DeleteAccountContract.Intent.OnDeleteAccountPressed -> onDeleteAccountPressed()
            is DeleteAccountContract.Intent.OnConfirmDialogDismissed -> onConfirmDialogDismissed()
            is DeleteAccountContract.Intent.OnConfirmDialogConfirmed -> onConfirmDialogConfirmed()
        }
    }

    private fun onBackPressed() {
        screenModelScope.launch {
            _effect.send(DeleteAccountContract.Effect.NavigateBack)
        }
    }

    private fun onPasswordChanged(password: String) {
        _state.update {
            it.copy(
                password = password,
                validationErrors = it.validationErrors.copy(passwordError = null)
            )
        }
    }

    private fun onConfirmationTextChanged(text: String) {
        _state.update {
            it.copy(
                confirmationText = text,
                validationErrors = it.validationErrors.copy(confirmationTextError = null)
            )
        }
    }

    private fun onReasonChanged(reason: String) {
        _state.update {
            it.copy(reasonForDeletion = reason)
        }
    }

    private fun onDeleteAccountPressed() {
        screenModelScope.launch {
            val currentState = _state.value

            // バリデーション
            val errors = validateInput(currentState.password, currentState.confirmationText)

            if (errors != null) {
                _state.update { it.copy(validationErrors = errors) }
                return@launch
            }

            // 確認ダイアログを表示
            _state.update { it.copy(showConfirmDialog = true) }
        }
    }

    private fun onConfirmDialogDismissed() {
        _state.update { it.copy(showConfirmDialog = false) }
    }

    private fun onConfirmDialogConfirmed() {
        screenModelScope.launch {
            _state.update { it.copy(showConfirmDialog = false, isLoading = true) }

            try {
                // TODO: 実際のアカウント削除API呼び出し
                kotlinx.coroutines.delay(1500) // ネットワーク遅延をシミュレート

                _state.update { it.copy(isLoading = false) }

                // 削除成功後、ログイン画面に遷移
                _effect.send(DeleteAccountContract.Effect.NavigateToLogin)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _effect.send(DeleteAccountContract.Effect.ShowError("アカウントの削除に失敗しました"))
            }
        }
    }

    /**
     * 入力のバリデーション
     */
    private fun validateInput(
        password: String,
        confirmationText: String
    ): DeleteAccountContract.ValidationErrors? {
        var passwordError: String? = null
        var confirmationTextError: String? = null

        // パスワードのチェック
        if (password.isEmpty()) {
            passwordError = "パスワードを入力してください"
        }

        // 確認テキストのチェック
        if (confirmationText.isEmpty()) {
            confirmationTextError = "「$CONFIRMATION_TEXT」と入力してください"
        } else if (confirmationText != CONFIRMATION_TEXT) {
            confirmationTextError = "入力が正しくありません"
        }

        return if (passwordError != null || confirmationTextError != null) {
            DeleteAccountContract.ValidationErrors(
                passwordError = passwordError,
                confirmationTextError = confirmationTextError
            )
        } else {
            null
        }
    }
}
