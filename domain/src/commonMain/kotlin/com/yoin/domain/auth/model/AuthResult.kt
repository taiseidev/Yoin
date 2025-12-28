package com.yoin.domain.auth.model

/**
 * 認証結果
 */
sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
}
