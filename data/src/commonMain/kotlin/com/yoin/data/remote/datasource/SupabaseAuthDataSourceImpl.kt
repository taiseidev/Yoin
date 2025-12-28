package com.yoin.data.remote.datasource

import com.yoin.data.remote.dto.UserDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Supabase Auth DataSource実装
 */
class SupabaseAuthDataSourceImpl(
    private val supabaseClient: SupabaseClient
) : AuthDataSource {

    /**
     * 現在のユーザー情報を監視
     * Supabase Authのセッション状態を監視し、認証済みの場合はusersテーブルからユーザー情報を取得
     */
    override fun getCurrentUser(): Flow<UserDto?> =
        supabaseClient.auth.sessionStatus.map { status ->
            when (status) {
                is SessionStatus.Authenticated -> {
                    val userId = status.session.user?.id ?: return@map null
                    fetchUserFromDatabase(userId)
                }
                else -> null
            }
        }

    /**
     * ゲストユーザーとして匿名ログイン
     */
    override suspend fun signInAnonymously(): Result<UserDto> = runCatching {
        // 1. Supabase Authで匿名ログイン
        supabaseClient.auth.signInAnonymously()
        val userId = supabaseClient.auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("Anonymous sign-in failed: No user ID")

        // 2. usersテーブルにゲストユーザーを登録
        // NOTE: created_at/updated_atはSupabaseのデフォルト値(NOW())を使用
        val insertData = mapOf(
            "id" to userId,
            "display_name" to "ゲスト",
            "plan" to "GUEST",
            "is_guest" to true
        )

        supabaseClient.from("users")
            .insert(insertData)

        // 登録後のユーザー情報を取得
        fetchUserFromDatabase(userId)
            ?: throw IllegalStateException("Failed to create guest user")
    }

    /**
     * メールアドレスで新規登録
     */
    override suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String
    ): Result<UserDto> = runCatching {
        // 1. Supabase Authでメール登録
        supabaseClient.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }

        val userId = supabaseClient.auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("Sign-up failed: No user ID")

        // 2. usersテーブルにユーザーを登録
        // NOTE: created_at/updated_atはSupabaseのデフォルト値(NOW())を使用
        val insertData = mapOf(
            "id" to userId,
            "email" to email,
            "display_name" to displayName,
            "plan" to "FREE",
            "is_guest" to false
        )

        supabaseClient.from("users")
            .insert(insertData)

        // 登録後のユーザー情報を取得
        fetchUserFromDatabase(userId)
            ?: throw IllegalStateException("Failed to create user")
    }

    /**
     * メールアドレスでログイン
     */
    override suspend fun signInWithEmail(
        email: String,
        password: String
    ): Result<UserDto> = runCatching {
        // 1. Supabase Authでメールログイン
        supabaseClient.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }

        val userId = supabaseClient.auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("Sign-in failed: No user ID")

        // 2. usersテーブルからユーザー情報を取得
        fetchUserFromDatabase(userId)
            ?: throw IllegalStateException("User not found in database")
    }

    /**
     * ログアウト
     */
    override suspend fun signOut(): Result<Unit> = runCatching {
        supabaseClient.auth.signOut()
    }

    /**
     * ユーザー情報を更新
     */
    override suspend fun updateUser(
        userId: String,
        displayName: String?,
        avatarUrl: String?
    ): Result<UserDto> = runCatching {
        // 更新データを作成（nullでないフィールドのみ）
        // NOTE: updated_atはSupabaseのトリガーまたはデフォルト値で自動更新
        val updates = buildMap {
            displayName?.let { put("display_name", it) }
            avatarUrl?.let { put("avatar_url", it) }
        }

        // usersテーブルを更新
        supabaseClient.from("users")
            .update(updates) {
                filter {
                    eq("id", userId)
                }
            }

        // 更新後のユーザー情報を取得
        fetchUserFromDatabase(userId)
            ?: throw IllegalStateException("User not found after update")
    }

    /**
     * データベースからユーザー情報を取得
     */
    private suspend fun fetchUserFromDatabase(userId: String): UserDto? {
        return try {
            supabaseClient.from("users")
                .select() {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingle<UserDto>()
        } catch (e: Exception) {
            null
        }
    }
}
