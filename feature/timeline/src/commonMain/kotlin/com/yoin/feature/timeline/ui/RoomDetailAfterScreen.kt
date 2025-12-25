package com.yoin.feature.timeline.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.design.theme.YoinSizes
import com.yoin.core.design.theme.YoinFontSizes
import com.yoin.feature.timeline.viewmodel.RoomDetailAfterContract
import com.yoin.feature.timeline.viewmodel.RoomDetailAfterViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * 現像後のルーム詳細画面
 *
 * 機能:
 * - 現像済み写真のタイムライン表示
 * - 撮影者と撮影場所の表示
 * - 写真のダウンロード
 *
 * @param roomId ルームID
 * @param viewModel RoomDetailAfterViewModel
 * @param onNavigateBack 戻るボタンのコールバック
 */
@Composable
fun RoomDetailAfterScreen(
    roomId: String,
    viewModel: RoomDetailAfterViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is RoomDetailAfterContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is RoomDetailAfterContract.Effect.NavigateBack -> {
                    onNavigateBack()
                }
                is RoomDetailAfterContract.Effect.NavigateToPhotoDetail -> {
                    // TODO: 写真詳細画面への遷移
                }
                is RoomDetailAfterContract.Effect.ShowDownloadSuccess -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    // 画面表示時の初期化
    LaunchedEffect(roomId) {
        viewModel.onIntent(RoomDetailAfterContract.Intent.OnScreenDisplayed(roomId))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(YoinColors.Surface)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = YoinColors.Primary
            )
        } else {
            state.roomInfo?.let { roomInfo ->
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // ヘッダー
                    RoomDetailAfterHeader(
                        roomInfo = roomInfo,
                        onBackPressed = {
                            viewModel.onIntent(RoomDetailAfterContract.Intent.OnBackPressed)
                        },
                        onDownloadAllPressed = {
                            viewModel.onIntent(RoomDetailAfterContract.Intent.OnDownloadAll)
                        }
                    )

                    // ビューモード切り替え
                    ViewModeToggle(
                        currentMode = state.viewMode,
                        onModeChanged = { mode ->
                            viewModel.onIntent(RoomDetailAfterContract.Intent.OnViewModeChanged(mode))
                        }
                    )

                    // コンテンツ
                    when (state.viewMode) {
                        RoomDetailAfterContract.ViewMode.TIMELINE -> {
                            PhotoTimeline(
                                photos = state.photos,
                                onPhotoClicked = { photoId ->
                                    viewModel.onIntent(RoomDetailAfterContract.Intent.OnPhotoClicked(photoId))
                                },
                                onDownloadPhoto = { photoId ->
                                    viewModel.onIntent(RoomDetailAfterContract.Intent.OnDownloadPhoto(photoId))
                                }
                            )
                        }
                        RoomDetailAfterContract.ViewMode.MAP -> {
                            // TODO: マップビューの実装
                            MapPlaceholder()
                        }
                    }
                }
            }
        }

        // スナックバー
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * ルーム詳細ヘッダー
 */
@Composable
private fun RoomDetailAfterHeader(
    roomInfo: RoomDetailAfterContract.RoomInfo,
    onBackPressed: () -> Unit,
    onDownloadAllPressed: () -> Unit
) {
    Surface(
        color = YoinColors.Surface,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // ステータスバー領域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(YoinSpacing.xxl),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "9:41",
                    fontSize = YoinFontSizes.labelLarge.value.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = YoinColors.TextPrimary,
                    letterSpacing = (-0.15).sp
                )
            }

            // ヘッダーコンテンツ
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = YoinSpacing.lg, vertical = YoinSpacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 戻るボタン
                Text(
                    text = "←",
                    fontSize = YoinFontSizes.labelLarge.value.sp,
                    color = YoinColors.TextPrimary,
                    modifier = Modifier.clickable(onClick = onBackPressed)
                )

                // タイトルと情報
                Column(
                    modifier = Modifier.weight(1f).padding(start = YoinSpacing.lg)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(YoinSpacing.xs)
                    ) {
                        Text(
                            text = roomInfo.emoji,
                            fontSize = YoinFontSizes.headingSmall.value.sp
                        )
                        Text(
                            text = roomInfo.title,
                            fontSize = YoinFontSizes.headingSmall.value.sp,
                            fontWeight = FontWeight.Bold,
                            color = YoinColors.TextPrimary
                        )
                    }
                    Text(
                        text = "${roomInfo.dateRange} • ${roomInfo.location}",
                        fontSize = YoinFontSizes.labelSmall.value.sp,
                        color = YoinColors.TextSecondary
                    )
                    Text(
                        text = "${roomInfo.photoCount}枚 • ${roomInfo.memberCount}人",
                        fontSize = YoinFontSizes.caption.value.sp,
                        color = YoinColors.TextSecondary
                    )
                }

                // 一括ダウンロードボタン
                Button(
                    onClick = onDownloadAllPressed,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YoinColors.Primary
                    ),
                    shape = RoundedCornerShape(YoinSpacing.sm),
                    modifier = Modifier.height(YoinSizes.iconLarge),
                    contentPadding = PaddingValues(horizontal = YoinSpacing.lg, vertical = YoinSpacing.xs + 2.dp)
                ) {
                    Text(
                        text = "⬇ 全保存",
                        fontSize = YoinFontSizes.labelSmall.value.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.Surface
                    )
                }
            }

            HorizontalDivider(
                color = YoinColors.SurfaceVariant,
                thickness = 0.65.dp
            )
        }
    }
}

