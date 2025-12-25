package com.yoin.feature.profile.viewmodel

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
 * プロフィール編集画面のScreenModel
 *
 * 責務:
 * - プロフィール情報の取得と管理
 * - ニックネームと自己紹介の入力バリデーション
 * - プロフィール更新処理
 */
class ProfileEditViewModel : ScreenModel {

    private val _state = MutableStateFlow(ProfileEditContract.State())
    val state: StateFlow<ProfileEditContract.State> = _state.asStateFlow()

    private val _effect = Channel<ProfileEditContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    // 元の値を保持（変更検知用）
    private var originalNickname: String = ""
    private var originalBio: String = ""

    /**
     * ユーザーの意図を処理
     */
    fun onIntent(intent: ProfileEditContract.Intent) {
        when (intent) {
            is ProfileEditContract.Intent.OnScreenDisplayed -> onScreenDisplayed(intent.userId)
            is ProfileEditContract.Intent.OnProfileImageTapped -> onProfileImageTapped()
            is ProfileEditContract.Intent.OnNicknameChanged -> onNicknameChanged(intent.nickname)
            is ProfileEditContract.Intent.OnNicknameClearPressed -> onNicknameClearPressed()
            is ProfileEditContract.Intent.OnBioChanged -> onBioChanged(intent.bio)
            is ProfileEditContract.Intent.OnSavePressed -> onSavePressed()
            is ProfileEditContract.Intent.OnCancelPressed -> onCancelPressed()
            is ProfileEditContract.Intent.OnChangePasswordPressed -> onChangePasswordPressed()
            is ProfileEditContract.Intent.OnDeleteAccountPressed -> onDeleteAccountPressed()
        }
    }

    private fun onScreenDisplayed(userId: String) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // TODO: 実際のユーザー情報取得APIを実装
                delay(500)

                // サンプルデータ
                val nickname = "山田花子"
                val email = "yamada@example.com"
                val bio = "旅行が大好きです！\n写真を撮るのが趣味です📷"

                originalNickname = nickname
                originalBio = bio

                _state.update {
                    it.copy(
                        isLoading = false,
                        nickname = nickname,
                        email = email,
                        bio = bio,
                        bioCharCount = bio.length
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _effect.send(ProfileEditContract.Effect.ShowError("プロフィール情報の取得に失敗しました"))
            }
        }
    }

    private fun onProfileImageTapped() {
        screenModelScope.launch {
            _effect.send(ProfileEditContract.Effect.ShowProfileImagePicker)
        }
    }

    private fun onNicknameChanged(nickname: String) {
        _state.update {
            it.copy(
                nickname = nickname,
                nicknameError = null,
                hasUnsavedChanges = checkHasUnsavedChanges(nickname, it.bio)
            )
        }
    }

    private fun onNicknameClearPressed() {
        _state.update {
            it.copy(
                nickname = "",
                nicknameError = null,
                hasUnsavedChanges = checkHasUnsavedChanges("", it.bio)
            )
        }
    }

    private fun onBioChanged(bio: String) {
        val currentState = _state.value

        // 最大文字数チェック
        if (bio.length <= currentState.bioMaxLength) {
            _state.update {
                it.copy(
                    bio = bio,
                    bioCharCount = bio.length,
                    hasUnsavedChanges = checkHasUnsavedChanges(it.nickname, bio)
                )
            }
        }
    }

    private fun onSavePressed() {
        screenModelScope.launch {
            val currentState = _state.value

            // バリデーション
            if (currentState.nickname.isBlank()) {
                _state.update {
                    it.copy(nicknameError = "ニックネームを入力してください")
                }
                return@launch
            }

            // 保存処理
            _state.update { it.copy(isSaving = true) }

            try {
                // TODO: 実際のプロフィール更新APIを実装
                delay(1000)

                // 元の値を更新
                originalNickname = currentState.nickname
                originalBio = currentState.bio

                _state.update {
                    it.copy(
                        isSaving = false,
                        hasUnsavedChanges = false
                    )
                }
                _effect.send(ProfileEditContract.Effect.ShowSuccess("プロフィールを更新しました"))
                delay(500)
                _effect.send(ProfileEditContract.Effect.NavigateBack)
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false) }
                _effect.send(ProfileEditContract.Effect.ShowError("プロフィールの更新に失敗しました"))
            }
        }
    }

    private fun onCancelPressed() {
        screenModelScope.launch {
            val currentState = _state.value

            if (currentState.hasUnsavedChanges) {
                _effect.send(ProfileEditContract.Effect.ShowUnsavedChangesDialog)
            } else {
                _effect.send(ProfileEditContract.Effect.NavigateBack)
            }
        }
    }

    private fun onChangePasswordPressed() {
        screenModelScope.launch {
            _effect.send(ProfileEditContract.Effect.NavigateToChangePassword)
        }
    }

    private fun onDeleteAccountPressed() {
        screenModelScope.launch {
            _effect.send(ProfileEditContract.Effect.NavigateToDeleteAccount)
        }
    }

    private fun checkHasUnsavedChanges(nickname: String, bio: String): Boolean {
        return nickname != originalNickname || bio != originalBio
    }
}
