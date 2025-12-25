package com.yoin.feature.shop.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * 配送先住所入力画面のViewModel
 *
 * 機能:
 * - 住所フォームの入力管理
 * - 郵便番号から住所検索
 * - 入力バリデーション
 */
class ShippingAddressViewModel : ScreenModel {
    private val _state = MutableStateFlow(ShippingAddressContract.State())
    val state: StateFlow<ShippingAddressContract.State> = _state.asStateFlow()

    private val _effect = Channel<ShippingAddressContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /**
     * Intentを処理
     */
    fun onIntent(intent: ShippingAddressContract.Intent) {
        when (intent) {
            is ShippingAddressContract.Intent.OnScreenDisplayed -> handleScreenDisplayed()
            is ShippingAddressContract.Intent.OnBackPressed -> handleBackPressed()
            is ShippingAddressContract.Intent.OnLastNameChanged -> handleLastNameChanged(intent.value)
            is ShippingAddressContract.Intent.OnFirstNameChanged -> handleFirstNameChanged(intent.value)
            is ShippingAddressContract.Intent.OnPostalCodeChanged -> handlePostalCodeChanged(intent.value)
            is ShippingAddressContract.Intent.OnSearchAddressPressed -> handleSearchAddressPressed()
            is ShippingAddressContract.Intent.OnPrefectureAndCityChanged -> handlePrefectureAndCityChanged(intent.value)
            is ShippingAddressContract.Intent.OnAddressLineChanged -> handleAddressLineChanged(intent.value)
            is ShippingAddressContract.Intent.OnPhoneNumberChanged -> handlePhoneNumberChanged(intent.value)
            is ShippingAddressContract.Intent.OnSaveAddressToggled -> handleSaveAddressToggled(intent.checked)
            is ShippingAddressContract.Intent.OnProceedToConfirmationPressed -> handleProceedToConfirmationPressed()
        }
    }

    /**
     * 画面表示時の処理
     */
    private fun handleScreenDisplayed() {
        _state.value = _state.value.copy(isLoading = false)
    }

    /**
     * 戻るボタンの処理
     */
    private fun handleBackPressed() {
        screenModelScope.launch {
            _effect.send(ShippingAddressContract.Effect.NavigateBack)
        }
    }

    /**
     * 姓変更の処理
     */
    private fun handleLastNameChanged(value: String) {
        _state.value = _state.value.copy(
            lastName = value,
            validationErrors = _state.value.validationErrors.copy(lastNameError = null)
        )
    }

    /**
     * 名変更の処理
     */
    private fun handleFirstNameChanged(value: String) {
        _state.value = _state.value.copy(
            firstName = value,
            validationErrors = _state.value.validationErrors.copy(firstNameError = null)
        )
    }

    /**
     * 郵便番号変更の処理
     */
    private fun handlePostalCodeChanged(value: String) {
        // 数字とハイフンのみ許可
        val filtered = value.filter { it.isDigit() || it == '-' }
        _state.value = _state.value.copy(
            postalCode = filtered,
            validationErrors = _state.value.validationErrors.copy(postalCodeError = null)
        )
    }

    /**
     * 住所検索の処理
     */
    private fun handleSearchAddressPressed() {
        screenModelScope.launch {
            try {
                val postalCode = _state.value.postalCode.replace("-", "")

                if (postalCode.length != 7) {
                    _effect.send(
                        ShippingAddressContract.Effect.ShowError("郵便番号は7桁で入力してください")
                    )
                    return@launch
                }

                _state.value = _state.value.copy(isAddressSearching = true)

                // TODO: 実際のAPIを使用して住所検索
                // モックとして東京都渋谷区を返す
                delay(500)

                _state.value = _state.value.copy(
                    prefecture = "東京都渋谷区",
                    isAddressSearching = false
                )

                _effect.send(
                    ShippingAddressContract.Effect.ShowSuccess("住所を取得しました")
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isAddressSearching = false)
                _effect.send(
                    ShippingAddressContract.Effect.ShowError("住所の取得に失敗しました")
                )
            }
        }
    }

    /**
     * 都道府県・市区町村変更の処理
     */
    private fun handlePrefectureAndCityChanged(value: String) {
        _state.value = _state.value.copy(
            prefecture = value,
            validationErrors = _state.value.validationErrors.copy(prefectureError = null)
        )
    }

    /**
     * 番地・建物名変更の処理
     */
    private fun handleAddressLineChanged(value: String) {
        _state.value = _state.value.copy(
            addressLine = value,
            validationErrors = _state.value.validationErrors.copy(addressLineError = null)
        )
    }

    /**
     * 電話番号変更の処理
     */
    private fun handlePhoneNumberChanged(value: String) {
        // 数字とハイフンのみ許可
        val filtered = value.filter { it.isDigit() || it == '-' }
        _state.value = _state.value.copy(
            phoneNumber = filtered,
            validationErrors = _state.value.validationErrors.copy(phoneNumberError = null)
        )
    }

    /**
     * 保存チェックボックスの処理
     */
    private fun handleSaveAddressToggled(checked: Boolean) {
        _state.value = _state.value.copy(saveAddress = checked)
    }

    /**
     * 確認画面へ進むボタンの処理
     */
    private fun handleProceedToConfirmationPressed() {
        screenModelScope.launch {
            val validationErrors = validateInputs()

            if (validationErrors.hasErrors()) {
                _state.value = _state.value.copy(validationErrors = validationErrors)
                _effect.send(
                    ShippingAddressContract.Effect.ShowError("入力内容を確認してください")
                )
                return@launch
            }

            _effect.send(
                ShippingAddressContract.Effect.NavigateToConfirmation(
                    lastName = _state.value.lastName,
                    firstName = _state.value.firstName,
                    postalCode = _state.value.postalCode,
                    prefecture = _state.value.prefecture,
                    city = _state.value.city,
                    addressLine = _state.value.addressLine,
                    phoneNumber = _state.value.phoneNumber
                )
            )
        }
    }

    /**
     * 入力内容のバリデーション
     */
    private fun validateInputs(): ShippingAddressContract.ValidationErrors {
        val state = _state.value

        return ShippingAddressContract.ValidationErrors(
            lastNameError = if (state.lastName.isBlank()) "姓を入力してください" else null,
            firstNameError = if (state.firstName.isBlank()) "名を入力してください" else null,
            postalCodeError = if (state.postalCode.replace("-", "").length != 7) {
                "郵便番号は7桁で入力してください"
            } else null,
            prefectureError = if (state.prefecture.isBlank()) "都道府県・市区町村を入力してください" else null,
            addressLineError = if (state.addressLine.isBlank()) "番地・建物名を入力してください" else null,
            phoneNumberError = if (state.phoneNumber.replace("-", "").length !in 10..11) {
                "電話番号を正しく入力してください"
            } else null
        )
    }
}
