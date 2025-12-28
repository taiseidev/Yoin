package com.yoin.domain.notification.usecase

import com.yoin.domain.notification.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow

/**
 * 未読通知数を取得するUseCase
 */
class GetUnreadCountUseCase(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(): Flow<Int> {
        // TODO: データソース実装後にダミーデータを削除
        return notificationRepository.getUnreadCount()
    }
}
