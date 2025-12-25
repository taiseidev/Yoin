package com.yoin.feature.home.viewmodel

/**
 * 旅行詳細画面のMVI Contract
 */
object TripDetailContract {
    /**
     * UI状態
     */
    data class State(
        val isLoading: Boolean = false,
        val tripDetail: TripDetail? = null,
        val errorMessage: String? = null
    )

    /**
     * 旅行詳細情報
     */
    data class TripDetail(
        val id: String,
        val emoji: String,
        val title: String,
        val dateRange: String,
        val location: String,
        val daysUntilDevelopment: Int,
        val developmentDateTime: String,
        val todayPhotos: Int,
        val maxPhotos: Int,
        val members: List<Member>
    ) {
        /**
         * 撮影可能残り枚数
         */
        val remainingPhotos: Int
            get() = maxPhotos - todayPhotos

        /**
         * 撮影進捗（0.0-1.0）
         */
        val photoProgress: Float
            get() = todayPhotos.toFloat() / maxPhotos.toFloat()
    }

    /**
     * メンバー情報
     */
    data class Member(
        val id: String,
        val name: String,
        val avatarUrl: String? = null,
        val isCurrentUser: Boolean = false
    )

    /**
     * ユーザーインテント（アクション）
     */
    sealed interface Intent {
        /**
         * 画面が表示された
         */
        data class OnScreenDisplayed(val tripId: String) : Intent

        /**
         * 戻るボタンをタップ
         */
        data object OnBackPressed : Intent

        /**
         * 招待ボタンをタップ
         */
        data object OnInvitePressed : Intent

        /**
         * メンバー一覧をタップ
         */
        data object OnMembersPressed : Intent

        /**
         * 撮影ボタンをタップ
         */
        data object OnCameraPressed : Intent

        /**
         * 設定ボタンをタップ
         */
        data object OnSettingsPressed : Intent

        /**
         * 地図表示ボタンをタップ
         */
        data object OnMapPressed : Intent
    }

    /**
     * 一時的なイベント（副作用）
     */
    sealed interface Effect {
        /**
         * エラーメッセージを表示
         */
        data class ShowError(val message: String) : Effect

        /**
         * 前の画面に戻る
         */
        data object NavigateBack : Effect

        /**
         * 招待画面へ遷移
         */
        data class NavigateToInvite(val tripId: String) : Effect

        /**
         * メンバー一覧画面へ遷移
         */
        data class NavigateToMembers(val tripId: String) : Effect

        /**
         * カメラ画面へ遷移
         */
        data class NavigateToCamera(val tripId: String) : Effect

        /**
         * ルーム設定画面へ遷移
         */
        data class NavigateToSettings(val tripId: String) : Effect

        /**
         * 地図フルスクリーン画面へ遷移
         */
        data class NavigateToMap(val tripId: String) : Effect
    }
}
