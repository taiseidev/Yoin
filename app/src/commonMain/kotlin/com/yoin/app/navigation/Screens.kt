package com.yoin.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinNavigatorScreenModel
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.koinInject
import com.yoin.feature.auth.ui.LoginScreen
import com.yoin.feature.auth.ui.PasswordResetScreen
import com.yoin.feature.auth.ui.RegisterMethodScreen
import com.yoin.feature.auth.ui.RegisterPasswordScreen
import com.yoin.feature.auth.ui.RegisterScreen
import com.yoin.feature.auth.ui.WelcomeScreen
import com.yoin.feature.auth.viewmodel.LoginViewModel
import com.yoin.feature.auth.viewmodel.PasswordResetViewModel
import com.yoin.feature.auth.viewmodel.RegisterMethodViewModel
import com.yoin.feature.auth.viewmodel.RegisterPasswordViewModel
import com.yoin.feature.auth.viewmodel.RegisterViewModel
import com.yoin.feature.auth.viewmodel.WelcomeViewModel
import com.yoin.feature.camera.ui.CameraScreen
import com.yoin.feature.camera.ui.PhotoConfirmScreen
import com.yoin.feature.camera.viewmodel.CameraViewModel
import com.yoin.feature.camera.viewmodel.PhotoConfirmViewModel
import com.yoin.feature.home.ui.TripDetailScreen
import com.yoin.feature.home.viewmodel.TripDetailViewModel
import com.yoin.feature.map.ui.MapFullscreenScreen
import com.yoin.feature.map.viewmodel.MapFullscreenViewModel
import com.yoin.feature.notifications.ui.NotificationScreen
import com.yoin.feature.notifications.viewmodel.NotificationViewModel
import com.yoin.feature.onboarding.ui.OnboardingScreen
import com.yoin.feature.onboarding.ui.SplashScreen
import com.yoin.feature.onboarding.viewmodel.OnboardingViewModel
import com.yoin.feature.onboarding.viewmodel.SplashViewModel
import com.yoin.feature.room.ui.JoinConfirmScreen
import com.yoin.feature.room.ui.ManualInputScreen
import com.yoin.feature.room.ui.MemberListScreen
import com.yoin.feature.room.ui.QRScanScreen
import com.yoin.feature.room.ui.RoomCreateScreen
import com.yoin.feature.room.ui.RoomCreatedScreen
import com.yoin.feature.room.ui.RoomDetailBeforeScreen
import com.yoin.feature.room.ui.RoomSettingsScreen
import com.yoin.feature.room.viewmodel.JoinConfirmViewModel
import com.yoin.feature.room.viewmodel.ManualInputViewModel
import com.yoin.feature.room.viewmodel.MemberListViewModel
import com.yoin.feature.room.viewmodel.QRScanViewModel
import com.yoin.feature.room.viewmodel.RoomCreateViewModel
import com.yoin.feature.room.viewmodel.RoomCreatedViewModel
import com.yoin.feature.room.viewmodel.RoomDetailBeforeViewModel
import com.yoin.feature.room.viewmodel.RoomSettingsViewModel
import com.yoin.feature.settings.ui.CategoryDetailScreen
import com.yoin.feature.settings.ui.ChangePasswordScreen
import com.yoin.feature.settings.ui.ContactFormScreen
import com.yoin.feature.settings.ui.DeleteAccountScreen
import com.yoin.feature.settings.ui.FaqDetailScreen
import com.yoin.feature.settings.ui.HelpFaqScreen
import com.yoin.feature.settings.ui.NotificationSettingsScreen
import com.yoin.feature.settings.ui.PlanComparisonScreen
import com.yoin.feature.settings.ui.PremiumPlanScreen
import com.yoin.feature.settings.viewmodel.ChangePasswordViewModel
import com.yoin.feature.settings.viewmodel.ContactFormViewModel
import com.yoin.feature.settings.viewmodel.DeleteAccountViewModel
import com.yoin.feature.settings.viewmodel.HelpFaqViewModel
import com.yoin.feature.settings.viewmodel.NotificationSettingsViewModel
import com.yoin.feature.settings.viewmodel.PremiumPlanViewModel
import com.yoin.feature.shop.ui.DeliveryTrackingScreen
import com.yoin.feature.shop.ui.OrderCompleteScreen
import com.yoin.feature.shop.ui.OrderConfirmationScreen
import com.yoin.feature.shop.ui.OrderDetailScreen
import com.yoin.feature.shop.ui.OrderHistoryScreen
import com.yoin.feature.shop.ui.ShippingAddressScreen
import com.yoin.feature.shop.ui.ShopOrderScreen
import com.yoin.feature.shop.viewmodel.DeliveryTrackingViewModel
import com.yoin.feature.shop.viewmodel.OrderCompleteViewModel
import com.yoin.feature.shop.viewmodel.OrderConfirmationViewModel
import com.yoin.feature.shop.viewmodel.OrderDetailViewModel
import com.yoin.feature.shop.viewmodel.OrderHistoryViewModel
import com.yoin.feature.shop.viewmodel.ShippingAddressViewModel
import com.yoin.feature.shop.viewmodel.ShopOrderViewModel
import com.yoin.feature.timeline.ui.PhotoDetailScreen
import com.yoin.feature.timeline.ui.RoomDetailAfterScreen
import com.yoin.feature.timeline.viewmodel.PhotoDetailViewModel
import com.yoin.feature.timeline.viewmodel.RoomDetailAfterViewModel
import org.koin.core.parameter.parametersOf

