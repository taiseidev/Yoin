package com.yoin.feature.timeline.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
            // ステータスバー風の時刻表示
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .padding(top = 16.dp, bottom = 8.dp)
            ) {
                Text(
                    text = "9:41",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = YoinColors.Surface,
                    letterSpacing = (-0.15).sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // ヘッダーコントロール
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 戻るボタン
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(YoinColors.Surface.copy(alpha = 0.2f))
                        .clickable(onClick = onBackPressed),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "←",
                        fontSize = 20.sp,
                        color = YoinColors.Surface
                    )
                }

                // ダウンロードボタン
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(YoinColors.Surface.copy(alpha = 0.2f))
                        .clickable(onClick = onDownloadPressed),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📥",
                        fontSize = 16.sp
                    )
                }
            }
        }

        // 日付の透かし（右下）
        Text(
            text = photo.dateWatermark,
            fontSize = 12.sp,
            color = YoinColors.Surface,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 40.dp, bottom = 16.dp)
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
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = YoinColors.Surface,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ハンドルバー
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(YoinColors.SurfaceVariant, RoundedCornerShape(2.dp))
                    .align(Alignment.CenterHorizontally)
            )

            // 撮影者情報
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // アバター
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(YoinColors.AccentLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = photo.photographerInitial,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.TextPrimary
                    )
                }

                Column {
                    Text(
                        text = photo.photographerName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.TextPrimary
                    )
                    Text(
                        text = "撮影者",
                        fontSize = 13.sp,
                        color = YoinColors.TextSecondary
                    )
                }
            }

            HorizontalDivider(color = YoinColors.SurfaceVariant, thickness = 0.65.dp)

            // 日時情報
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📅",
                    fontSize = 18.sp
                )
                Text(
                    text = photo.dateTime,
                    fontSize = 14.sp,
                    color = YoinColors.TextPrimary
                )
            }

            // 位置情報
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📍",
                        fontSize = 18.sp
                    )
                    Text(
                        text = photo.location,
                        fontSize = 14.sp,
                        color = YoinColors.TextPrimary
                    )
                }
                Text(
                    text = photo.subLocation,
                    fontSize = 12.sp,
                    color = YoinColors.TextSecondary,
                    modifier = Modifier.padding(start = 26.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                    Text(
                        text = "←",
                        fontSize = 16.sp,
                        color = if (currentIndex > 0) YoinColors.TextSecondary else YoinColors.SurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // ドットインジケーター
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(
                                if (currentIndex > 0) YoinColors.SurfaceVariant else YoinColors.TextPrimary,
                                CircleShape
                            )
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(YoinColors.TextPrimary, CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(
                                if (currentIndex < totalPhotos - 1) YoinColors.SurfaceVariant else YoinColors.TextPrimary,
                                CircleShape
                            )
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // 次へボタン
                IconButton(
                    onClick = onNextPressed,
                    enabled = currentIndex < totalPhotos - 1
                ) {
                    Text(
                        text = "→",
                        fontSize = 16.sp,
                        color = if (currentIndex < totalPhotos - 1) YoinColors.TextSecondary else YoinColors.SurfaceVariant
                    )
                }
            }

            // カウンター
            Text(
                text = "${currentIndex + 1} / $totalPhotos",
                fontSize = 12.sp,
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
