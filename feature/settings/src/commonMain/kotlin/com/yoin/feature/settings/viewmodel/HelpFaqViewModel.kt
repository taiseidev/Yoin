package com.yoin.feature.settings.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * ヘルプ・FAQ画面のViewModel
 *
 * 機能:
 * - FAQデータの管理
 * - カテゴリデータの管理
 * - 検索機能
 */
class HelpFaqViewModel : ScreenModel {
    private val _state = MutableStateFlow(
        HelpFaqContract.State(
            faqItems = getDefaultFaqItems(),
            categories = getDefaultCategories()
        )
    )
    val state: StateFlow<HelpFaqContract.State> = _state.asStateFlow()

    private val _effect = Channel<HelpFaqContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /**
     * Intentを処理
     */
    fun onIntent(intent: HelpFaqContract.Intent) {
        when (intent) {
            is HelpFaqContract.Intent.OnScreenDisplayed -> handleScreenDisplayed()
            is HelpFaqContract.Intent.OnBackPressed -> handleBackPressed()
            is HelpFaqContract.Intent.OnSearchQueryChanged -> handleSearchQueryChanged(intent.query)
            is HelpFaqContract.Intent.OnFaqItemClicked -> handleFaqItemClicked(intent.faqItem)
            is HelpFaqContract.Intent.OnCategoryClicked -> handleCategoryClicked(intent.category)
            is HelpFaqContract.Intent.OnContactSupportPressed -> handleContactSupportPressed()
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
            _effect.send(HelpFaqContract.Effect.NavigateBack)
        }
    }

    /**
     * 検索クエリ変更の処理
     */
    private fun handleSearchQueryChanged(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        // TODO: 実際の検索機能を実装
    }

    /**
     * FAQアイテムクリックの処理
     */
    private fun handleFaqItemClicked(faqItem: HelpFaqContract.FaqItem) {
        screenModelScope.launch {
            _effect.send(HelpFaqContract.Effect.NavigateToFaqDetail(faqItem))
        }
    }

    /**
     * カテゴリクリックの処理
     */
    private fun handleCategoryClicked(category: HelpFaqContract.Category) {
        screenModelScope.launch {
            _effect.send(HelpFaqContract.Effect.NavigateToCategoryDetail(category))
        }
    }

    /**
     * お問い合わせボタンの処理
     */
    private fun handleContactSupportPressed() {
        screenModelScope.launch {
            _effect.send(HelpFaqContract.Effect.NavigateToContactSupport)
        }
    }

    /**
     * デフォルトのFAQデータを取得
     * TODO: 実際のAPIから取得
     */
    private fun getDefaultFaqItems(): List<HelpFaqContract.FaqItem> {
        return listOf(
            HelpFaqContract.FaqItem(
                icon = "📸",
                question = "写真は何枚まで撮れますか？",
                answer = "1日24枚まで（プレミアムは48枚）"
            ),
            HelpFaqContract.FaqItem(
                icon = "🎞",
                question = "現像はいつされますか？",
                answer = "旅行終了日の翌朝9:00"
            ),
            HelpFaqContract.FaqItem(
                icon = "👥",
                question = "メンバーを招待するには？",
                answer = "招待コードまたはQRコードで共有"
            ),
            HelpFaqContract.FaqItem(
                icon = "💳",
                question = "支払い方法は何がありますか？",
                answer = "クレジットカード、Apple Pay"
            )
        )
    }

    /**
     * デフォルトのカテゴリデータを取得
     * TODO: 実際のAPIから取得
     */
    private fun getDefaultCategories(): List<HelpFaqContract.Category> {
        return listOf(
            HelpFaqContract.Category(
                icon = "📱",
                title = "アプリの使い方",
                categoryType = HelpFaqContract.CategoryType.APP_USAGE
            ),
            HelpFaqContract.Category(
                icon = "💰",
                title = "料金・プラン",
                categoryType = HelpFaqContract.CategoryType.PRICING_PLAN
            ),
            HelpFaqContract.Category(
                icon = "📦",
                title = "注文・配送",
                categoryType = HelpFaqContract.CategoryType.ORDER_SHIPPING
            )
        )
    }
}
