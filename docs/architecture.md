# アーキテクチャ設計

## システム概要

Yoin（余韻）は Kotlin Multiplatform (KMP) と Compose Multiplatform をベースとした、Android/iOS クロスプラットフォームアプリです。

---

## 設計方針

| 方針 | 理由 |
|---|---|
| **SDK直接利用** | 個人開発のため、シンプルな構成を優先 |
| **サーバーレス** | 運用コストを最小化 |
| **段階的拡張** | 将来必要になったらKtor + Cloud Runを追加 |

---

## システム構成図

```
┌─────────────────────────────────────────────────────────┐
│            KMP (Compose Multiplatform)                  │
│                                                         │
│    ┌─────────────────┐    ┌─────────────────┐          │
│    │    Android      │    │      iOS        │          │
│    │ Jetpack Compose │    │ Compose for iOS │          │
│    └────────┬────────┘    └────────┬────────┘          │
│             │                      │                    │
│             └──────────┬───────────┘                    │
│                        │                                │
│              ┌─────────┴─────────┐                      │
│              │   Shared Module   │                      │
│              │  (Common Logic)   │                      │
│              └─────────┬─────────┘                      │
└────────────────────────┼────────────────────────────────┘
                         │
         ┌───────────────┼───────────────┬────────────────┐
         │               │               │                │
         ▼               ▼               ▼                ▼
   ┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐
   │ Supabase │   │ Firebase │   │RevenueCat│   │  Stripe  │
   │          │   │          │   │          │   │          │
   │ - Auth   │   │ - FCM    │   │ - 課金   │   │ - 決済   │
   │ - DB     │   │ - Analytics   │ - 復元   │   │ - Shop   │
   │ - Storage│   │ - Crashlytics │          │   │          │
   │ - Edge Fn│   │          │   │          │   │          │
   │ - pg_cron│   │          │   │          │   │          │
   └──────────┘   └──────────┘   └──────────┘   └──────────┘
```

---

## モジュール構成

```
yoin/
├── composeApp/                    # Compose Multiplatform UI
│   ├── commonMain/
│   │   └── kotlin/
│   │       ├── ui/
│   │       │   ├── screens/       # 画面
│   │       │   ├── components/    # 共通コンポーネント
│   │       │   └── theme/         # テーマ設定
│   │       └── navigation/        # ナビゲーション
│   ├── androidMain/               # Android固有UI
│   └── iosMain/                   # iOS固有UI
│
├── shared/                        # KMPビジネスロジック
│   ├── commonMain/
│   │   └── kotlin/
│   │       ├── domain/
│   │       │   ├── model/         # ドメインモデル
│   │       │   ├── repository/    # Repositoryインターフェース
│   │       │   └── usecase/       # ユースケース
│   │       ├── data/
│   │       │   ├── repository/    # Repository実装
│   │       │   ├── datasource/    # データソース
│   │       │   └── mapper/        # マッパー
│   │       ├── di/                # DI設定
│   │       └── util/              # ユーティリティ
│   ├── androidMain/               # Android固有実装
│   │   └── kotlin/
│   │       ├── camera/            # CameraX
│   │       └── notification/      # FCM
│   └── iosMain/                   # iOS固有実装
│       └── kotlin/
│           ├── camera/            # AVFoundation
│           └── notification/      # APNs
│
└── iosApp/                        # iOSアプリエントリーポイント
    └── iosApp/
        └── iOSApp.swift
```

---

## レイヤードアーキテクチャ

### MVI + Clean Architecture

```
┌──────────────────────────────────────────┐
│           Presentation Layer             │
│  (UI, ViewModel, Intent, State)          │
│  - Compose UI                            │
│  - MVI Pattern                           │
└──────────────┬───────────────────────────┘
               │
┌──────────────┴───────────────────────────┐
│           Domain Layer                   │
│  (UseCase, Repository Interface, Model)  │
│  - Business Logic                        │
│  - Platform Independent                  │
└──────────────┬───────────────────────────┘
               │
┌──────────────┴───────────────────────────┐
│           Data Layer                     │
│  (Repository Impl, DataSource, DTO)      │
│  - Supabase SDK                          │
│  - Firebase SDK                          │
│  - Local DB (SQLDelight)                 │
└──────────────────────────────────────────┘
```

