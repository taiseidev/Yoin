package com.yoin.feature.onboarding.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.yoin.domain.common.model.InitializationState
import com.yoin.domain.common.usecase.InitializeAppUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * スプラッシュ画面のScreenModel
 */
class SplashViewModel(
    private val initializeAppUseCase: InitializeAppUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(SplashContract.State())
    val state: StateFlow<SplashContract.State> = _state.asStateFlow()

    private val _effect = Channel<SplashContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        observeInitializationState()
    }

    /**
     * Intentを処理する
     */
    fun handleIntent(intent: SplashContract.Intent) {
        when (intent) {
            is SplashContract.Intent.StartInitialization -> startInitialization()
        }
    }

    /**
     * 初期化状態を監視する
     */
    private fun observeInitializationState() {
        screenModelScope.launch {
            initializeAppUseCase.observeState().collect { initState ->
                _state.update { it.copy(initializationState = initState) }

                when (initState) {
                    is InitializationState.Completed -> {
                        _effect.send(SplashContract.Effect.NavigateToMain)
                    }

                    is InitializationState.Failed -> {
                        _effect.send(SplashContract.Effect.ShowError(initState.error))
                    }

                    else -> { /* 他の状態は特に処理しない */
                    }
                }
            }
        }
    }

    /**
     * 初期化を開始する
     */
    private fun startInitialization() {
        screenModelScope.launch {
            initializeAppUseCase()
        }
    }
}
