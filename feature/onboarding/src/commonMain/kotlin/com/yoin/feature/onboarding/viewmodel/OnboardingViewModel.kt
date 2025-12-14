package com.yoin.feature.onboarding.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.yoin.domain.common.model.OnboardingPages
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * オンボーディング画面のScreenModel
 */
class OnboardingViewModel : ScreenModel {

    private val _state = MutableStateFlow(
        OnboardingContract.State(
            pages = OnboardingPages.pages,
            currentPage = 0,
            isLastPage = false
        )
    )
    val state: StateFlow<OnboardingContract.State> = _state.asStateFlow()

    private val _effect = Channel<OnboardingContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /**
     * ユーザーの意図を処理
     */
    fun handleIntent(intent: OnboardingContract.Intent) {
        when (intent) {
            is OnboardingContract.Intent.NextPage -> onNextPage()
            is OnboardingContract.Intent.PreviousPage -> onPreviousPage()
            is OnboardingContract.Intent.PageChanged -> onPageChanged(intent.page)
            is OnboardingContract.Intent.Skip -> onSkip()
            is OnboardingContract.Intent.GetStarted -> onGetStarted()
        }
    }

    private fun onNextPage() {
        val currentPage = _state.value.currentPage
        val totalPages = _state.value.pages.size
        if (currentPage < totalPages - 1) {
            onPageChanged(currentPage + 1)
        }
    }

    private fun onPreviousPage() {
        val currentPage = _state.value.currentPage
        if (currentPage > 0) {
            onPageChanged(currentPage - 1)
        }
    }

    private fun onPageChanged(page: Int) {
        _state.update { currentState ->
            currentState.copy(
                currentPage = page,
                isLastPage = page == currentState.pages.size - 1
            )
        }
    }

    private fun onSkip() {
        navigateToLogin()
    }

    private fun onGetStarted() {
        navigateToLogin()
    }

    private fun navigateToLogin() {
        screenModelScope.launch {
            _effect.send(OnboardingContract.Effect.NavigateToLogin)
        }
    }
}
