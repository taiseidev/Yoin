package com.yoin.feature.timeline.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.design.theme.YoinSizes
import com.yoin.core.design.theme.YoinFontSizes
import com.yoin.feature.timeline.viewmodel.PhotoDetailContract
import com.yoin.feature.timeline.viewmodel.PhotoDetailViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * 写真詳細画面
 *
 * 機能:
 * - フルスクリーンでの写真表示
 * - 写真のメタデータ表示（撮影者、日時、位置情報）
 * - 写真間のナビゲーション
 * - 写真のダウンロード
 *
 * @param roomId ルームID
 * @param photoId 写真ID
 * @param viewModel PhotoDetailViewModel
 * @param onNavigateBack 戻るコールバック
 */
@Composable
fun PhotoDetailScreen(
    roomId: String,
    photoId: String,
    viewModel: PhotoDetailViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is PhotoDetailContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is PhotoDetailContract.Effect.ShowSuccess -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is PhotoDetailContract.Effect.NavigateBack -> {
                    onNavigateBack()
                }
            }
        }
    }

    // 画面表示時の初期化
    LaunchedEffect(roomId, photoId) {
        viewModel.onIntent(PhotoDetailContract.Intent.OnScreenDisplayed(roomId, photoId))
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = YoinColors.Primary
            )
        } else {
            state.photoDetail?.let { photo ->
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // メイン写真エリア
                    PhotoContent(
                        photo = photo,
                        onBackPressed = {
                            viewModel.onIntent(PhotoDetailContract.Intent.OnBackPressed)
                        },
                        onDownloadPressed = {
                            viewModel.onIntent(PhotoDetailContract.Intent.OnDownloadPressed)
                        }
                    )

                    // 写真情報ボトムシート
                    PhotoInfoBottomSheet(
                        photo = photo,
                        currentIndex = state.currentPhotoIndex,
                        totalPhotos = state.totalPhotos,
                        onPreviousPressed = {
                            viewModel.onIntent(PhotoDetailContract.Intent.OnPreviousPhotoPressed)
                        },
                        onNextPressed = {
                            viewModel.onIntent(PhotoDetailContract.Intent.OnNextPhotoPressed)
                        }
                    )
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
 * 写真コンテンツエリア
 */
@Composable
private fun ColumnScope.PhotoContent(
    photo: PhotoDetailContract.PhotoDetail,
    onBackPressed: () -> Unit,
    onDownloadPressed: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .background(YoinColors.Primary) // サンプル背景色（実際は画像表示）
    ) {
        // TODO: 実際の写真を表示
        // AsyncImage(model = photo.imageUrl, ...)

        // 上部のオーバーレイとコントロール
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            // ヘッダーコントロール
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .padding(start = YoinSpacing.md, end = YoinSpacing.md, top = YoinSpacing.xxl, bottom = YoinSpacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 戻るボタン
                Box(
                    modifier = Modifier
                        .size(YoinSizes.buttonHeightSmall)
                        .clip(CircleShape)
                        .background(YoinColors.Surface.copy(alpha = 0.2f))
                        .clickable(onClick = onBackPressed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "戻る",
                        tint = YoinColors.Surface,
                        modifier = Modifier.size(YoinSizes.iconMedium)
                    )
                }

                // ダウンロードボタン
                Box(
                    modifier = Modifier
                        .size(YoinSizes.buttonHeightSmall)
                        .clip(CircleShape)
                        .background(YoinColors.Surface.copy(alpha = 0.2f))
                        .clickable(onClick = onDownloadPressed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Download,
                        contentDescription = "ダウンロード",
                        tint = YoinColors.Surface,
                        modifier = Modifier.size(YoinSizes.iconMedium)
                    )
                }
            }
        }

        // 日付の透かし（右下）
        Text(
            text = photo.dateWatermark,
            fontSize = YoinFontSizes.labelSmall.value.sp,
            color = YoinColors.Surface,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = YoinSizes.buttonHeightSmall, bottom = YoinSpacing.lg)
        )
    }
}

/**
 * 写真情報ボトムシート
 */
