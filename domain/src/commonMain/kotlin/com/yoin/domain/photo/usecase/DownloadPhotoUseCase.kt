package com.yoin.domain.photo.usecase

import com.yoin.domain.photo.repository.PhotoRepository

/**
 * 写真をダウンロードするUseCase
 */
class DownloadPhotoUseCase(
    private val photoRepository: PhotoRepository
) {
    suspend operator fun invoke(
        photoId: String,
        highQuality: Boolean = false
    ): Result<String> {
        // TODO: ゲストユーザーのチェック（AuthRepositoryから取得）
        // TODO: データソース実装後にダミーデータを削除
        return photoRepository.downloadPhoto(photoId, highQuality)
    }
}