/**
 * スプラッシュ画面
 */
class SplashScreenVoyager : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: SplashViewModel = koinScreenModel()

        SplashScreen(
            viewModel = viewModel,
            onNavigateToMain = {
                navigator.replace(OnboardingScreenVoyager())
            }
        )
    }
}

/**
 * オンボーディング画面
 */
class OnboardingScreenVoyager : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: OnboardingViewModel = koinScreenModel()

        OnboardingScreen(
            viewModel = viewModel,
            onNavigateToLogin = {
                navigator.replace(WelcomeScreenVoyager())
            }
        )
    }
}

/**
 * ウェルカム画面（ログイン方法選択）
 */
class WelcomeScreenVoyager : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: WelcomeViewModel = koinScreenModel()

        WelcomeScreen(
            viewModel = viewModel,
            onNavigateToEmailLogin = {
                navigator.push(LoginScreenVoyager())
            },
            onNavigateToRegister = {
                navigator.push(RegisterMethodScreenVoyager())
            },
            onNavigateToHome = {
                navigator.replace(MainScreenVoyager())
            }
        )
    }
}

/**
 * ログイン画面（メールログイン）
 */
class LoginScreenVoyager : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: LoginViewModel = koinScreenModel()

        LoginScreen(
            viewModel = viewModel,
            onNavigateToHome = {
                navigator.replace(MainScreenVoyager())
            },
            onNavigateToPasswordReset = {
                navigator.push(PasswordResetScreenVoyager())
            },
            onNavigateBack = {
                navigator.pop()
            }
        )
    }
}

/**
 * パスワードリセット画面
 */
class PasswordResetScreenVoyager : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: PasswordResetViewModel = koinScreenModel()

        PasswordResetScreen(
            viewModel = viewModel,
            onNavigateToLogin = {
                navigator.pop()
            }
        )
    }
}

/**
 * 新規登録方法選択画面
 */
class RegisterMethodScreenVoyager : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: RegisterMethodViewModel = koinScreenModel()

        RegisterMethodScreen(
            viewModel = viewModel,
            onNavigateToEmailRegister = {
                navigator.push(RegisterScreenVoyager())
            },
            onNavigateToHome = {
                navigator.replace(MainScreenVoyager())
            },
            onNavigateBack = {
                navigator.pop()
            }
        )
    }
}

/**
 * 新規登録画面（基本情報入力）
 */
class RegisterScreenVoyager : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: RegisterViewModel = koinScreenModel()
        val state by viewModel.state.collectAsState()

        RegisterScreen(
            viewModel = viewModel,
            onNavigateToPasswordScreen = {
                // Pass name and email to password screen
                navigator.push(RegisterPasswordScreenVoyager(state.name, state.email))
            },
            onNavigateBack = {
                navigator.pop()
            }
        )
    }
}