@Composable
private fun PhotoInfoBottomSheet(
    photo: PhotoDetailContract.PhotoDetail,
    currentIndex: Int,
    totalPhotos: Int,
    onPreviousPressed: () -> Unit,
    onNextPressed: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = YoinSpacing.xxl, topEnd = YoinSpacing.xxl),
        color = YoinColors.Surface,
        shadowElevation = YoinSpacing.sm
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = YoinSpacing.xxl, vertical = YoinSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(YoinSpacing.lg)
        ) {
            // ハンドルバー
            Box(
                modifier = Modifier
                    .width(YoinSizes.buttonHeightSmall)
                    .height(YoinSpacing.xs)
                    .background(YoinColors.SurfaceVariant, RoundedCornerShape(2.dp))
                    .align(Alignment.CenterHorizontally)
            )

            // 撮影者情報
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(YoinSpacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // アバター
                Box(
                    modifier = Modifier
                        .size(YoinSizes.iconXLarge)
                        .background(YoinColors.AccentPeach, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = photo.photographerInitial,
                        fontSize = YoinFontSizes.labelLarge.value.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.TextPrimary
                    )
                }

                Column {
                    Text(
                        text = photo.photographerName,
                        fontSize = YoinFontSizes.bodyMedium.value.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.TextPrimary
                    )
                    Text(
                        text = "撮影者",
                        fontSize = YoinFontSizes.labelMedium.value.sp,
                        color = YoinColors.TextSecondary
                    )
                }
            }

            HorizontalDivider(color = YoinColors.SurfaceVariant, thickness = 0.65.dp)

            // 日時情報
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(YoinSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarToday,
                    contentDescription = "日時",
                    tint = YoinColors.TextPrimary,
                    modifier = Modifier.size(YoinSizes.iconMedium)
                )
                Text(
                    text = photo.dateTime,
                    fontSize = YoinFontSizes.labelLarge.value.sp,
                    color = YoinColors.TextPrimary
                )
            }

            // 位置情報
            Column(
                verticalArrangement = Arrangement.spacedBy(YoinSpacing.xs)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(YoinSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "位置",
                        tint = YoinColors.TextPrimary,
                        modifier = Modifier.size(YoinSizes.iconMedium)
                    )
                    Text(
                        text = photo.location,
                        fontSize = YoinFontSizes.labelLarge.value.sp,
                        color = YoinColors.TextPrimary
                    )
                }
                Text(
                    text = photo.subLocation,
                    fontSize = YoinFontSizes.labelSmall.value.sp,
                    color = YoinColors.TextSecondary,
                    modifier = Modifier.padding(start = 26.dp)
                )
            }

            Spacer(modifier = Modifier.height(YoinSpacing.sm))

            // ナビゲーションコントロール
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 前へボタン
                IconButton(
                    onClick = onPreviousPressed,
                    enabled = currentIndex > 0
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIos,
                        contentDescription = "前へ",
                        tint = if (currentIndex > 0) YoinColors.TextSecondary else YoinColors.SurfaceVariant,
                        modifier = Modifier.size(YoinSizes.iconMedium)
                    )
                }

                Spacer(modifier = Modifier.width(YoinSpacing.lg))

                // ドットインジケーター
                Row(
                    horizontalArrangement = Arrangement.spacedBy(YoinSpacing.xs + 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(YoinSpacing.xs + 2.dp)
                            .background(
                                if (currentIndex > 0) YoinColors.SurfaceVariant else YoinColors.TextPrimary,
                                CircleShape
                            )
                    )
                    Box(
                        modifier = Modifier
                            .size(YoinSpacing.xs + 2.dp)
                            .background(YoinColors.TextPrimary, CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(YoinSpacing.xs + 2.dp)
                            .background(
                                if (currentIndex < totalPhotos - 1) YoinColors.SurfaceVariant else YoinColors.TextPrimary,
                                CircleShape
                            )
                    )
                }

                Spacer(modifier = Modifier.width(YoinSpacing.lg))

                // 次へボタン
                IconButton(
                    onClick = onNextPressed,
                    enabled = currentIndex < totalPhotos - 1
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForwardIos,
                        contentDescription = "次へ",
                        tint = if (currentIndex < totalPhotos - 1) YoinColors.TextSecondary else YoinColors.SurfaceVariant,
                        modifier = Modifier.size(YoinSizes.iconMedium)
                    )
                }
            }

            // カウンター
            Text(
                text = "${currentIndex + 1} / $totalPhotos",
                fontSize = YoinFontSizes.labelSmall.value.sp,
                color = YoinColors.TextSecondary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // ホームインジケーター
            Box(
                modifier = Modifier
                    .width(134.dp)
                    .height(5.dp)
                    .background(Color.Black, RoundedCornerShape(100.dp))
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}
