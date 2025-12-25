package com.yoin.feature.settings.di

import com.yoin.feature.settings.viewmodel.ChangePasswordViewModel
import com.yoin.feature.settings.viewmodel.ContactFormViewModel
import com.yoin.feature.settings.viewmodel.HelpFaqViewModel
import com.yoin.feature.settings.viewmodel.NotificationSettingsViewModel
import com.yoin.feature.settings.viewmodel.PremiumPlanViewModel
import com.yoin.feature.settings.viewmodel.SettingsViewModel
import org.koin.dsl.module

/**
 * 設定機能のDIモジュール
 */
val settingsModule = module {
    factory { SettingsViewModel() }
    factory { NotificationSettingsViewModel() }
    factory { PremiumPlanViewModel() }
    factory { HelpFaqViewModel() }
    factory { ContactFormViewModel() }
    factory { ChangePasswordViewModel() }
}
