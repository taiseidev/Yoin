# モジュール化マイグレーション計画

## 概要

現在の2モジュール構成（composeApp + shared）から、適切に分割されたfeature/core構成への移行計画。

---

## 現在の構成

```
yoin/
├── composeApp/              # すべてのUI
└── shared/                  # すべてのビジネスロジック
```

### 問題点

1. **ビルド時間が長い**
   - すべてのコードが1つのモジュールに集約
   - 小さな変更でも全体を再ビルド

2. **コードの責任範囲が不明確**
   - 機能間の境界が曖昧
   - 依存関係が複雑化しやすい

3. **チーム開発が困難**
   - 同じファイルでのコンフリクトが発生しやすい
   - 機能単位での並行開発が難しい

4. **テストが困難**
   - モジュール単位でのテストが不可能
   - モックの範囲が広くなりがち

---

## 移行後の構成

```
yoin/
├── app/                     # アプリケーション統合
├── feature/                 # 機能モジュール（9個）
├── core/                    # コアモジュール（10個）
└── shared/ (削除予定)       # 徐々にcore/に移行
```

---

## 移行戦略

### フェーズ1: Core層の構築（Week 1-2）

最も依存されるCoreモジュールから作成。

#### 1.1 core:common の作成
```bash
# ディレクトリ作成
mkdir -p core/common/src/{commonMain,androidMain,iosMain}/kotlin

# build.gradle.kts 作成
```

**移行内容:**
- `shared/src/commonMain/kotlin/util/` → `core/common/`
- Extension Functions
- Result型
- Constants

#### 1.2 core:design の作成
```bash
mkdir -p core/design/src/commonMain/kotlin
```

**移行内容:**
- `composeApp/src/commonMain/kotlin/ui/theme/` → `core/design/`
- Color, Typography, Shape
- YoinTheme

#### 1.3 core:ui の作成
```bash
mkdir -p core/ui/src/commonMain/kotlin
```

**移行内容:**
- `composeApp/src/commonMain/kotlin/ui/components/` → `core/ui/`
- `composeApp/src/commonMain/kotlin/ui/preview/` → `core/ui/`
- 共通UIコンポーネント
- Previewアノテーション

#### 1.4 core:domain の作成
```bash
mkdir -p core/domain/src/commonMain/kotlin
```

**移行内容:**
- `shared/src/commonMain/kotlin/domain/` → `core/domain/`
- Models, Repository Interfaces, UseCases

#### 1.5 core:network の作成
```bash
mkdir -p core/network/src/{commonMain,androidMain,iosMain}/kotlin
```

**新規作成:**
- SupabaseClient設定
- KtorClient設定
- API Endpoints

#### 1.6 core:database の作成
```bash
mkdir -p core/database/src/{commonMain,androidMain,iosMain}/kotlin
```

**新規作成:**
- SQLDelight設定
- Database Queries

#### 1.7 core:data の作成
```bash
mkdir -p core/data/src/{commonMain,androidMain,iosMain}/kotlin
```

**移行内容:**
- `shared/src/commonMain/kotlin/data/` → `core/data/`
- Repository実装
- DataSource
- Mapper

#### 1.8 core:image の作成
```bash
mkdir -p core/image/src/{commonMain,androidMain,iosMain}/kotlin
```

**新規作成:**
- ImageProcessor
- Filter実装

#### 1.9 core:camera の作成
```bash
mkdir -p core/camera/src/{commonMain,androidMain,iosMain}/kotlin
```

**新規作成:**
- expect/actual Camera API
- CameraX (Android)
- AVFoundation (iOS)

#### 1.10 core:analytics と core:notification の作成
```bash
mkdir -p core/analytics/src/{commonMain,androidMain,iosMain}/kotlin
mkdir -p core/notification/src/{commonMain,androidMain,iosMain}/kotlin
```

**新規作成:**
- Firebase Analytics統合
- FCM統合

---

### フェーズ2: Feature層の構築（Week 3-6）

#### 2.1 feature:onboarding の作成（優先度: 最高）
```bash
mkdir -p feature/onboarding/src/commonMain/kotlin
```

