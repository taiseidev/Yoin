package com.yoin.feature.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.feature.profile.viewmodel.ProfileEditContract
import com.yoin.feature.profile.viewmodel.ProfileEditViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * プロフィール編集画面
 *
 * 機能:
 * - プロフィール画像の変更
 * - ニックネームと自己紹介の編集
 * - パスワード変更・アカウント削除への遷移
 */
@Composable
fun ProfileEditScreen(
    userId: String,
    viewModel: ProfileEditViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effect処理
    LaunchedEffect(Unit) {
        viewModel.onIntent(ProfileEditContract.Intent.OnScreenDisplayed(userId))

        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ProfileEditContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is ProfileEditContract.Effect.ShowSuccess -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is ProfileEditContract.Effect.NavigateBack -> {
                    onNavigateBack()
                }
                is ProfileEditContract.Effect.NavigateToChangePassword -> {
                    onNavigateToChangePassword()
                }
                is ProfileEditContract.Effect.NavigateToDeleteAccount -> {
                    onNavigateToDeleteAccount()
                }
                is ProfileEditContract.Effect.ShowProfileImagePicker -> {
                    // TODO: プロフィール画像選択ダイアログを表示
                }
                is ProfileEditContract.Effect.ShowUnsavedChangesDialog -> {
                    // TODO: 未保存の変更があることを警告するダイアログを表示
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = YoinColors.Background,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // ヘッダー
                ProfileEditHeader(
                    onCancelClick = {
                        viewModel.onIntent(ProfileEditContract.Intent.OnCancelPressed)
                    },
                    onSaveClick = {
                        viewModel.onIntent(ProfileEditContract.Intent.OnSavePressed)
                    },
                    isSaving = state.isSaving
                )

                Spacer(modifier = Modifier.height(24.dp))

                // プロフィール画像
                ProfileImageSection(
                    emoji = state.profileImageEmoji,
                    onImageClick = {
                        viewModel.onIntent(ProfileEditContract.Intent.OnProfileImageTapped)
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // ニックネーム
                ProfileEditField(
                    label = "ニックネーム",
                    value = state.nickname,
                    onValueChange = {
                        viewModel.onIntent(ProfileEditContract.Intent.OnNicknameChanged(it))
                    },
                    error = state.nicknameError,
                    showClearButton = state.nickname.isNotEmpty(),
                    onClearClick = {
                        viewModel.onIntent(ProfileEditContract.Intent.OnNicknameClearPressed)
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // メールアドレス
                ProfileEditReadOnlyField(
                    label = "メールアドレス",
                    value = state.email,
                    hint = "メールアドレスは変更できません"
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 自己紹介
                ProfileEditBioField(
                    label = "自己紹介",
                    value = state.bio,
                    charCount = state.bioCharCount,
                    maxLength = state.bioMaxLength,
                    onValueChange = {
                        viewModel.onIntent(ProfileEditContract.Intent.OnBioChanged(it))
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // アカウントセクション
                AccountSection(
                    onChangePasswordClick = {
                        viewModel.onIntent(ProfileEditContract.Intent.OnChangePasswordPressed)
                    },
                    onDeleteAccountClick = {
                        viewModel.onIntent(ProfileEditContract.Intent.OnDeleteAccountPressed)
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

            // ローディング表示
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = YoinColors.Primary
                )
            }
        }
    }
}

@Composable
private fun ProfileEditHeader(
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    isSaving: Boolean
) {
    Column {
        // ステータスバー
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(YoinColors.Surface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "9:41",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary
            )
        }

        // ヘッダー
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(YoinColors.Surface)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // キャンセルボタン
            Text(
                text = "キャンセル",
                fontSize = 16.sp,
                color = YoinColors.TextSecondary,
                modifier = Modifier.clickable(onClick = onCancelClick)
            )

            Spacer(modifier = Modifier.weight(1f))

            // タイトル
            Text(
                text = "プロフィール編集",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.TextPrimary
            )

            Spacer(modifier = Modifier.weight(1f))

            // 保存ボタン
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = YoinColors.Primary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "保存",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.Primary,
                    modifier = Modifier.clickable(onClick = onSaveClick)
                )
            }
        }

        // 区切り線
        Divider(color = YoinColors.SurfaceVariant, thickness = 1.dp)
    }
}

@Composable
private fun ProfileImageSection(
    emoji: String,
    onImageClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            // 背景円
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .clip(CircleShape)
                    .background(YoinColors.AccentPeach)
                    .clickable(onClick = onImageClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )
            }

            // カメラアイコン
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(YoinColors.Primary)
                    .clickable(onClick = onImageClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📷",
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "写真を変更",
            fontSize = 14.sp,
            color = YoinColors.Primary
        )
    }
}

@Composable
private fun ProfileEditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String? = null,
    showClearButton: Boolean = false,
    onClearClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = YoinColors.TextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(
                    width = 1.dp,
                    color = if (error != null) YoinColors.Error else YoinColors.SurfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = YoinColors.TextPrimary
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            innerTextField()
                        }

                        if (showClearButton) {
                            Text(
                                text = "✕",
                                fontSize = 14.sp,
                                color = YoinColors.TextSecondary,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .clickable(onClick = onClearClick)
                            )
                        }
                    }
                }
            )
        }

        if (error != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = error,
                fontSize = 11.sp,
                color = YoinColors.Error
            )
        }
    }
}

@Composable
private fun ProfileEditReadOnlyField(
    label: String,
    value: String,
    hint: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = YoinColors.TextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(
                    width = 1.dp,
                    color = YoinColors.SurfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                )
                .background(YoinColors.Background)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    fontSize = 14.sp,
                    color = YoinColors.TextSecondary,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "🔒",
                    fontSize = 12.sp,
                    color = YoinColors.TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = hint,
            fontSize = 11.sp,
            color = YoinColors.TextSecondary
        )
    }
}

@Composable
private fun ProfileEditBioField(
    label: String,
    value: String,
    charCount: Int,
    maxLength: Int,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(172.dp)
                .border(
                    width = 1.dp,
                    color = YoinColors.SurfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = YoinColors.TextPrimary
                ),
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "$charCount/$maxLength",
            fontSize = 11.sp,
            color = YoinColors.TextSecondary,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
private fun AccountSection(
    onChangePasswordClick: () -> Unit,
    onDeleteAccountClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        // セクションヘッダー
        Text(
            text = "アカウント",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = YoinColors.TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // パスワード変更
        AccountMenuItem(
            text = "パスワードを変更",
            textColor = YoinColors.TextPrimary,
            onClick = onChangePasswordClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        // アカウント削除
        AccountMenuItem(
            text = "アカウントを削除",
            textColor = YoinColors.Error,
            onClick = onDeleteAccountClick
        )
    }
}

@Composable
private fun AccountMenuItem(
    text: String,
    textColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(
                width = 1.dp,
                color = YoinColors.SurfaceVariant,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                color = textColor,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "›",
                fontSize = 16.sp,
                color = textColor
            )
        }
    }
}
