package com.yoin.feature.home.di

import com.yoin.feature.home.viewmodel.HomeViewModel
import com.yoin.feature.home.viewmodel.TripDetailViewModel
import org.koin.dsl.module

/**
 * ホーム機能のDIモジュール
 */
val homeModule = module {
    factory { HomeViewModel() }
    factory { TripDetailViewModel() }
}
