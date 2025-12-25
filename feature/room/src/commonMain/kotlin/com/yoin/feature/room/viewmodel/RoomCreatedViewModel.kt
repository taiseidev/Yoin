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
import kotlinx.coroutines.launch

/**
 * ルーム作成完了画面のViewModel
 *
 * MVIパターンに基づいた状態管理:
 * - State: ルーム情報と招待リンク
 * - Intent: ユーザーアクション
 * - Effect: 一時的なイベント（コピー、共有、ナビゲーション）
 */
class RoomCreatedViewModel : ScreenModel {
    private val _state = MutableStateFlow(RoomCreatedContract.State())
    val state: StateFlow<RoomCreatedContract.State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<RoomCreatedContract.Effect>()
    val effect: SharedFlow<RoomCreatedContract.Effect> = _effect.asSharedFlow()

    fun onIntent(intent: RoomCreatedContract.Intent) {
        when (intent) {
            is RoomCreatedContract.Intent.OnScreenDisplayed -> handleScreenDisplayed(intent.roomId)
            is RoomCreatedContract.Intent.OnCopyLinkPressed -> handleCopyLinkPressed()
            is RoomCreatedContract.Intent.OnShareQRPressed -> handleShareQRPressed()
            is RoomCreatedContract.Intent.OnGoToRoomPressed -> handleGoToRoomPressed()
            is RoomCreatedContract.Intent.OnBackToHomePressed -> handleBackToHomePressed()
        }
    }

    private fun handleScreenDisplayed(roomId: String) {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                // TODO: 実際のルーム情報取得ロジックを実装
                delay(500)

                // サンプルデータ
                val roomInfo = RoomCreatedContract.RoomInfo(
                    id = roomId,
                    emoji = "🏔️",
                    title = "北海道旅行",
                    dateRange = "2024/12/25 〜 2024/12/28",
                    destination = "札幌・小樽"
                )

                // 招待リンクを生成（仮実装）
                val inviteLink = "https://yoin.app/invite/$roomId"

                // QRコードデータ（仮実装）
                val qrCodeData = inviteLink

                _state.value = _state.value.copy(
                    isLoading = false,
                    roomInfo = roomInfo,
                    inviteLink = inviteLink,
                    qrCodeData = qrCodeData
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
                _effect.emit(RoomCreatedContract.Effect.ShowError(e.message ?: "エラーが発生しました"))
            }
        }
    }

    private fun handleCopyLinkPressed() {
        screenModelScope.launch {
            try {
                // TODO: 実際のクリップボードコピー処理を実装
                val link = _state.value.inviteLink
                // プラットフォーム固有のクリップボード処理はUI層で実装
                _effect.emit(RoomCreatedContract.Effect.ShowSuccess("招待リンクをコピーしました"))
            } catch (e: Exception) {
                _effect.emit(RoomCreatedContract.Effect.ShowError("コピーに失敗しました"))
            }
        }
    }

    private fun handleShareQRPressed() {
        screenModelScope.launch {
            try {
                val link = _state.value.inviteLink
                _effect.emit(RoomCreatedContract.Effect.ShareInviteLink(link))
            } catch (e: Exception) {
                _effect.emit(RoomCreatedContract.Effect.ShowError("共有に失敗しました"))
            }
        }
    }

    private fun handleGoToRoomPressed() {
        screenModelScope.launch {
            val roomId = _state.value.roomInfo?.id
            if (roomId != null) {
                _effect.emit(RoomCreatedContract.Effect.NavigateToRoomDetail(roomId))
            }
        }
    }

    private fun handleBackToHomePressed() {
        screenModelScope.launch {
            _effect.emit(RoomCreatedContract.Effect.NavigateToHome)
        }
    }
}
