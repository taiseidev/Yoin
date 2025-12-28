package com.yoin.domain.photo.usecase

import com.yoin.domain.photo.model.PhotoLimit
import com.yoin.domain.photo.repository.PhotoRepository

/**
 * 撮影枚数制限を取得するUseCase
 */
class GetPhotoLimitUseCase(
    private val photoRepository: PhotoRepository
) {
    suspend operator fun invoke(roomId: String): PhotoLimit {
        // TODO: データソース実装後にダミーデータを削除
        return photoRepository.getPhotoLimit(roomId)
    }
}
