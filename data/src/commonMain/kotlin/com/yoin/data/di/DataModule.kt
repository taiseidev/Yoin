package com.yoin.data.di

import com.yoin.data.remote.datasource.AuthDataSource
import com.yoin.data.remote.datasource.SupabaseAuthDataSourceImpl
import com.yoin.data.repository.AppInitializationRepositoryImpl
import com.yoin.data.repository.AuthRepositoryImpl
import com.yoin.data.repository.RoomRepositoryImpl
import com.yoin.domain.auth.repository.AuthRepository
import com.yoin.domain.common.repository.AppInitializationRepository
import com.yoin.domain.room.repository.RoomRepository
import org.koin.dsl.module

/**
 * データ層のDIモジュール
 *
 * DataSourceとRepositoryの実装を提供
 */
val dataModule = module {
    // DataSource
    single<AuthDataSource> {
        SupabaseAuthDataSourceImpl(get())
    }

    // Repository
    single<AuthRepository> {
        AuthRepositoryImpl(get())
    }

    single<AppInitializationRepository> {
        AppInitializationRepositoryImpl(get())
    }

    single<RoomRepository> {
        RoomRepositoryImpl()
    }
}
