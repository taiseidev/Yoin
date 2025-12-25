package com.yoin.feature.settings.viewmodel

/**
 * 設定画面のMVI Contract
 *
 * 機能:
 * - ユーザープロフィール表示
 * - プラン管理
 * - 通知設定
 * - ダークモード切り替え
 * - サポートページへのナビゲーション
 */
object SettingsContract {
    data class State(
        val isLoading: Boolean = false,
        val userProfile: UserProfile? = null,
        val plan: PlanInfo? = null,
        val isDarkModeEnabled: Boolean = false,
        val errorMessage: String? = null
    )

    /**
     * ユーザープロフィール情報
     */
    data class UserProfile(
        val name: String,
        val email: String,
        val initial: String
    )

    /**
     * プラン情報
     */
    data class PlanInfo(
        val name: String,
        val description: String,
        val isPremium: Boolean
    )

    sealed interface Intent {
        data object OnScreenDisplayed : Intent
        data object OnProfilePressed : Intent
        data object OnPlanPressed : Intent
        data object OnNotificationPressed : Intent
        data class OnDarkModeToggled(val enabled: Boolean) : Intent
        data object OnHelpPressed : Intent
        data object OnContactPressed : Intent
        data object OnTermsPressed : Intent
        data object OnPrivacyPolicyPressed : Intent
    }

    sealed interface Effect {
        data class ShowError(val message: String) : Effect
        data object NavigateToProfile : Effect
        data object NavigateToPlan : Effect
        data object NavigateToNotification : Effect
        data object NavigateToHelp : Effect
        data object NavigateToContact : Effect
        data object NavigateToTerms : Effect
        data object NavigateToPrivacyPolicy : Effect
    }
}
