package com.yoin.domain.auth.repository

import com.yoin.domain.auth.model.AuthResult
import com.yoin.domain.auth.model.User
import kotlinx.coroutines.flow.Flow

/**
 * 認証リポジトリ
 */
interface AuthRepository {
    /**
     * 現在のユーザーを取得
     */
    fun getCurrentUser(): Flow<User?>

    /**
     * ゲストユーザーとして作成
     */
    suspend fun createGuestUser(displayName: String): AuthResult

    /**
     * メールアドレスで登録
     */
    suspend fun registerWithEmail(
        email: String,
        password: String,
        displayName: String
    ): AuthResult

    /**
     * メールアドレスでログイン
     */
    suspend fun loginWithEmail(
        email: String,
        password: String
    ): AuthResult

    /**
     * ログアウト
     */
    suspend fun logout(): Result<Unit>

    /**
     * ゲストから登録ユーザーに変換
     */
    suspend fun convertGuestToRegistered(
        email: String,
        password: String
    ): AuthResult

    /**
     * ユーザー情報を更新
     */
    suspend fun updateUser(
        displayName: String? = null,
        avatarUrl: String? = null
    ): Result<User>
}
