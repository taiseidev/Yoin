package com.yoin.feature.settings.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.ui.component.YoinAppBar
import com.yoin.core.ui.preview.PhonePreview

/**
 * プラン比較画面
 *
 * 無料プランとプレミアムプランの機能を比較表示します。
 *
 * @param onNavigateBack 戻るボタンのコールバック
 */
@Composable
fun PlanComparisonScreen(
    onNavigateBack: () -> Unit = {}
) {
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
                title = "プラン比較",
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
                Spacer(modifier = Modifier.height(24.dp))

                // プラン比較テーブルヘッダー
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 機能名の列
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp)
                    )

                    // 無料プランヘッダー
                    PlanHeaderCard(
                        planName = "無料プラン",
                        price = "¥0",
                        highlight = false,
                        modifier = Modifier.weight(1f)
                    )

                    // プレミアムプランヘッダー
                    PlanHeaderCard(
                        planName = "プレミアム",
                        price = "¥980/月",
                        highlight = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 機能比較リスト
                ComparisonItem(
                    featureName = "写真保存枚数",
                    freeValue = "100枚まで",
                    premiumValue = "無制限"
                )

                ComparisonItem(
                    featureName = "動画保存",
                    freeValue = "最大30秒",
                    premiumValue = "無制限"
                )

                ComparisonItem(
                    featureName = "ストレージ容量",
                    freeValue = "2GB",
                    premiumValue = "100GB"
                )

                ComparisonItem(
                    featureName = "ルーム作成数",
                    freeValue = "3個まで",
                    premiumValue = "無制限"
                )

                ComparisonItem(
                    featureName = "メンバー招待",
                    freeValue = "10人まで",
                    premiumValue = "無制限"
                )

                ComparisonItem(
                    featureName = "高画質保存",
                    freeAvailable = false,
                    premiumAvailable = true
                )

                ComparisonItem(
                    featureName = "オフライン閲覧",
                    freeAvailable = false,
                    premiumAvailable = true
                )

                ComparisonItem(
                    featureName = "自動バックアップ",
                    freeAvailable = true,
                    premiumAvailable = true
                )

                ComparisonItem(
                    featureName = "フォトブック作成",
                    freeAvailable = false,
                    premiumAvailable = true
                )

                ComparisonItem(
                    featureName = "広告非表示",
                    freeAvailable = false,
                    premiumAvailable = true
                )

                ComparisonItem(
                    featureName = "優先サポート",
                    freeAvailable = false,
                    premiumAvailable = true
                )

                ComparisonItem(
                    featureName = "AIアルバム整理",
                    freeAvailable = false,
                    premiumAvailable = true
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
                .background(Color.Black, RoundedCornerShape(100.dp))
        )
    }
}

@Composable
private fun PlanHeaderCard(
    planName: String,
    price: String,
    highlight: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (highlight) YoinColors.AccentGold.copy(alpha = 0.1f) else YoinColors.Surface,
        border = if (highlight) BorderStroke(2.dp, YoinColors.AccentGold) else null,
        shadowElevation = if (highlight) 2.dp else 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = planName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (highlight) YoinColors.AccentGold else YoinColors.TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = price,
                fontSize = 12.sp,
                color = if (highlight) YoinColors.AccentGold else YoinColors.TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ComparisonItem(
    featureName: String,
    freeValue: String? = null,
    premiumValue: String? = null,
    freeAvailable: Boolean? = null,
    premiumAvailable: Boolean? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        color = YoinColors.Surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 機能名
            Text(
                text = featureName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = YoinColors.TextPrimary,
                modifier = Modifier.weight(1f)
            )

            // 無料プランの値
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when {
                    freeValue != null -> Text(
                        text = freeValue,
                        fontSize = 12.sp,
                        color = YoinColors.TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    freeAvailable != null -> Icon(
                        imageVector = if (freeAvailable) Icons.Filled.Check else Icons.Filled.Close,
                        contentDescription = if (freeAvailable) "Available" else "Not Available",
                        tint = if (freeAvailable) Color(0xFF4CAF50) else Color(0xFFE53935),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // プレミアムプランの値
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when {
                    premiumValue != null -> Text(
                        text = premiumValue,
                        fontSize = 12.sp,
                        color = YoinColors.AccentGold,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    premiumAvailable != null -> Icon(
                        imageVector = if (premiumAvailable) Icons.Filled.Check else Icons.Filled.Close,
                        contentDescription = if (premiumAvailable) "Available" else "Not Available",
                        tint = if (premiumAvailable) YoinColors.AccentGold else Color(0xFFE53935),
                        modifier = Modifier.size(20.dp)
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
private fun PlanComparisonScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            PlanComparisonScreen()
        }
    }
}
