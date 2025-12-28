package com.yoin.domain.room.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

/**
 * ルームドメインモデル
 */
data class Room(
    val id: String,
    val name: String,
    val destination: String?,
    val iconEmoji: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: RoomStatus,
    val developmentType: DevelopmentType,
    val developmentScheduledAt: Instant?,
    val developedAt: Instant?,
    val totalPhotos: Int,
    val ownerId: String,
    val createdAt: Instant,
    val updatedAt: Instant
)

/**
 * ルームステータス
 */
enum class RoomStatus {
    UPCOMING,           // 旅行開始前
    ACTIVE,             // 旅行中（撮影可能）
    PENDING_DEVELOPMENT,// 旅行終了、現像待ち
    DEVELOPED,          // 現像済み
    ARCHIVED,           // アーカイブ済み
    DELETED             // 削除済み（論理削除）
}

/**
 * 現像タイミング（v2.0対応）
 */
enum class DevelopmentType {
    NEXT_MORNING,   // 翌朝9時（デフォルト）
    IMMEDIATE,      // 即時
    CUSTOM          // カスタム時刻
}
