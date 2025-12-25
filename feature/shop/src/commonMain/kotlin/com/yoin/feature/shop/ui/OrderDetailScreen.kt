package com.yoin.feature.shop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.ui.component.YoinAppBar
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.shop.viewmodel.OrderDetailContract
import com.yoin.feature.shop.viewmodel.OrderDetailViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * 注文詳細画面
 *
 * 機能:
 * - 注文情報の表示
 * - 商品情報の表示
 * - 配送先情報の表示
 * - 支払い情報の表示
 * - 配送追跡への遷移
 * - サポートへの問い合わせ
 * - 再注文
 *
 * @param viewModel OrderDetailViewModel
 * @param onNavigateBack 戻るボタンのコールバック
 * @param onNavigateToDeliveryTracking 配送追跡画面への遷移コールバック
 * @param onNavigateToContactSupport サポート問い合わせ画面への遷移コールバック
 * @param onNavigateToShopOrder 商品注文画面への遷移コールバック
 */
@Composable
fun OrderDetailScreen(
    viewModel: OrderDetailViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToDeliveryTracking: (String) -> Unit = {},
    onNavigateToContactSupport: (String) -> Unit = {},
    onNavigateToShopOrder: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is OrderDetailContract.Effect.NavigateBack -> onNavigateBack()
                is OrderDetailContract.Effect.NavigateToDeliveryTracking -> {
                    onNavigateToDeliveryTracking(effect.orderId)
                }
                is OrderDetailContract.Effect.NavigateToContactSupport -> {
                    onNavigateToContactSupport(effect.orderId)
                }
                is OrderDetailContract.Effect.NavigateToShopOrder -> {
                    onNavigateToShopOrder(effect.productId)
                }
                is OrderDetailContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(YoinColors.Background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ヘッダー
            YoinAppBar(
                title = "注文詳細",
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.handleIntent(OrderDetailContract.Intent.OnBackPressed)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = YoinColors.TextPrimary
                        )
                    }
                }
            )

            // コンテンツ
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = YoinColors.Primary)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // 注文ステータスカード
                    OrderStatusCard(
                        orderId = state.orderId,
                        orderDate = state.orderDate,
                        orderStatus = state.orderStatus
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 商品情報カード
                    ProductInfoCard(
                        productName = state.productName,
                        productPrice = state.productPrice,
                        quantity = state.quantity
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 配送情報カード
                    DeliveryInfoCard(
                        trackingNumber = state.trackingNumber,
                        estimatedDeliveryDate = state.estimatedDeliveryDate,
                        shippingAddress = state.shippingAddress
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 支払い情報カード
                    PaymentInfoCard(
                        paymentMethod = state.paymentMethod,
                        subtotal = state.subtotal,
                        shippingFee = state.shippingFee,
                        totalAmount = state.totalAmount
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // アクションボタン
                    ActionButtons(
                        onTrackDeliveryPressed = {
                            viewModel.handleIntent(OrderDetailContract.Intent.OnTrackDeliveryPressed)
                        },
                        onContactSupportPressed = {
                            viewModel.handleIntent(OrderDetailContract.Intent.OnContactSupportPressed)
                        },
                        onReorderPressed = {
                            viewModel.handleIntent(OrderDetailContract.Intent.OnReorderPressed)
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // ホームインジケーター
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .width(134.dp)
                .height(5.dp)
                .background(Color.Black, RoundedCornerShape(100.dp))
        )

        // スナックバー
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * 注文ステータスカード
 */
@Composable
private fun OrderStatusCard(
    orderId: String,
    orderDate: String,
    orderStatus: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = YoinColors.Surface,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "注文番号",
                fontSize = 13.sp,
                color = YoinColors.TextSecondary
            )
            Text(
                text = orderId,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = YoinColors.TextPrimary,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            HorizontalDivider(color = YoinColors.SurfaceVariant, thickness = 0.65.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "注文日",
                        fontSize = 13.sp,
                        color = YoinColors.TextSecondary
                    )
                    Text(
                        text = orderDate,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = YoinColors.TextPrimary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "ステータス",
                        fontSize = 13.sp,
                        color = YoinColors.TextSecondary
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = YoinColors.Primary.copy(alpha = 0.1f),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = orderStatus,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = YoinColors.Primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 商品情報カード
 */
@Composable
private fun ProductInfoCard(
    productName: String,
    productPrice: String,
    quantity: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = YoinColors.Surface,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "商品情報",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextSecondary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 商品画像プレースホルダー
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(YoinColors.SurfaceVariant, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalShipping,
                        contentDescription = null,
                        tint = YoinColors.TextSecondary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = productName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = YoinColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = productPrice,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.Primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "数量: $quantity",
                        fontSize = 13.sp,
                        color = YoinColors.TextSecondary
                    )
                }
            }
        }
    }
}

/**
 * 配送情報カード
 */
@Composable
private fun DeliveryInfoCard(
    trackingNumber: String,
    estimatedDeliveryDate: String,
    shippingAddress: OrderDetailContract.ShippingAddress
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = YoinColors.Surface,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "配送情報",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextSecondary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 配送番号
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "配送番号",
                    fontSize = 13.sp,
                    color = YoinColors.TextSecondary
                )
                Text(
                    text = trackingNumber,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = YoinColors.TextPrimary
                )
            }

            // 配達予定日
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "お届け予定日",
                    fontSize = 13.sp,
                    color = YoinColors.TextSecondary
                )
                Text(
                    text = estimatedDeliveryDate,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = YoinColors.TextPrimary
                )
            }

            HorizontalDivider(color = YoinColors.SurfaceVariant, thickness = 0.65.dp)

            // 配送先住所
            Column(
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text(
                    text = "配送先",
                    fontSize = 13.sp,
                    color = YoinColors.TextSecondary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = shippingAddress.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = YoinColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "〒${shippingAddress.postalCode}",
                    fontSize = 14.sp,
                    color = YoinColors.TextPrimary
                )
                Text(
                    text = shippingAddress.address,
                    fontSize = 14.sp,
                    color = YoinColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = shippingAddress.phoneNumber,
                    fontSize = 14.sp,
                    color = YoinColors.TextPrimary
                )
            }
        }
    }
}

