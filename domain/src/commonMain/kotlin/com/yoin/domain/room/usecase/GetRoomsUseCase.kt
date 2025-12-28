package com.yoin.domain.room.usecase

import com.yoin.domain.room.model.Room
import com.yoin.domain.room.repository.RoomRepository
import kotlinx.coroutines.flow.Flow

/**
 * ルーム一覧を取得するUseCase
 */
class GetRoomsUseCase(
    private val roomRepository: RoomRepository
) {
    operator fun invoke(userId: String): Flow<List<Room>> {
        // TODO: データソース実装後にダミーデータを削除
        return roomRepository.getRooms(userId)
    }
}
