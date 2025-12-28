package com.yoin.domain.room.model

import kotlinx.datetime.Instant

/**
 * ルームメンバー
 */
data class RoomMember(
    val id: String,
    val roomId: String,
    val userId: String,
    val role: MemberRole,
    val nickname: String?,
    val joinedAt: Instant,
    val leftAt: Instant?,
    val isActive: Boolean
)

/**
 * メンバー役割
 */
enum class MemberRole {
    OWNER,      // オーナー
    MEMBER      // メンバー
}
