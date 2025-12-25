package com.yoin.feature.shop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
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
import com.yoin.feature.shop.viewmodel.DeliveryTrackingContract
import com.yoin.feature.shop.viewmodel.DeliveryTrackingViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * 配送追跡画面
 *
 * 機能:
 * - 配送状況のタイムライン表示
 * - 配送番号の表示
 * - 配達予定日の表示
 * - 情報の更新
 *
 * @param viewModel DeliveryTrackingViewModel
 * @param onNavigateBack 戻るボタンのコールバック
 */
@Composable
fun DeliveryTrackingScreen(
    viewModel: DeliveryTrackingViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is DeliveryTrackingContract.Effect.NavigateBack -> onNavigateBack()
                is DeliveryTrackingContract.Effect.ShowError -> {
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
                title = "配送追跡",
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.handleIntent(DeliveryTrackingContract.Intent.OnBackPressed)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = YoinColors.TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.handleIntent(DeliveryTrackingContract.Intent.OnRefresh)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh",
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

                    // 配送状況カード
                    DeliveryStatusCard(
                        orderId = state.orderId,
                        trackingNumber = state.trackingNumber,
                        currentStatus = state.currentStatus,
                        estimatedDeliveryDate = state.estimatedDeliveryDate
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 配送ステップ
                    Text(
                        text = "配送状況",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // タイムライン
                    state.deliverySteps.forEachIndexed { index, step ->
                        DeliveryTimelineItem(
                            step = step,
                            isLast = index == state.deliverySteps.lastIndex
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
 * 配送状況カード
 */
@Composable
private fun DeliveryStatusCard(
    orderId: String,
    trackingNumber: String,
    currentStatus: String,
    estimatedDeliveryDate: String
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
            // 注文番号
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "注文番号",
                    fontSize = 13.sp,
                    color = YoinColors.TextSecondary
                )
                Text(
                    text = orderId,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = YoinColors.TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 配送番号
            Row(
                modifier = Modifier.fillMaxWidth(),
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

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = YoinColors.SurfaceVariant, thickness = 0.65.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // 現在の状態
            Text(
                text = "現在の状態",
                fontSize = 13.sp,
                color = YoinColors.TextSecondary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = currentStatus,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.Primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 配達予定日
            Text(
                text = "お届け予定日",
                fontSize = 13.sp,
                color = YoinColors.TextSecondary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = estimatedDeliveryDate,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = YoinColors.TextPrimary
            )
        }
    }
}

/**
 * 配送タイムライン項目
 */
@Composable
private fun DeliveryTimelineItem(
    step: DeliveryTrackingContract.DeliveryStep,
    isLast: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // タイムラインインジケーター
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            // アイコン
            Icon(
                imageVector = if (step.isCompleted) Icons.Filled.CheckCircle else Icons.Filled.Circle,
                contentDescription = if (step.isCompleted) "Completed" else "Pending",
                tint = if (step.isCompleted) YoinColors.Primary else YoinColors.SurfaceVariant,
                modifier = Modifier.size(24.dp)
            )

            // 縦線
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(60.dp)
                        .background(
                            color = if (step.isCompleted) YoinColors.Primary else YoinColors.SurfaceVariant
                        )
                )
            }
        }

        // 内容
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp, bottom = if (isLast) 0.dp else 16.dp)
        ) {
            Text(
                text = step.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = if (step.isCompleted) YoinColors.TextPrimary else YoinColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = step.description,
                fontSize = 13.sp,
                color = YoinColors.TextSecondary
            )

            if (step.timestamp.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = step.timestamp,
                    fontSize = 12.sp,
                    color = YoinColors.TextSecondary
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
private fun DeliveryTrackingScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            Text("Delivery Tracking Screen Preview")
        }
    }
}
