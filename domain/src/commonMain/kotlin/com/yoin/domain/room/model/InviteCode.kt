package com.yoin.domain.room.model

import kotlinx.datetime.Instant

/**
 * 招待コード
 */
data class InviteCode(
    val id: String,
    val roomId: String,
    val code: String,
    val type: InviteType,
    val isActive: Boolean,
    val useCount: Int,
    val maxUses: Int?,
    val expiresAt: Instant?,
    val createdBy: String,
    val createdAt: Instant
)

/**
 * 招待タイプ
 */
enum class InviteType {
    LINK,   // リンク
    QR      // QRコード
}
