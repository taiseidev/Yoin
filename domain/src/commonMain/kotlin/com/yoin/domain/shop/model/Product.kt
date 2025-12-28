package com.yoin.domain.shop.model

import kotlinx.datetime.Instant

/**
 * 商品
 */
data class Product(
    val id: String,
    val name: String,
    val description: String?,
    val category: String,
    val imageUrl: String?,
    val isActive: Boolean,
    val variants: List<ProductVariant>,
    val createdAt: Instant
)

/**
 * 商品バリアント
 */
data class ProductVariant(
    val id: String,
    val productId: String,
    val name: String,
    val sku: String,
    val price: Int,
    val description: String?,
    val minPhotos: Int?,
    val maxPhotos: Int?,
    val estimatedDays: Int?,
    val isActive: Boolean
)
