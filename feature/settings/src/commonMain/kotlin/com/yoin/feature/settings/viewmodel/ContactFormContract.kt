package com.yoin.feature.settings.viewmodel

/**
 * お問い合わせフォーム画面のMVIコントラクト
 *
 * 機能:
 * - お問い合わせフォームの入力管理
 * - 添付ファイルのアップロード
 * - フォームのバリデーション
 * - お問い合わせの送信
 */
interface ContactFormContract {
    /**
     * 画面の状態
     */
    data class State(
        val isLoading: Boolean = false,
        val contactType: ContactType = ContactType.APP_BUG,
        val subject: String = "",
        val content: String = "",
        val attachedFileName: String? = null,
        val userEmail: String = "yamada@example.com",
        val validationErrors: ValidationErrors = ValidationErrors(),
    )

    /**
     * お問い合わせ種類
     */
    enum class ContactType(val displayName: String) {
        APP_BUG("アプリの不具合"),
        FEATURE_REQUEST("機能の要望"),
        ACCOUNT_ISSUE("アカウントについて"),
        PAYMENT_ISSUE("支払いについて"),
        ORDER_ISSUE("注文・配送について"),
        OTHER("その他"),
    }

    /**
     * バリデーションエラー
     */
    data class ValidationErrors(
        val subjectError: String? = null,
        val contentError: String? = null,
    ) {
        fun hasErrors(): Boolean {
            return subjectError != null || contentError != null
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
         * お問い合わせ種類が変更された
         */
        data class OnContactTypeChanged(val contactType: ContactType) : Intent

        /**
         * 件名が変更された
         */
        data class OnSubjectChanged(val subject: String) : Intent

        /**
         * お問い合わせ内容が変更された
         */
        data class OnContentChanged(val content: String) : Intent

        /**
         * ファイル選択がタップされた
         */
        data object OnFileSelectPressed : Intent

        /**
         * ファイルが選択された
         */
        data class OnFileSelected(val fileName: String) : Intent

        /**
         * 送信するボタンがタップされた
         */
        data object OnSubmitPressed : Intent
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
         * ファイル選択画面を表示
         */
        data object ShowFilePicker : Effect

        /**
         * 送信完了画面に遷移
         */
        data object NavigateToSubmitComplete : Effect

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
