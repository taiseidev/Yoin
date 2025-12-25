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
 * 配送追跡画面のScreenModel
 *
 * @param orderId 注文ID
 */
class DeliveryTrackingViewModel(
    private val orderId: String
) : ScreenModel {

    private val _state = MutableStateFlow(DeliveryTrackingContract.State())
    val state: StateFlow<DeliveryTrackingContract.State> = _state.asStateFlow()

    private val _effect = Channel<DeliveryTrackingContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadTrackingInfo()
    }

    /**
     * ユーザーの意図を処理
     */
    fun handleIntent(intent: DeliveryTrackingContract.Intent) {
        when (intent) {
            is DeliveryTrackingContract.Intent.OnBackPressed -> onBackPressed()
            is DeliveryTrackingContract.Intent.OnRefresh -> onRefresh()
        }
    }

    private fun loadTrackingInfo() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // TODO: 実際の配送追跡API呼び出し
            kotlinx.coroutines.delay(500) // ネットワーク遅延をシミュレート

            val deliverySteps = listOf(
                DeliveryTrackingContract.DeliveryStep(
                    title = "注文受付",
                    description = "ご注文を受け付けました",
                    timestamp = "2024-01-15 10:30",
                    isCompleted = true
                ),
                DeliveryTrackingContract.DeliveryStep(
                    title = "商品準備中",
                    description = "商品を準備しています",
                    timestamp = "2024-01-15 14:00",
                    isCompleted = true
                ),
                DeliveryTrackingContract.DeliveryStep(
                    title = "発送完了",
                    description = "商品を発送しました",
                    timestamp = "2024-01-16 09:00",
                    isCompleted = true
                ),
                DeliveryTrackingContract.DeliveryStep(
                    title = "配送中",
                    description = "お届け先に向けて配送中です",
                    timestamp = "",
                    isCompleted = false
                ),
                DeliveryTrackingContract.DeliveryStep(
                    title = "配達完了",
                    description = "お届け完了",
                    timestamp = "",
                    isCompleted = false
                )
            )

            _state.update {
                it.copy(
                    isLoading = false,
                    orderId = orderId,
                    trackingNumber = "1234567890",
                    currentStatus = "配送中",
                    deliverySteps = deliverySteps,
                    estimatedDeliveryDate = "2024-01-18"
                )
            }
        }
    }

    private fun onBackPressed() {
        screenModelScope.launch {
            _effect.send(DeliveryTrackingContract.Effect.NavigateBack)
        }
    }

    private fun onRefresh() {
        loadTrackingInfo()
    }
}
