package com.yoin.feature.auth.di

import com.yoin.feature.auth.viewmodel.LoginViewModel
import org.koin.dsl.module

/**
 * Auth機能のDIモジュール
 */
val authModule = module {
    factory { LoginViewModel() }
}
