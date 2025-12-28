package com.yoin.domain.room.usecase

import com.yoin.domain.room.model.RoomMember
import com.yoin.domain.room.repository.RoomRepository
import kotlinx.coroutines.flow.Flow

/**
 * ルームメンバー一覧を取得するUseCase
 */
class GetRoomMembersUseCase(
    private val roomRepository: RoomRepository
) {
    operator fun invoke(roomId: String): Flow<List<RoomMember>> {
        // TODO: データソース実装後にダミーデータを削除
        return roomRepository.getRoomMembers(roomId)
    }
}
