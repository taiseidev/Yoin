package com.yoin.feature.shop.viewmodel

/**
 * 注文確認画面のMVIコントラクト
 */
interface OrderConfirmationContract {

    /**
     * 画面の状態
     */
    data class State(
        val isLoading: Boolean = false,
        val productName: String = "",
        val productPrice: String = "",
        val shippingAddress: ShippingAddress = ShippingAddress(),
        val paymentMethod: String = "クレジットカード",
        val totalAmount: String = "",
    )

    /**
     * 配送先住所
     */
    data class ShippingAddress(
        val name: String = "",
        val postalCode: String = "",
        val address: String = "",
        val phoneNumber: String = ""
    )

    /**
     * ユーザーの意図（アクション）
     */
    sealed interface Intent {
        data object OnBackPressed : Intent
        data object OnPlaceOrderPressed : Intent
    }

    /**
     * 副作用（ナビゲーションなど）
     */
    sealed interface Effect {
        data object NavigateBack : Effect
        data class NavigateToOrderComplete(
            val orderId: String,
            val productName: String,
            val deliveryAddress: String,
            val deliveryDateRange: String,
            val email: String
        ) : Effect
        data class ShowError(val message: String) : Effect
    }
}
