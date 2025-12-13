# コーディング規約

Yoin（余韻）プロジェクトにおけるコーディング規約を定義します。

---

## 基本方針

1. **可読性を最優先** - 他の開発者（未来の自分を含む）が読みやすいコードを書く
2. **一貫性を保つ** - プロジェクト全体で統一されたスタイルを使用
3. **シンプルに保つ** - 複雑な実装よりもシンプルで明確な実装を優先

---

## Kotlin コーディング規約

### 命名規則

#### クラス・インターフェース

```kotlin
// PascalCase
class UserRepository { }
interface PhotoDataSource { }
data class RoomState()
sealed interface RoomIntent { }
```

#### 関数・変数

```kotlin
// camelCase
fun getUserById(id: String): User { }
val userName: String = ""
var isLoading: Boolean = false
```

#### 定数

```kotlin
// UPPER_SNAKE_CASE
const val MAX_PHOTO_COUNT = 24
const val API_BASE_URL = "https://api.example.com"

companion object {
    const val DEFAULT_TIMEOUT = 30000L
}
```

#### プライベートプロパティ

```kotlin
// アンダースコアプレフィックス（MutableStateFlow等）
private val _state = MutableStateFlow(State())
val state: StateFlow<State> = _state.asStateFlow()

private val _photos = MutableStateFlow<List<Photo>>(emptyList())
val photos: StateFlow<List<Photo>> = _photos.asStateFlow()
```

---

### インデント・フォーマット

#### 基本

- インデント: **4スペース**（タブは使用しない）
- 1行の長さ: **120文字**まで
- 波括弧: K&R スタイル

```kotlin
class Example {
    fun doSomething() {
        if (condition) {
            // ...
        } else {
            // ...
        }
    }
}
```

#### 長いパラメータリスト

```kotlin
// 1パラメータ1行
fun createRoom(
    name: String,
    destination: String,
    startDate: LocalDate,
    endDate: LocalDate,
    ownerId: String
): Room {
    // ...
}
```

---

### Nullable 型

#### 安全な呼び出し

```kotlin
// Good
val userName = user?.name ?: "Unknown"

// Bad
val userName = if (user != null) user.name else "Unknown"
```

#### Elvis 演算子

```kotlin
// Good
val config = loadConfig() ?: getDefaultConfig()

// Bad
val config = if (loadConfig() != null) loadConfig() else getDefaultConfig()
```

#### let, also, apply の使用

```kotlin
// Good
user?.let { user ->
    println("User: ${user.name}")
    saveUser(user)
}

// 単一の呼び出しの場合は省略可
user?.also { saveUser(it) }
```

---

### 関数

#### 単一式関数

```kotlin
// Good
fun double(x: Int): Int = x * 2

// Bad（単純な場合）
fun double(x: Int): Int {
    return x * 2
}
```

#### 拡張関数

```kotlin
// 適切なファイルに配置
// extensions/StringExtensions.kt
fun String.isValidEmail(): Boolean {
    return this.contains("@")
}

// 使用
val email = "user@example.com"
if (email.isValidEmail()) {
    // ...
}
```

---

### データクラス

```kotlin
// Good
data class User(
    val id: String,
    val name: String,
    val email: String,
    val createdAt: Instant
)

// プロパティが多い場合は改行
data class Room(
    val id: String,
    val name: String,
    val destination: String?,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: RoomStatus,
    val ownerId: String,
    val createdAt: Instant,
    val updatedAt: Instant
)
```

---

### sealed class / sealed interface

```kotlin
// MVI の Intent/State に使用
sealed interface RoomIntent {
    data class LoadPhotos(val roomId: String) : RoomIntent
    data class UploadPhoto(val imageBytes: ByteArray) : RoomIntent
    object Refresh : RoomIntent
}

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val exception: Throwable) : Result<Nothing>
    object Loading : Result<Nothing>
}
```

---

### Coroutine

#### スコープ

```kotlin
// ViewModel
class RoomViewModel : ViewModel() {
    init {
        viewModelScope.launch {
            // 自動的にキャンセルされる
        }
    }
}

// Repository
class PhotoRepository(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun uploadPhoto() = withContext(ioDispatcher) {
        // IO処理
    }
}
```

