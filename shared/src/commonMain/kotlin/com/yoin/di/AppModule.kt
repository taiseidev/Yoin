package com.yoin.di

import com.yoin.data.repository.AppInitializationRepositoryImpl
import com.yoin.domain.repository.AppInitializationRepository
import com.yoin.domain.usecase.InitializeAppUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * アプリケーション全体のDIモジュール
 */
val appModule = module {
    // Repository
    singleOf(::AppInitializationRepositoryImpl) bind AppInitializationRepository::class

    // UseCase
    singleOf(::InitializeAppUseCase)
}
