package com.yoin.feature.room.model

/**
 * ルーム情報モデル
 */
data class RoomInfo(
    val id: String,
    val name: String,
    val icon: String = "🏔", // 絵文字アイコン
    val destination: String,
    val startDate: String,
    val endDate: String,
    val memberCount: Int,
    val isOwner: Boolean = false, // このユーザーがオーナーかどうか
)
