package com.yoin.feature.notifications.model

import kotlinx.datetime.LocalDateTime

/**
 * 通知の種類
 */
enum class NotificationType {
    PHOTO_DEVELOPED,    // 写真が現像された
    MEMBER_JOINED,      // メンバーが参加した
    INVITATION,         // 招待
    TRIP_REMINDER,      // 旅行開始リマインダー
    SYSTEM              // システム通知
}

/**
 * 通知モデル
 */
data class Notification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestamp: LocalDateTime,
    val isRead: Boolean = false,
    val icon: String? = null,           // 絵文字アイコン
    val avatarText: String? = null,     // アバター用の文字（例: "田"）
    val relatedTripId: String? = null,  // 関連するトリップID
    val relatedUserId: String? = null,  // 関連するユーザーID
)

/**
 * 通知のセクション
 */
enum class NotificationSection {
    TODAY,      // 今日
    YESTERDAY,  // 昨日
    OLDER       // それ以前
}

/**
 * セクション付き通知リスト
 */
data class NotificationGroup(
    val section: NotificationSection,
    val notifications: List<Notification>
)
