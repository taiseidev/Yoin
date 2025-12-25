package com.yoin.feature.notifications.di

import com.yoin.feature.notifications.viewmodel.NotificationViewModel
import org.koin.dsl.module

/**
 * Notification機能のDIモジュール
 */
val notificationModule = module {
    factory { NotificationViewModel() }
}
