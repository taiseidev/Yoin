package com.yoin.data.remote.datasource

import com.yoin.data.remote.dto.UserDto
import kotlinx.coroutines.flow.Flow

/**
 * 認証DataSource - Supabase Authとの通信を担当
 */
interface AuthDataSource {
    /**
     * 現在のユーザー情報を監視
     */
    fun getCurrentUser(): Flow<UserDto?>

    /**
     * ゲストユーザーとして匿名ログイン
     */
    suspend fun signInAnonymously(): Result<UserDto>

    /**
     * メールアドレスで新規登録
     */
    suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String
    ): Result<UserDto>

    /**
     * メールアドレスでログイン
     */
    suspend fun signInWithEmail(
        email: String,
        password: String
    ): Result<UserDto>

    /**
     * ログアウト
     */
    suspend fun signOut(): Result<Unit>

    /**
     * ユーザー情報を更新
     */
    suspend fun updateUser(
        userId: String,
        displayName: String? = null,
        avatarUrl: String? = null
    ): Result<UserDto>
}