**移行内容:**
- `composeApp/src/commonMain/kotlin/ui/screens/splash/` → `feature/onboarding/`
- SplashScreen, SplashViewModel, SplashContract

**理由:** すでに実装済みで、独立性が高い

#### 2.2 feature:auth の作成（優先度: 高）
```bash
mkdir -p feature/auth/src/commonMain/kotlin
```

**新規作成:**
- LoginScreen, SignUpScreen, GuestModePromptScreen
- AuthViewModel, AuthContract

**理由:** 他の機能の前提条件

#### 2.3 feature:home の作成（優先度: 高）
```bash
mkdir -p feature/home/src/commonMain/kotlin
```

**新規作成:**
- HomeScreen, RoomListScreen
- HomeViewModel, HomeContract

**理由:** アプリの中核画面

#### 2.4 feature:room の作成（優先度: 高）
```bash
mkdir -p feature/room/src/commonMain/kotlin
```

**新規作成:**
- CreateRoomScreen, EditRoomScreen, InviteScreen
- RoomViewModel, RoomContract

**理由:** MVP機能に必須

#### 2.5 feature:camera の作成（優先度: 最高）
```bash
mkdir -p feature/camera/src/commonMain/kotlin
```

**新規作成:**
- CameraScreen, PhotoPreviewScreen
- CameraViewModel, CameraContract

**理由:** アプリの中核機能

#### 2.6 feature:timeline の作成（優先度: 高）
```bash
mkdir -p feature/timeline/src/commonMain/kotlin
```

**新規作成:**
- TimelineScreen, PhotoDetailScreen
- TimelineViewModel, TimelineContract

**理由:** MVP機能に必須

#### 2.7 feature:map の作成（優先度: 中）
```bash
mkdir -p feature/map/src/commonMain/kotlin
```

**新規作成:**
- MapScreen
- MapViewModel, MapContract

**理由:** MVP機能だが、後回し可能

#### 2.8 feature:profile の作成（優先度: 低）
```bash
mkdir -p feature/profile/src/commonMain/kotlin
```

**新規作成:**
- ProfileScreen, EditProfileScreen
- ProfileViewModel, ProfileContract

**理由:** MVP後でも追加可能

#### 2.9 feature:settings の作成（優先度: 中）
```bash
mkdir -p feature/settings/src/commonMain/kotlin
```

**新規作成:**
- SettingsScreen
- SettingsViewModel, SettingsContract

**理由:** MVP機能に必要だが、後回し可能

---

### フェーズ3: app モジュールの構築（Week 7）

```bash
mkdir -p app/src/commonMain/kotlin
```

**責務:**
- 全Featureモジュールの統合
- ナビゲーション定義
- DI統合（Koin modules）
- App Entry Point

**実装内容:**
```kotlin
// app/src/commonMain/kotlin/App.kt
@Composable
fun YoinApp() {
    YoinTheme {
        YoinNavHost()
    }
}

// app/src/commonMain/kotlin/navigation/YoinNavHost.kt
@Composable
fun YoinNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // feature:onboarding
        composable("splash") {
            SplashScreen(
                viewModel = koinViewModel(),
                onNavigateToAuth = { navController.navigate("auth") },
                onNavigateToHome = { navController.navigate("home") }
            )
        }

        // feature:auth
        composable("auth") { /* ... */ }

        // feature:home
        composable("home") { /* ... */ }

        // その他のfeature...
    }
}

// app/src/commonMain/kotlin/di/AppModule.kt
val appModule = module {
    // すべてのCore/Featureモジュールを統合
    includes(
        coreCommonModule,
        coreDesignModule,
        coreUiModule,
        coreDomainModule,
        coreDataModule,
        // ...
        featureOnboardingModule,
        featureAuthModule,
        featureHomeModule,
        // ...
    )
}
```

---

### フェーズ4: settings.gradle.kts の更新（Week 7）

