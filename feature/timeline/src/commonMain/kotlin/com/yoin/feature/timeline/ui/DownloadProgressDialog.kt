package com.yoin.feature.timeline.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.timeline.viewmodel.DownloadProgressContract
import com.yoin.feature.timeline.viewmodel.DownloadProgressViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * ダウンロード進捗ダイアログ
 *
 * 機能:
 * - ダウンロード進捗の表示
 * - 進捗バーの更新
 * - 残り時間の表示
 * - ダウンロードのキャンセル
 *
 * @param viewModel DownloadProgressViewModel
 * @param onDismiss ダイアログを閉じるコールバック
 */
@Composable
fun DownloadProgressDialog(
    viewModel: DownloadProgressViewModel,
    onDismiss: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is DownloadProgressContract.Effect.CancelDownload -> {
                    // キャンセル処理は既にViewModelで完了
                }
                is DownloadProgressContract.Effect.DismissDialog -> {
                    onDismiss()
                }
                is DownloadProgressContract.Effect.ShowDownloadComplete -> {
                    // 完了メッセージは親画面で表示
                }
                is DownloadProgressContract.Effect.ShowError -> {
                    // エラーメッセージは親画面で表示
                }
            }
        }
    }

    // ダイアログ表示時の初期化
    LaunchedEffect(Unit) {
        viewModel.onIntent(DownloadProgressContract.Intent.OnDialogDisplayed)
    }

    Dialog(onDismissRequest = {}) {
        Surface(
            modifier = Modifier
                .width(320.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            color = YoinColors.Surface
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // アイコン
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(YoinColors.AccentLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📥",
                        fontSize = 36.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // タイトル
                Text(
                    text = "写真をダウンロード中...",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 進捗バー
                LinearProgressIndicator(
                    progress = { state.progressFloat },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = YoinColors.Primary,
                    trackColor = YoinColors.SurfaceVariant,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 進捗テキスト
                Text(
                    text = state.progressText,
                    fontSize = 14.sp,
                    color = YoinColors.TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 残り時間
                if (state.estimatedSeconds > 0) {
                    Text(
                        text = state.estimatedTimeText,
                        fontSize = 12.sp,
                        color = YoinColors.TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // キャンセルボタン
                TextButton(
                    onClick = {
                        viewModel.onIntent(DownloadProgressContract.Intent.OnCancelPressed)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = YoinColors.TextSecondary
                    )
                ) {
                    Text(
                        text = "キャンセル",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * プレビュー
 */
@PhonePreview
@Composable
private fun DownloadProgressDialogPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x80000000)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .width(320.dp)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(20.dp),
                color = YoinColors.Surface
            ) {
                Column(
                    modifier = Modifier
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(YoinColors.AccentLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📥", fontSize = 36.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "写真をダウンロード中...",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.TextPrimary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    LinearProgressIndicator(
                        progress = { 0.6f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = YoinColors.Primary,
                        trackColor = YoinColors.SurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "29 / 48 枚完了",
                        fontSize = 14.sp,
                        color = YoinColors.TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "残り約 30 秒",
                        fontSize = 12.sp,
                        color = YoinColors.TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    TextButton(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = YoinColors.TextSecondary
                        )
                    ) {
                        Text(
                            text = "キャンセル",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
