package com.yoin.app

import androidx.compose.runtime.Composable
import com.yoin.app.navigation.YoinNavHost
import com.yoin.core.design.theme.YoinTheme
import com.yoin.data.local.di.localDataModule
import com.yoin.feature.auth.di.authModule
import com.yoin.feature.onboarding.di.onboardingModule
import org.koin.compose.KoinApplication

/**
 * Yoinアプリのルートコンポーザブル
 *
 * 責務:
 * - Koinによる依存性注入の初期化
 * - MaterialThemeの適用
 * - Voyagerナビゲーションホストの統合
 */
@Composable
fun App() {
    KoinApplication(
        application = {
            // DIモジュールの登録
            modules(
                // データ層
                localDataModule,

                // フィーチャー層
                onboardingModule,
                authModule,
            )
        }
    ) {
        YoinTheme {
            YoinNavHost()
        }
    }
}
