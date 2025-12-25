package com.yoin.feature.settings.viewmodel

/**
 * プレミアムプラン画面のMVIコントラクト
 *
 * 機能:
 * - プレミアムプランの特典表示
 * - 価格と年払い割引の表示
 * - 無料トライアル開始
 * - プラン詳細比較画面への遷移
 */
interface PremiumPlanContract {
    /**
     * 画面の状態
     */
    data class State(
        val isLoading: Boolean = false,
        val monthlyPrice: String = "¥480",
        val yearlyPrice: String = "¥3,800",
        val yearlySavings: String = "2ヶ月分お得",
        val benefits: List<PlanBenefit> = emptyList(),
        val trialDays: Int = 7,
        val error: String? = null,
    )

    /**
     * プラン特典
     */
    data class PlanBenefit(
        val icon: String,
        val title: String,
        val description: String,
        val iconBackgroundColor: Long = 0xFFF5EDE3, // ベージュ色
    )

    /**
     * ユーザーの操作
     */
    sealed interface Intent {
        /**
         * 画面が表示された
         */
        data object OnScreenDisplayed : Intent

        /**
         * 閉じるボタンがタップされた
         */
        data object OnClosePressed : Intent

        /**
         * 無料トライアルボタンがタップされた
         */
        data object OnStartTrialPressed : Intent

        /**
         * プラン詳細比較リンクがタップされた
         */
        data object OnComparePlansPressed : Intent
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
         * プラン比較画面に遷移
         */
        data object NavigateToPlanComparison : Effect

        /**
         * サブスクリプション購入フローを開始
         */
        data object StartSubscriptionFlow : Effect

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
