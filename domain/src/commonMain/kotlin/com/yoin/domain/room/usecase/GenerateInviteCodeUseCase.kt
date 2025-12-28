package com.yoin.domain.room.usecase

import com.yoin.domain.room.model.InviteCode
import com.yoin.domain.room.repository.RoomRepository

/**
 * 招待コードを生成するUseCase
 */
class GenerateInviteCodeUseCase(
    private val roomRepository: RoomRepository
) {
    suspend operator fun invoke(roomId: String): Result<InviteCode> {
        // TODO: データソース実装後にダミーデータを削除
        return roomRepository.generateInviteCode(roomId)
    }
}