### 各レイヤーの責務

#### Presentation Layer
- **UI**: Compose によるUI構築
- **ViewModel**: 状態管理とビジネスロジック呼び出し
- **Intent**: ユーザーアクション
- **State**: UI状態の保持

#### Domain Layer
- **UseCase**: ビジネスロジックの実装
- **Repository Interface**: データ操作の抽象化
- **Model**: ドメインモデル（エンティティ）

#### Data Layer
- **Repository Implementation**: データ取得・保存の具体的な実装
- **DataSource**: 外部サービスとの通信
- **Mapper**: DTOとドメインモデルの変換

---

## 依存関係の方向

```
Presentation → Domain ← Data
```

- Presentation層はDomain層に依存
- Data層はDomain層に依存
- Domain層は他のレイヤーに依存しない（Dependency Inversion）

---

## 主要コンポーネント

### 認証フロー（Supabase Auth）

```kotlin
// commonMain
interface AuthRepository {
    suspend fun signInWithApple(token: String): User
    suspend fun signInWithGoogle(token: String): User
    suspend fun signInAsGuest(): User
    suspend fun signOut()
    fun getCurrentUser(): Flow<User?>
}

// androidMain / iosMain
expect class AuthService {
    suspend fun signInWithApple(): String
    suspend fun signInWithGoogle(): String
}
```

### データフロー（写真アップロード）

```kotlin
// UseCase
class UploadPhotoUseCase(
    private val photoRepository: PhotoRepository,
    private val imageProcessor: ImageProcessor
) {
    suspend operator fun invoke(
        roomId: String,
        imageBytes: ByteArray,
        filter: Filter
    ): Result<Photo> {
        // 1. フィルター適用
        val filteredImage = imageProcessor.applyFilter(imageBytes, filter)

        // 2. 日付スタンプ追加
        val stampedImage = imageProcessor.addDateStamp(filteredImage)

        // 3. アップロード
        return photoRepository.uploadPhoto(roomId, stampedImage, filter)
    }
}
```

---

## 画像処理アーキテクチャ

### 端末内処理（クライアントサイド）

```kotlin
// commonMain
expect class ImageProcessor {
    fun applyFilter(imageBytes: ByteArray, filter: Filter): ByteArray
    fun addDateStamp(imageBytes: ByteArray): ByteArray
    fun resize(imageBytes: ByteArray, maxWidth: Int): ByteArray
}

// androidMain
actual class ImageProcessor {
    actual fun applyFilter(imageBytes: ByteArray, filter: Filter): ByteArray {
        // GPUImage を使用
    }
}

// iosMain
actual class ImageProcessor {
    actual fun applyFilter(imageBytes: ByteArray, filter: Filter): ByteArray {
        // CoreImage を使用
    }
}
```

---

## バックエンド構成（Supabase）

### Edge Functions

```typescript
// supabase/functions/notify-development/index.ts
serve(async (req) => {
  // 現像完了したルームを取得
  const { data: rooms } = await supabase
    .from('rooms')
    .select('id, room_members(users(fcm_token))')
    .eq('is_developed', true)

  // FCMで通知送信
  for (const room of rooms) {
    // ...
  }
})
```

### pg_cron（現像処理）

```sql
-- 5分ごとに現像時刻を過ぎた写真を公開
SELECT cron.schedule(
  'develop-photos',
  '*/5 * * * *',
  $$
    UPDATE photos
    SET developed_at = NOW()
    WHERE developed_at IS NULL
      AND development_time <= NOW()
  $$
);
```

---

## 状態管理（MVI）

### MVI Pattern

