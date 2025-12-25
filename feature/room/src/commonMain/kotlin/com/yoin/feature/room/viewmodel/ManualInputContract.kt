package com.yoin.feature.room.viewmodel

/**
 * 手動入力画面のMVIコントラクト
 */
interface ManualInputContract {

    /**
     * 画面の状態
     */
    data class State(
        val roomCode: String = "",
        val isLoading: Boolean = false,
        val isCodeValid: Boolean = true,
        val errorMessage: String? = null,
    )

    /**
     * ユーザーの意図（アクション）
     */
    sealed interface Intent {
        data class OnRoomCodeChanged(val code: String) : Intent
        data object OnJoinPressed : Intent
        data object OnCancelPressed : Intent
    }

    /**
     * 副作用（ナビゲーションなど）
     */
    sealed interface Effect {
        data object NavigateBack : Effect
        data class NavigateToJoinConfirm(val roomId: String) : Effect
        data class ShowError(val message: String) : Effect
    }
}
