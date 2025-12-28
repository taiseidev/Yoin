package com.yoin.domain.notification.model

import kotlinx.datetime.Instant

/**
 * 通知ドメインモデル
 */
data class Notification(
    val id: String,
    val userId: String,
    val type: NotificationType,
    val category: NotificationCategory,
    val title: String,
    val body: String?,
    val imageUrl: String?,
    val actionUrl: String?,
    val isRead: Boolean,
    val readAt: Instant?,
    val createdAt: Instant
)

/**
 * 通知タイプ
 */
enum class NotificationType {
    ROOM_INVITE,            // ルーム招待
    MEMBER_JOINED,          // メンバー参加
    DEVELOPMENT_COMPLETE,   // 現像完了
    DEVELOPMENT_REMINDER,   // 現像リマインド
    PHOTO_RETURNED,         // 写真返却
    PHOTO_REJECTED,         // 写真却下
    TRIP_REMINDER,          // 旅行開始リマインド（前日）
    ROOM_EXPIRING,          // ルーム削除警告（無料プラン）
    ORDER_CONFIRMED,        // 注文確定
    ORDER_SHIPPED,          // 注文発送
    ORDER_DELIVERED,        // 配達完了
    SUBSCRIPTION_EXPIRING,  // サブスク期限切れ間近
    SUBSCRIPTION_EXPIRED,   // サブスク期限切れ
    COUPON_RECEIVED,        // クーポン付与
    COUPON_EXPIRING,        // クーポン期限切れ間近
    SYSTEM,                 // システム通知
    MARKETING               // キャンペーン通知
}

/**
 * 通知カテゴリ
 */
enum class NotificationCategory {
    ROOM,           // ルーム関連
    PHOTO,          // 写真関連
    ORDER,          // 注文関連
    SUBSCRIPTION,   // サブスク関連
    GENERAL,        // 一般
    MARKETING       // マーケティング
}
