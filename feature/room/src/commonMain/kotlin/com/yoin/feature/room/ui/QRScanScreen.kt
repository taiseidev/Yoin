package com.yoin.feature.room.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.design.theme.YoinSizes
import com.yoin.core.design.theme.YoinFontSizes
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.room.viewmodel.QRScanContract
import com.yoin.feature.room.viewmodel.QRScanViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * QRスキャン画面
 *
 * 機能:
 * - QRコードのカメラスキャン
 * - トーチ（ライト）の切り替え
 * - 画像からのQRコード読み取り
 * - 手動入力への遷移
 *
 * @param viewModel QRScanViewModel
 * @param onNavigateBack 戻るボタンのコールバック
 * @param onNavigateToJoinConfirm 参加確認画面への遷移コールバック
 * @param onNavigateToManualInput 手動入力画面への遷移コールバック
 */
@Composable
fun QRScanScreen(
    viewModel: QRScanViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToJoinConfirm: (String) -> Unit = {},
    onNavigateToManualInput: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is QRScanContract.Effect.NavigateBack -> onNavigateBack()
                is QRScanContract.Effect.NavigateToJoinConfirm -> onNavigateToJoinConfirm(effect.roomId)
                is QRScanContract.Effect.NavigateToManualInput -> onNavigateToManualInput()
                is QRScanContract.Effect.RequestCameraPermission -> {
                    // TODO: プラットフォーム固有のカメラ権限リクエスト処理
                    // 現在はモックとして権限付与済みにする
                    viewModel.onIntent(QRScanContract.Intent.OnCameraPermissionGranted)
                }

                is QRScanContract.Effect.OpenImagePicker -> {
                    // TODO: プラットフォーム固有の画像ピッカー処理
                    snackbarHostState.showSnackbar("画像選択機能は未実装です")
                }

                is QRScanContract.Effect.EnableTorch -> {
                    // TODO: プラットフォーム固有のトーチ有効化処理
                }

                is QRScanContract.Effect.DisableTorch -> {
                    // TODO: プラットフォーム固有のトーチ無効化処理
                }

                is QRScanContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    // 画面表示時の初期化
    LaunchedEffect(Unit) {
        viewModel.onIntent(QRScanContract.Intent.OnScreenDisplayed)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // カメラプレビュー（プラットフォーム固有の実装が必要）
        CameraPreviewPlaceholder()

        // オーバーレイ
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ヘッダー
            QRScanHeader(
                onClosePressed = {
                    viewModel.onIntent(QRScanContract.Intent.OnClosePressed)
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            // スキャンフレーム
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = YoinSpacing.huge),
                contentAlignment = Alignment.Center
            ) {
                ScanFrame(isScanning = state.isScanning)
            }

            Spacer(modifier = Modifier.height(YoinSpacing.xxl))

            // 説明テキスト
            Text(
                text = "招待されたQRコードを枠内に収めてください",
                fontSize = YoinFontSizes.labelLarge.value.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = YoinSpacing.xxxl)
            )

            Spacer(modifier = Modifier.weight(1f))

            // コントロールボタン
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = YoinSpacing.huge, vertical = YoinSpacing.lg),
                horizontalArrangement = Arrangement.spacedBy(YoinSpacing.lg)
            ) {
                ControlButton(
                    text = "ライト",
                    isActive = state.isTorchEnabled,
                    onClick = {
                        viewModel.onIntent(QRScanContract.Intent.OnTorchToggled)
                    },
                    modifier = Modifier.weight(1f)
                )

                ControlButton(
                    text = "画像から",
                    isActive = false,
                    onClick = {
                        viewModel.onIntent(QRScanContract.Intent.OnImagePickerPressed)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(YoinSpacing.lg))

            // 手動入力ボタン
            TextButton(
                onClick = {
                    viewModel.onIntent(QRScanContract.Intent.OnManualInputPressed)
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "コードを手入力する",
                    fontSize = YoinFontSizes.labelLarge.value.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(YoinSpacing.sm))

            // ホームインジケーター
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = YoinSpacing.lg)
                    .width(134.dp)
                    .height(YoinSpacing.xs)
                    .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(100.dp))
            )
        }

        // スナックバー
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * カメラプレビューのプレースホルダー
 * 実際の実装ではプラットフォーム固有のカメラプレビューを表示
 */
