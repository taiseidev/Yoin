package com.yoin.feature.room.viewmodel

import com.yoin.feature.room.model.RoomInfo

/**
 * ルーム設定画面のMVIコントラクト
 */
interface RoomSettingsContract {

    /**
     * 画面の状態
     */
    data class State(
        val isLoading: Boolean = false,
        val roomInfo: RoomInfo? = null,
        val roomName: String = "",
        val destination: String = "",
        val hasUnsavedChanges: Boolean = false,
        val error: String? = null,
    )

    /**
     * ユーザーの意図（アクション）
     */
    sealed interface Intent {
        data object OnCancelPressed : Intent
        data object OnSavePressed : Intent
        data object OnIconEditPressed : Intent
        data class OnRoomNameChanged(val name: String) : Intent
        data class OnDestinationChanged(val destination: String) : Intent
        data object OnMemberListPressed : Intent
        data object OnRegenerateInviteLinkPressed : Intent
        data object OnLeaveRoomPressed : Intent
        data object OnDeleteRoomPressed : Intent
    }

    /**
     * 副作用（ナビゲーションなど）
     */
    sealed interface Effect {
        data object NavigateBack : Effect
        data object NavigateToMemberList : Effect
        data object NavigateToInviteLinkRegenerate : Effect
        data class ShowLeaveRoomConfirmation(val roomId: String) : Effect
        data class ShowDeleteRoomConfirmation(val roomId: String) : Effect
        data class ShowError(val message: String) : Effect
        data class ShowSuccess(val message: String) : Effect
    }
}
