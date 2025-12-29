package com.yoin.feature.onboarding.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraRoll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.ui.preview.ComprehensivePreview
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.domain.common.model.InitializationState
import com.yoin.feature.onboarding.viewmodel.SplashContract
import com.yoin.feature.onboarding.viewmodel.SplashViewModel
import kotlinx.coroutines.delay

/**
 * スプラッシュ画面 - Modern Cinematic Design
 *
 * デザインコンセプト:
 * - シネマティックなアンバーグラデーション背景
 * - フェードイン/スケールアニメーション
 * - ミニマルで洗練されたデザイン
 * - 「余韻」を感じさせる演出
 */
@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigateToMain: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // Effectを監視
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SplashContract.Effect.NavigateToMain -> {
                    onNavigateToMain()
                }

                is SplashContract.Effect.ShowError -> {
                    // エラー表示（実装は後で追加可能）
                }
            }
        }
    }

    // 初期化開始
    LaunchedEffect(Unit) {
        viewModel.handleIntent(SplashContract.Intent.StartInitialization)
    }

    SplashContent(initializationState = state.initializationState)
}

@Composable
private fun SplashContent(
    initializationState: InitializationState
) {
    // アニメーション状態
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    // アニメーション値
    val logoScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )

    val logoAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "logoAlpha"
    )

    val textAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, delayMillis = 300),
        label = "textAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black,
                        YoinColors.Primary.copy(alpha = 0.3f),
                        YoinColors.PrimaryVariant.copy(alpha = 0.2f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // アイコン部分（グラデーション背景 + フィルムアイコン）
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                YoinColors.Primary,
                                YoinColors.PrimaryVariant
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // ラジアルグラデーションオーバーレイ
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                )

                Icon(
                    imageVector = Icons.Filled.CameraRoll,
                    contentDescription = "Yoin Film Camera Icon",
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // アプリ名
            Text(
                text = "Yoin.",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = (-1).sp,
                modifier = Modifier.alpha(textAlpha)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // サブタイトル
            Text(
                text = "余韻を残す旅の記録",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                fontStyle = FontStyle.Italic,
                letterSpacing = 1.sp,
                modifier = Modifier.alpha(textAlpha)
            )
        }
    }
}

// Preview functions

/**
 * 基本的なスマホサイズでのPreview
 */
@PhonePreview
@Composable
private fun SplashContentPreview_NotStarted() {
    SplashContent(initializationState = InitializationState.NotStarted)
}

@PhonePreview
@Composable
private fun SplashContentPreview_Initializing() {
    SplashContent(initializationState = InitializationState.Initializing(0.5f))
}

@PhonePreview
@Composable
private fun SplashContentPreview_Completed() {
    SplashContent(initializationState = InitializationState.Completed)
}

@PhonePreview
@Composable
private fun SplashContentPreview_Failed() {
    SplashContent(initializationState = InitializationState.Failed("エラーが発生しました"))
}

/**
 * 包括的なPreview（様々なデバイスサイズ、フォントスケール対応確認用）
 */
@ComprehensivePreview
@Composable
private fun SplashContentPreview_Comprehensive() {
    SplashContent(initializationState = InitializationState.NotStarted)
}
