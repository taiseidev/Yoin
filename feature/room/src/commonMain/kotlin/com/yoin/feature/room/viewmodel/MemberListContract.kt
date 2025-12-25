package com.yoin.feature.room.viewmodel

import com.yoin.feature.room.model.RoomMember

/**
 * メンバー一覧画面のMVIコントラクト
 */
interface MemberListContract {

    /**
     * 画面の状態
     */
    data class State(
        val isLoading: Boolean = false,
        val roomId: String = "",
        val roomName: String = "",
        val members: List<RoomMember> = emptyList(),
        val isOwner: Boolean = false,
        val error: String? = null,
    )

    /**
     * ユーザーの意図（アクション）
     */
    sealed interface Intent {
        data class OnMemberClicked(val member: RoomMember) : Intent
        data class OnRemoveMemberClicked(val memberId: String) : Intent
        data class OnChangeRoleClicked(val memberId: String) : Intent
        data object OnInviteMemberPressed : Intent
    }

    /**
     * 副作用（ナビゲーションなど）
     */
    sealed interface Effect {
        data object NavigateBack : Effect
        data class NavigateToMemberProfile(val memberId: String) : Effect
        data class ShowRemoveMemberConfirmation(val memberId: String, val memberName: String) : Effect
        data class ShowChangeRoleDialog(val memberId: String, val memberName: String) : Effect
        data object NavigateToInvite : Effect
        data class ShowError(val message: String) : Effect
        data class ShowSuccess(val message: String) : Effect
    }
}
