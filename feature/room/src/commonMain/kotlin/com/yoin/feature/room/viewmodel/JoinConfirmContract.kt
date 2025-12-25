package com.yoin.feature.room.viewmodel

/**
 * ルーム参加確認画面のMVI Contract
 *
 * 機能:
 * - 招待されたルーム情報の表示
 * - ニックネーム入力
 * - 参加方法の選択（ログイン/新規登録/ゲスト）
 */
object JoinConfirmContract {
    data class State(
        val isLoading: Boolean = false,
        val roomInfo: RoomInfo? = null,
        val nickname: String = "",
        val nicknameError: String? = null,
        val errorMessage: String? = null
    )

    /**
     * 招待されたルーム情報
     */
    data class RoomInfo(
        val id: String,
        val emoji: String,
        val title: String,
        val dateRange: String,
        val destination: String,
        val memberCount: Int,
        val memberAvatars: List<String> = emptyList(),
        val developmentDateTime: String
    )

    sealed interface Intent {
        data class OnScreenDisplayed(val roomId: String) : Intent
        data class OnNicknameChanged(val nickname: String) : Intent
        data object OnLoginAndJoinPressed : Intent
        data object OnRegisterAndJoinPressed : Intent
        data object OnGuestJoinPressed : Intent
        data object OnClosePressed : Intent
    }

    sealed interface Effect {
        data class ShowError(val message: String) : Effect
        data object NavigateToLogin : Effect
        data object NavigateToRegister : Effect
        data class NavigateToRoomDetail(val roomId: String) : Effect
        data object NavigateBack : Effect
    }
}
