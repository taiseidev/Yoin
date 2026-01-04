package com.yoin.feature.room.di

import com.yoin.feature.room.viewmodel.JoinConfirmViewModel
import com.yoin.feature.room.viewmodel.ManualInputViewModel
import com.yoin.feature.room.viewmodel.MemberListViewModel
import com.yoin.feature.room.viewmodel.QRScanViewModel
import com.yoin.feature.room.viewmodel.RoomCreateViewModel
import com.yoin.feature.room.viewmodel.RoomCreatedViewModel
import com.yoin.feature.room.viewmodel.RoomDetailBeforeViewModel
import com.yoin.feature.room.viewmodel.RoomSettingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

/**
 * ルーム機能のDIモジュール
 */
val roomModule = module {
    factoryOf(::RoomCreateViewModel)
    factory { RoomCreatedViewModel() }
    factory { JoinConfirmViewModel() }
    factory { QRScanViewModel() }
    factory { ManualInputViewModel() }
    factory { (roomId: String) -> RoomSettingsViewModel(roomId) }
    factory { (roomId: String) -> MemberListViewModel(roomId) }
    factory { (roomId: String) -> RoomDetailBeforeViewModel(roomId) }
}
