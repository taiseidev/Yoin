package com.yoin.domain.shop.usecase

import com.yoin.domain.shop.model.Order
import com.yoin.domain.shop.repository.ShopRepository
import kotlinx.coroutines.flow.Flow

/**
 * 注文一覧を取得するUseCase
 */
class GetOrdersUseCase(
    private val shopRepository: ShopRepository
) {
    operator fun invoke(): Flow<List<Order>> {
        // TODO: データソース実装後にダミーデータを削除
        return shopRepository.getOrders()
    }
}
