package com.yoin.app.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition

/**
 * Yoinアプリのナビゲーションホスト（Voyager版）
 *
 * 画面構成:
 * - SplashScreenVoyager: スプラッシュ画面（初期画面）
 * - OnboardingScreenVoyager: オンボーディング画面
 * - LoginScreenVoyager: ログイン画面
 * - MainScreenVoyager: メイン画面（仮実装）
 */
@Composable
fun YoinNavHost() {
    Navigator(SplashScreenVoyager()) { navigator ->
        SlideTransition(navigator)
    }
}
