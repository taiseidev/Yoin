package com.yoin.domain.user.usecase

import com.yoin.domain.user.model.UserPreferences
import com.yoin.domain.user.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow

/**
 * ユーザー設定を取得するUseCase
 */
class GetUserPreferencesUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<UserPreferences?> {
        // TODO: データソース実装後にダミーデータを削除
        return userPreferencesRepository.getUserPreferences()
    }
}
