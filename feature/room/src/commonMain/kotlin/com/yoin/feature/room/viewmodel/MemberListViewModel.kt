package com.yoin.feature.room.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.yoin.feature.room.model.MemberRole
import com.yoin.feature.room.model.RoomMember
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * メンバー一覧画面のScreenModel
 *
 * @param roomId ルームID
 */
class MemberListViewModel(private val roomId: String) : ScreenModel {

    private val _state = MutableStateFlow(MemberListContract.State(roomId = roomId))
    val state: StateFlow<MemberListContract.State> = _state.asStateFlow()

    private val _effect = Channel<MemberListContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadMembers()
    }

    /**
     * ユーザーの意図を処理
     */
    fun handleIntent(intent: MemberListContract.Intent) {
        when (intent) {
            is MemberListContract.Intent.OnMemberClicked -> onMemberClicked(intent.member)
            is MemberListContract.Intent.OnRemoveMemberClicked -> onRemoveMemberClicked(intent.memberId)
            is MemberListContract.Intent.OnChangeRoleClicked -> onChangeRoleClicked(intent.memberId)
            is MemberListContract.Intent.OnInviteMemberPressed -> onInviteMemberPressed()
        }
    }

    private fun loadMembers() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // TODO: 実際のメンバー情報取得処理を実装
            // 現在はダミーデータを使用
            kotlinx.coroutines.delay(300)

            val dummyMembers = listOf(
                RoomMember(
                    id = "1",
                    name = "田中太郎",
                    avatar = "",
                    role = MemberRole.OWNER,
                    joinedDate = "2025/1/1"
                ),
                RoomMember(
                    id = "2",
                    name = "佐藤花子",
                    avatar = "",
                    role = MemberRole.ADMIN,
                    joinedDate = "2025/1/2"
                ),
                RoomMember(
                    id = "3",
                    name = "鈴木一郎",
                    avatar = "",
                    role = MemberRole.MEMBER,
                    joinedDate = "2025/1/3"
                ),
                RoomMember(
                    id = "4",
                    name = "高橋美咲",
                    avatar = "",
                    role = MemberRole.MEMBER,
                    joinedDate = "2025/1/4"
                ),
                RoomMember(
                    id = "5",
                    name = "渡辺健二",
                    avatar = "",
                    role = MemberRole.MEMBER,
                    joinedDate = "2025/1/5"
                ),
                RoomMember(
                    id = "6",
                    name = "伊藤さくら",
                    avatar = "",
                    role = MemberRole.MEMBER,
                    joinedDate = "2025/1/6"
                ),
            )

            _state.update {
                it.copy(
                    isLoading = false,
                    roomName = "北海道旅行2025",
                    members = dummyMembers,
                    isOwner = true // 仮でオーナーとする
                )
            }
        }
    }

    private fun onMemberClicked(member: RoomMember) {
        screenModelScope.launch {
            _effect.send(MemberListContract.Effect.NavigateToMemberProfile(member.id))
        }
    }

    private fun onRemoveMemberClicked(memberId: String) {
        screenModelScope.launch {
            val member = _state.value.members.find { it.id == memberId }
            if (member != null) {
                if (member.role == MemberRole.OWNER) {
                    _effect.send(MemberListContract.Effect.ShowError("オーナーは削除できません"))
                } else if (!_state.value.isOwner) {
                    _effect.send(MemberListContract.Effect.ShowError("メンバーの削除はオーナーのみ可能です"))
                } else {
                    _effect.send(
                        MemberListContract.Effect.ShowRemoveMemberConfirmation(
                            memberId,
                            member.name
                        )
                    )
                }
            }
        }
    }

    private fun onChangeRoleClicked(memberId: String) {
        screenModelScope.launch {
            val member = _state.value.members.find { it.id == memberId }
            if (member != null) {
                if (member.role == MemberRole.OWNER) {
                    _effect.send(MemberListContract.Effect.ShowError("オーナーのロールは変更できません"))
                } else if (!_state.value.isOwner) {
                    _effect.send(MemberListContract.Effect.ShowError("ロールの変更はオーナーのみ可能です"))
                } else {
                    _effect.send(
                        MemberListContract.Effect.ShowChangeRoleDialog(
                            memberId,
                            member.name
                        )
                    )
                }
            }
        }
    }

    private fun onInviteMemberPressed() {
        screenModelScope.launch {
            _effect.send(MemberListContract.Effect.NavigateToInvite)
        }
    }
}
