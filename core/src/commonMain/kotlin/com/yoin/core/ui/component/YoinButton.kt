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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.ui.preview.PhonePreview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share

/**
 * Yoin ボタンコンポーネント
 *
 * Modern Cinematic with Amber Accent デザインに基づく5種類のボタン:
 * 1. YoinPrimaryButton - メインアクション用（塗りつぶし）
 * 2. YoinSecondaryButton - セカンダリアクション用（アウトライン）
 * 3. YoinTextButton - テキストのみボタン
 * 4. YoinIconButton - アイコンボタン
 * 5. YoinFloatingActionButton - FAB
 */

private val ButtonCornerRadius = 16.dp
private val ButtonHeight = 56.dp
private val IconButtonSize = 40.dp
private val FABSize = 56.dp
private val PressedAlpha = 0.7f
private val DisabledAlpha = 0.5f
private val AnimationDuration = 150

// ============================================
// 1. YoinPrimaryButton - メインアクション用（塗りつぶし）
// ============================================

/**
 * プライマリボタン - メインアクション用
 *
 * Amber (#FF6B35) 背景、白文字、16dp角丸、56dp高さ
 * 「余韻」の夕暮れの残光を表現
 *
 * @param text ボタンテキスト
 * @param onClick クリック時のコールバック
 * @param modifier カスタムModifier
 * @param enabled ボタンの有効/無効状態
 * @param leadingIcon 先頭に表示するアイコン（オプション）
 */
@Composable
fun YoinPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val alpha by animateFloatAsState(
        targetValue = when {
            !enabled -> DisabledAlpha
            isPressed -> PressedAlpha
            else -> 1f
        },
        animationSpec = tween(durationMillis = AnimationDuration),
        label = "primaryButtonAlpha"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .height(ButtonHeight)
            .alpha(alpha),
        enabled = enabled,
        shape = RoundedCornerShape(ButtonCornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = YoinColors.Primary,
            contentColor = Color.White,
            disabledContainerColor = YoinColors.Primary,
            disabledContentColor = Color.White
        ),
        interactionSource = interactionSource,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ============================================
// 2. YoinSecondaryButton - セカンダリアクション用（アウトライン）
// ============================================

/**
 * セカンダリボタン - セカンダリアクション用
 *
 * 透明背景、Amber (#FF6B35) ボーダー、Amber文字、16dp角丸、56dp高さ
 *
 * @param text ボタンテキスト
 * @param onClick クリック時のコールバック
 * @param modifier カスタムModifier
 * @param enabled ボタンの有効/無効状態
 * @param leadingIcon 先頭に表示するアイコン（オプション）
 */
@Composable
fun YoinSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val alpha by animateFloatAsState(
        targetValue = when {
            !enabled -> DisabledAlpha
            isPressed -> PressedAlpha
            else -> 1f
        },
        animationSpec = tween(durationMillis = AnimationDuration),
        label = "secondaryButtonAlpha"
    )

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .height(ButtonHeight)
            .alpha(alpha),
        enabled = enabled,
        shape = RoundedCornerShape(ButtonCornerRadius),
        border = BorderStroke(
            width = 1.5.dp,
            color = YoinColors.Primary
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = YoinColors.Primary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = YoinColors.Primary
        ),
        interactionSource = interactionSource,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ============================================
// 3. YoinTextButton - テキストのみボタン
// ============================================

/**
 * テキストボタン - テキストのみ
 *
 * 背景なし、Amber (#FF6B35) 文字、下線なし、タップ時に0.7透明度
 *
 * @param text ボタンテキスト
 * @param onClick クリック時のコールバック
 * @param modifier カスタムModifier
 * @param enabled ボタンの有効/無効状態
 */
@Composable
fun YoinTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val alpha by animateFloatAsState(
        targetValue = when {
            !enabled -> DisabledAlpha
            isPressed -> PressedAlpha
            else -> 1f
        },
        animationSpec = tween(durationMillis = AnimationDuration),
        label = "textButtonAlpha"
    )

    TextButton(
        onClick = onClick,
        modifier = modifier.alpha(alpha),
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            containerColor = Color.Transparent,
            contentColor = YoinColors.Primary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = YoinColors.Primary
        ),
        interactionSource = interactionSource
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// ============================================
// 4. YoinIconButton - アイコンボタン
// ============================================

/**
 * アイコンボタン
 *
 * 40dp × 40dp、円形、ダークグレー (#1C1C1E) 背景、白アイコン、24dp
 *
 * @param icon 表示するアイコン
 * @param contentDescription アクセシビリティ用の説明
 * @param onClick クリック時のコールバック
 * @param modifier カスタムModifier
 * @param enabled ボタンの有効/無効状態
 * @param size ボタンサイズ（デフォルト: 40dp）
 * @param backgroundColor 背景色（デフォルト: Surface）
 * @param iconColor アイコン色（デフォルト: 白）
 */
@Composable
fun YoinIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = IconButtonSize,
    backgroundColor: Color = YoinColors.Surface,
    iconColor: Color = Color.White
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val alpha by animateFloatAsState(
        targetValue = when {
            !enabled -> DisabledAlpha
            isPressed -> PressedAlpha
            else -> 1f
        },
        animationSpec = tween(durationMillis = AnimationDuration),
        label = "iconButtonAlpha"
    )

    Surface(
        modifier = modifier
            .size(size)
            .alpha(alpha),
        shape = CircleShape,
        color = backgroundColor
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled,
            interactionSource = interactionSource,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Transparent,
                contentColor = iconColor,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = iconColor
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(24.dp),
                tint = iconColor
            )
        }
    }
}

// ============================================
// 5. YoinFloatingActionButton - FAB
// ============================================

