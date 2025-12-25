package com.yoin.feature.shop.viewmodel

/**
 * 注文詳細画面のMVIコントラクト
 */
interface OrderDetailContract {

    /**
     * 画面の状態
     */
    data class State(
        val isLoading: Boolean = false,
        val orderId: String = "",
        val orderDate: String = "",
        val orderStatus: String = "",
        val productName: String = "",
        val productPrice: String = "",
        val productImage: String = "",
        val quantity: Int = 1,
        val shippingAddress: ShippingAddress = ShippingAddress(),
        val paymentMethod: String = "",
        val subtotal: String = "",
        val shippingFee: String = "",
        val totalAmount: String = "",
        val trackingNumber: String = "",
        val estimatedDeliveryDate: String = ""
    )

    /**
     * 配送先情報
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
        data object OnTrackDeliveryPressed : Intent
        data object OnContactSupportPressed : Intent
        data object OnReorderPressed : Intent
    }

    /**
     * 副作用（ナビゲーションなど）
     */
    sealed interface Effect {
        data object NavigateBack : Effect
        data class NavigateToDeliveryTracking(val orderId: String) : Effect
        data class NavigateToContactSupport(val orderId: String) : Effect
        data class NavigateToShopOrder(val productId: String) : Effect
        data class ShowError(val message: String) : Effect
    }
}
