package com.yoin.domain.shop.repository

import com.yoin.domain.shop.model.Order
import com.yoin.domain.shop.model.Product
import com.yoin.domain.shop.model.ShippingAddress
import kotlinx.coroutines.flow.Flow

/**
 * ショップリポジトリ
 */
interface ShopRepository {
    /**
     * 商品一覧を取得
     */
    fun getProducts(): Flow<List<Product>>

    /**
     * 商品を取得
     */
    fun getProduct(productId: String): Flow<Product?>

    /**
     * 注文を作成
     */
    suspend fun createOrder(
        productVariantId: String,
        photoIds: List<String>,
        shippingAddressId: String,
        couponId: String? = null
    ): Result<Order>

    /**
     * 注文一覧を取得
     */
    fun getOrders(): Flow<List<Order>>

    /**
     * 注文を取得
     */
    fun getOrder(orderId: String): Flow<Order?>

    /**
     * 注文をキャンセル
     */
    suspend fun cancelOrder(orderId: String, reason: String): Result<Unit>

    /**
     * 配送先住所一覧を取得
     */
    fun getShippingAddresses(): Flow<List<ShippingAddress>>

    /**
     * 配送先住所を作成
     */
    suspend fun createShippingAddress(address: ShippingAddress): Result<ShippingAddress>

    /**
     * 配送先住所を更新
     */
    suspend fun updateShippingAddress(address: ShippingAddress): Result<ShippingAddress>

    /**
     * 配送先住所を削除
     */
    suspend fun deleteShippingAddress(addressId: String): Result<Unit>
}
