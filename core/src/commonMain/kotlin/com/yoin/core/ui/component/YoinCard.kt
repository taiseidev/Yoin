package com.yoin.core.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.design.theme.YoinTheme
import com.yoin.core.ui.preview.PhonePreview

/**
 * YoinCard - 標準カード
 *
 * Modern Cinematic with Amber Accent デザインに基づく標準カードコンポーネント
 * - Surface (#1C1C1E) 背景
 * - 12dp角丸、1dp elevation
 * - パディング: 16dp
 * - 暗い背景で写真とコンテンツを引き立てる
 *
 * @param modifier Modifier
 * @param onClick カードタップ時のコールバック（nullの場合クリック不可）
 * @param isSelected 選択状態（trueの場合アンバーボーダー表示）
 * @param backgroundColor 背景色（デフォルト: YoinColors.Surface）
 * @param cornerRadius 角丸の半径（デフォルト: 12.dp）
 * @param elevation 影の高さ（デフォルト: 1.dp）
 * @param contentPadding コンテンツのパディング（デフォルト: 16.dp）
 * @param content カード内のコンテンツ
 */
@Composable
fun YoinCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    isSelected: Boolean = false,
    backgroundColor: Color = YoinColors.Surface,
    cornerRadius: Dp = 12.dp,
    elevation: Dp = 1.dp,
    contentPadding: Dp = YoinSpacing.lg,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // タップフィードバック: 0.95スケール（100ms）
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "cardScale"
    )

    val shape = RoundedCornerShape(cornerRadius)
    val border = if (isSelected) {
        BorderStroke(1.dp, YoinColors.Primary)
    } else {
        null
    }

    Card(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation
        ),
        border = border
    ) {
        Box(
            modifier = Modifier.padding(contentPadding)
        ) {
            content()
        }
    }
}

/**
 * YoinPhotoCard - 写真カード
 *
 * Modern Cinematic with Amber Accent デザインに基づく写真カードコンポーネント
 * - アスペクト比1:1固定
 * - 角丸なし（Instagram-styleグリッド用）
 * - オーバーレイ情報表示対応
 * - お気に入りボタン統合（Amber #FF6B35）
 *
 * @param modifier Modifier
 * @param onClick 写真タップ時のコールバック（nullの場合クリック不可）
 * @param isFavorite お気に入り状態
 * @param onFavoriteClick お気に入りボタンタップ時のコールバック（nullの場合ボタン非表示）
 * @param overlayContent オーバーレイ上に表示するコンテンツ（BottomCenter配置）
 * @param aspectRatio アスペクト比（デフォルト: 1f - 正方形）
 * @param cornerRadius 角丸の半径（デフォルト: 0.dp - グリッド用）
 * @param imageContent 画像コンテンツ（AsyncImage等を使用する場合に渡す）
 */
@Composable
fun YoinPhotoCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    isFavorite: Boolean = false,
    onFavoriteClick: (() -> Unit)? = null,
    overlayContent: (@Composable () -> Unit)? = null,
    aspectRatio: Float = 1f,
    cornerRadius: Dp = 0.dp,
    imageContent: (@Composable BoxScope.() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // タップフィードバック: 0.95スケール（100ms）
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "photoCardScale"
    )

    val shape = if (cornerRadius > 0.dp) {
        RoundedCornerShape(cornerRadius)
    } else {
        RoundedCornerShape(0.dp)
    }

    Box(
        modifier = modifier
            .aspectRatio(aspectRatio)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            )
    ) {
        // 画像コンテンツ
        if (imageContent != null) {
            imageContent()
        } else {
            // フォールバック: プレースホルダー
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(YoinColors.SurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PhotoCamera,
                    contentDescription = "写真",
                    tint = YoinColors.TextSecondary.copy(alpha = 0.5f),
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        // お気に入りボタン
        if (onFavoriteClick != null) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(YoinSpacing.sm),
                color = Color.Black.copy(alpha = 0.5f),
                shape = CircleShape
            ) {
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavorite) "お気に入りから削除" else "お気に入りに追加",
                        tint = if (isFavorite) YoinColors.Primary else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // オーバーレイコンテンツ
        if (overlayContent != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                overlayContent()
            }
        }
    }
}

// =============================================================================
// プレビュー
// =============================================================================

@PhonePreview
@Composable
private fun YoinCardPreview() {
    YoinTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
                .padding(YoinSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(YoinSpacing.lg)
        ) {
            // 基本カード
            YoinCard(
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "基本カード",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(YoinSpacing.sm))
                    Text(
                        text = "Surface背景、12dp角丸、16dpパディング",
                        fontSize = 14.sp,
                        color = YoinColors.TextSecondary
                    )
                }
            }

            // 選択状態のカード
            YoinCard(
                onClick = {},
                isSelected = true,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "選択状態のカード",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(YoinSpacing.sm))
                    Text(
                        text = "アンバーボーダー表示",
                        fontSize = 14.sp,
                        color = YoinColors.Primary
                    )
                }
            }

            // クリック不可カード
            YoinCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "クリック不可のカード",
                    fontSize = 14.sp,
                    color = YoinColors.TextSecondary
                )
            }
        }
    }
}

@PhonePreview
@Composable
private fun YoinPhotoCardPreview() {
    YoinTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
                .padding(YoinSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(YoinSpacing.lg)
        ) {
            Text(
                text = "写真カード",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary
            )

            // 3列グリッド風
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // お気に入り済み
                YoinPhotoCard(
                    modifier = Modifier.weight(1f),
                    onClick = {},
                    isFavorite = true,
                    onFavoriteClick = {}
                )

                // 未お気に入り
                YoinPhotoCard(
                    modifier = Modifier.weight(1f),
                    onClick = {},
                    isFavorite = false,
                    onFavoriteClick = {}
                )

                // ボタンなし
                YoinPhotoCard(
                    modifier = Modifier.weight(1f),
                    onClick = {}
                )
            }

            // オーバーレイ付き
            Text(
                text = "オーバーレイ付き",
                fontSize = 14.sp,
                color = YoinColors.TextSecondary
            )

            YoinPhotoCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                aspectRatio = 16f / 9f,
                cornerRadius = 12.dp,
                onClick = {},
                isFavorite = true,
                onFavoriteClick = {},
                overlayContent = {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Black.copy(alpha = 0.6f)
                    ) {
                        Column(
                            modifier = Modifier.padding(YoinSpacing.md)
                        ) {
                            Text(
                                text = "京都の旅",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "金閣寺 • 2024/01/15",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            )
        }
    }
}
