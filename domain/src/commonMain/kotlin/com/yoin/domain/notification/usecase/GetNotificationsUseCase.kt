package com.yoin.domain.notification.usecase

import com.yoin.domain.notification.model.Notification
import com.yoin.domain.notification.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow

/**
 * 通知一覧を取得するUseCase
 */
class GetNotificationsUseCase(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(): Flow<List<Notification>> {
        // TODO: データソース実装後にダミーデータを削除
        return notificationRepository.getNotifications()
    }
}
