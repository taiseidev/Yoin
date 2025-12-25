package com.yoin.feature.settings.viewmodel

import com.yoin.feature.settings.model.NotificationSettings

/**
 * 通知設定画面のMVIコントラクト
 */
interface NotificationSettingsContract {

    /**
     * 画面の状態
     */
    data class State(
        val isLoading: Boolean = false,
        val settings: NotificationSettings = NotificationSettings(),
        val error: String? = null,
    )

    /**
     * ユーザーの意図（アクション）
     */
    sealed interface Intent {
        data object OnBackPressed : Intent
        data class OnPushNotificationToggled(val enabled: Boolean) : Intent
        data class OnTripInvitationToggled(val enabled: Boolean) : Intent
        data class OnMemberJoinedToggled(val enabled: Boolean) : Intent
        data class OnTripReminderToggled(val enabled: Boolean) : Intent
        data class OnNewFeatureToggled(val enabled: Boolean) : Intent
        data class OnCampaignToggled(val enabled: Boolean) : Intent
    }

    /**
     * 副作用（ナビゲーションなど）
     */
    sealed interface Effect {
        data object NavigateBack : Effect
        data class ShowError(val message: String) : Effect
        data class ShowSuccess(val message: String) : Effect
    }
}
