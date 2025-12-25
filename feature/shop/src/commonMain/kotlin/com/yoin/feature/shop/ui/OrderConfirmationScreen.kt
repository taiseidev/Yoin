package com.yoin.feature.shop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.yoin.feature.shop.viewmodel.OrderConfirmationContract
import com.yoin.feature.shop.viewmodel.OrderConfirmationViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * 注文確認画面
 *
 * 機能:
 * - 商品情報の表示
 * - 配送先住所の表示
 * - 支払い方法の表示
 * - 合計金額の表示
 * - 注文確定
 *
 * @param viewModel OrderConfirmationViewModel
 * @param onNavigateBack 戻るボタンのコールバック
 * @param onNavigateToOrderComplete 注文完了画面への遷移コールバック
 */
@Composable
fun OrderConfirmationScreen(
    viewModel: OrderConfirmationViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToOrderComplete: (String, String, String, String, String) -> Unit = { _, _, _, _, _ -> }
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is OrderConfirmationContract.Effect.NavigateBack -> onNavigateBack()
                is OrderConfirmationContract.Effect.NavigateToOrderComplete -> {
                    onNavigateToOrderComplete(
                        effect.orderId,
                        effect.productName,
                        effect.deliveryAddress,
                        effect.deliveryDateRange,
                        effect.email
                    )
                }
                is OrderConfirmationContract.Effect.ShowError -> {
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
                title = "注文確認",
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.handleIntent(OrderConfirmationContract.Intent.OnBackPressed)
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // 商品情報
                SectionTitle(title = "商品情報")
                Spacer(modifier = Modifier.height(8.dp))
                ProductInfoCard(
                    productName = state.productName,
                    productPrice = state.productPrice
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 配送先
                SectionTitle(title = "配送先")
                Spacer(modifier = Modifier.height(8.dp))
                ShippingAddressCard(shippingAddress = state.shippingAddress)

                Spacer(modifier = Modifier.height(24.dp))

                // 支払い方法
                SectionTitle(title = "支払い方法")
                Spacer(modifier = Modifier.height(8.dp))
                PaymentMethodCard(paymentMethod = state.paymentMethod)

                Spacer(modifier = Modifier.height(24.dp))

                // 合計金額
                SectionTitle(title = "お支払い金額")
                Spacer(modifier = Modifier.height(8.dp))
                PriceBreakdownCard(
                    productPrice = state.productPrice,
                    totalAmount = state.totalAmount
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 注文確定ボタン
                Button(
                    onClick = {
                        viewModel.handleIntent(OrderConfirmationContract.Intent.OnPlaceOrderPressed)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YoinColors.Primary,
                        contentColor = YoinColors.OnPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = YoinColors.OnPrimary
                        )
                    } else {
                        Text(
                            text = "注文を確定する",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
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
 * セクションタイトル
 */
@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = YoinColors.TextSecondary
    )
}

/**
 * 商品情報カード
 */
@Composable
private fun ProductInfoCard(
    productName: String,
    productPrice: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = YoinColors.Surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = productName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = YoinColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "数量: 1",
                    fontSize = 13.sp,
                    color = YoinColors.TextSecondary
                )
            }

            Text(
                text = productPrice,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary
            )
        }
    }
}

/**
 * 配送先住所カード
 */
@Composable
private fun ShippingAddressCard(shippingAddress: OrderConfirmationContract.ShippingAddress) {
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
                text = shippingAddress.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = YoinColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "〒${shippingAddress.postalCode}",
                fontSize = 14.sp,
                color = YoinColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = shippingAddress.address,
                fontSize = 14.sp,
                color = YoinColors.TextPrimary,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "TEL: ${shippingAddress.phoneNumber}",
                fontSize = 14.sp,
                color = YoinColors.TextSecondary
            )
        }
    }
}

/**
 * 支払い方法カード
 */
@Composable
private fun PaymentMethodCard(paymentMethod: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = YoinColors.Surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = paymentMethod,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = YoinColors.TextPrimary
            )
        }
    }
}

/**
 * 金額内訳カード
 */
@Composable
private fun PriceBreakdownCard(
    productPrice: String,
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
            // 商品代金
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "商品代金",
                    fontSize = 14.sp,
                    color = YoinColors.TextSecondary
                )
                Text(
                    text = productPrice,
                    fontSize = 14.sp,
                    color = YoinColors.TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 送料
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "送料",
                    fontSize = 14.sp,
                    color = YoinColors.TextSecondary
                )
                Text(
                    text = "¥500",
                    fontSize = 14.sp,
                    color = YoinColors.TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = YoinColors.SurfaceVariant, thickness = 0.65.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // 合計
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "合計",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )
                Text(
                    text = totalAmount,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.Primary
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
private fun OrderConfirmationScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            Text("Order Confirmation Screen Preview")
        }
    }
}
