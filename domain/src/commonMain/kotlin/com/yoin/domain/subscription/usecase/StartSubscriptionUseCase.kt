package com.yoin.domain.subscription.usecase

import com.yoin.domain.subscription.model.Subscription
import com.yoin.domain.subscription.repository.SubscriptionRepository

/**
 * サブスクリプションを開始するUseCase
 */
class StartSubscriptionUseCase(
    private val subscriptionRepository: SubscriptionRepository
) {
    suspend operator fun invoke(
        plan: String,
        storeProductId: String,
        storeTransactionId: String
    ): Result<Subscription> {
        // TODO: データソース実装後にダミーデータを削除
        return subscriptionRepository.startSubscription(
            plan,
            storeProductId,
            storeTransactionId
        )
    }
}
