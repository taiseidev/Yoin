package com.yoin.feature.camera.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.camera.viewmodel.CameraContract
import com.yoin.feature.camera.viewmodel.CameraViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

/**
 * フィルムプリセット
 */
enum class FilmPreset(
    val displayName: String,
    val description: String,
    val colorTint: Color
) {
    VELVIA("Velvia", "鮮やかな色彩", Color(0xFFFF6B35)),
    PORTRA("Portra", "柔らかなトーン", Color(0xFFE8A598)),
    KODAK("Kodak Gold", "温かみのある", Color(0xFFFFB84D)),
    CLASSIC("Classic", "ビンテージ風", Color(0xFFB87F6A))
}

/**
 * カメラ画面 - インスタントカメラ風UI
 */
@Composable
fun CameraScreen(
    tripId: String,
    viewModel: CameraViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // フィルムプリセット
    var selectedPreset by remember { mutableStateOf(FilmPreset.VELVIA) }

    // シャッターアニメーション
    var isShutterAnimating by remember { mutableStateOf(false) }

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
                    // シャッターアニメーション
                    isShutterAnimating = true
                    delay(300)
                    isShutterAnimating = false
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
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ヘッダー
            InstantCameraHeader(
                remainingPhotos = state.remainingPhotos,
                onBackClick = {
                    viewModel.onIntent(CameraContract.Intent.OnClosePressed)
                }
            )

            // ビューファインダー
            ViewfinderPreview(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                selectedPreset = selectedPreset,
                location = state.location
            )

            // フィルムプリセット選択
            FilmPresetSelector(
                selectedPreset = selectedPreset,
                onPresetChange = { selectedPreset = it }
            )

            // カメラコントロール
            InstantCameraControls(
                onShutterClick = {
                    viewModel.onIntent(CameraContract.Intent.OnShutterPressed)
                }
            )

            // フィルムカメラ風の注意書き
            FilmCameraNotice()
        }

        // シャッターアニメーション
        AnimatedVisibility(
            visible = isShutterAnimating,
            enter = fadeIn(animationSpec = tween(100)),
            exit = fadeOut(animationSpec = tween(200))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
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
 * インスタントカメラ風ヘッダー
 */
@Composable
private fun InstantCameraHeader(
    remainingPhotos: Int,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 戻るボタン
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(40.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "戻る",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        // フィルムカウンター（写るんです風）
        FilmCounter(remainingPhotos = remainingPhotos)
    }
}

/**
 * フィルムカウンター - 写るんです風
 */
@Composable
private fun FilmCounter(remainingPhotos: Int) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black.copy(alpha = 0.7f))
            .border(2.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.PhotoCamera,
            contentDescription = null,
            tint = YoinColors.Primary,
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = "$remainingPhotos",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = Color.White,
            letterSpacing = 1.sp
        )

        Text(
            text = "shots",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.7f),
            letterSpacing = 0.5.sp
        )
    }
}

/**
 * ビューファインダープレビュー
 */
@Composable
private fun ViewfinderPreview(
    modifier: Modifier = Modifier,
    selectedPreset: FilmPreset,
    location: String?
) {
    Box(
        modifier = modifier
            .padding(16.dp)
    ) {
        // カメラプレビューエリア
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF2C2C2E)),
            contentAlignment = Alignment.Center
        ) {
            // プレースホルダー
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "Camera Preview",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.3f),
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // ビューファインダーフレーム（4隅）
        ViewfinderCorners()

        // フィルムプリセット表示
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
            color = Color.Black.copy(alpha = 0.7f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(selectedPreset.colorTint, CircleShape)
                )
                Text(
                    text = selectedPreset.displayName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // 位置情報表示
        if (!location.isNullOrEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = location,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * ビューファインダーの4隅フレーム
 */
@Composable
private fun BoxScope.ViewfinderCorners() {
    val cornerLength = 40.dp
    val cornerWidth = 3.dp
    val cornerColor = Color.White.copy(alpha = 0.6f)

    // 左上
    Box(
        modifier = Modifier
            .align(Alignment.TopStart)
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .width(cornerLength)
                .height(cornerWidth)
                .background(cornerColor)
        )
        Box(
            modifier = Modifier
                .width(cornerWidth)
                .height(cornerLength)
                .background(cornerColor)
        )
    }

    // 右上
    Box(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .width(cornerLength)
                .height(cornerWidth)
                .background(cornerColor)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .width(cornerWidth)
                .height(cornerLength)
                .background(cornerColor)
        )
    }

    // 左下
    Box(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .width(cornerLength)
                .height(cornerWidth)
                .background(cornerColor)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .width(cornerWidth)
                .height(cornerLength)
                .background(cornerColor)
        )
    }

    // 右下
    Box(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .width(cornerLength)
                .height(cornerWidth)
                .background(cornerColor)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .width(cornerWidth)
                .height(cornerLength)
                .background(cornerColor)
        )
    }
}

/**
 * フィルムプリセット選択
 */
@Composable
private fun FilmPresetSelector(
    selectedPreset: FilmPreset,
    onPresetChange: (FilmPreset) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Palette,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Film Preset",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = selectedPreset.description,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilmPreset.entries.forEach { preset ->
                FilmPresetChip(
                    preset = preset,
                    isSelected = preset == selectedPreset,
                    onClick = { onPresetChange(preset) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * フィルムプリセットチップ
 */
@Composable
private fun FilmPresetChip(
    preset: FilmPreset,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) {
                    Brush.verticalGradient(
                        colors = listOf(
                            preset.colorTint,
                            preset.colorTint.copy(alpha = 0.7f)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2C2C2E),
                            Color(0xFF1C1C1E)
                        )
                    )
                }
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = preset.displayName,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Monospace
        )
    }
}

/**
 * インスタントカメラコントロール
 */
@Composable
private fun InstantCameraControls(
    onShutterClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        // シャッターボタン（写るんです風）
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFFE0E0E0)
                        )
                    )
                )
                .clickable(onClick = onShutterClick),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                YoinColors.Primary,
                                YoinColors.PrimaryVariant
                            )
                        ),
                        CircleShape
                    )
            ) {
                // 内側の円
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                )
            }
        }
    }
}

/**
 * フィルムカメラ風の注意書き
 */
@Composable
private fun FilmCameraNotice() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = YoinColors.Primary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "撮り直しはできません",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 0.3.sp
            )
        }

        Text(
            text = "フィルムカメラのように、一枚一枚を大切に",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
    }
}

/**
 * プレビュー: カメラヘッダー
 */
@PhonePreview
@Composable
private fun InstantCameraHeaderPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            InstantCameraHeader(
                remainingPhotos = 24,
                onBackClick = {}
            )
        }
    }
}

/**
 * プレビュー: フィルムカウンター
 */
@PhonePreview
@Composable
private fun FilmCounterPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            FilmCounter(remainingPhotos = 24)
        }
    }
}

/**
 * プレビュー: フィルムプリセット選択
 */
@PhonePreview
@Composable
private fun FilmPresetSelectorPreview() {
    MaterialTheme {
        var selectedPreset by remember { mutableStateOf(FilmPreset.VELVIA) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            FilmPresetSelector(
                selectedPreset = selectedPreset,
                onPresetChange = { selectedPreset = it }
            )
        }
    }
}
