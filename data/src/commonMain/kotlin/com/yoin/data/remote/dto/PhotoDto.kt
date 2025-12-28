package com.yoin.data.remote.dto

import com.yoin.domain.photo.model.Photo
import com.yoin.domain.photo.model.PhotoLocation
import com.yoin.domain.photo.model.PhotoStatus
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 写真DTO - Supabase photosテーブルに対応
 */
@Serializable
data class PhotoDto(
    @SerialName("id")
    val id: String,

    @SerialName("room_id")
    val roomId: String,

    @SerialName("user_id")
    val userId: String,

    @SerialName("storage_path")
    val storagePath: String,

    @SerialName("storage_path_hq")
    val storagePathHq: String? = null,

    @SerialName("thumbnail_path")
    val thumbnailPath: String? = null,

    @SerialName("status")
    val status: String, // "PENDING", "APPROVED", "REJECTED", "RETURNED"

    @SerialName("rejection_reason")
    val rejectionReason: String? = null,

    @SerialName("filter_id")
    val filterId: String? = null,

    @SerialName("has_date_stamp")
    val hasDateStamp: Boolean = false,

    @SerialName("location")
    val location: PhotoLocationDto? = null,

    @SerialName("is_visible")
    val isVisible: Boolean = true,

    @SerialName("taken_at")
    val takenAt: String, // ISO8601 format

    @SerialName("expires_at")
    val expiresAt: String? = null, // ISO8601 format

    @SerialName("created_at")
    val createdAt: String // ISO8601 format
)

/**
 * 写真位置情報DTO
 */
@Serializable
data class PhotoLocationDto(
    @SerialName("latitude")
    val latitude: Double,

    @SerialName("longitude")
    val longitude: Double,

    @SerialName("location_name")
    val locationName: String? = null,

    @SerialName("address")
    val address: String? = null
)

/**
 * PhotoDtoをドメインモデルに変換
 */
@OptIn(kotlin.time.ExperimentalTime::class)
fun PhotoDto.toDomain(): Photo = Photo(
    id = id,
    roomId = roomId,
    userId = userId,
    storagePath = storagePath,
    storagePathHq = storagePathHq,
    thumbnailPath = thumbnailPath,
    status = when (status) {
        "PENDING" -> PhotoStatus.PENDING
        "APPROVED" -> PhotoStatus.APPROVED
        "REJECTED" -> PhotoStatus.REJECTED
        "RETURNED" -> PhotoStatus.RETURNED
        else -> PhotoStatus.PENDING
    },
    rejectionReason = rejectionReason,
    filterId = filterId,
    hasDateStamp = hasDateStamp,
    location = location?.toDomain(),
    isVisible = isVisible,
    takenAt = Instant.parse(takenAt),
    expiresAt = expiresAt?.let { Instant.parse(it) },
    createdAt = Instant.parse(createdAt)
)

/**
 * PhotoLocationDtoをドメインモデルに変換
 */
fun PhotoLocationDto.toDomain(): PhotoLocation = PhotoLocation(
    latitude = latitude,
    longitude = longitude,
    locationName = locationName,
    address = address
)