#### エラーハンドリング

```kotlin
// Good
suspend fun getPhotos(roomId: String): Result<List<Photo>> {
    return try {
        val photos = api.fetchPhotos(roomId)
        Result.Success(photos)
    } catch (e: Exception) {
        Result.Error(e)
    }
}

// Bad（例外を上に投げるだけ）
suspend fun getPhotos(roomId: String): List<Photo> {
    return api.fetchPhotos(roomId) // 例外が起きたらクラッシュ
}
```

---

### Flow

```kotlin
// StateFlow でUIの状態を公開
private val _state = MutableStateFlow(UiState())
val state: StateFlow<UiState> = _state.asStateFlow()

// SharedFlow でイベントを公開
private val _event = MutableSharedFlow<Event>()
val event: SharedFlow<Event> = _event.asSharedFlow()

// collect
viewModelScope.launch {
    photoRepository.getPhotos(roomId)
        .collect { photos ->
            _state.update { it.copy(photos = photos) }
        }
}
```

---

## Jetpack Compose 規約

### Composable 関数

#### 命名

```kotlin
// PascalCase
@Composable
fun LoginScreen() { }

@Composable
fun PhotoCard(photo: Photo) { }
```

#### Modifier

```kotlin
// Modifier は第一引数
@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(text)
    }
}
```

#### プレビュー

```kotlin
@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    YoinTheme {
        LoginScreen()
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun LoginScreenPreviewDark() {
    YoinTheme {
        LoginScreen()
    }
}
```

---

### State Hoisting

```kotlin
// Bad（内部でStateを持つ）
@Composable
fun SearchBar() {
    var query by remember { mutableStateOf("") }
    TextField(
        value = query,
        onValueChange = { query = it }
    )
}

// Good（Stateを外部から受け取る）
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
    )
}
```

---

### remember と LaunchedEffect

```kotlin
// remember: 状態の保持
@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }
    Button(onClick = { count++ }) {
        Text("Count: $count")
    }
}

// LaunchedEffect: 副作用
@Composable
fun PhotoList(roomId: String, viewModel: RoomViewModel) {
    LaunchedEffect(roomId) {
        viewModel.loadPhotos(roomId)
    }

    // ...
}
```

---

## アーキテクチャ規約

### レイヤー構成

```
Presentation (UI, ViewModel)
      ↓
Domain (UseCase, Repository Interface, Model)
      ↓
Data (Repository Impl, DataSource, DTO)
```

### 依存関係のルール

1. **Presentation → Domain**: ViewModel は UseCase を呼び出す
2. **Domain ← Data**: Repository は Domain で定義、Data で実装
3. **Domain は独立**: Domain は他のレイヤーに依存しない

---

### ファイル配置

```
shared/src/commonMain/kotlin/com/yoin/
├── domain/
│   ├── model/
│   │   ├── User.kt
│   │   ├── Room.kt
│   │   └── Photo.kt
│   ├── repository/
│   │   ├── AuthRepository.kt
│   │   ├── RoomRepository.kt
│   │   └── PhotoRepository.kt
│   └── usecase/
│       ├── GetPhotosUseCase.kt
│       └── UploadPhotoUseCase.kt
├── data/
│   ├── repository/
│   │   ├── AuthRepositoryImpl.kt
│   │   ├── RoomRepositoryImpl.kt
│   │   └── PhotoRepositoryImpl.kt
│   ├── datasource/
│   │   ├── SupabaseDataSource.kt
│   │   └── LocalDataSource.kt
│   └── dto/
│       ├── UserDto.kt
│       └── RoomDto.kt
└── di/
    └── AppModule.kt
```

---

### Repository パターン

