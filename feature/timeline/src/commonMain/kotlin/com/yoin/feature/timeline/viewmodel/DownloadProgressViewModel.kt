package com.yoin.feature.timeline.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * ダウンロード進捗ダイアログのViewModel
 *
 * 機能:
 * - ダウンロード進捗の管理
 * - 進捗バーの更新
 * - 残り時間の推定
 * - ダウンロードのキャンセル
 */
class DownloadProgressViewModel(
    private val totalCount: Int
) : ScreenModel {
    private val _state = MutableStateFlow(
        DownloadProgressContract.State(
            isDownloading = true,
            currentCount = 0,
            totalCount = totalCount,
            progressPercentage = 0,
            estimatedSeconds = 0
        )
    )
    val state: StateFlow<DownloadProgressContract.State> = _state.asStateFlow()

    private val _effect = Channel<DownloadProgressContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var downloadJob: Job? = null

    /**
     * Intentを処理
     */
    fun onIntent(intent: DownloadProgressContract.Intent) {
        when (intent) {
            is DownloadProgressContract.Intent.OnDialogDisplayed -> handleDialogDisplayed()
            is DownloadProgressContract.Intent.OnCancelPressed -> handleCancelPressed()
            is DownloadProgressContract.Intent.OnProgressUpdated -> handleProgressUpdated(
                intent.currentCount,
                intent.totalCount
            )
        }
    }

    /**
     * ダイアログ表示時の処理
     */
    private fun handleDialogDisplayed() {
        startDownload()
    }

    /**
     * キャンセルボタンの処理
     */
    private fun handleCancelPressed() {
        screenModelScope.launch {
            downloadJob?.cancel()
            _state.value = _state.value.copy(isDownloading = false)
            _effect.send(DownloadProgressContract.Effect.CancelDownload)
            _effect.send(DownloadProgressContract.Effect.DismissDialog)
        }
    }

    /**
     * 進捗更新の処理
     */
    private fun handleProgressUpdated(currentCount: Int, totalCount: Int) {
        val progressPercentage = if (totalCount > 0) {
            ((currentCount.toFloat() / totalCount) * 100).toInt()
        } else {
            0
        }

        val estimatedSeconds = calculateEstimatedTime(currentCount, totalCount)

        _state.value = _state.value.copy(
            currentCount = currentCount,
            totalCount = totalCount,
            progressPercentage = progressPercentage,
            estimatedSeconds = estimatedSeconds
        )
    }

    /**
     * 残り時間の推定
     * 各アイテムのダウンロードに200msかかると仮定
     */
    private fun calculateEstimatedTime(currentCount: Int, totalCount: Int): Int {
        val remainingItems = totalCount - currentCount
        val estimatedMillis = remainingItems * 200L
        return (estimatedMillis / 1000).toInt().coerceAtLeast(1)
    }

    /**
     * ダウンロードのシミュレーション（モック実装）
     */
    private fun startDownload() {
        downloadJob = screenModelScope.launch {
            try {
                for (i in 1..totalCount) {
                    // 各ファイルのダウンロードをシミュレート（200ms）
                    delay(200)

                    if (!_state.value.isDownloading) {
                        break
                    }

                    onIntent(
                        DownloadProgressContract.Intent.OnProgressUpdated(
                            currentCount = i,
                            totalCount = totalCount
                        )
                    )
                }

                if (_state.value.isDownloading && _state.value.currentCount == totalCount) {
                    _state.value = _state.value.copy(isDownloading = false)
                    _effect.send(DownloadProgressContract.Effect.ShowDownloadComplete)
                    delay(500)
                    _effect.send(DownloadProgressContract.Effect.DismissDialog)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isDownloading = false)
                _effect.send(DownloadProgressContract.Effect.ShowError("ダウンロードに失敗しました"))
            }
        }
    }

    override fun onDispose() {
        super.onDispose()
        downloadJob?.cancel()
    }
}
