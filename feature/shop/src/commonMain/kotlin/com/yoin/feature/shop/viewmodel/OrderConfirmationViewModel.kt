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
 * 注文確認画面のScreenModel
 *
 * @param lastName 姓
 * @param firstName 名
 * @param postalCode 郵便番号
 * @param prefecture 都道府県
 * @param city 市区町村
 * @param addressLine 番地・建物名
 * @param phoneNumber 電話番号
 */
class OrderConfirmationViewModel(
    private val lastName: String,
    private val firstName: String,
    private val postalCode: String,
    private val prefecture: String,
    private val city: String,
    private val addressLine: String,
    private val phoneNumber: String
) : ScreenModel {

    private val _state = MutableStateFlow(OrderConfirmationContract.State())
    val state: StateFlow<OrderConfirmationContract.State> = _state.asStateFlow()

    private val _effect = Channel<OrderConfirmationContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadOrderData()
    }

    /**
     * ユーザーの意図を処理
     */
    fun handleIntent(intent: OrderConfirmationContract.Intent) {
        when (intent) {
            is OrderConfirmationContract.Intent.OnBackPressed -> onBackPressed()
            is OrderConfirmationContract.Intent.OnPlaceOrderPressed -> onPlaceOrderPressed()
        }
    }

    private fun loadOrderData() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // TODO: 実際の商品データ取得処理を実装
            kotlinx.coroutines.delay(300) // ネットワーク遅延をシミュレート

            val shippingAddress = OrderConfirmationContract.ShippingAddress(
                name = "$lastName $firstName",
                postalCode = postalCode,
                address = "$prefecture $city $addressLine",
                phoneNumber = phoneNumber
            )

            _state.update {
                it.copy(
                    isLoading = false,
                    productName = "フォトアルバム",
                    productPrice = "¥3,980",
                    shippingAddress = shippingAddress,
                    totalAmount = "¥4,480" // 商品代金 + 送料
                )
            }
        }
    }

    private fun onBackPressed() {
        screenModelScope.launch {
            _effect.send(OrderConfirmationContract.Effect.NavigateBack)
        }
    }

    private fun onPlaceOrderPressed() {
        screenModelScope.launch {
            val currentState = _state.value
            _state.update { it.copy(isLoading = true) }

            try {
                // TODO: 実際の注文API呼び出し
                kotlinx.coroutines.delay(1500) // ネットワーク遅延をシミュレート

                // 注文IDを生成（本来はAPIから取得）
                val orderId = "YN${System.currentTimeMillis() % 1000000}"

                _state.update { it.copy(isLoading = false) }

                // 注文完了画面に遷移
                _effect.send(
                    OrderConfirmationContract.Effect.NavigateToOrderComplete(
                        orderId = orderId,
                        productName = currentState.productName,
                        deliveryAddress = "${currentState.shippingAddress.postalCode} ${currentState.shippingAddress.address}",
                        deliveryDateRange = "3-5営業日",
                        email = "user@example.com" // TODO: 実際のユーザーメールアドレスを取得
                    )
                )
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _effect.send(OrderConfirmationContract.Effect.ShowError("注文の確定に失敗しました"))
            }
        }
    }
}
