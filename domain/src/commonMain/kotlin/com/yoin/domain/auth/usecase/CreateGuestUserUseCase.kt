package com.yoin.domain.auth.usecase

import com.yoin.domain.auth.model.AuthResult
import com.yoin.domain.auth.repository.AuthRepository

/**
 * ゲストユーザーを作成するUseCase
 */
class CreateGuestUserUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(displayName: String): AuthResult {
        // TODO: データソース実装後にダミーデータを削除
        return authRepository.createGuestUser(displayName)
    }
}
