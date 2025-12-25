package com.yoin.feature.settings.viewmodel

/**
 * ヘルプ・FAQ画面のMVIコントラクト
 *
 * 機能:
 * - よくある質問の表示
 * - カテゴリから探す
 * - 検索機能
 * - お問い合わせへの遷移
 */
interface HelpFaqContract {
    /**
     * 画面の状態
     */
    data class State(
        val isLoading: Boolean = false,
        val searchQuery: String = "",
        val faqItems: List<FaqItem> = emptyList(),
        val categories: List<Category> = emptyList(),
    )

    /**
     * FAQアイテム
     */
    data class FaqItem(
        val icon: String,
        val question: String,
        val answer: String,
    )

    /**
     * カテゴリ
     */
    data class Category(
        val icon: String,
        val title: String,
        val categoryType: CategoryType,
    )

    /**
     * カテゴリタイプ
     */
    enum class CategoryType {
        APP_USAGE,      // アプリの使い方
        PRICING_PLAN,   // 料金・プラン
        ORDER_SHIPPING, // 注文・配送
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
         * 検索クエリが変更された
         */
        data class OnSearchQueryChanged(val query: String) : Intent

        /**
         * FAQアイテムがタップされた
         */
        data class OnFaqItemClicked(val faqItem: FaqItem) : Intent

        /**
         * カテゴリがタップされた
         */
        data class OnCategoryClicked(val category: Category) : Intent

        /**
         * お問い合わせがタップされた
         */
        data object OnContactSupportPressed : Intent
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
         * FAQ詳細画面に遷移
         */
        data class NavigateToFaqDetail(val faqItem: FaqItem) : Effect

        /**
         * カテゴリ詳細画面に遷移
         */
        data class NavigateToCategoryDetail(val category: Category) : Effect

        /**
         * お問い合わせ画面に遷移
         */
        data object NavigateToContactSupport : Effect

        /**
         * エラーメッセージを表示
         */
        data class ShowError(val message: String) : Effect
    }
}
