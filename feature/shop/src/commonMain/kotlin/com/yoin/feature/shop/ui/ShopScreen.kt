package com.yoin.feature.shop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.ui.component.YoinSimpleAppBar
import com.yoin.core.ui.preview.PhonePreview
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
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
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
 * セクションヘッダー - Modern Cinematic Design
 */
@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = YoinColors.TextPrimary,
            letterSpacing = (-0.3).sp
        )

        // アクセントライン
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            YoinColors.Primary.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

/**
 * キャンペーンバナー - Modern Cinematic Design
 */
@Composable
private fun CampaignBanner(
    campaign: ShopContract.Campaign,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        YoinColors.Primary,
                        YoinColors.PrimaryVariant
                    )
                )
            )
    ) {
        // グラデーションオーバーレイ
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        radius = 800f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // アイコンをMaterial Iconに変更
            Icon(
                imageVector = Icons.Filled.LocalOffer,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = campaign.title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = campaign.description,
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.95f),
                fontWeight = FontWeight.Medium
            )
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
 * 旅行カード - Modern Cinematic Design with Small Icon
 */
@Composable
private fun TripCard(
    trip: ShopContract.Trip,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (trip.isSelected) {
                    Brush.verticalGradient(
                        colors = listOf(
                            YoinColors.Primary,
                            YoinColors.PrimaryVariant
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            YoinColors.Surface,
                            YoinColors.SurfaceVariant
                        )
                    )
                }
            )
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Material Iconに変更
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (trip.isSelected) {
                            Color.White.copy(alpha = 0.2f)
                        } else {
                            YoinColors.Primary.copy(alpha = 0.15f)
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Photo,
                    contentDescription = null,
                    tint = if (trip.isSelected) Color.White else YoinColors.Primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = trip.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (trip.isSelected) Color.White else YoinColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = trip.date,
                    fontSize = 11.sp,
                    color = if (trip.isSelected) {
                        Color.White.copy(alpha = 0.8f)
                    } else {
                        YoinColors.TextSecondary
                    }
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhotoCamera,
                        contentDescription = null,
                        tint = if (trip.isSelected) Color.White else YoinColors.Primary,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "${trip.photoCount}枚",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (trip.isSelected) Color.White else YoinColors.Primary
                    )
                }
            }
        }

        // 選択インジケーター
        if (trip.isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(20.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = YoinColors.Primary,
                    modifier = Modifier.size(14.dp)
                )
            }
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
 * 商品カード - Modern Cinematic Premium Design with Real Photos
 */
@Composable
private fun ProductCard(
    product: ShopContract.Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(YoinColors.Surface)
            .clickable(onClick = onClick)
    ) {
        // 商品画像エリア（実際の写真）
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            // 商品写真
            val imageUrl = getProductImageUrl(product.id)
            AsyncImage(
                model = imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // グラデーションオーバーレイ（写真の視認性向上）
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.15f),
                                Color.Black.copy(alpha = 0.4f)
                            )
                        )
                    )
            )

            // 人気No.1バッジ
            if (product.isPopular) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    YoinColors.Primary,
                                    YoinColors.PrimaryVariant
                                )
                            ),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "人気",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // 商品情報エリア
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = product.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.priceFrom,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.Primary
                )

                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = YoinColors.TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * 商品IDに基づいて商品写真のURLを取得
 */
private fun getProductImageUrl(productId: String): String {
    return when (productId) {
        "1" -> "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=800" // フォトアルバム - 美しいノート/本
        "2" -> "https://images.unsplash.com/photo-1513519245088-0e12902e35ca?w=800" // フォトフレーム - 額縁に入った写真
        "3" -> "https://images.unsplash.com/photo-1514228742587-6b1558fcca3d?w=800" // マグカップ - コーヒーカップ
        "4" -> "https://images.unsplash.com/photo-1590874103328-eac38a683ce7?w=800" // トートバッグ - トートバッグ商品
        else -> "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=800"
    }
}

/**
 * プレビュー: キャンペーンバナー
 */
@PhonePreview
@Composable
private fun CampaignBannerPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
                .padding(16.dp)
        ) {
            CampaignBanner(
                campaign = ShopContract.Campaign(
                    title = "新春キャンペーン",
                    description = "全品10%オフ",
                    emoji = "🎉"
                )
            )
        }
    }
}

/**
 * プレビュー: セクションヘッダー
 */
@PhonePreview
@Composable
private fun SectionHeaderPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
                .padding(16.dp)
        ) {
            SectionHeader(title = "商品を選ぶ")
        }
    }
}
