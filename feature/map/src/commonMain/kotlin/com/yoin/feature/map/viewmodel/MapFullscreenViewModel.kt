package com.yoin.feature.map.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.yoin.feature.map.model.MapMember
import com.yoin.feature.map.model.PhotoLocation
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 地図フルスクリーン画面のScreenModel
 *
 * @param roomId ルームID
 */
class MapFullscreenViewModel(private val roomId: String) : ScreenModel {

    private val _state = MutableStateFlow(MapFullscreenContract.State())
    val state: StateFlow<MapFullscreenContract.State> = _state.asStateFlow()

    private val _effect = Channel<MapFullscreenContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadMapData()
    }

    /**
     * ユーザーの意図を処理
     */
    fun handleIntent(intent: MapFullscreenContract.Intent) {
        when (intent) {
            is MapFullscreenContract.Intent.OnBackPressed -> onBackPressed()
            is MapFullscreenContract.Intent.OnMenuPressed -> onMenuPressed()
            is MapFullscreenContract.Intent.OnMemberFilterSelected -> onMemberFilterSelected(intent.memberId)
            is MapFullscreenContract.Intent.OnPhotoMarkerTapped -> onPhotoMarkerTapped(intent.photoId)
            is MapFullscreenContract.Intent.OnZoomInPressed -> onZoomInPressed()
            is MapFullscreenContract.Intent.OnZoomOutPressed -> onZoomOutPressed()
            is MapFullscreenContract.Intent.OnCurrentLocationPressed -> onCurrentLocationPressed()
            is MapFullscreenContract.Intent.OnPhotoCardTapped -> onPhotoCardTapped()
        }
    }

    private fun loadMapData() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // TODO: 実際のデータ取得処理を実装
            kotlinx.coroutines.delay(300)

            // ダミーデータ
            val dummyMembers = listOf(
                MapMember(id = "1", name = "田中太郎", color = "#E8D4BC"),
                MapMember(id = "2", name = "佐藤花子", color = "#E8D4BC"),
                MapMember(id = "3", name = "鈴木健一", color = "#E8D4BC"),
                MapMember(id = "4", name = "高橋美咲", color = "#E8D4BC")
            )

            val dummyPhotos = listOf(
                PhotoLocation(
                    photoId = "1",
                    thumbnailUrl = null,
                    title = "小樽運河",
                    photographer = "田中太郎",
                    timestamp = "7/4 14:32",
                    location = "北海道小樽市港町",
                    latitude = 43.194230,
                    longitude = 140.995300
                ),
                PhotoLocation(
                    photoId = "2",
                    thumbnailUrl = null,
                    title = "札幌時計台",
                    photographer = "佐藤花子",
                    timestamp = "7/3 10:15",
                    location = "北海道札幌市中央区",
                    latitude = 43.063910,
                    longitude = 141.353880
                ),
                PhotoLocation(
                    photoId = "3",
                    thumbnailUrl = null,
                    title = "大通公園",
                    photographer = "鈴木健一",
                    timestamp = "7/3 16:20",
                    location = "北海道札幌市中央区",
                    latitude = 43.060000,
                    longitude = 141.350000
                ),
                PhotoLocation(
                    photoId = "4",
                    thumbnailUrl = null,
                    title = "すすきの",
                    photographer = "高橋美咲",
                    timestamp = "7/4 19:45",
                    location = "北海道札幌市中央区",
                    latitude = 43.054230,
                    longitude = 141.352560
                )
            )

            _state.update {
                it.copy(
                    isLoading = false,
                    roomTitle = "北海道旅行2025",
                    members = dummyMembers,
                    photos = dummyPhotos,
                    selectedPhotoId = dummyPhotos.firstOrNull()?.photoId,
                    totalPhotoCount = 12,
                    centerLatitude = 43.064170,
                    centerLongitude = 141.346936
                )
            }
        }
    }

    private fun onBackPressed() {
        screenModelScope.launch {
            _effect.send(MapFullscreenContract.Effect.NavigateBack)
        }
    }

    private fun onMenuPressed() {
        screenModelScope.launch {
            _effect.send(MapFullscreenContract.Effect.ShowMenu(roomId))
        }
    }

    private fun onMemberFilterSelected(memberId: String?) {
        _state.update { it.copy(selectedMemberId = memberId) }
    }

    private fun onPhotoMarkerTapped(photoId: String) {
        _state.update { it.copy(selectedPhotoId = photoId) }
    }

    private fun onZoomInPressed() {
        _state.update { it.copy(zoomLevel = (it.zoomLevel + 1f).coerceAtMost(18f)) }
    }

    private fun onZoomOutPressed() {
        _state.update { it.copy(zoomLevel = (it.zoomLevel - 1f).coerceAtLeast(3f)) }
    }

    private fun onCurrentLocationPressed() {
        screenModelScope.launch {
            _effect.send(MapFullscreenContract.Effect.MoveToCurrentLocation)
        }
    }

    private fun onPhotoCardTapped() {
        screenModelScope.launch {
            _state.value.selectedPhotoId?.let { photoId ->
                _effect.send(MapFullscreenContract.Effect.NavigateToPhotoDetail(roomId, photoId))
            }
        }
    }
}
