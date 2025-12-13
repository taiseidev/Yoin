package com.taiseidev.yoin

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.taiseidev.yoin.di.viewModelModule
import com.taiseidev.yoin.ui.screens.splash.SplashScreen
import com.taiseidev.yoin.ui.screens.splash.SplashViewModel
import com.yoin.di.appModule
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    KoinApplication(
        application = {
            modules(appModule, viewModelModule)
        }
    ) {
        MaterialTheme {
            YoinNavigation()
        }
    }
}

@Composable
private fun YoinNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            val viewModel: SplashViewModel = koinViewModel()
            SplashScreen(
                viewModel = viewModel,
                onNavigateToMain = {
                    navController.navigate("main") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            // 仮のメイン画面（後で実装）
            MainScreen()
        }
    }
}

@Composable
private fun MainScreen() {
    // 仮のメイン画面
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text("メイン画面（実装予定）")
    }
}