package com.yoin.feature.shop.di

import com.yoin.feature.shop.viewmodel.OrderCompleteViewModel
import com.yoin.feature.shop.viewmodel.OrderConfirmationViewModel
import com.yoin.feature.shop.viewmodel.OrderHistoryViewModel
import com.yoin.feature.shop.viewmodel.ShippingAddressViewModel
import com.yoin.feature.shop.viewmodel.ShopViewModel
import com.yoin.feature.shop.viewmodel.ShopOrderViewModel
import org.koin.dsl.module

/**
 * Shop機能のDIモジュール
 */
val shopModule = module {
    factory { ShopViewModel() }
    factory { ShopOrderViewModel() }
    factory { ShippingAddressViewModel() }
    factory { params ->
        val lastName = params.get<String>(0)
        val firstName = params.get<String>(1)
        val postalCode = params.get<String>(2)
        val prefecture = params.get<String>(3)
        val city = params.get<String>(4)
        val addressLine = params.get<String>(5)
        val phoneNumber = params.get<String>(6)
        OrderConfirmationViewModel(lastName, firstName, postalCode, prefecture, city, addressLine, phoneNumber)
    }
    factory { (orderId: String, productName: String, deliveryAddress: String, deliveryDateRange: String, email: String) ->
        OrderCompleteViewModel(orderId, productName, deliveryAddress, deliveryDateRange, email)
    }
    factory { OrderHistoryViewModel() }
}
