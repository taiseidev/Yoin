package com.yoin.feature.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.settings.viewmodel.PremiumPlanContract
import com.yoin.feature.settings.viewmodel.PremiumPlanViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * プレミアムプラン画面
 *
 * 機能:
 * - プレミアムプランの特典表示
 * - 価格と年払い割引の表示
 * - 無料トライアル開始
 * - プラン詳細比較画面への遷移
 *
 * @param viewModel PremiumPlanViewModel
 * @param onNavigateBack 戻るボタンのコールバック
 * @param onNavigateToPlanComparison プラン比較画面への遷移コールバック
 */
@Composable
fun PremiumPlanScreen(
    viewModel: PremiumPlanViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToPlanComparison: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is PremiumPlanContract.Effect.NavigateBack -> onNavigateBack()
                is PremiumPlanContract.Effect.NavigateToPlanComparison -> onNavigateToPlanComparison()
                is PremiumPlanContract.Effect.StartSubscriptionFlow -> {
                    // TODO: プラットフォーム固有のサブスクリプション購入処理
                    snackbarHostState.showSnackbar("サブスクリプション購入機能は未実装です")
                }
                is PremiumPlanContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is PremiumPlanContract.Effect.ShowSuccess -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    // 画面表示時の初期化
    LaunchedEffect(Unit) {
        viewModel.onIntent(PremiumPlanContract.Intent.OnScreenDisplayed)
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
            // 緑色のヘッダーエリア
            PremiumHeader(
                monthlyPrice = state.monthlyPrice,
                yearlyPrice = state.yearlyPrice,
                yearlySavings = state.yearlySavings,
                onClosePressed = {
                    viewModel.onIntent(PremiumPlanContract.Intent.OnClosePressed)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 特典リスト
            BenefitsList(benefits = state.benefits)

            Spacer(modifier = Modifier.height(24.dp))

            // プラン詳細比較リンク
            Text(
                text = "プラン詳細を比較する ›",
                fontSize = 14.sp,
                color = YoinColors.TextSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.onIntent(PremiumPlanContract.Intent.OnComparePlansPressed)
                    }
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 無料トライアルボタン
            StartTrialButton(
                trialDays = state.trialDays,
                isLoading = state.isLoading,
                onPressed = {
                    viewModel.onIntent(PremiumPlanContract.Intent.OnStartTrialPressed)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // キャンセル可能の注記
            Text(
                text = "いつでもキャンセル可能・自動更新",
                fontSize = 12.sp,
                color = YoinColors.TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        // スナックバー
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * プレミアムヘッダー
 */
@Composable
private fun PremiumHeader(
    monthlyPrice: String,
    yearlyPrice: String,
    yearlySavings: String,
    onClosePressed: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        YoinColors.Primary,
                        YoinColors.Primary
                    )
                )
            )
            .padding(bottom = 32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ステータスバーエリア
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
                    color = YoinColors.Surface,
                    letterSpacing = (-0.15).sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 👑アイコン
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(YoinColors.Primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👑",
                    fontSize = 48.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // タイトル
            Text(
                text = "プレミアムプラン",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.Surface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // サブタイトル
            Text(
                text = "もっと自由に、もっと楽しく",
                fontSize = 14.sp,
                color = YoinColors.Surface.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 価格
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = monthlyPrice,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.Surface
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "/月",
                    fontSize = 18.sp,
                    color = YoinColors.Surface.copy(alpha = 0.9f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 年払い割引
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "▶",
                    fontSize = 10.sp,
                    color = YoinColors.AccentMedium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "年払いで$yearlySavings！",
                    fontSize = 13.sp,
                    color = YoinColors.Surface
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = yearlyPrice,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.AccentMedium
                )
                Text(
                    text = "/年",
                    fontSize = 13.sp,
                    color = YoinColors.Surface
                )
            }
        }

        // 閉じるボタン
        IconButton(
            onClick = onClosePressed,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 24.dp, end = 8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = YoinColors.Surface.copy(alpha = 0.2f),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "×",
                        fontSize = 20.sp,
                        color = YoinColors.Surface
                    )
                }
            }
        }
    }
}

/**
 * 特典リスト
 */
@Composable
private fun BenefitsList(benefits: List<PremiumPlanContract.PlanBenefit>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        benefits.forEach { benefit ->
            BenefitItem(benefit = benefit)
        }
    }
}

/**
 * 特典アイテム
 */
@Composable
private fun BenefitItem(benefit: PremiumPlanContract.PlanBenefit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = YoinColors.Surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // アイコン
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(benefit.iconBackgroundColor), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = benefit.icon,
                    fontSize = 24.sp
                )
            }

            // テキスト
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = benefit.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )
                Text(
                    text = benefit.description,
                    fontSize = 13.sp,
                    color = YoinColors.TextSecondary
                )
            }
        }
    }
}

/**
 * 無料トライアル開始ボタン
 */
@Composable
private fun StartTrialButton(
    trialDays: Int,
    isLoading: Boolean,
    onPressed: () -> Unit
) {
    Button(
        onClick = onPressed,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = YoinColors.AccentMedium,
            contentColor = YoinColors.Surface
        ),
        shape = RoundedCornerShape(12.dp),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = YoinColors.Surface
            )
        } else {
            Text(
                text = "${trialDays}日間無料で試す",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * プレビュー
 */
@PhonePreview
@Composable
private fun PremiumPlanScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            Text("Premium Plan Screen Preview")
        }
    }
}
