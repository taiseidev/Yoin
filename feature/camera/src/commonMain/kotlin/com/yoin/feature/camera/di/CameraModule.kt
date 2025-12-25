package com.yoin.feature.camera.di

import com.yoin.feature.camera.viewmodel.CameraViewModel
import com.yoin.feature.camera.viewmodel.PhotoConfirmViewModel
import org.koin.dsl.module

/**
 * カメラ機能のDIモジュール
 */
val cameraModule = module {
    factory { CameraViewModel() }
    factory { PhotoConfirmViewModel() }
}