```kotlin
// Domain Layer
interface PhotoRepository {
    suspend fun getPhotos(roomId: String): List<Photo>
    suspend fun uploadPhoto(photo: Photo): Result<Photo>
}

// Data Layer
class PhotoRepositoryImpl(
    private val remoteDataSource: SupabaseDataSource,
    private val localDataSource: LocalDataSource
) : PhotoRepository {

    override suspend fun getPhotos(roomId: String): List<Photo> {
        return try {
            val remote = remoteDataSource.fetchPhotos(roomId)
            localDataSource.savePhotos(remote)
            remote.map { it.toDomain() }
        } catch (e: Exception) {
            localDataSource.getPhotos(roomId).map { it.toDomain() }
        }
    }

    override suspend fun uploadPhoto(photo: Photo): Result<Photo> {
        return try {
            val uploaded = remoteDataSource.uploadPhoto(photo.toDto())
            Result.Success(uploaded.toDomain())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
```

---

### UseCase パターン

```kotlin
// 1つの責務を持つ
class GetPhotosUseCase(
    private val repository: PhotoRepository
) {
    suspend operator fun invoke(roomId: String): List<Photo> {
        return repository.getPhotos(roomId)
            .sortedByDescending { it.takenAt }
    }
}

// ViewModel から呼び出し
class RoomViewModel(
    private val getPhotosUseCase: GetPhotosUseCase
) : ViewModel() {

    fun loadPhotos(roomId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val photos = getPhotosUseCase(roomId)
            _state.update { it.copy(photos = photos, isLoading = false) }
        }
    }
}
```

---

## テストコード規約

### テストの命名

```kotlin
class GetPhotosUseCaseTest {

    // should_<期待する動作>_when_<条件>
    @Test
    fun `should return photos when repository returns success`() {
        // Given
        val expected = listOf(Photo(id = "1"))
        coEvery { repository.getPhotos(any()) } returns expected

        // When
        val result = useCase("roomId")

        // Then
        assertEquals(expected, result)
    }

    @Test
    fun `should return empty list when repository returns error`() {
        // ...
    }
}
```

### AAA パターン

```kotlin
@Test
fun testExample() {
    // Arrange (Given)
    val input = "test"

    // Act (When)
    val result = doSomething(input)

    // Assert (Then)
    assertEquals("expected", result)
}
```

---

## コメント規約

### KDoc

```kotlin
/**
 * ユーザー情報を取得する
 *
 * @param userId ユーザーID
 * @return ユーザー情報。存在しない場合はnull
 * @throws NetworkException ネットワークエラーが発生した場合
 */
suspend fun getUserById(userId: String): User?
```

### インラインコメント

```kotlin
// Good（なぜそうするのかを説明）
// iOS では日付フォーマットが異なるため、個別に処理
val formattedDate = if (Platform.isIOS) {
    formatDateForIOS(date)
} else {
    formatDateForAndroid(date)
}

// Bad（コードを読めばわかることを書く）
// ユーザー名を取得
val name = user.name
```

---

## その他のベストプラクティス

### イミュータビリティ

```kotlin
// Good（val を使用）
data class User(
    val id: String,
    val name: String
)

// Bad（var は避ける）
data class User(
    var id: String,
    var name: String
)
```

### デフォルト引数

```kotlin
// Good
fun loadPhotos(
    roomId: String,
    limit: Int = 20,
    offset: Int = 0
) {
    // ...
}

// 呼び出し
loadPhotos("room1")
loadPhotos("room1", limit = 50)
```

### when の exhaustive

```kotlin
// sealed class では else を使わない
sealed interface Status {
    object Loading : Status
    data class Success(val data: String) : Status
    data class Error(val message: String) : Status
}

fun handle(status: Status) = when (status) {
    is Status.Loading -> showLoading()
    is Status.Success -> showData(status.data)
    is Status.Error -> showError(status.message)
    // else 不要（全パターン網羅）
}
```

---

## ツール

### ktlint

```bash
# インストール
brew install ktlint

# チェック
ktlint

# 自動修正
ktlint -F
```

### detekt

```kotlin
// build.gradle.kts
plugins {
    id("io.gitlab.arturbosch.detekt") version "1.23.0"
}

detekt {
    config.setFrom(files("config/detekt/detekt.yml"))
}
```

---

**関連ドキュメント**:
- [開発ガイドライン](development-guide.md)
- [アーキテクチャ設計](architecture.md)

**最終更新**: 2024年12月
