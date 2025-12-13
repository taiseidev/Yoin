# 開発ガイドライン

このドキュメントでは、Yoin（余韻）の開発を進める上でのガイドラインを説明します。

---

## 開発環境のセットアップ

### 必要なツール

| ツール | バージョン | インストール方法 |
|---|---|---|
| JDK | 17以上 | [Adoptium](https://adoptium.net/) |
| Android Studio | Hedgehog以降 | [公式サイト](https://developer.android.com/studio) |
| Xcode | 15以降 | App Store |
| Cocoapods | 最新 | `sudo gem install cocoapods` |
| Node.js | 18以上 | [nvm](https://github.com/nvm-sh/nvm) 推奨 |

### 初回セットアップ

```bash
# 1. リポジトリのクローン
git clone <repository-url>
cd YoinApp

# 2. 環境変数の設定
cp .env.example .env.local
# .env.local を編集して各種APIキーを設定

# 3. 依存関係のインストール
./gradlew build

# 4. iOS の依存関係
cd iosApp
pod install
cd ..
```

---

## ブランチ戦略

### Git Flow

```
main          ─────●────────●─────── (本番リリース)
               ╲    ╲        ╲
develop     ────●────●────●──●────── (開発統合)
             ╲   ╲    ╲    ╲
feature/*     ●──●  ●──●  ●──●────── (機能開発)
```

### ブランチ種別

| ブランチ | 用途 | 命名規則 |
|---|---|---|
| `main` | 本番環境 | - |
| `develop` | 開発環境 | - |
| `feature/*` | 機能開発 | `feature/login-screen` |
| `fix/*` | バグ修正 | `fix/camera-crash` |
| `refactor/*` | リファクタリング | `refactor/repository-layer` |
| `release/*` | リリース準備 | `release/v1.0.0` |

### ブランチ運用

```bash
# 新機能開発
git checkout develop
git pull
git checkout -b feature/my-feature

# 開発完了後
git push origin feature/my-feature
# プルリクエストを作成

# develop へマージ
# main へのマージはリリース時のみ
```

---

## コミットメッセージ規約

### Conventional Commits

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type 一覧

| Type | 説明 | 例 |
|---|---|---|
| `feat` | 新機能追加 | `feat(auth): add Google sign-in` |
| `fix` | バグ修正 | `fix(camera): resolve crash on iOS` |
| `docs` | ドキュメント更新 | `docs(readme): update setup guide` |
| `style` | コードフォーマット | `style: apply ktlint` |
| `refactor` | リファクタリング | `refactor(repo): extract data source` |
| `test` | テスト追加・修正 | `test(auth): add login test` |
| `chore` | ビルド設定など | `chore: update dependencies` |
| `perf` | パフォーマンス改善 | `perf(image): optimize compression` |

### 例

```bash
# 良い例
feat(room): add room creation screen
fix(photo): resolve upload failure on slow network
docs(api): add Supabase integration guide

# 悪い例
update
fix bug
WIP
```

---

## プルリクエスト

### テンプレート

```markdown
## 概要
この PR の目的を簡潔に説明

## 変更内容
- 変更点1
- 変更点2

## スクリーンショット（UI変更の場合）
Before / After

## テスト方法
1. xxx画面を開く
2. xxxボタンをタップ

## チェックリスト
- [ ] ビルドが通る
- [ ] テストが通る
- [ ] ドキュメントを更新した
- [ ] UIの確認をした（Android/iOS）
```

### レビュー観点

- [ ] 要件を満たしているか
- [ ] アーキテクチャに沿っているか
- [ ] テストは十分か
- [ ] パフォーマンスに問題ないか
- [ ] セキュリティリスクはないか

---

## コーディング規約

詳細は [coding-standards.md](coding-standards.md) を参照。

### 主要なルール

#### Kotlin

```kotlin
// クラス名: PascalCase
class UserRepository { }

// 関数名: camelCase
fun getUserById() { }

// 定数: UPPER_SNAKE_CASE
const val MAX_RETRY_COUNT = 3

// プライベート変数: _camelCase
private val _state = MutableStateFlow(State())
val state: StateFlow<State> = _state.asStateFlow()
```

#### Compose

```kotlin
// Composable関数: PascalCase
@Composable
fun LoginScreen() {
    // ...
}

// Modifier は第一引数
@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    // ...
}
```

---

## テスト戦略

### テストピラミッド

```
        ┌─────────┐
        │ UI Test │  10%
        ├─────────┤
        │  Integ  │  20%
        │  Test   │
        ├─────────┤
        │  Unit   │  70%
        │  Test   │
        └─────────┘
```

### Unit Test

```kotlin
class GetPhotosUseCaseTest {
    private val repository = mockk<PhotoRepository>()
    private val useCase = GetPhotosUseCase(repository)

    @Test
    fun `should return photos when repository returns success`() = runTest {
        // Given
        val photos = listOf(Photo(id = "1", roomId = "room1"))
        coEvery { repository.getPhotos("room1") } returns photos

        // When
        val result = useCase("room1")

        // Then
        assertEquals(photos, result)
    }
}
```

### UI Test

```kotlin
@Test
fun testLoginScreen() {
    composeTestRule.setContent {
        LoginScreen()
    }

    composeTestRule
        .onNodeWithText("ログイン")
        .assertIsDisplayed()
        .performClick()
}
```

### テスト実行

```bash
# 全テスト
./gradlew allTests

# Android のみ
./gradlew :shared:testDebugUnitTest

# iOS のみ
./gradlew :shared:iosSimulatorArm64Test
```

---

## ビルド・実行

### Android

```bash
# デバッグビルド
./gradlew :composeApp:assembleDebug

# リリースビルド
./gradlew :composeApp:assembleRelease

# インストール
./gradlew :composeApp:installDebug

# ログ確認
adb logcat | grep "Yoin"
```

### iOS

```bash
# Xcode で開く
cd iosApp
open iosApp.xcworkspace

# コマンドラインビルド
xcodebuild -workspace iosApp.xcworkspace \
           -scheme iosApp \
           -configuration Debug \
           build
```

---

## デバッグ

### ログ出力

```kotlin
// commonMain
expect object Logger {
    fun d(tag: String, message: String)
    fun e(tag: String, message: String, throwable: Throwable? = null)
}

// androidMain
actual object Logger {
    actual fun d(tag: String, message: String) {
        Log.d(tag, message)
    }
}

// iosMain
actual object Logger {
    actual fun d(tag: String, message: String) {
        NSLog("[$tag] $message")
    }
}

// 使用例
Logger.d("PhotoRepository", "Uploading photo: $photoId")
```

### ネットワークデバッグ

```kotlin
// Ktor Client でロギング
val client = HttpClient {
    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.ALL
    }
}
```

---

## パフォーマンス最適化

### 画像最適化

```kotlin
// アップロード前にリサイズ
val resized = imageProcessor.resize(
    imageBytes = originalBytes,
    maxWidth = 1920,
    quality = 85
)
```

### リスト表示

```kotlin
// LazyColumn で仮想化
@Composable
fun PhotoList(photos: List<Photo>) {
    LazyColumn {
        items(photos) { photo ->
            PhotoItem(photo)
        }
    }
}
```

### メモリリーク対策

```kotlin
// ViewModel で Coroutine を使用
class RoomViewModel : ViewModel() {
    init {
        viewModelScope.launch {
            // ViewModel破棄時に自動キャンセル
        }
    }
}
```

---

## CI/CD

### GitHub Actions

`.github/workflows/android-build.yml`:

```yaml
name: Android Build

on:
  push:
    branches: [ develop, main ]
  pull_request:
    branches: [ develop ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Build
        run: ./gradlew build
      - name: Test
        run: ./gradlew test
```

---

## リリース手順

### バージョニング

Semantic Versioning を採用：

```
MAJOR.MINOR.PATCH
1.0.0
```

- MAJOR: 破壊的変更
- MINOR: 新機能追加
- PATCH: バグ修正

### リリースプロセス

1. **release ブランチ作成**
   ```bash
   git checkout develop
   git pull
   git checkout -b release/v1.0.0
   ```

2. **バージョン番号更新**
   - `build.gradle.kts` の `versionName`, `versionCode`
   - `Info.plist` の `CFBundleShortVersionString`

3. **ビルド・テスト**
   ```bash
   ./gradlew clean build test
   ```

4. **タグ作成・プッシュ**
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```

5. **ストア申請**
   - App Store Connect
   - Google Play Console

---

## トラブルシューティング

### よくある問題

#### ビルドエラー

```bash
# Gradle キャッシュクリア
./gradlew clean
rm -rf ~/.gradle/caches

# Podfile.lock 削除（iOS）
cd iosApp
rm Podfile.lock
pod install
```

#### Supabase 接続エラー

```kotlin
// タイムアウト設定
val client = HttpClient {
    install(HttpTimeout) {
        requestTimeoutMillis = 30000
    }
}
```

---

## 参考リンク

- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Supabase Docs](https://supabase.com/docs)
- [Firebase Docs](https://firebase.google.com/docs)

---

**関連ドキュメント**:
- [コーディング規約](coding-standards.md)
- [アーキテクチャ設計](architecture.md)
- [API連携ガイド](api-integration.md)

**最終更新**: 2024年12月
