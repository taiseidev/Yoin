package com.yoin.feature.shop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.shop.viewmodel.ShippingAddressContract
import com.yoin.feature.shop.viewmodel.ShippingAddressViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * 配送先住所入力画面
 *
 * 機能:
 * - 配送先住所の入力フォーム
 * - 郵便番号から住所検索
 * - 入力バリデーション
 * - 住所の保存設定
 *
 * @param viewModel ShippingAddressViewModel
 * @param onNavigateBack 戻るボタンのコールバック
 * @param onNavigateToConfirmation 確認画面への遷移コールバック
 */
@Composable
fun ShippingAddressScreen(
    viewModel: ShippingAddressViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToConfirmation: (String, String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _, _ -> }
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ShippingAddressContract.Effect.NavigateBack -> onNavigateBack()
                is ShippingAddressContract.Effect.NavigateToConfirmation -> {
                    onNavigateToConfirmation(
                        effect.lastName,
                        effect.firstName,
                        effect.postalCode,
                        effect.prefecture,
                        effect.city,
                        effect.addressLine,
                        effect.phoneNumber
                    )
                }
                is ShippingAddressContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is ShippingAddressContract.Effect.ShowSuccess -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    // 画面表示時の初期化
    LaunchedEffect(Unit) {
        viewModel.onIntent(ShippingAddressContract.Intent.OnScreenDisplayed)
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
            ShippingAddressHeader(
                currentStep = state.currentStep,
                totalSteps = state.totalSteps,
                onBackPressed = {
                    viewModel.onIntent(ShippingAddressContract.Intent.OnBackPressed)
                }
            )

            // フォームコンテンツ
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // お名前
                Text(
                    text = "お名前*",
                    fontSize = 13.sp,
                    color = YoinColors.TextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 姓
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = state.lastName,
                            onValueChange = {
                                viewModel.onIntent(ShippingAddressContract.Intent.OnLastNameChanged(it))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            isError = state.validationErrors.lastNameError != null,
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = YoinColors.SurfaceVariant,
                                focusedBorderColor = YoinColors.Primary
                            )
                        )
                        Text(
                            text = "姓",
                            fontSize = 11.sp,
                            color = YoinColors.TextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // 名
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = state.firstName,
                            onValueChange = {
                                viewModel.onIntent(ShippingAddressContract.Intent.OnFirstNameChanged(it))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            isError = state.validationErrors.firstNameError != null,
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = YoinColors.SurfaceVariant,
                                focusedBorderColor = YoinColors.Primary
                            )
                        )
                        Text(
                            text = "名",
                            fontSize = 11.sp,
                            color = YoinColors.TextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 郵便番号
                Text(
                    text = "郵便番号*",
                    fontSize = 13.sp,
                    color = YoinColors.TextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = state.postalCode,
                        onValueChange = {
                            viewModel.onIntent(ShippingAddressContract.Intent.OnPostalCodeChanged(it))
                        },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("150-0001", color = YoinColors.TextSecondary) },
                        isError = state.validationErrors.postalCodeError != null,
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = YoinColors.SurfaceVariant,
                            focusedBorderColor = YoinColors.Primary
                        )
                    )

                    Button(
                        onClick = {
                            viewModel.onIntent(ShippingAddressContract.Intent.OnSearchAddressPressed)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = YoinColors.Primary,
                            contentColor = YoinColors.OnPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(56.dp),
                        enabled = !state.isAddressSearching
                    ) {
                        if (state.isAddressSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = YoinColors.OnPrimary
                            )
                        } else {
                            Text(
                                text = "住所検索",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 都道府県・市区町村
                Text(
                    text = "都道府県・市区町村*",
                    fontSize = 13.sp,
                    color = YoinColors.TextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = state.prefecture,
                    onValueChange = {
                        viewModel.onIntent(ShippingAddressContract.Intent.OnPrefectureAndCityChanged(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("東京都渋谷区", color = YoinColors.TextSecondary) },
                    isError = state.validationErrors.prefectureError != null,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = YoinColors.SurfaceVariant,
                        focusedBorderColor = YoinColors.Primary
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 番地・建物名
                Text(
                    text = "番地・建物名*",
                    fontSize = 13.sp,
                    color = YoinColors.TextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = state.addressLine,
                    onValueChange = {
                        viewModel.onIntent(ShippingAddressContract.Intent.OnAddressLineChanged(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("神宮前1-2-3 〇〇マンション 101", color = YoinColors.TextSecondary) },
                    isError = state.validationErrors.addressLineError != null,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = YoinColors.SurfaceVariant,
                        focusedBorderColor = YoinColors.Primary
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 電話番号
                Text(
                    text = "電話番号*",
                    fontSize = 13.sp,
                    color = YoinColors.TextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = state.phoneNumber,
                    onValueChange = {
                        viewModel.onIntent(ShippingAddressContract.Intent.OnPhoneNumberChanged(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("090-1234-5678", color = YoinColors.TextSecondary) },
                    isError = state.validationErrors.phoneNumberError != null,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = YoinColors.SurfaceVariant,
                        focusedBorderColor = YoinColors.Primary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "配送に関するご連絡に使用します",
                    fontSize = 11.sp,
                    color = YoinColors.TextSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 保存チェックボックス
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.onIntent(
                                ShippingAddressContract.Intent.OnSaveAddressToggled(!state.saveAddress)
                            )
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = state.saveAddress,
                        onCheckedChange = {
                            viewModel.onIntent(
                                ShippingAddressContract.Intent.OnSaveAddressToggled(it)
                            )
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = YoinColors.Primary,
                            uncheckedColor = YoinColors.TextSecondary
                        )
                    )
                    Text(
                        text = "この住所を保存する",
                        fontSize = 14.sp,
                        color = YoinColors.TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 確認画面へ進むボタン
                Button(
                    onClick = {
                        viewModel.onIntent(ShippingAddressContract.Intent.OnProceedToConfirmationPressed)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YoinColors.Primary,
                        contentColor = YoinColors.OnPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !state.isLoading
                ) {
                    Text(
                        text = "確認画面へ進む",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
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
 * 配送先住所入力ヘッダー
 */
@Composable
private fun ShippingAddressHeader(
    currentStep: Int,
    totalSteps: Int,
    onBackPressed: () -> Unit
) {
    Surface(
        color = YoinColors.Surface,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // ステータスバー
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "9:41",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = YoinColors.TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // タイトルと戻るボタン
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 戻るボタン
                Text(
                    text = "←",
                    fontSize = 20.sp,
                    color = YoinColors.TextPrimary,
                    modifier = Modifier.clickable(onClick = onBackPressed)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // タイトル
                Text(
                    text = "配送先入力",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = YoinColors.TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // プログレスバー
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(totalSteps) { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .background(
                                    color = if (index < currentStep) YoinColors.Primary else YoinColors.SurfaceVariant,
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$currentStep/$totalSteps",
                    fontSize = 12.sp,
                    color = YoinColors.TextSecondary,
                    modifier = Modifier.align(Alignment.End)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = YoinColors.SurfaceVariant)
        }
    }
}

/**
 * プレビュー
 */
@PhonePreview
@Composable
private fun ShippingAddressScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            Text("Shipping Address Screen Preview")
        }
    }
}
