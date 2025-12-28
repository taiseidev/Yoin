package com.yoin.domain.user.model

import kotlinx.datetime.Instant

/**
 * ユーザー設定
 */
data class UserPreferences(
    val id: String,
    val userId: String,
    val darkMode: Boolean,
    val dateStampEnabled: Boolean,
    val dateStampFormat: String,
    val dateStampFont: String?,
    val dateStampColor: String?,
    val dateStampPosition: DateStampPosition,
    val autoSaveToCameraRoll: Boolean,
    val language: String,
    val updatedAt: Instant
)

/**
 * 日付スタンプ位置
 */
enum class DateStampPosition {
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT
}

/**
 * 通知設定
 */
data class NotificationSettings(
    val id: String,
    val userId: String,
    val pushEnabled: Boolean,
    val roomInvite: Boolean,
    val memberJoined: Boolean,
    val developmentComplete: Boolean,
    val tripReminder: Boolean,
    val photoReturned: Boolean,
    val orderUpdates: Boolean,
    val subscriptionAlerts: Boolean,
    val marketing: Boolean,
    val emailEnabled: Boolean,
    val emailDevelopmentComplete: Boolean,
    val emailOrderUpdates: Boolean,
    val quietHoursEnabled: Boolean,
    val quietHoursStart: String?,
    val quietHoursEnd: String?,
    val updatedAt: Instant
)
