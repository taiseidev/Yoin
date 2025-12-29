package com.yoin.feature.onboarding.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.domain.common.model.OnboardingPage
import com.yoin.feature.onboarding.viewmodel.OnboardingContract
import com.yoin.feature.onboarding.viewmodel.OnboardingViewModel
import kotlinx.coroutines.launch

/**
 * オンボーディングページデータ
 */
data class OnboardingPageData(
    val title: String,
    val description: String,
    val imageUrl: String,
    val icon: ImageVector,
    val gradientColors: List<Color>
)

/**
 * オンボーディング画面 - Modern Cinematic Design
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

    val pagerState = rememberPagerState(pageCount = { 4 })

    // pager の状態が変わったら ViewModel に通知
    LaunchedEffect(pagerState.currentPage) {
        viewModel.handleIntent(OnboardingContract.Intent.PageChanged(pagerState.currentPage))
    }

    // オンボーディングページのデータ
    val pages = remember {
        listOf(
            OnboardingPageData(
                title = "見えない状態で撮影",
                description = "フィルムカメラのように、撮った写真はすぐには見られません。\nその瞬間を大切に、一枚一枚を撮影しましょう。",
                imageUrl = "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=800", // フィルムカメラ
                icon = Icons.Filled.PhotoCamera,
                gradientColors = listOf(
                    Color.Black,
                    YoinColors.Primary.copy(alpha = 0.3f)
                )
            ),
            OnboardingPageData(
                title = "仲間とシェア",
                description = "旅の仲間を招待して、一緒に写真を撮影。\n全員の写真が集まって、特別なアルバムになります。",
                imageUrl = "https://images.unsplash.com/photo-1511988617509-a57c8a288659?w=800", // 友達と旅行
                icon = Icons.Filled.People,
                gradientColors = listOf(
                    Color.Black,
                    YoinColors.AccentRoseGold.copy(alpha = 0.3f)
                )
            ),
            OnboardingPageData(
                title = "翌朝9時に現像",
                description = "旅行が終わった翌朝9時、写真が「現像」されます。\n待つ時間が、期待と余韻を生み出します。",
                imageUrl = "https://images.unsplash.com/photo-1501619951397-5ba40d0f75da?w=800", // 朝の風景
                icon = Icons.Filled.Schedule,
                gradientColors = listOf(
                    Color.Black,
                    YoinColors.AccentCopper.copy(alpha = 0.3f)
                )
            ),
            OnboardingPageData(
                title = "フィルムカメラ体験",
                description = "デジタルなのに、アナログの温かさ。\nYoin.で、特別な旅の思い出を残しましょう。",
                imageUrl = "https://images.unsplash.com/photo-1452588511530-6a5bfb92e297?w=800", // ビンテージカメラ
                icon = Icons.Filled.CameraRoll,
                gradientColors = listOf(
                    Color.Black,
                    YoinColors.AccentSepia.copy(alpha = 0.3f)
                )
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // スキップボタン
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { viewModel.handleIntent(OnboardingContract.Intent.Skip) }
                ) {
                    Text(
                        text = "スキップ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            // ページャー
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPageContent(
                    pageData = pages[page],
                    modifier = Modifier.fillMaxSize()
                )
            }

            // ページインジケーター
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(4) { index ->
                    val isSelected = index == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 32.dp else 8.dp, 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (isSelected) {
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            YoinColors.Primary,
                                            YoinColors.PrimaryVariant
                                        )
                                    )
                                } else {
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.3f),
                                            Color.White.copy(alpha = 0.3f)
                                        )
                                    )
                                }
                            )
                    )
                    if (index < 3) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }

            // ボタン
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                if (pagerState.currentPage == 3) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        YoinColors.Primary,
                                        YoinColors.PrimaryVariant
                                    )
                                )
                            )
                    ) {
                        Button(
                            onClick = { viewModel.handleIntent(OnboardingContract.Intent.GetStarted) },
                            modifier = Modifier.fillMaxSize(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp)
                        ) {
                            Text(
                                text = "はじめる",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .border(
                                width = 2.dp,
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        YoinColors.Primary,
                                        YoinColors.PrimaryVariant
                                    )
                                ),
                                shape = RoundedCornerShape(28.dp)
                            )
                    ) {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            },
                            modifier = Modifier.fillMaxSize(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp)
                        ) {
                            Text(
                                text = "次へ",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 各ページのコンテンツ - Modern Cinematic Design
 */
@Composable
private fun OnboardingPageContent(
    pageData: OnboardingPageData,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
    ) {
        // 背景画像
        AsyncImage(
            model = pageData.imageUrl,
            contentDescription = pageData.title,
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // グラデーションオーバーレイ
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = pageData.gradientColors + listOf(
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                )
        )

        // コンテンツ
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // アイコン
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                YoinColors.Primary.copy(alpha = 0.3f),
                                YoinColors.PrimaryVariant.copy(alpha = 0.3f)
                            )
                        )
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = pageData.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // タイトル
            Text(
                text = pageData.title,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White,
                lineHeight = 40.sp,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 説明
            Text(
                text = pageData.description,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.9f),
                lineHeight = 24.sp,
                fontStyle = FontStyle.Italic
            )
        }
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
