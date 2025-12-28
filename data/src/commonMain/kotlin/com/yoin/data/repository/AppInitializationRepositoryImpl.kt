package com.yoin.data.repository

import com.yoin.domain.auth.repository.AuthRepository
import com.yoin.domain.common.model.InitializationState
import com.yoin.domain.common.repository.AppInitializationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

/**
 * アプリ初期化Repository実装
 */
class AppInitializationRepositoryImpl(
    private val authRepository: AuthRepository
) : AppInitializationRepository {

    private val _initializationState = MutableStateFlow<InitializationState>(
        InitializationState.NotStarted
    )

    /**
     * 初期化状態を監視する
     */
    override fun observeInitializationState(): Flow<InitializationState> {
        return _initializationState.asStateFlow()
    }

    /**
     * アプリの初期化を実行する
     *
     * 実行内容:
     * 1. 認証状態の確認（ログイン済みかゲストか未認証か）
     * 2. 必要に応じてローカルデータベースの準備
     * 3. その他の初期設定
     */
    override suspend fun initialize() {
        try {
            _initializationState.value = InitializationState.Initializing(progress = 0.0f)

            // 1. 認証状態の確認 (30%)
            val currentUser = authRepository.getCurrentUser().first()
            _initializationState.value = InitializationState.Initializing(progress = 0.3f)

            // 2. 必要に応じて追加の初期化処理
            // - ローカルデータベースのセットアップ
            // - キャッシュの準備
            // - リモート設定の取得 など
            _initializationState.value = InitializationState.Initializing(progress = 0.6f)

            // 3. 初期化完了
            _initializationState.value = InitializationState.Initializing(progress = 1.0f)
            _initializationState.value = InitializationState.Completed

        } catch (e: Exception) {
            _initializationState.value = InitializationState.Failed(
                error = e.message ?: "初期化に失敗しました"
            )
        }
    }
}
