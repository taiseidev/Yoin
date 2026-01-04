package com.yoin.core.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.yoin.core.design.theme.YoinColors
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * アバターのサイズバリエーション
 */
enum class AvatarSize(val sizeDp: Dp, val fontSize: Int, val borderWidth: Dp) {
    XS(24.dp, 10, 1.dp),    // 通知、コメント
    S(32.dp, 12, 1.dp),     // メンバーリスト
    M(48.dp, 16, 2.dp),     // 標準
    L(80.dp, 24, 2.dp),     // プロフィール
    XL(120.dp, 36, 2.dp)    // プロフィール編集
}

/**
 * アバター表示コンポーネント
 *
 * ユーザーのプロフィール画像を円形で表示する。
 * 画像がない場合は、ユーザー名のイニシャルをグラデーション背景で表示する。
 *
 * @param imageUrl プロフィール画像URL（nullの場合はイニシャル表示）
 * @param size アバターのサイズ（XS/S/M/L/XL）
 * @param userName ユーザー名（画像なし時のイニシャル表示用）
 * @param badge バッジオーバーレイ（オプション、右下に表示）
 * @param modifier Modifier
 */
@Composable
fun YoinAvatar(
    imageUrl: String?,
    size: AvatarSize,
    userName: String,
    badge: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(size.sizeDp),
        contentAlignment = Alignment.Center
    ) {
        // メインアバター
        AvatarContent(
            imageUrl = imageUrl,
            size = size,
            userName = userName
        )

        // バッジオーバーレイ（右下に配置）
        if (badge != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
            ) {
                badge()
            }
        }
    }
}

/**
 * アバターのコンテンツ部分
 * 画像読み込み中はシマー効果、失敗時はイニシャル表示
 */
