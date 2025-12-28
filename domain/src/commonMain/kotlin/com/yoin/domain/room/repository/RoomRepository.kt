package com.yoin.domain.room.repository

import com.yoin.domain.room.model.InviteCode
import com.yoin.domain.room.model.Room
import com.yoin.domain.room.model.RoomMember
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

/**
 * ルームリポジトリ
 */
interface RoomRepository {
    /**
     * ルーム一覧を取得
     */
    fun getRooms(userId: String): Flow<List<Room>>

    /**
     * ルームを取得
     */
    fun getRoom(roomId: String): Flow<Room?>

    /**
     * ルームを作成
     */
    suspend fun createRoom(
        name: String,
        destination: String?,
        iconEmoji: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Result<Room>

    /**
     * ルームを更新
     */
    suspend fun updateRoom(
        roomId: String,
        name: String? = null,
        destination: String? = null,
        iconEmoji: String? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null
    ): Result<Room>

    /**
     * ルームを削除（論理削除）
     */
    suspend fun deleteRoom(roomId: String): Result<Unit>

    /**
     * 招待コードを生成
     */
    suspend fun generateInviteCode(roomId: String): Result<InviteCode>

    /**
     * 招待コードからルームに参加
     */
    suspend fun joinRoomWithCode(code: String): Result<Room>

    /**
     * ルームメンバー一覧を取得
     */
    fun getRoomMembers(roomId: String): Flow<List<RoomMember>>

    /**
     * ルームから退出
     */
    suspend fun leaveRoom(roomId: String): Result<Unit>
}
