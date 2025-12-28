package com.yoin.domain.user.repository

import com.yoin.domain.user.model.NotificationSettings
import com.yoin.domain.user.model.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * ユーザー設定リポジトリ
 */
interface UserPreferencesRepository {
    /**
     * ユーザー設定を取得
     */
    fun getUserPreferences(): Flow<UserPreferences?>

    /**
     * ユーザー設定を更新
     */
    suspend fun updateUserPreferences(preferences: UserPreferences): Result<UserPreferences>

    /**
     * 通知設定を取得
     */
    fun getNotificationSettings(): Flow<NotificationSettings?>

    /**
     * 通知設定を更新
     */
    suspend fun updateNotificationSettings(settings: NotificationSettings): Result<NotificationSettings>
}
