package com.yoin.feature.shop.viewmodel

/**
 * 注文完了画面のMVIコントラクト
 *
 * 機能:
 * - 注文完了情報の表示
 * - 配送状況確認への遷移
 * - ホームへの遷移
 * - 注文履歴への遷移
 */
interface OrderCompleteContract {
    /**
     * 画面の状態
     */
    data class State(
        val isLoading: Boolean = false,
        val orderId: String = "",
        val productName: String = "",
        val deliveryAddress: String = "",
        val deliveryDateRange: String = "",
        val email: String = "",
    )

    /**
     * ユーザーの操作
     */
    sealed interface Intent {
        /**
         * 画面が表示された
         */
        data object OnScreenDisplayed : Intent

        /**
         * 配送状況を確認ボタンがタップされた
         */
        data object OnCheckDeliveryStatusPressed : Intent

        /**
         * ホームに戻るボタンがタップされた
         */
        data object OnReturnHomePressed : Intent

        /**
         * 注文履歴を見るボタンがタップされた
         */
        data object OnViewOrderHistoryPressed : Intent
    }

    /**
     * 副作用
     */
    sealed interface Effect {
        /**
         * 配送状況確認画面に遷移
         */
        data class NavigateToDeliveryTracking(val orderId: String) : Effect

        /**
         * ホームに遷移
         */
        data object NavigateToHome : Effect

        /**
         * 注文履歴に遷移
         */
        data object NavigateToOrderHistory : Effect

        /**
         * エラーメッセージを表示
         */
        data class ShowError(val message: String) : Effect
    }
}
