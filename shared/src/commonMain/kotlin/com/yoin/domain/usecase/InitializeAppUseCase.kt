package com.yoin.domain.usecase

import com.yoin.domain.model.InitializationState
import com.yoin.domain.repository.AppInitializationRepository
import kotlinx.coroutines.flow.Flow

/**
 * アプリ初期化を実行するUseCase
 */
class InitializeAppUseCase(
    private val repository: AppInitializationRepository
) {
    /**
     * 初期化状態を監視する
     */
    fun observeState(): Flow<InitializationState> {
        return repository.observeInitializationState()
    }

    /**
     * 初期化を実行する
     */
    suspend operator fun invoke() {
        repository.initialize()
    }
}
