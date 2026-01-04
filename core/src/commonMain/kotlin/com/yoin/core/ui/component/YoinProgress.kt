package com.yoin.core.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinTheme
import com.yoin.core.ui.preview.PhonePreview

/**
 * ローディングインジケーターのサイズ
 */
enum class LoadingIndicatorSize(val sizeDp: Dp, val strokeWidth: Dp) {
    /** 小サイズ（24dp） - ボタン内などに使用 */
    Small(24.dp, 2.dp),
    /** 中サイズ（40dp） - インライン表示に使用 */
    Medium(40.dp, 3.dp),
    /** 大サイズ（56dp） - 画面中央などに使用 */
    Large(56.dp, 4.dp)
}

/**
 * Yoinローディングインジケーター
 *
 * 円形の無限回転アニメーションを表示します。
 * Modern Cinematic with Amber Accentデザインに準拠。
 *
 * @param modifier Modifier
 * @param size インジケーターのサイズ（Small/Medium/Large）
 * @param color プログレスの色（デフォルト: YoinColors.Primary = Amber #FF6B35）
 */
@Composable
fun YoinLoadingIndicator(
    modifier: Modifier = Modifier,
    size: LoadingIndicatorSize = LoadingIndicatorSize.Medium,
    color: Color = YoinColors.Primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing)
        ),
        label = "rotation"
    )

    CircularProgressIndicator(
        modifier = modifier
            .size(size.sizeDp)
            .graphicsLayer { rotationZ = rotation },
        color = color,
        strokeWidth = size.strokeWidth,
        trackColor = Color.Transparent
    )
}

/**
 * 全画面オーバーレイ付きローディングインジケーター
 *
 * 画面全体を半透明の黒でカバーし、中央にローディングを表示します。
 * ユーザーの操作をブロックする際に使用します。
 *
 * @param isVisible オーバーレイを表示するか
 * @param modifier Modifier
 * @param size インジケーターのサイズ（デフォルト: Large）
 * @param color プログレスの色
 * @param overlayColor オーバーレイの色（デフォルト: 黒80%透明度）
 */
@Composable
fun YoinLoadingOverlay(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    size: LoadingIndicatorSize = LoadingIndicatorSize.Large,
    color: Color = YoinColors.Primary,
    overlayColor: Color = Color.Black.copy(alpha = 0.8f)
) {
    if (isVisible) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(overlayColor)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { /* 背景タップを無効化 */ },
            contentAlignment = Alignment.Center
        ) {
            YoinLoadingIndicator(
                size = size,
                color = color
            )
        }
    }
}

/**
 * Yoinプログレスバー
 *
 * リニアプログレスを表示します。
 * Modern Cinematic with Amber Accentデザインに準拠。
 *
 * @param progress 進捗（0.0〜1.0）
 * @param modifier Modifier
 * @param color プログレスの色（デフォルト: YoinColors.Primary = Amber #FF6B35）
 * @param trackColor 背景トラックの色（デフォルト: YoinColors.SurfaceVariant #2C2C2E）
 */
@Composable
fun YoinProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = YoinColors.Primary,
    trackColor: Color = YoinColors.SurfaceVariant
) {
    LinearProgressIndicator(
        progress = { progress.coerceIn(0f, 1f) },
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp)),
        color = color,
        trackColor = trackColor,
        drawStopIndicator = {}
    )
}

/**
 * 不確定プログレスバー
 *
 * 進捗が不明な場合のアニメーション付きプログレスバーを表示します。
 *
 * @param modifier Modifier
 * @param color プログレスの色（デフォルト: YoinColors.Primary = Amber #FF6B35）
 * @param trackColor 背景トラックの色（デフォルト: YoinColors.SurfaceVariant #2C2C2E）
 */
@Composable
fun YoinIndeterminateProgressBar(
    modifier: Modifier = Modifier,
    color: Color = YoinColors.Primary,
    trackColor: Color = YoinColors.SurfaceVariant
) {
    LinearProgressIndicator(
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp)),
        color = color,
        trackColor = trackColor
    )
}

// ===== Previews =====

@PhonePreview
@Composable
private fun YoinLoadingIndicatorPreview() {
    YoinTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Small
            YoinLoadingIndicator(size = LoadingIndicatorSize.Small)
            Spacer(modifier = Modifier.height(32.dp))

            // Medium
            YoinLoadingIndicator(size = LoadingIndicatorSize.Medium)
            Spacer(modifier = Modifier.height(32.dp))

            // Large
            YoinLoadingIndicator(size = LoadingIndicatorSize.Large)
        }
    }
}

@PhonePreview
@Composable
private fun YoinLoadingOverlayPreview() {
    YoinTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            // 背景コンテンツ（サンプル）
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(YoinColors.Surface)
            )
            // オーバーレイ
            YoinLoadingOverlay(isVisible = true)
        }
    }
}

@PhonePreview
@Composable
private fun YoinProgressBarPreview() {
    YoinTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // 0%
            YoinProgressBar(progress = 0f)
            Spacer(modifier = Modifier.height(24.dp))

            // 25%
            YoinProgressBar(progress = 0.25f)
            Spacer(modifier = Modifier.height(24.dp))

            // 50%
            YoinProgressBar(progress = 0.5f)
            Spacer(modifier = Modifier.height(24.dp))

            // 75%
            YoinProgressBar(progress = 0.75f)
            Spacer(modifier = Modifier.height(24.dp))

            // 100%
            YoinProgressBar(progress = 1f)
            Spacer(modifier = Modifier.height(48.dp))

            // 不確定プログレスバー
            YoinIndeterminateProgressBar()
        }
    }
}