@Composable
private fun CameraPreviewPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(YoinColors.TextPrimary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "カメラプレビュー\n（プラットフォーム固有の実装が必要）",
            fontSize = YoinFontSizes.labelLarge.value.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * QRスキャンヘッダー
 */
@Composable
private fun QRScanHeader(onClosePressed: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = YoinSpacing.massive, start = YoinSpacing.lg, end = YoinSpacing.lg, bottom = YoinSpacing.lg)
    ) {
        // 閉じるボタン
        IconButton(
            onClick = onClosePressed,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "閉じる",
                tint = Color.White,
                modifier = Modifier.size(YoinSizes.iconLarge)
            )
        }

        // タイトル
        Text(
            text = "QRコードをスキャン",
            fontSize = YoinFontSizes.headingSmall.value.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

/**
 * スキャンフレーム
 */
@Composable
private fun ScanFrame(isScanning: Boolean) {
    val infiniteTransition = rememberInfiniteTransition()
    val scanLinePosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        // スキャンフレームの枠
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, YoinColors.Primary, RoundedCornerShape(16.dp))
        ) {
            // スキャンライン
            if (isScanning) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = (scanLinePosition * 280).dp)
                        .background(YoinColors.Primary)
                )
            }
        }

        // 四隅の緑のコーナー
        val cornerSize = 24.dp
        val cornerThickness = 4.dp

        // 左上
        TopLeftCorner(
            size = cornerSize,
            thickness = cornerThickness,
            color = YoinColors.Primary,
            modifier = Modifier.align(Alignment.TopStart)
        )

        // 右上
        TopRightCorner(
            size = cornerSize,
            thickness = cornerThickness,
            color = YoinColors.Primary,
            modifier = Modifier.align(Alignment.TopEnd)
        )

        // 左下
        BottomLeftCorner(
            size = cornerSize,
            thickness = cornerThickness,
            color = YoinColors.Primary,
            modifier = Modifier.align(Alignment.BottomStart)
        )

        // 右下
        BottomRightCorner(
            size = cornerSize,
            thickness = cornerThickness,
            color = YoinColors.Primary,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

/**
 * 左上コーナー
 */
@Composable
private fun TopLeftCorner(size: Dp, thickness: Dp, color: Color, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(size)) {
        // 横線
        Box(
            modifier = Modifier
                .width(size)
                .height(thickness)
                .background(color)
                .align(Alignment.TopStart)
        )
        // 縦線
        Box(
            modifier = Modifier
                .width(thickness)
                .height(size)
                .background(color)
                .align(Alignment.TopStart)
        )
    }
}

/**
 * 右上コーナー
 */
@Composable
private fun TopRightCorner(size: Dp, thickness: Dp, color: Color, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(size)) {
        // 横線
        Box(
            modifier = Modifier
                .width(size)
                .height(thickness)
                .background(color)
                .align(Alignment.TopEnd)
        )
        // 縦線
        Box(
            modifier = Modifier
                .width(thickness)
                .height(size)
                .background(color)
                .align(Alignment.TopEnd)
        )
    }
}

/**
 * 左下コーナー
 */
@Composable
private fun BottomLeftCorner(size: Dp, thickness: Dp, color: Color, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(size)) {
        // 横線
        Box(
            modifier = Modifier
                .width(size)
                .height(thickness)
                .background(color)
                .align(Alignment.BottomStart)
        )
        // 縦線
        Box(
            modifier = Modifier
                .width(thickness)
                .height(size)
                .background(color)
                .align(Alignment.BottomStart)
        )
    }
}

/**
 * 右下コーナー
 */
@Composable
private fun BottomRightCorner(
    size: Dp,
    thickness: Dp,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.size(size)) {
        // 横線
        Box(
            modifier = Modifier
                .width(size)
                .height(thickness)
                .background(color)
                .align(Alignment.BottomEnd)
        )
        // 縦線
        Box(
            modifier = Modifier
                .width(thickness)
                .height(size)
                .background(color)
                .align(Alignment.BottomEnd)
        )
    }
}

/**
 * コントロールボタン
 */
@Composable
private fun ControlButton(
    text: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isActive) YoinColors.Primary else Color.White.copy(alpha = 0.2f)
    val textColor = YoinColors.OnPrimary

    Button(
        onClick = onClick,
        modifier = modifier.height(YoinSizes.buttonHeightMedium),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor
        ),
        shape = RoundedCornerShape(YoinSpacing.sm)
    ) {
        Text(
            text = text,
            fontSize = YoinFontSizes.labelLarge.value.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * プレビュー
 */
@PhonePreview
@Composable
private fun QRScanScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Text(
                text = "QR Scan Screen Preview",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