/**
 * 新規登録画面（パスワード設定）
 */
data class RegisterPasswordScreenVoyager(val name: String, val email: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = RegisterPasswordViewModel(name, email)

        RegisterPasswordScreen(
            viewModel = viewModel,
            onNavigateToHome = {
                navigator.replace(MainScreenVoyager())
            },
            onNavigateBack = {
                navigator.pop()
            }
        )
    }
}

/**
 * 旅行詳細画面
 */
data class TripDetailScreenVoyager(val tripId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: TripDetailViewModel = koinScreenModel()

        TripDetailScreen(
            tripId = tripId,
            viewModel = viewModel,
            onNavigateBack = {
                navigator.pop()
            },
            onNavigateToCamera = { tripId ->
                navigator.push(CameraScreenVoyager(tripId))
            },
            onNavigateToSettings = { tripId ->
                navigator.push(RoomSettingsScreenVoyager(tripId))
            },
            onNavigateToMap = { tripId ->
                navigator.push(MapFullscreenScreenVoyager(tripId))
            }
        )
    }
}

/**
 * カメラ画面
 */
data class CameraScreenVoyager(val tripId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: CameraViewModel = koinScreenModel()

        CameraScreen(
            tripId = tripId,
            viewModel = viewModel,
            onNavigateBack = {
                navigator.pop()
            }
        )
    }
}

/**
 * 写真確認画面
 */
data class PhotoConfirmScreenVoyager(val photoPath: String, val tripId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: PhotoConfirmViewModel = koinScreenModel()

        PhotoConfirmScreen(
            photoPath = photoPath,
            tripId = tripId,
            viewModel = viewModel,
            onNavigateBack = {
                navigator.pop()
            },
            onNavigateToRoomDetail = {
                // ルーム詳細画面に戻る（写真確認画面とカメラ画面をスタックから削除）
                navigator.popUntilRoot()
                navigator.replace(TripDetailScreenVoyager(tripId))
            }
        )
    }
}

/**
 * 現像後のルーム詳細画面（タイムライン表示）
 */
data class RoomDetailAfterScreenVoyager(val roomId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: RoomDetailAfterViewModel = koinScreenModel()

        RoomDetailAfterScreen(
            roomId = roomId,
            viewModel = viewModel,
            onNavigateBack = {
                navigator.pop()
            }
        )
    }
}

/**
 * 現像前のルーム詳細画面
 * 旅行中にユーザーが最も頻繁に見る画面
 */
data class RoomDetailBeforeScreenVoyager(val roomId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: RoomDetailBeforeViewModel = koinScreenModel { parametersOf(roomId) }

        RoomDetailBeforeScreen(
            roomId = roomId,
            viewModel = viewModel,
            onNavigateBack = {
                navigator.pop()
            },
            onNavigateToCamera = { roomId ->
                navigator.push(CameraScreenVoyager(roomId))
            },
            onNavigateToMemberList = { roomId ->
                navigator.push(MemberListScreenVoyager(roomId))
            },
            onNavigateToSettings = { roomId ->
                navigator.push(RoomSettingsScreenVoyager(roomId))
            }
        )
    }
}

/**
 * ルーム作成画面
 */
class RoomCreateScreenVoyager : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: RoomCreateViewModel = koinScreenModel()

        RoomCreateScreen(
            viewModel = viewModel,
            onNavigateBack = {
                navigator.pop()
            },
            onNavigateToRoomDetail = { roomId ->
                navigator.replace(RoomCreatedScreenVoyager(roomId))
            }
        )
    }
}

/**
 * ルーム作成完了画面
 */
data class RoomCreatedScreenVoyager(val roomId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: RoomCreatedViewModel = koinScreenModel()

        RoomCreatedScreen(
            roomId = roomId,
            viewModel = viewModel,
            onNavigateToRoomDetail = { roomId ->
                navigator.replace(TripDetailScreenVoyager(roomId))
            },
            onNavigateToHome = {
                // MainScreenに戻る（スタックをクリア）
                navigator.replaceAll(MainScreenVoyager())
            }
        )
    }
}

/**
 * ルーム参加確認画面
 */
