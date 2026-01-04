package com.yoin.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinTheme
import com.yoin.core.ui.preview.PhonePreview

/**
 * アバターのサイズ定義
 *
 * Modern Cinematic with Amber Accentデザインに基づく5サイズ
 */
enum class AvatarSize(
    val sizeDp: Dp,
    val iconSizeDp: Dp,
    val borderWidth: Dp,
    val fontSize: Int
) {
    /** XS: 24dp - リスト内の小さいアイコン */
    XS(24.dp, 14.dp, 1.dp, 10),

    /** S: 32dp - コンパクトなリスト表示 */
    S(32.dp, 18.dp, 1.dp, 12),

    /** M: 40dp - 標準サイズ（コメント、チャットなど） */
    M(40.dp, 22.dp, 1.5.dp, 14),

    /** L: 56dp - プロフィールカード */
    L(56.dp, 28.dp, 2.dp, 18),

    /** XL: 80dp - プロフィール画面のメインアバター */
    XL(80.dp, 40.dp, 2.dp, 24)
}

/**
 * Yoinアバターコンポーネント
 *
 * Modern Cinematic with Amber Accentデザインに基づくアバター
 * - 円形デザイン
 * - 5サイズ対応（XS/S/M/L/XL）
 * - 画像、イニシャル、プレースホルダーアイコンに対応
 * - オプションでボーダー表示
 *
 * @param modifier Modifier
 * @param size アバターサイズ（デフォルト: M）
 * @param showBorder ボーダー表示（デフォルト: false）
 * @param borderColor ボーダー色（デフォルト: Primary = Amber #FF6B35）
 * @param backgroundColor 背景色（デフォルト: SurfaceVariant #2C2C2E）
 * @param imageContent 画像コンテンツ（AsyncImageなど）
 */
@Composable
fun YoinAvatar(
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.M,
    showBorder: Boolean = false,
    borderColor: Color = YoinColors.Primary,
    backgroundColor: Color = YoinColors.SurfaceVariant,
    imageContent: (@Composable BoxScope.() -> Unit)? = null
) {
    val borderModifier = if (showBorder) {
        Modifier.border(size.borderWidth, borderColor, CircleShape)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .size(size.sizeDp)
            .then(borderModifier)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (imageContent != null) {
            imageContent()
        } else {
            // プレースホルダーアイコン
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Avatar",
                modifier = Modifier.size(size.iconSizeDp),
                tint = YoinColors.TextSecondary
            )
        }
    }
}

/**
 * イニシャル付きアバター
 *
 * ユーザー名のイニシャルを表示するアバター
 *
 * @param initials 表示するイニシャル（1-2文字推奨）
 * @param modifier Modifier
 * @param size アバターサイズ（デフォルト: M）
 * @param showBorder ボーダー表示（デフォルト: false）
 * @param borderColor ボーダー色（デフォルト: Primary = Amber #FF6B35）
 * @param backgroundColor 背景色（デフォルト: Primary = Amber #FF6B35）
 * @param textColor テキスト色（デフォルト: 白）
 */
@Composable
fun YoinAvatarWithInitials(
    initials: String,
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.M,
    showBorder: Boolean = false,
    borderColor: Color = YoinColors.Primary,
    backgroundColor: Color = YoinColors.Primary,
    textColor: Color = Color.White
) {
    val borderModifier = if (showBorder) {
        Modifier.border(size.borderWidth, borderColor, CircleShape)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .size(size.sizeDp)
            .then(borderModifier)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials.take(2).uppercase(),
            fontSize = size.fontSize.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

/**
 * オンラインステータス付きアバター
 *
 * 右下にオンラインインジケーターを表示するアバター
 *
 * @param modifier Modifier
 * @param size アバターサイズ（デフォルト: M）
 * @param isOnline オンライン状態
 * @param showBorder ボーダー表示（デフォルト: false）
 * @param borderColor ボーダー色
 * @param imageContent 画像コンテンツ
 */
@Composable
fun YoinAvatarWithStatus(
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.M,
    isOnline: Boolean = false,
    showBorder: Boolean = false,
    borderColor: Color = YoinColors.Primary,
    imageContent: (@Composable BoxScope.() -> Unit)? = null
) {
    val indicatorSize = when (size) {
        AvatarSize.XS -> 6.dp
        AvatarSize.S -> 8.dp
        AvatarSize.M -> 10.dp
        AvatarSize.L -> 12.dp
        AvatarSize.XL -> 16.dp
    }

    Box(modifier = modifier) {
        YoinAvatar(
            size = size,
            showBorder = showBorder,
            borderColor = borderColor,
            imageContent = imageContent
        )

        // オンラインインジケーター
        if (isOnline) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(indicatorSize)
                    .border(2.dp, YoinColors.Background, CircleShape)
                    .clip(CircleShape)
                    .background(YoinColors.Success)
            )
        }
    }
}

// ============================================
// Previews
// ============================================

@PhonePreview
@Composable
private fun YoinAvatarSizesPreview() {
    YoinTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Avatar Sizes",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarSize.entries.forEach { size ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        YoinAvatar(size = size)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = size.name,
                            fontSize = 10.sp,
                            color = YoinColors.TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@PhonePreview
@Composable
private fun YoinAvatarWithBorderPreview() {
    YoinTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Avatar with Border",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                YoinAvatar(size = AvatarSize.M, showBorder = false)
                YoinAvatar(size = AvatarSize.M, showBorder = true)
                YoinAvatar(
                    size = AvatarSize.M,
                    showBorder = true,
                    borderColor = YoinColors.AccentRoseGold
                )
            }
        }
    }
}

@PhonePreview
@Composable
private fun YoinAvatarWithInitialsPreview() {
    YoinTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Avatar with Initials",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                YoinAvatarWithInitials(
                    initials = "YO",
                    size = AvatarSize.S
                )
                YoinAvatarWithInitials(
                    initials = "TK",
                    size = AvatarSize.M
                )
                YoinAvatarWithInitials(
                    initials = "AB",
                    size = AvatarSize.L,
                    backgroundColor = YoinColors.AccentCopper
                )
                YoinAvatarWithInitials(
                    initials = "CD",
                    size = AvatarSize.XL,
                    backgroundColor = YoinColors.AccentSepia
                )
            }
        }
    }
}

@PhonePreview
@Composable
private fun YoinAvatarWithStatusPreview() {
    YoinTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Avatar with Online Status",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                YoinAvatarWithStatus(
                    size = AvatarSize.S,
                    isOnline = true
                )
                YoinAvatarWithStatus(
                    size = AvatarSize.M,
                    isOnline = true
                )
                YoinAvatarWithStatus(
                    size = AvatarSize.L,
                    isOnline = true
                )
                YoinAvatarWithStatus(
                    size = AvatarSize.XL,
                    isOnline = false
                )
            }
        }
    }
}
