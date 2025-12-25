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
 * FAQ詳細画面
 *
 * FAQの質問と回答を詳細に表示します。
 *
 * @param faqItem 表示するFAQ項目
 * @param onNavigateBack 戻るボタンのコールバック
 */
@Composable
fun FaqDetailScreen(
    faqItem: HelpFaqContract.FaqItem,
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
                title = "FAQ詳細",
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

                // 質問カード
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = YoinColors.Primary.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // アイコン
                        Surface(
                            shape = CircleShape,
                            color = YoinColors.Primary,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = faqItem.icon,
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // 質問テキスト
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "質問",
                                fontSize = 12.sp,
                                color = YoinColors.TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = faqItem.question,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = YoinColors.TextPrimary,
                                lineHeight = 24.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 回答カード
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
                            text = "回答",
                            fontSize = 12.sp,
                            color = YoinColors.TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = faqItem.answer,
                            fontSize = 15.sp,
                            color = YoinColors.TextPrimary,
                            lineHeight = 24.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // サポート問い合わせボタン（オプション）
                OutlinedButton(
                    onClick = { /* サポート問い合わせへ遷移 */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = YoinColors.Primary
                    )
                ) {
                    Text(
                        text = "さらに詳しいサポートが必要ですか？",
                        fontSize = 14.sp
                    )
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
 * プレビュー
 */
@PhonePreview
@Composable
private fun FaqDetailScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            FaqDetailScreen(
                faqItem = HelpFaqContract.FaqItem(
                    icon = "❓",
                    question = "写真のアップロード方法を教えてください",
                    answer = "旅の記録に写真を追加するには、タイムライン画面の「+」ボタンをタップして、カメラで撮影するか、ギャラリーから選択してください。写真は自動的にルームメンバーと共有されます。"
                )
            )
        }
    }
}
