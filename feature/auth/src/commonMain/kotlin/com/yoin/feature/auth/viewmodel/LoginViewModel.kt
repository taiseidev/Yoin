package com.yoin.feature.auth.viewmodel

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
 * ログイン画面のScreenModel
 *
 * 注意: 現在はUI実装のみで、実際の認証ロジックは未実装です。
 * 各ボタンをタップするとホーム画面に遷移します（仮実装）。
 */
class LoginViewModel : ScreenModel {

    private val _state = MutableStateFlow(LoginContract.State())
    val state: StateFlow<LoginContract.State> = _state.asStateFlow()

    private val _effect = Channel<LoginContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /**
     * ユーザーの意図を処理
     */
    fun handleIntent(intent: LoginContract.Intent) {
        when (intent) {
            is LoginContract.Intent.SignInWithApple -> onSignInWithApple()
            is LoginContract.Intent.SignInWithGoogle -> onSignInWithGoogle()
            is LoginContract.Intent.SignInAsGuest -> onSignInAsGuest()
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

            // TODO: ゲストログイン実装
            // 現在は仮実装として、そのままホーム画面に遷移
            _effect.send(LoginContract.Effect.NavigateToHome)

            _state.update { it.copy(isLoading = false) }
        }
    }
}
