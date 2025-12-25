package com.yoin.feature.shop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.ui.component.YoinAppBar
import com.yoin.feature.shop.viewmodel.ShopOrderContract
import com.yoin.feature.shop.viewmodel.ShopOrderViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Shop注文画面
 *
 * 機能:
 * - 商品詳細表示
 * - 旅行選択
 * - 数量選択
 * - 配送先情報入力
 * - 注文確認
 *
 * @param productId 商品ID
 * @param tripId 旅行ID（オプション）
 * @param viewModel ShopOrderViewModel
 * @param onNavigateBack 戻るコールバック
 * @param onNavigateToOrderComplete 注文完了画面への遷移コールバック
 */
@Composable
fun ShopOrderScreen(
    productId: String,
    tripId: String? = null,
    viewModel: ShopOrderViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToOrderComplete: (String, String, String, String, String) -> Unit = { _, _, _, _, _ -> }
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ShopOrderContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is ShopOrderContract.Effect.ShowSuccess -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is ShopOrderContract.Effect.NavigateBack -> {
                    onNavigateBack()
                }
                is ShopOrderContract.Effect.NavigateToOrderComplete -> {
                    onNavigateToOrderComplete(
                        effect.orderId,
                        effect.productName,
                        effect.deliveryAddress,
                        effect.deliveryDateRange,
                        effect.email
                    )
                }
            }
        }
    }

    // 画面表示時の初期化
    LaunchedEffect(productId, tripId) {
        viewModel.onIntent(ShopOrderContract.Intent.OnScreenDisplayed(productId, tripId))
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
                title = "注文内容確認",
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.onIntent(ShopOrderContract.Intent.OnBackPressed)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = YoinColors.TextPrimary
                        )
                    }
                }
            )

            if (state.isLoading && state.product == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = YoinColors.Primary)
                }
            } else {
                // コンテンツ
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    state.product?.let { product ->
                        // 商品情報
                        ProductInfoCard(product)

                        // 旅行選択
                        TripSelectionCard(
                            selectedTrip = state.selectedTrip,
                            onTripClick = {
                                // TODO: 旅行選択ダイアログを表示
                            }
                        )

                        // 数量選択
                        QuantitySelectionCard(
                            quantity = state.quantity,
                            onQuantityChanged = { quantity ->
                                viewModel.onIntent(ShopOrderContract.Intent.OnQuantityChanged(quantity))
                            }
                        )

                        // 配送先情報
                        ShippingAddressCard(
                            shippingAddress = state.shippingAddress,
                            onNameChanged = { name ->
                                viewModel.onIntent(ShopOrderContract.Intent.OnNameChanged(name))
                            },
                            onPostalCodeChanged = { postalCode ->
                                viewModel.onIntent(ShopOrderContract.Intent.OnPostalCodeChanged(postalCode))
                            },
                            onAddressChanged = { address ->
                                viewModel.onIntent(ShopOrderContract.Intent.OnAddressChanged(address))
                            },
                            onPhoneNumberChanged = { phoneNumber ->
                                viewModel.onIntent(ShopOrderContract.Intent.OnPhoneNumberChanged(phoneNumber))
                            }
                        )

                        // 価格表示
                        PriceCard(
                            product = product,
                            quantity = state.quantity,
                            totalPrice = state.totalPrice
                        )

                        Spacer(modifier = Modifier.height(80.dp)) // ボタン用の余白
                    }
                }
            }

            // 注文ボタン
            state.product?.let {
                OrderButton(
                    totalPrice = state.totalPrice,
                    isLoading = state.isLoading,
                    onOrderClick = {
                        viewModel.onIntent(ShopOrderContract.Intent.OnOrderPressed)
                    }
                )
            }
        }

        // スナックバー
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * 商品情報カード
 */
@Composable
private fun ProductInfoCard(product: ShopOrderContract.ProductDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = YoinColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 商品絵文字
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(YoinColors.Background, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = product.emoji,
                    fontSize = 32.sp
                )
            }

            // 商品情報
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )

                Text(
                    text = product.description,
                    fontSize = 14.sp,
                    color = YoinColors.TextSecondary
                )

                if (product.isPopular) {
                    Text(
                        text = "人気No.1",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE07B39)
                    )
                }
            }
        }
    }
}

/**
 * 旅行選択カード
 */
