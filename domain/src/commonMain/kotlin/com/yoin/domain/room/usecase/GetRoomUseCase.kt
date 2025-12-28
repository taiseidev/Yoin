package com.yoin.domain.room.usecase

import com.yoin.domain.room.model.Room
import com.yoin.domain.room.repository.RoomRepository
import kotlinx.coroutines.flow.Flow

/**
 * ルームを取得するUseCase
 */
class GetRoomUseCase(
    private val roomRepository: RoomRepository
) {
    operator fun invoke(roomId: String): Flow<Room?> {
        // TODO: データソース実装後にダミーデータを削除
        return roomRepository.getRoom(roomId)
    }
}
