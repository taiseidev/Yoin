package com.yoin.feature.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.ui.component.YoinAppBar
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.settings.viewmodel.HelpFaqContract

/**
 * カテゴリ詳細画面
 *
 * FAQカテゴリの詳細情報を表示します。
 *
 * @param category 表示するカテゴリ
 * @param onNavigateBack 戻るボタンのコールバック
 */
@Composable
fun CategoryDetailScreen(
    category: HelpFaqContract.Category,
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
                title = category.title,
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

                // カテゴリヘッダー
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // アイコン
                    Surface(
                        shape = CircleShape,
                        color = YoinColors.Primary,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = category.icon,
                                fontSize = 32.sp,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // カテゴリタイトル
                    Column {
                        Text(
                            text = category.title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = YoinColors.TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = getCategoryDescription(category.categoryType),
                            fontSize = 14.sp,
                            color = YoinColors.TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // カテゴリ詳細カード
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
                            text = "このカテゴリについて",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = YoinColors.TextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = getCategoryDetailDescription(category.categoryType),
                            fontSize = 14.sp,
                            color = YoinColors.TextPrimary,
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 関連リンクカード
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
                            text = "関連するヘルプ",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = YoinColors.TextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "• よくある質問を確認する\n• サポートに問い合わせる\n• チュートリアルを見る",
                            fontSize = 14.sp,
                            color = YoinColors.TextSecondary,
                            lineHeight = 24.sp
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
    }
}

/**
 * カテゴリの説明を取得
 */
private fun getCategoryDescription(categoryType: HelpFaqContract.CategoryType): String {
    return when (categoryType) {
        HelpFaqContract.CategoryType.APP_USAGE -> "アプリの使い方"
        HelpFaqContract.CategoryType.PRICING_PLAN -> "料金・プラン"
        HelpFaqContract.CategoryType.ORDER_SHIPPING -> "注文・配送"
    }
}

/**
 * カテゴリの詳細説明を取得
 */
private fun getCategoryDetailDescription(categoryType: HelpFaqContract.CategoryType): String {
    return when (categoryType) {
        HelpFaqContract.CategoryType.APP_USAGE ->
            "Yoinの基本的な使い方や、アプリの主要機能についてのよくある質問をまとめています。旅の作成、写真のアップロード、メンバーの招待など、初めてご利用の方はこちらをご確認ください。"
        HelpFaqContract.CategoryType.PRICING_PLAN ->
            "プレミアムプランの詳細、料金体系、支払い方法の変更、請求書の確認など、料金とプランに関する質問をまとめています。"
        HelpFaqContract.CategoryType.ORDER_SHIPPING ->
            "フォトアルバムの注文、配送状況の確認、配送先の変更、返品・交換など、注文と配送に関する質問をまとめています。"
    }
}

/**
 * プレビュー
 */
@PhonePreview
@Composable
private fun CategoryDetailScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            CategoryDetailScreen(
                category = HelpFaqContract.Category(
                    icon = "📦",
                    title = "注文・配送",
                    categoryType = HelpFaqContract.CategoryType.ORDER_SHIPPING
                )
            )
        }
    }
}
