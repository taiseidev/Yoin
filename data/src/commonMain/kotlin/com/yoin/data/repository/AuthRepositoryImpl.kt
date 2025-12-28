package com.yoin.data.repository

import com.yoin.data.remote.datasource.AuthDataSource
import com.yoin.data.remote.dto.toDomain
import com.yoin.domain.auth.model.AuthResult
import com.yoin.domain.auth.model.User
import com.yoin.domain.auth.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 認証リポジトリ実装
 */
class AuthRepositoryImpl(
    private val authDataSource: AuthDataSource
) : AuthRepository {

    /**
     * 現在のユーザーを取得
     */
    override fun getCurrentUser(): Flow<User?> =
        authDataSource.getCurrentUser()
            .map { it?.toDomain() }

    /**
     * ゲストユーザーとして作成
     */
    override suspend fun createGuestUser(displayName: String): AuthResult {
        return authDataSource.signInAnonymously()
            .fold(
                onSuccess = { userDto ->
                    AuthResult.Success(userDto.toDomain())
                },
                onFailure = { error ->
                    AuthResult.Error(error.message ?: "ゲストユーザーの作成に失敗しました")
                }
            )
    }

    /**
     * メールアドレスで登録
     */
    override suspend fun registerWithEmail(
        email: String,
        password: String,
        displayName: String
    ): AuthResult {
        return authDataSource.signUpWithEmail(
            email = email,
            password = password,
            displayName = displayName
        ).fold(
            onSuccess = { userDto ->
                AuthResult.Success(userDto.toDomain())
            },
            onFailure = { error ->
                AuthResult.Error(error.message ?: "メールアドレスでの登録に失敗しました")
            }
        )
    }

    /**
     * メールアドレスでログイン
     */
    override suspend fun loginWithEmail(
        email: String,
        password: String
    ): AuthResult {
        return authDataSource.signInWithEmail(
            email = email,
            password = password
        ).fold(
            onSuccess = { userDto ->
                AuthResult.Success(userDto.toDomain())
            },
            onFailure = { error ->
                AuthResult.Error(error.message ?: "ログインに失敗しました")
            }
        )
    }

    /**
     * ログアウト
     */
    override suspend fun logout(): Result<Unit> {
        return authDataSource.signOut()
    }

    /**
     * ゲストから登録ユーザーに変換
     *
     * TODO: ゲスト変換機能の実装
     * 現在は簡易実装として、新規メールアドレス登録として処理
     */
    override suspend fun convertGuestToRegistered(
        email: String,
        password: String
    ): AuthResult {
        // TODO: 実際にはゲストユーザーのデータを引き継ぐ必要がある
        // 現在の実装では、新規ユーザーとして登録される
        return registerWithEmail(
            email = email,
            password = password,
            displayName = "ユーザー" // TODO: 現在のゲストユーザーのdisplayNameを使用
        )
    }

    /**
     * ユーザー情報を更新
     */
    override suspend fun updateUser(
        displayName: String?,
        avatarUrl: String?
    ): Result<User> {
        // TODO: 現在のユーザーIDを取得する方法が必要
        // 暫定実装として、エラーを返す
        return Result.failure(
            IllegalStateException("updateUser: 現在のユーザーIDの取得機能が未実装です")
        )
    }
}
