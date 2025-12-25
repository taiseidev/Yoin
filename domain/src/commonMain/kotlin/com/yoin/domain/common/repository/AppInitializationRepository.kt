package com.yoin.domain.common.repository

import com.yoin.domain.common.model.InitializationState
import kotlinx.coroutines.flow.Flow

/**
 * アプリ初期化を管理するRepositoryインターフェース
 */
interface AppInitializationRepository {
    /**
     * 初期化状態を監視する
     * @return 初期化状態のFlow
     */
    fun observeInitializationState(): Flow<InitializationState>

    /**
     * アプリの初期化を実行する
     */
    suspend fun initialize()
}
