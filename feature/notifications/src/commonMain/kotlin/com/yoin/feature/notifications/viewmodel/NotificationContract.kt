package com.yoin.feature.notifications.viewmodel

import com.yoin.feature.notifications.model.Notification
import com.yoin.feature.notifications.model.NotificationGroup

/**
 * 通知画面のMVIコントラクト
 */
interface NotificationContract {

    /**
     * 画面の状態
     */
    data class State(
        val isLoading: Boolean = false,
        val notificationGroups: List<NotificationGroup> = emptyList(),
        val unreadCount: Int = 0,
        val error: String? = null,
    )

    /**
     * ユーザーの意図（アクション）
     */
    sealed interface Intent {
        data object OnBackPressed : Intent
        data object OnMarkAllAsRead : Intent
        data class OnNotificationClicked(val notification: Notification) : Intent
        data class OnNotificationDismissed(val notificationId: String) : Intent
        data object OnRefresh : Intent
    }

    /**
     * 副作用（ナビゲーションなど）
     */
    sealed interface Effect {
        data object NavigateBack : Effect
        data class NavigateToTripDetail(val tripId: String) : Effect
        data class NavigateToPhotoDetail(val photoId: String) : Effect
        data class ShowError(val message: String) : Effect
        data class ShowSuccess(val message: String) : Effect
    }
}
