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
                    .height(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "9:41",
                    fontSize = 14.sp,
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
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 戻るボタン
                Text(
                    text = "←",
                    fontSize = 20.sp,
                    color = YoinColors.TextPrimary,
                    modifier = Modifier.clickable(onClick = onBackPressed)
                )

                // タイトルと情報
                Column(
                    modifier = Modifier.weight(1f).padding(start = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = roomInfo.emoji,
                            fontSize = 18.sp
                        )
                        Text(
                            text = roomInfo.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = YoinColors.TextPrimary
                        )
                    }
                    Text(
                        text = "${roomInfo.dateRange} • ${roomInfo.location}",
                        fontSize = 12.sp,
                        color = YoinColors.TextSecondary
                    )
                    Text(
                        text = "${roomInfo.photoCount}枚 • ${roomInfo.memberCount}人",
                        fontSize = 11.sp,
                        color = YoinColors.TextSecondary
                    )
                }

                // 一括ダウンロードボタン
                Button(
                    onClick = onDownloadAllPressed,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YoinColors.Primary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "⬇ 全保存",
                        fontSize = 12.sp,
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
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
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
            .height(40.dp)
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
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
                fontSize = 16.sp,
                color = YoinColors.TextSecondary
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = YoinColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // 撮影者情報
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // アバター
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(YoinColors.AccentLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = photo.photographerName.take(1),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = YoinColors.TextPrimary
                        )
                    }

                    Column {
                        Text(
                            text = "📷 ${photo.photographerName}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = YoinColors.TextPrimary
                        )
                        Text(
                            text = photo.timestamp,
                            fontSize = 11.sp,
                            color = YoinColors.TextSecondary
                        )
                    }
                }

                // ダウンロードボタン
                IconButton(
                    onClick = onDownloadClicked,
                    modifier = Modifier.size(32.dp)
                ) {
                    Text(
                        text = if (photo.isDownloaded) "✓" else "⬇",
                        fontSize = 16.sp,
                        color = if (photo.isDownloaded) YoinColors.AccentMedium else YoinColors.Primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 写真プレースホルダー
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .background(YoinColors.SurfaceVariant, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "📸",
                        fontSize = 48.sp
                    )
                    Text(
                        text = "Photo #${photo.id}",
                        fontSize = 12.sp,
                        color = YoinColors.TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 撮影場所
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "📍",
                    fontSize = 14.sp
                )
                Text(
                    text = photo.location,
                    fontSize = 13.sp,
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "🗺️",
                fontSize = 64.sp
            )
            Text(
                text = "マップビュー",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary
            )
            Text(
                text = "写真の撮影場所を地図上に表示します",
                fontSize = 14.sp,
                color = YoinColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "（次のバージョンで実装予定）",
                fontSize = 12.sp,
                color = YoinColors.TextSecondary
            )
        }
    }
}
