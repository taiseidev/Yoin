package com.yoin.feature.map.di

import com.yoin.feature.map.viewmodel.MapFullscreenViewModel
import org.koin.dsl.module

/**
 * 地図機能のDIモジュール
 */
val mapModule = module {
    factory { (roomId: String) -> MapFullscreenViewModel(roomId) }
}
