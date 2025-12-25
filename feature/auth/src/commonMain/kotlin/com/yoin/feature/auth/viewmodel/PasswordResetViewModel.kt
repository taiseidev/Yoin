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
 * パスワードリセット画面のScreenModel
 *
 * 注意: 現在はUI実装のみで、実際のパスワードリセットロジックは未実装です。
 * リセットリンク送信ボタンをタップすると成功メッセージを表示します（仮実装）。
 */
class PasswordResetViewModel : ScreenModel {

    private val _state = MutableStateFlow(PasswordResetContract.State())
    val state: StateFlow<PasswordResetContract.State> = _state.asStateFlow()

    private val _effect = Channel<PasswordResetContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /**
     * ユーザーの意図を処理
     */
    fun handleIntent(intent: PasswordResetContract.Intent) {
        when (intent) {
            is PasswordResetContract.Intent.OnEmailChanged -> onEmailChanged(intent.email)
            is PasswordResetContract.Intent.OnSendResetLinkPressed -> onSendResetLinkPressed()
            is PasswordResetContract.Intent.OnBackToLoginPressed -> onBackToLoginPressed()
        }
    }

    private fun onEmailChanged(email: String) {
        _state.update { it.copy(email = email, emailError = null) }
    }

    private fun onSendResetLinkPressed() {
        screenModelScope.launch {
            val currentState = _state.value

            // バリデーション
            if (currentState.email.isBlank()) {
                _state.update { it.copy(emailError = "メールアドレスを入力してください") }
                return@launch
            }

            if (!currentState.email.contains("@")) {
                _state.update { it.copy(emailError = "有効なメールアドレスを入力してください") }
                return@launch
            }

            _state.update { it.copy(isLoading = true, error = null) }

            // TODO: パスワードリセットメール送信実装
            // 現在は仮実装として、成功メッセージを表示
            kotlinx.coroutines.delay(1000) // ネットワーク遅延をシミュレート

            _state.update { it.copy(isLoading = false, isEmailSent = true) }
            _effect.send(
                PasswordResetContract.Effect.ShowSuccess(
                    "パスワード再設定用のリンクを${currentState.email}に送信しました"
                )
            )
        }
    }

    private fun onBackToLoginPressed() {
        screenModelScope.launch {
            _effect.send(PasswordResetContract.Effect.NavigateToLogin)
        }
    }
}
