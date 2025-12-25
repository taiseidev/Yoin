package com.yoin.feature.shop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.ui.component.YoinSimpleAppBar
import com.yoin.feature.shop.viewmodel.ShopContract
import com.yoin.feature.shop.viewmodel.ShopViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Shop画面
 *
 * 機能:
 * - キャンペーンバナー表示
 * - 旅行リスト表示（横スクロール）
 * - 商品リスト表示（グリッド）
 *
 * @param viewModel ShopViewModel
 * @param onNavigateToProductOrder 商品注文画面への遷移コールバック
 */
@Composable
fun ShopScreen(
    viewModel: ShopViewModel,
    onNavigateToProductOrder: (String, String?) -> Unit = { _, _ -> }
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ShopContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is ShopContract.Effect.NavigateToTripDetail -> {
                    snackbarHostState.showSnackbar("旅行詳細画面は未実装です")
                }
                is ShopContract.Effect.NavigateToProductDetail -> {
                    onNavigateToProductOrder(effect.productId, null)
                }
            }
        }
    }

    // 画面表示時の初期化
    LaunchedEffect(Unit) {
        viewModel.onIntent(ShopContract.Intent.OnScreenDisplayed)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(YoinColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // ヘッダー
            YoinSimpleAppBar(title = "Shop")

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = YoinColors.Primary)
                }
            } else {
                // キャンペーンバナー
                state.campaign?.let { campaign ->
                    CampaignBanner(
                        campaign = campaign,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 旅行を選ぶ
                SectionHeader(
                    title = "旅行を選ぶ",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                TripList(
                    trips = state.trips,
                    onTripClick = { tripId ->
                        viewModel.onIntent(ShopContract.Intent.OnTripSelected(tripId))
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 商品を選ぶ
                SectionHeader(
                    title = "商品を選ぶ",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                ProductGrid(
                    products = state.products,
                    onProductClick = { productId ->
                        viewModel.onIntent(ShopContract.Intent.OnProductSelected(productId))
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(100.dp)) // ボトムナビゲーション用の余白
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
 * セクションヘッダー
 */
@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = YoinColors.TextPrimary,
        modifier = modifier
    )
}

/**
 * キャンペーンバナー
 */
@Composable
private fun CampaignBanner(
    campaign: ShopContract.Campaign,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        YoinColors.Primary, // 緑
                        Color(0xFFE07B39)  // オレンジ
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = campaign.emoji,
                fontSize = 28.sp
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = campaign.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.OnPrimary
                )
                Text(
                    text = campaign.description,
                    fontSize = 13.sp,
                    color = YoinColors.OnPrimary
                )
            }
        }
    }
}

/**
 * 旅行リスト（横スクロール）
 */
@Composable
private fun TripList(
    trips: List<ShopContract.Trip>,
    onTripClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        trips.forEach { trip ->
            TripCard(
                trip = trip,
                onClick = { onTripClick(trip.id) }
            )
        }
    }
}

/**
 * 旅行カード
 */
@Composable
private fun TripCard(
    trip: ShopContract.Trip,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(100.dp)
            .height(130.dp)
            .clickable(onClick = onClick)
            .then(
                if (trip.isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = YoinColors.Primary,
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        color = YoinColors.OnPrimary,
        shadowElevation = if (trip.isSelected) 4.dp else 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = trip.emoji,
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = trip.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = trip.date,
                fontSize = 10.sp,
                color = YoinColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${trip.photoCount}枚",
                fontSize = 10.sp,
                color = if (trip.isSelected) YoinColors.Primary else YoinColors.TextSecondary
            )
        }
    }
}

/**
 * 商品グリッド
 */
@Composable
private fun ProductGrid(
    products: List<ShopContract.Product>,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 2x2グリッド
        products.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { product ->
                    ProductCard(
                        product = product,
                        onClick = { onProductClick(product.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // 奇数個の場合、空のスペースを追加
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * 商品カード
 */
@Composable
private fun ProductCard(
    product: ShopContract.Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = YoinColors.OnPrimary,
        shadowElevation = 1.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 背景ボックス
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .background(
                            color = when (product.id) {
                                "1" -> YoinColors.Background // ベージュ
                                "2" -> YoinColors.Background // ベージュ
                                "3" -> YoinColors.Primary // 茶色
                                "4" -> YoinColors.Primary // 緑
                                else -> YoinColors.Background
                            },
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = product.emoji,
                        fontSize = 40.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = product.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = product.priceFrom,
                    fontSize = 13.sp,
                    color = YoinColors.Primary
                )
            }

            // 人気No.1バッジ
            if (product.isPopular) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(Color(0xFFE07B39), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "人気No.1",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.OnPrimary
                    )
                }
            }
        }
    }
}
