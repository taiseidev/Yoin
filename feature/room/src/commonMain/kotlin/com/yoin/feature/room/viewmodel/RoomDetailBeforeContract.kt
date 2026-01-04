package com.yoin.feature.room.viewmodel

/**
 * ルーム詳細（現像前）画面のMVI Contract
 *
 * 旅行中にユーザーが最も頻繁に見る画面
 * - 撮影進捗の確認
 * - 現像までのカウントダウン
 * - カメラへの導線
 */
object RoomDetailBeforeContract {
    /**
     * UI状態
     */
    data class State(
        val isLoading: Boolean = false,
        val roomDetail: RoomDetail? = null,
        val errorMessage: String? = null
    )

    /**
     * ルーム詳細情報
     */
    data class RoomDetail(
        val id: String,
        val emoji: String,
        val name: String,
        val dateRange: String,
        val location: String,
        val daysUntilDevelopment: Int,
        val developmentDateTime: String,
        val todayPhotos: Int,
        val maxPhotos: Int,
        val members: List<Member>,
        val roomStatus: RoomStatus
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
            get() = if (maxPhotos > 0) todayPhotos.toFloat() / maxPhotos.toFloat() else 0f

        /**
         * 撮影可能かどうか
         */
        val canTakePhoto: Boolean
            get() = roomStatus == RoomStatus.ACTIVE && todayPhotos < maxPhotos
    }

    /**
     * ルームステータス
     */
    enum class RoomStatus {
        UPCOMING,           // 旅行開始前
        ACTIVE,             // 旅行中（撮影可能）
        PENDING_DEVELOPMENT,// 旅行終了、現像待ち
        DEVELOPED           // 現像済み
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
        data class OnScreenDisplayed(val roomId: String) : Intent

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

        /**
         * リフレッシュ
         */
        data object OnRefresh : Intent
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
        data class NavigateToInvite(val roomId: String) : Effect

        /**
         * メンバー一覧画面へ遷移
         */
        data class NavigateToMembers(val roomId: String) : Effect

        /**
         * カメラ画面へ遷移
         */
        data class NavigateToCamera(val roomId: String) : Effect

        /**
         * ルーム設定画面へ遷移
         */
        data class NavigateToSettings(val roomId: String) : Effect

        /**
         * 地図フルスクリーン画面へ遷移
         */
        data class NavigateToMap(val roomId: String) : Effect
    }
}
