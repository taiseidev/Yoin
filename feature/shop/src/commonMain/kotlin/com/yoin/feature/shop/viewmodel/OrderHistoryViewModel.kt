package com.yoin.feature.shop.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * 注文履歴画面のViewModel
 *
 * 機能:
 * - 注文履歴の取得と管理
 * - 注文詳細への遷移
 * - お問い合わせへの遷移
 */
class OrderHistoryViewModel : ScreenModel {
    private val _state = MutableStateFlow(
        OrderHistoryContract.State(
            orders = getDefaultOrders()
        )
    )
    val state: StateFlow<OrderHistoryContract.State> = _state.asStateFlow()

    private val _effect = Channel<OrderHistoryContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /**
     * Intentを処理
     */
    fun onIntent(intent: OrderHistoryContract.Intent) {
        when (intent) {
            is OrderHistoryContract.Intent.OnScreenDisplayed -> handleScreenDisplayed()
            is OrderHistoryContract.Intent.OnBackPressed -> handleBackPressed()
            is OrderHistoryContract.Intent.OnOrderItemClicked -> handleOrderItemClicked(intent.orderId)
            is OrderHistoryContract.Intent.OnContactSupportPressed -> handleContactSupportPressed()
        }
    }

    /**
     * 画面表示時の処理
     */
    private fun handleScreenDisplayed() {
        _state.value = _state.value.copy(isLoading = false)
    }

    /**
     * 戻るボタンの処理
     */
    private fun handleBackPressed() {
        screenModelScope.launch {
            _effect.send(OrderHistoryContract.Effect.NavigateBack)
        }
    }

    /**
     * 注文アイテムクリックの処理
     */
    private fun handleOrderItemClicked(orderId: String) {
        screenModelScope.launch {
            _effect.send(OrderHistoryContract.Effect.NavigateToOrderDetail(orderId))
        }
    }

    /**
     * お問い合わせボタンの処理
     */
    private fun handleContactSupportPressed() {
        screenModelScope.launch {
            _effect.send(OrderHistoryContract.Effect.NavigateToContactSupport)
        }
    }

    /**
     * デフォルトの注文データを取得
     * TODO: 実際のAPIから取得
     */
    private fun getDefaultOrders(): List<OrderHistoryContract.Order> {
        return listOf(
            OrderHistoryContract.Order(
                orderId = "TF-20250704-1234",
                productName = "フォトブック A5",
                productIcon = "📖",
                tripName = "北海道旅行2025",
                orderDate = "注文日: 2025/7/4",
                status = OrderHistoryContract.OrderStatus.SHIPPING,
                deliveryInfo = "7/10 到着予定",
                price = "¥2,000"
            ),
            OrderHistoryContract.Order(
                orderId = "TF-20250510-5678",
                productName = "ポストカード 10枚",
                productIcon = "📮",
                tripName = "沖縄旅行2025",
                orderDate = "注文日: 2025/5/10",
                status = OrderHistoryContract.OrderStatus.DELIVERED,
                deliveryInfo = "5/15 配達完了",
                price = "¥800"
            ),
            OrderHistoryContract.Order(
                orderId = "TF-20250420-9012",
                productName = "フォトブック A4",
                productIcon = "📖",
                tripName = "東京観光2025",
                orderDate = "注文日: 2025/4/20",
                status = OrderHistoryContract.OrderStatus.DELIVERED,
                deliveryInfo = "4/25 配達完了",
                price = "¥2,500"
            )
        )
    }
}
