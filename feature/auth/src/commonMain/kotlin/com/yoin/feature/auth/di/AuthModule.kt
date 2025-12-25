package com.yoin.feature.auth.di

import com.yoin.feature.auth.viewmodel.LoginViewModel
import com.yoin.feature.auth.viewmodel.PasswordResetViewModel
import com.yoin.feature.auth.viewmodel.RegisterViewModel
import org.koin.dsl.module

/**
 * Auth機能のDIモジュール
 */
val authModule = module {
    factory { LoginViewModel() }
    factory { RegisterViewModel() }
    factory { PasswordResetViewModel() }
}