data class JoinConfirmScreenVoyager(val roomId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: JoinConfirmViewModel = koinScreenModel()

        JoinConfirmScreen(
            roomId = roomId,
            viewModel = viewModel,
            onNavigateToLogin = {
                navigator.push(WelcomeScreenVoyager())
            },
            onNavigateToRegister = {
                navigator.push(RegisterMethodScreenVoyager())
            },
            onNavigateToRoomDetail = { roomId ->
                navigator.replace(TripDetailScreenVoyager(roomId))
            },
            onNavigateBack = {
                navigator.pop()
            }
        )
    }
}

/**
 * QRスキャン画面
 */
class QRScanScreenVoyager : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: QRScanViewModel = koinScreenModel()

        QRScanScreen(
            viewModel = viewModel,
            onNavigateBack = {
                navigator.pop()
            },
            onNavigateToJoinConfirm = { roomId ->
                navigator.replace(JoinConfirmScreenVoyager(roomId))
            },
            onNavigateToManualInput = {
                navigator.push(ManualInputScreenVoyager())
            }
        )
    }
}

/**
 * ルーム設定画面
 */
data class RoomSettingsScreenVoyager(val roomId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: RoomSettingsViewModel = koinScreenModel { parametersOf(roomId) }

        RoomSettingsScreen(
            viewModel = viewModel,
            onNavigateBack = {
                navigator.pop()
            },
            onNavigateToMemberList = {
                navigator.push(MemberListScreenVoyager(roomId))
            }
        )
    }
}

/**
 * 写真詳細画面
 */
data class PhotoDetailScreenVoyager(val roomId: String, val photoId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: PhotoDetailViewModel = koinScreenModel()

        PhotoDetailScreen(
            roomId = roomId,
            photoId = photoId,
            viewModel = viewModel,
            onNavigateBack = {
                navigator.pop()
            }
        )
    }
}

/**
 * Shop注文画面
 */
data class ShopOrderScreenVoyager(val productId: String, val tripId: String? = null) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ShopOrderViewModel = koinScreenModel()

        ShopOrderScreen(
            productId = productId,
            tripId = tripId,
            viewModel = viewModel,
            onNavigateBack = {
                navigator.pop()
            },
            onNavigateToOrderComplete = { orderId, productName, deliveryAddress, deliveryDateRange, email ->
                navigator.push(
                    OrderCompleteScreenVoyager(
                        orderId = orderId,
                        productName = productName,
                        deliveryAddress = deliveryAddress,
                        deliveryDateRange = deliveryDateRange,
                        email = email
                    )
                )
            }
        )
    }
}

/**
 * プロフィール編集画面
 */
data class ProfileEditScreenVoyager(val userId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: com.yoin.feature.profile.viewmodel.ProfileEditViewModel = koinScreenModel()

        com.yoin.feature.profile.ui.ProfileEditScreen(
            userId = userId,
            viewModel = viewModel,
            onNavigateBack = {
                navigator.pop()
            },
            onNavigateToChangePassword = {
                navigator.push(ChangePasswordScreenVoyager())
            },
            onNavigateToDeleteAccount = {
                navigator.push(DeleteAccountScreenVoyager())
            }
        )
    }
}

/**
 * 通知画面
 */
class NotificationScreenVoyager : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: NotificationViewModel = koinScreenModel()

        NotificationScreen(
            viewModel = viewModel,
            onNavigateBack = {
                navigator.pop()
            },
            onNavigateToTripDetail = { tripId ->
                navigator.push(TripDetailScreenVoyager(tripId))
            },
            onNavigateToSettings = {
                navigator.push(NotificationSettingsScreenVoyager())
            }
        )
    }
}

/**
 * 通知設定画面
 */
class NotificationSettingsScreenVoyager : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: NotificationSettingsViewModel = koinScreenModel()

        NotificationSettingsScreen(
            viewModel = viewModel,
            onNavigateBack = {
                navigator.pop()
            }
        )
    }
}

/**
 * 地図フルスクリーン画面
 */
data class MapFullscreenScreenVoyager(val roomId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: MapFullscreenViewModel = koinScreenModel { parametersOf(roomId) }

        MapFullscreenScreen(
            viewModel = viewModel,
            onNavigateBack = {
                navigator.pop()
            },
            onNavigateToPhotoDetail = { roomId, photoId ->
                navigator.push(PhotoDetailScreenVoyager(roomId, photoId))
            }
        )
    }
}

