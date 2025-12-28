package com.yoin.data.remote.dto

import com.yoin.domain.room.model.DevelopmentType
import com.yoin.domain.room.model.Room
import com.yoin.domain.room.model.RoomStatus
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * ルームDTO - Supabase roomsテーブルに対応
 */
@Serializable
data class RoomDto(
    @SerialName("id")
    val id: String,

    @SerialName("name")
    val name: String,

    @SerialName("destination")
    val destination: String? = null,

    @SerialName("icon_emoji")
    val iconEmoji: String,

    @SerialName("start_date")
    val startDate: String, // YYYY-MM-DD format

    @SerialName("end_date")
    val endDate: String, // YYYY-MM-DD format

    @SerialName("status")
    val status: String, // "UPCOMING", "ACTIVE", "PENDING_DEVELOPMENT", "DEVELOPED", "ARCHIVED", "DELETED"

    @SerialName("development_type")
    val developmentType: String = "NEXT_MORNING", // "NEXT_MORNING", "IMMEDIATE", "CUSTOM"

    @SerialName("development_scheduled_at")
    val developmentScheduledAt: String? = null, // ISO8601 format

    @SerialName("developed_at")
    val developedAt: String? = null, // ISO8601 format

    @SerialName("total_photos")
    val totalPhotos: Int = 0,

    @SerialName("owner_id")
    val ownerId: String,

    @SerialName("created_at")
    val createdAt: String, // ISO8601 format

    @SerialName("updated_at")
    val updatedAt: String // ISO8601 format
)

/**
 * RoomDtoをドメインモデルに変換
 */
@OptIn(kotlin.time.ExperimentalTime::class)
fun RoomDto.toDomain(): Room = Room(
    id = id,
    name = name,
    destination = destination,
    iconEmoji = iconEmoji,
    startDate = LocalDate.parse(startDate),
    endDate = LocalDate.parse(endDate),
    status = when (status) {
        "UPCOMING" -> RoomStatus.UPCOMING
        "ACTIVE" -> RoomStatus.ACTIVE
        "PENDING_DEVELOPMENT" -> RoomStatus.PENDING_DEVELOPMENT
        "DEVELOPED" -> RoomStatus.DEVELOPED
        "ARCHIVED" -> RoomStatus.ARCHIVED
        "DELETED" -> RoomStatus.DELETED
        else -> RoomStatus.UPCOMING
    },
    developmentType = when (developmentType) {
        "NEXT_MORNING" -> DevelopmentType.NEXT_MORNING
        "IMMEDIATE" -> DevelopmentType.IMMEDIATE
        "CUSTOM" -> DevelopmentType.CUSTOM
        else -> DevelopmentType.NEXT_MORNING
    },
    developmentScheduledAt = developmentScheduledAt?.let { Instant.parse(it) },
    developedAt = developedAt?.let { Instant.parse(it) },
    totalPhotos = totalPhotos,
    ownerId = ownerId,
    createdAt = Instant.parse(createdAt),
    updatedAt = Instant.parse(updatedAt)
)
