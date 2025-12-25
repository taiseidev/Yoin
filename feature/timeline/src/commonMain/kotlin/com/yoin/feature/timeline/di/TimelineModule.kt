package com.yoin.feature.timeline.di

import com.yoin.feature.timeline.viewmodel.DownloadProgressViewModel
import com.yoin.feature.timeline.viewmodel.PhotoDetailViewModel
import com.yoin.feature.timeline.viewmodel.RoomDetailAfterViewModel
import com.yoin.feature.timeline.viewmodel.TimelineViewModel
import org.koin.dsl.module

/**
 * タイムライン機能のDIモジュール
 */
val timelineModule = module {
    factory { TimelineViewModel() }
    factory { RoomDetailAfterViewModel() }
    factory { PhotoDetailViewModel() }
    factory { (totalCount: Int) ->
        DownloadProgressViewModel(totalCount)
    }
}
