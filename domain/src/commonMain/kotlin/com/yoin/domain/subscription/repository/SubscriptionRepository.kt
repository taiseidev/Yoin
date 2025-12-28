package com.yoin.domain.subscription.repository

import com.yoin.domain.subscription.model.Subscription
import kotlinx.coroutines.flow.Flow

/**
 * サブスクリプションリポジトリ
 */
interface SubscriptionRepository {
    /**
     * 現在のサブスクリプションを取得
     */
    fun getCurrentSubscription(): Flow<Subscription?>

    /**
     * サブスクリプションを開始
     */
    suspend fun startSubscription(
        plan: String,
        storeProductId: String,
        storeTransactionId: String
    ): Result<Subscription>

    /**
     * サブスクリプションをキャンセル
     */
    suspend fun cancelSubscription(reason: String? = null): Result<Unit>

    /**
     * サブスクリプションを復元
     */
    suspend fun restoreSubscription(): Result<Subscription?>
}
