package com.yoin.feature.room.viewmodel

/**
 * ルーム作成画面のMVI Contract
 *
 * 機能:
 * - 旅行名、絵文字、期間、目的地の入力
 * - 入力バリデーション
 * - ルーム作成処理
 */
object RoomCreateContract {
    data class State(
        val isLoading: Boolean = false,
        val tripTitle: String = "",
        val emoji: String = "🏔️",
        val startDate: String = "",
        val endDate: String = "",
        val destination: String = "",
        val titleError: String? = null,
        val startDateError: String? = null,
        val endDateError: String? = null,
        val destinationError: String? = null,
        val isFormValid: Boolean = false
    )

    sealed interface Intent {
        data object OnScreenDisplayed : Intent
        data class OnTripTitleChanged(val title: String) : Intent
        data class OnEmojiSelected(val emoji: String) : Intent
        data class OnStartDateChanged(val date: String) : Intent
        data class OnEndDateChanged(val date: String) : Intent
        data class OnDestinationChanged(val destination: String) : Intent
        data object OnStartDatePickerClicked : Intent
        data object OnEndDatePickerClicked : Intent
        data object OnEmojiPickerClicked : Intent
        data object OnCreateButtonClicked : Intent
        data object OnBackPressed : Intent
    }

    sealed interface Effect {
        data class ShowError(val message: String) : Effect
        data object NavigateBack : Effect
        data object ShowStartDatePicker : Effect
        data object ShowEndDatePicker : Effect
        data object ShowEmojiPicker : Effect
        data class NavigateToRoomDetail(val roomId: String) : Effect
        data class ShowSuccess(val message: String) : Effect
    }

    /**
     * よく使われる絵文字のリスト
     */
    val POPULAR_EMOJIS = listOf(
        "🏔️", "🏖️", "🗼", "🏰", "🗾",
        "✈️", "🚗", "🚢", "🚂", "🏕️",
        "🌸", "🍁", "⛄", "🌊", "🌅",
        "🎌", "🗻", "🏯", "⛩️", "🎑"
    )
}
