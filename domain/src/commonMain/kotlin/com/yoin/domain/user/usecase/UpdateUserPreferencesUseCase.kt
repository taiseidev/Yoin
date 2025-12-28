package com.yoin.domain.user.usecase

import com.yoin.domain.user.model.UserPreferences
import com.yoin.domain.user.repository.UserPreferencesRepository

/**
 * ユーザー設定を更新するUseCase
 */
class UpdateUserPreferencesUseCase(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(preferences: UserPreferences): Result<UserPreferences> {
        // TODO: データソース実装後にダミーデータを削除
        return userPreferencesRepository.updateUserPreferences(preferences)
    }
}