/**
 * プレミアムプラン画面
 */
class PremiumPlanScreenVoyager : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: PremiumPlanViewModel = koinScreenModel()

        PremiumPlanScreen(
            viewModel = viewModel,
            onNavigateBack = {
                navigator.pop()
            },
            onNavigateToPlanComparison = {
                navigator.push(PlanComparisonScreenVoyager())
            }
        )
    }
}

/**
 * 配送先住所入力画面
 */
class ShippingAddressScreenVoyager : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ShippingAddressViewModel = koinScreenModel()

        ShippingAddressScreen(
            viewModel = viewModel,
            onNavigateBack = {
                navigator.pop()
            },
            onNavigateToConfirmation = { lastName, firstName, postalCode, prefecture, city, addressLine, phoneNumber ->
                navigator.push(
                    OrderConfirmationScreenVoyager(
                        lastName = lastName,
                        firstName = firstName,
                        postalCode = postalCode,
                        prefecture = prefecture,
                        city = city,
                        addressLine = addressLine,
                        phoneNumber = phoneNumber
                    )
                )
            }
        )
    }
}

/**
 * 注文完了画面
 */
data class OrderCompleteScreenVoyager(
    val orderId: String,
    val productName: String,
    val deliveryAddress: String,
    val deliveryDateRange: String,
    val email: String
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: OrderCompleteViewModel = koinScreenModel {
            parametersOf(orderId, productName, deliveryAddress, deliveryDateRange, email)
        }

        OrderCompleteScreen(
            viewModel = viewModel,
            onNavigateToDeliveryTracking = { orderId ->
                navigator.push(DeliveryTrackingScreenVoyager(orderId))
            },
            onNavigateToHome = {
                navigator.replaceAll(MainScreenVoyager())
            },
            onNavigateToOrderHistory = {
                navigator.push(OrderHistoryScreenVoyager())
            }
        )
    }
}

/**
 * 注文履歴画面
 */
class OrderHistoryScreenVoyager : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: OrderHistoryViewModel = koinScreenModel()

        OrderHistoryScreen(
            viewModel = viewModel,
            onNavigateBack = {
                navigator.pop()
            },
            onNavigateToOrderDetail = { orderId ->
                navigator.push(OrderDetailScreenVoyager(orderId))
            },
            onNavigateToContactSupport = {
                navigator.push(HelpFaqScreenVoyager())
            }
        )
    }
}

/**
 * ヘルプ・FAQ画面
 */
class HelpFaqScreenVoyager : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: HelpFaqViewModel = koinScreenModel()

        HelpFaqScreen(
            viewModel = viewModel,
            onNavigateBack = {
                navigator.pop()
            },
            onNavigateToFaqDetail = { faqItem ->
                navigator.push(FaqDetailScreenVoyager(faqItem))
            },
            onNavigateToCategoryDetail = { category ->
                navigator.push(CategoryDetailScreenVoyager(category))
            },
            onNavigateToContactSupport = {
                navigator.push(ContactFormScreenVoyager())
            }
        )
    }
}

/**
 * お問い合わせフォーム画面
 */
class ContactFormScreenVoyager : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ContactFormViewModel = koinScreenModel()

        ContactFormScreen(
            viewModel = viewModel,
            onNavigateBack = {
                navigator.pop()
            }
        )
    }
}

/**
 * ルームID手動入力画面（プレースホルダー）
 */
class ManualInputScreenVoyager : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ManualInputViewModel = koinScreenModel()

        ManualInputScreen(
            viewModel = viewModel,
            onNavigateBack = { navigator.pop() },
            onNavigateToJoinConfirm = { roomId ->
                navigator.replace(JoinConfirmScreenVoyager(roomId))
            }
        )
    }
}

/**
 * メンバー一覧画面
 */
data class MemberListScreenVoyager(val roomId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: MemberListViewModel = koinScreenModel { parametersOf(roomId) }

        MemberListScreen(
            viewModel = viewModel,
            onNavigateBack = { navigator.pop() }
        )
    }
}

