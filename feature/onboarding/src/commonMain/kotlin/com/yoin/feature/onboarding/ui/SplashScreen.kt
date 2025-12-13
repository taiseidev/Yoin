package com.yoin.feature.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.ui.preview.ComprehensivePreview
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.domain.common.model.InitializationState
import com.yoin.feature.onboarding.viewmodel.SplashContract
import com.yoin.feature.onboarding.viewmodel.SplashViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * スプラッシュ画面
 * Figmaデザイン仕様:
 * - グリーンからオレンジへのグラデーション背景
 * - 中央に白い角丸の枠でフィルムアイコン（🎞）
 * - アプリ名「Yoin.」（白、太字、36px、Noto Sans JP）
 * - サブタイトル「旅の思い出を、フィルムで。」（白80%透明度、14px）
 * - 下部にページインジケーター（3つのドット）
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4A7C59), // グリーン
                        Color(0xFF8B6F47)  // オレンジ
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // アイコン部分（白い角丸背景 + フィルム絵文字）
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🎞",
                    fontSize = 56.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // アプリ名
            Text(
                text = "Yoin.",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // サブタイトル
            Text(
                text = "旅の思い出を、フィルムで。",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }

        // ページインジケーター（下部）
        PageIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
        )
    }
}

@Composable
private fun PageIndicator(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == 0) Color.White else Color.White.copy(alpha = 0.5f)
                    )
            )
            if (index < 2) {
                Spacer(modifier = Modifier.size(8.dp))
            }
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
