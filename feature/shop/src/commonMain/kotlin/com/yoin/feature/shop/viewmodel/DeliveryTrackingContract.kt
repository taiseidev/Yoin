package com.yoin.feature.shop.viewmodel

/**
 * 配送追跡画面のMVIコントラクト
 */
interface DeliveryTrackingContract {

    /**
     * 画面の状態
     */
    data class State(
        val isLoading: Boolean = false,
        val orderId: String = "",
        val trackingNumber: String = "",
        val currentStatus: String = "",
        val deliverySteps: List<DeliveryStep> = emptyList(),
        val estimatedDeliveryDate: String = ""
    )

    /**
     * 配送ステップ
     */
    data class DeliveryStep(
        val title: String,
        val description: String,
        val timestamp: String,
        val isCompleted: Boolean
    )

    /**
     * ユーザーの意図（アクション）
     */
    sealed interface Intent {
        data object OnBackPressed : Intent
        data object OnRefresh : Intent
    }

    /**
     * 副作用（ナビゲーションなど）
     */
    sealed interface Effect {
        data object NavigateBack : Effect
        data class ShowError(val message: String) : Effect
    }
}
