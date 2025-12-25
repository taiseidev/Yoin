package com.yoin.feature.shop.viewmodel

/**
 * 注文履歴画面のMVIコントラクト
 *
 * 機能:
 * - 注文履歴の一覧表示
 * - 注文の詳細確認
 * - お問い合わせへの遷移
 */
interface OrderHistoryContract {
    /**
     * 画面の状態
     */
    data class State(
        val isLoading: Boolean = false,
        val orders: List<Order> = emptyList(),
    )

    /**
     * 注文データモデル
     */
    data class Order(
        val orderId: String,
        val productName: String,
        val productIcon: String,
        val tripName: String,
        val orderDate: String,
        val status: OrderStatus,
        val deliveryInfo: String,
        val price: String,
    )

    /**
     * 注文ステータス
     */
    enum class OrderStatus {
        SHIPPING,   // 配送中
        DELIVERED,  // 配送済み
    }

    /**
     * ユーザーの操作
     */
    sealed interface Intent {
        /**
         * 画面が表示された
         */
        data object OnScreenDisplayed : Intent

        /**
         * 戻るボタンがタップされた
         */
        data object OnBackPressed : Intent

        /**
         * 注文がタップされた
         */
        data class OnOrderItemClicked(val orderId: String) : Intent

        /**
         * お問い合わせがタップされた
         */
        data object OnContactSupportPressed : Intent
    }

    /**
     * 副作用
     */
    sealed interface Effect {
        /**
         * 前の画面に戻る
         */
        data object NavigateBack : Effect

        /**
         * 注文詳細画面に遷移
         */
        data class NavigateToOrderDetail(val orderId: String) : Effect

        /**
         * お問い合わせ画面に遷移
         */
        data object NavigateToContactSupport : Effect

        /**
         * エラーメッセージを表示
         */
        data class ShowError(val message: String) : Effect
    }
}
