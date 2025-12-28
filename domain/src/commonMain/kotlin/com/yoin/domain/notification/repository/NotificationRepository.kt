package com.yoin.domain.notification.repository

import com.yoin.domain.notification.model.Notification
import kotlinx.coroutines.flow.Flow

/**
 * 通知リポジトリ
 */
interface NotificationRepository {
    /**
     * 通知一覧を取得
     */
    fun getNotifications(): Flow<List<Notification>>

    /**
     * 未読通知数を取得
     */
    fun getUnreadCount(): Flow<Int>

    /**
     * 通知を既読にする
     */
    suspend fun markAsRead(notificationId: String): Result<Unit>

    /**
     * 全ての通知を既読にする
     */
    suspend fun markAllAsRead(): Result<Unit>

    /**
     * 通知を削除
     */
    suspend fun deleteNotification(notificationId: String): Result<Unit>
}
