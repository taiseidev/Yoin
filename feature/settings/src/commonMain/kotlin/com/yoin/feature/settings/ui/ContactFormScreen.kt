package com.yoin.feature.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.ui.component.YoinAppBar
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.settings.viewmodel.ContactFormContract
import com.yoin.feature.settings.viewmodel.ContactFormViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * お問い合わせフォーム画面
 *
 * 機能:
 * - お問い合わせ種類の選択
 * - 件名の入力
 * - お問い合わせ内容の入力
 * - 添付ファイルの選択
 * - フォームの送信
 *
 * @param viewModel ContactFormViewModel
 * @param onNavigateBack 戻るボタンのコールバック
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactFormScreen(
    viewModel: ContactFormViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showContactTypeDropdown by remember { mutableStateOf(false) }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ContactFormContract.Effect.NavigateBack -> onNavigateBack()
                is ContactFormContract.Effect.ShowFilePicker -> {
                    // TODO: 実際のファイルピッカーを表示
                    // モックとしてサンプルファイル名を設定
                    viewModel.onIntent(ContactFormContract.Intent.OnFileSelected("screenshot.png"))
                }
                is ContactFormContract.Effect.NavigateToSubmitComplete -> {
                    // 送信完了後は自動で戻る
                }
                is ContactFormContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is ContactFormContract.Effect.ShowSuccess -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    // 画面表示時の初期化
    LaunchedEffect(Unit) {
        viewModel.onIntent(ContactFormContract.Intent.OnScreenDisplayed)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(YoinColors.Background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ヘッダー
            YoinAppBar(
                title = "お問い合わせ",
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.onIntent(ContactFormContract.Intent.OnBackPressed)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = YoinColors.TextPrimary
                        )
                    }
                }
            )

            // コンテンツ
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // お問い合わせ種類
                Text(
                    text = "お問い合わせ種類",
                    fontSize = 12.sp,
                    color = YoinColors.TextSecondary
                )
                Text(
                    text = "*",
                    fontSize = 12.sp,
                    color = YoinColors.Error,
                    modifier = Modifier.offset(x = 100.dp, y = (-16).dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ドロップダウン
                ExposedDropdownMenuBox(
                    expanded = showContactTypeDropdown,
                    onExpandedChange = { showContactTypeDropdown = it }
                ) {
                    OutlinedTextField(
                        value = state.contactType.displayName,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = {
                            Text(
                                text = "▼",
                                fontSize = 14.sp,
                                color = YoinColors.TextSecondary
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = YoinColors.SurfaceVariant,
                            focusedBorderColor = YoinColors.TextSecondary,
                            unfocusedContainerColor = YoinColors.Surface,
                            focusedContainerColor = YoinColors.Surface
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = showContactTypeDropdown,
                        onDismissRequest = { showContactTypeDropdown = false }
                    ) {
                        ContactFormContract.ContactType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.displayName) },
                                onClick = {
                                    viewModel.onIntent(ContactFormContract.Intent.OnContactTypeChanged(type))
                                    showContactTypeDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 件名
                Text(
                    text = "件名",
                    fontSize = 12.sp,
                    color = YoinColors.TextSecondary
                )
                Text(
                    text = "*",
                    fontSize = 12.sp,
                    color = YoinColors.Error,
                    modifier = Modifier.offset(x = 32.dp, y = (-16).dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.subject,
                    onValueChange = {
                        viewModel.onIntent(ContactFormContract.Intent.OnSubjectChanged(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("件名を入力", color = YoinColors.TextSecondary) },
                    isError = state.validationErrors.subjectError != null,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = YoinColors.SurfaceVariant,
                        focusedBorderColor = YoinColors.TextSecondary,
                        unfocusedContainerColor = YoinColors.Surface,
                        focusedContainerColor = YoinColors.Surface
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // お問い合わせ内容
                Text(
                    text = "お問い合わせ内容",
                    fontSize = 12.sp,
                    color = YoinColors.TextSecondary
                )
                Text(
                    text = "*",
                    fontSize = 12.sp,
                    color = YoinColors.Error,
                    modifier = Modifier.offset(x = 106.dp, y = (-16).dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.content,
                    onValueChange = {
                        viewModel.onIntent(ContactFormContract.Intent.OnContentChanged(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    placeholder = {
                        Text(
                            "お問い合わせ内容を詳しくご記入ください",
                            color = YoinColors.TextSecondary
                        )
                    },
                    isError = state.validationErrors.contentError != null,
                    maxLines = 6,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = YoinColors.SurfaceVariant,
                        focusedBorderColor = YoinColors.TextSecondary,
                        unfocusedContainerColor = YoinColors.Surface,
                        focusedContainerColor = YoinColors.Surface
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                // 文字数カウンター
                Text(
                    text = "${state.content.length}/1000",
                    fontSize = 11.sp,
                    color = YoinColors.TextSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    textAlign = TextAlign.End
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 添付ファイル
                Row {
                    Text(
                        text = "添付ファイル",
                        fontSize = 12.sp,
                        color = YoinColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "（任意）",
                        fontSize = 11.sp,
                        color = YoinColors.TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ファイル選択ボックス
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clickable {
                            viewModel.onIntent(ContactFormContract.Intent.OnFileSelectPressed)
                        },
                    color = YoinColors.Surface,
                    shape = RoundedCornerShape(8.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(YoinColors.SurfaceVariant)
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AttachFile,
                            contentDescription = "Attach File",
                            tint = YoinColors.TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = state.attachedFileName ?: "タップしてファイルを選択",
                            fontSize = 12.sp,
                            color = YoinColors.TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "画像: JPG, PNG（最大5MB）",
                    fontSize = 11.sp,
                    color = YoinColors.TextSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // お知らせカード
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = YoinColors.AccentPeach,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "Info",
                                tint = YoinColors.Primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "通常1〜3営業日以内にご返信いたします",
                                fontSize = 12.sp,
                                color = YoinColors.TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${state.userEmail} 宛に返信します",
                            fontSize = 11.sp,
                            color = YoinColors.Primary,
                            modifier = Modifier.padding(start = 24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 送信ボタン
                Button(
                    onClick = {
                        viewModel.onIntent(ContactFormContract.Intent.OnSubmitPressed)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YoinColors.Primary,
                        contentColor = YoinColors.Surface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = YoinColors.Surface
                        )
                    } else {
                        Text(
                            text = "送信する",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
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
 * プレビュー
 */
@PhonePreview
@Composable
private fun ContactFormScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            Text("Contact Form Screen Preview")
        }
    }
}
