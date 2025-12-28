package com.yoin.domain.photo.usecase

import com.yoin.domain.photo.model.Photo
import com.yoin.domain.photo.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow

/**
 * ルームの写真一覧を取得するUseCase
 */
class GetRoomPhotosUseCase(
    private val photoRepository: PhotoRepository
) {
    operator fun invoke(roomId: String): Flow<List<Photo>> {
        // TODO: データソース実装後にダミーデータを削除
        return photoRepository.getRoomPhotos(roomId)
    }
}
