package com.yoin.feature.room.viewmodel

/**
 * ルーム作成完了画面のMVI Contract
 *
 * 機能:
 * - ルーム作成成功メッセージの表示
 * - 招待リンク/QRコードの表示
 * - リンクコピー機能
 * - ルーム詳細画面への遷移
 */
object RoomCreatedContract {
    data class State(
        val isLoading: Boolean = false,
        val roomInfo: RoomInfo? = null,
        val inviteLink: String = "",
        val qrCodeData: String = "",
        val errorMessage: String? = null
    )

    /**
     * 作成されたルーム情報
     */
    data class RoomInfo(
        val id: String,
        val emoji: String,
        val title: String,
        val dateRange: String,
        val destination: String
    )

    sealed interface Intent {
        data class OnScreenDisplayed(val roomId: String) : Intent
        data object OnCopyLinkPressed : Intent
        data object OnShareQRPressed : Intent
        data object OnGoToRoomPressed : Intent
        data object OnBackToHomePressed : Intent
    }

    sealed interface Effect {
        data class ShowError(val message: String) : Effect
        data class ShowSuccess(val message: String) : Effect
        data class NavigateToRoomDetail(val roomId: String) : Effect
        data object NavigateToHome : Effect
        data class ShareInviteLink(val link: String) : Effect
    }
}
