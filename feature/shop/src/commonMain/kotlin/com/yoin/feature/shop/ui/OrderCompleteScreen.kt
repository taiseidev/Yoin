package com.yoin.feature.shop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalShipping
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
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.ui.component.YoinSimpleAppBar
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.shop.viewmodel.OrderCompleteContract
import com.yoin.feature.shop.viewmodel.OrderCompleteViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * 注文完了画面
 *
 * 機能:
 * - 注文完了メッセージ
 * - 注文詳細の表示
 * - 確認メール送信通知
 * - 配送状況確認への遷移
 * - ホームへの遷移
 * - 注文履歴への遷移
 *
 * @param viewModel OrderCompleteViewModel
 * @param onNavigateToDeliveryTracking 配送状況確認画面への遷移コールバック
 * @param onNavigateToHome ホームへの遷移コールバック
 * @param onNavigateToOrderHistory 注文履歴への遷移コールバック
 */
@Composable
fun OrderCompleteScreen(
    viewModel: OrderCompleteViewModel,
    onNavigateToDeliveryTracking: (String) -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToOrderHistory: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is OrderCompleteContract.Effect.NavigateToDeliveryTracking -> {
                    onNavigateToDeliveryTracking(effect.orderId)
                }
                is OrderCompleteContract.Effect.NavigateToHome -> onNavigateToHome()
                is OrderCompleteContract.Effect.NavigateToOrderHistory -> onNavigateToOrderHistory()
                is OrderCompleteContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    // 画面表示時の初期化
    LaunchedEffect(Unit) {
        viewModel.onIntent(OrderCompleteContract.Intent.OnScreenDisplayed)
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
            YoinSimpleAppBar(title = "注文完了")

            // コンテンツ
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // 成功アイコン
                SuccessIcon()

                Spacer(modifier = Modifier.height(32.dp))

                // タイトル
                Text(
                    text = "ご注文ありがとうございます！",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // サブタイトル
                Text(
                    text = "ご注文を受け付けました",
                    fontSize = 14.sp,
                    color = YoinColors.TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 注文詳細カード
                OrderDetailsCard(
                    orderId = state.orderId,
                    productName = state.productName,
                    deliveryAddress = state.deliveryAddress,
                    deliveryDateRange = state.deliveryDateRange
                )

                Spacer(modifier = Modifier.height(16.dp))

                // メール送信通知カード
                EmailConfirmationCard(email = state.email)

                Spacer(modifier = Modifier.height(16.dp))

                // 配送状況確認カード
                DeliveryStatusCard(
                    onClick = {
                        viewModel.onIntent(OrderCompleteContract.Intent.OnCheckDeliveryStatusPressed)
                    }
                )

                Spacer(modifier = Modifier.height(40.dp))

                // ホームに戻るボタン
                Button(
                    onClick = {
                        viewModel.onIntent(OrderCompleteContract.Intent.OnReturnHomePressed)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YoinColors.Primary,
                        contentColor = YoinColors.OnPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "ホームに戻る",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 注文履歴を見るリンク
                Text(
                    text = "注文履歴を見る",
                    fontSize = 14.sp,
                    color = YoinColors.Primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.clickable {
                        viewModel.onIntent(OrderCompleteContract.Intent.OnViewOrderHistoryPressed)
                    }
                )

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
                .background(YoinColors.TextPrimary, RoundedCornerShape(100.dp))
        )

        // スナックバー
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * 成功アイコン
 */
@Composable
private fun SuccessIcon() {
    Box(
        modifier = Modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        // 外側の薄い円
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    color = YoinColors.Primary.copy(alpha = 0.1f),
                    shape = CircleShape
                )
        )

        // 内側の濃い円
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = YoinColors.Primary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // チェックマーク
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Success",
                tint = YoinColors.OnPrimary,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

/**
 * 注文詳細カード
 */
@Composable
private fun OrderDetailsCard(
    orderId: String,
    productName: String,
    deliveryAddress: String,
    deliveryDateRange: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = YoinColors.Surface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // 注文番号
            OrderDetailRow(
                label = "注文番号",
                value = orderId,
                valueColor = YoinColors.TextPrimary,
                valueFontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = YoinColors.SurfaceVariant, thickness = 0.65.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // 商品
            OrderDetailRow(
                label = "商品",
                value = productName,
                valueColor = YoinColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = YoinColors.SurfaceVariant, thickness = 0.65.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // お届け先
            OrderDetailRow(
                label = "お届け先",
                value = deliveryAddress,
                valueColor = YoinColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = YoinColors.SurfaceVariant, thickness = 0.65.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // お届け予定
            OrderDetailRow(
                label = "お届け予定",
                value = deliveryDateRange,
                valueColor = YoinColors.Primary,
                valueFontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 注文詳細行
 */
@Composable
private fun OrderDetailRow(
    label: String,
    value: String,
    valueColor: Color = YoinColors.TextPrimary,
    valueFontWeight: FontWeight = FontWeight.Normal
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = YoinColors.TextSecondary
        )

        Text(
            text = value,
            fontSize = 14.sp,
            color = valueColor,
            fontWeight = valueFontWeight,
            textAlign = TextAlign.End
        )
    }
}

/**
 * メール送信通知カード
 */
@Composable
private fun EmailConfirmationCard(email: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = YoinColors.AccentPeach,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // メールアイコン
            Icon(
                imageVector = Icons.Filled.Email,
                contentDescription = "Email",
                tint = YoinColors.Primary,
                modifier = Modifier.size(20.dp)
            )

            // テキスト
            Column {
                Text(
                    text = "確認メールを送信しました",
                    fontSize = 13.sp,
                    color = YoinColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = email,
                    fontSize = 12.sp,
                    color = YoinColors.TextSecondary
                )
            }
        }
    }
}

/**
 * 配送状況確認カード
 */
@Composable
private fun DeliveryStatusCard(onClick: () -> Unit) {
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 配送アイコン
                Icon(
                    imageVector = Icons.Filled.LocalShipping,
                    contentDescription = "Delivery",
                    tint = YoinColors.Primary,
                    modifier = Modifier.size(18.dp)
                )

                // テキスト
                Text(
                    text = "配送状況を確認",
                    fontSize = 14.sp,
                    color = YoinColors.TextPrimary
                )
            }

            // 矢印
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Navigate",
                tint = YoinColors.TextSecondary
            )
        }
    }
}

/**
 * プレビュー
 */
@PhonePreview
@Composable
private fun OrderCompleteScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            Text("Order Complete Screen Preview")
        }
    }
}
