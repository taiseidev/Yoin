package com.yoin.feature.shop.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 注文詳細画面のScreenModel
 *
 * @param orderId 注文ID
 */
class OrderDetailViewModel(
    private val orderId: String
) : ScreenModel {

    private val _state = MutableStateFlow(OrderDetailContract.State())
    val state: StateFlow<OrderDetailContract.State> = _state.asStateFlow()

    private val _effect = Channel<OrderDetailContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadOrderDetail()
    }

    /**
     * ユーザーの意図を処理
     */
    fun handleIntent(intent: OrderDetailContract.Intent) {
        when (intent) {
            is OrderDetailContract.Intent.OnBackPressed -> onBackPressed()
            is OrderDetailContract.Intent.OnTrackDeliveryPressed -> onTrackDeliveryPressed()
            is OrderDetailContract.Intent.OnContactSupportPressed -> onContactSupportPressed()
            is OrderDetailContract.Intent.OnReorderPressed -> onReorderPressed()
        }
    }

    private fun loadOrderDetail() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // TODO: 実際の注文詳細API呼び出し
            kotlinx.coroutines.delay(500) // ネットワーク遅延をシミュレート

            val shippingAddress = OrderDetailContract.ShippingAddress(
                name = "山田 太郎",
                postalCode = "123-4567",
                address = "東京都渋谷区1-2-3",
                phoneNumber = "090-1234-5678"
            )

            _state.update {
                it.copy(
                    isLoading = false,
                    orderId = orderId,
                    orderDate = "2024-01-15",
                    orderStatus = "配送中",
                    productName = "フォトアルバム",
                    productPrice = "¥3,980",
                    productImage = "",
                    quantity = 1,
                    shippingAddress = shippingAddress,
                    paymentMethod = "クレジットカード",
                    subtotal = "¥3,980",
                    shippingFee = "¥500",
                    totalAmount = "¥4,480",
                    trackingNumber = "1234567890",
                    estimatedDeliveryDate = "2024-01-18"
                )
            }
        }
    }

    private fun onBackPressed() {
        screenModelScope.launch {
            _effect.send(OrderDetailContract.Effect.NavigateBack)
        }
    }

    private fun onTrackDeliveryPressed() {
        screenModelScope.launch {
            _effect.send(OrderDetailContract.Effect.NavigateToDeliveryTracking(orderId))
        }
    }

    private fun onContactSupportPressed() {
        screenModelScope.launch {
            _effect.send(OrderDetailContract.Effect.NavigateToContactSupport(orderId))
        }
    }

    private fun onReorderPressed() {
        screenModelScope.launch {
            // TODO: 実際の商品IDを使用
            _effect.send(OrderDetailContract.Effect.NavigateToShopOrder("product-001"))
        }
    }
}
