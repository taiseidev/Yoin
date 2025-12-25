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
 * パスワード変更画面のScreenModel
 *
 * 注意: 現在はUI実装のみで、実際のパスワード変更ロジックは未実装です。
 */
class ChangePasswordViewModel : ScreenModel {

    private val _state = MutableStateFlow(ChangePasswordContract.State())
    val state: StateFlow<ChangePasswordContract.State> = _state.asStateFlow()

    private val _effect = Channel<ChangePasswordContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /**
     * ユーザーの意図を処理
     */
    fun handleIntent(intent: ChangePasswordContract.Intent) {
        when (intent) {
            is ChangePasswordContract.Intent.OnBackPressed -> onBackPressed()
            is ChangePasswordContract.Intent.OnCurrentPasswordChanged -> onCurrentPasswordChanged(intent.password)
            is ChangePasswordContract.Intent.OnNewPasswordChanged -> onNewPasswordChanged(intent.password)
            is ChangePasswordContract.Intent.OnConfirmPasswordChanged -> onConfirmPasswordChanged(intent.password)
            is ChangePasswordContract.Intent.OnChangePasswordPressed -> onChangePasswordPressed()
        }
    }

    private fun onBackPressed() {
        screenModelScope.launch {
            _effect.send(ChangePasswordContract.Effect.NavigateBack)
        }
    }

    private fun onCurrentPasswordChanged(password: String) {
        _state.update {
            it.copy(
                currentPassword = password,
                validationErrors = it.validationErrors.copy(currentPasswordError = null)
            )
        }
    }

    private fun onNewPasswordChanged(password: String) {
        _state.update {
            it.copy(
                newPassword = password,
                validationErrors = it.validationErrors.copy(newPasswordError = null)
            )
        }
    }

    private fun onConfirmPasswordChanged(password: String) {
        _state.update {
            it.copy(
                confirmPassword = password,
                validationErrors = it.validationErrors.copy(confirmPasswordError = null)
            )
        }
    }

    private fun onChangePasswordPressed() {
        screenModelScope.launch {
            val currentState = _state.value

            // バリデーション
            val errors = validatePasswords(
                currentState.currentPassword,
                currentState.newPassword,
                currentState.confirmPassword
            )

            if (errors != null) {
                _state.update { it.copy(validationErrors = errors) }
                return@launch
            }

            // パスワード変更処理
            _state.update { it.copy(isLoading = true) }

            try {
                // TODO: 実際のパスワード変更API呼び出し
                kotlinx.coroutines.delay(1000) // ネットワーク遅延をシミュレート

                _state.update { it.copy(isLoading = false) }
                _effect.send(ChangePasswordContract.Effect.ShowSuccess("パスワードを変更しました"))

                // 成功後、少し待ってから戻る
                kotlinx.coroutines.delay(500)
                _effect.send(ChangePasswordContract.Effect.NavigateBack)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _effect.send(ChangePasswordContract.Effect.ShowError("パスワードの変更に失敗しました"))
            }
        }
    }

    /**
     * パスワードのバリデーション
     */
    private fun validatePasswords(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): ChangePasswordContract.ValidationErrors? {
        var currentPasswordError: String? = null
        var newPasswordError: String? = null
        var confirmPasswordError: String? = null

        // 現在のパスワードのチェック
        if (currentPassword.isEmpty()) {
            currentPasswordError = "現在のパスワードを入力してください"
        }

        // 新しいパスワードのチェック
        if (newPassword.isEmpty()) {
            newPasswordError = "新しいパスワードを入力してください"
        } else if (newPassword.length < 8) {
            newPasswordError = "パスワードは8文字以上で入力してください"
        } else if (!newPassword.any { it.isDigit() }) {
            newPasswordError = "パスワードには数字を含めてください"
        } else if (!newPassword.any { it.isLetter() }) {
            newPasswordError = "パスワードには英字を含めてください"
        }

        // 確認パスワードのチェック
        if (confirmPassword.isEmpty()) {
            confirmPasswordError = "確認用パスワードを入力してください"
        } else if (newPassword != confirmPassword) {
            confirmPasswordError = "パスワードが一致しません"
        }

        return if (currentPasswordError != null || newPasswordError != null || confirmPasswordError != null) {
            ChangePasswordContract.ValidationErrors(
                currentPasswordError = currentPasswordError,
                newPasswordError = newPasswordError,
                confirmPasswordError = confirmPasswordError
            )
        } else {
            null
        }
    }
}
