package com.yoin.feature.auth.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.yoin.domain.auth.model.AuthResult
import com.yoin.domain.auth.usecase.CreateGuestUserUseCase
import com.yoin.domain.auth.usecase.LoginWithEmailUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ログイン画面のScreenModel
 */
class LoginViewModel(
    private val loginWithEmailUseCase: LoginWithEmailUseCase,
    private val createGuestUserUseCase: CreateGuestUserUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(LoginContract.State())
    val state: StateFlow<LoginContract.State> = _state.asStateFlow()

    private val _effect = Channel<LoginContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /**
     * ユーザーの意図を処理
     */
    fun handleIntent(intent: LoginContract.Intent) {
        when (intent) {
            is LoginContract.Intent.OnEmailChanged -> onEmailChanged(intent.email)
            is LoginContract.Intent.OnPasswordChanged -> onPasswordChanged(intent.password)
            is LoginContract.Intent.OnPasswordVisibilityToggled -> onPasswordVisibilityToggled()
            is LoginContract.Intent.OnLoginPressed -> onLoginPressed()
            is LoginContract.Intent.OnForgotPasswordPressed -> onForgotPasswordPressed()
            is LoginContract.Intent.OnRegisterPressed -> onRegisterPressed()
            is LoginContract.Intent.SignInWithApple -> onSignInWithApple()
            is LoginContract.Intent.SignInWithGoogle -> onSignInWithGoogle()
            is LoginContract.Intent.SignInAsGuest -> onSignInAsGuest()
        }
    }

    private fun onEmailChanged(email: String) {
        _state.update { it.copy(email = email, emailError = null) }
    }

    private fun onPasswordChanged(password: String) {
        _state.update { it.copy(password = password, passwordError = null) }
    }

    private fun onPasswordVisibilityToggled() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    private fun onLoginPressed() {
        screenModelScope.launch {
            val currentState = _state.value

            // バリデーション
            var hasError = false

            if (currentState.email.isBlank()) {
                _state.update { it.copy(emailError = "メールアドレスを入力してください") }
                hasError = true
            } else if (!currentState.email.contains("@")) {
                _state.update { it.copy(emailError = "有効なメールアドレスを入力してください") }
                hasError = true
            }

            if (currentState.password.isBlank()) {
                _state.update { it.copy(passwordError = "パスワードを入力してください") }
                hasError = true
            } else if (currentState.password.length < 6) {
                _state.update { it.copy(passwordError = "パスワードは6文字以上で入力してください") }
                hasError = true
            }

            if (hasError) return@launch

            _state.update { it.copy(isLoading = true, error = null) }

            // UseCaseでログイン処理を実行
            when (val result = loginWithEmailUseCase(currentState.email, currentState.password)) {
                is AuthResult.Success -> {
                    _effect.send(LoginContract.Effect.NavigateToHome)
                }
                is AuthResult.Error -> {
                    _state.update { it.copy(error = result.message) }
                }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun onForgotPasswordPressed() {
        screenModelScope.launch {
            _effect.send(LoginContract.Effect.NavigateToForgotPassword)
        }
    }

    private fun onRegisterPressed() {
        screenModelScope.launch {
            _effect.send(LoginContract.Effect.NavigateToRegister)
        }
    }

    private fun onSignInWithApple() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // TODO: Apple Sign-In実装
            // 現在は仮実装として、そのままホーム画面に遷移
            _effect.send(LoginContract.Effect.NavigateToHome)

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun onSignInWithGoogle() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // TODO: Google Sign-In実装
            // 現在は仮実装として、そのままホーム画面に遷移
            _effect.send(LoginContract.Effect.NavigateToHome)

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun onSignInAsGuest() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // UseCaseでゲストユーザーを作成
            when (val result = createGuestUserUseCase("ゲスト")) {
                is AuthResult.Success -> {
                    _effect.send(LoginContract.Effect.NavigateToHome)
                }
                is AuthResult.Error -> {
                    _state.update { it.copy(error = result.message) }
                }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }
}
