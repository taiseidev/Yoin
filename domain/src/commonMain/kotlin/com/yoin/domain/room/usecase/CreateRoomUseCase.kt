package com.yoin.domain.room.usecase

import com.yoin.domain.room.model.Room
import com.yoin.domain.room.repository.RoomRepository
import kotlinx.datetime.LocalDate

/**
 * ルームを作成するUseCase
 */
class CreateRoomUseCase(
    private val roomRepository: RoomRepository
) {
    suspend operator fun invoke(
        name: String,
        destination: String?,
        iconEmoji: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Result<Room> {
        // バリデーション
        if (name.isBlank()) {
            return Result.failure(IllegalArgumentException("ルーム名を入力してください"))
        }

        if (startDate > endDate) {
            return Result.failure(IllegalArgumentException("開始日は終了日より前の日付を指定してください"))
        }

        // TODO: データソース実装後にダミーデータを削除
        return roomRepository.createRoom(name, destination, iconEmoji, startDate, endDate)
    }
}
