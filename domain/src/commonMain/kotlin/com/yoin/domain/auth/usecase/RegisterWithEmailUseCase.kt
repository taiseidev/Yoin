package com.yoin.domain.auth.usecase

import com.yoin.domain.auth.model.AuthResult
import com.yoin.domain.auth.repository.AuthRepository

/**
 * メールアドレスで登録するUseCase
 */
class RegisterWithEmailUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        displayName: String
    ): AuthResult {
        // バリデーション
        if (email.isBlank() || !isValidEmail(email)) {
            return AuthResult.Error("有効なメールアドレスを入力してください")
        }

        if (password.length < 8) {
            return AuthResult.Error("パスワードは8文字以上で入力してください")
        }

        if (displayName.isBlank()) {
            return AuthResult.Error("表示名を入力してください")
        }

        // TODO: データソース実装後にダミーデータを削除
        return authRepository.registerWithEmail(email, password, displayName)
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }
}
