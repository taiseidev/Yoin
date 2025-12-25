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
 * 注文完了画面のViewModel
 *
 * 機能:
 * - 注文完了情報の表示
 * - 配送状況確認への遷移
 * - ホームへの遷移
 * - 注文履歴への遷移
 */
class OrderCompleteViewModel(
    orderId: String,
    productName: String,
    deliveryAddress: String,
    deliveryDateRange: String,
    email: String,
) : ScreenModel {
    private val _state = MutableStateFlow(
        OrderCompleteContract.State(
            orderId = orderId,
            productName = productName,
            deliveryAddress = deliveryAddress,
            deliveryDateRange = deliveryDateRange,
            email = email,
        )
    )
    val state: StateFlow<OrderCompleteContract.State> = _state.asStateFlow()

    private val _effect = Channel<OrderCompleteContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /**
     * Intentを処理
     */
    fun onIntent(intent: OrderCompleteContract.Intent) {
        when (intent) {
            is OrderCompleteContract.Intent.OnScreenDisplayed -> handleScreenDisplayed()
            is OrderCompleteContract.Intent.OnCheckDeliveryStatusPressed -> handleCheckDeliveryStatusPressed()
            is OrderCompleteContract.Intent.OnReturnHomePressed -> handleReturnHomePressed()
            is OrderCompleteContract.Intent.OnViewOrderHistoryPressed -> handleViewOrderHistoryPressed()
        }
    }

    /**
     * 画面表示時の処理
     */
    private fun handleScreenDisplayed() {
        _state.value = _state.value.copy(isLoading = false)
    }

    /**
     * 配送状況確認ボタンの処理
     */
    private fun handleCheckDeliveryStatusPressed() {
        screenModelScope.launch {
            _effect.send(
                OrderCompleteContract.Effect.NavigateToDeliveryTracking(_state.value.orderId)
            )
        }
    }

    /**
     * ホームに戻るボタンの処理
     */
    private fun handleReturnHomePressed() {
        screenModelScope.launch {
            _effect.send(OrderCompleteContract.Effect.NavigateToHome)
        }
    }

    /**
     * 注文履歴を見るボタンの処理
     */
    private fun handleViewOrderHistoryPressed() {
        screenModelScope.launch {
            _effect.send(OrderCompleteContract.Effect.NavigateToOrderHistory)
        }
    }
}
