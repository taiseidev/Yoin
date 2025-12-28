package com.yoin.domain.shop.usecase

import com.yoin.domain.shop.model.Order
import com.yoin.domain.shop.repository.ShopRepository

/**
 * 注文を作成するUseCase
 */
class CreateOrderUseCase(
    private val shopRepository: ShopRepository
) {
    suspend operator fun invoke(
        productVariantId: String,
        photoIds: List<String>,
        shippingAddressId: String,
        couponId: String? = null
    ): Result<Order> {
        // バリデーション
        if (photoIds.isEmpty()) {
            return Result.failure(IllegalArgumentException("写真を選択してください"))
        }

        // TODO: データソース実装後にダミーデータを削除
        return shopRepository.createOrder(
            productVariantId,
            photoIds,
            shippingAddressId,
            couponId
        )
    }
}
