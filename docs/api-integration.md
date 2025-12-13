# API連携ガイド

このドキュメントでは、Yoin（余韻）アプリが使用する外部サービスとの連携方法を説明します。

---

## Supabase

### 概要

Supabase は PostgreSQL ベースのマネージドバックエンドサービスで、認証・データベース・ストレージ・Edge Functions を提供します。

### セットアップ

#### 1. プロジェクト作成

```bash
# Supabase CLI のインストール
npm install -g supabase

# ローカルプロジェクトの初期化
supabase init

# ローカル環境の起動
supabase start
```

#### 2. Kotlin SDK の導入

```kotlin
// build.gradle.kts (shared)
dependencies {
    implementation("io.github.jan-tennert.supabase:postgrest-kt:2.0.0")
    implementation("io.github.jan-tennert.supabase:auth-kt:2.0.0")
    implementation("io.github.jan-tennert.supabase:storage-kt:2.0.0")
    implementation("io.github.jan-tennert.supabase:realtime-kt:2.0.0")
}
```

#### 3. クライアント初期化

```kotlin
// commonMain
val supabase = createSupabaseClient(
    supabaseUrl = "https://xxx.supabase.co",
    supabaseKey = "YOUR_ANON_KEY"
) {
    install(Postgrest)
    install(Auth)
    install(Storage)
    install(Realtime)
}
```

---

### Auth（認証）

#### Apple Sign-In

```kotlin
// iOS
actual suspend fun signInWithApple(): AuthResult {
    return supabase.auth.signInWith(Apple) {
        // iOS の認証プロバイダー設定
    }
}
```

#### Google Sign-In

```kotlin
// Android
actual suspend fun signInWithGoogle(): AuthResult {
    return supabase.auth.signInWith(Google) {
        // Android の認証プロバイダー設定
    }
}
```

#### 匿名認証（ゲストモード）

```kotlin
suspend fun signInAsGuest(): AuthResult {
    return supabase.auth.signInAnonymously()
}
```

#### 現在のユーザー取得

```kotlin
fun getCurrentUser(): Flow<User?> {
    return supabase.auth.sessionStatus.map { status ->
        when (status) {
            is SessionStatus.Authenticated -> status.session.user
            else -> null
        }
    }
}
```

---

### Database（Postgrest）

#### データ取得（SELECT）

```kotlin
// ルーム一覧取得
val rooms = supabase.from("rooms")
    .select {
        filter {
            eq("status", "active")
        }
        order("created_at", Order.DESCENDING)
    }
    .decodeList<Room>()
```

#### データ挿入（INSERT）

```kotlin
// ルーム作成
val room = Room(
    name = "北海道旅行",
    startDate = LocalDate(2025, 7, 1),
    endDate = LocalDate(2025, 7, 5)
)

val created = supabase.from("rooms")
    .insert(room)
    .decodeSingle<Room>()
```

#### データ更新（UPDATE）

```kotlin
// ルーム更新
supabase.from("rooms")
    .update({
        set("status", "developed")
        set("developed_at", Clock.System.now())
    }) {
        filter {
            eq("id", roomId)
        }
    }
```

#### JOINクエリ

```kotlin
// ルームとメンバーを取得
val rooms = supabase.from("rooms")
    .select {
        columns = listOf("*", "room_members(*)", "room_members(users(*))")
    }
    .decodeList<RoomWithMembers>()
```

---

### Storage（ストレージ）

#### 写真アップロード

```kotlin
suspend fun uploadPhoto(
    roomId: String,
    imageBytes: ByteArray
): String {
    val path = "photos/${roomId}/${UUID.randomUUID()}.jpg"

    supabase.storage
        .from("photos")
        .upload(path, imageBytes)

    return path
}
```

#### 写真ダウンロード

```kotlin
suspend fun downloadPhoto(path: String): ByteArray {
    return supabase.storage
        .from("photos")
        .downloadAuthenticated(path)
}
```

