package com.yoin.app.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.yoin.core.ui.navigation.BottomNavigationItem
import com.yoin.core.ui.navigation.YoinBottomNavigationBar
import com.yoin.feature.home.ui.HomeScreen
import com.yoin.feature.home.viewmodel.HomeViewModel
import com.yoin.feature.profile.ui.ProfileScreen
import com.yoin.feature.shop.ui.ShopScreen
import com.yoin.feature.shop.viewmodel.ShopViewModel
import com.yoin.feature.settings.ui.SettingsScreen
import com.yoin.feature.settings.viewmodel.SettingsViewModel
import com.yoin.feature.timeline.ui.TimelineScreen
import com.yoin.feature.timeline.viewmodel.TimelineViewModel
import org.koin.compose.koinInject

/**
 * メイン画面（ボトムナビゲーション付き）
 *
 * Figmaデザイン「01_home」に基づいた実装:
 * - ホーム、アルバム、Shop、設定のタブ
 * - 中央のFABボタンでルーム作成機能
 */
class MainScreenVoyager : Screen {
    @Composable
    override fun Content() {
        MainScreenWithBottomNav()
    }
}

@Composable
private fun MainScreenWithBottomNav() {
    val navigator = LocalNavigator.currentOrThrow
    // 現在選択されているタブの状態
    var selectedRoute by remember { mutableStateOf(BottomNavigationItem.Home.route) }

    Scaffold(
        bottomBar = {
            YoinBottomNavigationBar(
                selectedRoute = selectedRoute,
                onNavigate = { route ->
                    selectedRoute = route
                },
                onCreatePost = {
                    navigator.push(RoomCreateScreenVoyager())
                }
            )
        }
    ) { paddingValues ->
        // 選択されたタブに応じて画面を切り替え
        // paddingValuesはボトムバーの高さを提供
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedRoute) {
                BottomNavigationItem.Home.route -> {
                    val viewModel: HomeViewModel = koinInject()
                    // ホーム画面は独自のヘッダーを持つため、paddingは適用しない
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToTripDetail = { tripId ->
                            navigator.push(TripDetailScreenVoyager(tripId))
                        },
                        onNavigateToNotifications = {
                            navigator.push(NotificationScreenVoyager())
                        }
                    )
                }
                BottomNavigationItem.Album.route -> {
                    Box(modifier = Modifier.padding(paddingValues)) {
                        val viewModel: TimelineViewModel = koinInject()
                        TimelineScreen(
                            viewModel = viewModel,
                            onNavigateToPhotoDetail = { photoId, roomId ->
                                navigator.push(PhotoDetailScreenVoyager(roomId, photoId))
                            }
                        )
                    }
                }
                BottomNavigationItem.Shop.route -> {
                    val viewModel: ShopViewModel = koinInject()
                    // Shop画面は独自のヘッダーを持つため、paddingは適用しない
                    ShopScreen(
                        viewModel = viewModel,
                        onNavigateToProductOrder = { productId, tripId ->
                            navigator.push(ShopOrderScreenVoyager(productId, tripId))
                        }
                    )
                }
                BottomNavigationItem.Settings.route -> {
                    val viewModel: SettingsViewModel = koinInject()
                    // 設定画面は独自のヘッダーを持つため、paddingは適用しない
                    SettingsScreen(
                        viewModel = viewModel,
                        onNavigateToNotificationSettings = {
                            navigator.push(NotificationSettingsScreenVoyager())
                        },
                        onNavigateToProfileEdit = { userId ->
                            navigator.push(ProfileEditScreenVoyager(userId))
                        },
                        onNavigateToPremium = {
                            navigator.push(PremiumPlanScreenVoyager())
                        },
                        onNavigateToHelp = {
                            navigator.push(HelpFaqScreenVoyager())
                        }
                    )
                }
            }
        }
    }
}
