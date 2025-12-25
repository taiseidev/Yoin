package com.yoin.feature.settings.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 設定画面のViewModel
 *
 * MVIパターンに基づいた状態管理:
 * - State: ユーザープロフィールと設定状態
 * - Intent: ユーザーアクション
 * - Effect: 一時的なイベント（ナビゲーション、エラー表示）
 */
class SettingsViewModel : ScreenModel {
    private val _state = MutableStateFlow(SettingsContract.State())
    val state: StateFlow<SettingsContract.State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SettingsContract.Effect>()
    val effect: SharedFlow<SettingsContract.Effect> = _effect.asSharedFlow()

    fun onIntent(intent: SettingsContract.Intent) {
        when (intent) {
            is SettingsContract.Intent.OnScreenDisplayed -> handleScreenDisplayed()
            is SettingsContract.Intent.OnProfilePressed -> handleProfilePressed()
            is SettingsContract.Intent.OnPlanPressed -> handlePlanPressed()
            is SettingsContract.Intent.OnNotificationPressed -> handleNotificationPressed()
            is SettingsContract.Intent.OnDarkModeToggled -> handleDarkModeToggled(intent.enabled)
            is SettingsContract.Intent.OnHelpPressed -> handleHelpPressed()
            is SettingsContract.Intent.OnContactPressed -> handleContactPressed()
            is SettingsContract.Intent.OnTermsPressed -> handleTermsPressed()
            is SettingsContract.Intent.OnPrivacyPolicyPressed -> handlePrivacyPolicyPressed()
        }
    }

    private fun handleScreenDisplayed() {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                // TODO: 実際のユーザー情報とプラン情報を取得
                delay(300)

                val userProfile = SettingsContract.UserProfile(
                    name = "山田花子",
                    email = "yamada@example.com",
                    initial = "山"
                )

                val planInfo = SettingsContract.PlanInfo(
                    name = "プレミアムプラン",
                    description = "無制限の旅を楽しもう",
                    isPremium = true
                )

                _state.value = _state.value.copy(
                    isLoading = false,
                    userProfile = userProfile,
                    plan = planInfo
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
                _effect.emit(SettingsContract.Effect.ShowError(e.message ?: "データの読み込みに失敗しました"))
            }
        }
    }

    private fun handleProfilePressed() {
        screenModelScope.launch {
            _effect.emit(SettingsContract.Effect.NavigateToProfile)
        }
    }

    private fun handlePlanPressed() {
        screenModelScope.launch {
            _effect.emit(SettingsContract.Effect.NavigateToPlan)
        }
    }

    private fun handleNotificationPressed() {
        screenModelScope.launch {
            _effect.emit(SettingsContract.Effect.NavigateToNotification)
        }
    }

    private fun handleDarkModeToggled(enabled: Boolean) {
        _state.value = _state.value.copy(isDarkModeEnabled = enabled)
        // TODO: 実際のダークモード設定を保存
    }

    private fun handleHelpPressed() {
        screenModelScope.launch {
            _effect.emit(SettingsContract.Effect.NavigateToHelp)
        }
    }

    private fun handleContactPressed() {
        screenModelScope.launch {
            _effect.emit(SettingsContract.Effect.NavigateToContact)
        }
    }

    private fun handleTermsPressed() {
        screenModelScope.launch {
            _effect.emit(SettingsContract.Effect.NavigateToTerms)
        }
    }

    private fun handlePrivacyPolicyPressed() {
        screenModelScope.launch {
            _effect.emit(SettingsContract.Effect.NavigateToPrivacyPolicy)
        }
    }
}
