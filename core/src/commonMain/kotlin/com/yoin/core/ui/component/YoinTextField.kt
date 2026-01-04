package com.yoin.core.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.core.design.theme.YoinTheme

/**
 * Yoin標準テキスト入力フィールド
 *
 * Modern Cinematic with Amber Accentデザインに基づく入力フィールド
 *
 * @param value 現在の入力値
 * @param onValueChange 値が変更された時のコールバック
 * @param modifier Modifier
 * @param placeholder プレースホルダーテキスト
 * @param isError エラー状態かどうか
 * @param errorMessage エラーメッセージ（isError=trueの時に表示）
 * @param enabled 入力可能かどうか
 * @param singleLine 単一行入力かどうか
 * @param maxLines 最大行数
 * @param leadingIcon 左側に表示するアイコン
 * @param trailingIcon 右側に表示するアイコン
 * @param keyboardOptions キーボードオプション
 * @param keyboardActions キーボードアクション
 * @param visualTransformation 入力の視覚的変換（パスワードマスクなど）
 */
@Composable
fun YoinTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    // ボーダー色のアニメーション
    val borderColor by animateColorAsState(
        targetValue = when {
            isError -> YoinColors.Error
            isFocused -> YoinColors.Primary
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 150),
        label = "borderColor"
    )

    Column(modifier = modifier) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(YoinColors.SurfaceVariant)
                .border(
                    width = 2.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(12.dp)
                ),
            enabled = enabled,
            textStyle = TextStyle(
                color = YoinColors.TextPrimary,
                fontSize = 16.sp
            ),
            singleLine = singleLine,
            maxLines = maxLines,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            interactionSource = interactionSource,
            cursorBrush = SolidColor(YoinColors.Primary),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = YoinSpacing.lg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Leading icon
                    if (leadingIcon != null) {
                        leadingIcon()
                        Spacer(modifier = Modifier.width(YoinSpacing.sm))
                    }

                    // Text field content
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = YoinColors.TextSecondary,
                                fontSize = 16.sp
                            )
                        }
                        innerTextField()
                    }

                    // Trailing icon
                    if (trailingIcon != null) {
                        Spacer(modifier = Modifier.width(YoinSpacing.sm))
                        trailingIcon()
                    }
                }
            }
        )

        // エラーメッセージ
        if (isError && !errorMessage.isNullOrBlank()) {
            Text(
                text = errorMessage,
                color = YoinColors.Error,
                fontSize = 12.sp,
                modifier = Modifier.padding(
                    start = YoinSpacing.lg,
                    top = YoinSpacing.xs
                )
            )
        }
    }
}

/**
 * Yoin検索用入力フィールド
 *
 * 虫眼鏡アイコンとクリアボタン付きの検索専用フィールド
 *
 * @param value 現在の検索テキスト
 * @param onValueChange 値が変更された時のコールバック
 * @param modifier Modifier
 * @param placeholder プレースホルダーテキスト
 * @param onClear クリアボタンが押された時のコールバック
 * @param enabled 入力可能かどうか
 * @param keyboardOptions キーボードオプション
 * @param keyboardActions キーボードアクション
 */
@Composable
fun YoinSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search",
    onClear: (() -> Unit)? = null,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    // フォーカス時のボーダー色
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) YoinColors.Primary else Color.Transparent,
        animationSpec = tween(durationMillis = 150),
        label = "borderColor"
    )

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(YoinColors.SurfaceVariant)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(28.dp)
            ),
        enabled = enabled,
        textStyle = TextStyle(
            color = YoinColors.TextPrimary,
            fontSize = 16.sp
        ),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        cursorBrush = SolidColor(YoinColors.Primary),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = YoinSpacing.lg),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 検索アイコン（左側）
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = YoinColors.TextSecondary,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(YoinSpacing.sm))

                // テキスト入力エリア
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = YoinColors.TextSecondary,
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }

                // クリアボタン（右側、テキストがある場合のみ表示）
                if (value.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            onClear?.invoke() ?: onValueChange("")
                        },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = YoinColors.TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    )
}

// ============ Previews ============

@PhonePreview
@Composable
private fun YoinTextFieldPreview() {
    YoinTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
                .padding(YoinSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(YoinSpacing.lg)
        ) {
            // 空のフィールド
            var text1 by remember { mutableStateOf("") }
            YoinTextField(
                value = text1,
                onValueChange = { text1 = it },
                placeholder = "Enter your email"
            )

            // 入力済みフィールド
            var text2 by remember { mutableStateOf("hello@example.com") }
            YoinTextField(
                value = text2,
                onValueChange = { text2 = it },
                placeholder = "Enter your email"
            )

            // エラー状態
            var text3 by remember { mutableStateOf("invalid-email") }
            YoinTextField(
                value = text3,
                onValueChange = { text3 = it },
                placeholder = "Enter your email",
                isError = true,
                errorMessage = "Please enter a valid email address"
            )
        }
    }
}

@PhonePreview
@Composable
private fun YoinSearchFieldPreview() {
    YoinTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
                .padding(YoinSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(YoinSpacing.lg)
        ) {
            // 空の検索フィールド
            var search1 by remember { mutableStateOf("") }
            YoinSearchField(
                value = search1,
                onValueChange = { search1 = it },
                placeholder = "Search rooms..."
            )

            // 入力済み検索フィールド
            var search2 by remember { mutableStateOf("Tokyo trip") }
            YoinSearchField(
                value = search2,
                onValueChange = { search2 = it },
                placeholder = "Search rooms..."
            )
        }
    }
}
