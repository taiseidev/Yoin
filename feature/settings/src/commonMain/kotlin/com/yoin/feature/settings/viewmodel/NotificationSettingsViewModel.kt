package com.yoin.feature.settings.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 通知設定画面のScreenModel
 *
 * 注意: 現在はUI実装のみで、実際の設定保存ロジックは未実装です。
 */
class NotificationSettingsViewModel : ScreenModel {

    private val _state = MutableStateFlow(NotificationSettingsContract.State())
    val state: StateFlow<NotificationSettingsContract.State> = _state.asStateFlow()

    private val _effect = Channel<NotificationSettingsContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadSettings()
    }

    /**
     * ユーザーの意図を処理
     */
    fun handleIntent(intent: NotificationSettingsContract.Intent) {
        when (intent) {
            is NotificationSettingsContract.Intent.OnBackPressed -> onBackPressed()
            is NotificationSettingsContract.Intent.OnPushNotificationToggled -> onPushNotificationToggled(intent.enabled)
            is NotificationSettingsContract.Intent.OnTripInvitationToggled -> onTripInvitationToggled(intent.enabled)
            is NotificationSettingsContract.Intent.OnMemberJoinedToggled -> onMemberJoinedToggled(intent.enabled)
            is NotificationSettingsContract.Intent.OnTripReminderToggled -> onTripReminderToggled(intent.enabled)
            is NotificationSettingsContract.Intent.OnNewFeatureToggled -> onNewFeatureToggled(intent.enabled)
            is NotificationSettingsContract.Intent.OnCampaignToggled -> onCampaignToggled(intent.enabled)
        }
    }

    private fun loadSettings() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // TODO: 実際の設定データ取得処理を実装
            // 現在はデフォルト値を使用
            kotlinx.coroutines.delay(300) // ネットワーク遅延をシミュレート

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun onBackPressed() {
        screenModelScope.launch {
            _effect.send(NotificationSettingsContract.Effect.NavigateBack)
        }
    }

    private fun onPushNotificationToggled(enabled: Boolean) {
        screenModelScope.launch {
            val currentSettings = _state.value.settings

            _state.update {
                it.copy(
                    settings = currentSettings.copy(
                        pushNotificationEnabled = enabled,
                        // プッシュ通知がOFFの場合、すべての通知をOFFにする
                        tripInvitationEnabled = if (enabled) currentSettings.tripInvitationEnabled else false,
                        memberJoinedEnabled = if (enabled) currentSettings.memberJoinedEnabled else false,
                        tripReminderEnabled = if (enabled) currentSettings.tripReminderEnabled else false,
                        newFeatureEnabled = if (enabled) currentSettings.newFeatureEnabled else false,
                        campaignEnabled = if (enabled) currentSettings.campaignEnabled else false,
                        // 現像完了は常にON
                    )
                )
            }

            // TODO: 設定を保存する処理を実装
            saveSettings()
        }
    }

    private fun onTripInvitationToggled(enabled: Boolean) {
        screenModelScope.launch {
            _state.update {
                it.copy(
                    settings = it.settings.copy(
                        tripInvitationEnabled = enabled
                    )
                )
            }
            saveSettings()
        }
    }

    private fun onMemberJoinedToggled(enabled: Boolean) {
        screenModelScope.launch {
            _state.update {
                it.copy(
                    settings = it.settings.copy(
                        memberJoinedEnabled = enabled
                    )
                )
            }
            saveSettings()
        }
    }

    private fun onTripReminderToggled(enabled: Boolean) {
        screenModelScope.launch {
            _state.update {
                it.copy(
                    settings = it.settings.copy(
                        tripReminderEnabled = enabled
                    )
                )
            }
            saveSettings()
        }
    }

    private fun onNewFeatureToggled(enabled: Boolean) {
        screenModelScope.launch {
            _state.update {
                it.copy(
                    settings = it.settings.copy(
                        newFeatureEnabled = enabled
                    )
                )
            }
            saveSettings()
        }
    }

    private fun onCampaignToggled(enabled: Boolean) {
        screenModelScope.launch {
            _state.update {
                it.copy(
                    settings = it.settings.copy(
                        campaignEnabled = enabled
                    )
                )
            }
            saveSettings()
        }
    }

    private suspend fun saveSettings() {
        // TODO: 実際の設定保存処理を実装
        kotlinx.coroutines.delay(100) // ネットワーク遅延をシミュレート
    }
}
