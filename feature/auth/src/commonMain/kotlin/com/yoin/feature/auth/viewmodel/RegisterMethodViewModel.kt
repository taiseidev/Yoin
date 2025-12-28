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
 * Register Method画面のViewModel
 */
class RegisterMethodViewModel : ScreenModel {
    private val _state = MutableStateFlow(RegisterMethodContract.State())
    val state: StateFlow<RegisterMethodContract.State> = _state.asStateFlow()

    private val _effect = Channel<RegisterMethodContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /**
     * Intent処理
     */
    fun handleIntent(intent: RegisterMethodContract.Intent) {
        when (intent) {
            is RegisterMethodContract.Intent.OnEmailRegisterPressed -> {
                screenModelScope.launch {
                    _effect.send(RegisterMethodContract.Effect.NavigateToEmailRegister)
                }
            }

            is RegisterMethodContract.Intent.OnGoogleRegisterPressed -> {
                screenModelScope.launch {
                    _state.value = _state.value.copy(isLoading = true)
                    // TODO: Google登録実装
                    _effect.send(RegisterMethodContract.Effect.ShowSuccess("Google登録成功"))
                    _effect.send(RegisterMethodContract.Effect.NavigateToHome)
                    _state.value = _state.value.copy(isLoading = false)
                }
            }

            is RegisterMethodContract.Intent.OnAppleRegisterPressed -> {
                screenModelScope.launch {
                    _state.value = _state.value.copy(isLoading = true)
                    // TODO: Apple登録実装
                    _effect.send(RegisterMethodContract.Effect.ShowSuccess("Apple登録成功"))
                    _effect.send(RegisterMethodContract.Effect.NavigateToHome)
                    _state.value = _state.value.copy(isLoading = false)
                }
            }
        }
    }
}
