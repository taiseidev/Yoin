package com.yoin.feature.shop.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Shop画面のViewModel
 *
 * MVIパターンに基づいた状態管理:
 * - State: キャンペーン、旅行、商品のリスト
 * - Intent: ユーザーアクション
 * - Effect: 一時的なイベント（ナビゲーション、エラー表示）
 */
class ShopViewModel : ScreenModel {
    private val _state = MutableStateFlow(ShopContract.State())
    val state: StateFlow<ShopContract.State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ShopContract.Effect>()
    val effect: SharedFlow<ShopContract.Effect> = _effect.asSharedFlow()

    fun onIntent(intent: ShopContract.Intent) {
        when (intent) {
            is ShopContract.Intent.OnScreenDisplayed -> handleScreenDisplayed()
            is ShopContract.Intent.OnTripSelected -> handleTripSelected(intent.tripId)
            is ShopContract.Intent.OnProductSelected -> handleProductSelected(intent.productId)
        }
    }

    private fun handleScreenDisplayed() {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                // TODO: 実際のデータ取得処理を実装
                delay(300)

                val campaign = ShopContract.Campaign(
                    title = "旅の思い出をカタチに",
                    description = "フォトブック 20% OFF • 12/31まで",
                    emoji = "🎁"
                )

                val trips = listOf(
                    ShopContract.Trip(
                        id = "1",
                        emoji = "🏔",
                        name = "北海道",
                        date = "2025.07",
                        photoCount = 48,
                        isSelected = true
                    ),
                    ShopContract.Trip(
                        id = "2",
                        emoji = "🏖",
                        name = "沖縄",
                        date = "2025.05",
                        photoCount = 36
                    ),
                    ShopContract.Trip(
                        id = "3",
                        emoji = "🗼",
                        name = "東京",
                        date = "2025.04",
                        photoCount = 32
                    )
                )

                val products = listOf(
                    ShopContract.Product(
                        id = "1",
                        emoji = "📖",
                        name = "フォトブック",
                        priceFrom = "¥1,500〜",
                        isPopular = true
                    ),
                    ShopContract.Product(
                        id = "2",
                        emoji = "📮",
                        name = "ポストカード",
                        priceFrom = "¥500〜"
                    ),
                    ShopContract.Product(
                        id = "3",
                        emoji = "📅",
                        name = "カレンダー",
                        priceFrom = "¥1,500〜"
                    ),
                    ShopContract.Product(
                        id = "4",
                        emoji = "🖼",
                        name = "キャンバスプリント",
                        priceFrom = "¥3,000〜"
                    )
                )

                _state.value = _state.value.copy(
                    isLoading = false,
                    campaign = campaign,
                    trips = trips,
                    products = products
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
                _effect.emit(ShopContract.Effect.ShowError(e.message ?: "データの読み込みに失敗しました"))
            }
        }
    }

    private fun handleTripSelected(tripId: String) {
        screenModelScope.launch {
            _effect.emit(ShopContract.Effect.NavigateToTripDetail(tripId))
        }
    }

    private fun handleProductSelected(productId: String) {
        screenModelScope.launch {
            _effect.emit(ShopContract.Effect.NavigateToProductDetail(productId))
        }
    }
}
