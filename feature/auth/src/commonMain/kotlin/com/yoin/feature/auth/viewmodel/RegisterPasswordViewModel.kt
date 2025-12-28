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
 * RegisterPassword画面のViewModel
 */
class RegisterPasswordViewModel(
    private val name: String,
    private val email: String
) : ScreenModel {
    private val _state = MutableStateFlow(
        RegisterPasswordContract.State(
            name = name,
            email = email
        )
    )
    val state: StateFlow<RegisterPasswordContract.State> = _state.asStateFlow()

    private val _effect = Channel<RegisterPasswordContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /**
     * Intent処理
     */
    fun handleIntent(intent: RegisterPasswordContract.Intent) {
        when (intent) {
            is RegisterPasswordContract.Intent.OnPasswordChanged -> {
                _state.value = _state.value.copy(
                    password = intent.password,
                    passwordError = null
                )
            }

            is RegisterPasswordContract.Intent.OnConfirmPasswordChanged -> {
                _state.value = _state.value.copy(
                    confirmPassword = intent.confirmPassword,
                    confirmPasswordError = null
                )
            }

            is RegisterPasswordContract.Intent.OnPasswordVisibilityToggled -> {
                _state.value = _state.value.copy(
                    isPasswordVisible = !_state.value.isPasswordVisible
                )
            }

            is RegisterPasswordContract.Intent.OnConfirmPasswordVisibilityToggled -> {
                _state.value = _state.value.copy(
                    isConfirmPasswordVisible = !_state.value.isConfirmPasswordVisible
                )
            }

            is RegisterPasswordContract.Intent.OnRegisterPressed -> {
                if (validateInputs()) {
                    performRegister()
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (_state.value.password.length < 8) {
            _state.value = _state.value.copy(
                passwordError = "パスワードは8文字以上で入力してください"
            )
            isValid = false
        }

        if (_state.value.password != _state.value.confirmPassword) {
            _state.value = _state.value.copy(
                confirmPasswordError = "パスワードが一致しません"
            )
            isValid = false
        }

        return isValid
    }

    private fun performRegister() {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            // TODO: 実際の登録処理
            kotlinx.coroutines.delay(1000)

            _effect.send(RegisterPasswordContract.Effect.ShowSuccess("登録が完了しました"))
            _effect.send(RegisterPasswordContract.Effect.NavigateToHome)

            _state.value = _state.value.copy(isLoading = false)
        }
    }
}
