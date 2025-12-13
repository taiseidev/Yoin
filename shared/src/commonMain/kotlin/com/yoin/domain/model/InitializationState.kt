package com.yoin.domain.model

/**
 * アプリ初期化の状態を表すモデル
 */
sealed class InitializationState {
    /**
     * 初期化開始前
     */
    data object NotStarted : InitializationState()

    /**
     * 初期化中
     * @param progress 進捗率（0.0～1.0）
     */
    data class Initializing(val progress: Float = 0f) : InitializationState()

    /**
     * 初期化完了
     */
    data object Completed : InitializationState()

    /**
     * 初期化失敗
     * @param error エラーメッセージ
     */
    data class Failed(val error: String) : InitializationState()
}
