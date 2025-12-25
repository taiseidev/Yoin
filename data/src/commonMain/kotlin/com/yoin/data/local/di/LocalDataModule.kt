package com.yoin.data.local.di

import com.yoin.data.local.repository.AppInitializationRepositoryImpl
import com.yoin.domain.common.repository.AppInitializationRepository
import com.yoin.domain.common.usecase.InitializeAppUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * ローカルデータ層のDIモジュール
 */
val localDataModule = module {
    // Repository
    singleOf(::AppInitializationRepositoryImpl) bind AppInitializationRepository::class

    // UseCase
    singleOf(::InitializeAppUseCase)
}
