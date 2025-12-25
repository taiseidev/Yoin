package com.yoin.feature.shop.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Shop注文画面のScreenModel
 *
 * 責務:
 * - 商品詳細情報の管理
 * - 数量と価格の計算
 * - 配送先情報のバリデーション
 * - 注文処理
 */
class ShopOrderViewModel : ScreenModel {

    private val _state = MutableStateFlow(ShopOrderContract.State())
    val state: StateFlow<ShopOrderContract.State> = _state.asStateFlow()

    private val _effect = Channel<ShopOrderContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /**
     * ユーザーの意図を処理
     */
    fun onIntent(intent: ShopOrderContract.Intent) {
        when (intent) {
            is ShopOrderContract.Intent.OnScreenDisplayed -> onScreenDisplayed(intent.productId, intent.tripId)
            is ShopOrderContract.Intent.OnTripSelected -> onTripSelected(intent.tripId)
            is ShopOrderContract.Intent.OnQuantityChanged -> onQuantityChanged(intent.quantity)
            is ShopOrderContract.Intent.OnNameChanged -> onNameChanged(intent.name)
            is ShopOrderContract.Intent.OnPostalCodeChanged -> onPostalCodeChanged(intent.postalCode)
            is ShopOrderContract.Intent.OnAddressChanged -> onAddressChanged(intent.address)
            is ShopOrderContract.Intent.OnPhoneNumberChanged -> onPhoneNumberChanged(intent.phoneNumber)
            is ShopOrderContract.Intent.OnOrderPressed -> onOrderPressed()
            is ShopOrderContract.Intent.OnBackPressed -> onBackPressed()
        }
    }

    private fun onScreenDisplayed(productId: String, tripId: String?) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // TODO: 実際の商品情報取得APIを実装
                delay(500)

                // サンプルデータ
                val product = when (productId) {
                    "1" -> ShopOrderContract.ProductDetail(
                        id = "1",
                        emoji = "📖",
                        name = "フォトブック",
                        description = "高品質な写真集で旅の思い出を残そう",
                        basePrice = 2980,
                        pricePerUnit = 2980,
                        isPopular = true
                    )
                    "2" -> ShopOrderContract.ProductDetail(
                        id = "2",
                        emoji = "📮",
                        name = "ポストカード",
                        description = "お気に入りの写真をポストカードに",
                        basePrice = 500,
                        pricePerUnit = 100,
                        isPopular = false
                    )
                    "3" -> ShopOrderContract.ProductDetail(
                        id = "3",
                        emoji = "📅",
                        name = "カレンダー",
                        description = "毎月の思い出を飾ろう",
                        basePrice = 1980,
                        pricePerUnit = 1980,
                        isPopular = false
                    )
                    "4" -> ShopOrderContract.ProductDetail(
                        id = "4",
                        emoji = "🖼️",
                        name = "キャンバスプリント",
                        description = "お部屋に飾れる本格アート",
                        basePrice = 4980,
                        pricePerUnit = 4980,
                        isPopular = false
                    )
                    else -> null
                }

                val trip = if (tripId != null) {
                    ShopOrderContract.TripInfo(
                        id = tripId,
                        emoji = "🏔️",
                        name = "北海道旅行2025",
                        photoCount = 48
                    )
                } else {
                    null
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        product = product,
                        selectedTrip = trip,
                        totalPrice = product?.basePrice ?: 0
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _effect.send(ShopOrderContract.Effect.ShowError("商品情報の取得に失敗しました"))
            }
        }
    }

    private fun onTripSelected(tripId: String) {
        screenModelScope.launch {
            // TODO: 旅行情報取得APIを実装
            val trip = ShopOrderContract.TripInfo(
                id = tripId,
                emoji = "🏔️",
                name = "北海道旅行2025",
                photoCount = 48
            )
            _state.update { it.copy(selectedTrip = trip) }
        }
    }

    private fun onQuantityChanged(quantity: Int) {
        val currentState = _state.value
        val product = currentState.product ?: return

        val totalPrice = product.basePrice + (product.pricePerUnit * (quantity - 1))
        _state.update {
            it.copy(
                quantity = quantity,
                totalPrice = totalPrice
            )
        }
    }

    private fun onNameChanged(name: String) {
        _state.update {
            it.copy(
                shippingAddress = it.shippingAddress.copy(
                    name = name,
                    nameError = null
                )
            )
        }
    }

    private fun onPostalCodeChanged(postalCode: String) {
        _state.update {
            it.copy(
                shippingAddress = it.shippingAddress.copy(
                    postalCode = postalCode,
                    postalCodeError = null
                )
            )
        }
    }

    private fun onAddressChanged(address: String) {
        _state.update {
            it.copy(
                shippingAddress = it.shippingAddress.copy(
                    address = address,
                    addressError = null
                )
            )
        }
    }

    private fun onPhoneNumberChanged(phoneNumber: String) {
        _state.update {
            it.copy(
                shippingAddress = it.shippingAddress.copy(
                    phoneNumber = phoneNumber,
                    phoneNumberError = null
                )
            )
        }
    }

    private fun onOrderPressed() {
        screenModelScope.launch {
            val currentState = _state.value
            var hasError = false

            // バリデーション
            if (currentState.shippingAddress.name.isBlank()) {
                _state.update {
                    it.copy(
                        shippingAddress = it.shippingAddress.copy(
                            nameError = "お名前を入力してください"
                        )
                    )
                }
                hasError = true
            }

            if (currentState.shippingAddress.postalCode.isBlank()) {
                _state.update {
                    it.copy(
                        shippingAddress = it.shippingAddress.copy(
                            postalCodeError = "郵便番号を入力してください"
                        )
                    )
                }
                hasError = true
            }

            if (currentState.shippingAddress.address.isBlank()) {
                _state.update {
                    it.copy(
                        shippingAddress = it.shippingAddress.copy(
                            addressError = "住所を入力してください"
                        )
                    )
                }
                hasError = true
            }

            if (currentState.shippingAddress.phoneNumber.isBlank()) {
                _state.update {
                    it.copy(
                        shippingAddress = it.shippingAddress.copy(
                            phoneNumberError = "電話番号を入力してください"
                        )
                    )
                }
                hasError = true
            }

            if (currentState.selectedTrip == null) {
                _effect.send(ShopOrderContract.Effect.ShowError("旅行を選択してください"))
                hasError = true
            }

            if (hasError) return@launch

            // 注文処理
            _state.update { it.copy(isLoading = true) }

            try {
                // TODO: 実際の注文API呼び出しを実装
                delay(1500)

                // モックの注文ID (実装時はUUID等を使用)
                val orderId = "ORDER_2025_001"
                _state.update { it.copy(isLoading = false) }
                _effect.send(ShopOrderContract.Effect.ShowSuccess("注文が完了しました"))
                delay(500)
                _effect.send(ShopOrderContract.Effect.NavigateToOrderComplete(orderId))
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _effect.send(ShopOrderContract.Effect.ShowError("注文に失敗しました"))
            }
        }
    }

    private fun onBackPressed() {
        screenModelScope.launch {
            _effect.send(ShopOrderContract.Effect.NavigateBack)
        }
    }
}
