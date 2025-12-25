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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Warning
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
import com.yoin.feature.camera.viewmodel.PhotoConfirmContract
import com.yoin.feature.camera.viewmodel.PhotoConfirmViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * 写真確認画面
 *
 * 撮影した写真のプレビューと保存確認を行う
 * Yoinの特徴として、一度撮影したら撮り直しはできない
 *
 * @param photoPath 撮影した写真のパス
 * @param tripId 旅行ID
 * @param viewModel PhotoConfirmViewModel
 * @param onNavigateBack 戻るコールバック
 * @param onNavigateToRoomDetail ルーム詳細画面への遷移コールバック
 */
@Composable
fun PhotoConfirmScreen(
    photoPath: String,
    tripId: String,
    viewModel: PhotoConfirmViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToRoomDetail: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is PhotoConfirmContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is PhotoConfirmContract.Effect.ShowSuccess -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is PhotoConfirmContract.Effect.NavigateToCamera -> {
                    onNavigateBack()
                }
                is PhotoConfirmContract.Effect.NavigateToRoomDetail -> {
                    onNavigateToRoomDetail()
                }
                is PhotoConfirmContract.Effect.ShowConfirmDialog -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    // 画面表示時の初期化
    LaunchedEffect(photoPath, tripId) {
        viewModel.onIntent(PhotoConfirmContract.Intent.OnScreenDisplayed(photoPath, tripId))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1F2937)) // ダークグレー背景
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = YoinColors.Primary
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // ヘッダー
                PhotoConfirmHeader(
                    onCloseClick = {
                        viewModel.onIntent(PhotoConfirmContract.Intent.OnClosePressed)
                    }
                )

                // 写真プレビュー
                PhotoPreview(
                    photoPath = state.photoPath,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                // 写真情報
                PhotoInfo(
                    location = state.location,
                    timestamp = state.timestamp
                )

                // アクションボタン
                PhotoActions(
                    isSaving = state.isSaving,
                    onSaveClick = {
                        viewModel.onIntent(PhotoConfirmContract.Intent.OnSavePressed)
                    },
                    onDeleteClick = {
                        viewModel.onIntent(PhotoConfirmContract.Intent.OnDeletePressed)
                    }
                )

                // 警告メッセージ
                WarningMessage()

                Spacer(modifier = Modifier.height(YoinSpacing.sm))
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
 * 写真確認ヘッダー
 */
@Composable
private fun PhotoConfirmHeader(
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

        // タイトル（右）
        Surface(
            modifier = Modifier.align(Alignment.CenterEnd),
            color = Color(0xFF374151),
            shape = RoundedCornerShape(YoinSpacing.xl)
        ) {
            Text(
                text = "確認",
                fontSize = YoinFontSizes.labelLarge.value.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = YoinSpacing.md, vertical = YoinSpacing.sm)
            )
        }
    }
}

/**
 * 写真プレビュー
 */
@Composable
private fun PhotoPreview(
    photoPath: String,
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
        // TODO: 実際の写真を表示する実装を追加
        Surface(
            color = Color(0xFF374151),
            shape = RoundedCornerShape(YoinSpacing.sm),
            modifier = Modifier.padding(YoinSpacing.lg)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = YoinSpacing.xxl, vertical = YoinSpacing.md),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(YoinSpacing.sm)
            ) {
                Icon(
                    imageVector = Icons.Filled.PhotoCamera,
                    contentDescription = "写真",
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(YoinSizes.logoMedium)
                )
                Text(
                    text = "撮影した写真",
                    fontSize = YoinFontSizes.labelLarge.value.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}

/**
 * 写真情報
 */
@Composable
private fun PhotoInfo(
    location: String?,
    timestamp: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF111827))
            .padding(horizontal = YoinSpacing.lg, vertical = YoinSpacing.md),
        verticalArrangement = Arrangement.spacedBy(YoinSpacing.sm)
    ) {
        // 位置情報
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
                text = location ?: "位置情報なし",
                fontSize = YoinFontSizes.labelLarge.value.sp,
                color = Color.White
            )
        }

        // 撮影時刻
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(YoinSpacing.xs)
        ) {
            Icon(
                imageVector = Icons.Filled.AccessTime,
                contentDescription = "時刻",
                tint = Color.White,
                modifier = Modifier.size(YoinSizes.iconSmall)
            )
            Text(
                text = timestamp,
                fontSize = YoinFontSizes.labelLarge.value.sp,
                color = Color.White
            )
        }
    }
}

/**
 * アクションボタン
 */
@Composable
private fun PhotoActions(
    isSaving: Boolean,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF111827))
            .padding(horizontal = YoinSpacing.lg, vertical = YoinSpacing.lg),
        horizontalArrangement = Arrangement.spacedBy(YoinSpacing.md)
    ) {
        // 削除ボタン
        OutlinedButton(
            onClick = onDeleteClick,
            modifier = Modifier
                .weight(1f)
                .height(YoinSizes.buttonHeightLarge),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFFEF4444)
            ),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFEF4444)),
            shape = RoundedCornerShape(YoinSpacing.md),
            enabled = !isSaving
        ) {
            Text(
                text = "削除",
                fontSize = YoinFontSizes.bodyMedium.value.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 保存ボタン
        Button(
            onClick = onSaveClick,
            modifier = Modifier
                .weight(1f)
                .height(YoinSizes.buttonHeightLarge),
            colors = ButtonDefaults.buttonColors(
                containerColor = YoinColors.Primary,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(YoinSpacing.md),
            enabled = !isSaving
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(YoinSizes.iconMedium),
                    color = Color.White
                )
            } else {
                Text(
                    text = "保存",
                    fontSize = YoinFontSizes.bodyMedium.value.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
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
                text = "保存後は削除できません",
                fontSize = YoinFontSizes.labelSmall.value.sp,
                color = Color(0xFF9CA3AF),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(YoinSpacing.xs))

        Text(
            text = "よく確認してから保存しましょう",
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
