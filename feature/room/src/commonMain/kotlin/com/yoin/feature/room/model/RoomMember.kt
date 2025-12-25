package com.yoin.feature.room.model

/**
 * ルームメンバー情報モデル
 */
data class RoomMember(
    val id: String,
    val name: String,
    val avatar: String = "", // アバター画像URL
    val role: MemberRole = MemberRole.MEMBER,
    val joinedDate: String,
)

/**
 * メンバーロール
 */
enum class MemberRole {
    OWNER,   // オーナー
    ADMIN,   // 管理者
    MEMBER,  // 一般メンバー
}
