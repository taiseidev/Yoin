package com.yoin.feature.notifications.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.yoin.feature.notifications.model.Notification
import com.yoin.feature.notifications.model.NotificationGroup
import com.yoin.feature.notifications.model.NotificationSection
import com.yoin.feature.notifications.model.NotificationType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

/**
 * 通知画面のScreenModel
 *
 * 注意: 現在はUI実装のみで、実際の通知取得ロジックは未実装です。
 * ダミーデータを表示しています。
 */
class NotificationViewModel : ScreenModel {

    private val _state = MutableStateFlow(NotificationContract.State())
    val state: StateFlow<NotificationContract.State> = _state.asStateFlow()

    private val _effect = Channel<NotificationContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadNotifications()
    }

    /**
     * ユーザーの意図を処理
     */
    fun handleIntent(intent: NotificationContract.Intent) {
        when (intent) {
            is NotificationContract.Intent.OnBackPressed -> onBackPressed()
            is NotificationContract.Intent.OnMarkAllAsRead -> onMarkAllAsRead()
            is NotificationContract.Intent.OnNotificationClicked -> onNotificationClicked(intent.notification)
            is NotificationContract.Intent.OnNotificationDismissed -> onNotificationDismissed(intent.notificationId)
            is NotificationContract.Intent.OnRefresh -> onRefresh()
        }
    }

    private fun loadNotifications() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // TODO: 実際の通知データ取得処理を実装
            // 現在はダミーデータを使用
            val notifications = getDummyNotifications()
            val groups = groupNotificationsBySection(notifications)
            val unreadCount = notifications.count { !it.isRead }

            _state.update {
                it.copy(
                    isLoading = false,
                    notificationGroups = groups,
                    unreadCount = unreadCount
                )
            }
        }
    }

    private fun onBackPressed() {
        screenModelScope.launch {
            _effect.send(NotificationContract.Effect.NavigateBack)
        }
    }

    private fun onMarkAllAsRead() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // TODO: すべての通知を既読にする処理を実装
            kotlinx.coroutines.delay(500) // ネットワーク遅延をシミュレート

            val updatedGroups = _state.value.notificationGroups.map { group ->
                group.copy(
                    notifications = group.notifications.map { notification ->
                        notification.copy(isRead = true)
                    }
                )
            }

            _state.update {
                it.copy(
                    isLoading = false,
                    notificationGroups = updatedGroups,
                    unreadCount = 0
                )
            }

            _effect.send(NotificationContract.Effect.ShowSuccess("すべての通知を既読にしました"))
        }
    }

    private fun onNotificationClicked(notification: Notification) {
        screenModelScope.launch {
            // 通知を既読にする
            val updatedGroups = _state.value.notificationGroups.map { group ->
                group.copy(
                    notifications = group.notifications.map { n ->
                        if (n.id == notification.id) {
                            n.copy(isRead = true)
                        } else {
                            n
                        }
                    }
                )
            }

            val unreadCount = updatedGroups.flatMap { it.notifications }.count { !it.isRead }

            _state.update {
                it.copy(
                    notificationGroups = updatedGroups,
                    unreadCount = unreadCount
                )
            }

            // 通知タイプに応じて画面遷移
            notification.relatedTripId?.let { tripId ->
                _effect.send(NotificationContract.Effect.NavigateToTripDetail(tripId))
            }
        }
    }

    private fun onNotificationDismissed(notificationId: String) {
        screenModelScope.launch {
            // TODO: 通知を削除する処理を実装
            val updatedGroups = _state.value.notificationGroups.map { group ->
                group.copy(
                    notifications = group.notifications.filter { it.id != notificationId }
                )
            }.filter { it.notifications.isNotEmpty() }

            val unreadCount = updatedGroups.flatMap { it.notifications }.count { !it.isRead }

            _state.update {
                it.copy(
                    notificationGroups = updatedGroups,
                    unreadCount = unreadCount
                )
            }
        }
    }

    private fun onRefresh() {
        loadNotifications()
    }

    private fun groupNotificationsBySection(notifications: List<Notification>): List<NotificationGroup> {
        val grouped = notifications.groupBy { notification ->
            // TODO: 実際のタイムスタンプを使用してセクション分け
            // 現在はダミーロジック
            when {
                notification.message.contains("10分前") || notification.message.contains("2時間前") -> NotificationSection.TODAY
                notification.message.contains("昨日") -> NotificationSection.YESTERDAY
                else -> NotificationSection.OLDER
            }
        }

        return listOf(
            NotificationSection.TODAY,
            NotificationSection.YESTERDAY,
            NotificationSection.OLDER
        ).mapNotNull { section ->
            grouped[section]?.let { notifs ->
                NotificationGroup(section, notifs)
            }
        }
    }

    private fun getDummyNotifications(): List<Notification> {
        return listOf(
            Notification(
                id = "1",
                type = NotificationType.PHOTO_DEVELOPED,
                title = "写真が現像されました！",
                message = "北海道旅行2025の写真48枚が見られます\n10分前",
                timestamp = LocalDateTime(2025, 1, 1, 12, 0),
                isRead = false,
                icon = "🎉",
                relatedTripId = "trip_hokkaido_2025"
            ),
            Notification(
                id = "2",
                type = NotificationType.MEMBER_JOINED,
                title = "田中太郎さんが参加しました",
                message = "北海道旅行2025に新しいメンバー\n2時間前",
                timestamp = LocalDateTime(2025, 1, 1, 10, 0),
                isRead = false,
                avatarText = "田",
                relatedTripId = "trip_hokkaido_2025",
                relatedUserId = "user_tanaka"
            ),
            Notification(
                id = "3",
                type = NotificationType.INVITATION,
                title = "山田花子さんから招待",
                message = "沖縄旅行2025に招待されました\n昨日 18:30",
                timestamp = LocalDateTime(2024, 12, 31, 18, 30),
                isRead = true,
                avatarText = "山",
                relatedTripId = "trip_okinawa_2025",
                relatedUserId = "user_yamada"
            ),
            Notification(
                id = "4",
                type = NotificationType.TRIP_REMINDER,
                title = "明日から旅行開始！",
                message = "北海道旅行2025が明日スタートします\n昨日 9:00",
                timestamp = LocalDateTime(2024, 12, 31, 9, 0),
                isRead = true,
                icon = "💡",
                relatedTripId = "trip_hokkaido_2025"
            ),
            Notification(
                id = "5",
                type = NotificationType.SYSTEM,
                title = "新機能のお知らせ",
                message = "フォトブック機能がリリースされました\n3日前",
                timestamp = LocalDateTime(2024, 12, 29, 10, 0),
                isRead = true,
                icon = "📢"
            )
        )
    }
}
