package com.yoin.app

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/**
 * iOS用のメインビューコントローラ
 *
 * SwiftUIからCompose UIを呼び出すためのエントリーポイント
 */
fun MainViewController(): UIViewController {
    return ComposeUIViewController {
        App()
    }
}