#### 署名付きURL生成

```kotlin
suspend fun getSignedUrl(path: String, expiresIn: Int = 3600): String {
    return supabase.storage
        .from("photos")
        .createSignedUrl(path, expiresIn)
}
```

---

### Edge Functions

#### Edge Function の呼び出し

```kotlin
suspend fun notifyDevelopmentComplete(roomId: String) {
    supabase.functions.invoke(
        function = "notify-development",
        body = mapOf("room_id" to roomId)
    )
}
```

---

## Firebase

### 概要

Firebase は、プッシュ通知（FCM）、アナリティクス、クラッシュレポートを提供します。

### セットアップ

#### 1. Firebase プロジェクト作成

1. [Firebase Console](https://console.firebase.google.com/) でプロジェクト作成
2. Android アプリを追加 → `google-services.json` をダウンロード
3. iOS アプリを追加 → `GoogleService-Info.plist` をダウンロード

#### 2. Kotlin SDK の導入

```kotlin
// build.gradle.kts (shared)
dependencies {
    implementation("dev.gitlive:firebase-kotlin-sdk:1.10.0")
    implementation("dev.gitlive:firebase-messaging:1.10.0")
    implementation("dev.gitlive:firebase-analytics:1.10.0")
    implementation("dev.gitlive:firebase-crashlytics:1.10.0")
}
```

---

### Cloud Messaging（FCM）

#### トークン取得

```kotlin
// commonMain
expect class NotificationService {
    suspend fun getToken(): String?
}

// androidMain
actual class NotificationService {
    actual suspend fun getToken(): String? {
        return Firebase.messaging.getToken()
    }
}

// iosMain
actual class NotificationService {
    actual suspend fun getToken(): String? {
        return Firebase.messaging.getToken()
    }
}
```

#### トークン登録

```kotlin
suspend fun registerFcmToken(userId: String, token: String) {
    supabase.from("fcm_tokens")
        .upsert(
            FcmToken(
                userId = userId,
                token = token,
                deviceType = Platform.osName
            )
        )
}
```

#### 通知受信

```kotlin
// androidMain
class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        // 通知処理
    }
}
```

---

### Analytics

#### イベント送信

```kotlin
fun logEvent(name: String, params: Map<String, Any>) {
    Firebase.analytics.logEvent(name, params)
}

// 使用例
logEvent("photo_uploaded", mapOf(
    "room_id" to roomId,
    "filter" to filterName
))
```

#### スクリーン遷移記録

```kotlin
fun logScreenView(screenName: String) {
    Firebase.analytics.logEvent("screen_view", mapOf(
        "screen_name" to screenName
    ))
}
```

---

### Crashlytics

#### クラッシュレポート

```kotlin
// 自動的にクラッシュを記録

// カスタムログ
Firebase.crashlytics.log("User uploaded photo")

// エラー記録
try {
    // ...
} catch (e: Exception) {
    Firebase.crashlytics.recordException(e)
}
```

---

## RevenueCat

### 概要

RevenueCat は、サブスクリプション管理を簡単にするサービスです。

### セットアップ

#### 1. SDK の導入

```kotlin
// build.gradle.kts (shared)
dependencies {
    implementation("com.revenuecat.purchases:purchases-kmp:1.0.0")
}
```

#### 2. 初期化

```kotlin
// commonMain
Purchases.configure(
    apiKey = if (Platform.osName == "iOS") {
        "appl_YOUR_IOS_KEY"
    } else {
        "goog_YOUR_ANDROID_KEY"
    }
)
```

---

### サブスクリプション

#### オファリング取得

```kotlin
suspend fun getOfferings(): Offerings {
    return Purchases.sharedInstance.awaitOfferings()
}
```

#### 購入処理

```kotlin
suspend fun purchase(activity: Activity, package: Package): CustomerInfo {
    return Purchases.sharedInstance.awaitPurchase(
        PurchaseParams.Builder(activity, package).build()
    ).customerInfo
}
```

#### サブスクリプション状態確認

```kotlin
fun isPremium(): Flow<Boolean> {
    return Purchases.sharedInstance.customerInfoFlow
        .map { it.entitlements["premium"]?.isActive == true }
}
```

#### 購入復元

```kotlin
suspend fun restorePurchases(): CustomerInfo {
    return Purchases.sharedInstance.awaitRestorePurchases()
}
```

---

## Stripe

### 概要

Stripe は、Shop機能での物販決済に使用します。

### セットアップ

#### 1. SDK の導入

```kotlin
// build.gradle.kts (shared)
dependencies {
    implementation("com.stripe:stripe-kotlin:20.0.0")
}
```

#### 2. Payment Intent 作成

```kotlin
// バックエンド（Edge Function）で作成
suspend fun createPaymentIntent(amount: Int): String {
    val response = supabase.functions.invoke(
        function = "create-payment-intent",
        body = mapOf("amount" to amount)
    )
    return response["client_secret"] as String
}
```

#### 3. 決済処理

```kotlin
// Android
val paymentSheet = PaymentSheet(activity)
paymentSheet.presentWithPaymentIntent(
    clientSecret = clientSecret,
    configuration = PaymentSheet.Configuration("Yoin")
)
```

---

## Google Maps API

### 概要

位置情報表示とマップ機能に使用します。

### セットアップ

#### 1. API キー取得

[Google Cloud Console](https://console.cloud.google.com/) で Maps SDK を有効化し、APIキーを取得。

#### 2. Compose Maps の導入

```kotlin
// build.gradle.kts (composeApp)
dependencies {
    implementation("com.google.maps.android:maps-compose:2.15.0")
}
```

#### 3. マップ表示

```kotlin
@Composable
fun PhotoMap(photos: List<Photo>) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(photos.first().latitude, photos.first().longitude),
            12f
        )
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        photos.forEach { photo ->
            Marker(
                state = MarkerState(position = LatLng(photo.latitude, photo.longitude)),
                title = photo.userName
            )
        }
    }
}
```

---

## エラーハンドリング

### 統一的なエラー処理

```kotlin
sealed class ApiException(message: String) : Exception(message) {
    class NetworkError(message: String) : ApiException(message)
    class AuthError(message: String) : ApiException(message)
    class ServerError(message: String) : ApiException(message)
}

suspend fun <T> safeApiCall(call: suspend () -> T): Result<T> {
    return try {
        Result.Success(call())
    } catch (e: HttpRequestException) {
        Result.Error(ApiException.NetworkError(e.message ?: "Network error"))
    } catch (e: Exception) {
        Result.Error(ApiException.ServerError(e.message ?: "Unknown error"))
    }
}
```

---

## 環境変数管理

### .env.local の設定

```bash
# Supabase
SUPABASE_URL=https://xxx.supabase.co
SUPABASE_ANON_KEY=your_anon_key

# Firebase
FIREBASE_PROJECT_ID=your_project_id

# RevenueCat
REVENUECAT_API_KEY_ANDROID=goog_xxx
REVENUECAT_API_KEY_IOS=appl_xxx

# Stripe
STRIPE_PUBLISHABLE_KEY=pk_test_xxx

# Google Maps
GOOGLE_MAPS_API_KEY=AIzaSyXXX
```

### BuildConfig での参照

```kotlin
// build.gradle.kts
android {
    defaultConfig {
        buildConfigField("String", "SUPABASE_URL", "\"${System.getenv("SUPABASE_URL")}\"")
    }
}
```

---

**関連ドキュメント**:
- [アーキテクチャ設計](architecture.md)
- [データベーススキーマ](database-schema.md)
- [開発ガイドライン](development-guide.md)

**最終更新**: 2024年12月
