package com.yoin.feature.map.model

/**
 * 地図上の写真位置情報
 */
data class PhotoLocation(
    val photoId: String,
    val thumbnailUrl: String?,
    val title: String,
    val photographer: String,
    val timestamp: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val isSelected: Boolean = false
)

/**
 * メンバー情報（フィルター用）
 */
data class MapMember(
    val id: String,
    val name: String,
    val avatarUrl: String? = null,
    val color: String = "#E8D4BC"
)
