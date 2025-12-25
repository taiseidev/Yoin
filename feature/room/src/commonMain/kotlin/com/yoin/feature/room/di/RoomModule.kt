package com.yoin.feature.room.di

import com.yoin.feature.room.viewmodel.JoinConfirmViewModel
import com.yoin.feature.room.viewmodel.QRScanViewModel
import com.yoin.feature.room.viewmodel.RoomCreateViewModel
import com.yoin.feature.room.viewmodel.RoomCreatedViewModel
import com.yoin.feature.room.viewmodel.RoomSettingsViewModel
import org.koin.dsl.module

/**
 * ルーム機能のDIモジュール
 */
val roomModule = module {
    factory { RoomCreateViewModel() }
    factory { RoomCreatedViewModel() }
    factory { JoinConfirmViewModel() }
    factory { QRScanViewModel() }
    factory { (roomId: String) -> RoomSettingsViewModel(roomId) }
}
