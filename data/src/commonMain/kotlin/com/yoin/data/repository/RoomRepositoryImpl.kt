package com.yoin.data.repository

import com.yoin.domain.room.model.DevelopmentType
import com.yoin.domain.room.model.InviteCode
import com.yoin.domain.room.model.Room
import com.yoin.domain.room.model.RoomMember
import com.yoin.domain.room.model.RoomStatus
import com.yoin.domain.room.repository.RoomRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlin.random.Random

/**
 * ルームリポジトリの実装（仮実装）
 * TODO: Supabaseとの連携を実装
 */
@OptIn(kotlin.time.ExperimentalTime::class)
class RoomRepositoryImpl : RoomRepository {
    override fun getRooms(userId: String): Flow<List<Room>> {
        // TODO: Supabaseからデータを取得
        return flowOf(emptyList())
    }

    override fun getRoom(roomId: String): Flow<Room?> {
        // TODO: Supabaseからデータを取得
        return flowOf(null)
    }

    override suspend fun createRoom(
        name: String,
        destination: String?,
        iconEmoji: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Result<Room> {
        // TODO: Supabaseにデータを保存
        return try {
            // 仮実装: 実際のSupabase実装時に現在時刻を正しく設定
            val now = Instant.DISTANT_PAST
            val room = Room(
                id = "room_${Random.nextInt(100000, 999999)}",
                name = name,
                destination = destination,
                iconEmoji = iconEmoji,
                startDate = startDate,
                endDate = endDate,
                status = RoomStatus.UPCOMING,
                developmentType = DevelopmentType.NEXT_MORNING,
                developmentScheduledAt = null,
                developedAt = null,
                totalPhotos = 0,
                ownerId = "user_${Random.nextInt(100000, 999999)}", // TODO: 実際のユーザーIDを使用
                createdAt = now,
                updatedAt = now
            )
            Result.success(room)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateRoom(
        roomId: String,
        name: String?,
        destination: String?,
        iconEmoji: String?,
        startDate: LocalDate?,
        endDate: LocalDate?
    ): Result<Room> {
        // TODO: Supabaseのデータを更新
        return Result.failure(NotImplementedError("updateRoom is not implemented yet"))
    }

    override suspend fun deleteRoom(roomId: String): Result<Unit> {
        // TODO: Supabaseで論理削除
        return Result.failure(NotImplementedError("deleteRoom is not implemented yet"))
    }

    override suspend fun generateInviteCode(roomId: String): Result<InviteCode> {
        // TODO: Supabaseで招待コードを生成
        return Result.failure(NotImplementedError("generateInviteCode is not implemented yet"))
    }

    override suspend fun joinRoomWithCode(code: String): Result<Room> {
        // TODO: Supabaseで招待コードからルームに参加
        return Result.failure(NotImplementedError("joinRoomWithCode is not implemented yet"))
    }

    override fun getRoomMembers(roomId: String): Flow<List<RoomMember>> {
        // TODO: Supabaseからメンバー一覧を取得
        return flowOf(emptyList())
    }

    override suspend fun leaveRoom(roomId: String): Result<Unit> {
        // TODO: Supabaseでルームから退出
        return Result.failure(NotImplementedError("leaveRoom is not implemented yet"))
    }
}
