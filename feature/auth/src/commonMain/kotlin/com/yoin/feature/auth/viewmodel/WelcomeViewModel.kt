package com.yoin.feature.auth.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Welcome画面のViewModel
 */
class WelcomeViewModel : ScreenModel {
    private val _state = MutableStateFlow(WelcomeContract.State())
    val state: StateFlow<WelcomeContract.State> = _state.asStateFlow()

    private val _effect = Channel<WelcomeContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /**
     * Intent処理
     */
    fun handleIntent(intent: WelcomeContract.Intent) {
        when (intent) {
            is WelcomeContract.Intent.OnEmailLoginPressed -> {
                screenModelScope.launch {
                    _effect.send(WelcomeContract.Effect.NavigateToEmailLogin)
                }
            }

            is WelcomeContract.Intent.OnRegisterPressed -> {
                screenModelScope.launch {
                    _effect.send(WelcomeContract.Effect.NavigateToRegister)
                }
            }

            is WelcomeContract.Intent.OnGoogleSignInPressed -> {
                screenModelScope.launch {
                    _state.value = _state.value.copy(isLoading = true)
                    // TODO: Google認証実装
                    _effect.send(WelcomeContract.Effect.ShowSuccess("Googleログイン成功"))
                    _effect.send(WelcomeContract.Effect.NavigateToHome)
                    _state.value = _state.value.copy(isLoading = false)
                }
            }

            is WelcomeContract.Intent.OnAppleSignInPressed -> {
                screenModelScope.launch {
                    _state.value = _state.value.copy(isLoading = true)
                    // TODO: Apple認証実装
                    _effect.send(WelcomeContract.Effect.ShowSuccess("Appleログイン成功"))
                    _effect.send(WelcomeContract.Effect.NavigateToHome)
                    _state.value = _state.value.copy(isLoading = false)
                }
            }

            is WelcomeContract.Intent.OnGuestPressed -> {
                screenModelScope.launch {
                    _state.value = _state.value.copy(isLoading = true)
                    // TODO: ゲストログイン実装
                    _effect.send(WelcomeContract.Effect.ShowSuccess("ゲストモードで開始"))
                    _effect.send(WelcomeContract.Effect.NavigateToHome)
                    _state.value = _state.value.copy(isLoading = false)
                }
            }
        }
    }
}
