package com.taiseidev.yoin.di

import com.taiseidev.yoin.ui.screens.splash.SplashViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * ViewModelのDIモジュール
 */
val viewModelModule = module {
    viewModelOf(::SplashViewModel)
}
