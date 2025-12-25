package com.yoin.feature.settings.model

/**
 * 通知設定モデル
 */
data class NotificationSettings(
    // プッシュ通知（マスタースイッチ）
    val pushNotificationEnabled: Boolean = true,

    // アクティビティ
    val tripInvitationEnabled: Boolean = true,
    val memberJoinedEnabled: Boolean = true,
    val photoDevelopedEnabled: Boolean = true, // 常にON（変更不可）
    val tripReminderEnabled: Boolean = false,

    // お知らせ
    val newFeatureEnabled: Boolean = true,
    val campaignEnabled: Boolean = false,
)
