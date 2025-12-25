package com.yoin.feature.camera.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Refresh
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
import com.yoin.feature.camera.viewmodel.CameraContract
import com.yoin.feature.camera.viewmodel.CameraViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * カメラ画面
 *
 * @param tripId 旅行ID
 * @param viewModel CameraViewModel
 * @param onNavigateBack 戻るボタンのコールバック
 */
@Composable
fun CameraScreen(
    tripId: String,
    viewModel: CameraViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is CameraContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is CameraContract.Effect.NavigateBack -> {
                    onNavigateBack()
                }
                is CameraContract.Effect.PhotoCaptured -> {
                    // TODO: 写真保存の処理
                }
                is CameraContract.Effect.NavigateToPreview -> {
                    // TODO: プレビュー画面への遷移
                }
            }
        }
    }

    // 画面表示時の初期化
    LaunchedEffect(tripId) {
        viewModel.onIntent(CameraContract.Intent.OnScreenDisplayed(tripId))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1F2937)) // ダークグレー背景
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ヘッダー
            CameraHeader(
                remainingPhotos = state.remainingPhotos,
                onCloseClick = {
                    viewModel.onIntent(CameraContract.Intent.OnClosePressed)
                }
            )

            // カメラプレビュー領域
            CameraPreview(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            // 位置情報
            LocationSection(
                location = state.location,
                isLoading = state.isLocationLoading
            )

            // カメラコントロール
            CameraControls(
                flashMode = state.flashMode,
                onFlashClick = {
                    viewModel.onIntent(CameraContract.Intent.OnFlashToggle)
                },
                onShutterClick = {
                    viewModel.onIntent(CameraContract.Intent.OnShutterPressed)
                },
                onSwitchClick = {
                    viewModel.onIntent(CameraContract.Intent.OnCameraSwitch)
                }
            )

            // 警告メッセージ
            WarningMessage()

            Spacer(modifier = Modifier.height(YoinSpacing.sm))
        }

        // スナックバー
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * カメラヘッダー
 */
@Composable
private fun CameraHeader(
    remainingPhotos: Int,
    onCloseClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = YoinSpacing.lg, vertical = YoinSpacing.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 閉じるボタン（左）
            Surface(
                modifier = Modifier.size(YoinSizes.buttonHeightSmall),
                color = Color(0xFF374151),
                shape = CircleShape
            ) {
                IconButton(onClick = onCloseClick) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "閉じる",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 空のスペース
            Box(modifier = Modifier.size(YoinSizes.buttonHeightSmall))
        }

        // 残り枚数（右）
        Surface(
            modifier = Modifier
                .align(Alignment.CenterEnd),
            color = Color(0xFF374151),
            shape = RoundedCornerShape(YoinSpacing.xl)
        ) {
            Text(
                text = "残り ${remainingPhotos}枚",
                fontSize = YoinFontSizes.labelLarge.value.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = YoinSpacing.md, vertical = YoinSpacing.sm)
            )
        }
    }
}

/**
 * カメラプレビュー
 */
@Composable
private fun CameraPreview(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(YoinSpacing.lg)
            .border(
                width = 2.dp,
                color = Color(0xFF6B7280),
                shape = RoundedCornerShape(YoinSpacing.md)
            )
            .clip(RoundedCornerShape(YoinSpacing.md)),
        contentAlignment = Alignment.Center
    ) {
        // プレースホルダー
        Surface(
            color = Color(0xFF374151),
            shape = RoundedCornerShape(YoinSpacing.sm),
            modifier = Modifier.padding(YoinSpacing.lg)
        ) {
            Text(
                text = "カメラプレビュー",
                fontSize = YoinFontSizes.labelLarge.value.sp,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = YoinSpacing.xxl, vertical = YoinSpacing.md)
            )
        }
    }
}

/**
 * 位置情報セクション
 */
@Composable
private fun LocationSection(
    location: String?,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF111827))
            .padding(horizontal = YoinSpacing.lg, vertical = YoinSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(YoinSpacing.xs)
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "位置",
                tint = Color.White,
                modifier = Modifier.size(YoinSizes.iconSmall)
            )
            Text(
                text = location ?: "位置情報を取得中...",
                fontSize = YoinFontSizes.labelLarge.value.sp,
                color = Color.White
            )
        }

        if (isLoading) {
            Text(
                text = "取得中...",
                fontSize = YoinFontSizes.labelSmall.value.sp,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}

/**
 * カメラコントロール
 */
@Composable
private fun CameraControls(
    flashMode: CameraContract.FlashMode,
    onFlashClick: () -> Unit,
    onShutterClick: () -> Unit,
    onSwitchClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF111827))
            .padding(vertical = YoinSpacing.xxl)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // フラッシュボタン
            Surface(
                modifier = Modifier.size(YoinSizes.buttonHeightLarge),
                color = Color(0xFF374151),
                shape = CircleShape
            ) {
                IconButton(onClick = onFlashClick) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "フラッシュ",
                        tint = Color.White,
                        modifier = Modifier.size(YoinSizes.iconLarge)
                    )
                }
            }

            // シャッターボタン
            ShutterButton(onClick = onShutterClick)

            // カメラ切り替えボタン
            Surface(
                modifier = Modifier.size(YoinSizes.buttonHeightLarge),
                color = Color(0xFF374151),
                shape = CircleShape
            ) {
                IconButton(onClick = onSwitchClick) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "カメラ切り替え",
                        tint = Color.White,
                        modifier = Modifier.size(YoinSizes.iconLarge)
                    )
                }
            }
        }
    }
}

/**
 * シャッターボタン
 */
@Composable
private fun ShutterButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(YoinSizes.logoSmall)
            .background(Color.White, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .background(YoinColors.Primary, CircleShape)
        )
    }
}

/**
 * 警告メッセージ
 */
@Composable
private fun WarningMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF111827))
            .padding(horizontal = YoinSpacing.lg, vertical = YoinSpacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider(
            color = Color(0xFF374151),
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = YoinSpacing.md)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(YoinSpacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = "警告",
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(YoinSizes.iconSmall)
            )
            Text(
                text = "撮り直しはできません",
                fontSize = YoinFontSizes.labelSmall.value.sp,
                color = Color(0xFF9CA3AF),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(YoinSpacing.xs))

        Text(
            text = "大切に1枚を撮りましょう",
            fontSize = YoinFontSizes.labelSmall.value.sp,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(YoinSpacing.sm))

        // ホームインジケーター
        Box(
            modifier = Modifier
                .width(134.dp)
                .height(YoinSpacing.xs)
                .background(Color(0xFF374151), RoundedCornerShape(100.dp))
        )
    }
}
