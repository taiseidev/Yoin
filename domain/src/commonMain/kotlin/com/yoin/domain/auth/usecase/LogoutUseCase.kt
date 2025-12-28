package com.yoin.domain.auth.usecase

import com.yoin.domain.auth.repository.AuthRepository

/**
 * ログアウトするUseCase
 */
class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        // TODO: データソース実装後にダミーデータを削除
        return authRepository.logout()
    }
}