@Composable
private fun TripSelectionCard(
    selectedTrip: ShopOrderContract.TripInfo?,
    onTripClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = YoinColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "使用する旅行",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary
            )

            if (selectedTrip != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, YoinColors.Primary, RoundedCornerShape(8.dp))
                        .clickable(onClick = onTripClick)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = selectedTrip.emoji,
                        fontSize = 24.sp
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedTrip.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = YoinColors.TextPrimary
                        )

                        Text(
                            text = "${selectedTrip.photoCount}枚の写真",
                            fontSize = 12.sp,
                            color = YoinColors.TextSecondary
                        )
                    }

                    Text(
                        text = "変更",
                        fontSize = 14.sp,
                        color = YoinColors.Primary
                    )
                }
            } else {
                OutlinedButton(
                    onClick = onTripClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, YoinColors.SurfaceVariant)
                ) {
                    Text(
                        text = "旅行を選択",
                        fontSize = 14.sp,
                        color = YoinColors.TextSecondary
                    )
                }
            }
        }
    }
}

/**
 * 数量選択カード
 */
@Composable
private fun QuantitySelectionCard(
    quantity: Int,
    onQuantityChanged: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = YoinColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "数量",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // マイナスボタン
                OutlinedButton(
                    onClick = { if (quantity > 1) onQuantityChanged(quantity - 1) },
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp),
                    enabled = quantity > 1
                ) {
                    Text(text = "−", fontSize = 20.sp)
                }

                // 数量表示
                Text(
                    text = quantity.toString(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )

                // プラスボタン
                OutlinedButton(
                    onClick = { if (quantity < 10) onQuantityChanged(quantity + 1) },
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp),
                    enabled = quantity < 10
                ) {
                    Text(text = "＋", fontSize = 20.sp)
                }
            }
        }
    }
}

/**
 * 配送先情報カード
 */
@Composable
private fun ShippingAddressCard(
    shippingAddress: ShopOrderContract.ShippingAddress,
    onNameChanged: (String) -> Unit,
    onPostalCodeChanged: (String) -> Unit,
    onAddressChanged: (String) -> Unit,
    onPhoneNumberChanged: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = YoinColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "配送先情報",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary
            )

            // お名前
            OutlinedTextField(
                value = shippingAddress.name,
                onValueChange = onNameChanged,
                label = { Text("お名前") },
                isError = shippingAddress.nameError != null,
                supportingText = shippingAddress.nameError?.let { { Text(it, color = Color.Red) } },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = YoinColors.Primary,
                    unfocusedBorderColor = YoinColors.SurfaceVariant
                )
            )

            // 郵便番号
            OutlinedTextField(
                value = shippingAddress.postalCode,
                onValueChange = onPostalCodeChanged,
                label = { Text("郵便番号") },
                placeholder = { Text("123-4567") },
                isError = shippingAddress.postalCodeError != null,
                supportingText = shippingAddress.postalCodeError?.let { { Text(it, color = Color.Red) } },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = YoinColors.Primary,
                    unfocusedBorderColor = YoinColors.SurfaceVariant
                )
            )

            // 住所
            OutlinedTextField(
                value = shippingAddress.address,
                onValueChange = onAddressChanged,
                label = { Text("住所") },
                isError = shippingAddress.addressError != null,
                supportingText = shippingAddress.addressError?.let { { Text(it, color = Color.Red) } },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = YoinColors.Primary,
                    unfocusedBorderColor = YoinColors.SurfaceVariant
                ),
                minLines = 2
            )

            // 電話番号
            OutlinedTextField(
                value = shippingAddress.phoneNumber,
                onValueChange = onPhoneNumberChanged,
                label = { Text("電話番号") },
                placeholder = { Text("090-1234-5678") },
                isError = shippingAddress.phoneNumberError != null,
                supportingText = shippingAddress.phoneNumberError?.let { { Text(it, color = Color.Red) } },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = YoinColors.Primary,
                    unfocusedBorderColor = YoinColors.SurfaceVariant
                )
            )
        }
    }
}

/**
 * 価格カード
 */
@Composable
private fun PriceCard(
    product: ShopOrderContract.ProductDetail,
    quantity: Int,
    totalPrice: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = YoinColors.Background),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "商品単価",
                    fontSize = 14.sp,
                    color = YoinColors.TextSecondary
                )
                Text(
                    text = "¥${product.basePrice}",
                    fontSize = 14.sp,
                    color = YoinColors.TextPrimary
                )
            }

            if (quantity > 1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "追加 ${quantity - 1}点",
                        fontSize = 14.sp,
                        color = YoinColors.TextSecondary
                    )
                    Text(
                        text = "¥${product.pricePerUnit * (quantity - 1)}",
                        fontSize = 14.sp,
                        color = YoinColors.TextPrimary
                    )
                }
            }

            HorizontalDivider(color = YoinColors.SurfaceVariant)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "合計",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )
                Text(
                    text = "¥${totalPrice}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.Primary
                )
            }
        }
    }
}

/**
 * 注文ボタン
 */
@Composable
private fun OrderButton(
    totalPrice: Int,
    isLoading: Boolean,
    onOrderClick: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = onOrderClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = YoinColors.Primary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "¥${totalPrice} で注文する",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
