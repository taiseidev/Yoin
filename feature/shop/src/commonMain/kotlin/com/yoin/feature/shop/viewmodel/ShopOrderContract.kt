package com.yoin.feature.shop.viewmodel

/**
 * Shop注文画面のMVI Contract
 *
 * 機能:
 * - 商品詳細表示
 * - 旅行選択
 * - 数量選択
 * - 配送先情報入力
 * - 注文確認
 */
object ShopOrderContract {

    data class State(
        val isLoading: Boolean = false,
        val product: ProductDetail? = null,
        val selectedTrip: TripInfo? = null,
        val quantity: Int = 1,
        val shippingAddress: ShippingAddress = ShippingAddress(),
        val totalPrice: Int = 0,
        val errorMessage: String? = null
    )

    /**
     * 商品詳細
     */
    data class ProductDetail(
        val id: String,
        val emoji: String,
        val name: String,
        val description: String,
        val basePrice: Int,
        val pricePerUnit: Int,
        val isPopular: Boolean = false
    )

    /**
     * 旅行情報
     */
    data class TripInfo(
        val id: String,
        val emoji: String,
        val name: String,
        val photoCount: Int,
        val thumbnailPhotos: List<String> = emptyList() // 写真のサムネイル
    )

    /**
     * 配送先情報
     */
    data class ShippingAddress(
        val name: String = "",
        val nameError: String? = null,
        val postalCode: String = "",
        val postalCodeError: String? = null,
        val address: String = "",
        val addressError: String? = null,
        val phoneNumber: String = "",
        val phoneNumberError: String? = null
    )

    sealed interface Intent {
        data class OnScreenDisplayed(val productId: String, val tripId: String?) : Intent
        data class OnTripSelected(val tripId: String) : Intent
        data class OnQuantityChanged(val quantity: Int) : Intent
        data class OnNameChanged(val name: String) : Intent
        data class OnPostalCodeChanged(val postalCode: String) : Intent
        data class OnAddressChanged(val address: String) : Intent
        data class OnPhoneNumberChanged(val phoneNumber: String) : Intent
        data object OnOrderPressed : Intent
        data object OnBackPressed : Intent
    }

    sealed interface Effect {
        data class ShowError(val message: String) : Effect
        data class ShowSuccess(val message: String) : Effect
        data object NavigateBack : Effect
        data class NavigateToOrderComplete(
            val orderId: String,
            val productName: String,
            val deliveryAddress: String,
            val deliveryDateRange: String,
            val email: String
        ) : Effect
    }
}
