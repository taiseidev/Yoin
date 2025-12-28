package com.yoin.domain.auth.usecase

import com.yoin.domain.auth.model.User
import com.yoin.domain.auth.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

/**
 * 現在のユーザーを取得するUseCase
 */
class GetCurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<User?> {
        // TODO: データソース実装後にダミーデータを削除
        return authRepository.getCurrentUser()
    }
}
