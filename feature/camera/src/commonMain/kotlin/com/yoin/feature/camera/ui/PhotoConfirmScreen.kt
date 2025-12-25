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

                Spacer(modifier = Modifier.height(8.dp))
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

        // タイトル（右）
        Surface(
            modifier = Modifier.align(Alignment.CenterEnd),
            color = Color(0xFF374151),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = "確認",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
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
        // TODO: 実際の写真を表示する実装を追加
        Surface(
            color = Color(0xFF374151),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "📷",
                    fontSize = 48.sp
                )
                Text(
                    text = "撮影した写真",
                    fontSize = 14.sp,
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 位置情報
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "📍",
                fontSize = 16.sp
            )
            Text(
                text = location ?: "位置情報なし",
                fontSize = 14.sp,
                color = Color.White
            )
        }

        // 撮影時刻
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "🕐",
                fontSize = 16.sp
            )
            Text(
                text = timestamp,
                fontSize = 14.sp,
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
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 削除ボタン
        OutlinedButton(
            onClick = onDeleteClick,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFFEF4444)
            ),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFEF4444)),
            shape = RoundedCornerShape(12.dp),
            enabled = !isSaving
        ) {
            Text(
                text = "削除",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 保存ボタン
        Button(
            onClick = onSaveClick,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = YoinColors.Primary,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = !isSaving
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text(
                    text = "保存",
                    fontSize = 16.sp,
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
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider(
            color = Color(0xFF374151),
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Text(
            text = "⚠️ 保存後は削除できません",
            fontSize = 12.sp,
            color = Color(0xFF9CA3AF),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "よく確認してから保存しましょう",
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