/**
 * フローティングアクションボタン (FAB)
 *
 * Amber (#FF6B35) 背景、白アイコン、56dp × 56dp、4dp elevation
 *
 * @param icon 表示するアイコン
 * @param contentDescription アクセシビリティ用の説明
 * @param onClick クリック時のコールバック
 * @param modifier カスタムModifier
 */
@Composable
fun YoinFloatingActionButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val alpha by animateFloatAsState(
        targetValue = if (isPressed) PressedAlpha else 1f,
        animationSpec = tween(durationMillis = AnimationDuration),
        label = "fabAlpha"
    )

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .size(FABSize)
            .alpha(alpha),
        shape = CircleShape,
        containerColor = YoinColors.Primary,
        contentColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        interactionSource = interactionSource
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp)
        )
    }
}

// ============================================
// Previews
// ============================================

/**
 * プレビュー: すべてのボタンタイプ
 */
@PhonePreview
@Composable
private fun YoinButtonsPreview() {
    Column(
        modifier = Modifier
            .background(YoinColors.Background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Yoin Buttons",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = YoinColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Primary Button
        Text(
            text = "Primary Button",
            fontSize = 14.sp,
            color = YoinColors.TextSecondary
        )
        YoinPrimaryButton(
            text = "次へ進む",
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )
        YoinPrimaryButton(
            text = "写真を撮る",
            onClick = {},
            leadingIcon = Icons.Filled.Camera,
            modifier = Modifier.fillMaxWidth()
        )
        YoinPrimaryButton(
            text = "無効状態",
            onClick = {},
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Secondary Button
        Text(
            text = "Secondary Button",
            fontSize = 14.sp,
            color = YoinColors.TextSecondary
        )
        YoinSecondaryButton(
            text = "キャンセル",
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )
        YoinSecondaryButton(
            text = "共有する",
            onClick = {},
            leadingIcon = Icons.Filled.Share,
            modifier = Modifier.fillMaxWidth()
        )
        YoinSecondaryButton(
            text = "無効状態",
            onClick = {},
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Text Button
        Text(
            text = "Text Button",
            fontSize = 14.sp,
            color = YoinColors.TextSecondary
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            YoinTextButton(
                text = "スキップ",
                onClick = {}
            )
            YoinTextButton(
                text = "詳細を見る",
                onClick = {}
            )
            YoinTextButton(
                text = "無効",
                onClick = {},
                enabled = false
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Icon Button
        Text(
            text = "Icon Button",
            fontSize = 14.sp,
            color = YoinColors.TextSecondary
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            YoinIconButton(
                icon = Icons.Filled.Camera,
                contentDescription = "カメラ",
                onClick = {}
            )
            YoinIconButton(
                icon = Icons.Filled.Favorite,
                contentDescription = "お気に入り",
                onClick = {}
            )
            YoinIconButton(
                icon = Icons.Filled.Share,
                contentDescription = "共有",
                onClick = {}
            )
            YoinIconButton(
                icon = Icons.Filled.Add,
                contentDescription = "追加（無効）",
                onClick = {},
                enabled = false
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // FAB
        Text(
            text = "Floating Action Button",
            fontSize = 14.sp,
            color = YoinColors.TextSecondary
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            YoinFloatingActionButton(
                icon = Icons.Filled.Add,
                contentDescription = "追加",
                onClick = {}
            )
            YoinFloatingActionButton(
                icon = Icons.Filled.Camera,
                contentDescription = "カメラ",
                onClick = {}
            )
        }
    }
}

/**
 * プレビュー: Primary Button バリエーション
 */
@PhonePreview
@Composable
private fun YoinPrimaryButtonPreview() {
    Column(
        modifier = Modifier
            .background(YoinColors.Background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Primary Button Variations",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = YoinColors.TextPrimary
        )

        YoinPrimaryButton(
            text = "通常状態",
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )

        YoinPrimaryButton(
            text = "アイコン付き",
            onClick = {},
            leadingIcon = Icons.Filled.Camera,
            modifier = Modifier.fillMaxWidth()
        )

        YoinPrimaryButton(
            text = "無効状態",
            onClick = {},
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * プレビュー: Secondary Button バリエーション
 */
@PhonePreview
@Composable
private fun YoinSecondaryButtonPreview() {
    Column(
        modifier = Modifier
            .background(YoinColors.Background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Secondary Button Variations",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = YoinColors.TextPrimary
        )

        YoinSecondaryButton(
            text = "通常状態",
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )

        YoinSecondaryButton(
            text = "アイコン付き",
            onClick = {},
            leadingIcon = Icons.Filled.Share,
            modifier = Modifier.fillMaxWidth()
        )

        YoinSecondaryButton(
            text = "無効状態",
            onClick = {},
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * プレビュー: Icon Button と FAB
 */
@PhonePreview
@Composable
private fun YoinIconButtonAndFabPreview() {
    Column(
        modifier = Modifier
            .background(YoinColors.Background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Icon Buttons",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = YoinColors.TextPrimary
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            YoinIconButton(
                icon = Icons.Filled.Camera,
                contentDescription = "カメラ",
                onClick = {}
            )
            YoinIconButton(
                icon = Icons.Filled.Favorite,
                contentDescription = "お気に入り",
                onClick = {},
                backgroundColor = YoinColors.Primary,
                iconColor = Color.White
            )
            YoinIconButton(
                icon = Icons.Filled.Share,
                contentDescription = "共有",
                onClick = {},
                size = 48.dp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "FAB",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = YoinColors.TextPrimary
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            YoinFloatingActionButton(
                icon = Icons.Filled.Add,
                contentDescription = "追加",
                onClick = {}
            )
            YoinFloatingActionButton(
                icon = Icons.Filled.Camera,
                contentDescription = "カメラ",
                onClick = {}
            )
        }
    }
}
