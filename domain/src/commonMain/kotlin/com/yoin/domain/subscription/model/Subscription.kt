package com.yoin.domain.subscription.model

import kotlinx.datetime.Instant

/**
 * サブスクリプション
 */
data class Subscription(
    val id: String,
    val userId: String,
    val plan: SubscriptionPlan,
    val status: SubscriptionStatus,
    val platform: Platform,
    val currentPeriodStart: Instant,
    val currentPeriodEnd: Instant,
    val trialStart: Instant?,
    val trialEnd: Instant?,
    val canceledAt: Instant?,
    val createdAt: Instant,
    val updatedAt: Instant
)

/**
 * サブスクリプションプラン
 */
enum class SubscriptionPlan {
    MONTHLY,    // 月額
    YEARLY      // 年額
}

/**
 * サブスクリプションステータス
 */
enum class SubscriptionStatus {
    ACTIVE,         // アクティブ
    CANCELED,       // キャンセル済み
    EXPIRED,        // 期限切れ
    TRIAL,          // トライアル中
    PAST_DUE        // 支払い遅延
}

/**
 * プラットフォーム
 */
enum class Platform {
    IOS,
    ANDROID,
    WEB
}
