package com.yoin.feature.profile.di

import com.yoin.feature.profile.viewmodel.ProfileEditViewModel
import org.koin.dsl.module

/**
 * プロフィール機能のDIモジュール
 */
val profileModule = module {
    factory { ProfileEditViewModel() }
}