```kotlin
// Intent
sealed interface RoomIntent {
    data class LoadPhotos(val roomId: String) : RoomIntent
    data class UploadPhoto(val imageBytes: ByteArray) : RoomIntent
}

// State
data class RoomState(
    val photos: List<Photo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// ViewModel
class RoomViewModel(
    private val getPhotosUseCase: GetPhotosUseCase,
    private val uploadPhotoUseCase: UploadPhotoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RoomState())
    val state: StateFlow<RoomState> = _state.asStateFlow()

    fun onIntent(intent: RoomIntent) {
        when (intent) {
            is RoomIntent.LoadPhotos -> loadPhotos(intent.roomId)
            is RoomIntent.UploadPhoto -> uploadPhoto(intent.imageBytes)
        }
    }
}
```

---

## データキャッシング戦略

### SQLDelight によるオフライン対応

```kotlin
class PhotoRepositoryImpl(
    private val supabase: SupabaseClient,
    private val database: Database
) : PhotoRepository {

    override fun getPhotos(roomId: String): Flow<List<Photo>> = flow {
        // 1. ローカルDBから取得（即座に表示）
        val cached = database.photoQueries.selectByRoom(roomId).executeAsList()
        emit(cached.map { it.toDomain() })

        // 2. Supabaseから最新データを取得
        val remote = supabase.from("photos")
            .select { filter { eq("room_id", roomId) } }
            .decodeList<PhotoDto>()

        // 3. ローカルDBを更新
        database.photoQueries.transaction {
            remote.forEach { database.photoQueries.insert(it) }
        }

        // 4. 最新データを返す
        emit(remote.map { it.toDomain() })
    }
}
```

---

## エラーハンドリング

### Result型による統一的なエラー処理

```kotlin
sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val exception: Throwable) : Result<Nothing>
}

// UseCase
class GetPhotosUseCase(
    private val repository: PhotoRepository
) {
    suspend operator fun invoke(roomId: String): Result<List<Photo>> {
        return try {
            val photos = repository.getPhotos(roomId)
            Result.Success(photos)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
```

---

## DI（Dependency Injection）

### Koin によるDI設定

```kotlin
val appModule = module {
    // Repository
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<PhotoRepository> { PhotoRepositoryImpl(get(), get()) }

    // UseCase
    factory { GetPhotosUseCase(get()) }
    factory { UploadPhotoUseCase(get(), get()) }

    // ViewModel
    viewModel { RoomViewModel(get(), get()) }

    // External Services
    single { createSupabaseClient() }
    single { createFirebaseApp() }
}
```

---

## 将来の拡張構成

複雑な処理が必要になった場合、Ktor + Cloud Run を追加可能。

```
┌─────────────────────────────────────────────────────────┐
│            KMP (Compose Multiplatform)                  │
└────────────────────────┬────────────────────────────────┘
                         │
         ┌───────────────┼───────────────┐
         │               │               │
         ▼               ▼               ▼
   ┌──────────┐   ┌──────────┐   ┌──────────────┐
   │ Supabase │   │ Firebase │   │ Ktor Server  │  ← 将来追加
   └──────────┘   └──────────┘   │ (Cloud Run)  │
                                 │              │
                                 │ - 画像処理   │
                                 │ - AI判定     │
                                 │ - 複雑なAPI  │
                                 └──────────────┘
```

---

## セキュリティアーキテクチャ

### Row Level Security (RLS)

- Supabase の RLS により、データベースレベルでアクセス制御
- ルームメンバーのみが写真を閲覧可能
- ユーザーは自分のデータのみ更新可能

### 通信暗号化

- HTTPS（Supabase 標準）
- 画像URLは署名付きURL（有効期限付き）

---

## パフォーマンス最適化

### 画像最適化

- アップロード前にクライアントサイドでリサイズ
- サムネイル生成
- 段階的な読み込み（Placeholder → Thumbnail → Full）

### リスト表示最適化

- LazyColumn / LazyVerticalGrid による仮想化
- Paging による段階的なデータ取得

---

**関連ドキュメント**:
- [技術構成（詳細）](/Users/t-z/workspace/obsidian-notes/08_個人開発/Yoin. - 余韻 -/技術構成.md)
- [データベーススキーマ](database-schema.md)
- [開発ガイドライン](development-guide.md)

**最終更新**: 2024年12月
