package com.yoin.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yoin.feature.onboarding.ui.OnboardingScreen
import com.yoin.feature.onboarding.ui.SplashScreen
import com.yoin.feature.onboarding.viewmodel.OnboardingViewModel
import com.yoin.feature.onboarding.viewmodel.SplashViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * Yoinアプリのナビゲーションホスト
 *
 * 画面構成:
 * - splash: スプラッシュ画面（初期画面）
 * - onboarding: オンボーディング画面
 * - main: メイン画面（仮実装）
 */
@Composable
fun YoinNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // スプラッシュ画面
        composable("splash") {
            val viewModel: SplashViewModel = koinViewModel()
            SplashScreen(
                viewModel = viewModel,
                onNavigateToMain = {
                    navController.navigate("onboarding") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // オンボーディング画面
        composable("onboarding") {
            val viewModel: OnboardingViewModel = koinViewModel()
            OnboardingScreen(
                viewModel = viewModel,
                onNavigateToLogin = {
                    navController.navigate("main") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        // メイン画面（仮実装）
        composable("main") {
            MainScreenPlaceholder()
        }
    }
}

/**
 * メイン画面のプレースホルダー
 * TODO: 実際のメイン画面実装後に置き換える
 */
@Composable
private fun MainScreenPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text("Main Screen (Coming Soon)")
    }
}
