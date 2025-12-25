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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CameraRoll
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import com.yoin.core.design.theme.YoinFontSizes
import com.yoin.core.design.theme.YoinSizes
import com.yoin.core.design.theme.YoinSpacing
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
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(YoinSpacing.xxl))

            // スキップボタン
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = YoinSpacing.xl, vertical = YoinSpacing.sm),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { viewModel.handleIntent(OnboardingContract.Intent.Skip) }
                ) {
                    Text(
                        text = "スキップ",
                        fontSize = YoinFontSizes.bodySmall.value.sp,
                        fontWeight = FontWeight.Medium,
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
                modifier = Modifier.padding(vertical = YoinSpacing.xl),
                horizontalArrangement = Arrangement.spacedBy(YoinSpacing.xs)
            ) {
                repeat(state.pages.size) { index ->
                    val isSelected = index == state.currentPage
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) YoinSizes.indicatorLarge else YoinSizes.indicatorSmall)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) {
                                    YoinColors.Primary
                                } else {
                                    YoinColors.AccentPeach
                                }
                            )
                    )
                }
            }

            // ボタン
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = YoinSpacing.xxl, vertical = YoinSpacing.huge)
            ) {
                if (state.isLastPage) {
                    Button(
                        onClick = { viewModel.handleIntent(OnboardingContract.Intent.GetStarted) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(YoinSizes.buttonHeightLarge),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = YoinColors.Primary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(YoinSpacing.lg)
                    ) {
                        Text(
                            text = "はじめる",
                            fontSize = YoinFontSizes.bodyLarge.value.sp,
                            fontWeight = FontWeight.SemiBold
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
                            .height(YoinSizes.buttonHeightLarge),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = YoinColors.Primary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(YoinSpacing.lg)
                    ) {
                        Text(
                            text = "次へ",
                            fontSize = YoinFontSizes.bodyLarge.value.sp,
                            fontWeight = FontWeight.SemiBold
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
    // 各ページのアイコンを定義
    val icon = when (pageIndex) {
        0 -> Icons.Filled.PhotoCamera // 見えない状態で撮影
        1 -> Icons.Filled.People // 仲間とシェア
        2 -> Icons.Filled.AccessTime // 翌朝9時に現像
        3 -> Icons.Filled.CameraRoll // フィルムカメラ体験
        else -> Icons.Filled.PhotoCamera
    }

    // 各ページの背景色を定義（コーラル/ピーチ系の優しい色）
    val backgroundColor = when (pageIndex) {
        0 -> Color(0xFFFFF5F0) // ほんのりピーチ
        1 -> Color(0xFFFFECE6) // 薄いコーラル
        2 -> Color(0xFFFFF0EB) // 明るいピーチ
        3 -> YoinColors.AccentPeach // 淡いピーチ
        else -> Color(0xFFFFF5F0)
    }

    Column(
        modifier = modifier.padding(horizontal = YoinSpacing.xxxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // アイコンを表示
        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(RoundedCornerShape(YoinSpacing.xxxl))
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = page.title,
                tint = YoinColors.Primary,
                modifier = Modifier.size(88.dp)
            )
        }

        Spacer(modifier = Modifier.height(YoinSpacing.huge))

        Text(
            text = page.title,
            fontSize = YoinFontSizes.headingLarge.value.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = YoinColors.TextPrimary,
            lineHeight = 34.sp
        )

        Spacer(modifier = Modifier.height(YoinSpacing.md))

        Text(
            text = page.description,
            fontSize = YoinFontSizes.bodyMedium.value.sp,
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
