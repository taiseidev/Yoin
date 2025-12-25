package com.yoin.feature.auth.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 新規登録画面のScreenModel
 *
 * 責務:
 * - ユーザー入力の管理と検証
 * - 新規登録処理の実行
 * - ソーシャルログイン処理
 */
class RegisterViewModel : ScreenModel {

    private val _state = MutableStateFlow(RegisterContract.State())
    val state: StateFlow<RegisterContract.State> = _state.asStateFlow()

    private val _effect = Channel<RegisterContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /**
     * ユーザーの意図を処理
     */
    fun onIntent(intent: RegisterContract.Intent) {
        when (intent) {
            is RegisterContract.Intent.OnNameChanged -> onNameChanged(intent.name)
            is RegisterContract.Intent.OnEmailChanged -> onEmailChanged(intent.email)
            is RegisterContract.Intent.OnPasswordChanged -> onPasswordChanged(intent.password)
            is RegisterContract.Intent.OnConfirmPasswordChanged -> onConfirmPasswordChanged(intent.confirmPassword)
            is RegisterContract.Intent.OnPasswordVisibilityToggled -> onPasswordVisibilityToggled()
            is RegisterContract.Intent.OnConfirmPasswordVisibilityToggled -> onConfirmPasswordVisibilityToggled()
            is RegisterContract.Intent.OnRegisterPressed -> onRegisterPressed()
            is RegisterContract.Intent.OnGoogleRegisterPressed -> onGoogleRegisterPressed()
            is RegisterContract.Intent.OnAppleRegisterPressed -> onAppleRegisterPressed()
            is RegisterContract.Intent.OnLoginPressed -> onLoginPressed()
        }
    }

    private fun onNameChanged(name: String) {
        _state.update { it.copy(name = name, nameError = null) }
    }

    private fun onEmailChanged(email: String) {
        _state.update { it.copy(email = email, emailError = null) }
    }

    private fun onPasswordChanged(password: String) {
        _state.update { it.copy(password = password, passwordError = null) }
    }

    private fun onConfirmPasswordChanged(confirmPassword: String) {
        _state.update { it.copy(confirmPassword = confirmPassword, confirmPasswordError = null) }
    }

    private fun onPasswordVisibilityToggled() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    private fun onConfirmPasswordVisibilityToggled() {
        _state.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    private fun onRegisterPressed() {
        screenModelScope.launch {
            val currentState = _state.value
            var hasError = false

            // 名前のバリデーション
            if (currentState.name.isBlank()) {
                _state.update { it.copy(nameError = "名前を入力してください") }
                hasError = true
            }

            // メールアドレスのバリデーション
            if (currentState.email.isBlank()) {
                _state.update { it.copy(emailError = "メールアドレスを入力してください") }
                hasError = true
            } else if (!currentState.email.contains("@")) {
                _state.update { it.copy(emailError = "有効なメールアドレスを入力してください") }
                hasError = true
            }

            // パスワードのバリデーション
            if (currentState.password.isBlank()) {
                _state.update { it.copy(passwordError = "パスワードを入力してください") }
                hasError = true
            } else if (currentState.password.length < 6) {
                _state.update { it.copy(passwordError = "パスワードは6文字以上で入力してください") }
                hasError = true
            }

            // パスワード確認のバリデーション
            if (currentState.confirmPassword.isBlank()) {
                _state.update { it.copy(confirmPasswordError = "パスワード（確認）を入力してください") }
                hasError = true
            } else if (currentState.password != currentState.confirmPassword) {
                _state.update { it.copy(confirmPasswordError = "パスワードが一致しません") }
                hasError = true
            }

            if (hasError) return@launch

            // 登録処理を実行（モック）
            _state.update { it.copy(isLoading = true) }
            delay(1500) // API呼び出しをシミュレート

            // モック：登録成功
            _state.update { it.copy(isLoading = false) }
            _effect.send(RegisterContract.Effect.ShowSuccess("アカウントを作成しました"))
            delay(500)
            _effect.send(RegisterContract.Effect.NavigateToHome)
        }
    }

    private fun onGoogleRegisterPressed() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(1000) // Google認証をシミュレート

            // モック：登録成功
            _state.update { it.copy(isLoading = false) }
            _effect.send(RegisterContract.Effect.ShowSuccess("Googleアカウントで登録しました"))
            delay(500)
            _effect.send(RegisterContract.Effect.NavigateToHome)
        }
    }

    private fun onAppleRegisterPressed() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(1000) // Apple認証をシミュレート

            // モック：登録成功
            _state.update { it.copy(isLoading = false) }
            _effect.send(RegisterContract.Effect.ShowSuccess("Appleアカウントで登録しました"))
            delay(500)
            _effect.send(RegisterContract.Effect.NavigateToHome)
        }
    }

    private fun onLoginPressed() {
        screenModelScope.launch {
            _effect.send(RegisterContract.Effect.NavigateToLogin)
        }
    }
}