/**
 * パスワード変更画面
 */
class ChangePasswordScreenVoyager : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ChangePasswordViewModel = koinInject()

        ChangePasswordScreen(
            viewModel = viewModel,
            onNavigateBack = { navigator.pop() }
        )
    }
}

/**
 * アカウント削除画面
 */
class DeleteAccountScreenVoyager : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: DeleteAccountViewModel = koinInject()

        DeleteAccountScreen(
            viewModel = viewModel,
            onNavigateBack = { navigator.pop() },
            onNavigateToLogin = {
                // ログイン画面に遷移し、スタックをクリア
                navigator.replaceAll(LoginScreenVoyager())
            }
        )
    }
}

/**
 * プラン比較画面（プレースホルダー）
 */
class PlanComparisonScreenVoyager : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        PlanComparisonScreen(
            onNavigateBack = { navigator.pop() }
        )
    }
}

/**
 * 注文確認画面
 */
data class OrderConfirmationScreenVoyager(
    val lastName: String,
    val firstName: String,
    val postalCode: String,
    val prefecture: String,
    val city: String,
    val addressLine: String,
    val phoneNumber: String
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: OrderConfirmationViewModel = koinInject(
            parameters = {
                parametersOf(lastName, firstName, postalCode, prefecture, city, addressLine, phoneNumber)
            }
        )

        OrderConfirmationScreen(
            viewModel = viewModel,
            onNavigateBack = { navigator.pop() },
            onNavigateToOrderComplete = { orderId, productName, deliveryAddress, deliveryDateRange, email ->
                navigator.push(
                    OrderCompleteScreenVoyager(
                        orderId = orderId,
                        productName = productName,
                        deliveryAddress = deliveryAddress,
                        deliveryDateRange = deliveryDateRange,
                        email = email
                    )
                )
            }
        )
    }
}

/**
 * 配送追跡画面
 */
data class DeliveryTrackingScreenVoyager(val orderId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: DeliveryTrackingViewModel = koinInject(parameters = { parametersOf(orderId) })

        DeliveryTrackingScreen(
            viewModel = viewModel,
            onNavigateBack = { navigator.pop() }
        )
    }
}

/**
 * 注文詳細画面
 */
data class OrderDetailScreenVoyager(val orderId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: OrderDetailViewModel = koinInject(parameters = { parametersOf(orderId) })

        OrderDetailScreen(
            viewModel = viewModel,
            onNavigateBack = { navigator.pop() },
            onNavigateToDeliveryTracking = { orderId ->
                navigator.push(DeliveryTrackingScreenVoyager(orderId))
            },
            onNavigateToContactSupport = { orderId ->
                navigator.push(ContactFormScreenVoyager())
            },
            onNavigateToShopOrder = { productId ->
                navigator.push(ShopOrderScreenVoyager(productId = productId))
            }
        )
    }
}

/**
 * FAQ詳細画面
 */
data class FaqDetailScreenVoyager(val faqItem: com.yoin.feature.settings.viewmodel.HelpFaqContract.FaqItem) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        FaqDetailScreen(
            faqItem = faqItem,
            onNavigateBack = { navigator.pop() }
        )
    }
}

/**
 * カテゴリ詳細画面
 */
data class CategoryDetailScreenVoyager(val category: com.yoin.feature.settings.viewmodel.HelpFaqContract.Category) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        CategoryDetailScreen(
            category = category,
            onNavigateBack = { navigator.pop() }
        )
    }
}

/**
 * プレースホルダー画面
 * 未実装の画面用の汎用プレースホルダー
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaceholderScreen(
    title: String,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("←")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = com.yoin.core.design.theme.YoinColors.Surface,
                    titleContentColor = com.yoin.core.design.theme.YoinColors.TextPrimary
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(com.yoin.core.design.theme.YoinColors.Background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "この画面は未実装です",
                    fontSize = 18.sp,
                    color = com.yoin.core.design.theme.YoinColors.TextPrimary
                )
                Text(
                    text = title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = com.yoin.core.design.theme.YoinColors.Primary
                )
            }
        }
    }
}

// MainScreenVoyagerはMainScreenVoyager.ktに移動しました
