package com.yoin.feature.shop.viewmodel

/**
 * Shop画面のMVI Contract
 *
 * 機能:
 * - キャンペーンバナー表示
 * - 旅行リスト表示
 * - 商品リスト表示
 */
object ShopContract {
    data class State(
        val isLoading: Boolean = false,
        val campaign: Campaign? = null,
        val trips: List<Trip> = emptyList(),
        val products: List<Product> = emptyList(),
        val errorMessage: String? = null
    )

    /**
     * キャンペーン情報
     */
    data class Campaign(
        val title: String,
        val description: String,
        val emoji: String
    )

    /**
     * 旅行情報
     */
    data class Trip(
        val id: String,
        val emoji: String,
        val name: String,
        val date: String,
        val photoCount: Int,
        val isSelected: Boolean = false
    )

    /**
     * 商品情報
     */
    data class Product(
        val id: String,
        val emoji: String,
        val name: String,
        val priceFrom: String,
        val isPopular: Boolean = false
    )

    sealed interface Intent {
        data object OnScreenDisplayed : Intent
        data class OnTripSelected(val tripId: String) : Intent
        data class OnProductSelected(val productId: String) : Intent
    }

    sealed interface Effect {
        data class ShowError(val message: String) : Effect
        data class NavigateToTripDetail(val tripId: String) : Effect
        data class NavigateToProductDetail(val productId: String) : Effect
    }
}
