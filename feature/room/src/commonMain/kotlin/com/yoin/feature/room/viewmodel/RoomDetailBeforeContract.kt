package com.yoin.feature.room.viewmodel

import com.yoin.feature.room.model.RoomInfo
import com.yoin.feature.room.model.RoomMember

/**
 * ルーム詳細（現像前）画面のMVIコントラクト
 *
 * 旅行中にユーザーが最も頻繁に見る画面
 * - 撮影進捗の確認
 * - 現像までのカウントダウン
 * - カメラへの導線
 */
interface RoomDetailBeforeContract {

    /**
     * 旅行の状態
     */
    enum class TripStatus {
        BEFORE_TRIP,    // 旅行開始前
        IN_PROGRESS,    // 旅行中
        TRIP_ENDED,     // 旅行終了（現像待ち）
        LIMIT_REACHED   // 本日の撮影上限到達
    }

    /**
     * 画面の状態
     */
    data class State(
        val isLoading: Boolean = true,
        val roomInfo: RoomInfo? = null,
        val members: List<RoomMember> = emptyList(),
        val tripStatus: TripStatus = TripStatus.IN_PROGRESS,

        // 現像カウントダウン
        val daysUntilDevelopment: Int = 0,
        val developmentDateTime: String = "", // "2025/7/6 AM9:00"

        // 撮影進捗
        val todayPhotoCount: Int = 0,
        val dailyPhotoLimit: Int = 24,

        val error: String? = null,
    )

    /**
     * ユーザーの意図（アクション）
     */
    sealed interface Intent {
        data object OnScreenDisplayed : Intent
        data object OnRefresh : Intent
        data object OnInvitePressed : Intent
        data object OnMemberBarPressed : Intent
        data object OnCameraPressed : Intent
        data object OnBackPressed : Intent
        data object OnSettingsPressed : Intent
    }

    /**
     * 副作用（ナビゲーションなど）
     */
    sealed interface Effect {
        data object NavigateBack : Effect
        data class NavigateToCamera(val roomId: String) : Effect
        data class NavigateToMemberList(val roomId: String) : Effect
        data class NavigateToSettings(val roomId: String) : Effect
        data object ShowInviteDialog : Effect
        data class ShowError(val message: String) : Effect
    }
}
