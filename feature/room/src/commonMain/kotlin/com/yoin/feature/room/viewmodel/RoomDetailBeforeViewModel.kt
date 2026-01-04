package com.yoin.feature.room.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ルーム詳細（現像前）画面のViewModel
 *
 * 旅行中のメイン画面を管理する
 */
class RoomDetailBeforeViewModel(
    private val roomId: String
) : ScreenModel {

    private val _state = MutableStateFlow(RoomDetailBeforeContract.State())
    val state: StateFlow<RoomDetailBeforeContract.State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<RoomDetailBeforeContract.Effect>()
    val effect: SharedFlow<RoomDetailBeforeContract.Effect> = _effect.asSharedFlow()

    fun onIntent(intent: RoomDetailBeforeContract.Intent) {
        when (intent) {
            is RoomDetailBeforeContract.Intent.OnScreenDisplayed -> loadRoomDetail(intent.roomId)
            is RoomDetailBeforeContract.Intent.OnBackPressed -> navigateBack()
            is RoomDetailBeforeContract.Intent.OnInvitePressed -> navigateToInvite()
            is RoomDetailBeforeContract.Intent.OnMembersPressed -> navigateToMembers()
            is RoomDetailBeforeContract.Intent.OnCameraPressed -> navigateToCamera()
            is RoomDetailBeforeContract.Intent.OnSettingsPressed -> navigateToSettings()
            is RoomDetailBeforeContract.Intent.OnMapPressed -> navigateToMap()
            is RoomDetailBeforeContract.Intent.OnRefresh -> refreshRoomDetail()
        }
    }

    private fun loadRoomDetail(roomId: String) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // TODO: 実際のリポジトリから取得する
                delay(500) // Simulate network delay

                val dummyDetail = createDummyRoomDetail(roomId)
                _state.update {
                    it.copy(
                        isLoading = false,
                        roomDetail = dummyDetail
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _effect.emit(
                    RoomDetailBeforeContract.Effect.ShowError(
                        e.message ?: "ルーム情報の取得に失敗しました"
                    )
                )
            }
        }
    }

    private fun refreshRoomDetail() {
        _state.value.roomDetail?.let { detail ->
            loadRoomDetail(detail.id)
        }
    }

    private fun navigateBack() {
        screenModelScope.launch {
            _effect.emit(RoomDetailBeforeContract.Effect.NavigateBack)
        }
    }

    private fun navigateToInvite() {
        screenModelScope.launch {
            _effect.emit(RoomDetailBeforeContract.Effect.NavigateToInvite(roomId))
        }
    }

    private fun navigateToMembers() {
        screenModelScope.launch {
            _effect.emit(RoomDetailBeforeContract.Effect.NavigateToMembers(roomId))
        }
    }

    private fun navigateToCamera() {
        val roomDetail = _state.value.roomDetail
        if (roomDetail?.canTakePhoto == true) {
            screenModelScope.launch {
                _effect.emit(RoomDetailBeforeContract.Effect.NavigateToCamera(roomId))
            }
        } else {
            screenModelScope.launch {
                val message = when (roomDetail?.roomStatus) {
                    RoomDetailBeforeContract.RoomStatus.UPCOMING -> "旅行が始まったら撮影できます"
                    RoomDetailBeforeContract.RoomStatus.PENDING_DEVELOPMENT -> "現像をお楽しみに！"
                    RoomDetailBeforeContract.RoomStatus.DEVELOPED -> "このルームは現像済みです"
                    RoomDetailBeforeContract.RoomStatus.ACTIVE -> "本日の撮影枚数に達しました"
                    null -> "撮影できません"
                }
                _effect.emit(RoomDetailBeforeContract.Effect.ShowError(message))
            }
        }
    }

    private fun navigateToSettings() {
        screenModelScope.launch {
            _effect.emit(RoomDetailBeforeContract.Effect.NavigateToSettings(roomId))
        }
    }

    private fun navigateToMap() {
        screenModelScope.launch {
            _effect.emit(RoomDetailBeforeContract.Effect.NavigateToMap(roomId))
        }
    }

    /**
     * ダミーデータを生成（開発・デバッグ用）
     */
    private fun createDummyRoomDetail(roomId: String): RoomDetailBeforeContract.RoomDetail {
        return RoomDetailBeforeContract.RoomDetail(
            id = roomId,
            emoji = "✈️",
            name = "北海道旅行2025",
            dateRange = "7/1〜7/5",
            location = "札幌",
            daysUntilDevelopment = 3,
            developmentDateTime = "2025/7/6 AM9:00",
            todayPhotos = 12,
            maxPhotos = 24,
            members = listOf(
                RoomDetailBeforeContract.Member(
                    id = "1",
                    name = "田中太郎",
                    isCurrentUser = true
                ),
                RoomDetailBeforeContract.Member(
                    id = "2",
                    name = "山田花子"
                ),
                RoomDetailBeforeContract.Member(
                    id = "3",
                    name = "佐藤次郎"
                ),
                RoomDetailBeforeContract.Member(
                    id = "4",
                    name = "鈴木さくら"
                ),
                RoomDetailBeforeContract.Member(
                    id = "5",
                    name = "高橋健太"
                ),
                RoomDetailBeforeContract.Member(
                    id = "6",
                    name = "伊藤美咲"
                ),
                RoomDetailBeforeContract.Member(
                    id = "7",
                    name = "渡辺翔"
                ),
                RoomDetailBeforeContract.Member(
                    id = "8",
                    name = "中村愛"
                )
            ),
            roomStatus = RoomDetailBeforeContract.RoomStatus.ACTIVE
        )
    }
}
