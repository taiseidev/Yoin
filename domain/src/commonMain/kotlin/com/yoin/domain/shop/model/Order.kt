package com.yoin.domain.shop.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

/**
 * 注文
 */
data class Order(
    val id: String,
    val orderNumber: String,
    val userId: String,
    val roomId: String?,
    val shippingAddress: ShippingAddress,
    val status: OrderStatus,
    val subtotal: Int,
    val shippingFee: Int,
    val discount: Int,
    val tax: Int,
    val total: Int,
    val paymentMethod: String?,
    val paymentStatus: PaymentStatus,
    val estimatedDeliveryStart: LocalDate?,
    val estimatedDeliveryEnd: LocalDate?,
    val shippedAt: Instant?,
    val deliveredAt: Instant?,
    val trackingNumber: String?,
    val trackingUrl: String?,
    val carrier: String?,
    val items: List<OrderItem>,
    val createdAt: Instant,
    val updatedAt: Instant
)

/**
 * 注文ステータス
 */
enum class OrderStatus {
    PENDING,        // 保留中
    CONFIRMED,      // 確定
    PROCESSING,     // 処理中
    SHIPPED,        // 発送済み
    DELIVERED,      // 配達完了
    CANCELED,       // キャンセル
    REFUNDED        // 返金済み
}

/**
 * 支払いステータス
 */
enum class PaymentStatus {
    PENDING,        // 保留中
    AUTHORIZED,     // 承認済み
    CAPTURED,       // 決済完了
    FAILED,         // 失敗
    REFUNDED        // 返金済み
}

/**
 * 注文アイテム
 */
data class OrderItem(
    val id: String,
    val orderId: String,
    val productVariantId: String,
    val productName: String,
    val variantName: String,
    val quantity: Int,
    val unitPrice: Int,
    val subtotal: Int,
    val photoIds: List<String>
)

/**
 * 配送先住所
 */
data class ShippingAddress(
    val id: String,
    val userId: String,
    val name: String,
    val postalCode: String,
    val prefecture: String,
    val city: String,
    val addressLine1: String,
    val addressLine2: String?,
    val phone: String,
    val isDefault: Boolean
)
