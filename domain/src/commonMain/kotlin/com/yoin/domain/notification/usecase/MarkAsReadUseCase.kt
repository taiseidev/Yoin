package com.yoin.domain.notification.usecase

import com.yoin.domain.notification.repository.NotificationRepository

/**
 * 通知を既読にするUseCase
 */
class MarkAsReadUseCase(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(notificationId: String): Result<Unit> {
        // TODO: データソース実装後にダミーデータを削除
        return notificationRepository.markAsRead(notificationId)
    }
}
