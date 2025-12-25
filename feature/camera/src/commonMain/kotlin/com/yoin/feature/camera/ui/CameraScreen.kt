package com.yoin.feature.camera.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
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

            Spacer(modifier = Modifier.height(8.dp))
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
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ステータスバー時刻（中央）
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "9:41",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = Color.White
                )
            }
        }

        // 閉じるボタン（左）
        Surface(
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.CenterStart),
            color = Color(0xFF374151),
            shape = CircleShape
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = onCloseClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✕",
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        }

        // 残り枚数（右）
        Surface(
            modifier = Modifier
                .align(Alignment.CenterEnd),
            color = Color(0xFF374151),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = "残り ${remainingPhotos}枚",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
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
            .padding(16.dp)
            .border(
                width = 2.dp,
                color = Color(0xFF6B7280),
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        // プレースホルダー
        Surface(
            color = Color(0xFF374151),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "カメラプレビュー",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "📍",
                fontSize = 16.sp
            )
            Text(
                text = location ?: "位置情報を取得中...",
                fontSize = 14.sp,
                color = Color.White
            )
        }

        if (isLoading) {
            Text(
                text = "取得中...",
                fontSize = 12.sp,
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
            .padding(vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // フラッシュボタン
            ControlButton(
                icon = "⚡",
                onClick = onFlashClick
            )

            // シャッターボタン
            ShutterButton(onClick = onShutterClick)

            // カメラ切り替えボタン
            ControlButton(
                icon = "🔄",
                onClick = onSwitchClick
            )
        }
    }
}

/**
 * コントロールボタン
 */
@Composable
private fun ControlButton(
    icon: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.size(52.dp),
        color = Color(0xFF374151),
        shape = CircleShape
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 20.sp
            )
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
            .size(80.dp)
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
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider(
            color = Color(0xFF374151),
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Text(
            text = "⚠️ 撮り直しはできません",
            fontSize = 12.sp,
            color = Color(0xFF9CA3AF),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "大切に1枚を撮りましょう",
            fontSize = 12.sp,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ホームインジケーター
        Box(
            modifier = Modifier
                .width(134.dp)
                .height(4.dp)
                .background(Color(0xFF374151), RoundedCornerShape(100.dp))
        )
    }
}
