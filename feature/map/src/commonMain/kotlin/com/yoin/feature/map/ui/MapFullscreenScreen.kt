package com.yoin.feature.map.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.map.model.MapMember
import com.yoin.feature.map.model.PhotoLocation
import com.yoin.feature.map.viewmodel.MapFullscreenContract
import com.yoin.feature.map.viewmodel.MapFullscreenViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * 地図フルスクリーン画面
 *
 * 機能:
 * - 地図上に写真の位置をピンで表示
 * - メンバーフィルター
 * - ズーム操作
 * - 写真詳細カード表示
 *
 * @param viewModel MapFullscreenViewModel
 * @param onNavigateBack 戻るボタンのコールバック
 * @param onNavigateToPhotoDetail 写真詳細画面への遷移コールバック
 */
@Composable
fun MapFullscreenScreen(
    viewModel: MapFullscreenViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToPhotoDetail: (String, String) -> Unit = { _, _ -> }
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is MapFullscreenContract.Effect.NavigateBack -> onNavigateBack()
                is MapFullscreenContract.Effect.ShowMenu -> {
                    snackbarHostState.showSnackbar("メニュー機能は未実装です")
                }
                is MapFullscreenContract.Effect.NavigateToPhotoDetail -> {
                    onNavigateToPhotoDetail(effect.roomId, effect.photoId)
                }
                is MapFullscreenContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is MapFullscreenContract.Effect.MoveToCurrentLocation -> {
                    snackbarHostState.showSnackbar("現在地機能は未実装です")
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(YoinColors.Background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ステータスバー風の時刻表示
            Text(
                text = "9:41",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = YoinColors.TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ヘッダー
            MapHeader(
                title = state.roomTitle,
                onBackPressed = {
                    viewModel.handleIntent(MapFullscreenContract.Intent.OnBackPressed)
                },
                onMenuPressed = {
                    viewModel.handleIntent(MapFullscreenContract.Intent.OnMenuPressed)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // メンバーフィルター
            MemberFilterRow(
                members = state.members,
                selectedMemberId = state.selectedMemberId,
                onMemberSelected = { memberId ->
                    viewModel.handleIntent(MapFullscreenContract.Intent.OnMemberFilterSelected(memberId))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 地図エリア
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                // 地図のモックアップ（実際の実装では地図ライブラリを使用）
                MapMockup(
                    photos = state.filteredPhotos,
                    selectedPhotoId = state.selectedPhotoId,
                    onPhotoMarkerTapped = { photoId ->
                        viewModel.handleIntent(MapFullscreenContract.Intent.OnPhotoMarkerTapped(photoId))
                    }
                )

                // 地図コントロール
                MapControls(
                    onZoomInPressed = {
                        viewModel.handleIntent(MapFullscreenContract.Intent.OnZoomInPressed)
                    },
                    onZoomOutPressed = {
                        viewModel.handleIntent(MapFullscreenContract.Intent.OnZoomOutPressed)
                    },
                    onCurrentLocationPressed = {
                        viewModel.handleIntent(MapFullscreenContract.Intent.OnCurrentLocationPressed)
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                )

                // 写真枚数バッジ
                PhotoCountBadge(
                    count = state.totalPhotoCount,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, bottom = 120.dp)
                )

                // 写真詳細カード
                state.selectedPhoto?.let { photo ->
                    PhotoDetailCard(
                        photo = photo,
                        onClick = {
                            viewModel.handleIntent(MapFullscreenContract.Intent.OnPhotoCardTapped)
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    )
                }
            }
        }

        // ホームインジケーター
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .width(134.dp)
                .height(5.dp)
                .background(Color.Black, RoundedCornerShape(100.dp))
        )

        // スナックバー
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * 地図ヘッダー
 */
@Composable
private fun MapHeader(
    title: String,
    onBackPressed: () -> Unit,
    onMenuPressed: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = YoinColors.OnPrimary,
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 戻るボタン
            Text(
                text = "←",
                fontSize = 20.sp,
                color = YoinColors.TextPrimary,
                modifier = Modifier.clickable(onClick = onBackPressed)
            )

            // タイトル
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary
            )

            // メニューボタン
            Text(
                text = "...",
                fontSize = 20.sp,
                color = YoinColors.TextPrimary,
                modifier = Modifier.clickable(onClick = onMenuPressed)
            )
        }
    }
}

/**
 * メンバーフィルター行
 */
@Composable
private fun MemberFilterRow(
    members: List<MapMember>,
    selectedMemberId: String?,
    onMemberSelected: (String?) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 「全員」チップ
        item {
            MemberChip(
                label = "全員",
                isSelected = selectedMemberId == null,
                onClick = { onMemberSelected(null) }
            )
        }

        // メンバーチップ
        items(members) { member ->
            MemberAvatarChip(
                member = member,
                isSelected = selectedMemberId == member.id,
                onClick = { onMemberSelected(member.id) }
            )
        }
    }
}

/**
 * メンバーチップ
 */
@Composable
private fun MemberChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) YoinColors.Primary else Color.White
    val textColor = if (isSelected) Color.White else YoinColors.TextSecondary

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

/**
 * メンバーアバターチップ
 */
@Composable
private fun MemberAvatarChip(
    member: MapMember,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(YoinColors.AccentLight) // デフォルトのメンバーアバター色
            .clickable(onClick = onClick)
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, YoinColors.Primary, CircleShape)
                } else {
                    Modifier
                }
            )
    )
}

