package com.yoin.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.yoin.feature.auth.ui.LoginScreen
import com.yoin.feature.auth.viewmodel.LoginViewModel
import com.yoin.feature.onboarding.ui.OnboardingScreen
import com.yoin.feature.onboarding.ui.SplashScreen
import com.yoin.feature.onboarding.viewmodel.OnboardingViewModel
import com.yoin.feature.onboarding.viewmodel.SplashViewModel

/**
 * スプラッシュ画面
 */
class SplashScreenVoyager : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: SplashViewModel = koinScreenModel()

        SplashScreen(
            viewModel = viewModel,
            onNavigateToMain = {
                navigator.replace(OnboardingScreenVoyager())
            }
        )
    }
}

/**
 * オンボーディング画面
 */
class OnboardingScreenVoyager : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: OnboardingViewModel = koinScreenModel()

        OnboardingScreen(
            viewModel = viewModel,
            onNavigateToLogin = {
                navigator.replace(LoginScreenVoyager())
            }
        )
    }
}

/**
 * ログイン画面
 */
class LoginScreenVoyager : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: LoginViewModel = koinScreenModel()

        LoginScreen(
            viewModel = viewModel,
            onNavigateToHome = {
                navigator.replace(MainScreenVoyager())
            }
        )
    }
}

/**
 * メイン画面（プレースホルダー）
 */
class MainScreenVoyager : Screen {
    @Composable
    override fun Content() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text("Main Screen (Coming Soon)")
        }
    }
}
