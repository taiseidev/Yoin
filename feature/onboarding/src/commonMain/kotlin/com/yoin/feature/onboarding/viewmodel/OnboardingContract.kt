package com.yoin.feature.onboarding.viewmodel

import com.yoin.domain.common.model.OnboardingPage

/**
 * オンボーディング画面のMVIコントラクト
 */
interface OnboardingContract {

    /**
     * 画面の状態
     */
    data class State(
        val pages: List<OnboardingPage> = emptyList(),
        val currentPage: Int = 0,
        val isLastPage: Boolean = false,
    )

    /**
     * ユーザーの意図（アクション）
     */
    sealed interface Intent {
        data object NextPage : Intent
        data object PreviousPage : Intent
        data class PageChanged(val page: Int) : Intent
        data object Skip : Intent
        data object GetStarted : Intent
    }

    /**
     * 副作用（ナビゲーションなど）
     */
    sealed interface Effect {
        data object NavigateToLogin : Effect
    }
}