@Composable
private fun AvatarContent(
    imageUrl: String?,
    size: AvatarSize,
    userName: String
) {
    var imageState by remember { mutableStateOf<ImageLoadState>(ImageLoadState.Loading) }

    Box(
        modifier = Modifier
            .size(size.sizeDp)
            .clip(CircleShape)
            .border(size.borderWidth, YoinColors.SurfaceVariant, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        when {
            imageUrl.isNullOrBlank() -> {
                // 画像URLがない場合はイニシャル表示
                InitialAvatar(userName = userName, size = size)
            }
            else -> {
                // 画像読み込み
                when (imageState) {
                    ImageLoadState.Loading -> {
                        ShimmerAvatar(size = size)
                    }
                    ImageLoadState.Error -> {
                        InitialAvatar(userName = userName, size = size)
                    }
                    ImageLoadState.Success -> {
                        // 画像表示はAsyncImageで
                    }
                }

                AsyncImage(
                    model = imageUrl,
                    contentDescription = "$userName のアバター",
                    modifier = Modifier
                        .size(size.sizeDp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    onState = { state ->
                        imageState = when (state) {
                            is AsyncImagePainter.State.Loading -> ImageLoadState.Loading
                            is AsyncImagePainter.State.Success -> ImageLoadState.Success
                            is AsyncImagePainter.State.Error -> ImageLoadState.Error
                            is AsyncImagePainter.State.Empty -> ImageLoadState.Loading
                        }
                    }
                )
            }
        }
    }
}

/**
 * 画像読み込み状態
 */
private enum class ImageLoadState {
    Loading,
    Success,
    Error
}

/**
 * イニシャル表示アバター
 * Amber → Copper グラデーション背景
 */
@Composable
private fun InitialAvatar(
    userName: String,
    size: AvatarSize
) {
    val initial = extractInitial(userName)

    Box(
        modifier = Modifier
            .size(size.sizeDp)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        YoinColors.Primary,      // #FF6B35 - アンバー
                        YoinColors.AccentCopper  // #D4886C - コッパー
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(size.sizeDp.value, size.sizeDp.value)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            color = YoinColors.TextPrimary,
            fontSize = size.fontSize.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * シマー効果アバター（ローディング中）
 */
@Composable
private fun ShimmerAvatar(size: AvatarSize) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val shimmerColors = listOf(
        YoinColors.SurfaceVariant,
        YoinColors.SurfaceBright,
        YoinColors.SurfaceVariant
    )

    Box(
        modifier = Modifier
            .size(size.sizeDp)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = shimmerColors,
                    start = Offset(
                        x = size.sizeDp.value * shimmerProgress * 2 - size.sizeDp.value,
                        y = 0f
                    ),
                    end = Offset(
                        x = size.sizeDp.value * shimmerProgress * 2,
                        y = size.sizeDp.value
                    )
                )
            )
    )
}

/**
 * ユーザー名からイニシャルを抽出
 * - 日本語: 最初の1文字
 * - 英語: 最初の1文字（大文字）
 * - 空文字列: "?"
 */
private fun extractInitial(userName: String): String {
    val trimmed = userName.trim()
    if (trimmed.isEmpty()) return "?"

    val firstChar = trimmed.first()
    return firstChar.uppercaseChar().toString()
}

// ========================================
// プレビュー
// ========================================

@Preview
@Composable
private fun YoinAvatarAllSizesPreview() {
    Column(
        modifier = Modifier
            .background(YoinColors.Background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Avatar - All Sizes",
            color = YoinColors.TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarSize.entries.forEach { size ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    YoinAvatar(
                        imageUrl = null,
                        size = size,
                        userName = "Taro Yamada"
                    )
                    Text(
                        size.name,
                        color = YoinColors.TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun YoinAvatarInitialsPreview() {
    Column(
        modifier = Modifier
            .background(YoinColors.Background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Avatar - Initials (No Image)",
            color = YoinColors.TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            YoinAvatar(
                imageUrl = null,
                size = AvatarSize.M,
                userName = "Alice"
            )
            YoinAvatar(
                imageUrl = null,
                size = AvatarSize.M,
                userName = "Bob"
            )
            YoinAvatar(
                imageUrl = null,
                size = AvatarSize.M,
                userName = "太郎"
            )
            YoinAvatar(
                imageUrl = null,
                size = AvatarSize.M,
                userName = ""
            )
        }
    }
}

@Preview
@Composable
private fun YoinAvatarWithBadgePreview() {
    Column(
        modifier = Modifier
            .background(YoinColors.Background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Avatar - With Badge",
            color = YoinColors.TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // オンラインステータスバッジ
            YoinAvatar(
                imageUrl = null,
                size = AvatarSize.M,
                userName = "User",
                badge = {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF32D74B))
                            .border(2.dp, YoinColors.Background, CircleShape)
                    )
                }
            )

            // 大サイズ + バッジ
            YoinAvatar(
                imageUrl = null,
                size = AvatarSize.L,
                userName = "User",
                badge = {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF32D74B))
                            .border(2.dp, YoinColors.Background, CircleShape)
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun YoinAvatarUsagePreview() {
    Column(
        modifier = Modifier
            .background(YoinColors.Background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Avatar - Usage Examples",
            color = YoinColors.TextPrimary,
            fontWeight = FontWeight.Bold
        )

        // 通知リスト例
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            YoinAvatar(
                imageUrl = null,
                size = AvatarSize.XS,
                userName = "Notification User"
            )
            Text(
                "通知: XSサイズ",
                color = YoinColors.TextSecondary,
                fontSize = 12.sp
            )
        }

        // メンバーリスト例
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            YoinAvatar(
                imageUrl = null,
                size = AvatarSize.S,
                userName = "Member"
            )
            Text(
                "メンバーリスト: Sサイズ",
                color = YoinColors.TextSecondary,
                fontSize = 12.sp
            )
        }

        // 標準表示例
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            YoinAvatar(
                imageUrl = null,
                size = AvatarSize.M,
                userName = "Standard"
            )
            Text(
                "標準: Mサイズ",
                color = YoinColors.TextSecondary,
                fontSize = 14.sp
            )
        }

        // プロフィール例
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            YoinAvatar(
                imageUrl = null,
                size = AvatarSize.L,
                userName = "Profile"
            )
            Text(
                "プロフィール: Lサイズ",
                color = YoinColors.TextSecondary,
                fontSize = 16.sp
            )
        }

        // プロフィール編集例
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            YoinAvatar(
                imageUrl = null,
                size = AvatarSize.XL,
                userName = "Profile Edit"
            )
            Text(
                "プロフィール編集: XLサイズ",
                color = YoinColors.TextSecondary,
                fontSize = 18.sp
            )
        }
    }
}
