package com.yoin.data.remote.dto

import com.yoin.domain.auth.model.User
import com.yoin.domain.auth.model.UserPlan
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * ユーザーDTO - Supabase usersテーブルに対応
 */
@Serializable
data class UserDto(
    @SerialName("id")
    val id: String,

    @SerialName("firebase_uid")
    val firebaseUid: String? = null,

    @SerialName("email")
    val email: String? = null,

    @SerialName("display_name")
    val displayName: String,

    @SerialName("avatar_url")
    val avatarUrl: String? = null,

    @SerialName("plan")
    val plan: String = "FREE", // "GUEST", "FREE", "PREMIUM"

    @SerialName("is_guest")
    val isGuest: Boolean = false,

    @SerialName("guest_converted_at")
    val guestConvertedAt: String? = null, // ISO8601 format

    @SerialName("created_at")
    val createdAt: String, // ISO8601 format

    @SerialName("updated_at")
    val updatedAt: String // ISO8601 format
)

/**
 * UserDtoをドメインモデルに変換
 */
@OptIn(kotlin.time.ExperimentalTime::class)
fun UserDto.toDomain(): User = User(
    id = id,
    firebaseUid = firebaseUid,
    email = email,
    displayName = displayName,
    avatarUrl = avatarUrl,
    plan = when (plan) {
        "GUEST" -> UserPlan.GUEST
        "FREE" -> UserPlan.FREE
        "PREMIUM" -> UserPlan.PREMIUM
        else -> UserPlan.FREE
    },
    isGuest = isGuest,
    guestConvertedAt = guestConvertedAt?.let { Instant.parse(it) },
    createdAt = Instant.parse(createdAt),
    updatedAt = Instant.parse(updatedAt)
)
