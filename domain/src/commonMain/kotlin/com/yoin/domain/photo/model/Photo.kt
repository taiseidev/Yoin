package com.yoin.domain.photo.model

import kotlinx.datetime.Instant

/**
 * 写真ドメインモデル
 */
data class Photo(
    val id: String,
    val roomId: String,
    val userId: String,
    val storagePath: String,
    val storagePathHq: String?,
    val thumbnailPath: String?,
    val status: PhotoStatus,
    val rejectionReason: String?,
    val filterId: String?,
    val hasDateStamp: Boolean,
    val location: PhotoLocation?,
    val isVisible: Boolean,
    val takenAt: Instant,
    val expiresAt: Instant?,
    val createdAt: Instant
)

/**
 * 写真ステータス
 */
enum class PhotoStatus {
    PENDING,        // AI判定待ち
    APPROVED,       // 承認済み
    REJECTED,       // 却下（不適切/真っ暗/ブレ等）
    RETURNED        // 返却済み（枚数返却）
}

/**
 * 写真位置情報
 */
data class PhotoLocation(
    val latitude: Double,
    val longitude: Double,
    val locationName: String?,
    val address: String?
)

/**
 * 撮影枚数制限情報
 */
data class PhotoLimit(
    val remaining: Int,
    val limit: Int,
    val isGuest: Boolean
) {
    val canTakePhoto: Boolean
        get() = remaining > 0
}
