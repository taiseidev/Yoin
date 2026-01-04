package com.yoin.feature.room.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.yoin.feature.room.model.MemberRole
import com.yoin.feature.room.model.RoomInfo
import com.yoin.feature.room.model.RoomMember
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ルーム詳細（現像前）画面のScreenModel
 *
 * @param roomId ルームID
 */
class RoomDetailBeforeViewModel(private val roomId: String) : ScreenModel {

    private val _state = MutableStateFlow(RoomDetailBeforeContract.State())
    val state: StateFlow<RoomDetailBeforeContract.State> = _state.asStateFlow()

    private val _effect = Channel<RoomDetailBeforeContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadRoomData()
    }

    /**
     * ユーザーの意図を処理
     */
    fun handleIntent(intent: RoomDetailBeforeContract.Intent) {
        when (intent) {
            is RoomDetailBeforeContract.Intent.OnScreenDisplayed -> loadRoomData()
            is RoomDetailBeforeContract.Intent.OnRefresh -> loadRoomData()
            is RoomDetailBeforeContract.Intent.OnInvitePressed -> onInvitePressed()
            is RoomDetailBeforeContract.Intent.OnMemberBarPressed -> onMemberBarPressed()
            is RoomDetailBeforeContract.Intent.OnCameraPressed -> onCameraPressed()
            is RoomDetailBeforeContract.Intent.OnBackPressed -> onBackPressed()
            is RoomDetailBeforeContract.Intent.OnSettingsPressed -> onSettingsPressed()
        }
    }

    private fun loadRoomData() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // TODO: 実際のデータ取得処理を実装
            // 現在はダミーデータを使用
            kotlinx.coroutines.delay(300)

            val dummyRoomInfo = RoomInfo(
                id = roomId,
                name = "北海道旅行2025",
                icon = "🏔",
                destination = "札幌",
                startDate = "7/1",
                endDate = "7/5",
                memberCount = 6,
                isOwner = true
            )

            val dummyMembers = listOf(
                RoomMember(
                    id = "1",
                    name = "田中太郎",
                    avatar = "",
                    role = MemberRole.OWNER,
                    joinedDate = "2025/6/15"
                ),
                RoomMember(
                    id = "2",
                    name = "佐藤花子",
                    avatar = "",
                    role = MemberRole.MEMBER,
                    joinedDate = "2025/6/16"
                ),
                RoomMember(
                    id = "3",
                    name = "鈴木一郎",
                    avatar = "",
                    role = MemberRole.MEMBER,
                    joinedDate = "2025/6/17"
                ),
                RoomMember(
                    id = "4",
                    name = "山田二郎",
                    avatar = "",
                    role = MemberRole.MEMBER,
                    joinedDate = "2025/6/18"
                ),
                RoomMember(
                    id = "5",
                    name = "高橋三郎",
                    avatar = "",
                    role = MemberRole.MEMBER,
                    joinedDate = "2025/6/19"
                ),
                RoomMember(
                    id = "6",
                    name = "伊藤四郎",
                    avatar = "",
                    role = MemberRole.MEMBER,
                    joinedDate = "2025/6/20"
                )
            )

            // ダミーの旅行状態を設定
            val tripStatus = RoomDetailBeforeContract.TripStatus.IN_PROGRESS
            val daysUntilDevelopment = 3
            val developmentDateTime = "2025/7/6 AM9:00"
            val todayPhotoCount = 12
            val dailyPhotoLimit = 24

            _state.update {
                it.copy(
                    isLoading = false,
                    roomInfo = dummyRoomInfo,
                    members = dummyMembers,
                    tripStatus = tripStatus,
                    daysUntilDevelopment = daysUntilDevelopment,
                    developmentDateTime = developmentDateTime,
                    todayPhotoCount = todayPhotoCount,
                    dailyPhotoLimit = dailyPhotoLimit,
                    error = null
                )
            }
        }
    }

    private fun onInvitePressed() {
        screenModelScope.launch {
            _effect.send(RoomDetailBeforeContract.Effect.ShowInviteDialog)
        }
    }

    private fun onMemberBarPressed() {
        screenModelScope.launch {
            _effect.send(RoomDetailBeforeContract.Effect.NavigateToMemberList(roomId))
        }
    }

    private fun onCameraPressed() {
        screenModelScope.launch {
            val currentState = _state.value

            when (currentState.tripStatus) {
                RoomDetailBeforeContract.TripStatus.BEFORE_TRIP -> {
                    _effect.send(RoomDetailBeforeContract.Effect.ShowError("旅行が始まったら撮影できます"))
                }
                RoomDetailBeforeContract.TripStatus.LIMIT_REACHED -> {
                    _effect.send(RoomDetailBeforeContract.Effect.ShowError("本日の撮影は終了です。また明日！"))
                }
                RoomDetailBeforeContract.TripStatus.TRIP_ENDED -> {
                    _effect.send(RoomDetailBeforeContract.Effect.ShowError("現像をお楽しみに！"))
                }
                RoomDetailBeforeContract.TripStatus.IN_PROGRESS -> {
                    _effect.send(RoomDetailBeforeContract.Effect.NavigateToCamera(roomId))
                }
            }
        }
    }

    private fun onBackPressed() {
        screenModelScope.launch {
            _effect.send(RoomDetailBeforeContract.Effect.NavigateBack)
        }
    }

    private fun onSettingsPressed() {
        screenModelScope.launch {
            _effect.send(RoomDetailBeforeContract.Effect.NavigateToSettings(roomId))
        }
    }
}
