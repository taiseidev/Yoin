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
 * プレミアムプラン画面のViewModel
 *
 * 機能:
 * - プレミアムプランの特典を表示
 * - 無料トライアルの開始
 * - サブスクリプション購入フローの管理
 */
class PremiumPlanViewModel : ScreenModel {
    private val _state = MutableStateFlow(
        PremiumPlanContract.State(
            benefits = getDefaultBenefits()
        )
    )
    val state: StateFlow<PremiumPlanContract.State> = _state.asStateFlow()

    private val _effect = Channel<PremiumPlanContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /**
     * Intentを処理
     */
    fun onIntent(intent: PremiumPlanContract.Intent) {
        when (intent) {
            is PremiumPlanContract.Intent.OnScreenDisplayed -> handleScreenDisplayed()
            is PremiumPlanContract.Intent.OnClosePressed -> handleClosePressed()
            is PremiumPlanContract.Intent.OnStartTrialPressed -> handleStartTrialPressed()
            is PremiumPlanContract.Intent.OnComparePlansPressed -> handleComparePlansPressed()
        }
    }

    /**
     * 画面表示時の処理
     */
    private fun handleScreenDisplayed() {
        screenModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = false,
                benefits = getDefaultBenefits()
            )
        }
    }

    /**
     * 閉じるボタンの処理
     */
    private fun handleClosePressed() {
        screenModelScope.launch {
            _effect.send(PremiumPlanContract.Effect.NavigateBack)
        }
    }

    /**
     * 無料トライアル開始の処理
     */
    private fun handleStartTrialPressed() {
        screenModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)

                // サブスクリプション購入フローを開始
                _effect.send(PremiumPlanContract.Effect.StartSubscriptionFlow)

                _state.value = _state.value.copy(isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
                _effect.send(
                    PremiumPlanContract.Effect.ShowError(
                        "無料トライアルの開始に失敗しました"
                    )
                )
            }
        }
    }

    /**
     * プラン比較の処理
     */
    private fun handleComparePlansPressed() {
        screenModelScope.launch {
            _effect.send(PremiumPlanContract.Effect.NavigateToPlanComparison)
        }
    }

    /**
     * デフォルトの特典リストを取得
     */
    private fun getDefaultBenefits(): List<PremiumPlanContract.PlanBenefit> {
        return listOf(
            PremiumPlanContract.PlanBenefit(
                icon = "∞",
                title = "無制限の旅行作成",
                description = "フリーは月3回まで",
                iconBackgroundColor = 0xFFF5EDE3
            ),
            PremiumPlanContract.PlanBenefit(
                icon = "📷",
                title = "1日48枚まで撮影",
                description = "フリーは24枚まで",
                iconBackgroundColor = 0xFFF5EDE3
            ),
            PremiumPlanContract.PlanBenefit(
                icon = "📅",
                title = "高画質ダウンロード",
                description = "オリジナル画質で保存",
                iconBackgroundColor = 0xFFF5EDE3
            ),
            PremiumPlanContract.PlanBenefit(
                icon = "🎁",
                title = "Shop 10% OFF",
                description = "フォトブックなどがお得",
                iconBackgroundColor = 0xFFF5EDE3
            )
        )
    }
}
