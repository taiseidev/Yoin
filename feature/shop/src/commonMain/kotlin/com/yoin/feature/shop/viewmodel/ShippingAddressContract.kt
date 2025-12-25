package com.yoin.feature.shop.viewmodel

/**
 * 配送先住所入力画面のMVIコントラクト
 *
 * 機能:
 * - 配送先住所の入力
 * - 郵便番号から住所検索
 * - 住所の保存設定
 * - 入力バリデーション
 */
interface ShippingAddressContract {
    /**
     * 画面の状態
     */
    data class State(
        val isLoading: Boolean = false,
        val lastName: String = "",
        val firstName: String = "",
        val postalCode: String = "",
        val prefecture: String = "",
        val city: String = "",
        val addressLine: String = "",
        val phoneNumber: String = "",
        val saveAddress: Boolean = false,
        val isAddressSearching: Boolean = false,
        val validationErrors: ValidationErrors = ValidationErrors(),
        val currentStep: Int = 2,
        val totalSteps: Int = 3,
    )

    /**
     * バリデーションエラー
     */
    data class ValidationErrors(
        val lastNameError: String? = null,
        val firstNameError: String? = null,
        val postalCodeError: String? = null,
        val prefectureError: String? = null,
        val cityError: String? = null,
        val addressLineError: String? = null,
        val phoneNumberError: String? = null,
    ) {
        fun hasErrors(): Boolean {
            return lastNameError != null ||
                    firstNameError != null ||
                    postalCodeError != null ||
                    prefectureError != null ||
                    cityError != null ||
                    addressLineError != null ||
                    phoneNumberError != null
        }
    }

    /**
     * ユーザーの操作
     */
    sealed interface Intent {
        /**
         * 画面が表示された
         */
        data object OnScreenDisplayed : Intent

        /**
         * 戻るボタンがタップされた
         */
        data object OnBackPressed : Intent

        /**
         * 姓が変更された
         */
        data class OnLastNameChanged(val value: String) : Intent

        /**
         * 名が変更された
         */
        data class OnFirstNameChanged(val value: String) : Intent

        /**
         * 郵便番号が変更された
         */
        data class OnPostalCodeChanged(val value: String) : Intent

        /**
         * 住所検索ボタンがタップされた
         */
        data object OnSearchAddressPressed : Intent

        /**
         * 都道府県・市区町村が変更された
         */
        data class OnPrefectureAndCityChanged(val value: String) : Intent

        /**
         * 番地・建物名が変更された
         */
        data class OnAddressLineChanged(val value: String) : Intent

        /**
         * 電話番号が変更された
         */
        data class OnPhoneNumberChanged(val value: String) : Intent

        /**
         * 保存チェックボックスがトグルされた
         */
        data class OnSaveAddressToggled(val checked: Boolean) : Intent

        /**
         * 確認画面へ進むボタンがタップされた
         */
        data object OnProceedToConfirmationPressed : Intent
    }

    /**
     * 副作用
     */
    sealed interface Effect {
        /**
         * 前の画面に戻る
         */
        data object NavigateBack : Effect

        /**
         * 確認画面に遷移
         */
        data class NavigateToConfirmation(
            val lastName: String,
            val firstName: String,
            val postalCode: String,
            val prefecture: String,
            val city: String,
            val addressLine: String,
            val phoneNumber: String,
        ) : Effect

        /**
         * エラーメッセージを表示
         */
        data class ShowError(val message: String) : Effect

        /**
         * 成功メッセージを表示
         */
        data class ShowSuccess(val message: String) : Effect
    }
}
