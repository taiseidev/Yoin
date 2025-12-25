package com.yoin.feature.map.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.design.theme.YoinSizes
import com.yoin.core.design.theme.YoinFontSizes
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
            Spacer(modifier = Modifier.height(YoinSpacing.xxl + YoinSpacing.lg))

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

            Spacer(modifier = Modifier.height(YoinSpacing.lg))

            // メンバーフィルター
            MemberFilterRow(
                members = state.members,
                selectedMemberId = state.selectedMemberId,
                onMemberSelected = { memberId ->
                    viewModel.handleIntent(MapFullscreenContract.Intent.OnMemberFilterSelected(memberId))
                }
            )

            Spacer(modifier = Modifier.height(YoinSpacing.lg))

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
                        .padding(end = YoinSpacing.lg)
                )

                // 写真枚数バッジ
                PhotoCountBadge(
                    count = state.totalPhotoCount,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = YoinSpacing.lg, bottom = YoinSizes.logoLarge)
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
                            .padding(horizontal = YoinSpacing.lg, vertical = YoinSpacing.lg)
                    )
                }
            }
        }

        // ホームインジケーター
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = YoinSpacing.lg)
                .width(134.dp)
                .height(YoinSpacing.xs)
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
        shape = RoundedCornerShape(YoinSpacing.xxl)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = YoinSpacing.lg, vertical = YoinSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 戻るボタン
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "戻る",
                    tint = YoinColors.TextPrimary
                )
            }

            // タイトル
            Text(
                text = title,
                fontSize = YoinFontSizes.bodyMedium.value.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary
            )

            // メニューボタン
            IconButton(onClick = onMenuPressed) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "メニュー",
                    tint = YoinColors.TextPrimary
                )
            }
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
            .padding(horizontal = YoinSpacing.lg),
        horizontalArrangement = Arrangement.spacedBy(YoinSpacing.sm)
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
        shape = RoundedCornerShape(YoinSpacing.xl),
        color = backgroundColor,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            fontSize = YoinFontSizes.labelLarge.value.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            modifier = Modifier.padding(horizontal = YoinSpacing.lg, vertical = YoinSpacing.sm)
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
            .size(YoinSizes.buttonHeightSmall)
            .clip(CircleShape)
            .background(YoinColors.AccentPeach) // デフォルトのメンバーアバター色
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
            .background(YoinColors.AccentPeach)
    ) {
        Text(
            text = "地図表示エリア\n（実装には地図ライブラリが必要）\n\n写真: ${photos.size}枚",
            fontSize = YoinFontSizes.labelLarge.value.sp,
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
        verticalArrangement = Arrangement.spacedBy(YoinSpacing.sm)
    ) {
        // ズームインボタン
        Surface(
            shape = RoundedCornerShape(YoinSpacing.sm),
            color = YoinColors.OnPrimary,
            shadowElevation = 2.dp,
            modifier = Modifier
                .size(YoinSizes.buttonHeightSmall)
                .clickable(onClick = onZoomInPressed)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "ズームイン",
                    tint = YoinColors.TextPrimary,
                    modifier = Modifier.size(YoinSizes.iconMedium)
                )
            }
        }

        // ズームアウトボタン
        Surface(
            shape = RoundedCornerShape(YoinSpacing.sm),
            color = YoinColors.OnPrimary,
            shadowElevation = 2.dp,
            modifier = Modifier
                .size(YoinSizes.buttonHeightSmall)
                .clickable(onClick = onZoomOutPressed)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Remove,
                    contentDescription = "ズームアウト",
                    tint = YoinColors.TextPrimary,
                    modifier = Modifier.size(YoinSizes.iconMedium)
                )
            }
        }

        Spacer(modifier = Modifier.height(YoinSpacing.sm))

        // 現在地ボタン
        Surface(
            shape = RoundedCornerShape(YoinSpacing.sm),
            color = YoinColors.OnPrimary,
            shadowElevation = 2.dp,
            modifier = Modifier
                .size(YoinSizes.buttonHeightSmall)
                .clickable(onClick = onCurrentLocationPressed)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.MyLocation,
                    contentDescription = "現在地",
                    tint = YoinColors.TextPrimary,
                    modifier = Modifier.size(YoinSizes.iconMedium)
                )
            }
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
        modifier = modifier.size(YoinSizes.iconXLarge + YoinSpacing.md)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                fontSize = YoinFontSizes.headingSmall.value.sp,
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
        shape = RoundedCornerShape(YoinSpacing.lg),
        color = YoinColors.OnPrimary,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(YoinSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(YoinSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // サムネイル
            Box(
                modifier = Modifier
                    .size(YoinSizes.logoSmall)
                    .clip(RoundedCornerShape(YoinSpacing.sm))
                    .background(YoinColors.Primary)
            )

            // 情報
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(YoinSpacing.xs)
            ) {
                Text(
                    text = photo.title,
                    fontSize = YoinFontSizes.bodyMedium.value.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )

                Text(
                    text = "${photo.photographer} • ${photo.timestamp}",
                    fontSize = YoinFontSizes.labelSmall.value.sp,
                    color = YoinColors.TextSecondary
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(YoinSpacing.xs)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "位置",
                        tint = YoinColors.Error,
                        modifier = Modifier.size(YoinSizes.iconSmall)
                    )
                    Text(
                        text = photo.location,
                        fontSize = YoinFontSizes.labelSmall.value.sp,
                        color = YoinColors.Error
                    )
                }
            }

            // シェブロン
            Text(
                text = "›",
                fontSize = YoinFontSizes.headingSmall.value.sp,
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
