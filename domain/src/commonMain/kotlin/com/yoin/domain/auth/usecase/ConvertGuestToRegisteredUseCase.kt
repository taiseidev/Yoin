package com.yoin.domain.auth.usecase

import com.yoin.domain.auth.model.AuthResult
import com.yoin.domain.auth.repository.AuthRepository

/**
 * ゲストから登録ユーザーに変換するUseCase
 */
class ConvertGuestToRegisteredUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AuthResult {
        if (email.isBlank() || password.isBlank()) {
            return AuthResult.Error("メールアドレスとパスワードを入力してください")
        }

        if (password.length < 8) {
            return AuthResult.Error("パスワードは8文字以上で入力してください")
        }

        // TODO: データソース実装後にダミーデータを削除
        return authRepository.convertGuestToRegistered(email, password)
    }
}