/**
 * 地図のモックアップ
 */
@Composable
private fun MapMockup(
    photos: List<PhotoLocation>,
    selectedPhotoId: String?,
    onPhotoMarkerTapped: (String) -> Unit
) {
    // TODO: 実際の地図ライブラリを使用して実装
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(YoinColors.AccentLight)
    ) {
        Text(
            text = "地図表示エリア\n（実装には地図ライブラリが必要）\n\n写真: ${photos.size}枚",
            fontSize = 14.sp,
            color = YoinColors.TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

/**
 * 地図コントロール
 */
@Composable
private fun MapControls(
    onZoomInPressed: () -> Unit,
    onZoomOutPressed: () -> Unit,
    onCurrentLocationPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // ズームインボタン
        ControlButton(
            text = "+",
            onClick = onZoomInPressed
        )

        // ズームアウトボタン
        ControlButton(
            text = "-",
            onClick = onZoomOutPressed
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 現在地ボタン
        ControlButton(
            text = "📍",
            onClick = onCurrentLocationPressed
        )
    }
}

/**
 * コントロールボタン
 */
@Composable
private fun ControlButton(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = YoinColors.OnPrimary,
        shadowElevation = 2.dp,
        modifier = Modifier
            .size(40.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary
            )
        }
    }
}

/**
 * 写真枚数バッジ
 */
@Composable
private fun PhotoCountBadge(
    count: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = CircleShape,
        color = YoinColors.Primary,
        modifier = modifier.size(60.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.OnPrimary
            )
        }
    }
}

/**
 * 写真詳細カード
 */
@Composable
private fun PhotoDetailCard(
    photo: PhotoLocation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = YoinColors.OnPrimary,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // サムネイル
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(YoinColors.Primary)
            )

            // 情報
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = photo.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )

                Text(
                    text = "${photo.photographer} • ${photo.timestamp}",
                    fontSize = 12.sp,
                    color = YoinColors.TextSecondary
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "📍",
                        fontSize = 12.sp
                    )
                    Text(
                        text = photo.location,
                        fontSize = 12.sp,
                        color = YoinColors.Error
                    )
                }
            }

            // シェブロン
            Text(
                text = "›",
                fontSize = 20.sp,
                color = YoinColors.TextSecondary
            )
        }
    }
}

/**
 * プレビュー
 */
@PhonePreview
@Composable
private fun MapFullscreenScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            Text("Map Fullscreen Screen Preview")
        }
    }
}
