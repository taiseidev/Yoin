package com.yoin.domain.room.usecase

import com.yoin.domain.room.model.Room
import com.yoin.domain.room.repository.RoomRepository

/**
 * 招待コードでルームに参加するUseCase
 */
class JoinRoomWithCodeUseCase(
    private val roomRepository: RoomRepository
) {
    suspend operator fun invoke(code: String): Result<Room> {
        if (code.isBlank()) {
            return Result.failure(IllegalArgumentException("招待コードを入力してください"))
        }

        // TODO: データソース実装後にダミーデータを削除
        return roomRepository.joinRoomWithCode(code)
    }
}
