package com.yoin.domain.di

import com.yoin.domain.common.usecase.InitializeAppUseCase
import com.yoin.domain.room.usecase.CreateRoomUseCase
import org.koin.dsl.module

/**
 * ドメイン層のDIモジュール
 *
 * UseCaseの提供
 */
val domainModule = module {
    // Common UseCases
    factory {
        InitializeAppUseCase(get())
    }

    // Room UseCases
    factory {
        CreateRoomUseCase(get())
    }
}
