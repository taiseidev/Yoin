package com.yoin.feature.room.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.yoin.feature.room.model.RoomInfo
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ルーム設定画面のScreenModel
 *
 * @param roomId ルームID
 */
class RoomSettingsViewModel(private val roomId: String) : ScreenModel {

    private val _state = MutableStateFlow(RoomSettingsContract.State())
    val state: StateFlow<RoomSettingsContract.State> = _state.asStateFlow()

    private val _effect = Channel<RoomSettingsContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadRoomInfo()
    }

    /**
     * ユーザーの意図を処理
     */
    fun handleIntent(intent: RoomSettingsContract.Intent) {
        when (intent) {
            is RoomSettingsContract.Intent.OnCancelPressed -> onCancelPressed()
            is RoomSettingsContract.Intent.OnSavePressed -> onSavePressed()
            is RoomSettingsContract.Intent.OnIconEditPressed -> onIconEditPressed()
            is RoomSettingsContract.Intent.OnRoomNameChanged -> onRoomNameChanged(intent.name)
            is RoomSettingsContract.Intent.OnDestinationChanged -> onDestinationChanged(intent.destination)
            is RoomSettingsContract.Intent.OnMemberListPressed -> onMemberListPressed()
            is RoomSettingsContract.Intent.OnRegenerateInviteLinkPressed -> onRegenerateInviteLinkPressed()
            is RoomSettingsContract.Intent.OnLeaveRoomPressed -> onLeaveRoomPressed()
            is RoomSettingsContract.Intent.OnDeleteRoomPressed -> onDeleteRoomPressed()
        }
    }

    private fun loadRoomInfo() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // TODO: 実際のルーム情報取得処理を実装
            // 現在はダミーデータを使用
            kotlinx.coroutines.delay(300)

            val dummyRoomInfo = RoomInfo(
                id = roomId,
                name = "北海道旅行2025",
                icon = "🏔",
                destination = "北海道",
                startDate = "2025/7/1",
                endDate = "7/5",
                memberCount = 6,
                isOwner = true // 仮でオーナーとする
            )

            _state.update {
                it.copy(
                    isLoading = false,
                    roomInfo = dummyRoomInfo,
                    roomName = dummyRoomInfo.name,
                    destination = dummyRoomInfo.destination
                )
            }
        }
    }

    private fun onCancelPressed() {
        screenModelScope.launch {
            if (_state.value.hasUnsavedChanges) {
                // TODO: 未保存の変更がある場合は確認ダイアログを表示
            }
            _effect.send(RoomSettingsContract.Effect.NavigateBack)
        }
    }

    private fun onSavePressed() {
        screenModelScope.launch {
            val currentState = _state.value

            if (currentState.roomName.isBlank()) {
                _effect.send(RoomSettingsContract.Effect.ShowError("ルーム名を入力してください"))
                return@launch
            }

            if (currentState.destination.isBlank()) {
                _effect.send(RoomSettingsContract.Effect.ShowError("目的地を入力してください"))
                return@launch
            }

            _state.update { it.copy(isLoading = true) }

            // TODO: 実際の保存処理を実装
            kotlinx.coroutines.delay(500)

            _state.update { it.copy(isLoading = false, hasUnsavedChanges = false) }
            _effect.send(RoomSettingsContract.Effect.ShowSuccess("設定を保存しました"))
            _effect.send(RoomSettingsContract.Effect.NavigateBack)
        }
    }

    private fun onIconEditPressed() {
        screenModelScope.launch {
            // TODO: アイコン編集画面への遷移を実装
            _effect.send(RoomSettingsContract.Effect.ShowError("アイコン編集機能は未実装です"))
        }
    }

    private fun onRoomNameChanged(name: String) {
        _state.update {
            it.copy(
                roomName = name,
                hasUnsavedChanges = name != it.roomInfo?.name
            )
        }
    }

    private fun onDestinationChanged(destination: String) {
        _state.update {
            it.copy(
                destination = destination,
                hasUnsavedChanges = destination != it.roomInfo?.destination
            )
        }
    }

    private fun onMemberListPressed() {
        screenModelScope.launch {
            _effect.send(RoomSettingsContract.Effect.NavigateToMemberList)
        }
    }

    private fun onRegenerateInviteLinkPressed() {
        screenModelScope.launch {
            _effect.send(RoomSettingsContract.Effect.NavigateToInviteLinkRegenerate)
        }
    }

    private fun onLeaveRoomPressed() {
        screenModelScope.launch {
            _effect.send(RoomSettingsContract.Effect.ShowLeaveRoomConfirmation(roomId))
        }
    }

    private fun onDeleteRoomPressed() {
        screenModelScope.launch {
            if (_state.value.roomInfo?.isOwner == true) {
                _effect.send(RoomSettingsContract.Effect.ShowDeleteRoomConfirmation(roomId))
            } else {
                _effect.send(RoomSettingsContract.Effect.ShowError("ルームの削除はオーナーのみ可能です"))
            }
        }
    }
}
