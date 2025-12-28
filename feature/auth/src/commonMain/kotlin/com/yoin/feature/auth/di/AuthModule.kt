package com.yoin.feature.auth.di

import com.yoin.feature.auth.viewmodel.*
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

/**
 * Auth機能のDIモジュール
 */
val authModule = module {
    factory { WelcomeViewModel() }
    factoryOf(::LoginViewModel)
    factory { RegisterMethodViewModel() }
    factoryOf(::RegisterViewModel)
    factory { PasswordResetViewModel() }
    // Note: RegisterPasswordViewModel is created with parameters at navigation layer
}
