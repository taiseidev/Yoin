package com.yoin.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinTheme
import com.yoin.core.ui.preview.PhonePreview

/**
 * Yoinアプリの標準ダイアログコンポーネント
 *
 * Modern Cinematic with Amber Accentデザインに基づいた確認ダイアログ
 * - 背景: Surface (#1C1C1E)
 * - 角丸: 16dp
 * - 幅: 最大280dp
 * - スクリム: #000000 60%透明度
 *
 * 使用例:
 * - 旅行削除確認
 * - 写真削除確認
 * - ログアウト確認
 * - エラーダイアログ
 *
 * @param title ダイアログタイトル
 * @param message ダイアログメッセージ（説明文）
 * @param confirmButtonText 確認ボタンのテキスト
 * @param dismissButtonText キャンセルボタンのテキスト
 * @param onConfirm 確認ボタン押下時のコールバック
 * @param onDismiss ダイアログ閉じる時のコールバック
 * @param isDestructive 破壊的アクション（削除など）の場合true（ボタンが赤色になる）
 * @param confirmEnabled 確認ボタンが有効かどうか
 */
@Composable
fun YoinDialog(
    title: String,
    message: String,
    confirmButtonText: String,
    dismissButtonText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isDestructive: Boolean = false,
    confirmEnabled: Boolean = true
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(16.dp),
            color = YoinColors.Surface,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // タイトル
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // メッセージ
                Text(
                    text = message,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = YoinColors.TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ボタン
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // キャンセルボタン
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = YoinColors.TextSecondary
                        )
                    ) {
                        Text(
                            text = dismissButtonText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // 確認ボタン
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        enabled = confirmEnabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDestructive) YoinColors.Error else YoinColors.Primary,
                            contentColor = if (isDestructive) Color.White else YoinColors.OnPrimary,
                            disabledContainerColor = YoinColors.SurfaceVariant,
                            disabledContentColor = YoinColors.TextTertiary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = confirmButtonText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

/**
 * シンプルな確認ダイアログ
 *
 * タイトルとメッセージ、OKボタンのみのシンプルなダイアログ
 *
 * @param title ダイアログタイトル
 * @param message ダイアログメッセージ
 * @param buttonText ボタンテキスト（デフォルト: "OK"）
 * @param onDismiss ダイアログ閉じる時のコールバック
 */
@Composable
fun YoinAlertDialog(
    title: String,
    message: String,
    buttonText: String = "OK",
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(16.dp),
            color = YoinColors.Surface,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // タイトル
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // メッセージ
                Text(
                    text = message,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = YoinColors.TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // OKボタン
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YoinColors.Primary,
                        contentColor = YoinColors.OnPrimary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = buttonText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// Previews

@PhonePreview
@Composable
private fun YoinDialogPreview() {
    YoinTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background),
            contentAlignment = Alignment.Center
        ) {
            YoinDialog(
                title = "旅行を削除しますか？",
                message = "この旅行に含まれるすべての写真も削除されます。この操作は取り消せません。",
                confirmButtonText = "削除",
                dismissButtonText = "キャンセル",
                onConfirm = {},
                onDismiss = {},
                isDestructive = true
            )
        }
    }
}

@PhonePreview
@Composable
private fun YoinDialogNormalPreview() {
    YoinTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background),
            contentAlignment = Alignment.Center
        ) {
            YoinDialog(
                title = "ログアウトしますか？",
                message = "再度ログインするにはメールアドレスとパスワードが必要です。",
                confirmButtonText = "ログアウト",
                dismissButtonText = "キャンセル",
                onConfirm = {},
                onDismiss = {},
                isDestructive = false
            )
        }
    }
}

@PhonePreview
@Composable
private fun YoinAlertDialogPreview() {
    YoinTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background),
            contentAlignment = Alignment.Center
        ) {
            YoinAlertDialog(
                title = "エラー",
                message = "ネットワーク接続に失敗しました。インターネット接続を確認してください。",
                buttonText = "OK",
                onDismiss = {}
            )
        }
    }
}