```kotlin
// settings.gradle.kts
include(":app")

// Core modules
include(":core:common")
include(":core:design")
include(":core:ui")
include(":core:domain")
include(":core:data")
include(":core:network")
include(":core:database")
include(":core:analytics")
include(":core:notification")
include(":core:camera")
include(":core:image")

// Feature modules
include(":feature:onboarding")
include(":feature:auth")
include(":feature:home")
include(":feature:room")
include(":feature:camera")
include(":feature:timeline")
include(":feature:map")
include(":feature:profile")
include(":feature:settings")
```

---

### フェーズ5: 古いモジュールの削除（Week 8）

```bash
# composeApp と shared の削除
rm -rf composeApp/
rm -rf shared/
```

**前提条件:**
- すべてのコードがcore/feature/appに移行済み
- すべてのテストが通過
- ビルドが成功

---

## 移行の優先順位

### 最優先（Week 1-3）
1. core:common
2. core:design
3. core:ui
4. core:domain
5. feature:onboarding

### 高優先度（Week 4-5）
6. core:network
7. core:data
8. feature:auth
9. feature:home

### 中優先度（Week 6-7）
10. core:database
11. core:image
12. core:camera
13. feature:room
14. feature:camera
15. feature:timeline

### 低優先度（Week 8以降）
16. core:analytics
17. core:notification
18. feature:map
19. feature:profile
20. feature:settings

---

## 各モジュールのbuild.gradle.kts例

### core:design/build.gradle.kts
```kotlin
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget()
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "CoreDesign"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
        }
    }
}
```

### feature:onboarding/build.gradle.kts
```kotlin
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget()
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "FeatureOnboarding"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)

            // Core dependencies
            implementation(project(":core:ui"))
            implementation(project(":core:design"))
            implementation(project(":core:domain"))

            // Koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.composeViewModel)

            // Navigation
            implementation(libs.androidx.navigation.compose)

            // ViewModel
            implementation(libs.androidx.lifecycle.viewmodelCompose)
        }
    }
}
```

---

## マイグレーションチェックリスト

### フェーズ1完了条件
- [ ] すべてのCoreモジュールが作成済み
- [ ] 各Coreモジュールのbuild.gradle.ktsが正しく設定済み
- [ ] Coreモジュール間の依存関係が正しい
- [ ] ビルドが成功

### フェーズ2完了条件
- [ ] すべてのFeatureモジュールが作成済み
- [ ] 各Featureモジュールのbuild.gradle.ktsが正しく設定済み
- [ ] Feature→Coreの依存関係が正しい
- [ ] すべての画面が実装済み
- [ ] ビルドが成功

### フェーズ3完了条件
- [ ] appモジュールが作成済み
- [ ] ナビゲーションが正しく実装済み
- [ ] DI統合が完了
- [ ] すべての画面に遷移可能
- [ ] ビルドが成功

### フェーズ4完了条件
- [ ] settings.gradle.ktsが更新済み
- [ ] すべてのモジュールがincludeされている
- [ ] ビルドが成功

### フェーズ5完了条件
- [ ] composeApp/が削除済み
- [ ] shared/が削除済み
- [ ] すべてのテストが通過
- [ ] ビルドが成功
- [ ] アプリが正常に動作

---

## リスクと対策

### リスク1: ビルド時間の増加
**対策:**
- gradle.propertiesで並列ビルド有効化
- Build Cacheを有効化

### リスク2: 依存関係の循環
**対策:**
- Feature→Coreの一方向のみ許可
- 定期的に依存関係グラフを確認

### リスク3: マイグレーション中のバグ
**対策:**
- 段階的に移行
- 各フェーズでテスト実行
- Git履歴を細かく残す

---

## ロールバック計画

各フェーズでGitタグを作成し、問題が発生した場合は前のフェーズに戻れるようにする。

```bash
git tag phase-1-core-modules
git tag phase-2-feature-modules
git tag phase-3-app-module
git tag phase-4-settings-update
git tag phase-5-cleanup
```

---

## 次のステップ

1. このドキュメントをレビュー
2. フェーズ1の実装開始
3. 各フェーズ完了後、チェックリストを確認
4. 問題があれば、このドキュメントを更新

---

**作成日**: 2024年12月
**最終更新**: 2024年12月
