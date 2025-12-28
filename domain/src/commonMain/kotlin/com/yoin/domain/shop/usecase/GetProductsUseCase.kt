package com.yoin.domain.shop.usecase

import com.yoin.domain.shop.model.Product
import com.yoin.domain.shop.repository.ShopRepository
import kotlinx.coroutines.flow.Flow

/**
 * 商品一覧を取得するUseCase
 */
class GetProductsUseCase(
    private val shopRepository: ShopRepository
) {
    operator fun invoke(): Flow<List<Product>> {
        // TODO: データソース実装後にダミーデータを削除
        return shopRepository.getProducts()
    }
}
