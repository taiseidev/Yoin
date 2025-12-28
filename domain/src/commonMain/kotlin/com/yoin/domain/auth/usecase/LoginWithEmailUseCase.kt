package com.yoin.domain.auth.usecase

import com.yoin.domain.auth.model.AuthResult
import com.yoin.domain.auth.repository.AuthRepository

/**
 * メールアドレスでログインするUseCase
 */
class LoginWithEmailUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AuthResult {
        if (email.isBlank() || password.isBlank()) {
            return AuthResult.Error("メールアドレスとパスワードを入力してください")
        }

        // TODO: データソース実装後にダミーデータを削除
        return authRepository.loginWithEmail(email, password)
    }
}
