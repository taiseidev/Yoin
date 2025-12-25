package com.yoin.feature.shop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.shop.viewmodel.OrderHistoryContract
import com.yoin.feature.shop.viewmodel.OrderHistoryViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * 注文履歴画面
 *
 * 機能:
 * - 注文履歴の一覧表示
 * - 注文詳細への遷移
 * - お問い合わせへの遷移
 *
 * @param viewModel OrderHistoryViewModel
 * @param onNavigateBack 戻るボタンのコールバック
 * @param onNavigateToOrderDetail 注文詳細への遷移コールバック
 * @param onNavigateToContactSupport お問い合わせへの遷移コールバック
 */
@Composable
fun OrderHistoryScreen(
    viewModel: OrderHistoryViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToOrderDetail: (String) -> Unit = {},
    onNavigateToContactSupport: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is OrderHistoryContract.Effect.NavigateBack -> onNavigateBack()
                is OrderHistoryContract.Effect.NavigateToOrderDetail -> {
                    onNavigateToOrderDetail(effect.orderId)
                }
                is OrderHistoryContract.Effect.NavigateToContactSupport -> {
                    onNavigateToContactSupport()
                }
                is OrderHistoryContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    // 画面表示時の初期化
    LaunchedEffect(Unit) {
        viewModel.onIntent(OrderHistoryContract.Intent.OnScreenDisplayed)
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
            OrderHistoryHeader(
                onBackPressed = {
                    viewModel.onIntent(OrderHistoryContract.Intent.OnBackPressed)
                }
            )

            // コンテンツ
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 注文リスト
                items(state.orders) { order ->
                    OrderItemCard(
                        order = order,
                        onClick = {
                            viewModel.onIntent(
                                OrderHistoryContract.Intent.OnOrderItemClicked(order.orderId)
                            )
                        }
                    )
                }

                // お問い合わせセクション
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "お困りですか？",
                            fontSize = 13.sp,
                            color = YoinColors.TextSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "お問い合わせ",
                            fontSize = 14.sp,
                            color = YoinColors.Primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.clickable {
                                viewModel.onIntent(OrderHistoryContract.Intent.OnContactSupportPressed)
                            }
                        )
                    }

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
 * 注文履歴ヘッダー
 */
@Composable
private fun OrderHistoryHeader(onBackPressed: () -> Unit) {
    Surface(
        color = YoinColors.Surface,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // ステータスバー
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "9:41",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = YoinColors.TextPrimary,
                    letterSpacing = (-0.15).sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // タイトルと戻るボタン
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 戻るボタン
                Text(
                    text = "←",
                    fontSize = 20.sp,
                    color = YoinColors.TextPrimary,
                    modifier = Modifier.clickable(onClick = onBackPressed)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // タイトル
                Text(
                    text = "注文履歴",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = YoinColors.SurfaceVariant,
                thickness = 0.65.dp
            )
        }
    }
}

/**
 * 注文アイテムカード
 */
@Composable
private fun OrderItemCard(
    order: OrderHistoryContract.Order,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = YoinColors.Surface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 商品アイコン
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = YoinColors.AccentPeach,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = order.productIcon,
                    fontSize = 36.sp
                )
            }

            // 注文詳細
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp)
            ) {
                // ステータスバッジと商品名
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = order.productName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.TextPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // ステータスバッジ
                    Surface(
                        color = when (order.status) {
                            OrderHistoryContract.OrderStatus.SHIPPING -> YoinColors.Primary
                            OrderHistoryContract.OrderStatus.DELIVERED -> YoinColors.AccentCoral
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = when (order.status) {
                                OrderHistoryContract.OrderStatus.SHIPPING -> "配送中"
                                OrderHistoryContract.OrderStatus.DELIVERED -> "配送済み"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = YoinColors.Surface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 旅行名
                Text(
                    text = order.tripName,
                    fontSize = 13.sp,
                    color = YoinColors.TextSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 注文日
                Text(
                    text = order.orderDate,
                    fontSize = 12.sp,
                    color = YoinColors.TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider(
                    color = YoinColors.SurfaceVariant,
                    thickness = 0.65.dp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 配送情報と価格
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 配送アイコン
                        Text(
                            text = when (order.status) {
                                OrderHistoryContract.OrderStatus.SHIPPING -> "📦"
                                OrderHistoryContract.OrderStatus.DELIVERED -> "✓"
                            },
                            fontSize = 14.sp
                        )

                        // 配送情報
                        Text(
                            text = order.deliveryInfo,
                            fontSize = 12.sp,
                            color = when (order.status) {
                                OrderHistoryContract.OrderStatus.SHIPPING -> YoinColors.Primary
                                OrderHistoryContract.OrderStatus.DELIVERED -> YoinColors.AccentCoral
                            }
                        )
                    }

                    // 価格
                    Text(
                        text = order.price,
                        fontSize = 12.sp,
                        color = YoinColors.TextSecondary
                    )
                }
            }
        }
    }
}

/**
 * プレビュー
 */
@PhonePreview
@Composable
private fun OrderHistoryScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            Text("Order History Screen Preview")
        }
    }
}
