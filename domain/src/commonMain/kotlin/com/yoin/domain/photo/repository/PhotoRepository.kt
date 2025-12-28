package com.yoin.domain.photo.repository

import com.yoin.domain.photo.model.Photo
import com.yoin.domain.photo.model.PhotoLimit
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

/**
 * 写真リポジトリ
 */
interface PhotoRepository {
    /**
     * ルームの写真一覧を取得
     */
    fun getRoomPhotos(roomId: String): Flow<List<Photo>>

    /**
     * 写真を取得
     */
    fun getPhoto(photoId: String): Flow<Photo?>

    /**
     * 撮影枚数制限を取得
     */
    suspend fun getPhotoLimit(roomId: String): PhotoLimit

    /**
     * 写真を撮影（アップロード）
     */
    suspend fun uploadPhoto(
        roomId: String,
        imagePath: String,
        latitude: Double?,
        longitude: Double?,
        takenAt: Instant
    ): Result<Photo>

    /**
     * 写真をダウンロード
     */
    suspend fun downloadPhoto(
        photoId: String,
        highQuality: Boolean = false
    ): Result<String>

    /**
     * 写真を削除
     */
    suspend fun deletePhoto(photoId: String): Result<Unit>

    /**
     * 写真の既読状態を更新
     */
    suspend fun markPhotoAsViewed(photoId: String): Result<Unit>
}