/**
 * ビューモード切り替えトグル
 */
@Composable
private fun ViewModeToggle(
    currentMode: RoomDetailAfterContract.ViewMode,
    onModeChanged: (RoomDetailAfterContract.ViewMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(YoinColors.Background)
            .padding(YoinSpacing.lg),
        horizontalArrangement = Arrangement.spacedBy(YoinSpacing.md)
    ) {
        ViewModeButton(
            text = "タイムライン",
            isSelected = currentMode == RoomDetailAfterContract.ViewMode.TIMELINE,
            onClick = { onModeChanged(RoomDetailAfterContract.ViewMode.TIMELINE) },
            modifier = Modifier.weight(1f)
        )
        ViewModeButton(
            text = "マップ",
            isSelected = currentMode == RoomDetailAfterContract.ViewMode.MAP,
            onClick = { onModeChanged(RoomDetailAfterContract.ViewMode.MAP) },
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * ビューモード選択ボタン
 */
@Composable
private fun ViewModeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) YoinColors.Primary else YoinColors.Surface
    val textColor = if (isSelected) YoinColors.Surface else YoinColors.TextPrimary
    val borderColor = if (isSelected) YoinColors.Primary else YoinColors.SurfaceVariant

    Box(
        modifier = modifier
            .height(YoinSizes.buttonHeightSmall)
            .background(backgroundColor, RoundedCornerShape(YoinSpacing.sm))
            .border(1.dp, borderColor, RoundedCornerShape(YoinSpacing.sm))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = YoinFontSizes.labelLarge.value.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )
    }
}

/**
 * 写真タイムライン
 */
@Composable
private fun PhotoTimeline(
    photos: List<RoomDetailAfterContract.DevelopedPhoto>,
    onPhotoClicked: (String) -> Unit,
    onDownloadPhoto: (String) -> Unit
) {
    if (photos.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "まだ写真がありません",
                fontSize = YoinFontSizes.bodyMedium.value.sp,
                color = YoinColors.TextSecondary
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background),
            contentPadding = PaddingValues(YoinSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(YoinSpacing.lg)
        ) {
            items(photos) { photo ->
                PhotoCard(
                    photo = photo,
                    onPhotoClicked = { onPhotoClicked(photo.id) },
                    onDownloadClicked = { onDownloadPhoto(photo.id) }
                )
            }
        }
    }
}

/**
 * 写真カード
 */
@Composable
private fun PhotoCard(
    photo: RoomDetailAfterContract.DevelopedPhoto,
    onPhotoClicked: () -> Unit,
    onDownloadClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onPhotoClicked),
        shape = RoundedCornerShape(YoinSpacing.md),
        colors = CardDefaults.cardColors(containerColor = YoinColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(YoinSpacing.md)
        ) {
            // 撮影者情報
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(YoinSpacing.sm)
                ) {
                    // アバター
                    Box(
                        modifier = Modifier
                            .size(YoinSizes.iconLarge)
                            .background(YoinColors.AccentPeach, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = photo.photographerName.take(1),
                            fontSize = YoinFontSizes.labelLarge.value.sp,
                            fontWeight = FontWeight.Bold,
                            color = YoinColors.TextPrimary
                        )
                    }

                    Column {
                        Text(
                            text = "📷 ${photo.photographerName}",
                            fontSize = YoinFontSizes.labelLarge.value.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = YoinColors.TextPrimary
                        )
                        Text(
                            text = photo.timestamp,
                            fontSize = YoinFontSizes.caption.value.sp,
                            color = YoinColors.TextSecondary
                        )
                    }
                }

                // ダウンロードボタン
                IconButton(
                    onClick = onDownloadClicked,
                    modifier = Modifier.size(YoinSizes.iconLarge)
                ) {
                    Text(
                        text = if (photo.isDownloaded) "✓" else "⬇",
                        fontSize = YoinFontSizes.bodyMedium.value.sp,
                        color = if (photo.isDownloaded) YoinColors.AccentCoral else YoinColors.Primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(YoinSpacing.md))

            // 写真プレースホルダー
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .background(YoinColors.SurfaceVariant, RoundedCornerShape(YoinSpacing.sm)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(YoinSpacing.xs)
                ) {
                    Text(
                        text = "📸",
                        fontSize = 48.sp
                    )
                    Text(
                        text = "Photo #${photo.id}",
                        fontSize = YoinFontSizes.labelSmall.value.sp,
                        color = YoinColors.TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(YoinSpacing.md))

            // 撮影場所
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(YoinSpacing.xs)
            ) {
                Text(
                    text = "📍",
                    fontSize = YoinFontSizes.labelLarge.value.sp
                )
                Text(
                    text = photo.location,
                    fontSize = YoinFontSizes.labelMedium.value.sp,
                    color = YoinColors.TextSecondary
                )
            }
        }
    }
}

/**
 * マッププレースホルダー
 */
@Composable
private fun MapPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(YoinColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(YoinSpacing.sm)
        ) {
            Text(
                text = "🗺️",
                fontSize = 64.sp
            )
            Text(
                text = "マップビュー",
                fontSize = YoinFontSizes.headingSmall.value.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary
            )
            Text(
                text = "写真の撮影場所を地図上に表示します",
                fontSize = YoinFontSizes.labelLarge.value.sp,
                color = YoinColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(YoinSpacing.sm))
            Text(
                text = "（次のバージョンで実装予定）",
                fontSize = YoinFontSizes.labelSmall.value.sp,
                color = YoinColors.TextSecondary
            )
        }
    }
}
