package com.yoin.feature.shop.di

import com.yoin.feature.shop.viewmodel.OrderCompleteViewModel
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
    factory { (orderId: String, productName: String, deliveryAddress: String, deliveryDateRange: String, email: String) ->
        OrderCompleteViewModel(orderId, productName, deliveryAddress, deliveryDateRange, email)
    }
    factory { OrderHistoryViewModel() }
}
