package com.yoin.domain.photo.usecase

import com.yoin.domain.photo.model.Photo
import com.yoin.domain.photo.repository.PhotoRepository
import kotlinx.datetime.Instant

/**
 * 写真をアップロードするUseCase
 */
class UploadPhotoUseCase(
    private val photoRepository: PhotoRepository
) {
    suspend operator fun invoke(
        roomId: String,
        imagePath: String,
        latitude: Double?,
        longitude: Double?,
        takenAt: Instant
    ): Result<Photo> {
        // 撮影枚数制限をチェック
        val limit = photoRepository.getPhotoLimit(roomId)
        if (!limit.canTakePhoto) {
            return Result.failure(
                IllegalStateException(
                    if (limit.isGuest) {
                        "ゲストユーザーの撮影上限（5枚）に達しました。アカウント登録すると24枚/日まで撮影できます。"

                    } else {
                        "本日の撮影上限（${limit.limit}枚）に達しました。"
                    }
                )
            )
        }

        // TODO: データソース実装後にダミーデータを削除
        return photoRepository.uploadPhoto(roomId, imagePath, latitude, longitude, takenAt)
    }
}