/**
 * 支払い情報カード
 */
@Composable
private fun PaymentInfoCard(
    paymentMethod: String,
    subtotal: String,
    shippingFee: String,
    totalAmount: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = YoinColors.Surface,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "支払い情報",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextSecondary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 支払い方法
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "支払い方法",
                    fontSize = 13.sp,
                    color = YoinColors.TextSecondary
                )
                Text(
                    text = paymentMethod,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = YoinColors.TextPrimary
                )
            }

            HorizontalDivider(color = YoinColors.SurfaceVariant, thickness = 0.65.dp)

            // 価格内訳
            Column(
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "商品代金",
                        fontSize = 14.sp,
                        color = YoinColors.TextPrimary
                    )
                    Text(
                        text = subtotal,
                        fontSize = 14.sp,
                        color = YoinColors.TextPrimary
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "送料",
                        fontSize = 14.sp,
                        color = YoinColors.TextPrimary
                    )
                    Text(
                        text = shippingFee,
                        fontSize = 14.sp,
                        color = YoinColors.TextPrimary
                    )
                }

                HorizontalDivider(color = YoinColors.TextPrimary, thickness = 1.dp)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "合計",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.TextPrimary
                    )
                    Text(
                        text = totalAmount,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.Primary
                    )
                }
            }
        }
    }
}

/**
 * アクションボタン
 */
@Composable
private fun ActionButtons(
    onTrackDeliveryPressed: () -> Unit,
    onContactSupportPressed: () -> Unit,
    onReorderPressed: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 配送追跡ボタン
        Button(
            onClick = onTrackDeliveryPressed,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = YoinColors.Primary
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.LocalShipping,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "配送状況を追跡",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // サポート問い合わせボタン
            OutlinedButton(
                onClick = onContactSupportPressed,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = YoinColors.Primary
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Message,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "問い合わせ",
                    fontSize = 14.sp
                )
            }

            // 再注文ボタン
            OutlinedButton(
                onClick = onReorderPressed,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = YoinColors.Primary
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "再注文",
                    fontSize = 14.sp
                )
            }
        }
    }
}

/**
 * プレビュー
 */
@PhonePreview
@Composable
private fun OrderDetailScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            Text("Order Detail Screen Preview")
        }
    }
}
