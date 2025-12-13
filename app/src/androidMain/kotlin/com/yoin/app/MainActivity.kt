package com.yoin.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

/**
 * Yoinアプリのメインアクティビティ（Android）
 *
 * 責務:
 * - アプリのエントリーポイント
 * - Edge-to-Edgeの有効化
 * - ComposeのApp()の呼び出し
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-Edge表示を有効化（ステータスバー、ナビゲーションバーまで描画）
        enableEdgeToEdge()

        setContent {
            App()
        }
    }
}
