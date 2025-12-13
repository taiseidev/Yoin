package com.yoin.feature.onboarding.di

import com.yoin.feature.onboarding.viewmodel.OnboardingViewModel
import com.yoin.feature.onboarding.viewmodel.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Onboarding機能のDIモジュール
 */
val onboardingModule = module {
    viewModel { SplashViewModel(get()) }
    viewModel { OnboardingViewModel() }
}
