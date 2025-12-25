package com.yoin.feature.settings.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * お問い合わせフォーム画面のViewModel
 *
 * 機能:
 * - フォーム入力の管理
 * - バリデーション
 * - お問い合わせの送信
 */
class ContactFormViewModel : ScreenModel {
    private val _state = MutableStateFlow(ContactFormContract.State())
    val state: StateFlow<ContactFormContract.State> = _state.asStateFlow()

    private val _effect = Channel<ContactFormContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /**
     * Intentを処理
     */
    fun onIntent(intent: ContactFormContract.Intent) {
        when (intent) {
            is ContactFormContract.Intent.OnScreenDisplayed -> handleScreenDisplayed()
            is ContactFormContract.Intent.OnBackPressed -> handleBackPressed()
            is ContactFormContract.Intent.OnContactTypeChanged -> handleContactTypeChanged(intent.contactType)
            is ContactFormContract.Intent.OnSubjectChanged -> handleSubjectChanged(intent.subject)
            is ContactFormContract.Intent.OnContentChanged -> handleContentChanged(intent.content)
            is ContactFormContract.Intent.OnFileSelectPressed -> handleFileSelectPressed()
            is ContactFormContract.Intent.OnFileSelected -> handleFileSelected(intent.fileName)
            is ContactFormContract.Intent.OnSubmitPressed -> handleSubmitPressed()
        }
    }

    /**
     * 画面表示時の処理
     */
    private fun handleScreenDisplayed() {
        _state.value = _state.value.copy(isLoading = false)
    }

    /**
     * 戻るボタンの処理
     */
    private fun handleBackPressed() {
        screenModelScope.launch {
            _effect.send(ContactFormContract.Effect.NavigateBack)
        }
    }

    /**
     * お問い合わせ種類変更の処理
     */
    private fun handleContactTypeChanged(contactType: ContactFormContract.ContactType) {
        _state.value = _state.value.copy(contactType = contactType)
    }

    /**
     * 件名変更の処理
     */
    private fun handleSubjectChanged(subject: String) {
        _state.value = _state.value.copy(
            subject = subject,
            validationErrors = _state.value.validationErrors.copy(subjectError = null)
        )
    }

    /**
     * お問い合わせ内容変更の処理
     */
    private fun handleContentChanged(content: String) {
        // 最大1000文字に制限
        val limitedContent = if (content.length > 1000) {
            content.substring(0, 1000)
        } else {
            content
        }

        _state.value = _state.value.copy(
            content = limitedContent,
            validationErrors = _state.value.validationErrors.copy(contentError = null)
        )
    }

    /**
     * ファイル選択ボタンの処理
     */
    private fun handleFileSelectPressed() {
        screenModelScope.launch {
            _effect.send(ContactFormContract.Effect.ShowFilePicker)
        }
    }

    /**
     * ファイル選択時の処理
     */
    private fun handleFileSelected(fileName: String) {
        _state.value = _state.value.copy(attachedFileName = fileName)
    }

    /**
     * 送信ボタンの処理
     */
    private fun handleSubmitPressed() {
        screenModelScope.launch {
            val validationErrors = validateInputs()

            if (validationErrors.hasErrors()) {
                _state.value = _state.value.copy(validationErrors = validationErrors)
                _effect.send(ContactFormContract.Effect.ShowError("入力内容を確認してください"))
                return@launch
            }

            _state.value = _state.value.copy(isLoading = true)

            try {
                // TODO: 実際のAPIにお問い合わせを送信
                delay(1000) // モック送信

                _state.value = _state.value.copy(isLoading = false)
                _effect.send(ContactFormContract.Effect.ShowSuccess("お問い合わせを送信しました"))

                // 少し待ってから前の画面に戻る
                delay(500)
                _effect.send(ContactFormContract.Effect.NavigateBack)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false)
                _effect.send(ContactFormContract.Effect.ShowError("送信に失敗しました"))
            }
        }
    }

    /**
     * 入力内容のバリデーション
     */
    private fun validateInputs(): ContactFormContract.ValidationErrors {
        val state = _state.value

        return ContactFormContract.ValidationErrors(
            subjectError = if (state.subject.isBlank()) "件名を入力してください" else null,
            contentError = if (state.content.isBlank()) "お問い合わせ内容を入力してください" else null
        )
    }
}
