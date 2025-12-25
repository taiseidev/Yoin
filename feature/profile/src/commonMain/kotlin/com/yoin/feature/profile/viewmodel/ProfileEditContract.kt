package com.yoin.feature.profile.viewmodel

/**
 * プロフィール編集画面のMVI Contract
 *
 * 機能:
 * - プロフィール情報の編集
 * - プロフィール画像の変更
 * - ニックネームと自己紹介の更新
 * - パスワード変更・アカウント削除への遷移
 */
object ProfileEditContract {

    data class State(
        val isLoading: Boolean = false,
        val isSaving: Boolean = false,
        val profileImageEmoji: String = "山", // プロフィール画像（絵文字）
        val nickname: String = "",
        val nicknameError: String? = null,
        val email: String = "", // 読み取り専用
        val bio: String = "",
        val bioCharCount: Int = 0,
        val bioMaxLength: Int = 100,
        val hasUnsavedChanges: Boolean = false
    )

    sealed interface Intent {
        data class OnScreenDisplayed(val userId: String) : Intent
        data object OnProfileImageTapped : Intent
        data class OnNicknameChanged(val nickname: String) : Intent
        data object OnNicknameClearPressed : Intent
        data class OnBioChanged(val bio: String) : Intent
        data object OnSavePressed : Intent
        data object OnCancelPressed : Intent
        data object OnChangePasswordPressed : Intent
        data object OnDeleteAccountPressed : Intent
    }

    sealed interface Effect {
        data class ShowError(val message: String) : Effect
        data class ShowSuccess(val message: String) : Effect
        data object NavigateBack : Effect
        data object NavigateToChangePassword : Effect
        data object NavigateToDeleteAccount : Effect
        data object ShowProfileImagePicker : Effect
        data object ShowUnsavedChangesDialog : Effect
    }
}
