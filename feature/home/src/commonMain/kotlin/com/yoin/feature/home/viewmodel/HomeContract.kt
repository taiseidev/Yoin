package com.yoin.feature.home.viewmodel

/**
 * ホーム画面のMVI Contract
 */
object HomeContract {
    /**
     * UI状態
     */
    data class State(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val ongoingTrips: List<TripItem> = emptyList(),
        val completedTrips: List<TripItem> = emptyList(),
        val hasNotification: Boolean = false
    )

    /**
     * 旅行アイテム
     */
    data class TripItem(
        val id: String,
        val emoji: String,
        val title: String,
        val dateRange: String,
        val location: String,
        val progress: Float? = null, // 0.0-1.0、nullの場合は完了済み
        val daysUntilDevelopment: Int? = null,
        val photoCount: Int? = null,
        val memberAvatars: List<String> = emptyList(),
        val additionalMemberCount: Int = 0
    )

    /**
     * ユーザーインテント（アクション）
     */
    sealed interface Intent {
        /**
         * 画面が表示された
         */
        data object OnScreenDisplayed : Intent

        /**
         * リフレッシュ
         */
        data object OnRefresh : Intent

        /**
         * 旅行カードをタップ
         */
        data class OnTripTapped(val tripId: String) : Intent

        /**
         * すべてを表示をタップ
         */
        data class OnViewAllTapped(val section: TripSection) : Intent

        /**
         * 通知アイコンをタップ
         */
        data object OnNotificationTapped : Intent
    }

    /**
     * 旅行セクション
     */
    enum class TripSection {
        ONGOING,
        COMPLETED
    }

    /**
     * 一時的なイベント（副作用）
     */
    sealed interface Effect {
        /**
         * エラーメッセージを表示
         */
        data class ShowError(val message: String) : Effect

        /**
         * 旅行詳細画面へ遷移
         */
        data class NavigateToTripDetail(val tripId: String) : Effect

        /**
         * 旅行リスト画面へ遷移
         */
        data class NavigateToTripList(val section: TripSection) : Effect

        /**
         * 通知画面へ遷移
         */
        data object NavigateToNotifications : Effect
    }
}
