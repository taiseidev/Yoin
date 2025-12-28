package com.yoin.domain.auth.model

import kotlinx.datetime.Instant

/**
 * ユーザードメインモデル
 */
data class User(
    val id: String,
    val firebaseUid: String?,
    val email: String?,
    val displayName: String,
    val avatarUrl: String?,
    val plan: UserPlan,
    val isGuest: Boolean,
    val guestConvertedAt: Instant?,
    val createdAt: Instant,
    val updatedAt: Instant
)

/**
 * ユーザープラン
 */
enum class UserPlan {
    GUEST,      // ゲスト（撮影5枚/ルーム、DL不可）
    FREE,       // 無料（撮影24枚/日、月1ルーム作成、3ヶ月保存）
    PREMIUM;    // プレミアム（撮影36枚/日、無制限作成、永久保存）

    /**
     * 1日の撮影枚数制限
     */
    val dailyPhotoLimit: Int
        get() = when (this) {
            GUEST -> 0  // ゲストはルーム全体で5枚
            FREE -> 24
            PREMIUM -> 36
        }

    /**
     * ダウンロード可能かどうか
     */
    val canDownload: Boolean
        get() = this != GUEST
}
