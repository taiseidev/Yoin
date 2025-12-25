package com.yoin.feature.onboarding.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.domain.common.model.OnboardingPage
import com.yoin.feature.onboarding.viewmodel.OnboardingContract
import com.yoin.feature.onboarding.viewmodel.OnboardingViewModel
import kotlinx.coroutines.launch

/**
 * オンボーディング画面
 *
 * Yoinの特徴を4ページで紹介:
 * 1. 見えない状態で撮影
 * 2. 仲間とシェア
 * 3. 翌朝9時に現像
 * 4. フィルムカメラ体験
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onNavigateToLogin: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Effect の処理
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is OnboardingContract.Effect.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    val pagerState = rememberPagerState(pageCount = { state.pages.size })

    // pager の状態が変わったら ViewModel に通知
    LaunchedEffect(pagerState.currentPage) {
        viewModel.handleIntent(OnboardingContract.Intent.PageChanged(pagerState.currentPage))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(YoinColors.Background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ステータスバー風の時刻表示
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(YoinColors.Surface)
                    .padding(top = 24.dp, bottom = 8.dp)
            ) {
                Text(
                    text = "9:41",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = YoinColors.TextPrimary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // スキップボタン
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { viewModel.handleIntent(OnboardingContract.Intent.Skip) }
                ) {
                    Text(
                        text = "スキップ",
                        fontSize = 14.sp,
                        color = YoinColors.TextSecondary
                    )
                }
            }

            // ページャー
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPageContent(
                    page = state.pages[page],
                    pageIndex = page,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // ページインジケーター
            Row(
                modifier = Modifier.padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(state.pages.size) { index ->
                    val isSelected = index == state.currentPage
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) {
                                    YoinColors.Primary
                                } else {
                                    YoinColors.SurfaceVariant
                                }
                            )
                    )
                }
            }

            // ボタン
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                if (state.isLastPage) {
                    Button(
                        onClick = { viewModel.handleIntent(OnboardingContract.Intent.GetStarted) },
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
                            text = "はじめる",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(state.currentPage + 1)
                            }
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
                            text = "次へ",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * 各ページのコンテンツ
 */
@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    pageIndex: Int,
    modifier: Modifier = Modifier,
) {
    // 各ページの絵文字を定義
    val emoji = when (pageIndex) {
        0 -> "📷" // 見えない状態で撮影
        1 -> "👥" // 仲間とシェア
        2 -> "⏰" // 翌朝9時に現像
        3 -> "🎞️" // フィルムカメラ体験
        else -> "📸"
    }

    // 各ページの背景色を定義
    val backgroundColor = when (pageIndex) {
        0 -> YoinColors.Background // ベージュ
        1 -> Color(0xFFE8F5E8) // 薄い緑
        2 -> Color(0xFFFFF4E6) // 薄いオレンジ
        3 -> YoinColors.AccentLight // 茶色がかったベージュ
        else -> YoinColors.Background
    }

    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 絵文字アイコンを表示
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = 80.sp
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = page.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = YoinColors.TextPrimary,
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = YoinColors.TextSecondary,
            lineHeight = 24.sp
        )
    }
}

/**
 * プレビュー
 */
@PhonePreview
@Composable
private fun OnboardingScreenPreview() {
    OnboardingScreen(
        viewModel = OnboardingViewModel(),
        onNavigateToLogin = {}
    )
}
