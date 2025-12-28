package com.yoin.domain.subscription.usecase

import com.yoin.domain.subscription.model.Subscription
import com.yoin.domain.subscription.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow

/**
 * 現在のサブスクリプションを取得するUseCase
 */
class GetCurrentSubscriptionUseCase(
    private val subscriptionRepository: SubscriptionRepository
) {
    operator fun invoke(): Flow<Subscription?> {
        // TODO: データソース実装後にダミーデータを削除
        return subscriptionRepository.getCurrentSubscription()
    }
}
